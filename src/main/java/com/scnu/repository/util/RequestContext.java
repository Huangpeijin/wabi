package com.scnu.repository.util;


import java.io.Serializable;

public class RequestContext implements Serializable {

    //在接口入口获取IP，并给线程本地变量赋值，在中间如service层，获取线程本地变量IP
    //remoteAddr是一个远程地址
    private static ThreadLocal<String> remoteAddr = new ThreadLocal<>();

    //取值
    public static String getRemoteAddr() {
        return remoteAddr.get();
    }
    //放值
    public static void setRemoteAddr(String remoteAddr) {
        RequestContext.remoteAddr.set(remoteAddr);
    }

}