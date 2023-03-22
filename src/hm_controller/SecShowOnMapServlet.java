package hm_controller;


import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import hm_bean.Community;
import hm_bean.User;
import hm_dao.Community_Dao;
import hm_dao.House_Dao;
import hm_db.DBUtil;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/SecShowOnMapServlet")
public class SecShowOnMapServlet extends HttpServlet {

    /**
     * 处理get请求
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        float center_x=Float.parseFloat(request.getParameter("center_x"));
        float center_y=Float.parseFloat(request.getParameter("center_y"));
        Community_Dao communityDao=new Community_Dao(DBUtil.getConnection());
        List<Community> communityList;
        JsonArray jsonArray = new JsonArray();//每项代表一个小区
        try {
            //寻找小区
            communityList=communityDao.FindNearCommunity(center_x,center_y);
            WKTReader reader=new WKTReader();
            //获取小区号，小区名，小区中心坐标，小区边界，平均价格,房源套数
            for(Community community:communityList){
                Polygon polygon=(Polygon) reader.read(community.loc.STAsText());
                JsonObject jsonObject=new JsonObject();
                //小区号
                jsonObject.add("C_id",community.id);
                //小区名
                jsonObject.add("C_name",community.name);
                //小区中心坐标
                JsonObject center=new JsonObject();
                center.add("x",polygon.getCentroid().getX());
                center.add("y",polygon.getCentroid().getY());
                jsonObject.add("C_center",center);
                //小区边界
                JsonArray pnts=new JsonArray();
                for(int i=0;i<polygon.getNumPoints();i++){
                    JsonObject pnt=new JsonObject();
                    pnt.add("x",polygon.getCoordinates()[i].getX());
                    pnt.add("y",polygon.getCoordinates()[i].getY());
                    pnts.add(pnt);
                }
                jsonObject.add("C_loc", pnts);
                //平均价格
                jsonObject.add("Price",communityDao.FindSecondAVGPriceByCommunity(community.id));
                //房源套数
                jsonObject.add("Num",communityDao.FindSecondNumByC_id(community.id));
                jsonArray.add(jsonObject);
            }

            response.getWriter().println(jsonArray);


        } catch (SQLException | ParseException e) {
            throw new RuntimeException(e);
        }


    }
}
