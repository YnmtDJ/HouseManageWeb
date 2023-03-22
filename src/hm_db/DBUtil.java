package hm_db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL="jdbc:sqlserver://127.0.0.1:1433;"
            + "databaseName=HouseManageDB;"
            + "user=sa;"
            + "password=1624309321hn;"
            + "trustServerCertificate=true;";

    //增加连接
    private static Connection conn = null;
    //增加一个静态块
    static {
        try {
            //1、加载驱动程序
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            //2、获得数据库的连接
            conn = DriverManager.getConnection(URL);
        } catch (ClassNotFoundException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //对外提供一个方法来获取这个链接
    public static Connection getConnection() {
        return conn;
    }
    public static void main(String[] args) {

    }
}
