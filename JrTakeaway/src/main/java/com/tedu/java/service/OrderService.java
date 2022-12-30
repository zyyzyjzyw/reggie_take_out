package com.tedu.java.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.tedu.java.pojo.Orders;

public interface OrderService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);

    void againSumbit(Orders orders);

    boolean updateStatus(Orders orders);
}
