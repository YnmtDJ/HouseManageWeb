<%@ page import="java.sql.Connection" %>
<%@ page import="hm_db.DBUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Random" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="hm_dao.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="hm_bean.*" %><%--
  Created by IntelliJ IDEA.
  User: 胡楠
  Date: 2022/6/25
  Time: 23:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link rel="stylesheet"  type="text/css"  href="css/ButtonStyle.css"/>
    <link rel="stylesheet"  type="text/css"  href="css/wrap.css"/>
    <title>租房详情</title>
    <!--CSS样式-->
    <style>
        /*房源所有信息样式*/
        .AllInfo{
            padding-left: 20%;
            padding-right: 40%;
        }

    </style>
    <script type="text/javascript" src="https://api.map.baidu.com/api?v=3.0&ak=8CHdphqkGQTAwSxFOE04NYftqrIvU4kz">
        //v3.0版本的引用方式：src="https://api.map.baidu.com/api?v=3.0&ak=您的密钥"
    </script>
</head>
<body>
<!--联系经纪人看房结果-->
<%
    String SeeFlag=(String)request.getAttribute("SeeFlag");
    if(SeeFlag!=null){
        if(SeeFlag.equals("Success")){
%>
<script>confirm("成功联系该经纪人去看房源！")</script>
<%
}
else if(SeeFlag.equals("Duplicate")){
%>
<script>alert("您今天已经联系该经纪人去看房源了。")</script>
<%
}
else{
%>
<script>alert("看房记录插入失败，请联系管理员！")</script>
<%
        }
    }
%>

<!--加载头部导航栏-->
<%@ include file="head.jsp"%><br><br><br><br>

<%
    //获取信息
    String H_id=request.getParameter("H_id");
    Connection connection= DBUtil.getConnection();
    House_Dao houseDao=new House_Dao(connection);
    Community_Dao community_dao=new Community_Dao(connection);
    Admin_Area_Dao adminAreaDao=new Admin_Area_Dao(connection);
    Agent_Dao agentDao=new Agent_Dao(connection);
    Renting house;
    Community community=null;
    Admin_Area adminArea=null;
    Agent agent=null;
    int SeeNum=0;
    try {
        house=(Renting) houseDao.FindHouseById(H_id);
        if(house!=null){
            community=community_dao.FindCommunityById(house.C_id);
            adminArea=adminAreaDao.FindAdminByCId(house.C_id);
            List<Agent> agentList=agentDao.FindAgentsByCId(house.C_id);
            Random random=new Random();
            agent=agentList.get(random.nextInt(0,agentList.size()));//随机选取一个经纪人展示
            SeeNum=houseDao.FindSeeNumByHouse(house);
        }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }

    assert house != null;%>
<!--房源信息显示-->
<!--主要信息-->
<div style="width: 60%;margin-left: 20%">
    <img alt="图片加载失败" src="ShowImg.jsp?H_id=<%=H_id%>" style="vertical-align:middle;width: 480px; height: 320px"/>
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <span style="vertical-align: middle;display: inline-block;border: 1px solid lightgrey;padding: 25px">
                <span style="color: orange;font-size: xxx-large"><%=house.price%>元/月</span>
                <hr>
                <span style="font-size: large;font-weight: bold"><%=house.type%>&nbsp;&nbsp;&nbsp;</span>
                <span style="font-size: large;font-weight: bold"><%=house.direct%>&nbsp;&nbsp;&nbsp;</span>
                <span style="font-size: large;font-weight: bold"><%=house.area%>平米</span>
                <hr>
                <span>小区名称&nbsp;&nbsp;<%=community.name%></span>
                <br>
                <span>所在区域&nbsp;&nbsp;<%=adminArea.name%></span>
                <hr>
                <form method="post" action="SeeHouse?A_id=<%=agent.id%>&H_id=<%=house.id%>">
                    <img alt="图片加载失败" src="头像.jpg" style="width: 40px;height: 30px;vertical-align: middle"/>
                    <span style="font-size: medium;font-weight: bold"><%=agent.name%></span>
                    <input type="submit" style="background-color:cornflowerblue;color: lightgrey" value="联系经纪人去看房"
                           onmouseover="this.style.color='white'" onmouseout="this.style.color='lightgrey'">
                </form>
            </span>
</div><br><br><br><br>
<!--所有信息-->
<div style="width: 35%;margin-left: 20%;">
            <span>
                <span style="font-size: large;font-weight: bold">房源基本信息</span><hr>
                <table style="text-align: left;">
                    <tr><td style="width: 300px"></td></tr>
                    <tr>
                        <td><span>房屋户型&nbsp;&nbsp;<%=house.type%></span></td>
                        <td><span>房屋面积&nbsp;&nbsp;<%=house.area%>m²</span></td>
                    </tr>
                    <tr>
                        <td><span>装修情况&nbsp;&nbsp;<%=house.decorate%></span></td>
                        <td><span>房屋朝向&nbsp;&nbsp;<%=house.direct%></span></td>
                    </tr>
                    <tr>
                        <td> <span>梯户比例&nbsp;&nbsp;<%=house.rate%></span></td>
                        <td><span>所在楼层&nbsp;&nbsp;<%=house.layer%></span></td>
                    </tr>
                    <tr>
                        <td><span>发布日期&nbsp;&nbsp;<%=house.time%>&nbsp;&nbsp;&nbsp;&nbsp;</span></td>
                        <td><span>一周带看次数&nbsp;&nbsp;<%=SeeNum%></span></td>
                    </tr>
                </table>
            </span>
