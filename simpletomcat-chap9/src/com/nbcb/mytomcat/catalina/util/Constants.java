package com.nbcb.mytomcat.catalina.util;

import java.io.File;

/**
 * �������
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
     * socket���ӵĳ�ʱʱ��
     * ��λms
     */
    public static final int DEFAULT_CONNECTION_TIMEOUT = 5000;

    public static final String COOKIE_NAME = "Cookie";
    public static final String JSESSION_ID = "JSESSIONID";

    /**
     * ÿ����ü��һ�µ�ǰtomcat�����session����session�Ƿ����
     * ��������ŵ������ļ���ȥ ��λ����
     */
    public static final int CHECK_INTERVAL = 5;


    /**
     * Session�����ʱ�䣬һ��һ��Session����ʱ�䳬�����ʱ�����ͱ�����Ϊ��ʱ
     * ��������ŵ������ļ���ȥ����λ����
     */
    public static final int MAX_INACTIVE_INTERVAL = 1 * 60;


}
