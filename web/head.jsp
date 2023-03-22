<%@ page import="hm_bean.User" %><%--
  Created by IntelliJ IDEA.
  User: 胡楠
  Date: 2022/6/18
  Time: 15:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<link rel="stylesheet"  type="text/css"  href="css/ButtonStyle.css"/>
<table style="height:70px;width:100%;background-color:cornflowerblue;">
    <tr>
        <td style="width: 200px"></td>
        <td style="alignment: center" >
            <label>
                <input type="button" class="ButtonStyle" style="font-size: x-large" onclick="window.location.href='home_page.jsp';"
                       value="首页" onmouseover="this.style.color='white'" onmouseout="this.style.color='lightgray'">
            </label>
        </td>
        <td style="alignment: center">
            <label>
                <input type="button" class="ButtonStyle" style="font-size: x-large" onclick="window.location.href='Second_House.jsp';"
                       value="二手房" onmouseover="this.style.color='white'" onmouseout="this.style.color='lightgray'">
            </label>
        </td>
        <td style="alignment: center">
            <label>
                <input type="button" class="ButtonStyle" style="font-size: x-large" onclick="window.location.href='Renting.jsp';"
                       value="租房" onmouseover="this.style.color='white'" onmouseout="this.style.color='lightgray'">
            </label>
        </td>
        <td style="alignment: center">
            <label>
                <input type="button" class="ButtonStyle" style="font-size: x-large" onclick="window.location.href='Community.jsp';"
                       value="小区" onmouseover="this.style.color='white'" onmouseout="this.style.color='lightgray'">
            </label>
        </td>
        <%
            if(!((User)request.getSession().getAttribute("user")).U_type.equals("普通用户")){
        %>
            <td style="alignment: center">
                <label>
                    <input type="button" class="ButtonStyle" style="font-size: x-large" onclick="window.location.href='MyPost.jsp';"
                           value="我的发布" onmouseover="this.style.color='white'" onmouseout="this.style.color='lightgray'">
                </label>
            </td>
        <%
            }
        %>
        <td>
        <%
            User user=(User) session.getAttribute("user");
            if(user!=null){
        %>
            <span style="font-size: x-large;color: white">你好，<%=user.U_name%></span>
            <a href="Logout" style="font-size: small;color: white">退出</a>
        <%
            }
        %>
        </td>


    </tr>
</table>
