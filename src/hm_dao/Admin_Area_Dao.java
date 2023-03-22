package hm_dao;

import com.microsoft.sqlserver.jdbc.Geography;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;
import com.microsoft.sqlserver.jdbc.SQLServerResultSet;
import hm_bean.Admin_Area;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 行政区数据访问层
 */
public class Admin_Area_Dao {
    Connection connection;

    /**
     * 构造函数
     * @param connection 与数据库建立好的连接
     */
    public Admin_Area_Dao(Connection connection){
        this.connection=connection;
    }

    /**
     * 根据区名，查找行政区
     * @param name 区名
     * @return 行政区，如果没有找到返回null
     * @throws SQLException
     */
    public Admin_Area FindAdminByName(String name) throws SQLException {
        if(name==null) return null;
        String sql="select * from Admin_Area where name=?";
        SQLServerPreparedStatement prestm=(SQLServerPreparedStatement) connection.prepareStatement(sql);
        prestm.setString(1,name);
        SQLServerResultSet resultSet=(SQLServerResultSet) prestm.executeQuery();
        Admin_Area adminArea=null;
        if(resultSet.next()){
            String id=resultSet.getString("id");
            Geography loc=resultSet.getGeography("loc");
            adminArea=new Admin_Area(id,name,loc);
        }
        return adminArea;
    }

    /**
     * 查找所有行政区名称
     * @return 所有行政区名
     */
    public List<String> FindAllAdminName() throws SQLException {
        String sql="select name from Admin_Area ";
        PreparedStatement statement=connection.prepareStatement(sql);
        ResultSet resultSet=statement.executeQuery();
        List<String> names=new ArrayList<>();
        while(resultSet.next()){
            String name=resultSet.getString("name");
            names.add(name);
        }
        return names;
    }

    /**
     * 根据小区号，查询所在行政区
     * @param C_id 小区号
     * @return 行政区，没有查到返回null
     */
    public Admin_Area FindAdminByCId(String C_id) throws SQLException {
        String sql="select * from Admin_Area where id=" +
                "(select id from AdminCom where C_id=?)";
        SQLServerPreparedStatement prestm=(SQLServerPreparedStatement) connection.prepareStatement(sql);
        prestm.setString(1,C_id);
        SQLServerResultSet resultSet=(SQLServerResultSet) prestm.executeQuery();
        if(resultSet.next()){
            String id=resultSet.getString("id");
            String name=resultSet.getString("name");
            Geography loc=resultSet.getGeography("loc");
            return new Admin_Area(id,name,loc);
        }
        else{//根据最近距离查询
            sql="select Admin_Area.* " +
                    "from Community,Admin_Area " +
                    "where C_id=? " +
                    "order by C_loc.MakeValid().STDistance(loc.MakeValid()) ";
            prestm=(SQLServerPreparedStatement) connection.prepareStatement(sql);
            prestm.setString(1,C_id);
            resultSet=(SQLServerResultSet) prestm.executeQuery();
            if(resultSet.next()){
                String id=resultSet.getString("id");
                String name=resultSet.getString("name");
                Geography loc=resultSet.getGeography("loc");
                return new Admin_Area(id,name,loc);
            }
        }
        return null;
    }


    /**
     * 按行政区查询二手房平均价格
     * @param adminArea 行政区
     * @return 平均价格，查询失败返回-1
     */
    public double FindSecondAVGPriceByAdmin(Admin_Area adminArea) throws SQLException {
        CallableStatement proc=connection.prepareCall("{call FindSecondAVGPriceNumByid(?)}");
        proc.setString(1, adminArea.id);
        ResultSet resultSet=proc.executeQuery();
        if(resultSet.next()){
            return resultSet.getDouble(1);
        }
        return -1;
    }


    /**
     * 按行政区查询二手房房源套数
     * @param adminArea 行政区
     * @return 房源套数，查询失败返回-1
     */
    public int FindSecondNumByAdmin(Admin_Area adminArea) throws SQLException {
        CallableStatement proc=connection.prepareCall("{call FindSecondAVGPriceNumByid(?)}");
        proc.setString(1, adminArea.id);
        ResultSet resultSet=proc.executeQuery();
        if(resultSet.next()){
            return resultSet.getInt(2);
        }
        return -1;
    }



    /**
     * 按行政区查询租房平均价格
     * @param adminArea 行政区
     * @return 平均价格，查询失败返回-1
     */
    public double FindRentAVGPriceByAdmin(Admin_Area adminArea) throws SQLException {
        CallableStatement proc=connection.prepareCall("{call FindRentAVGPriceNumByid(?)}");
        proc.setString(1, adminArea.id);
        ResultSet resultSet=proc.executeQuery();
        if(resultSet.next()){
            return resultSet.getDouble(1);
        }
        return -1;
    }


    /**
     * 按行政区查询租房房源套数
     * @param adminArea 行政区
     * @return 房源套数，查询失败返回-1
     */
    public int FindRentNumByAdmin(Admin_Area adminArea) throws SQLException {
        CallableStatement proc=connection.prepareCall("{call FindRentAVGPriceNumByid(?)}");
        proc.setString(1, adminArea.id);
        ResultSet resultSet=proc.executeQuery();
        if(resultSet.next()){
            return resultSet.getInt(2);
        }
        return -1;
    }


    /**
     * 查找所有行政区
     * @return 行政区列表
     */
    public List<Admin_Area> FindAllAdmin() throws SQLException {
        String sql="select * from Admin_Area ";
        Statement stm=connection.createStatement();
        SQLServerResultSet resultSet=(SQLServerResultSet) stm.executeQuery(sql);
        List<Admin_Area> results=new ArrayList<>();
        while(resultSet.next()){
            String id=resultSet.getString("id");
            String name=resultSet.getString("name");
            Geography loc=resultSet.getGeography("loc");
            results.add(new Admin_Area(id,name,loc));
        }
        return results;
    }

    /**
     * 根据行政区号查询行政区
     * @param id 行政区号
     * @return 行政区没有查到返回null
     */
    public Admin_Area FindAdminById(String id) throws SQLException {
        String sql="select * from Admin_Area where id=?";
        SQLServerPreparedStatement prestm=(SQLServerPreparedStatement) connection.prepareStatement(sql);
        prestm.setString(1,id);
        SQLServerResultSet resultSet=(SQLServerResultSet) prestm.executeQuery();
        if(resultSet.next()){
            String name=resultSet.getString("name");
            Geography loc=resultSet.getGeography("loc");
            return new Admin_Area(id,name,loc);
        }
        return null;

    }


}
