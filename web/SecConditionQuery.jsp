<%@ page import="java.sql.Connection" %>
<%@ page import="hm_db.DBUtil" %>
<%@ page import="hm_dao.Admin_Area_Dao" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.SQLException" %><%--
  Created by IntelliJ IDEA.
  User: 胡楠
  Date: 2022/6/19
  Time: 15:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!--条件选择框以及查询-->
<%
  //获取参数
  String Name=request.getParameter("AdminSelect");
  if("null".equals(Name)) Name=null;
  Integer MinPrice=null;
  if(request.getParameter("MinPrice")!=null&&!request.getParameter("MinPrice").equals("null"))
    MinPrice=Integer.parseInt(request.getParameter("MinPrice"));
  Integer MaxPrice=null;
  if(request.getParameter("MaxPrice")!=null&&!request.getParameter("MaxPrice").equals("null"))
    MaxPrice=Integer.parseInt(request.getParameter("MaxPrice"));
  String H_type=request.getParameter("H_type");
  if("null".equals(H_type)) H_type=null;
  Float MinArea=null;
  if(request.getParameter("MinArea")!=null&&!request.getParameter("MinArea").equals("null"))
    MinArea=Float.valueOf(request.getParameter("MinArea"));
  Float MaxArea=null;
  if(request.getParameter("MaxArea")!=null&&!request.getParameter("MaxArea").equals("null"))
    MaxArea=Float.valueOf(request.getParameter("MaxArea"));
  Integer ShowPage=null;
  if(request.getParameter("ShowPage")!=null&&!request.getParameter("ShowPage").equals("null"))
    ShowPage=Integer.parseInt(request.getParameter("ShowPage"));
  if(ShowPage==null) ShowPage=0;//默认显示首页
%>

<form method="get" action="FindSecHouse">
  <tr>
    <td>行政区</td>
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
    <td><label><input type="radio" name="AdminSelect" value="<%=name%>" checked="checked"><%=name%></label></td>
    <%
    }
    else{
    %>
    <td><label><input type="radio" name="AdminSelect" value="<%=name%>"><%=name%></label></td>
    <%
        }
      }
    %>
  </tr>
  <br>
  <tr>
    <td>价格</td>
    <td>
      <label><input type="number" name="MinPrice" min="1" max="99999999" value="<%=MinPrice%>">-
        <input type="number" name="MaxPrice" min="1" max="99999999" value="<%=MaxPrice%>"></label>元/平米
    </td>
  </tr>
  <br>
  <tr>
    <td>户型</td>
    <td><label><input type="text" name="H_type" value="<%=H_type!=null?H_type:""%>"></label></td>
  </tr>
  <br>
  <tr>
    <td>面积</td>
    <td>
      <label><input type="number" name="MinArea" min="1" max="999" value="<%=MinArea%>">-
        <input type="number" name="MaxArea" min="1" max="999" value="<%=MaxArea%>"></label>平米
    </td>
  </tr>
  <br>
  <tr><td><label><button type="submit" name="FindSecondHouse" value="查找"></button></label></td></tr>
  <br>
</form>


