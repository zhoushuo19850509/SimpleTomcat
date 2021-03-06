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
     * Manager每隔多久检查一下当前tomcat管理的session，看session是否过期
     * 后续建议放到配置文件中去 单位是秒
     */
    public static final int CHECK_INTERVAL = 5;


    /**
     * 持久化管理器(StoreBase)每隔多久检查一下当前管理的session，看session是否过期
     * 后续建议放到配置文件中去 单位是秒
     * 一般来说，CHECK_INTERVAL_STOREBASE和CHECK_INTERVAL的时间最好错开
     */
    public static final int CHECK_INTERVAL_STOREBASE = 7;

    /**
     * Session最长空闲时间，
     * 一旦一个Session空闲时间超过这个时长，就被设置为超时expire
     * 后续建议放到配置文件中去，单位是秒
     */
    public static final int MAX_INACTIVE_INTERVAL = 1 * 60;


    /**
     * session如果保持空闲时间超过了以下两个参数(单位是秒)，
     * 就swap out from memory to persistence
     * 如果设置为-1，就取消swap机制
     */

    /**
     * 最小空闲时间
     * 用于PersistentManagerBase.processMaxActiveSwaps()
     * 意思是，Manager要把过多的session swap out to persistent之前，
     * 先要判断一下这个session空闲时间是不是达到了MIN_IDLE_SWAP时间
     * 避免把那些刚访问过的session放到持久化层了
     * 因为那些刚访问过的session，一般是热点数据，被再次访问的概率往往比较大
     */
    public static final int MIN_IDLE_SWAP = 10;

    /**
     * 最大空闲时间
     * 这个相对更容易一些，用于PersistentManagerBase.processMaxIdleSwaps()
     * 如果Manager发现某个session的空闲时间超过了MAX_IDLE_SWAP
     * 就把这个session放到持久化层
     */
    public static final int MAX_IDLE_SWAP = 30;

    /**
     * tomcat manager管理的最大session数
     * 用于PersistentManagerBase.processMaxActiveSwaps()
     * 如果session数超过了这个数值，就swap out to persistence
     * 如果设置为-1，就取消swap机制
     */
    public static final int MAX_ACTIVE_SESSIONS = 50;

    /**
     * 如果session的空闲时间数，超过了这个数值(单位为秒)
     * 那么PersistentManager会把这个session备份到持久化层
     * 用于PersistentManagerBase.processMaxIdleBackups()
     * 如果设置为-1，就取消backup机制
     */
    public static final int MAX_IDLE_BACKUP = 20;


}
