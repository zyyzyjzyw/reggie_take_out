package com.tedu.java.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tedu.java.mapper.EmployeeMapper;
import com.tedu.java.pojo.Employee;
import com.tedu.java.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author： zyy
 * @date： 2022/12/26 9:54
 * @description： TODO
 * @version: 1.0
 * @描述：
 **/

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService{
}
