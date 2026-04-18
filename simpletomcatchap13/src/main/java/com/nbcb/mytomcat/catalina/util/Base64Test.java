package com.nbcb.mytomcat.catalina.util;

import org.apache.catalina.util.Base64;

public class Base64Test {
    public static void main(String[] args) {
        String a = "zhoushuo:123456";
        String encoded = new String(Base64.encode(a.getBytes()));
        System.out.println(encoded);
    }
}
