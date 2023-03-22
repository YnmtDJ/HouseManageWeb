package hm_controller;

import hm_bean.User;
import hm_service.UserService;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

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
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("utf-8");
        //获取信息
        String U_id=request.getParameter("U_id");
        String U_password=request.getParameter("U_password");
        User user=new User(U_id,null,U_password,null);
        if(UserService.Login(user)){//登录成功，进入主界面
            request.getSession().setAttribute("user",user);//登陆的用户
            request.getRequestDispatcher("/home_page.jsp").forward(request,response);//进入主页面
        }
        else{//登录失败，返回提示
            request.setAttribute("result","登录失败");
            request.getRequestDispatcher("/login.jsp").forward(request,response);//返回登录页面
        }

    }

    public void destroy()
    {
        // 什么也不做
    }

}
