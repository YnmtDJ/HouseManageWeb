<%@ page import="java.sql.Connection" %>
<%@ page import="hm_db.DBUtil" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.io.*" %>
<%@ page import="hm_bean.House" %>
<%@ page import="hm_dao.House_Dao" %><%--
  Created by IntelliJ IDEA.
  User: 胡楠
  Date: 2022/6/18
  Time: 21:41
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String H_id=request.getParameter("H_id");
    Connection connection= DBUtil.getConnection();
    House_Dao houseDao=new House_Dao(connection);
    try {
        InputStream img=houseDao.FindImgById(H_id);
        if(img!=null){
            //以图片格式返回
            response.setContentType("image/jpg");

            //存储读取到的数据
            int read = 0;
            while ((read = img.read())!= -1){
                //将读取到的数据输出
                response.getOutputStream().write(read);
            }
            //关闭流
            response.getOutputStream().close();
            img.close();
        }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
%>
