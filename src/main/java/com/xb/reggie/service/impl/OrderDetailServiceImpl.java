package com.xb.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xb.reggie.entity.OrderDetail;
import com.xb.reggie.mapper.OrderDetailMapper;
import com.xb.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author xb
 * @create 2022-12-19 19:23
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
