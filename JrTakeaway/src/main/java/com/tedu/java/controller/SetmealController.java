package com.tedu.java.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tedu.java.dto.SetmealDto;
import com.tedu.java.pojo.Category;
import com.tedu.java.pojo.Setmeal;
import com.tedu.java.service.CategoryService;
import com.tedu.java.service.SetmealService;
import com.tedu.java.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author： zyy
 * @date： 2022/12/26 17:42
 * @description： TODO
 * @version: 1.0
 * @描述：Setmeal
 **/
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/status/{status}")
    public R<String> updateBatchStatus(@PathVariable Integer status,String ids){
        boolean flag = setmealService.updateBatchStatus(status,ids);
        if(flag){
            Set keys = redisTemplate.keys("setmeal_*");
            redisTemplate.delete(keys);
            return R.success(status==0?"停售成功":"启售成功");
        }
        return R.success(status==0?"停售失败":"启售失败");
    }

    @PutMapping
    private R<String> update(@RequestBody SetmealDto setmealDto){
        log.info(setmealDto.toString());
        boolean flag = setmealService.updateWithDish(setmealDto);
        if(flag){
            String key = "setmeal_"+setmealDto.getCategoryId()+"_1";
            redisTemplate.delete(key);
            return R.success("修改菜品成功");
        }
        return R.error("修改菜品失败");
    }
    /**
     * 根据id查询套餐信息和对应的菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
      SetmealDto setmealDto =   setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids: {}",ids);
        setmealService.removeWithDish(ids);
        Set keys = redisTemplate.keys("setmeal_*");
        redisTemplate.delete(keys);
        return R.success("删除成功");
    }
    /**
     * 分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> pageInfoDto = new Page<>();
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name!=null,Setmeal::getName,name);
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,wrapper);
        BeanUtils.copyProperties(pageInfo,pageInfoDto,"records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                // 分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        pageInfoDto.setRecords(list);
        return R.success(pageInfoDto);
    }
    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息: {}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        String key = "setmeal_"+setmealDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("新增套餐成功");
    }
    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        List<Setmeal> list = null;
        String key = "setmeal_"+setmeal.getCategoryId()+"_"+setmeal.getStatus();
        list = (List<Setmeal>)redisTemplate.opsForValue().get(key);
        if(list!=null){
            // 如果存在,直接返回,无需查询数据库
            return R.success(list);
        }
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        list = setmealService.list(queryWrapper);
        // 如果查询不到,则需要查询数据库,然后将菜品数据缓存到Redis中
        redisTemplate.opsForValue().set(key,list,60, TimeUnit.MINUTES);
        return R.success(list);
    }
}
