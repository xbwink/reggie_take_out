package com.xb.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xb.reggie.common.CustomException;
import com.xb.reggie.common.R;
import com.xb.reggie.dto.SetmealDto;
import com.xb.reggie.entity.Category;
import com.xb.reggie.entity.Dish;
import com.xb.reggie.entity.Setmeal;
import com.xb.reggie.entity.SetmealDish;
import com.xb.reggie.mapper.SetmealMapper;
import com.xb.reggie.service.CategoryService;
import com.xb.reggie.service.SetmealDishService;
import com.xb.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xb
 * @create 2022-12-10 20:45
 */
@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加套餐和对应套餐菜品
     *
     * @param setmealDto
     */
    @Transactional
    @Override
    public void insertWithSetmealDish(SetmealDto setmealDto) {
        //先执行基本套餐添加
        this.save(setmealDto);

        //执行添加套餐对应菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //遍历集合设置dishId
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 更新套餐和套餐对应的菜品
     *
     * @param setmealDto
     */
    @Transactional
    @Override
    public void updateWithSetmealDish(SetmealDto setmealDto) {
        //更新基本套餐表
        this.updateById(setmealDto);

        //先清除此套餐对应的旧的菜品表
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(lqw);

        //添加更新后的菜品表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //遍历集合设置setmealId
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 根据套餐id删除及其对应的菜品
     *
     * @param ids
     */
    @Transactional
    @Override
    public void deleteWithSetmealDish(Long[] ids) {
        //先将ids转成list集合
        List<Long> list = Arrays.asList(ids);

        //查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Setmeal::getId, ids);
        wrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(wrapper);
        if (count > 0) {
            //如果不能删除,抛出一个异常
            throw new CustomException("套餐正在售卖中,不能删除");
        }

        //如果可删除，先删除setemal表基本信息
        this.removeByIds(list);

        //删除setemal表对应的菜品数据
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lqw);


    }

    @Override
    public Page<SetmealDto> myPage(Integer page, Integer pageSize, String name) {
        //创建一个分页构造器
        Page<Setmeal> setmealPage = new Page<>();
        Page<SetmealDto> setmealDtoPage = new Page<>();

        //创建一个条件构造器
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        //添加过滤条件
        lqw.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        //执行分页查询
        this.page(setmealPage, lqw);

        //对象拷贝,此处排除了records属性
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");

        //遍历查询
        List<Setmeal> setmeals = setmealPage.getRecords();
        ArrayList<SetmealDto> setmealDtos = new ArrayList<>();
        for (Setmeal setmeal : setmeals) {
            SetmealDto setmealDto = new SetmealDto();
            //根据分类id查询分类名称
            Category category = categoryService.getById(setmeal.getCategoryId());
            BeanUtils.copyProperties(setmeal, setmealDto);
            setmealDto.setCategoryName(category.getName());
            setmealDtos.add(setmealDto);
        }

        setmealDtoPage.setRecords(setmealDtos);

        return setmealDtoPage;
    }

    /**
     * 根据id修改状态
     *
     * @param ids
     */
    @Transactional
    @Override
    public void updateStatus(Integer status, Long[] ids) {
        //先将ids转成list集合
        List<Long> list = Arrays.asList(ids);
        //根据id查询dish对象集合
        List<Setmeal> setmeals = this.listByIds(list);
        //遍历dishes修改状态
        for (Setmeal setmeal : setmeals) {
            setmeal.setStatus(status);
        }

        //执行更新
        this.updateBatchById(setmeals);
    }

}
