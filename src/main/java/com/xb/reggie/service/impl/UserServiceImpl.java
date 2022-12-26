package com.xb.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xb.reggie.entity.User;
import com.xb.reggie.mapper.UserMapper;
import com.xb.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author xb
 * @create 2022-12-14 10:38
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
