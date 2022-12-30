package com.tedu.java.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tedu.java.dto.DishDto;
import com.tedu.java.mapper.DishMapper;
import com.tedu.java.pojo.Dish;
import com.tedu.java.pojo.DishFlavor;
import com.tedu.java.service.DishFlavorService;
import com.tedu.java.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author： zyy
 * @date： 2022/12/26 17:39
 * @description： TODO
 * @version: 1.0
 * @描述：
 **/
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品同时保存对应的口味
     * @param dishDto
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息到菜品表dish
        this.save(dishDto);
        // 获取菜品id
        Long dishId = dishDto.getId();
        // 宝墩菜品口味数据到菜品口味表dish_flavor
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        boolean flag = dishFlavorService.saveBatch(flavors);
        return flag;
    }

    /**
     * 据id查询菜品信息和对应的口味信息
     * @param id
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 查询菜品基本信息,从dish表查询
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        // 查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(wrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 更新dish和口味表信息
     * @param dishDto
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateWithFlavor(DishDto dishDto) {
        // 更新dish表信息
        this.updateById(dishDto);
        // 清理当前菜品对应口味数据
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(wrapper);
        // 添加当前提交过来的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        boolean flag = dishFlavorService.saveBatch(flavors);
        return flag;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateStatusWithFlavor(Integer status, String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        List<Dish> dishes = this.listByIds(idList);
        dishes = dishes.stream().map((item)->{
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());
        boolean flag = this.updateBatchById(dishes);
        return flag;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeBatch(List<String> idList) {
        boolean flag = this.removeByIds(idList);
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(DishFlavor::getDishId,idList);
        boolean success = dishFlavorService.remove(wrapper);
        if(flag&&success){
            return true;
        }
        return false;
    }
}
