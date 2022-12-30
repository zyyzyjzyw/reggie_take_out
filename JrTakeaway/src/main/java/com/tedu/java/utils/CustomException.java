package com.tedu.java.utils;

/**
 * @author： zyy
 * @date： 2022/12/26 18:21
 * @description： TODO
 * @version: 1.0
 * @描述：自定义业务异常
 **/
public class CustomException extends RuntimeException{


    public CustomException(String message){
        super(message);
    }
}
