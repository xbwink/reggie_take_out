package com.xb;

import com.xb.reggie.entity.DishFlavor;

import java.util.ArrayList;

/**
 * @author xb
 * @create 2022-12-10 10:33
 */
public class Test01 {
    public static void main(String[] args) {
        ArrayList<DishFlavor> flavors = new ArrayList<>();
        flavors.add(new DishFlavor());
        flavors.add(new DishFlavor());

        for (DishFlavor flavor : flavors) {
            flavor.setDishId(1L);
        }
        System.out.println(flavors);
    }
}
