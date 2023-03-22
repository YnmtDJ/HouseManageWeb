<%@ page import="org.locationtech.jts.planargraph.algorithm.ConnectedSubgraphFinder" %>
<%@ page import="hm_db.DBUtil" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="hm_dao.Admin_Area_Dao" %>
<%@ page import="hm_bean.Admin_Area" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="org.locationtech.jts.geom.Polygon" %>
<%@ page import="org.locationtech.jts.io.WKTReader" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.locationtech.jts.io.ParseException" %>
<%@ page import="org.locationtech.jts.geom.Point" %>
<%@ page import="org.locationtech.jts.geom.MultiPolygon" %>
<%@ page import="hm_dao.House_Dao" %>
<%@ page import="hm_bean.Second_House" %>
<%@ page import="hm_bean.Community" %>
<%@ page import="hm_dao.Community_Dao" %>
<%@ page import="java.util.Objects" %><%--
  Created by IntelliJ IDEA.
  User: 胡楠
  Date: 2022/6/20
  Time: 22:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>地图找房</title>
    <style type="text/css">
        html{height:100%}
        body{height:100%;margin:0px;padding:0px}
        #container{height:100%}
    </style>
    <script type="text/javascript" src="https://api.map.baidu.com/api?v=3.0&ak=8CHdphqkGQTAwSxFOE04NYftqrIvU4kz">
        //v3.0版本的引用方式：src="https://api.map.baidu.com/api?v=3.0&ak=您的密钥"
    </script>
    <script src="js/http_cdn.bootcdn.net_ajax_libs_jquery_1.8.0_jquery-1.8.0.js"></script>
</head>

<body>
<div id="container"></div>
<%
    //读取数据
    Connection connection= DBUtil.getConnection();
    Admin_Area_Dao adminAreaDao=new Admin_Area_Dao(connection);
    List<Admin_Area> adminAreas;
    List<Polygon> polygons=new ArrayList<>();//行政区边界
    List<Double> prices=new ArrayList<>();//平均价格
    List<Integer> nums=new ArrayList<>();//房源套数
    double center_x=0,center_y=0;//初始武汉中心坐标
    try {
        WKTReader reader=new WKTReader();
        adminAreas = adminAreaDao.FindAllAdmin();
        for (Admin_Area adminArea : adminAreas) {//获取行政区边界，平均价格，房源套数
            polygons.add((Polygon) reader.read(adminArea.loc.STAsText()));
            prices.add(adminAreaDao.FindSecondAVGPriceByAdmin(adminArea));
            nums.add(adminAreaDao.FindSecondNumByAdmin(adminArea));
        }
        //初始化地图中心
        for (Polygon polygon : polygons) {//地图初始中心
            center_x += polygon.getCentroid().getX();
            center_y += polygon.getCentroid().getY();
        }
        center_x /= adminAreas.size();
        center_y /= adminAreas.size();


    } catch (SQLException|ParseException e) {
        throw new RuntimeException(e);
    }
