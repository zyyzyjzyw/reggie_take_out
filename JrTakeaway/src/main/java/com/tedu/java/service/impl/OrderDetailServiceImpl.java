package com.tedu.java.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tedu.java.mapper.OrderDetailMapper;
import com.tedu.java.pojo.OrderDetail;
import com.tedu.java.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}