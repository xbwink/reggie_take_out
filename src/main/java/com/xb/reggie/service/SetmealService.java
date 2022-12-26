package com.xb.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xb.reggie.common.R;
import com.xb.reggie.dto.SetmealDto;
import com.xb.reggie.entity.Dish;
import com.xb.reggie.entity.Setmeal;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * @author xb
 * @create 2022-12-10 20:45
 */
public interface SetmealService extends IService<Setmeal> {

    //添加套餐和对应套餐菜品
    public void insertWithSetmealDish(SetmealDto setmealDto);

    public Page<SetmealDto> myPage(Integer page, Integer pageSize, String name);

    //更新套餐和套餐对应的菜品
    void updateWithSetmealDish(SetmealDto setmealDto);

    //根据套餐id删除及其对应的菜品
    void deleteWithSetmealDish(Long[] ids);


     // 根据id修改状态
    public void updateStatus(Integer status,Long[] ids);

}
