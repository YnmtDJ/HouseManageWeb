<%--
  Created by IntelliJ IDEA.
  User: 胡楠
  Date: 2022/6/17
  Time: 21:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
    <head>
        <title>注册</title>
    </head>
    <body>
        <form style="margin-top: 15%;width: 45%;margin-left:25%;padding: 45px;border: 2px solid darkgrey; border-radius:5px;"
              method="post" action="register">
            <table>
                <tr>
                    <td style="width: 130px"><span style="font-size: x-large;vertical-align: middle">用户名</span></td>
                    <td style="width: 530px">
                        <label><input maxlength="16" style="width: 200px;height: 30px" type="text" name="U_name"/></label>
                        <span style="color:lightgrey;vertical-align: middle">（16个字符以内的汉字、英文字母、数字）</span>
                    </td>
                </tr>
                <tr>
                    <td><span style="font-size: x-large;vertical-align: middle">密码</span></td>
                    <td>
                        <label><input maxlength="16" style="width: 200px;height: 30px" type="password" name="U_password"/></label>
                        <span style="color:lightgrey;vertical-align: middle">（16位以内的英文字母、数字）</span>
                    </td>
                </tr>
                <tr>
                    <td><span style="font-size: x-large;vertical-align: middle">注册类别</span></td>
                    <td>
                        <label>
                            <select name="U_type" style="width: 200px;height: 30px">
                                <option value="普通用户">普通用户</option>
                                <option value="企业用户">企业用户</option>
                            </select>
                        </label>
                    </td>
                </tr>
            </table><br><br>
            <div style="margin-left: 230px">
            <button type="submit" style="width: 100px;height: 30px">注册</button>
            </div>
        </form><br><br><br><br><br><br><br><br><br><br><br><br><br>
        <%
            String result=(String)request.getAttribute("result");//注册结果
            String id=(String)request.getAttribute("U_id");//用户号
            if(result!=null&&result.equals("注册成功")){
        %>
            <script type="text/javascript">
                    const result = "<%=result%>";
                    const id = "<%=id%>";
                    confirm(result+"! 请牢记您的用户号:"+id);
            </script>

        <%  }
            else if(result!=null&&result.equals("注册失败")){
        %>
            <script type="text/javascript">
                alert("注册失败，请联系管理员！");
            </script>
        <%
            }
        %>

        <!--底部栏-->
        <%@ include file="bottom.jsp"%>

        <script src="https://cdn.bootcss.com/canvas-nest.js/2.0.4/canvas-nest.js" type="text/javascript" color="255,0,0" opacity="0.7" count="150"></script>
    </body>
</html>
