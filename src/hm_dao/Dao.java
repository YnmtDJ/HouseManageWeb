package hm_dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Dao {
	Connection connection;
	//连接sqlserver注册驱动程序字符串   
    String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    //与数据库建立连接的字符串
	String url="jdbc:sqlserver://localhost:1433;DatabaseName=HouseManageDB;trustServerCertificate=true";
	
	
	/**
	 * 建立连接
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Dao() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
		//将JDBC驱动类装载入Java虚拟机
		Class.forName(driver).newInstance();
		//与数据库建立连接
		connection = DriverManager.getConnection(url,"sa","1624309321hn");
		if(connection!=null) System.out.print("Connect Success");
		else System.out.print("Connect Failed");
	}


}
