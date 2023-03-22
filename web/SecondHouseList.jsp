<%@ page import="java.sql.Connection" %>
<%@ page import="hm_db.DBUtil" %>
<%@ page import="hm_dao.House_Dao" %>
<%@ page import="hm_bean.Second_House" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.List" %>
<%@ page import="hm_bean.Admin_Area" %>
<%@ page import="hm_dao.Admin_Area_Dao" %><%--
  Created by IntelliJ IDEA.
  User: 胡楠
  Date: 2022/6/19
  Time: 9:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!--分页列表显示-->
<%
    //获取连接
    Connection connection= DBUtil.getConnection();
    House_Dao houseDao=new House_Dao(connection);
    Admin_Area_Dao adminAreaDao=new Admin_Area_Dao(connection);
    List<Second_House> houseList;
    //获取条件参数
    String name=request.getParameter("AdminSelect");
    if("null".equals(name)) name=null;
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
    //查询
    try {
        Admin_Area adminArea=adminAreaDao.FindAdminByName(name);
        houseList = houseDao.FindSecond_House(adminArea,null,MinPrice,MaxPrice,H_type,MinArea,MaxArea);
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
    //分页显示，每页30条记录
    int PageSize=30;
    int PageCount=houseList.size()%PageSize==0?houseList.size()/PageSize:houseList.size()/PageSize+1;
    for(int i=0;i<PageSize;i++){
        //InputStream转成String
        /*byte[] bytes = new byte[house.img.available()];
        int len=house.img.read(bytes);
        String img = new String(bytes);*/
        int index=PageSize*ShowPage+i;
        if(index<houseList.size()){
%>
    <table>
        <tr>
            <td>
                <img src="source/毛胚房.jpg" alt="图片加载失败">
                <label>
                    <%=houseList.get(index).area%>平方米/<%=houseList.get(index).direct%>/<%=houseList.get(index).type%>
                </label>
                <span style="color: orange" >
                    <%=houseList.get(index).price%>元/平方米
                  </span>
            </td>
        </tr>
    </table>
<%      }
    }
%>

<!--跳转栏-->
<%
    String condition="";//组合条件
    if(name!=null) condition+="&AdminSelect="+name;
    if(MinPrice!=null) condition+="&MinPrice="+MinPrice;
    if(MaxPrice!=null) condition+="&MaxPrice="+MaxPrice;
    if(H_type!=null) condition+="&H_type="+H_type;
    if(MinArea!=null) condition+="&MinArea="+MinArea;
    if(MaxArea!=null) condition+="&MaxArea="+MaxArea;
%>
<br>
第<%=ShowPage+1%>页（共<%=PageCount%>页）
<br>
<a href="Second_House.jsp?ShowPage=0<%=condition%>">首页</a>
<%
    if(ShowPage>0){
%>
<a href="Second_House.jsp?ShowPage=<%=ShowPage-1%><%=condition%>">上一页</a>
<%
    }
%>
<%   //根据PageCount的值显示上下2页的数字并附加上相应的超链接
    for(int i=ShowPage-2;i<=ShowPage+2;i++){
        if(i>=0&&i<PageCount){
%>
<a href="Second_House.jsp?ShowPage=<%=i%><%=condition%>"><%=i+1%></a>
<%
        }
    }
%>
<%
    if(ShowPage<PageCount-1){
%>
<a href="Second_House.jsp?ShowPage=<%=ShowPage+1%><%=condition%>">下一页</a>
<%
    }
%>
<a href="Second_House.jsp?ShowPage=<%=PageCount-1%><%=condition%>">末页</a>

