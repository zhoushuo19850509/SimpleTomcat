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
 * ͨ��httpclient��� ���������Ǳ��ص�tomcat����
 * ģ��tomcat����BasicAuthenticator�ķ�ʽ����ͻ��˵������Ƿ�Ϸ�
 *
 * ��������������д��ϵ�password���ԣ�Ԥ���ǻ�ȡ����response���ݵ�
 */
public class BasicAuthenticatorClient {

    public void startAccessTomcat(){
        /**
         * ����Ĭ�ϵ�httpclient����
         */
        CloseableHttpClient httpclient =
                HttpClients.createDefault();

        startAccessTomcat(httpclient);
    }

    public void startAccessTomcat(CloseableHttpClient httpclient){


        /**
         * ���ñ���tomcat��servlet����1
         */
        String url = "http://localhost:8080/servlet/MySessionServlet";

        HttpGet httpGet = new HttpGet(url);

        /**
         * ����http header:
         * key: "authorization"
         * value: "basic emhvdXNodW86MTIzNDU2"
         *
         * ����valveҪ˵��һ�£�emhvdXNodW86MTIzNDU2��ξ���
         * �ͻ��˵�username:password("zhoushuo:nbcb")����base64ת������
         * ��������˻�����base64 decode����
         *
         * �ο���
         * tomcat4
         * �����ķ����ο��� BasicAuthenticator.parseUsername()
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
         * ����httpclient����tomcat�����Ƿ�ɹ�
         */
        boolean isSucc = false;

        try {
            response = httpclient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            content = EntityUtils.toString(httpEntity);

            System.out.println(response.getStatusLine());

            /**
             * �����ܹ����е������������˵������tomcat�ɹ�
             */
            isSucc = true;
            System.out.println("reponse content : \n" + content);
        } catch (IOException e) {
            System.out.println("�� fail to access tomcat��" );
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
