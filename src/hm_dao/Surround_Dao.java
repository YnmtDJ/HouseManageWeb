package hm_dao;

import com.microsoft.sqlserver.jdbc.Geography;
import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;
import com.microsoft.sqlserver.jdbc.SQLServerResultSet;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import hm_bean.Community;
import hm_bean.House;
import hm_bean.Surround;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 周边数据访问层
 */
public class Surround_Dao {
    Connection connection;//连接的数据库

    /**
     * 构造函数
     * @param connection 与之连接的数据库
     */
    public Surround_Dao(Connection connection){
        this.connection=connection;
    }

    /**
     * 查询房源周边的建筑
     * @param house 房源
     * @return 周边列表
     */
    public List<Surround> FindSurroundByHouse(House house) throws SQLException {
        String sql="select Surround.* " +
                "from Surround,House,Community " +
                "where H_id=? and House.C_id=Community.C_id " +
                "and S_loc.STDistance(C_loc)<=100;";
        SQLServerPreparedStatement preStm=(SQLServerPreparedStatement) connection.prepareStatement(sql);
        preStm.setString(1,house.id);
        SQLServerResultSet resultSet=(SQLServerResultSet) preStm.executeQuery();
        List<Surround> results=new ArrayList<>();
        while(resultSet.next()){
            String S_id=resultSet.getString("S_id");
            String S_name=resultSet.getString("S_name");
            String S_type=resultSet.getString("S_type");
            Geography S_loc=resultSet.getGeography("S_loc");
            results.add(new Surround(S_id,S_name,S_type,S_loc));
        }
        return results;
    }

    /**
     * 查询小区周边的建筑
     * @param community 小区
     * @return 周边列表
     */
    public List<Surround> FindSurroundByCommunity(Community community) throws SQLException {
        String sql="select Surround.* " +
                "from Surround,Community " +
                "where C_id=? and S_loc.STDistance(C_loc)<=100";
        SQLServerPreparedStatement preStm=(SQLServerPreparedStatement) connection.prepareStatement(sql);
        preStm.setString(1,community.id);
        SQLServerResultSet resultSet=(SQLServerResultSet) preStm.executeQuery();
        List<Surround> results=new ArrayList<>();
        while(resultSet.next()){
            String S_id=resultSet.getString("S_id");
            String S_name=resultSet.getString("S_name");
            String S_type=resultSet.getString("S_type");
            Geography S_loc=resultSet.getGeography("S_loc");
            results.add(new Surround(S_id,S_name,S_type,S_loc));
        }
        return results;
    }

    /**
     * 根据输入的周边名查询可能的周边
     * @param name 周边名
     * @return 周边列表
     */
    public List<Surround> FindSurroundByName(String name) throws SQLException {
        String sql="select * from Surround " +
                "where S_name like '%"+name+"%' " +
                "order by len(S_name) ";
        SQLServerStatement stm=(SQLServerStatement) connection.createStatement();
        SQLServerResultSet resultSet=(SQLServerResultSet) stm.executeQuery(sql);
        List<Surround> results=new ArrayList<>();
        while(resultSet.next()){
            String S_id=resultSet.getString("S_id");
            String S_name=resultSet.getString("S_name");
            String S_type=resultSet.getString("S_type");
            Geography S_loc=resultSet.getGeography("S_loc");
            results.add(new Surround(S_id,S_name,S_type,S_loc));
        }
        return results;
    }

}
