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
     * Managerÿ����ü��һ�µ�ǰtomcat�����session����session�Ƿ����
     * ��������ŵ������ļ���ȥ ��λ����
     */
    public static final int CHECK_INTERVAL = 5;


    /**
     * �־û�������(StoreBase)ÿ����ü��һ�µ�ǰ�����session����session�Ƿ����
     * ��������ŵ������ļ���ȥ ��λ����
     * һ����˵��CHECK_INTERVAL_STOREBASE��CHECK_INTERVAL��ʱ����ô�
     */
    public static final int CHECK_INTERVAL_STOREBASE = 7;

    /**
     * Session�����ʱ�䣬
     * һ��һ��Session����ʱ�䳬�����ʱ�����ͱ�����Ϊ��ʱexpire
     * ��������ŵ������ļ���ȥ����λ����
     */
    public static final int MAX_INACTIVE_INTERVAL = 1 * 60;


    /**
     * session������ֿ���ʱ�䳬����������������(��λ����)��
     * ��swap out from memory to persistence
     * �������Ϊ-1����ȡ��swap����
     */

    /**
     * ��С����ʱ��
     * ����PersistentManagerBase.processMaxActiveSwaps()
     * ��˼�ǣ�ManagerҪ�ѹ����session swap out to persistent֮ǰ��
     * ��Ҫ�ж�һ�����session����ʱ���ǲ��Ǵﵽ��MIN_IDLE_SWAPʱ��
     * �������Щ�շ��ʹ���session�ŵ��־û�����
     * ��Ϊ��Щ�շ��ʹ���session��һ�����ȵ����ݣ����ٴη��ʵĸ��������Ƚϴ�
     */
    public static final int MIN_IDLE_SWAP = 10;

    /**
     * ������ʱ��
     * �����Ը�����һЩ������PersistentManagerBase.processMaxIdleSwaps()
     * ���Manager����ĳ��session�Ŀ���ʱ�䳬����MAX_IDLE_SWAP
     * �Ͱ����session�ŵ��־û���
     */
    public static final int MAX_IDLE_SWAP = 30;

    /**
     * tomcat manager��������session��
     * ����PersistentManagerBase.processMaxActiveSwaps()
     * ���session�������������ֵ����swap out to persistence
     * �������Ϊ-1����ȡ��swap����
     */
    public static final int MAX_ACTIVE_SESSIONS = 50;

    /**
     * ���session�Ŀ���ʱ�����������������ֵ(��λΪ��)
     * ��ôPersistentManager������session���ݵ��־û���
     * ����PersistentManagerBase.processMaxIdleBackups()
     * �������Ϊ-1����ȡ��backup����
     */
    public static final int MAX_IDLE_BACKUP = 20;


}
