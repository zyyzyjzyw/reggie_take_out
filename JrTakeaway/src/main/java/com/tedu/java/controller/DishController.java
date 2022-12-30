package com.tedu.java.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tedu.java.dto.DishDto;
import com.tedu.java.pojo.Category;
import com.tedu.java.pojo.Dish;
import com.tedu.java.pojo.DishFlavor;
import com.tedu.java.service.CategoryService;
import com.tedu.java.service.DishFlavorService;
import com.tedu.java.service.DishService;
import com.tedu.java.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author： zyy
 * @date： 2022/12/26 17:42
 * @description： TODO
 * @version: 1.0
 * @描述：
 **/
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 对应条件查询菜品数据
     * @param dish
     * @return
     */
    /*@GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //菜品状态为在售
        wrapper.eq(Dish::getStatus,1);
        //添加排序条件
        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(wrapper);
        return R.success(list);
    }*/
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList = null;
        String key = "dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        // 先从redis中读取缓存数据
        dishDtoList = (List<DishDto>)redisTemplate.opsForValue().get(key);
        if(dishDtoList!=null){
            // 如果存在,直接返回,无需查询数据库
            return R.success(dishDtoList);
        }

        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        // 如果查询不到,则需要查询数据库,然后将菜品数据缓存到Redis中
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }

    @DeleteMapping
    public R<String> delectBath(String ids){
        List<String> idList = Arrays.asList(ids.split(","));
        boolean flag = dishService.removeBatch(idList);
        if(flag){
            Set keys = redisTemplate.keys("dish_*");
            redisTemplate.delete(keys);
            return R.success("删除成功");
        }
        return R.error("删除失败");
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status,String ids){
        boolean flag = dishService.updateStatusWithFlavor(status,ids);
        if(flag){
            Set keys = redisTemplate.keys("dish_*");
            redisTemplate.delete(keys);
            return R.success(status==0?"停售成功":"启售成功");
        }
        return R.success(status==0?"停售失败":"启售失败");
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    private R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        boolean flag = dishService.updateWithFlavor(dishDto);
        // 清理所有菜品的缓存数据
        /*Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);*/
        // 清理某个分类下面的菜品缓存数据
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        if(flag){
            return R.success("修改菜品成功");
        }
        return R.error("修改菜品失败");
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto byIdWithFlavor = dishService.getByIdWithFlavor(id);
        return R.success(byIdWithFlavor);
    }


    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    private R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        boolean flag = dishService.saveWithFlavor(dishDto);
        if(flag){
            String key = "dish_"+dishDto.getCategoryId()+"_1";
            redisTemplate.delete(key);
            return R.success("新增菜品成功");
        }
        return R.error("新增菜品失败");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> disDtoPage = new Page<>(page,pageSize);
        // 条件构造器
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        wrapper.like(name!=null,Dish::getName,name);
        //添加排序条件
        wrapper.orderByDesc(Dish::getCreateTime);
        // 执行分页查询
        dishService.page(pageInfo,wrapper);
        // 对象拷贝
        BeanUtils.copyProperties(pageInfo,disDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        disDtoPage.setRecords(list);
        return R.success(disDtoPage);
    }
}
