
import org.apache.catalina.Session;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * 这个servlet测试类，用于验证我们的session功能
 */
public class MySessionServlet extends HttpServlet {

    /**
     * constructor
     */
    public MySessionServlet() {
    }

    @Override
    public void init() throws ServletException {
        System.out.println("MySessionServlet init");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        writer.println("Hello client");

        /**
         * 先打印各个http header
         */
//        String parameterName = "";
//        Enumeration headerNames = req.getHeaderNames();
//        while(headerNames.hasMoreElements()) {
//            String headerName = (String)headerNames.nextElement();
//            parameterName = req.getHeader(headerName);
//            writer.println("<br>" + headerName + ":" + parameterName + "</br>");
//        }


        /**
         * session相关操作
         */


        // 先看看当前会话是否存在，如果会话存在，说明之前已经登录过了，无需再次登录
        HttpSession sessionOld = (HttpSession) req.getSession(false);
        if(null != sessionOld && null != sessionOld.getId()){
            System.out.println("sesssion exist! do not login again!");
            writer.println("<br>sesssion exist! session id: "+ sessionOld.getId() +"</br>");
            writer.println("<br>do not login again!</br>");

            return;
        }else{
            System.out.println("new session login! start valid the usr/passwd!");
        }

        /**
         * 先判断客户端上传的用户名、密码是否正确
         */
        boolean isUserPasswdValid = true;
        if(isUserPasswdValid){
            // 如果用户验证通过，就创建新的session
            HttpSession session = (HttpSession) req.getSession(true);

            // 创建session成功
            if(null != session){
                String sid = session.getId();
                System.out.println("new created session id: " + sid);
                //new created session id: 7b34fe86d64e40da8844894de05dc278

                writer.println("<br>new created session id: " + sid + "</br>");

                /**
                 * 然后，设置一些session相关的属性，像是token啊什么的，后续用于验证session合法性
                 * 备注：这些属性是保存在内存中的(当然后续可能swap out到持久化层)，
                 * 不会通过http header返回给客户端
                 */
                session.setAttribute("user-id","001");
                session.setAttribute("user-name","zhoushuo");
                session.setAttribute("user-age","35");
                session.setAttribute("token","aaa");
            }
        }
    }
}
