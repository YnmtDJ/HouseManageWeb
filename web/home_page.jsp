<%--
  Created by IntelliJ IDEA.
  User: 胡楠
  Date: 2022/6/18
  Time: 11:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
    <head>
        <title>主页</title>
        <link rel="stylesheet"  type="text/css"  href="css/ButtonStyle.css"/>
    </head>
    <body>
        <!--加载头部导航栏-->
        <%@ include file="head.jsp"%>

        <!--搜索栏-->
        <div class="search-tag-desc-result" style="width:50%;margin-left:25%;margin-top: 150px;text-align: left;background-color: #ffffff">
            <span style="font-size: xxx-large;font-weight: bold">开始寻找属于你的家</span><br><br><br>
            <input type="button" id="secbtn" value="找二手房" style="font-size: large;border:0;background-color: transparent;color: black"
                   onclick="SetFindType('二手房')">
            <input type="button" id="rentbtn" value="找租房" style="font-size: large;border:0;background-color: transparent;color: lightgrey"
                   onclick="SetFindType('租房')"><br>
            <form autocomplete="off" action="FindSecHouse" method="get" id="home_search">
                <label>
                    <input type="text" name="S_name" placeholder="请输入学校、医院、商场、公园或道路开始找房"
                           style="width: 80%;height: 50px">
                </label>
                <input type="submit" value="搜索" style="width: 10%;height: 50px">
            </form>
        </div>

        <script>
            //设置查询房源种类
            function SetFindType (type){
                if(type==="二手房") {
                    document.getElementById("home_search").action="FindSecHouse";
                    document.getElementById("secbtn").style.color="black";
                    document.getElementById("rentbtn").style.color="lightgrey";
                }
                else if(type==="租房"){
                    document.getElementById("home_search").action="FindRent";
                    document.getElementById("rentbtn").style.color="black";
                    document.getElementById("secbtn").style.color="lightgrey";
                }

            }
        </script>


        <br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
        <!--底部栏-->
        <%@ include file="bottom.jsp"%>

        <script src="https://cdn.bootcss.com/canvas-nest.js/2.0.4/canvas-nest.js" type="text/javascript" color="255,0,0" opacity="0.7" count="150"></script>
    </body>
</html>
