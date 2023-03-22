<%@ page import="hm_bean.User" %>
<%@ page import="hm_bean.Second_House" %>
<%@ page import="java.util.List" %>
<%@ page import="hm_dao.House_Dao" %>
<%@ page import="hm_db.DBUtil" %>
<%@ page import="hm_bean.Renting" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="hm_bean.House" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %><%--
  Created by IntelliJ IDEA.
  User: 胡楠
  Date: 2022/6/28
  Time: 9:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
  <head>
      <title>我的发布</title>

  </head>
  <body>
  <div style="width: 100%;height: 100%;">
      <!--加载头部导航栏-->
      <%@ include file="head.jsp"%><br><br><br>
      <span style="font-size: xxx-large;font-weight: bold;margin-left: 45%">我发布的房源</span>
      <br><br><br><br>


      <%
          //获取我的房源数据
          House_Dao houseDao = new House_Dao(DBUtil.getConnection());
          String ShowPage=request.getParameter("ShowPage");
          if(ShowPage==null) ShowPage="0";//默认显示首页
          int PageSize=30;//默认煤业30条记录
          String Type=request.getParameter("Type");
          if(Type==null) Type="二手房";//默认显示二手房
          List<Second_House> secondHouseList = null;
          List<Renting> rentingList=null;
          int houseNum=0,PageCount=0;
          try{
              if(Type.equals("二手房")){
                  secondHouseList=houseDao.FindSecondById(user.U_id,Integer.parseInt(ShowPage)*PageSize+1,PageSize);
                  houseNum= houseDao.FindSecondNumById(user.U_id);
              }
              else if(Type.equals("租房")){
                  rentingList=houseDao.FindRentById(user.U_id,Integer.parseInt(ShowPage)*PageSize+1,PageSize);
                  houseNum= houseDao.FindRentNumById(user.U_id);
              }
              PageCount=houseNum % PageSize == 0 ? houseNum / PageSize : houseNum / PageSize + 1;//页面个数
          }
          catch(SQLException e){
              e.printStackTrace();
          }
      %>

      <div style="width: 80%;margin-left: 20%">
          <div style="display: inline-block;vertical-align:top;">
              <input style="width: 100px;height: 50px" type="button" value="我的二手房" onclick="window.location.href='MyPost.jsp?Type=二手房';"><br><br>
              <input style="width: 100px;height: 50px" type="button" value="我的租房" onclick="window.location.href='MyPost.jsp?Type=租房';">
          </div>
          <div style="display: inline-block;">
              <%
                  if(Type.equals("二手房")){
              %>
              <div style="width: 100%;display: inline-block">
                  <%
                      if (secondHouseList.size() == 0) {
                  %>
                  <img src="暂无数据.png" alt="图片加载失败" style="width: 100%;height: 300px">
                  <%
                  }
                  else{
                      for(int i=0;i<PageSize;i++){
                          if(i<secondHouseList.size()){
                  %>
                  <div style="background-color: #ffffff;">
                      <img src="ShowImg.jsp?H_id=<%=secondHouseList.get(i).id%>" alt="图片加载失败" style="vertical-align: middle;width: 200px;height: 100px;">
                      &nbsp;&nbsp;&nbsp;&nbsp;
                      <span style="display: inline-block;">
                                          <a style="font-weight:bold;color: black" href="SecHouseDetail.jsp?H_id=<%=secondHouseList.get(i).id%>">点击查看详情</a><br>
                                          <%=secondHouseList.get(i).area%>平方米/<%=secondHouseList.get(i).direct%>/<%=secondHouseList.get(i).type%>
                                                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                          <span style="color: orange;font-size: x-large;vertical-align: bottom" ><%=secondHouseList.get(i).price%>元/平</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                          <input type="button" onclick="EditHouse('<%=secondHouseList.get(i).id%>')" value="编辑">
                                          <input type="button" onclick="DeleteHouse('<%=secondHouseList.get(i).id%>')" value="删除">
                                          </span><hr>
                  </div>

                  <!--隐藏的编辑框-->
                  <div id="editor<%=secondHouseList.get(i).id%>" style="display: none;position: absolute;z-index: 10">
                      <jsp:include page="EditSecond.jsp">
                          <jsp:param name="H_id" value="<%=secondHouseList.get(i).id%>"/>
                      </jsp:include>
                  </div>

                  <%
                              }
                          }
                      }
                  %>
              </div>
              <%
              }
              else if(Type.equals("租房")){
              %>
              <div style="width: 100%;display: inline-block">
                  <%
                      if (rentingList.size() == 0) {
                  %>
                  <img src="暂无数据.png" alt="图片加载失败" style="width: 100%;height: 300px">
                  <%
                  }
                  else{
                      for(int i=0;i<PageSize;i++){
                          /*int index=PageSize*Integer.parseInt(ShowPage)+i;*/
                          if(i<rentingList.size()){
                  %>
                  <div style="background-color: #ffffff;">
                      <img src="ShowImg.jsp?H_id=<%=rentingList.get(i).id%>" alt="图片加载失败" style="vertical-align: middle;width: 200px;height: 100px;">
                      &nbsp;&nbsp;&nbsp;&nbsp;
                      <span style="display: inline-block;">
                              <a style="font-weight:bold;color: black" href="RentDetail.jsp?H_id=<%=rentingList.get(i).id%>">点击查看详情</a><br>
                              <%=rentingList.get(i).area%>平方米/<%=rentingList.get(i).direct%>/<%=rentingList.get(i).type%>
                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                              <span style="color: orange;font-size: x-large;vertical-align: bottom" ><%=rentingList.get(i).price%>元/月</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                               <input type="button" onclick="EditHouse('<%=rentingList.get(i).id%>')" value="编辑">
                               <input type="button" onclick="DeleteHouse('<%=rentingList.get(i).id%>')" value="删除">
                              </span><hr>
                  </div>

                  <!--隐藏的编辑框-->
                  <div id="editor<%=rentingList.get(i).id%>" style="display: none;position: absolute;z-index: 10">
                      <jsp:include page="EditRent.jsp">
                          <jsp:param name="H_id" value="<%=rentingList.get(i).id%>"/>
                      </jsp:include>
                  </div>
                  <%
                              }
                          }
                      }
                  %>
              </div>

              <%
                  }
              %>

              <div style="margin-right: 0;text-align: right">
                  <!--跳转栏-->
                  <br>
                  第<%=Integer.parseInt(ShowPage)+1%>页（共<%=PageCount%>页）
                  <br>
                  <a style="color: black" href="MyPost.jsp?ShowPage=0&Type=<%=Type%>">首页</a>
                  <%
                      if(Integer.parseInt(ShowPage)>0){
                  %>
                  <a style="color: black" href="MyPost.jsp?ShowPage=<%=Integer.parseInt(ShowPage)-1%>&Type=<%=Type%>">上一页</a>
                  <%
                      }
                  %>
                  <%   //根据PageCount的值显示上下2页的数字并附加上相应的超链接
                      for(int i=Integer.parseInt(ShowPage)-2;i<=Integer.parseInt(ShowPage)+2;i++){
                          if(i>=0&&i<PageCount){
                  %>
                  <a style="color: black" href="MyPost.jsp?ShowPage=<%=i%>&Type=<%=Type%>"><%=i+1%></a>
                  <%
                          }
                      }
                  %>
                  <%
                      if(Integer.parseInt(ShowPage)<PageCount-1){
                  %>
                  <a style="color: black" href="MyPost.jsp?ShowPage=<%=Integer.parseInt(ShowPage)+1%>&Type=<%=Type%>">下一页</a>
                  <%
                      }
                  %>
                  <a style="color: black" href="MyPost.jsp?ShowPage=<%=PageCount-1%>&Type=<%=Type%>">末页</a>
              </div>

              <script>
                  //删除房源
                  function DeleteHouse(H_id) {
                      const result = confirm("确认取消该房源的发布？");
                      if (result === true) {
                          window.location.href="DeleteHouse?H_id="+H_id+"&Type=<%=Type%>";
                      }
                  }

                  //编辑房源
                  function EditHouse(H_id){
                      if('<%=Type%>'==="二手房"){
                          document.getElementById("editor"+H_id).style.display="block";
                      }
                      else if('<%=Type%>'==="租房"){
                          document.getElementById("editor"+H_id).style.display="block";
                      }

                  }

                  //隐藏编辑
                  function HideEditor(H_id){
                      document.getElementById("editor"+H_id).style.display="none";
                  }


              </script>


          </div>
      </div><br><br><br><br><br><br><br><br><br>


      <!--底部栏-->
      <%@ include file="bottom.jsp"%>

      <script src="https://cdn.bootcss.com/canvas-nest.js/2.0.4/canvas-nest.js" type="text/javascript" color="255,0,0" opacity="0.7" count="150"></script>

  </div>
  </body>
</html>
