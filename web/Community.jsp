<%@ page import="java.sql.Connection" %>
<%@ page import="hm_db.DBUtil" %>
<%@ page import="hm_dao.Comment_Dao" %>
<%@ page import="hm_dao.Community_Dao" %>
<%@ page import="hm_bean.Community" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="hm_bean.Admin_Area" %>
<%@ page import="hm_dao.Admin_Area_Dao" %><%--
  Created by IntelliJ IDEA.
  User: 胡楠
  Date: 2022/6/22
  Time: 22:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
    <head>
        <title>小区</title>
        <link rel="stylesheet"  type="text/css"  href="css/ButtonStyle.css"/>
    </head>
    <body>
        <!--加载头部导航栏-->
        <%@ include file="head.jsp"%><br><br><br><br>

        <!--搜索栏-->
        <!-- autocomplete="off" 确保表单已关闭自动填充功能： -->
        <form autocomplete="off" action="Community.jsp" method="get">
            <div class="search-tag-desc-result" style="width:50%;height: 50px;text-align: center;background-color: #ffffff;margin-left: 25%">
                <input type="text" name="search" placeholder="请输入小区名开始找房" style="width: 80%;height: 100%">
                <input type="submit" value="搜索" style="height: 100%;width: 10%;">
            </div><br><br>
        </form>



        <!--分页列表显示-->
        <%
            //读取数据
            Connection connection= DBUtil.getConnection();
            Community_Dao communityDao=new Community_Dao(connection);
            Admin_Area_Dao adminAreaDao=new Admin_Area_Dao(connection);
            List<Community> communityList;
            String search=request.getParameter("search");//小区查询关键字
            if(search==null) search="";
            try {
                communityList=communityDao.FindCommunityByName(search);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            //分页显示，每页30条记录
            String ShowPage=request.getParameter("ShowPage");
            if(ShowPage==null) ShowPage="0";//默认显示0页
            int PageSize=30;
            int PageCount=communityList.size()%PageSize==0?communityList.size()/PageSize:communityList.size()/PageSize+1;
        %>
        <div style="background-color: #ffffff;width: 50%;margin-left: 25%"><span style="font-weight: bold">共为您找到</span><span style="color: dodgerblue"><%=communityList.size()%></span>
            <span style="font-weight: bold">个小区</span><hr></div>
        <%
            if(communityList.size()==0){
        %>
            <img src="暂无数据.png" alt="图片加载失败" style="margin-top:50px;margin-bottom:50px;margin-left: 25%;width: 750px;height: 200px">
        <%
        }
            else{
                for(int i=0;i<PageSize;i++){
                    int index=PageSize*Integer.parseInt(ShowPage)+i;
                    if(index<communityList.size()&&index>=0){
                        Admin_Area adminArea;
                        double AvgRentPrice=0,AvgSecPrice=0;
                        try {
                            adminArea=adminAreaDao.FindAdminByCId(communityList.get(index).id);
                            AvgRentPrice=communityDao.FindRentAVGPriceByCommunity(communityList.get(index).id);
                            AvgSecPrice=communityDao.FindSecondAVGPriceByCommunity(communityList.get(index).id);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
        %>
                    <div style="background-color: #ffffff;width: 50%;margin-left: 25%;">
                        <table style="display: inline-block;vertical-align: bottom">
                            <tr>
                                <td style="width: 400px"><a style="font-weight:bold;color: black" href="CommunityDetail.jsp?C_id=<%=communityList.get(index).id%>">
                                    <%=communityList.get(index).name%></a></td>
                                <td><span style="color: orange;font-size: x-large;vertical-align: bottom" ><%=AvgRentPrice%>元/月</span></td>
                                <td><span style="color: orange;font-size: x-large;vertical-align: bottom" ><%=AvgSecPrice%>元/平米</span><br></td>
                            </tr>
                            <tr>
                                <td><%=adminArea.name%>/<%=communityList.get(index).time%>建成&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                                <td><span style="color: lightgrey">当月租房均价</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                                <td><span style="color: lightgrey">当月二手房均价</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                            </tr>
                        </table><hr>
                    </div>

        <%          }
                }
            }
        %>



        <!--跳转栏-->
        <br>
        <div style="background-color: #ffffff;width: 50%;margin-left: 25%;text-align: right;">
            第<%=Integer.parseInt(ShowPage)+1%>页（共<%=PageCount%>页）
            <br>
            <a style="color: black" href="Community.jsp?ShowPage=0&search=<%=search%>">首页</a>
            <%
                if(Integer.parseInt(ShowPage)>0){
            %>
            <a style="color: black" href="Community.jsp?ShowPage=<%=Integer.parseInt(ShowPage)-1%>&search=<%=search%>">上一页</a>
            <%
                }
            %>
            <%   //根据PageCount的值显示上下2页的数字并附加上相应的超链接
                for(int i=Integer.parseInt(ShowPage)-2;i<=Integer.parseInt(ShowPage)+2;i++){
                    if(i>=0&&i<PageCount){
            %>
            <a style="color: black" href="Community.jsp?ShowPage=<%=i%>&search=<%=search%>"><%=i+1%></a>
            <%
                    }
                }
            %>
            <%
                if(Integer.parseInt(ShowPage)<PageCount-1){
            %>
            <a style="color: black" href="Community.jsp?ShowPage=<%=Integer.parseInt(ShowPage)+1%>&search=<%=search%>">下一页</a>
            <%
                }
            %>
            <a style="color: black" href="Community.jsp?ShowPage=<%=PageCount-1%>&search=<%=search%>">末页</a>
        </div><br><br><br><br><br><br><br>



        <!--底部栏-->
        <%@ include file="bottom.jsp"%>

        <script src="https://cdn.bootcss.com/canvas-nest.js/2.0.4/canvas-nest.js" type="text/javascript" color="255,0,0" opacity="0.7" count="150"></script>
    </body>
</html>
