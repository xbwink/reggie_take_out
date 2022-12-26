package com.xb.reggie.dto;

import com.xb.reggie.entity.Dish;
import com.xb.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据传输对象
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
