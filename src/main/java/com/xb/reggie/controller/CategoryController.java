package com.xb.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xb.reggie.common.R;
import com.xb.reggie.entity.Category;
import com.xb.reggie.entity.DishFlavor;
import com.xb.reggie.entity.Employee;
import com.xb.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 * @author xb
 * @create 2022-12-10 19:51
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加菜品or套餐分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> add(@RequestBody Category category){
        //调用service执行添加
        categoryService.save(category);
        return R.success("添加分类成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page,Integer pageSize){

        //创建分页构造器
        Page pageInfo = new Page(page, pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        //添加排序条件(sort)
        lqw.orderByAsc(Category::getSort);

        //执行查询
        categoryService.page(pageInfo,lqw);
        return R.success(pageInfo);
    }

    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("id={}",ids);
        categoryService.remove(ids);
        return R.success("删除成分类成功");
    }

    /**
     * 根据id修改分类
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //创建条件构造器
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        //添加查询条件type
        lqw.eq(category.getType() != null,Category::getType,category.getType());
        //添加排序条件
        lqw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(lqw);
        return R.success(list);
    }

}
