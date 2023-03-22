package hm_controller;


import hm_bean.House;
import hm_bean.Second_House;
import hm_dao.House_Dao;
import hm_db.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

@WebServlet("/EditHouseServlet")
public class EditHouseServlet extends HttpServlet {

    /**
     * 处理post请求
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("utf-8");
        //获取参数
        String H_id=request.getParameter("H_id");
        String H_type=request.getParameter("H_type");
        Float H_area=Float.valueOf(request.getParameter("H_area"));
        Date H_time=Date.valueOf(request.getParameter("H_time"));
        String H_decorate=request.getParameter("H_decorate");
        String H_direct=request.getParameter("H_direct");
        String H_rate=request.getParameter("H_rate");
        int H_layer=Integer.parseInt(request.getParameter("H_layer"));
        int price=0;
        String Type=null;
        if(request.getParameter("S_price")!=null){
            price=Integer.parseInt(request.getParameter("S_price"));
            Type="二手房";
        }
        else if(request.getParameter("R_price")!=null){
            price=Integer.parseInt(request.getParameter("R_price"));
            Type="租房";
        }

        House_Dao houseDao=new House_Dao(DBUtil.getConnection());

        House house;
        try {
            house= houseDao.FindHouseById(H_id);//本质为了得到原图像
            //修改
            if(houseDao.UpdateHouse(H_id,H_type,H_area,H_time,H_decorate,H_direct,H_rate,H_layer,house.img,price)){
                System.out.println("修改房源成功!!!!");
            }
            else{
                System.out.println("修改房源失败!!!!");
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
