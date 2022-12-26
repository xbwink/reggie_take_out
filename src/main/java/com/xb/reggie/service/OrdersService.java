package com.xb.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xb.reggie.entity.Orders;

/**
 * @author xb
 * @create 2022-12-19 19:20
 */
public interface OrdersService extends IService<Orders> {
    /**
     * 用户下单
     * @param orders
     * @return
     */
    void submit(Orders orders);
}
