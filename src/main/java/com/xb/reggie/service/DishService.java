package com.xb.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xb.reggie.common.R;
import com.xb.reggie.dto.DishDto;
import com.xb.reggie.entity.Dish;

import java.util.List;

/**
 * @author xb
 * @create 2022-12-10 20:44
 */
public interface DishService extends IService<Dish> {

    //新增菜品,同时插入菜品对应的口味数据,需要操作两张表
    public void saveWithFlavor(DishDto dishDto);

    //据id查询菜品信息和对应的口味数据
    public DishDto getByIdWithFlavor(Long id);

    //修改菜品和对应的口味数据
    public void updateDishWithFlavor(DishDto dishDto);

    //根据id删除菜品和对应的口味数据
    void deleteWithFlavor(Long[] ids);

    //根据id修改状态
    void updateStatus(Integer status,Long[] ids);

    //根据categoryId查询菜品
    public List<Dish> getByCategoryId(Long categoryId);

}
