package com.xb.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xb.reggie.common.R;
import com.xb.reggie.dto.DishDto;
import com.xb.reggie.entity.Category;
import com.xb.reggie.entity.Dish;
import com.xb.reggie.entity.DishFlavor;
import com.xb.reggie.entity.SetmealDish;
import com.xb.reggie.service.CategoryService;
import com.xb.reggie.service.DishFlavorService;
import com.xb.reggie.service.DishService;
import com.xb.reggie.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 * @author xb
 * @create 2022-12-11 14:48
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;



    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
       dishService.saveWithFlavor(dishDto);
        return R.success("添加成功");
    }

    /**
     * 分页查询菜品
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page,Integer pageSize,String name){
        //创建分页构造器
        Page<Dish> pageInfo = new Page(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        //添加过滤条件
        lqw.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        //添加排序条件
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo,lqw);

        //对象拷贝,此处排除了records属性
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        ArrayList<DishDto> list = new ArrayList<>();
        for (Dish record : records) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record,dishDto);
            //通过dish对象的菜品分类id查询分类名称
            Category category = categoryService.getById(record.getCategoryId());
            if(category != null){
                dishDto.setCategoryName(category.getName());
            }
            list.add(dishDto);
        }

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     *根据id查询菜品信息和对应的口味数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateDishWithFlavor(dishDto);
        return R.success("修改成功");
    }

    /**
     * 根据id删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long[] ids){
        dishService.deleteWithFlavor(ids);
        return R.success("删除成功");
    }

    /**
     * 根据id修改菜品状态
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status, Long[] ids){
        dishService.updateStatus(status,ids);
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
     * 根据categoryId查询菜品
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //构建查询条件
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //查询状态为1(启售状态)
        lqw.eq(Dish::getStatus,1);
        //添加排序条件
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishes = dishService.list(lqw);

        List<DishDto> dishDtoList = dishes.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());


        return R.success(dishDtoList);
    }



}
