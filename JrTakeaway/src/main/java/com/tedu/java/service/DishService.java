package com.tedu.java.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tedu.java.dto.DishDto;
import com.tedu.java.pojo.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    boolean saveWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);

    boolean updateWithFlavor(DishDto dishDto);

    boolean updateStatusWithFlavor(Integer status, String ids);

    boolean removeBatch(List<String> idList);
}
