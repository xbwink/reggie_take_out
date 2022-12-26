package com.xb.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xb.reggie.common.R;
import com.xb.reggie.entity.ShoppingCart;
import com.xb.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xb
 * @create 2022-12-19 9:29
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加至购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, HttpSession session){
        log.info("shoppingCart={}",shoppingCart);
        //设置用户id
        shoppingCart.setUserId((Long) session.getAttribute("user"));

        //判断当前购物车数据是套餐还是菜品
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        Long dishId = shoppingCart.getDishId();
        if(dishId == null){
            //当前购物车数据为套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }else {
            //当前购物车数据为菜品
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }

        //查询菜品或套餐是否存在
        //SQL:select * from shopping_cart where user_id = ? and dish_id = ?
        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);
        if(cart != null){
            //存在则在原来数量+1
            cart.setNumber(cart.getNumber()+1);
            shoppingCartService.updateById(cart);
        }else {
            //不存在添加一条新的数据
            shoppingCart.setNumber(1);//初始化数量为1
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cart = shoppingCart;
        }


        return R.success(cart);
    }

    /**
     * 购物车-1
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart,HttpSession session){
        log.info("shoppingCart={}",shoppingCart);
        //设置用户id
        shoppingCart.setUserId((Long) session.getAttribute("user"));
        //判断当前购物车数据是套餐还是菜品
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        Long dishId = shoppingCart.getDishId();
        if(dishId == null){
            //当前购物车数据为套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }else {
            //当前购物车数据为菜品
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }

        //查询菜品或套餐数量
        //SQL:select * from shopping_cart where user_id = ? and dish_id = ?
        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);
        if(cart.getNumber() > 1){
            //数量>1则在原来数量-1
            cart.setNumber(cart.getNumber()-1);
            shoppingCartService.updateById(cart);
        }else {
            //否则删除数据
            shoppingCartService.removeById(cart);
            return R.success(shoppingCart);
        }


        return R.success(cart);
    }

    /**
     * 查询购物车数据
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(HttpSession session){
        //获取当前登录用户id
        Long userId =(Long) session.getAttribute("user");
        //根据用户id查询购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        return R.success(shoppingCartList);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(HttpSession session){
        //获取当前登录用户id
        Long userId =(Long) session.getAttribute("user");
        //清空用户购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        //SQL:delete from shopping_cart where user_id = ?
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车完成");
    }


}
