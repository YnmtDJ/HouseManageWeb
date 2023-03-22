package hm_dao;


import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;
import hm_bean.Building;
import hm_bean.Community;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 楼栋访问层接口
 */
public class Building_Dao {
    Connection connection;//与数据库连接

    /**
     * 构造函数
     * @param connection 与数据库建立的连接
     */
    public Building_Dao(Connection connection){
        this.connection=connection;
    }

    /**
     * 插入楼栋，并将其划分到归属的小区，保证主键自增
     * @param building 楼栋
     * @param C_id 小区号
     * @return 是否插入成功
     */
    public boolean InsertBuilding(Building building,String C_id) throws SQLException {
        Statement statement=connection.createStatement();
        //首先查询最大主键
        String sql="select max(B_id) as max_id from Building";
        ResultSet result=statement.executeQuery(sql);
        String max_id;
        if(result.next()){
            max_id=result.getString("max_id");
            max_id=String.format("%09d",Integer.parseInt(max_id)+1);
            building.id=max_id;//主键自增
        }
        //然后插入
        sql="insert into Building values(?,?,?,?)";
        SQLServerPreparedStatement preState=(SQLServerPreparedStatement) connection.prepareStatement(sql);
        preState.setString(1,building.id);
        preState.setFloat(2, building.height);
        preState.setInt(3, building.num);
        preState.setString(4, building.name);
        int result1=preState.executeUpdate();
        if(result1==0) return false;
        //建立小区，楼栋联系
        sql="insert into ComBuild values(?,?)";
        preState=(SQLServerPreparedStatement) connection.prepareStatement(sql);
        preState.setString(1,C_id);
        preState.setString(2,building.id);
        result1=preState.executeUpdate();
        return result1!=0;
    }

    /**
     * 删除楼栋
     * @param building 楼栋
     * @return 是否删除成功
     * @throws SQLException
     */
    public boolean DeleteBuilding(Building building) throws SQLException {
        Statement statement=connection.createStatement();
        String sql="delete Building where B_id='"+building.id+"'";
        int result=statement.executeUpdate(sql);
        return result!=0;
    }

    /**
     * 更新楼栋信息
     * @param building 楼栋
     * @return  是否更新成功
     * @throws SQLException
     */
    public boolean UpdateBuilding(Building building) throws SQLException {
        String sql ="update Building set B_height=?,B_num=?,B_name=? where B_id=?";
        SQLServerPreparedStatement preparedStatement=(SQLServerPreparedStatement) connection.prepareStatement(sql);
        preparedStatement.setFloat(1,building.height);
        preparedStatement.setInt(2,building.num);
        preparedStatement.setString(3,building.name);
        preparedStatement.setString(4,building.id);
        int result=preparedStatement.executeUpdate();
        return result!=0;
    }

    /**
     * 查找某个小区的所有楼栋
     * @param C_id 小区号
     * @return 楼栋列表
     */
    public List<Building> FindBuildByCId(String C_id) throws SQLException {
        String sql="select Building.* " +
                "from Building,ComBuild " +
                "where Building.B_id=ComBuild.B_id and C_id=?";
        PreparedStatement prestm=connection.prepareStatement(sql);
        prestm.setString(1,C_id);
        ResultSet resultSet=prestm.executeQuery();
        List<Building> results=new ArrayList<>();
        while(resultSet.next()){
            String B_id=resultSet.getString("B_id");
            float B_height=resultSet.getFloat("B_height");
            int B_num= resultSet.getInt("B_num");
            String B_name=resultSet.getString("B_name");
            results.add(new Building(B_id,B_height,B_num,B_name));
        }
        return results;
    }

    /**
     * 根据房源号，查找所属的的楼栋
     * @param H_id 房源号
     * @return 楼栋，没有查到返回Null
     */
    public Building FindBuildByH_id(String H_id) throws SQLException {
        String sql="select Building.* " +
                "from Building,BuildHouse " +
                "where Building.B_id=BuildHouse.B_id and BuildHouse.H_id=? ";
        PreparedStatement prestm=connection.prepareStatement(sql);
        prestm.setString(1,H_id);
        ResultSet resultSet=prestm.executeQuery();
        if(resultSet.next()){
            String B_id=resultSet.getString("B_id");
            float B_height=resultSet.getFloat("B_height");
            int B_num= resultSet.getInt("B_num");
            String B_name=resultSet.getString("B_name");
            return new Building(B_id,B_height,B_num,B_name);
        }
        return null;
    }



}
