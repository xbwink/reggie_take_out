package com.xb.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xb.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xb
 * @create 2022-12-14 10:37
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
