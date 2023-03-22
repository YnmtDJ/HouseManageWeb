package hm_dao;
import hm_bean.Second_House;
import hm_bean.User;
import java.sql.*;
import java.util.List;

/**
 * 用户数据库访问接口
 * @author 胡楠
 *
 */
public class User_Dao {
	Connection connection;//数据库连接

	/**
	 * 构造函数
	 * @param connection 与数据库建立好的连接
	 */
	public User_Dao(Connection connection) {
		this.connection=connection;
	}
	
	/**
	 * 根据用户id查找用户
	 * @param UserId 用户号
	 * @return User 查找到的用户，如果没有查到返回null
	 * @throws SQLException
	 */
	public User FindUserById(String UserId) throws SQLException {
		User user=null;
		Statement myStmt = connection.createStatement();
		ResultSet myResult; 
		String sql="select* from H_User where U_id='"+UserId+"'";
		myResult= myStmt.executeQuery(sql);
		//读取结果集
        if(myResult.next()) {
        	String id=myResult.getString(1);
        	String name=myResult.getString(2);
        	String psd=myResult.getString(3);
        	String type=myResult.getString(4);
        	user=new User(id,name,psd,type);
        }
		return user;
	}
	
	/**
	 * 插入用户信息，保证主键自增，返回给user
	 * @param user 用户信息
	 * @return 是否插入成功
	 * @throws SQLException 
	 */
	public boolean InsertUser(User user) throws SQLException {
		Statement stmt=connection.createStatement();
		//首先查询主键最大值
		String sql="select max(U_id) as max_id from H_User";
		ResultSet result=stmt.executeQuery(sql);
		String max_id;
		if(result.next()){
			max_id=result.getString("max_id");
			max_id=String.format("%09d",Integer.parseInt(max_id)+1);
			user.U_id=max_id;//主键自增
		}
		//然后插入
		sql="insert into H_User values('"+user.U_id+"','"+
		user.U_name+"','"+user.U_password+"','"+user.U_type+"')";
		int result1=stmt.executeUpdate(sql);//返回受影响的行数
		return result1 != 0;
	}

	/**
	 * 删除用户
	 * @param user
	 * @return 是否删除成功
	 * @throws SQLException
	 */
	public boolean DeleteUser(User user) throws SQLException {
		Statement statement=connection.createStatement();
		String sql="delete H_User where U_id='"+user.U_id+"'";
		int result=statement.executeUpdate(sql);
		return result!=0;
	}

	/**
	 * 更新用户
	 * @param id
	 * @param user
	 * @return 更新是否成功
	 * @throws SQLException
	 */
	public boolean UpdateUser(String id,User user) throws SQLException {
		String sql ="update H_User set U_name=?,U_password=?,U_type=? where U_id=?";
		PreparedStatement preparedStatement=connection.prepareStatement(sql);
		preparedStatement.setString(1,user.U_name);
		preparedStatement.setString(2,user.U_password);
		preparedStatement.setString(3,user.U_type);
		preparedStatement.setString(4,user.U_id);
		int result=preparedStatement.executeUpdate();
		return result!=0;
	}


	
	
	
}
