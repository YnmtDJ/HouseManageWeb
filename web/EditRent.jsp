<%@ page import="hm_dao.House_Dao" %>
<%@ page import="hm_db.DBUtil" %>
<%@ page import="hm_dao.Building_Dao" %>
<%@ page import="hm_dao.Community_Dao" %>
<%@ page import="hm_bean.Renting" %>
<%@ page import="hm_bean.Community" %>
<%@ page import="hm_bean.Building" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: 胡楠
  Date: 2022/6/29
  Time: 19:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String H_id=request.getParameter("H_id");
    House_Dao houseDao=new House_Dao(DBUtil.getConnection());
    Community_Dao communityDao=new Community_Dao(DBUtil.getConnection());
    Building_Dao buildingDao=new Building_Dao(DBUtil.getConnection());
    Renting house;
    Community community;
    List<Building> buildingList;
    Building building;
    try {
        house=(Renting) houseDao.FindHouseById(H_id);
        community=communityDao.FindCommunityById(house.C_id);
        building=buildingDao.FindBuildByH_id(H_id);
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
%>

<!--主体-->
<div style="background-color:white;width: 650px;margin-left: 25%;padding-left:50px;text-align: left;
        border: 5px solid black;border-radius: 1%">
    <form method="post" action="EditHouse?H_id=<%=house.id%>">
        <table>
            <tr>
                <td>
                    <span style="font-weight: bold"><%=community.name%>&nbsp;&nbsp;<%=building.name%></span>
                </td>
                <td>
                    <span style="font-weight: bold">户型&nbsp;&nbsp;</span>
                    <label><input type="text" name="H_type" value="<%=house.type%>" required="required"></label>
                </td>
            </tr>
            <tr>
                <td style="height: 50px">
                    <span style="font-weight: bold">面积&nbsp;&nbsp;</span>
                    <label><input type="number" name="H_area" value="<%=house.area%>" required="required">平米</label>
                </td>
                <td>
                    <span style="font-weight: bold">发布日期&nbsp;&nbsp;</span>
                    <label><input type="date" name="H_time" value="<%=house.time%>" required="required"></label>
                </td>
            </tr>
            <tr>
                <td style="height: 50px">
                    <span style="font-weight: bold">装修&nbsp;&nbsp;</span>
                    <label><input type="text" name="H_decorate" value="<%=house.decorate%>" required="required"></label>
                </td>
                <td>
                    <span style="font-weight: bold">朝向&nbsp;&nbsp;</span>
                    <label>
                        <select name="H_direct" data-placeholder="朝向">
                            <option id="东" value="东">东</option>
                            <option id="西" value="西">西</option>
                            <option id="南" value="南">南</option>
                            <option id="北" value="北">北</option>
                            <option id="东北" value="东北">东北</option>
                            <option id="东南" value="东南">东南</option>
                            <option id="西北" value="西北">西北</option>
                            <option id="西南" value="西南">西南</option>
                        </select>
                        <script>
                            document.getElementById('<%=house.direct%>').selected="selected";//原来的值
                        </script>
                    </label>
                </td>
            </tr>
            <tr>
                <td style="height: 50px">
                    <span style="font-weight: bold">梯户&nbsp;&nbsp;</span>
                    <label><input type="text" name="H_rate" value="<%=house.rate%>" required="required"></label>
                </td>
                <td>
                    <span style="font-weight: bold">楼层&nbsp;&nbsp;</span>
                    <label><input type="number" name="H_layer" value="<%=house.layer%>" required="required"></label>
                </td>
            </tr>
            <tr>
                <td style="height: 50px">
                    <span style="font-weight: bold">租金&nbsp;&nbsp;</span>
                    <label><input type="number" name="R_price" value="<%=house.price%>" required="required">元/月&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                </td>
            </tr>
        </table><br><br>
        <label style="margin-left:45%;background-color: transparent">
            <input style="width: 160px;height: 48px;" type="submit" value="确认修改">
            <input style="width: 160px;height: 48px;" type="button" value="取消" onclick="HideEditor('<%=H_id%>')">
        </label>
    </form>
</div>
