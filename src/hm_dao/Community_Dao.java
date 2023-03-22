package hm_dao;

import com.microsoft.sqlserver.jdbc.*;
import hm_bean.Community;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 小区数据库访问层接口
 */
public class Community_Dao {
    Connection connection;//数据库连接

    /**
     * 构造函数
     * @param connection 与数据库建立好的连接
     */
    public Community_Dao(Connection connection)  {
       this.connection=connection;
    }

    /**
     * 插入小区，并将其划分到所属的行政区，保证主键自增
     * @param community 小区
     * @return 是否插入成功
     */
    public boolean InsertCommunity(Community community) throws SQLException {
        Statement statement=connection.createStatement();
        //首先查询最大主键
        String sql="select max(C_id) as max_id from Community";
        ResultSet result=statement.executeQuery(sql);
        String max_id;
        if(result.next()){
            max_id=result.getString("max_id");
            max_id=String.format("%09d",Integer.parseInt(max_id)+1);
            community.id=max_id;//主键自增
        }
        //然后插入
        sql="insert into Community values(?,?,?,?,?,?,?,?)";
        SQLServerPreparedStatement preState=(SQLServerPreparedStatement) connection.prepareStatement(sql);
        preState.setString(1,community.id);
        preState.setString(2, community.name);
        preState.setGeography(3, community.loc);
        preState.setFloat(4,community.rate);
        preState.setFloat(5,community.green_rate);
        preState.setString(6,community.pro_name);
        preState.setString(7, community.type);
        preState.setDate(8,community.time);
        int result1=preState.executeUpdate();
        return result1!=0;

    }

    /**
     * 删除小区
     * @param community
     * @return 是否删除成功
     * @throws SQLException
     */
    public boolean DeleteCommunity(Community community) throws SQLException {
        Statement statement=connection.createStatement();
        String sql="delete Community where C_id='"+community.id+"'";
        int result=statement.executeUpdate(sql);
        return result!=0;
    }

    /**
     * 更新小区信息
     * @param community
     * @return  是否更新成功
     * @throws SQLException
     */
    public boolean UpdateCommunity(Community community) throws SQLException {
        String sql ="update Community set C_name=?,C_loc=?,C_rate=?,C_green_rate=?,C_pro_name=?,C_type=?,C_time=?" +
                " where C_id=?";
        SQLServerPreparedStatement preparedStatement=(SQLServerPreparedStatement) connection.prepareStatement(sql);
        preparedStatement.setString(1,community.name);
        preparedStatement.setGeography(2,community.loc);
        preparedStatement.setFloat(3,community.rate);
        preparedStatement.setFloat(4,community.green_rate);
        preparedStatement.setString(5,community.pro_name);
        preparedStatement.setString(6,community.type);
        preparedStatement.setDate(7,community.time);
        preparedStatement.setString(8,community.id);
        int result=preparedStatement.executeUpdate();
        return result!=0;
    }

    /**
     * 根据小区号获取小区
     * @param C_id 小区号
     * @return 小区，若没有返回null
     */
    public Community FindCommunityById(String C_id) throws SQLException {
        String sql="select * from Community where C_id=?";
        SQLServerPreparedStatement prestm=(SQLServerPreparedStatement) connection.prepareStatement(sql);
        prestm.setString(1,C_id);
        SQLServerResultSet resultSet= (SQLServerResultSet) prestm.executeQuery();
        if(resultSet.next()){
            String C_name= resultSet.getString("C_name");
            Geography C_loc=resultSet.getGeography("C_loc");
            float C_rate=resultSet.getFloat("C_rate");
            float C_green_rate=resultSet.getFloat("C_green_rate");
            String C_pro_name=resultSet.getString("C_pro_name");
            String C_type=resultSet.getString("C_type");
            Date C_time=resultSet.getDate("C_time");
            return new Community(C_id,C_name,C_loc,C_rate,C_green_rate,C_pro_name,C_type,C_time);
        }
        return null;
    }


    /**
     * 某小区当月发布租房均价查询
     * @param C_id 小区号
     * @return 租金均价，若查询失败返回-1
     */
    public double FindRentAVGPriceByCommunity(String C_id) throws SQLException {
        CallableStatement proc=connection.prepareCall("{call FindRentAVGPriceByC_id(?)}");
        proc.setString(1,C_id);
        ResultSet resultSet=proc.executeQuery();
        if(resultSet.next()){
            return resultSet.getFloat(1);
        }
        return -1;
    }


    /**
     * 某小区当月发布二手房均价查询
     * @param C_id 小区号
     * @return 房价均价，若查询失败返回-1
     */
    public double FindSecondAVGPriceByCommunity(String C_id) throws SQLException {
        CallableStatement proc=connection.prepareCall("{call FindSecondAVGPriceByC_id(?)}");
        proc.setString(1,C_id);
        ResultSet resultSet=proc.executeQuery();
        if(resultSet.next()){
            return resultSet.getFloat(1);
        }
        return -1;
    }

    /**
     * 查询小区的二手房套数
     * @param C_id 小区号
     * @return 房源套数，查询失败返回-1
     */
    public int FindSecondNumByC_id(String C_id) throws SQLException {
        String sql="select count(*) " +
                "from House,Second_House " +
                "where House.H_id=Second_House.H_id and House.C_id=?";
        PreparedStatement prestm=connection.prepareStatement(sql);
        prestm.setString(1,C_id);
        ResultSet resultSet=prestm.executeQuery();
        if(resultSet.next()){
            return resultSet.getInt(1);
        }
        return -1;
    }