%>
<script type="text/javascript">
    //初始化地图
    var map = new BMap.Map("container")// 创建地图实例
    var center =new BMap.Point(<%=center_x%>,<%=center_y%>);// 创建点坐标
    map.centerAndZoom(center, 11);// 初始化地图，设置中心点坐标和地图级别


    // 定义自定义覆盖物的构造函数
    function MyOverlay(center,text,num,price,MouseOver,MouseOut,OnClick){
        debugger;
        this._center = center;
        this._text = text;
        this._num = num;
        this._price=price;
        this._MouseOver=MouseOver;
        this._MouseOut=MouseOut;
        this._OnClick=OnClick;
        this._length=100;
        this._color="cornflowerblue";
    }
    // 继承API的BMap.Overlay
    MyOverlay.prototype = new BMap.Overlay();
    // 实现初始化方法
    MyOverlay.prototype.initialize = function(map){
        // 保存map对象实例
        this._map = map;
        // 创建div元素，作为自定义覆盖物的容器
        var div = document.createElement("div");
        div.style.position = "absolute";
        // 可以根据参数设置元素外观
        div.style.width = this._length + "px";
        div.style.height = this._length + "px";
        div.style.background = this._color;
        div.style.opacity="0.6";
        div.style.borderRadius = "50%";
        div.style.textAlign="center";
        div.style.color="white";
        div.innerText="\n"+this._text+"\n"+this._price+"\n"+this._num;
        //  给div添加事件监听
        div.onmouseover= this._MouseOver;
        div.onmouseout=this._MouseOut;
        div.onclick=this._OnClick;
        // 将div添加到覆盖物容器中
        map.getPanes().markerPane.appendChild(div);
        // 保存div实例
        this._div = div;
        // 需要将div元素作为方法的返回值，当调用该覆盖物的show、
        // hide方法，或者对覆盖物进行移除时，API都将操作此元素。
        return div;
    }
    // 实现绘制方法
    MyOverlay.prototype.draw = function(){
        // 根据地理坐标转换为像素坐标，并设置给容器
        var position = this._map.pointToOverlayPixel(this._center);
        this._div.style.left = position.x - this._length / 2 + "px";
        this._div.style.top = position.y - this._length / 2 + "px";
    }


    //为每个行政区添加覆盖物
    var AddAdminOverlays = function () {
        var points=[]
        <%
           for(int i=0;i<adminAreas.size();i++){
        %>
                //创建覆盖物
                points.push(new BMap.Point(<%=polygons.get(i).getCentroid().getX()%>, <%=polygons.get(i).getCentroid().getY()%>));
                var MouseOverListener = function () {//显示行政区范围
                    <%
                        for(int k=0;k<polygons.get(i).getNumGeometries();k++){//一个区可能由多个多边形构成
                    %>
                    var polygon = [];
                    <%
                       for(int j=0;j<polygons.get(i).getGeometryN(k).getNumPoints();j++){
                    %>
                    var pnt = new BMap.Point(<%=polygons.get(i).getGeometryN(k).getCoordinates()[j].getX()%>,
                        <%=polygons.get(i).getGeometryN(k).getCoordinates()[j].getY()%>);
                    polygon.push(pnt);
                    <%
                       }
                    %>
                    var Polygon = new BMap.Polygon(polygon);
                    map.addOverlay(Polygon);
                    <%
                        }
                    %>
                }
                var MouseOut = function () {//行政区显示移除
                    map.clearOverlays();
                    AddAdminOverlays();
                }
                var OnClick = function () {//点击查看此行政区下的小区
                    map.setZoom(17);
                    map.setCenter(points.at(<%=i%>));
                }
                var myOverlay = new MyOverlay(points.at(<%=i%>), "<%=adminAreas.get(i).name%>", "<%=nums.get(i)%>套", "<%=prices.get(i)%>元/平",
                    MouseOverListener, MouseOut, OnClick);
                //添加覆盖物
                map.addOverlay(myOverlay);
        <%
             }
        %>
    }

    //为小区添加覆盖物，以当前中心坐标1000米范围内
    var AddCommunityOverlays = function () {
        //首先以当前中心坐标为参数发送请求
        var center_x=map.getCenter().lng;
        var center_y=map.getCenter().lat;

        debugger;

        $.ajax({
            url: "SecShowOnMap",	//上传URL
            type: "GET", //请求方式
            data: {
                "center_x": center_x,
                "center_y": center_y
            },
            dataType: "JSON", //设置接受到的响应数据的格式
            success: function (data) {	//请求成功
                //data由若干小区项组成, 每个小区包含"C_id","C_name","C_center","Price","Num","C_loc"
                //其中"C_center"包含两项"x","y"  "C_loc"是一个点数组, 每项包含"x","y"
                for(var i=0;i<data.length;i++){//循环遍历每个小区
                    //使用闭包保存每次循环的i值
                    (function (i) {
                        var community=data[i];
                        var polygon;//小区边界多边形
                        var MouseOverListener = function () { //显示小区边界
                            var points=[];
                            for(var j=0;j<community.C_loc.length;j++){ //遍历点坐标
                                var x=community.C_loc[j].x;
                                var y=community.C_loc[j].y;
                                points.push(new BMap.Point(x,y));
                            }
                            polygon = new BMap.Polygon(points);
                            map.addOverlay(polygon);
                        }
                        var MouseOutListener=function () {  //隐藏小区边界
                            map.removeOverlay(polygon);
                        }
                        var OnClick =function () {  //点击小区进入详情
                            window.location.href="CommunityDetail.jsp?C_id="+community.C_id;
                        }
                        var center=new BMap.Point(community.C_center.x,community.C_center.y);
                        var myOverlay = new MyOverlay(center,community.C_name,community.Num+"套",community.Price+"元/平",
                            MouseOverListener, MouseOutListener,OnClick);
                        map.addOverlay(myOverlay);//添加小区覆盖物
                    })(i);
                }
            },
            error: function () {//表示如果请求响应出现错误，会执行的回调函数
                alert("异步请求失败！");
            },
        });
    }


    //为地图添加移动，缩放事件
    map.addEventListener("dragend", function(){
        if(this.getZoom()>14){
            map.clearOverlays();
            AddCommunityOverlays();
        }
    });
    map.addEventListener("zoomend", function(){
        map.clearOverlays();
        if(this.getZoom()<=14) AddAdminOverlays();
        else AddCommunityOverlays();
    });


    //初始时加载行政区覆盖物
    AddAdminOverlays();


</script>
</body>
</html>

