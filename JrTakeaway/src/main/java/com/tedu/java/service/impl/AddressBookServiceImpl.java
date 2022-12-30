package com.tedu.java.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tedu.java.mapper.AddressBookMapper;
import com.tedu.java.pojo.AddressBook;
import com.tedu.java.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
