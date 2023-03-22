<%@ page import="java.sql.Connection" %>
<%@ page import="hm_db.DBUtil" %>
<%@ page import="hm_dao.Admin_Area_Dao" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="hm_dao.House_Dao" %>
<%@ page import="hm_bean.Renting" %>
<%@ page import="hm_bean.Admin_Area" %>


<%--
  Created by IntelliJ IDEA.
  User: 胡楠
  Date: 2022/6/25
  Time: 14:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
    <link rel="stylesheet"  type="text/css"  href="css/wrap.css"/>
    <link rel="stylesheet"  type="text/css"  href="css/ButtonStyle.css"/>
    <title>租房</title>
</head>
<body>
<%
    //获取参数
    String Name= request.getParameter("AdminSelect");
    String MinPrice=  request.getParameter("MinPrice");
    String MaxPrice= request.getParameter("MaxPrice");
    String H_type= request.getParameter("H_type");
    String MinArea=  request.getParameter("MinArea");
    String MaxArea= request.getParameter("MaxArea");
    String ShowPage= request.getParameter("ShowPage");
    if(ShowPage==null) ShowPage="0";//默认显示首页
    String S_name=request.getParameter("S_name");
%>
<!--加载头部导航栏-->
<%@ include file="head.jsp"%>
<br><br>
<!--条件查询框-->
<form method="get" action="FindRent">
    <table style="background-color: #f8f3f3;margin-left: 15%;padding: 10px">
        <tr>
            <td>行政区</td>
            <td>
                <%
                    Connection connection= DBUtil.getConnection();
                    Admin_Area_Dao adminAreaDao=new Admin_Area_Dao(connection);
                    List<String> names=null;
                    try {
                        names=adminAreaDao.FindAllAdminName();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    for(String name:names){
                        if(name.equals(Name)){
                %>
                <label><input type="radio" name="AdminSelect" value="<%=name%>" checked="checked"><%=name%></label>
                <%
                        }
                        else{
                %>
                <label><input type="radio" name="AdminSelect" value="<%=name%>"><%=name%></label>
                <%
                        }
                    }
                %>
            </td>
        </tr>
        <tr>
            <td>价格</td>
            <td>
                <label><input type="number" name="MinPrice" min="1" max="99999999" value="<%=MinPrice%>">-
                    <input type="number" name="MaxPrice" min="1" max="99999999" value="<%=MaxPrice%>"></label>元/月
            </td>
        </tr>
        <tr>
            <td>户型</td>
            <td><label><input type="text" name="H_type" value="<%=H_type!=null?H_type:""%>"></label></td>
        </tr>
        <tr>
            <td>面积</td>
            <td>
                <label><input type="number" name="MinArea" min="1" max="999" value="<%=MinArea%>">-
                    <input type="number" name="MaxArea" min="1" max="999" value="<%=MaxArea%>"></label>平米
            </td>
        </tr>
        <tr>
            <td></td>
            <td style="text-align: right"><input style="width: 60px;height: 30px" type="submit" name="FindRenting" value="查找"></td>
        </tr>
    </table>
</form>
<br><br>


<!--分页列表显示-->
<%
    //获取连接
    House_Dao houseDao=new House_Dao(connection);
    List<Renting> houseList;
    int PageSize=30;//分页显示，每页30条记录
    int RentNum=0;//满足的租房个数
    //修改参数类型
    Integer minprice=null;
    if(MinPrice!=null&&!MinPrice.equals("")) minprice=Integer.valueOf(MinPrice);
    Integer maxprice=null;
    if(MaxPrice!=null&&!MaxPrice.equals("")) maxprice=Integer.valueOf(MaxPrice);
    if("".equals(H_type)) H_type=null;
    Float maxarea=null;
    if(MaxArea!=null&&!MaxArea.equals("")) maxarea=Float.valueOf(MaxArea);
    Float minarea=null;
    if(MinArea!=null&&!MinArea.equals("")) minarea=Float.valueOf(MinArea);
    //查询
    try {
        if(request.getAttribute("houseList")==null) {//条件查询
            Admin_Area adminArea = adminAreaDao.FindAdminByName(Name);
            houseList = houseDao.FindRenting(adminArea, null, minprice, maxprice, H_type, minarea, maxarea,
                    Integer.parseInt(ShowPage) * PageSize + 1, PageSize);
            RentNum = houseDao.FindRentNum(adminArea, null, minprice, maxprice, H_type, minarea, maxarea);
        }
        else{//根据周边查询
            houseList=(List<Renting>)request.getAttribute("houseList");
            RentNum=houseList.size();
            int fromIndex=Integer.parseInt(ShowPage) * PageSize;
            int toIndex=Math.min(fromIndex+PageSize,houseList.size());
            houseList=houseList.subList(fromIndex,toIndex);
        }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
%>
<div style="background-color: #ffffff;width: 70%;margin-left: 15%">
    <div style="background-color: #ffffff;width: 70%;display: inline-block">
        <%
            //页面个数
            int PageCount=RentNum%PageSize==0?RentNum/PageSize:RentNum/PageSize+1;
            if(houseList.size()==0){
        %>
        <img src="暂无数据.png" alt="图片加载失败" style="width: 100%;height: 300px">
        <%
            }
            else{
                for(int i=0;i<PageSize;i++){
                    /*int index=PageSize*Integer.parseInt(ShowPage)+i;*/
                    if(i<houseList.size()){
        %>
        <div style="background-color: #ffffff;">
            <img src="ShowImg.jsp?H_id=<%=houseList.get(i).id%>" alt="图片加载失败" style="vertical-align: middle;width: 200px;height: 100px;">
            &nbsp;&nbsp;&nbsp;&nbsp;
            <span style="display: inline-block;">
              <a style="font-weight:bold;color: black" href="RentDetail.jsp?H_id=<%=houseList.get(i).id%>">点击查看详情</a>
              <br>
              <%=houseList.get(i).area%>平方米/<%=houseList.get(i).direct%>/<%=houseList.get(i).type%>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
              <span style="color: orange;font-size: x-large;vertical-align: bottom" ><%=houseList.get(i).price%>元/月</span>
            </span>
            <hr>
        </div>

        <%
                    }
                }
            }
        %>
        <div style="margin-right: 0;text-align: right">
            <!--跳转栏-->
            <%String condition="";//组合条件
                if(Name!=null) condition+="&AdminSelect="+Name;
                if(MinPrice!=null) condition+="&MinPrice="+MinPrice;
                if(MaxPrice!=null) condition+="&MaxPrice="+MaxPrice;
                if(H_type!=null) condition+="&H_type="+H_type;
                if(MinArea!=null) condition+="&MinArea="+MinArea;
                if(MaxArea!=null) condition+="&MaxArea="+MaxArea;
                if(S_name!=null) condition+="&S_name="+S_name;
            %>
            <br>
            第<%=Integer.parseInt(ShowPage)+1%>页（共<%=PageCount%>页）
            <br>
            <a style="color: black" href="FindRent?ShowPage=0<%=condition%>">首页</a>
            <%
                if(Integer.parseInt(ShowPage)>0){
            %>
            <a style="color: black" href="FindRent?ShowPage=<%=Integer.parseInt(ShowPage)-1%><%=condition%>">上一页</a>
            <%
                }
            %>
            <%   //根据PageCount的值显示上下2页的数字并附加上相应的超链接
                for(int i=Integer.parseInt(ShowPage)-2;i<=Integer.parseInt(ShowPage)+2;i++){
                    if(i>=0&&i<PageCount){
            %>
            <a style="color: black" href="FindRent?ShowPage=<%=i%><%=condition%>"><%=i+1%></a>
            <%
                    }
                }
            %>
            <%
                if(Integer.parseInt(ShowPage)<PageCount-1){
            %>
            <a style="color: black" href="FindRent?ShowPage=<%=Integer.parseInt(ShowPage)+1%><%=condition%>">下一页</a>
            <%
                }
            %>
            <a style="color: black" href="FindRent?ShowPage=<%=PageCount-1%><%=condition%>">末页</a>
        </div>
    </div>
    <div style="background-color: #ffffff;display: inline-block;vertical-align: top;">
        <img src="试试地图找房.png" alt="图片加载失败" style="horiz-align: center">
        <span style="display: block;margin-left: 15px">
          <input style="width: 100%;height: 60px" type="submit" onclick='location.href=("RentMap2.jsp")'
                 value="试试地图找房">
        </span>
    </div>
</div>
<br><br><br><br>



<!--底部栏-->
<%@ include file="bottom.jsp"%>
<script src="https://cdn.bootcss.com/canvas-nest.js/2.0.4/canvas-nest.js" type="text/javascript" color="255,0,0" opacity="0.7" count="150"></script>

</body>
</html>

