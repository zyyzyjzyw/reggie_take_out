package com.tedu.java.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tedu.java.dto.SetmealDto;
import com.tedu.java.pojo.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    //新增套餐，同时需要保存套餐和菜品的关联关系
    void saveWithDish(SetmealDto setmealDto);
    // 删除套餐,同时需要删除套餐和菜品的关联数据
    void removeWithDish(List<Long> ids);

    SetmealDto getByIdWithDish(Long id);

    boolean updateWithDish(SetmealDto setmealDto);

    boolean updateBatchStatus(Integer status, String ids);
}
