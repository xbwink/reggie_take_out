package com.xb.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xb.reggie.dto.SetmealDto;
import com.xb.reggie.entity.SetmealDish;
import com.xb.reggie.mapper.SetmealDishMapper;

/**
 * @author xb
 * @create 2022-12-12 19:35
 */
public interface SetmealDishService extends IService<SetmealDish> {

    //根据套餐id查询对应菜品
    public SetmealDto getSetmeal(Long id);

}