</div><br><br><br>



<!--载入周边地图-->
<jsp:include page="sur_map.jsp">
    <jsp:param name="C_id" value="<%=community.id%>"/>
</jsp:include><br><br><br><br>



<!--相似房源推荐-->
<%
    List<Renting> houseList;//总价相似
    List<Renting> houseList1;//户型相似
    try {
        int RecommendNum=4;//推荐个数
        houseList=houseDao.FindSimilarRentByPrice(house,RecommendNum);
        houseList1=houseDao.FindSimilarRentByType(house,RecommendNum);
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
%>
<div class="wrap">
            <span>
                 <span style="font-size: large;font-weight: bold">同价优质房源</span><hr>
                 <table>
                     <!--图片-->
                     <tr>
                         <td>
                             <%
                                 for(Renting RecHouse:houseList){
                             %>
                             <img alt="图片加载失败" src="ShowImg.jsp?H_id=<%=RecHouse.id%>" style="width: 180px; height: 120px"/>
                             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                             <%
                                 }
                             %>
                         </td>
                     </tr>
                     <!--简要信息-->
                     <tr>
                         <td>
                             <%
                                 for(Renting RecHouse:houseList){
                             %>
                             <a style="color: black" href="RentDetail.jsp?H_id=<%=RecHouse.id%>">
                                <span><%=RecHouse.type%>·<%=RecHouse.area%>平米</span>
                                <span style="color: orange;font-size: large"><%=RecHouse.price%>元/月</span>
                             </a>&nbsp;
                             <%
                                 }
                             %>
                         </td>
                     </tr>
                 </table>
                <br>
                <span style="font-size: large;font-weight: bold">相似户型房源</span><hr>
                 <table>
                     <!--图片-->
                     <tr>
                         <td>
                             <%
                                 for(Renting RecHouse:houseList1){
                             %>
                             <img alt="图片加载失败" src="ShowImg.jsp?H_id=<%=RecHouse.id%>" style="width: 180px; height: 120px"/>
                             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                             <%
                                 }
                             %>
                         </td>
                     </tr>
                     <!--简要信息-->
                     <tr>
                         <td>
                             <%
                                 for(Renting RecHouse:houseList1){
                             %>
                             <a style="color: black" href="RentDetail.jsp?H_id=<%=RecHouse.id%>">
                                <span><%=RecHouse.type%>·<%=RecHouse.area%>平米</span>
                                <span style="color: orange;font-size: large"><%=RecHouse.price%>元/月</span>
                             </a>&nbsp;
                             <%
                                 }
                             %>
                         </td>
                     </tr>
                 </table>
            </span>
</div><br><br><br><br>



<!--用户评论-->
<!--评论框-->
<div style="width: 60%;text-align: left;padding-left: 20%">
    <span style="font-size: large;font-weight: bold;">请写下您的评论（100字以内）：</span>
    <form method="post" action="Comment?H_id=<%=house.id%>&Type=租房">
        <label>
            <textarea name="comment" style="width: 100%;height: 200px;font-size: large" maxlength="100"></textarea>
            <br><br>
            <input type="submit" value="评论" style="float: right">
        </label>
    </form>
</div>

<!--用户品论内容-->
<%
    Comment_Dao commentDao=new Comment_Dao(connection);
    List<User> users=new ArrayList<>();
    List<String> comments=new ArrayList<>();
    try {
        commentDao.FindUserCommentsByH_id(H_id,users,comments);
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
%>
<div style="width: 60%;text-align: left;padding-left: 20%">
    <span style="font-size: large;font-weight: bold">用户评论</span><hr>
    <%
        if(comments.size()==0){
    %>
    <img src="暂无数据.png" alt="图片丢失" style="width: 750px;height: 200px">
    <%
    }
    else{
        for(int i=0;i<comments.size();i++){
    %>
                <img src="头像.jpg" alt="图片丢失" style="width: 40px;height: 30px">
                <span style="display: inline-block;vertical-align: middle;text-align: left;">
                 <span style="font-size: large;font-weight: bold"><%=users.get(i).U_name%></span><br>
                 <span><%=comments.get(i)%></span>
                 </span><br><br>
    <%
            }
        }
    %>
</div><br><br><br>

<!--底部栏-->
<%@ include file="bottom.jsp"%>
<script src="https://cdn.bootcss.com/canvas-nest.js/2.0.4/canvas-nest.js" type="text/javascript" color="255,0,0" opacity="0.7" count="150"></script>

</body>
</html>
