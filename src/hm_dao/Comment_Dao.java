package hm_dao;


import hm_bean.House;
import hm_bean.User;

import javax.xml.stream.events.Comment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户评论数据访问层
 */
public class Comment_Dao {
    Connection connection;//连接的数据库

    public Comment_Dao(Connection connection){
        this.connection=connection;
    }

    /**
     * 插入用户对房源的评论
     * @param U_id 用户号
     * @param H_id 房源号
     * @param content 评论内容
     * @return 是否插入成功
     */
    public boolean InsertComment(String U_id, String H_id, String content) throws SQLException {
        //首先查询最大主键
        Statement statement=connection.createStatement();
        String sql="select max(Com_id) from Comment";
        ResultSet resultSet=statement.executeQuery(sql);
        String max_id;
        if(resultSet.next()){
            max_id=resultSet.getString(1);
            if(max_id==null) max_id="000000000";
            else max_id=String.format("%09d",Integer.parseInt(max_id)+1);
        }
        else return false;
        //然后插入记录
        sql="insert into Comment values(?,?,?,?)";
        PreparedStatement preStm=connection.prepareStatement(sql);
        preStm.setString(1,max_id);
        preStm.setString(2,U_id);
        preStm.setString(3,H_id);
        preStm.setString(4,content);
        int result=preStm.executeUpdate();
        return result!=0;
    }

    /**
     * 寻找房源的所用用户评论
     * @param H_id 房源好号
     * @param users 返回的用户
     * @param comments 返回的每个用户的评论
     * @return 查询是否成功
     */
    public boolean FindUserCommentsByH_id(String H_id,List<User> users,List<String> comments) throws SQLException {
        if(users!=null) users.clear();
        else users=new ArrayList<>();
        if(comments!=null) comments.clear();
        else comments=new ArrayList<>();
        //查询评论
        String sql="select U_id,Content from Comment where H_id='"+H_id+"'";
        Statement stm=connection.createStatement();
        ResultSet resultSet=stm.executeQuery(sql);
        List<String> U_ids=new ArrayList<>();
        while(resultSet.next()){
            U_ids.add(resultSet.getString("U_id"));
            comments.add(resultSet.getString("Content"));
        }
        //查询对应的用户
        for(String U_id:U_ids){
            sql="select * from H_User where U_id='"+U_id+"'";
            stm=connection.createStatement();
            resultSet=stm.executeQuery(sql);
            if(resultSet.next()){
                String U_name=resultSet.getString("U_name");
                String U_password=resultSet.getString("U_password");
                String U_type=resultSet.getString("U_type");
                users.add(new User(U_id,U_name,U_password,U_type));
            }
            else return false;
        }
        return true;
    }

}
