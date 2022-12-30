package com.tedu.java.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tedu.java.mapper.UserMapper;
import com.tedu.java.pojo.User;
import com.tedu.java.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author： zyy
 * @date： 2022/12/28 16:18
 * @description： TODO
 * @version: 1.0
 * @描述：
 **/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
