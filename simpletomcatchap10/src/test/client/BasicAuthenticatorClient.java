package client;

import org.apache.catalina.util.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * 通过httpclient组件 来访问我们本地的tomcat服务
 * 模拟tomcat按照BasicAuthenticator的方式检验客户端的请求是否合法
 *
 * 如果我们请求报文中带上的password不对，预期是获取不到response内容的
 */
public class BasicAuthenticatorClient {

    public void startAccessTomcat(){
        /**
         * 创建默认的httpclient对象
         */
        CloseableHttpClient httpclient =
                HttpClients.createDefault();

        startAccessTomcat(httpclient);
    }

    public void startAccessTomcat(CloseableHttpClient httpclient){


        /**
         * 调用本地tomcat的servlet服务1
         */
        String url = "http://localhost:8080/servlet/MySessionServlet";

        HttpGet httpGet = new HttpGet(url);

        /**
         * 设置http header:
         * key: "authorization"
         * value: "basic emhvdXNodW86MTIzNDU2"
         *
         * 其中valve要说明一下，emhvdXNodW86MTIzNDU2这段就是
         * 客户端的username:password("zhoushuo:nbcb")经过base64转化而来
         * 后续服务端会把这段base64 decode出来
         *
         * 参考：
         * tomcat4
         * 解析的方法参考： BasicAuthenticator.parseUsername()
         *
         */
        String username = "zhoushuo";
        String password = "nbcb";
        String userinfo = username + ":" + password;
        String encoded = new String(Base64.encode(userinfo.getBytes()));
        httpGet.addHeader("authorization","basic " + encoded);


        CloseableHttpResponse response = null;
        String content = null;

        /**
         * 调用httpclient访问tomcat服务是否成功
         */
        boolean isSucc = false;

        try {
            response = httpclient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            content = EntityUtils.toString(httpEntity);

            System.out.println(response.getStatusLine());

            /**
             * 程序能够运行到这里而不报错，说明访问tomcat成功
             */
            isSucc = true;
            System.out.println("reponse content : \n" + content);
        } catch (IOException e) {
            System.out.println("次 fail to access tomcat！" );
        }finally {
            try {
                if(response != null){
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }
    public static void main(String[] args){
        BasicAuthenticatorClient client = new BasicAuthenticatorClient();
        client.startAccessTomcat();
    }
}
