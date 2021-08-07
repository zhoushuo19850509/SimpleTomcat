import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public class MySessionPlainServlet extends HttpServlet {

    /**
     * constructor
     */
    public MySessionPlainServlet() {
    }

    @Override
    public void init() throws ServletException {
        System.out.println("MySessionServlet init");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        writer.println("Hello tomcat ! I'm plain servlet!!!");

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

        /**
         * 先判断当前客户端session是否存在
         * 备注1：getSession(false)这个参数的意思是如果session不存在，就不要新建session
         * 备注2：req.getSession()方法中，已经判断session.isValid()，
         *    即已经判断过session状态是否合法，servlet中无需额外再判断了
         */
        HttpSession session = (HttpSession) req.getSession(false);


        if(null != session && null != session.getId()){
            String sid = session.getId();
            System.out.println("get previous session info succ! sid: " + sid);
            writer.println("<br> get previous session info succ! sid: " + sid + " </br>");
            writer.println("user-id: " + session.getAttribute("user-id") + "\n");
            writer.println("user-name: " + session.getAttribute("user-name")  + "\n");
            writer.println("user-age: " + session.getAttribute("user-age")  + "\n");
        }else{
            System.out.println("get previous session info fail,Maybe session has expired! ");
            writer.println("<br> current session id invalid! </br>");
            writer.println("<br> Login again ,please! </br>");
            return;
        }


    }

}