    /**
     * 查询小区的租房套数
     * @param C_id 小区号
     * @return 房源套数，查询失败返回-1
     */
    public int FindRentingNumByC_id(String C_id) throws SQLException {
        String sql="select count(*) " +
                "from House,Renting " +
                "where House.H_id=Renting.H_id and House.C_id=?";
        PreparedStatement prestm=connection.prepareStatement(sql);
        prestm.setString(1,C_id);
        ResultSet resultSet=prestm.executeQuery();
        if(resultSet.next()){
            return resultSet.getInt(1);
        }
        return -1;
    }


    /**
     * 根据小区名做模糊查找
     * @param name 小区名
     * @return 小区列表
     */
    public List<Community> FindCommunityByName(String name) throws SQLException {
        String sql="select * from Community " +
                "where C_name like '%"+name+"%' " +
                "order by len(C_name) ";
        SQLServerPreparedStatement prestm=(SQLServerPreparedStatement) connection.prepareStatement(sql);
        SQLServerResultSet resultSet=(SQLServerResultSet) prestm.executeQuery();
        List<Community> results=new ArrayList<>();
        while(resultSet.next()){
            String C_id= resultSet.getString("C_id");
            String C_name=resultSet.getString("C_name");
            Geography C_loc=resultSet.getGeography("C_loc");
            float C_rate=resultSet.getFloat("C_rate");
            float C_green_rate=resultSet.getFloat("C_green_rate");
            String C_pro_name=resultSet.getString("C_pro_name");
            String C_type=resultSet.getString("C_type");
            Date C_time=resultSet.getDate("C_time");
            results.add(new Community(C_id,C_name,C_loc,C_rate,C_green_rate,C_pro_name,C_type,C_time));
        }
        return results;
    }

    /**
     * 获取所有小区
     * @return 小区列表
     */
    public List<Community> FindAllCommunity() throws SQLException {
        String sql="select * from Community";
        SQLServerPreparedStatement prestm=(SQLServerPreparedStatement) connection.prepareStatement(sql);
        SQLServerResultSet resultSet= (SQLServerResultSet) prestm.executeQuery();
        List<Community> results=new ArrayList<>();
        while(resultSet.next()){
            String C_id= resultSet.getString("C_id");
            String C_name=resultSet.getString("C_name");
            Geography C_loc=resultSet.getGeography("C_loc");
            float C_rate=resultSet.getFloat("C_rate");
            float C_green_rate=resultSet.getFloat("C_green_rate");
            String C_pro_name=resultSet.getString("C_pro_name");
            String C_type=resultSet.getString("C_type");
            Date C_time=resultSet.getDate("C_time");
            results.add(new Community(C_id,C_name,C_loc,C_rate,C_green_rate,C_pro_name,C_type,C_time));
        }
        return results;
    }


    /**
     * 查询某个行政区的小区
     * @param id 行政区号
     * @return 小区列表
     */
    public List<Community> FindCommunityByAdmin(String id) throws SQLException {
        String sql="select Community.* " +
                "from AdminCom,Community " +
                "where AdminCom.id=? and AdminCom.C_id=Community.C_id ";
        SQLServerPreparedStatement prestm=(SQLServerPreparedStatement) connection.prepareStatement(sql);
        prestm.setString(1,id);
        SQLServerResultSet resultSet=(SQLServerResultSet) prestm.executeQuery();
        List<Community> results=new ArrayList<>();
        while(resultSet.next()){
            String C_id= resultSet.getString("C_id");
            String C_name=resultSet.getString("C_name");
            Geography C_loc=resultSet.getGeography("C_loc");
            float C_rate=resultSet.getFloat("C_rate");
            float C_green_rate=resultSet.getFloat("C_green_rate");
            String C_pro_name=resultSet.getString("C_pro_name");
            String C_type=resultSet.getString("C_type");
            Date C_time=resultSet.getDate("C_time");
            results.add(new Community(C_id,C_name,C_loc,C_rate,C_green_rate,C_pro_name,C_type,C_time));
        }
        return results;
    }

    /**
     * 查询中心周围1000米范围内的小区
     * @param center_x 中心经度
     * @param center_y 中心纬度
     * @return 小区列表
     */
    public List<Community> FindNearCommunity(float center_x,float center_y) throws SQLException {
        String sql="declare @center geography " +
                "select @center = geography::STPointFromText('POINT ("+center_x +" "+center_y+")', 4326) " +
                "select * " +
                "from Community " +
                "where C_loc.STDistance(@center)<=1000";
        SQLServerStatement stm=(SQLServerStatement) connection.createStatement();
        SQLServerResultSet resultSet=(SQLServerResultSet) stm.executeQuery(sql);
        List<Community> results=new ArrayList<>();
        while(resultSet.next()){
            String C_id= resultSet.getString("C_id");
            String C_name=resultSet.getString("C_name");
            Geography C_loc=resultSet.getGeography("C_loc");
            float C_rate=resultSet.getFloat("C_rate");
            float C_green_rate=resultSet.getFloat("C_green_rate");
            String C_pro_name=resultSet.getString("C_pro_name");
            String C_type=resultSet.getString("C_type");
            Date C_time=resultSet.getDate("C_time");
            results.add(new Community(C_id,C_name,C_loc,C_rate,C_green_rate,C_pro_name,C_type,C_time));
        }
        return results;
    }


}
