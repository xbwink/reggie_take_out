package com.xb.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xb.reggie.common.R;
import com.xb.reggie.dto.SetmealDto;
import com.xb.reggie.entity.Category;
import com.xb.reggie.entity.Dish;
import com.xb.reggie.entity.Setmeal;
import com.xb.reggie.entity.SetmealDish;
import com.xb.reggie.service.CategoryService;
import com.xb.reggie.service.DishService;
import com.xb.reggie.service.SetmealDishService;
import com.xb.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 套餐管理
 * @author xb
 * @create 2022-12-12 19:39
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private DishService dishService;


    /**
     * 添加套餐和对应套餐菜品
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> insert(@RequestBody SetmealDto setmealDto){
        setmealService.insertWithSetmealDish(setmealDto);
        return R.success("添加套餐成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(Integer page,Integer pageSize,String name){
        Page<SetmealDto> setmealDtoPage = setmealService.myPage(page, pageSize, name);

        return R.success(setmealDtoPage);
    }



    /**
     * 拿到套餐信息，回填前端页面，为后续套餐更新做准备，调用Service层写
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        //创建条件构造器
        SetmealDto setmeal = setmealDishService.getSetmeal(id);
        return R.success(setmeal);
    }

    /**
     * 保存修改
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info("ssetmealDto={}",setmealDto);
        setmealService.updateWithSetmealDish(setmealDto);
        return R.success("更新成功");
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long[] ids){
        log.info("ids={}",ids);
        setmealService.deleteWithSetmealDish(ids);
        return R.success("删除成功");
    }

    /**
     * 更新套餐状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status") Integer status,Long[] ids){
        log.info("status={},ids={}",status,ids);
        setmealService.updateStatus(status,ids);
        return R.success("更新状态成功");
    }

//    /**
//     * 根据categoryId查询菜品
//     * @param dish
//     * @return
//     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        //构建查询条件
//        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
//        lqw.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
//        //查询状态为1(启售状态)
//        lqw.eq(Dish::getStatus,1);
//        //添加排序条件
//        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> dishes = dishService.list(lqw);
//        return R.success(dishes);
//    }


    /**
     * 根据setmealId查询套餐
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        log.info("setmeal:{}", setmeal);
        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(setmeal.getName()), Setmeal::getName, setmeal.getName());
        queryWrapper.eq(null != setmeal.getCategoryId(), Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(null != setmeal.getStatus(), Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        return R.success(setmealService.list(queryWrapper));
    }

}
