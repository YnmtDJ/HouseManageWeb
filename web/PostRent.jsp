<%@ page import="java.sql.Connection" %>
<%@ page import="hm_db.DBUtil" %>
<%@ page import="hm_dao.Community_Dao" %>
<%@ page import="hm_dao.Building_Dao" %>
<%@ page import="hm_bean.Community" %>
<%@ page import="hm_bean.Building" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.SQLException" %><%--
  Created by IntelliJ IDEA.
  User: 胡楠
  Date: 2022/6/26
  Time: 15:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
  <title>发布出租房源</title>
  <link rel="stylesheet"  type="text/css"  href="css/ButtonStyle.css"/>
</head>
<body>
<!--是否发布成功弹框-->
<script type="text/javascript" language="JavaScript">
  <%
      String post_flag=(String)request.getAttribute("Flag");
      if(post_flag!=null&&post_flag.equals("Success")){
  %>
  confirm("发布成功！");
  <%
      }
      else if(post_flag!=null&&post_flag.equals("Failed")){
  %>
  alert("发布失败，请联系管理员！");
  <%
      }
  %>
</script>

<!--加载头部导航栏-->
<%@ include file="head.jsp"%>

<%
  //获取数据
  String C_id=request.getParameter("C_id");
  Connection connection= DBUtil.getConnection();
  Community_Dao communityDao=new Community_Dao(connection);
  Building_Dao buildingDao=new Building_Dao(connection);
  Community community;
  List<Building> buildingList;
  try {
    community=communityDao.FindCommunityById(C_id);
    buildingList=buildingDao.FindBuildByCId(C_id);
  } catch (SQLException e) {
    throw new RuntimeException(e);
  }

%>

<!--标题-->
<div style="text-align: center;background-color: #f3efef;padding: 20px;">
  <span style="font-size: xxx-large">发布出租房源(<%=community.name%>)</span><br>
  <span style="color: darkgrey">数万线下门店 · 快速全城推广 · 专业经纪人服务</span>
</div><br><br><br>

<!--主体-->
<div style="background-color:transparent;width: 50%;margin-left: 25%;padding-left:50px;text-align: left;
        border: 1px solid lightgrey;border-radius: 1%">
  <form method="post" action="PostRent?C_id=<%=C_id%>" enctype="multipart/form-data">
    <table>
      <tr>
        <td style="width: 380px;height: 50px">
          <span style="font-weight: bold">单元号&nbsp;&nbsp;</span>
          <label>
            <select name="B_id" data-placeholder="单元号">
              <%
                for(Building building:buildingList){
              %>
              <option value="<%=building.id%>"><%=building.name%></option>
              <%
                }
              %>
            </select>
          </label>
        </td>
        <td>
          <span style="font-weight: bold">户型&nbsp;&nbsp;</span>
          <label><input type="text" name="H_type" required="required"></label>
        </td>
      </tr>
      <tr>
        <td style="height: 50px">
          <span style="font-weight: bold">面积&nbsp;&nbsp;</span>
          <label><input type="number" name="H_area" required="required">平米</label>
        </td>
        <td>
          <span style="font-weight: bold">发布日期&nbsp;&nbsp;</span>
          <label><input type="date" name="H_time" required="required"></label>
        </td>
      </tr>
      <tr>
        <td style="height: 50px">
          <span style="font-weight: bold">装修&nbsp;&nbsp;</span>
          <label><input type="text" name="H_decorate" required="required"></label>
        </td>
        <td>
          <span style="font-weight: bold">朝向&nbsp;&nbsp;</span>
          <label>
            <select name="H_direct" data-placeholder="朝向">
              <option value="东">东</option>
              <option value="西">西</option>
              <option value="南">南</option>
              <option value="北">北</option>
              <option value="东北">东北</option>
              <option value="东南">东南</option>
              <option value="西北">西北</option>
              <option value="西南">西南</option>
            </select>
          </label>
        </td>
      </tr>
      <tr>
        <td style="height: 50px">
          <span style="font-weight: bold">梯户&nbsp;&nbsp;</span>
          <label><input type="text" name="H_rate" required="required"></label>
        </td>
        <td>
          <span style="font-weight: bold">楼层&nbsp;&nbsp;</span>
          <label><input type="number" name="H_layer" required="required"></label>
        </td>
      </tr>
      <tr>
        <td style="height: 50px">
          <span style="font-weight: bold">租金&nbsp;&nbsp;</span>
          <label><input type="number" name="R_price" required="required">元/月&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
        </td>
        <td>
          <span style="font-weight: bold">房源图片&nbsp;&nbsp;</span>
          <label><input type="file" name="H_img" value="选择图片" required="required"></label>
        </td>
      </tr>
    </table><br><br>
    <label style="margin-left:70%;background-color: transparent">
      <input style="width: 160px;height: 48px;" type="submit" value="确认发布">
    </label>
  </form>
</div><br><br><br><br><br><br><br>


<!--底部栏-->
<%@ include file="bottom.jsp"%>

<script src="https://cdn.bootcss.com/canvas-nest.js/2.0.4/canvas-nest.js" type="text/javascript" color="255,0,0" opacity="0.7" count="150"></script>




</body>
</html>

