package com.tedu.java.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tedu.java.dto.OrdersDto;
import com.tedu.java.pojo.OrderDetail;
import com.tedu.java.pojo.Orders;
import com.tedu.java.service.OrderDetailService;
import com.tedu.java.service.OrderService;
import com.tedu.java.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @PutMapping
    public R<String> updateState(@RequestBody Orders orders){
        boolean flag = orderService.updateStatus(orders);
        if(flag){
            return R.success(orders.getStatus()==3?"派送成功":"订单已完成");
        }
        return R.error(orders.getStatus()==3?"派送失败":"订单完成失败");
    }

    @GetMapping("/page")
    public R<Page> pageInfo(int page,int pageSize,String number){
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrdersDto> pageInfoDto = new Page<>();
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(number!=null,Orders::getNumber,number);
        wrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo,wrapper);
        BeanUtils.copyProperties(pageInfo,pageInfoDto,"records");
        List<Orders> records = pageInfo.getRecords();
        List<OrdersDto> list=records.stream().map((item)->{
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);
            ordersDto.setAddress(item.getAddress());
            ordersDto.setConsignee(item.getConsignee());
            ordersDto.setPhone(item.getPhone());
            ordersDto.setUserName(item.getUserId().toString());
            return ordersDto;
        }).collect(Collectors.toList());
        pageInfoDto.setRecords(list);
        return R.success(pageInfoDto);
    }

    /**
     * 再来一单
     */
    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders){
        orderService.againSumbit(orders);
        return R.success("重新加入购物车");
    }

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page> page(int page,int pageSize){
        // 构造分页构造器
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrdersDto> pageInfoDto = new Page<>();
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo,wrapper);
        BeanUtils.copyProperties(pageInfo,pageInfoDto,"records");
        List<Orders> records = pageInfo.getRecords();
        List<OrdersDto> list=records.stream().map((item)->{
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);
            ordersDto.setAddress(item.getAddress());
            ordersDto.setConsignee(item.getConsignee());
            ordersDto.setPhone(item.getPhone());
            ordersDto.setUserName(item.getUserName());
            Long id = item.getId();
            LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderDetail::getOrderId,id);
            List<OrderDetail> list1 = orderDetailService.list(queryWrapper);
            if(list1.size()>0&&list1!=null){
                ordersDto.setOrderDetails(list1);
            }
            return ordersDto;
        }).collect(Collectors.toList());
        pageInfoDto.setRecords(list);
        return R.success(pageInfoDto);
    }
}