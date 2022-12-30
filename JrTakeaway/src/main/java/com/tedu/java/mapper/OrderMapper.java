package com.tedu.java.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tedu.java.pojo.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {

}