package test.client;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * 通过http client组件 来访问我们本地的tomcat服务
 */
public class SessionClient {

    public void startAccessTomcat(){
        /**
         * 创建默认的httpclient对象
         */
        CloseableHttpClient httpclient =
                HttpClients.createDefault();


        /**
         * 尝试创建不带自动重传功能的httpclient
         */
//        CloseableHttpClient httpclient = HttpClientBuilder.create().disableAutomaticRetries().build();

        startAccessTomcat(httpclient);
    }

    public void startAccessTomcat(CloseableHttpClient httpclient){

        /**
         * 设置一下httpclient的重传参数
         */
//        DefaultHttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(0,false);
//        ((AbstractHttpClient)httpclient).setHttpRequestRetryHandler(retryHandler);


        /**
         * 调用本地tomcat的servlet服务1
         */
        String url = "http://localhost:8080/servlet/MySessionServlet";

        HttpGet httpGet = new HttpGet(url);


        CloseableHttpResponse response = null;
        String content = null;

        /**
         * 调用httpclient访问tomcat服务是否成功
         */
        boolean isSucc = false;

        /**
         * 如果调用httpclient访问tomcat服务失败，那么重传3次
         * count代表重传次数
         */
        int count = 0;

        do{
            try {
                response = httpclient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                content = EntityUtils.toString(httpEntity);

                /**
                 * 程序能够运行到这里而不报错，说明访问tomcat成功
                 */
                isSucc = true;
//                System.out.println("第" + (count + 1) + "次访问tomcat成功！" );
            } catch (IOException e) {
//                System.out.println("catch error msg: " + e.getMessage());
//                e.printStackTrace();
                System.out.println("第" + (count + 1) + "次 fail to access tomcat！" );
            }

        }while(!isSucc && (++count < 3) );

        /**
         * 最后，校验一下返回结果
         * 如果content为空，或者content没有包含我们想要的结果，都要报错
         */

        System.out.println("reponse: " + content);

        try {
            if(response != null){
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        SessionClient client = new SessionClient();
        for (int i = 0; i < 30; i++) {
            client.startAccessTomcat();
        }
    }
}
