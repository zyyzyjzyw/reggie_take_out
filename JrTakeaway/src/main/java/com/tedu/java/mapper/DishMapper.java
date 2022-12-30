package com.tedu.java.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tedu.java.pojo.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
