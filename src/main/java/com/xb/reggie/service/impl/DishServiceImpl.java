package com.xb.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xb.reggie.common.R;
import com.xb.reggie.dto.DishDto;
import com.xb.reggie.entity.Dish;
import com.xb.reggie.entity.DishFlavor;
import com.xb.reggie.mapper.DishMapper;
import com.xb.reggie.service.DishFlavorService;
import com.xb.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
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
 * @create 2022-12-10 20:46
 */
@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品,同时保存对应的口味数据
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        //添加完成时自动生成id
        Long dishId = dishDto.getId();

        //遍历菜品口味表
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味表
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 据id查询菜品信息和对应的口味数据
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //调用service层查询获得对象
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        //对象拷贝
        BeanUtils.copyProperties(dish,dishDto);

        //创建条件构造器
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        //根据菜品id查询菜品口味
        lqw.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(lqw);
        dishDto.setFlavors(list);
        return dishDto;
    }

    /**
     * 修改菜品和对应的口味数据
     * @param dishDto
     */
    @Transactional
    @Override
    public void updateDishWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);
        //先清理当前菜品对应的口味数据
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lqw);

        //再插入对应的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        //遍历集合设置dishId
        flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id删除菜品和对应的口味数据
     * @param ids
     */
    @Transactional
    @Override
    public void deleteWithFlavor(Long[] ids) {
        //先将ids转成list集合
        List<Long> list = Arrays.asList(ids);
        //删除dish表基本信息
        this.removeByIds(list);

        //删除dishId对应的口味表信息
        for (Long dishId : list) {
            LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
            lqw.eq(DishFlavor::getDishId,dishId);
            dishFlavorService.remove(lqw);
        }

    }

    /**
     * 根据categoryId查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> getByCategoryId(Long categoryId) {
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Dish::getCategoryId,categoryId);

        List<Dish> list = this.list(lqw);
        return list;
    }

    /**
     * 根据id修改状态
     * @param ids
     */
    @Transactional
    @Override
    public void updateStatus(Integer status,Long[] ids) {
        //先将ids转成list集合
        List<Long> list = Arrays.asList(ids);
        //根据id查询dish对象集合
        List<Dish> dishes = this.listByIds(list);
        //遍历dishes修改状态
        for (Dish dish : dishes) {
            dish.setStatus(status);
        }
        //执行更新
        this.updateBatchById(dishes);
    }

}
