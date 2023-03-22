package hm_controller;


import hm_bean.User;
import hm_dao.House_Dao;
import hm_db.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/SeeHouseServlet")
public class SeeHouseServlet extends HttpServlet {

    /**
     * 处理post请求
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("utf-8");
        String A_id=request.getParameter("A_id");
        String H_id=request.getParameter("H_id");
        String fromURL=request.getHeader ("Referer");
        User user=(User)request.getSession().getAttribute("user");
        Connection connection= DBUtil.getConnection();
        House_Dao houseDao=new House_Dao(connection);
        try {
            int result=houseDao.InsertUAH(user.U_id,A_id,H_id);
            if(result==1){//插入成功
                request.setAttribute("SeeFlag","Success");
            }
            else if(result==0){//插入失败
                request.setAttribute("SeeFlag","Fail");
            }
            else{//不可重复插入
                request.setAttribute("SeeFlag","Duplicate");
            }
        } catch (SQLException e) {
            request.setAttribute("SeeFlag","Fail");
            e.printStackTrace();
        }
        request.setAttribute("H_id",H_id);
        if(fromURL.contains("SecHouseDetail.jsp")) {
            request.getRequestDispatcher("SecHouseDetail.jsp").forward(request, response);
        }
        else if(fromURL.contains("RentDetail.jsp")){
            request.getRequestDispatcher("RentDetail.jsp").forward(request,response);
        }

    }

}
