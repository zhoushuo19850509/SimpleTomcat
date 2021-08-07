package test.client;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * ͨ��http client��� ���������Ǳ��ص�tomcat����
 */
public class SessionClient {

    public void startAccessTomcat(){
        /**
         * ����Ĭ�ϵ�httpclient����
         */
        CloseableHttpClient httpclient =
                HttpClients.createDefault();


        /**
         * ���Դ��������Զ��ش����ܵ�httpclient
         */
//        CloseableHttpClient httpclient = HttpClientBuilder.create().disableAutomaticRetries().build();

        startAccessTomcat(httpclient);
    }

    public void startAccessTomcat(CloseableHttpClient httpclient){

        /**
         * ����һ��httpclient���ش�����
         */
//        DefaultHttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(0,false);
//        ((AbstractHttpClient)httpclient).setHttpRequestRetryHandler(retryHandler);


        /**
         * ���ñ���tomcat��servlet����1
         */
        String url = "http://localhost:8080/servlet/MySessionServlet";

        HttpGet httpGet = new HttpGet(url);


        CloseableHttpResponse response = null;
        String content = null;

        /**
         * ����httpclient����tomcat�����Ƿ�ɹ�
         */
        boolean isSucc = false;

        /**
         * �������httpclient����tomcat����ʧ�ܣ���ô�ش�3��
         * count�����ش�����
         */
        int count = 0;

        do{
            try {
                response = httpclient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                content = EntityUtils.toString(httpEntity);

                /**
                 * �����ܹ����е������������˵������tomcat�ɹ�
                 */
                isSucc = true;
//                System.out.println("��" + (count + 1) + "�η���tomcat�ɹ���" );
            } catch (IOException e) {
//                System.out.println("catch error msg: " + e.getMessage());
//                e.printStackTrace();
                System.out.println("��" + (count + 1) + "�� fail to access tomcat��" );
            }

        }while(!isSucc && (++count < 3) );

        /**
         * ���У��һ�·��ؽ��
         * ���contentΪ�գ�����contentû�а���������Ҫ�Ľ������Ҫ����
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
