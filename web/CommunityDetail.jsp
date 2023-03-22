<%@ page import="java.sql.Connection" %>
<%@ page import="hm_db.DBUtil" %>
<%@ page import="hm_dao.Community_Dao" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="hm_dao.Admin_Area_Dao" %>
<%@ page import="hm_dao.House_Dao" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.locationtech.jts.io.WKTReader" %>
<%@ page import="org.locationtech.jts.geom.Point" %>
<%@ page import="hm_dao.Surround_Dao" %>
<%@ page import="hm_bean.*" %>
<%@ page import="org.locationtech.jts.geom.Polygon" %>
<%@ page import="org.locationtech.jts.io.ParseException" %><%--
  Created by IntelliJ IDEA.
  User: 胡楠
  Date: 2022/6/22
  Time: 20:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
    <head>
        <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <link rel="stylesheet"  type="text/css"  href="css/ButtonStyle.css"/>
        <link rel="stylesheet"  type="text/css"  href="css/wrap.css"/>
        <title>小区详情</title>
        <script type="text/javascript" src="https://api.map.baidu.com/api?v=3.0&ak=8CHdphqkGQTAwSxFOE04NYftqrIvU4kz">
            //v3.0版本的引用方式：src="https://api.map.baidu.com/api?v=3.0&ak=您的密钥"
        </script>
    </head>
    <body>
        <!--加载头部导航栏-->
        <%@ include file="head.jsp"%>

        <%
            //获取参数
            String C_id=request.getParameter("C_id");
            Connection connection= DBUtil.getConnection();
            Community_Dao communityDao=new Community_Dao(connection);
            Admin_Area_Dao adminAreaDao=new Admin_Area_Dao(connection);
            Community community=null;
            Admin_Area adminArea=null;
            double AvgRentPrice=0;
            double AvgSecHousePrice=0;
            try {
                community=communityDao.FindCommunityById(C_id);
                AvgRentPrice=communityDao.FindRentAVGPriceByCommunity(community.id);
                AvgSecHousePrice=communityDao.FindSecondAVGPriceByCommunity(community.id);
                adminArea=adminAreaDao.FindAdminByCId(C_id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        %>
        <!--小区名标题-->
        <br><br>
        <span style="padding-left:20%;width: 60%;text-align:left;font-size: xxx-large;font-weight: bold"><%=community.name%></span>
        <br><br>

        <!--小区信息-->
        <div class="ComInfo" style="width: 60%;padding-left: 20%;">
            <!--详细信息-->
            <table style="text-align: left;display: inline-block">
                <tr>
                    <td><span>当月租房均价&nbsp;&nbsp;</span></td>
                    <td style="width: 200px"><span style="color: orange;font-size: x-large"><%=AvgRentPrice%>元/月</span></td>
                    <td><span>当月二手房均价&nbsp;&nbsp;</span></td>
                    <td><span style="color: orange;font-size: x-large"><%=AvgSecHousePrice%>元/平</span></td>
                </tr>
                <tr>
                    <td>所在区域</td><td><%=adminArea.name%></td>
                    <td>容积率</td><td><%=community.rate%></td>
                </tr>
                <tr>
                    <td>绿化率</td><td><%=community.green_rate%></td>
                    <td>物业名称</td><td><%=community.pro_name%></td>
                </tr>
                <tr>
                    <td>建筑类型</td><td><%=community.type%></td>
                    <td>建造年代</td><td><%=community.time%></td>
                </tr>
            </table>

            <!--编辑小区-->
            <span style="display: inline-block;margin-left: 120px">
                <%
                    //普通用户不能发布房源
                    if(((User)(request.getSession().getAttribute("user"))).U_type.equals("普通用户")){
                %>
                    <input type="submit" onclick="return alert('您无权限！')"
                                  value="发布租房    " style="font-size: x-large">
                    <input type="submit" onclick="return alert('您无权限！')"
                                  value="发布二手房" style="font-size: x-large">
                <%
                    }
                    else{//企业用户，管理员能够发布
                %>
                    <form method="post" action="PostRent.jsp?C_id=<%=C_id%>">
                         <label><input type="submit" value="发布租房    " style="font-size: large"></label>
                    </form>
                    <form method="post" action="PostSecHouse.jsp?C_id=<%=C_id%>">
                         <label><input type="submit" value="发布二手房" style="font-size: large"></label>
                    </form>
                <%
                    }
                %>
            </span><hr>
        </div><br><br><br>


        <!--载入周边地图-->
        <jsp:include page="sur_map.jsp">
            <jsp:param name="C_id" value="<%=C_id%>"/>
        </jsp:include><br><br><br><br>




        <!--优质房源推荐-->
        <%
            House_Dao houseDao=new House_Dao(connection);
            List<Second_House> secondHouseList;
            List<Renting> rentingList;
            int RecNum=4;//推荐个数
            try {
                secondHouseList=houseDao.FindSecondByCId(C_id,RecNum);
                rentingList=houseDao.FindRentingByCId(C_id,RecNum);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        %>
        <div style="width: 60%;margin-left: 20%;">
            <span>
                 <span style="font-size: large;font-weight: bold"><%=community.name%>二手房在售</span><hr>
                 <table>
                     <!--图片-->
                     <tr>
                         <td>
                             <%
                                 if(secondHouseList.size()==0){
                             %>
                                <img alt="图片加载失败" src="暂无数据.png" style="width: 750px;height: 200px"/>
                             <%
                                 }
                                 else{
                                     for(Second_House RecHouse:secondHouseList){
                             %>
                                 <img alt="图片加载失败" src="ShowImg.jsp?H_id=<%=RecHouse.id%>" style="width: 180px; height: 120px"/>
                             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                             <%
                                    }
                                 }
                             %>
                         </td>
                     </tr>
                     <!--简要信息-->
                     <tr>
                         <td>
                             <%
                                 if(secondHouseList.size()!=0){
                                    for(Second_House RecHouse:secondHouseList){
                             %>
                                 <a style="color: black" href="SecHouseDetail.jsp?H_id=<%=RecHouse.id%>">
                                    <span><%=RecHouse.type%>·<%=RecHouse.area%>平米</span>
                                    <span style="color: orange;font-size: large"><%=(int)(RecHouse.price*RecHouse.area/10000)%>万</span>
                                 </a>&nbsp;
                             <%
                                    }
                                 }
                             %>
                         </td>
                     </tr>
                 </table>
                <br>

                <span style="font-size: large;font-weight: bold"><%=community.name%>租房在售</span><hr>
                 <table>
                     <!--图片-->
                     <tr>
                         <td>
                             <%
                                 if(rentingList.size()==0){
                             %>
                                <img alt="图片加载失败" src="暂无数据.png" style="width: 750px;height: 200px"/>
                             <%
                                 }
                                 else{
                                    for(Renting renting:rentingList){
                             %>
                                <img alt="图片加载失败" src="ShowImg.jsp?H_id=<%=renting.id%>" style="width: 180px; height: 120px"/>
                             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                             <%
                                    }
                                 }
                             %>
                         </td>
                     </tr>
                     <!--简要信息-->
                     <tr>
                         <td>
                             <%
                                 if(rentingList.size()!=0){
                                    for(Renting renting:rentingList){
                             %>
                                 <a style="color: black" href="RentDetail.jsp?H_id=<%=renting.id%>">
                                    <span><%=renting.type%>·<%=renting.area%>平米</span>
                                    <span style="color: orange;font-size: large"><%=renting.price%>元/月</span>
                                 </a>&nbsp;
                             <%
                                    }
                                 }
                             %>
                         </td>
                     </tr>
                 </table>
            </span>
        </div><br><br><br><br>


        <!--底部栏-->
        <%@ include file="bottom.jsp"%>

        <script src="https://cdn.bootcss.com/canvas-nest.js/2.0.4/canvas-nest.js" type="text/javascript" color="255,0,0" opacity="0.7" count="150"></script>

    </body>
</html>
