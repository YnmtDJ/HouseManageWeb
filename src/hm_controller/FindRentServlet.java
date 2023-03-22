package hm_controller;

import hm_bean.Renting;
import hm_bean.Surround;
import hm_dao.House_Dao;
import hm_dao.Surround_Dao;
import hm_db.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/FindRentServlet")
public class FindRentServlet extends HttpServlet {

    /**
     * 处理get请求
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("utf-8");
        //获取信息
        String name=request.getParameter("AdminSelect");
        String MinPrice=request.getParameter("MinPrice");
        if("".equals(MinPrice)) MinPrice=null;
        String MaxPrice=request.getParameter("MaxPrice");
        if("".equals(MaxPrice)) MaxPrice=null;
        String H_type=request.getParameter("H_type");
        if("".equals(H_type)) H_type=null;
        String MinArea=request.getParameter("MinArea");
        if("".equals(MinArea)) MinArea=null;
        String MaxArea=request.getParameter("MaxArea");
        if("".equals(MaxArea)) MaxArea=null;
        String S_name=request.getParameter("S_name");
        if("".equals(S_name)) S_name=null;



        if(S_name!=null){//根据周边查询
            Surround_Dao surroundDao = new Surround_Dao(DBUtil.getConnection());
            House_Dao houseDao=new House_Dao(DBUtil.getConnection());
            List<Surround> surrounds;
            List<Renting> rentList=new ArrayList<>();
            try {
                surrounds = surroundDao.FindSurroundByName(S_name);//查询可能的周边
                surrounds=surrounds.subList(0,Math.min(15,surrounds.size()));//只考虑前15个
                for (Surround surround : surrounds) {
                    rentList.addAll(houseDao.FindRentBySurround(surround));
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            //设置返回值
            request.setAttribute("houseList",rentList);
            request.setAttribute("S_name",S_name);
            request.getRequestDispatcher("Renting.jsp").forward(request, response);
        }



        else{//条件查询
            //设置信息并传递
            if(name!=null) request.setAttribute("AdminSelect",name);
            if(MinPrice!=null) request.setAttribute("MinPrice",MinPrice);
            if(MaxPrice!=null) request.setAttribute("MaxPrice",MaxPrice);
            if(H_type!=null) request.setAttribute("H_type",H_type);
            if(MinArea!=null) request.setAttribute("MinArea",MinArea);
            if(MaxArea!=null) request.setAttribute("MaxArea",MaxArea);
            request.getRequestDispatcher("Renting.jsp").forward(request, response);
        }
    }

}
