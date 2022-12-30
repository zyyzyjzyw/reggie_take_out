package com.tedu.java.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tedu.java.mapper.ShoppingCartMapper;
import com.tedu.java.pojo.ShoppingCart;
import com.tedu.java.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author： zyy
 * @date： 2022/12/28 20:04
 * @description： TODO
 * @version: 1.0
 * @描述：
 **/
@Service
@Slf4j
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
