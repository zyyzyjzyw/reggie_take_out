package com.tedu.java.utils;

/**
 * @author： zyy
 * @date： 2022/12/26 16:42
 * @description： TODO
 * @version: 1.0
 * @描述：基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
 **/
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
