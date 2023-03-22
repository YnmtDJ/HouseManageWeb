package hm_service;


import hm_bean.User;
import hm_dao.User_Dao;
import hm_db.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class UserService {

    /**
     * 根据用户输入的信息进行用户注册，并返回用户号
     * @param user 用户信息
     * @return 是否注册成功，并返回用户号
     */
    public static boolean Register(User user) throws SQLException {
        Connection connection = DBUtil.getConnection();//获取连接
        User_Dao userDao = new User_Dao(connection);
        try {
            connection.setAutoCommit(false);
            if(userDao.InsertUser(user)){
                connection.commit();
                System.out.print("用户注册成功");
                return true;
            }
            else{
                connection.rollback();
                System.out.print("用户注册失败");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
            System.out.print("用户注册失败");
            return false;
        }
    }

    /**
     * 根据用户输入的用户号，密码登录。
     * 检验用户号，密码是否正确。若正确返回用户信息。
     * @param  user 输入的用户信息
     * @return 是否登录成功
     */
    public static boolean Login(User user){
        Connection connection=DBUtil.getConnection();
        User_Dao userDao=new User_Dao(connection);
        try {
            connection.setAutoCommit(true);
            User user1=userDao.FindUserById(user.U_id);
            if(user1!=null&&user.U_password.equals(user1.U_password)){//登录成功
                user.U_name=user1.U_name;
                user.U_type=user1.U_type;
                return true;
            }
            else{
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
