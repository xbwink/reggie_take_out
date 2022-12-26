package com.xb.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xb.reggie.common.R;
import com.xb.reggie.dto.DishDto;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 添加套餐和对应套餐菜品
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> insert(@RequestBody SetmealDto setmealDto){
        setmealService.insertWithSetmealDish(setmealDto);
        //清除该套餐分类的Redis缓存数据
        String key = "setmeal_"+setmealDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
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
        //清除该套餐分类的Redis缓存数据
        String key = "setmeal_"+setmealDto.getCategoryId()+"_1";
        redisTemplate.delete(key);

        return R.success("更新成功");
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long[] ids){
        //清除该套餐分类的Redis缓存数据
        Setmeal setmeal = setmealService.getById(ids[0]);
        String key = "setmeal_"+setmeal.getCategoryId()+"_1";
        redisTemplate.delete(key);

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
        //清除该套餐分类的Redis缓存数据
        Setmeal setmeal = setmealService.getById(ids[0]);
        String key = "setmeal_"+setmeal.getCategoryId()+"_1";
        redisTemplate.delete(key);

        log.info("status={},ids={}",status,ids);
        setmealService.updateStatus(status,ids);
        return R.success("更新状态成功");
    }




    /**
     * 根据setmealId查询套餐
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        List<Setmeal> setmealList = null;
        //动态构造key
        String key = "setmeal_"+setmeal.getCategoryId()+"_"+setmeal.getStatus();//setmeal_183173617823_1

        //先从Redis中获取缓存数据
        setmealList = (List<Setmeal>)redisTemplate.opsForValue().get(key);
        if(setmealList != null){
            //如果存在，直接返回，无需查询数据库
            return R.success(setmealList);
        }

        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(setmeal.getName()), Setmeal::getName, setmeal.getName());
        queryWrapper.eq(null != setmeal.getCategoryId(), Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(null != setmeal.getStatus(), Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealList = setmealService.list(queryWrapper);

        //如果不存在，需要查询数据库，并将查询到的套餐数据缓存到Redis
        redisTemplate.opsForValue().set(key,setmealList,60L, TimeUnit.MINUTES);

        return R.success(setmealList);
    }

}
