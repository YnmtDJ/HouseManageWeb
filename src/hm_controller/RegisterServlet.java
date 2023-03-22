package hm_controller;

import hm_bean.User;
import hm_service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    /**
     * 初始化
     */
    public void init()
    {
        // 执行必需的的初始化

    }

    /**
     * 处理get请求
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    {
        String U_id=request.getParameter("U_id");
        String U_password=request.getParameter("U_password");
        System.out.print(U_id+U_password);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //捕获注册信息
        request.setCharacterEncoding("utf-8");
        String U_name=request.getParameter("U_name");
        String U_password=request.getParameter("U_password");
        String U_type=request.getParameter("U_type");
        User user=new User(null,U_name,U_password,U_type);
        try {
            if(UserService.Register(user)){//注册
                request.setAttribute("result","注册成功");
                request.setAttribute("U_id",user.U_id);
            }
            else{
                request.setAttribute("result","注册失败");
            }
        } catch (SQLException e) {
            request.setAttribute("result","注册失败");
            e.printStackTrace();
        }
        request.getRequestDispatcher("/register.jsp").forward(request,response);//返回注册页面
    }

    public void destroy()
    {
        // 什么也不做
    }

}
