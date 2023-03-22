<%@ page import="org.locationtech.jts.io.WKTReader" %>
<%@ page import="org.locationtech.jts.geom.Point" %>
<%@ page import="java.util.List" %>
<%@ page import="hm_dao.Surround_Dao" %>
<%@ page import="hm_bean.Surround" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.locationtech.jts.index.strtree.STRtree" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="hm_db.DBUtil" %>
<%@ page import="hm_bean.Community" %>
<%@ page import="hm_dao.Community_Dao" %>
<%@ page import="org.locationtech.jts.geom.Polygon" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="org.locationtech.jts.io.ParseException" %>
<%--
  Created by IntelliJ IDEA.
  User: 胡楠
  Date: 2022/6/23
  Time: 11:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!--地图多标签页选择-->
<html>
    <head>
        <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <link rel="stylesheet"  type="text/css"  href="css/Map.css"/>
        <script type="text/javascript" src="https://api.map.baidu.com/api?v=3.0&ak=8CHdphqkGQTAwSxFOE04NYftqrIvU4kz">
            //v3.0版本的引用方式：src="https://api.map.baidu.com/api?v=3.0&ak=您的密钥"
        </script>
        <title>周边地图</title>
    </head>
    <body>
        <div class="MapTab">
            <span style="font-size: large;font-weight: bold">周边配套</span><hr>
            <label><input type="button" onclick="InitOverlays('医院');" name="S_type" value="医院"></label>
            <label><input type="button" onclick="InitOverlays('公园');" name="S_type" value="公园"></label>
            <label><input type="button" onclick="InitOverlays('学校');" name="S_type" value="学校"></label>
            <label><input type="button" onclick="InitOverlays('商场');" name="S_type" value="商场"></label>
        </div><br>
        <!--地图-->
        <div id="Map" class="Map"></div>
        <script type="text/javascript">
            <%
                //获取参数
                String C_id=request.getParameter("C_id");
                Connection connection=DBUtil.getConnection();
                //读取geography对象中的坐标点
                WKTReader reader=new WKTReader();
                Point center;//小区的中心坐标
                List<Point> sur_pnts=new ArrayList<>();
                Community_Dao communityDao=new Community_Dao(connection);
                Surround_Dao surroundDao=new Surround_Dao(connection);
                Community community;
                List<Surround> surrounds;//周边
                try {
                    community=communityDao.FindCommunityById(C_id);
                    surrounds=surroundDao.FindSurroundByCommunity(community);
                    Polygon polygon=(Polygon) reader.read(community.loc.STAsText());
                    center=polygon.getCentroid();
                    for(Surround sur:surrounds){
                        if(!sur.type.equals("道路")){//暂不显示道路
                            sur_pnts.add((Point) reader.read(sur.loc.STAsText()));
                        }
                    }
                } catch ( SQLException|ParseException e) {
                    throw new RuntimeException(e);
                }
            %>

            //以小区中心点作为地图中心点
            debugger;
            var map = new BMap.Map("Map");// 创建地图实例
            var center = new BMap.Point(<%=center.getX()%>, <%=center.getY()%>);// 创建点坐标
            map.centerAndZoom(center, 17);// 初始化地图，设置中心点坐标和地图级别
            var icon=new BMap.Icon("http://api.map.baidu.com/img/markers.png",new BMap.Size(23, 25),{
                anchor: new BMap.Size(10, 25), // 指定定位位置
                imageOffset: new BMap.Size(0, -12*25) // 设置图片偏移使用左侧的图片
                //imageOffset: new BMap.Size(-20, 0 - 10 * 25) // 设置图片偏移使用右侧的图片0-x*25代表使用第x+1张图片
            });//小区标记图标
            var center_marker= new BMap.Marker(center,{icon:icon});//地图覆盖物，标记
            var center_info = new BMap.InfoWindow("<%=community.name%>");
            center_marker.addEventListener("click", function(){
                map.openInfoWindow(center_info,center); //开启信息窗口
            })
            map.addOverlay(center_marker);


            //根据周边类别初始化地图标注
            function InitOverlays(S_type) {
                debugger;
                map.clearOverlays();//清空原有的覆盖物
                var icon=new BMap.Icon("http://api.map.baidu.com/img/markers.png",new BMap.Size(23, 25),{
                    anchor: new BMap.Size(10, 25), // 指定定位位置
                    imageOffset: new BMap.Size(0, -12*25) // 设置图片偏移使用左侧的图片
                    //imageOffset: new BMap.Size(-20, 0 - 10 * 25) // 设置图片偏移使用右侧的图片0-x*25代表使用第x+1张图片
                });//小区标记图标
                var center_marker= new BMap.Marker(center,{icon:icon});//地图覆盖物，标记
                var center_info = new BMap.InfoWindow("<%=community.name%>");
                center_marker.addEventListener("click", function(){
                    map.openInfoWindow(center_info,center); //开启信息窗口
                })
                map.addOverlay(center_marker);
                <%
                for(int i=0;i<sur_pnts.size();i++){
                %>
                debugger;
                if(typeof(S_type) == "undefined"||S_type==="<%=surrounds.get(i).type%>") {
                    AddOverlay(<%=sur_pnts.get(i).getX()%>,<%=sur_pnts.get(i).getY()%>,"<%=surrounds.get(i).name%>");
                }
                <%
                }
                %>
            }

            //根据坐标，信息添加覆盖物
            function AddOverlay(pnt_x,pnt_y,sur_info) {
                const pnt = new BMap.Point(pnt_x, pnt_y);
                const marker = new BMap.Marker(pnt);//地图覆盖物，标记
                const info = new BMap.InfoWindow(sur_info);
                map.addOverlay(marker);
                marker.addEventListener("click", function () {
                    map.openInfoWindow(info, pnt); //开启信息窗口
                });
            }
        </script>
    </body>
</html>

