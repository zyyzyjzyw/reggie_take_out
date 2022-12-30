package com.tedu.java.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tedu.java.dto.SetmealDto;
import com.tedu.java.mapper.SetmealMapper;
import com.tedu.java.pojo.Setmeal;
import com.tedu.java.pojo.SetmealDish;
import com.tedu.java.service.SetmealDishService;
import com.tedu.java.service.SetmealService;
import com.tedu.java.utils.CustomException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author： zyy
 * @date： 2022/12/26 17:41
 * @description： TODO
 * @version: 1.0
 * @描述：
 **/
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐的基本信息，操作setmeal,执行insert操作
        this.save(setmealDto);
        // 保存套餐和菜品的关联关系,操作setmeal_dish,执行insert操作
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐,同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态是否可以删除
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Setmeal::getId,ids);
        wrapper.eq(Setmeal::getStatus,1);
        int count = this.count(wrapper);
        if(count>0){
            // 如果不能删除,抛出一个业务异常
            throw new CustomException("套餐正在售卖中,不能删除");
        }
        // 如果可以删除,先删除套餐表中的数据
        this.removeByIds(ids);
        // 删除关系中的数据
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);
    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        // 获取当前套餐信息
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        // 获取当前的套餐所对应的菜品信息
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> list = setmealDishService.list(wrapper);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateWithDish(SetmealDto setmealDto) {
        // 更新setmeal表信息
        this.updateById(setmealDto);
        // 清理当前套餐所对应菜品数据
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(wrapper);
        // 添加当前提交过来的菜品数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        boolean flag = setmealDishService.saveBatch(setmealDishes);
        return flag;
    }

    @Override
    public boolean updateBatchStatus(Integer status, String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        List<Setmeal> setmeals = this.listByIds(idList);
        setmeals = setmeals.stream().map((item)->{
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());
        boolean success = this.updateBatchById(setmeals);
        return success;
    }
}
