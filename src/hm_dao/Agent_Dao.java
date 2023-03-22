package hm_dao;

import hm_bean.Agent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 经纪人数据访问层
 */
public class Agent_Dao {
    Connection connection;

    public Agent_Dao(Connection connection){
        this.connection=connection;
    }

    /**
     * 根据小区号查找负责的所有经纪人
     * @param C_id 小区号
     * @return 经纪人，没有返回空列表
     */
    public List<Agent> FindAgentsByCId(String C_id) throws SQLException {
        String sql="select * from Agent where A_id in " +
                "(select A_id from Assistance where C_id=?)";
        PreparedStatement prestm=connection.prepareStatement(sql);
        prestm.setString(1,C_id);
        ResultSet resultSet=prestm.executeQuery();
        List<Agent> agents=new ArrayList<>();
        while(resultSet.next()){
            String A_id=resultSet.getString("A_id");
            String A_name=resultSet.getString("A_name");
            agents.add(new Agent(A_id,A_name));
        }
        return agents;
    }
}
