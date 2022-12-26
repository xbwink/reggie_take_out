package com.xb.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xb.reggie.dto.SetmealDto;
import com.xb.reggie.entity.Setmeal;
import com.xb.reggie.entity.SetmealDish;
import com.xb.reggie.mapper.SetmealDishMapper;
import com.xb.reggie.service.SetmealDishService;
import com.xb.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xb
 * @create 2022-12-12 19:38
 */
@Slf4j
@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {


    @Autowired
    private SetmealService setmealService;

    /**
     * 获取套餐详细信息，填充到页面上
     * @param id
     * @return
     */
    @Override
    public SetmealDto getSetmeal(Long id) {
        //根据套餐id查询套餐对象
        Setmeal setmeal = setmealService.getById(id);
        SetmealDto setmealDto = new SetmealDto();

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null,SetmealDish::getSetmealId,id);

        if (setmeal != null){
            BeanUtils.copyProperties(setmeal,setmealDto);

            List<SetmealDish> dishes = this.list(queryWrapper);
            setmealDto.setSetmealDishes(dishes);

            return setmealDto;
        }

        return null;
    }
}
