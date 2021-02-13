package com.nbcb.mytomcat.catalina.util;

import java.io.File;

/**
 * 保存变量
 */
public class Constants {

    /**
     * the web root directory of the java container
     *
     * this dir contains :
     * 1.the static resource of the app server
     * 2.the compiledservlet classes
     */
    public static final String WEB_ROOT = System.getProperty("user.dir") +
            File.separator +"webroot";


    /**
     * socket连接的超时时间
     * 单位ms
     */
    public static final int DEFAULT_CONNECTION_TIMEOUT = 5000;

    public static final String COOKIE_NAME = "Cookie";
    public static final String JSESSION_ID = "JSESSIONID";

    /**
     * 每个多久检查一下当前tomcat管理的session，看session是否过期
     * 后续建议放到配置文件中去 单位是秒
     */
    public static final int CHECK_INTERVAL = 5;


    /**
     * Session最长空闲时间，一旦一个Session空闲时间超过这个时长，就被设置为超时
     * 后续建议放到配置文件中去，单位是秒
     */
    public static final int MAX_INACTIVE_INTERVAL = 1 * 60;


}
