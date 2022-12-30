package com.tedu.java.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tedu.java.mapper.CategoryMapper;
import com.tedu.java.pojo.Category;
import com.tedu.java.pojo.Dish;
import com.tedu.java.pojo.Setmeal;
import com.tedu.java.service.CategoryService;
import com.tedu.java.service.DishService;
import com.tedu.java.service.SetmealService;
import com.tedu.java.utils.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;


    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> wrapperDish = new LambdaQueryWrapper<>();
        wrapperDish.eq(Dish::getCategoryId,id);
        int countDish = dishService.count(wrapperDish);
        //删除之前分类是否关联了菜品,如果已经关联，抛出一个异常
        if(countDish>0){
            //已经关联了菜品,抛出业务异常
            throw new CustomException("当前分类下关联了菜品,不能删除");
        }
        //删除之前分类是否关联了套餐,如果已经关联，抛出一个异常
        LambdaQueryWrapper<Setmeal> wrapperSetmeal = new LambdaQueryWrapper<>();
        wrapperSetmeal.eq(Setmeal::getCategoryId,id);
        int countSetmeal = setmealService.count(wrapperSetmeal);
        if(countSetmeal>0){
            //已经关联了套餐,抛出业务异常
            throw new CustomException("当前分类下关联了套餐,不能删除");
        }
        //正常删除分类
        super.removeById(id);
    }

}
