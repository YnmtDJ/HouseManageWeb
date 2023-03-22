package hm_controller;


import hm_bean.User;
import hm_dao.Comment_Dao;
import hm_dao.House_Dao;
import hm_db.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/DeleteHouseServlet")
public class DeleteHouseServlet extends HttpServlet {

    /**
     * 处理post请求
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("utf-8");
        //获取参数
        String H_id=request.getParameter("H_id");
        String Type=request.getParameter("Type");
        House_Dao houseDao=new House_Dao(DBUtil.getConnection());
        try {
            if(houseDao.DeleteHouse(H_id)){
                System.out.println("删除成功！！！");
            }
            else{
                System.out.println("删除失败！！！");
            }
            request.getRequestDispatcher("MyPost.jsp?Type="+Type).forward(request, response);
        } catch (SQLException | ServletException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 处理get请求
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request,response);
    }


}
