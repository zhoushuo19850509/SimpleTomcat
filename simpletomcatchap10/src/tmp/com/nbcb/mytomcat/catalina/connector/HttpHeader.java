package com.nbcb.mytomcat.catalina.connector;

import java.util.HashMap;

public class HttpHeader {

    protected HashMap headers;

    public HttpHeader(){
        headers = new HashMap();
    }

    public HashMap getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap headers) {
        this.headers = headers;
    }
}
