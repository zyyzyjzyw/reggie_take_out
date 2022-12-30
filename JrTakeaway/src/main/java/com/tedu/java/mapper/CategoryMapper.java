package com.tedu.java.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tedu.java.pojo.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
