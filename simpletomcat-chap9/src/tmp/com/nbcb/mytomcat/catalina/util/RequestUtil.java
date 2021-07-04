package com.nbcb.mytomcat.catalina.util;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;

public class RequestUtil {

    /**
     * 这个方法主要是从Http header中解析出cookies
     *
     * cookies格式如下：
     * Cookie:userName=zs;password=123456;loginId=hob
     *
     * @param header  Cookies的实际值： userName=zs;password=123456;loginId=hob
     * @return  Array形式的Cookies集合
     */
    public static Cookie[] parseCookieHeaders(String header){

        // 把Cookie那些乱七八糟的结尾字符去掉
        if(header.endsWith("\n") || header.endsWith("\r")){
            header = header.substring(0,header.length() - 1);
        }
        String[] cookieArray = header.split(";");

        List<Cookie> cookies = new ArrayList<Cookie>();
        String cookStr = "";
        String name = "";
        String value = "";
        Cookie currentCookie = null;
        for(int i = 0 ; i < cookieArray.length ; i++){
            cookStr = cookieArray[i];
            String[] cookPair = cookStr.split("=");
            name = cookPair[0];
            value = cookPair[1];
            currentCookie = new Cookie(name,value);
            cookies.add(currentCookie);
        }

        return (Cookie[])cookies.toArray(new Cookie[cookies.size()]);
    }

    public static void main(String [] args){

//        String cookString = "userName=zs;password=123456;loginId=hob";
//        String cookString = " JSESSIONID=94b31f4d40a244959970d8be52a898bc";
        String cookString = " JSESSIONID=df8e6e01569e4ca19fa5caa0daf3088f\n";
        Cookie[] cookies = RequestUtil.parseCookieHeaders(cookString);
        Cookie cookie = null;
        for(int i = 0 ; i < cookies.length ; i++){
            cookie = cookies[i];
            System.out.println(cookie.getName() + " " + cookie.getValue());
        }

    }

}
