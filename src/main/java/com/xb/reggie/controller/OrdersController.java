package com.xb.reggie.controller;

import com.xb.reggie.common.R;
import com.xb.reggie.entity.Orders;
import com.xb.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xb
 * @create 2022-12-19 19:24
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {


    @Autowired
    private OrdersService ordersService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("orders={}",orders);
        ordersService.submit(orders);
        return R.success("下单完成");
    }

}
