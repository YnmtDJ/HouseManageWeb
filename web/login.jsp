<%--
  Created by IntelliJ IDEA.
  User: 胡楠
  Date: 2022/6/17
  Time: 19:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
  <head>
    <title>登录</title>
  </head>
  <body>

    <form style="margin-top: 15%;width: 40%;margin-left:30%;padding: 45px;border: 2px solid darkgrey; border-radius:5px;"
          method="get" action="login">
      <table>
        <tr>
          <td style="width: 100px"><span style="font-size: x-large;vertical-align: middle">用户号</span></td>
          <td style="width: 500px">
            <label><input maxlength="9" style="width: 200px;height: 30px" type="text" name="U_id" id="label_id"/></label>
            <span style="color:lightgrey;vertical-align: middle">（9位数字）</span>
          </td>
        </tr>
        <tr>
          <td> <span style="font-size: x-large;vertical-align: middle">密码</span></td>
          <td>
            <label><input maxlength="16" style="width: 200px;height: 30px" type="password" name="U_password" id="label_psd"/></label>
            <span style="color:lightgrey;vertical-align: middle">（16位以内的英文字母、数字）</span>
          </td>
        </tr>
      </table><br><br>
      <div style="margin-left: 100px">
        <button type="submit" style="width: 100px;height: 30px">登录</button>
        <input type="button" style="width: 100px;height: 30px" onclick="window.location.href='register.jsp';" value="注册">
      </div>
    </form><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
    <%
      String result=(String)request.getAttribute("result");//登录结果
      if(result!=null&&result.equals("登录失败")){
    %>
    <script type="text/javascript">
      alert("登录失败，请检查用户名或密码！");
    </script>
    <%
      }
    %>

    <!--底部栏-->
    <%@ include file="bottom.jsp"%>

    <script src="https://cdn.bootcss.com/canvas-nest.js/2.0.4/canvas-nest.js" type="text/javascript" color="255,0,0" opacity="0.7" count="150"></script>

  </body>
</html>
