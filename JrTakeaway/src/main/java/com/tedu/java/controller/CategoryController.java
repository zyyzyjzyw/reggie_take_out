package com.tedu.java.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tedu.java.pojo.Category;
import com.tedu.java.service.CategoryService;
import com.tedu.java.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author： zyy
 * @date： 2022/12/26 17:09
 * @description： TODO
 * @version: 1.0
 * @描述：分类管理
 **/
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category: {}"+category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        //根据sort进行排序
        wrapper.orderByAsc(Category::getSort);
        // 进行分页查询
        categoryService.page(pageInfo,wrapper);
        return R.success(pageInfo);

    }
    /**
     * 根据id删除分类
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除分类: id为{}",ids);
        //categoryService.removeById(id);
        categoryService.remove(ids);
        return R.success("分类信息删除成功");
    }

    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息: {}",category);
        categoryService.updateById(category);
        return R.success("修改分类成功");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(category.getType()!=null,Category::getType,category.getType());
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getCreateTime);
        List<Category> list = categoryService.list(wrapper);
        return R.success(list);
    }
}
