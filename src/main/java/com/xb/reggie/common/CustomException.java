package com.xb.reggie.common;

/**
 * 自定义业务异常
 * @author xb
 * @create 2022-12-10 21:19
 */
public class CustomException extends RuntimeException{

    public CustomException(String message){
        super(message);
    }

}
