package com.xb.reggie.dto;

import com.xb.reggie.entity.Setmeal;
import com.xb.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
