package com.tedu.java.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tedu.java.pojo.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
