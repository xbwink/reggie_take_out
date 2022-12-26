package com.xb.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xb.reggie.entity.Category;

/**
 * @author xb
 * @create 2022-12-10 19:48
 */
public interface CategoryService extends IService<Category> {

    public void remove(Long id);

}
