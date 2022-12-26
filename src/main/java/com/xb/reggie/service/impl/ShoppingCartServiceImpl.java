package com.xb.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xb.reggie.entity.ShoppingCart;
import com.xb.reggie.mapper.ShoppingCartMapper;
import com.xb.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author xb
 * @create 2022-12-19 9:28
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
