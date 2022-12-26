package com.xb.reggie.common;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录的id
 * @author xb
 * @create 2022-12-10 16:10
 */
public class BaseContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
