package hm_dao;

import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;
import jdk.jshell.spi.SPIResolutionException;
import hm_bean.*;

import javax.xml.transform.Result;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 房源数据访问层
 */
public class House_Dao {

    Connection connection;//与数据库连接

    /**
     * 构造函数
     * @param connection 与数据库建立的连接
     */
    public House_Dao(Connection connection){
        this.connection=connection;
    }

    /**
     * 插入房源，并将其划分到归属的楼栋，保证主键自增
     * @param house 房源
     * @param type 房源种类={二手房，租房}
     * @param B_id 栋号
     * @param U_id 用户号
     * @return 是否插入成功
     */
    public boolean InsertHouse(House house,String type, String B_id,String U_id) throws SQLException {
        Statement statement=connection.createStatement();
        //首先查询最大主键
        String sql="select max(H_id) as max_id from House";
        ResultSet result=statement.executeQuery(sql);
        String max_id;
        if(result.next()){
            max_id=result.getString("max_id");
            if(max_id==null) max_id="000000000";
            else max_id=String.format("%09d",Integer.parseInt(max_id)+1);
            house.id=max_id;//主键自增
        }
        //根据B_id查询所在小区
        sql="select C_id from ComBuild where B_id='"+B_id+"'";
        result=statement.executeQuery(sql);
        String C_id = null;
        if(result.next()){
            C_id=result.getString("C_id");
        }
        //然后插入
        sql="insert into House values(?,?,?,?,?,?,?,?,?,?)";
        SQLServerPreparedStatement preState=(SQLServerPreparedStatement) connection.prepareStatement(sql);
        preState.setString(1,house.id);
        preState.setString(2, house.type);
        preState.setFloat(3, house.area);
        preState.setDate(4, house.time);
        preState.setString(5, house.decorate);
        preState.setString(6, house.direct);
        preState.setString(7, house.rate);
        preState.setInt(8, house.layer);
        preState.setString(9, C_id);
        preState.setBinaryStream(10, house.img);
        int result1=preState.executeUpdate();
        if(result1==0) return false;
        //房源种类判断
        if(type.equals("二手房")){
            sql="insert into Second_House values(?,?)";
            preState=(SQLServerPreparedStatement) connection.prepareStatement(sql);
            preState.setString(1,house.id);
            preState.setInt(2,((Second_House)house).price);
        }
        else if(type.equals("租房")){
            sql="insert into Renting values(?,?)";
            preState=(SQLServerPreparedStatement) connection.prepareStatement(sql);
            preState.setString(1,house.id);
            preState.setInt(2,((Renting)house).price);
        }
        result1=preState.executeUpdate();
        if(result1==0) return false;
        //建立楼栋，房源联系
        sql="insert into BuildHouse values(?,?)";
        preState=(SQLServerPreparedStatement) connection.prepareStatement(sql);
        preState.setString(1,B_id);
        preState.setString(2,house.id);
        result1=preState.executeUpdate();
        if(result1==0) return false;
        //建立用户发布房源关系
        sql="insert into Post values(?,?)";
        preState=(SQLServerPreparedStatement) connection.prepareStatement(sql);
        preState.setString(1,U_id);
        preState.setString(2,house.id);
        result1= preState.executeUpdate();
        return result1!=0;
    }

    /**
     * 删除房源
     * @param H_id 房源号
     * @return 是否删除成功
     * @throws SQLException
     */
    public boolean DeleteHouse(String H_id) throws SQLException {
        Statement statement=connection.createStatement();
        String sql="delete House where H_id='"+H_id+"'";
        int result=statement.executeUpdate(sql);
        return result!=0;
    }

    /**
     * 更新房源，对于不更新的属性传入null，根据房源号自动判断房源类型
     * @param id 房源号
     * @param type 户型
     * @param area 面积
     * @param time 发布日期
     * @param decorate 装修
     * @param direct 朝向
     * @param rate 梯户
     * @param layer 楼层
     * @param img 房源图片
     * @param price 价格
     * @return 是否更行成功
     * @throws SQLException
     */
    public boolean UpdateHouse(String id,String type,Float area,Date time,String decorate,String direct,String rate,
                               Integer layer,InputStream img,int price) throws SQLException {
        String sql ="update House set ";
        if(type!=null) sql+="H_type=?,";
        if(area!=null) sql+="H_area=?,";
        if(time!=null) sql+="H_time=?,";
        if(decorate!=null) sql+="H_decorate=?,";
        if(direct!=null) sql+="H_direct=?,";
        if(rate!=null) sql+="H_rate=?,";
        if(layer!=null) sql+="H_layer=?,";
        if(img!=null) sql+="H_img=?,";
        sql=sql.substring(0,sql.length()-1);
        sql+=" where H_id=? ";
        SQLServerPreparedStatement preparedStatement=(SQLServerPreparedStatement) connection.prepareStatement(sql);
        int index=1;//'?'索引
        if(type!=null){
            preparedStatement.setString(index,type);
            index++;
        }
        if(area!=null){
            preparedStatement.setFloat(index,area);
            index++;
        }
        if(time!=null){
            preparedStatement.setDate(index,time);
            index++;
        }
        if(decorate!=null){
            preparedStatement.setString(index,decorate);
            index++;
        }
        if(direct!=null){
            preparedStatement.setString(index,direct);
            index++;
        }
        if(rate!=null) {
            preparedStatement.setString(index,rate);
            index++;
        }
        if(layer!=null){
            preparedStatement.setInt(index,layer);
            index++;
        }
        if(img!=null){
            preparedStatement.setBinaryStream(index,img);
            index++;
        }
        preparedStatement.setString(index,id);
        int result=preparedStatement.executeUpdate();
        if(result==0) return false;
        //判断是二手房还是租房
        sql="select * from Renting where H_id='"+id+"'";
        Statement statement=connection.createStatement();
        ResultSet resultSet=statement.executeQuery(sql);
        if(resultSet.next()){//租房
            sql="update Renting set R_price="+ price +
                    "where H_id='"+id+"'";
        }
        else{//二手房
            sql="update Second_House set S_price="+ price +
                    "where H_id='"+id+"'";
        }
        result=statement.executeUpdate(sql);
        return result!=0;
    }

    /**
     * 通过单值，或者组合条件查询租房，对于没有限定的条件传入null
     * @param adminArea 行政区
     * @param community 小区
     * @param MinPrice 最底租金
     * @param MaxPrice 最高租金
     * @param H_type 户型
     * @param MinArea 最小面积
     * @param MaxArea 最大面积
     * @param first 想要的第一条记录索引（从1开始）
     * @param num 想要取出的记录个数
     * @return 满足条件的房源列表
     */
    public List<Renting> FindRenting(Admin_Area adminArea,Community community,Integer MinPrice,Integer MaxPrice,String H_type,
                              Float MinArea,Float MaxArea,int first,int num) throws SQLException {
        //通过字符串拼接，形成最终条件
        String sql="select House.*,R_price from House,Renting where House.H_id=Renting.H_id and ";
        if(adminArea!=null){
            String sub_sql="(select C_id from AdminCom where id=?) ";
            sql+=("C_id in "+sub_sql+"and ");
        }
        if(community!=null){
            sql+="C_id=? and ";
        }
        if(MinPrice!=null){
            sql+="R_price>=? and ";
        }
        if(MaxPrice!=null){
            sql+="R_price<=? and ";
        }
        if(H_type!=null){
            sql+="H_type=? and ";
        }
        if(MinArea!=null){
            sql+="H_area>=? and ";
        }
        if(MaxArea!=null){
            sql+="H_area<=? and ";
        }
        sql=sql.substring(0,sql.length()-4);
        PreparedStatement preState=connection.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        int index=1;//'?'索引
        if(adminArea!=null){
            preState.setString(index, adminArea.id);
            index+=1;
        }
        if(community!=null){
            preState.setString(index,community.id);
            index+=1;
        }
        if(MinPrice!=null){
            preState.setInt(index,MinPrice);
            index+=1;
        }
        if(MaxPrice!=null){
            preState.setInt(index,MaxPrice);
            index+=1;
        }
        if(H_type!=null){
            preState.setString(index,H_type);
            index+=1;
        }
        if(MinArea!=null){
            preState.setFloat(index,MinArea);
            index+=1;
        }
        if(MaxArea!=null){
            preState.setFloat(index,MaxArea);
            index+=1;
        }
        //执行sql
        ResultSet resultSet=preState.executeQuery();
        resultSet.absolute(first-1);
        //循环读取结果
        List<Renting> results=new ArrayList<>();
        int n=0;
        while(resultSet.next()&&n<num){
            String Hid=resultSet.getString("H_id");
            String Htype=resultSet.getString("H_type");
            Float Harea=resultSet.getFloat("H_area");
            Date Htime=resultSet.getDate("H_time");
            String Hdecorate=resultSet.getString("H_decorate");
            String Hdirect=resultSet.getString("H_direct");
            String Hrate=resultSet.getString("H_rate");
            int Hlayer=resultSet.getInt("H_layer");
            String Cid=resultSet.getString("C_id");
            InputStream img=resultSet.getBinaryStream("H_img");
            int Rprice=resultSet.getInt("R_price");
            results.add(new Renting(Hid,Htype,Harea,Htime,Hdecorate,Hdirect,Hrate,Hlayer,Cid,img,Rprice));
            n++;
        }
        return results;
    }


    /**
     * 通过单值，或者组合条件查询二手房，对于没有限定的条件传入null。
     * @param adminArea 行政区
     * @param community 小区
     * @param MinPrice 最底租金
     * @param MaxPrice 最高租金
     * @param H_type 户型
     * @param MinArea 最小面积
     * @param MaxArea 最大面积
     * @param first 想要的第一条记录索引（从1开始）
     * @param num 想要取出的记录个数
     * @return 满足条件的房源列表
     */
    public List<Second_House> FindSecond_House(Admin_Area adminArea,Community community,Integer MinPrice,Integer MaxPrice,String H_type,
                              Float MinArea,Float MaxArea,int first,int num) throws SQLException {
        //通过字符串拼接，形成最终条件
        String sql="select House.*,S_price from House,Second_House where House.H_id=Second_House.H_id and ";
        if(adminArea!=null){
            String sub_sql="(select C_id from AdminCom where id=?) ";
            sql+=("C_id in "+sub_sql+"and ");
        }
        if(community!=null) sql+="C_id=? and ";
        if(MinPrice!=null) sql+="S_price>=? and ";
        if(MaxPrice!=null) sql+="S_price<=? and ";
        if(H_type!=null) sql+="H_type like ? and ";
        if(MinArea!=null) sql+="H_area>=? and ";
        if(MaxArea!=null) sql+="H_area<=? and ";
        sql=sql.substring(0,sql.length()-4);
        PreparedStatement preState=connection.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        int index=1;//'?'索引
        if(adminArea!=null){
            preState.setString(index, adminArea.id);
            index+=1;
        }
        if(community!=null){
            preState.setString(index,community.id);
            index+=1;
        }
        if(MinPrice!=null){
            preState.setInt(index,MinPrice);
            index+=1;
        }
        if(MaxPrice!=null){
            preState.setInt(index,MaxPrice);
            index+=1;
        }
        if(H_type!=null){
            preState.setString(index,H_type);
            index+=1;
        }
        if(MinArea!=null){
            preState.setFloat(index,MinArea);
            index+=1;
        }
        if(MaxArea!=null){
            preState.setFloat(index,MaxArea);
            index+=1;
        }
        //执行sql
        ResultSet resultSet=preState.executeQuery();
        //循环读取结果
        List<Second_House> results=new ArrayList<>();
        resultSet.absolute(first-1);
        int count=0;
        while(resultSet.next()&&count<num){
            String Hid=resultSet.getString("H_id");
            String Htype=resultSet.getString("H_type");
            Float Harea=resultSet.getFloat("H_area");
            Date Htime=resultSet.getDate("H_time");
            String Hdecorate=resultSet.getString("H_decorate");
            String Hdirect=resultSet.getString("H_direct");
            String Hrate=resultSet.getString("H_rate");
            int Hlayer=resultSet.getInt("H_layer");
            String Cid=resultSet.getString("C_id");
            InputStream img=resultSet.getBinaryStream("H_img");
            int Sprice=resultSet.getInt("S_price");
            results.add(new Second_House(Hid,Htype,Harea,Htime,Hdecorate,Hdirect,Hrate,Hlayer,Cid,img,Sprice));
            count++;
        }
        return results;
    }



    /**
     * 在售二手房查询
     * @return 房源列表
     */
    public List<Second_House> FindSecondOnSale() throws SQLException {
        CallableStatement proc=connection.prepareCall("{call FindSecondOnSale()}");
        ResultSet resultSet=proc.executeQuery();
        List<Second_House> results=new ArrayList<>();
        while(resultSet.next()){
            String Hid=resultSet.getString("H_id");
            String Htype=resultSet.getString("H_type");
            Float Harea=resultSet.getFloat("H_area");
            Date Htime=resultSet.getDate("H_time");
            String Hdecorate=resultSet.getString("H_decorate");
            String Hdirect=resultSet.getString("H_direct");
            String Hrate=resultSet.getString("H_rate");
            int Hlayer=resultSet.getInt("H_layer");
            String Cid=resultSet.getString("C_id");
            InputStream img=resultSet.getBinaryStream("H_img");
            int Sprice=resultSet.getInt("S_price");
            results.add(new Second_House(Hid,Htype,Harea,Htime,Hdecorate,Hdirect,Hrate,Hlayer,Cid,img,Sprice));
        }
        return results;
    }

    /**
     * 在售租房查询
     * @return 房源列表
     */
    public List<Renting> FindRentOnSale() throws SQLException {
        CallableStatement proc=connection.prepareCall("{call FindRentOnSale()}");
        ResultSet resultSet=proc.executeQuery();
        List<Renting> results=new ArrayList<>();
        while(resultSet.next()){
            String Hid=resultSet.getString("H_id");
            String Htype=resultSet.getString("H_type");
            Float Harea=resultSet.getFloat("H_area");
            Date Htime=resultSet.getDate("H_time");
            String Hdecorate=resultSet.getString("H_decorate");
            String Hdirect=resultSet.getString("H_direct");
            String Hrate=resultSet.getString("H_rate");
            int Hlayer=resultSet.getInt("H_layer");
            String Cid=resultSet.getString("C_id");
            InputStream img=resultSet.getBinaryStream("H_img");
            int Rprice=resultSet.getInt("R_price");
            results.add(new Renting(Hid,Htype,Harea,Htime,Hdecorate,Hdirect,Hrate,Hlayer,Cid,img,Rprice));
        }
        return results;
    }

    /**
     * 根据经纪人查询二手房
     * @param agent 经纪人
     * @return 房源列表
     */
    public List<Second_House> FindSecondByAgent(Agent agent) throws SQLException {
        CallableStatement proc=connection.prepareCall("{call FindSecondByA_id(?)}");
        proc.setString(1, agent.id);
        ResultSet resultSet=proc.executeQuery();
        List<Second_House> results=new ArrayList<>();
        while(resultSet.next()){
            String Hid=resultSet.getString("H_id");
            String Htype=resultSet.getString("H_type");
            Float Harea=resultSet.getFloat("H_area");
            Date Htime=resultSet.getDate("H_time");
            String Hdecorate=resultSet.getString("H_decorate");
            String Hdirect=resultSet.getString("H_direct");
            String Hrate=resultSet.getString("H_rate");
            int Hlayer=resultSet.getInt("H_layer");
            String Cid=resultSet.getString("C_id");
            InputStream img=resultSet.getBinaryStream("H_img");
            int Sprice=resultSet.getInt("S_price");
            results.add(new Second_House(Hid,Htype,Harea,Htime,Hdecorate,Hdirect,Hrate,Hlayer,Cid,img,Sprice));
        }
        return results;
    }


    /**
     * 根据经纪人查询租房
     * @param agent 经纪人
     * @return 房源列表
     */
    public List<Renting> FindRentByAgent(Agent agent) throws SQLException {
        CallableStatement proc=connection.prepareCall("{call FindRentByA_id(?)}");
        proc.setString(1, agent.id);
        ResultSet resultSet=proc.executeQuery();
        List<Renting> results=new ArrayList<>();
        while(resultSet.next()){
            String Hid=resultSet.getString("H_id");
            String Htype=resultSet.getString("H_type");
            Float Harea=resultSet.getFloat("H_area");
            Date Htime=resultSet.getDate("H_time");
            String Hdecorate=resultSet.getString("H_decorate");
            String Hdirect=resultSet.getString("H_direct");
            String Hrate=resultSet.getString("H_rate");
            int Hlayer=resultSet.getInt("H_layer");
            String Cid=resultSet.getString("C_id");
            InputStream img=resultSet.getBinaryStream("H_img");
            int Rprice=resultSet.getInt("R_price");
            results.add(new Renting(Hid,Htype,Harea,Htime,Hdecorate,Hdirect,Hrate,Hlayer,Cid,img,Rprice));
        }
        return results;
    }



    /**
     * 某个房源在近一周内的历史带看次数
     * @param house 房源
     * @return 带看次数，若查询失败返回-1
     */
    public int FindSeeNumByHouse(House house) throws SQLException {
        CallableStatement proc=connection.prepareCall("{call FindUAHNumByH_id(?)}");
        proc.setString(1, house.id);
        ResultSet resultSet=proc.executeQuery();
        if(resultSet.next()){
            return resultSet.getInt(1);
        }
        return -1;
    }

    /**
     * 二手房的相似总价推荐，根据价格差值排序
     * @param house 被比较的二手房
     * @param Num 推荐个数
     * @return 总价相似的房源列表
     */
    public List<Second_House> FindSimilarSecondByPrice(Second_House house,int Num) throws SQLException {
        CallableStatement proc=connection.prepareCall("{call FindSimilarSecondByPrice(?,?)}");
        proc.setString(1, house.id);
        proc.setFloat(2,house.price*house.area);
        ResultSet resultSet=proc.executeQuery();
        List<Second_House> results=new ArrayList<>();
        while(resultSet.next()&&results.size()<Num){
            String Hid=resultSet.getString("H_id");
            String Htype=resultSet.getString("H_type");
            Float Harea=resultSet.getFloat("H_area");
            Date Htime=resultSet.getDate("H_time");
            String Hdecorate=resultSet.getString("H_decorate");
            String Hdirect=resultSet.getString("H_direct");
            String Hrate=resultSet.getString("H_rate");
            int Hlayer=resultSet.getInt("H_layer");
            String Cid=resultSet.getString("C_id");
            InputStream img=resultSet.getBinaryStream("H_img");
            int Sprice=resultSet.getInt("S_price");
            results.add(new Second_House(Hid,Htype,Harea,Htime,Hdecorate,Hdirect,Hrate,Hlayer,Cid,img,Sprice));
        }
        return results;
    }

    /**
     * 二手房的相似户型推荐
     * @param house 被比较的二手房
     * @param Num 推荐个数
     * @return 户型相似的房源列表
     */
    public List<Second_House> FindSimilarSecondByType(Second_House house,int Num) throws SQLException {
        CallableStatement proc=connection.prepareCall("{call FindSimilarSecondByType(?,?)}");
        proc.setString(1, house.id);
        proc.setString(2,house.type);
        ResultSet resultSet=proc.executeQuery();
        List<Second_House> results=new ArrayList<>();
        while(resultSet.next()&&results.size()<Num){
            String Hid=resultSet.getString("H_id");
            String Htype=resultSet.getString("H_type");
            Float Harea=resultSet.getFloat("H_area");
            Date Htime=resultSet.getDate("H_time");
            String Hdecorate=resultSet.getString("H_decorate");
            String Hdirect=resultSet.getString("H_direct");
            String Hrate=resultSet.getString("H_rate");
            int Hlayer=resultSet.getInt("H_layer");
            String Cid=resultSet.getString("C_id");
            InputStream img=resultSet.getBinaryStream("H_img");
            int Sprice=resultSet.getInt("S_price");
            results.add(new Second_House(Hid,Htype,Harea,Htime,Hdecorate,Hdirect,Hrate,Hlayer,Cid,img,Sprice));
        }
        return results;
    }


    /**
     * 租房的相似租金推荐，根据价格差值排序
     * @param house 被比较的租房
     * @param Num 推荐个数
     * @return 租金相似的房源列表
     */
    public List<Renting> FindSimilarRentByPrice(Renting house,int Num) throws SQLException {
        CallableStatement proc=connection.prepareCall("{call FindSimilarRentByPrice(?,?)}");
        proc.setString(1, house.id);
        proc.setFloat(2,house.price);
        ResultSet resultSet=proc.executeQuery();
        List<Renting> results=new ArrayList<>();
        while(resultSet.next()&&results.size()<Num){
            String Hid=resultSet.getString("H_id");
            String Htype=resultSet.getString("H_type");
            Float Harea=resultSet.getFloat("H_area");
            Date Htime=resultSet.getDate("H_time");
            String Hdecorate=resultSet.getString("H_decorate");
            String Hdirect=resultSet.getString("H_direct");
            String Hrate=resultSet.getString("H_rate");
            int Hlayer=resultSet.getInt("H_layer");
            String Cid=resultSet.getString("C_id");
            InputStream img=resultSet.getBinaryStream("H_img");
            int Rprice=resultSet.getInt("R_price");
            results.add(new Renting(Hid,Htype,Harea,Htime,Hdecorate,Hdirect,Hrate,Hlayer,Cid,img,Rprice));
        }
        return results;
    }


    /**
     * 租房的相似户型推荐
     * @param house 被比较的租房
     * @param Num 推荐个数
     * @return 户型相似的房源列表
     */
    public List<Renting> FindSimilarRentByType(Renting house,int Num) throws SQLException {
        CallableStatement proc=connection.prepareCall("{call FindSimilarRentByType(?,?)}");
        proc.setString(1, house.id);
        proc.setString(2,house.type);
        ResultSet resultSet=proc.executeQuery();
        List<Renting> results=new ArrayList<>();
        while(resultSet.next()&&results.size()<Num){
            String Hid=resultSet.getString("H_id");
            String Htype=resultSet.getString("H_type");
            Float Harea=resultSet.getFloat("H_area");
            Date Htime=resultSet.getDate("H_time");
            String Hdecorate=resultSet.getString("H_decorate");
            String Hdirect=resultSet.getString("H_direct");
            String Hrate=resultSet.getString("H_rate");
            int Hlayer=resultSet.getInt("H_layer");
            String Cid=resultSet.getString("C_id");
            InputStream img=resultSet.getBinaryStream("H_img");
            int Rprice=resultSet.getInt("R_price");
            results.add(new Renting(Hid,Htype,Harea,Htime,Hdecorate,Hdirect,Hrate,Hlayer,Cid,img,Rprice));
        }
        return results;
    }



    /**
     * 根据周边查询附近的二手房
     * @param surround 周边
     * @return 房源列表
     * @throws SQLException
     */
    public List<Second_House> FindSecondBySurround(Surround surround) throws SQLException {
        CallableStatement proc=connection.prepareCall("{call FindSecondByS_id(?)}");
        proc.setString(1, surround.id);
        ResultSet resultSet=proc.executeQuery();
        List<Second_House> results=new ArrayList<>();
        while(resultSet.next()){
            String Hid=resultSet.getString("H_id");
            String Htype=resultSet.getString("H_type");
            Float Harea=resultSet.getFloat("H_area");
            Date Htime=resultSet.getDate("H_time");
            String Hdecorate=resultSet.getString("H_decorate");
            String Hdirect=resultSet.getString("H_direct");
            String Hrate=resultSet.getString("H_rate");
            int Hlayer=resultSet.getInt("H_layer");
            String Cid=resultSet.getString("C_id");
            InputStream img=resultSet.getBinaryStream("H_img");
            int Sprice=resultSet.getInt("S_price");
            results.add(new Second_House(Hid,Htype,Harea,Htime,Hdecorate,Hdirect,Hrate,Hlayer,Cid,img,Sprice));
        }
        return results;
    }


    /**
     * 根据周边查询附近的租房
     * @param surround 周边
     * @return 房源列表
     * @throws SQLException
     */
    public List<Renting> FindRentBySurround(Surround surround) throws SQLException {
        CallableStatement proc=connection.prepareCall("{call FindRentByS_id(?)}");
        proc.setString(1, surround.id);
        ResultSet resultSet=proc.executeQuery();
        List<Renting> results=new ArrayList<>();
        while(resultSet.next()){
            String Hid=resultSet.getString("H_id");
            String Htype=resultSet.getString("H_type");
            Float Harea=resultSet.getFloat("H_area");
            Date Htime=resultSet.getDate("H_time");
            String Hdecorate=resultSet.getString("H_decorate");
            String Hdirect=resultSet.getString("H_direct");
            String Hrate=resultSet.getString("H_rate");
            int Hlayer=resultSet.getInt("H_layer");
            String Cid=resultSet.getString("C_id");
            InputStream img=resultSet.getBinaryStream("H_img");
            int Rprice=resultSet.getInt("R_price");
            results.add(new Renting(Hid,Htype,Harea,Htime,Hdecorate,Hdirect,Hrate,Hlayer,Cid,img,Rprice));
        }
        return results;
    }

    /**
     * 根据房源号查询房源。
     * @param H_id 房源号
     * @return 二手房或租房，若没有查到返回null
     */
    public House FindHouseById(String H_id) throws SQLException {
        String sql="select * from House where H_id=?";
        PreparedStatement prestm=connection.prepareStatement(sql);
        prestm.setString(1,H_id);
        ResultSet resultSet=prestm.executeQuery();
        if(resultSet.next()){
            String Htype=resultSet.getString("H_type");
            Float Harea=resultSet.getFloat("H_area");
            Date Htime=resultSet.getDate("H_time");
            String Hdecorate=resultSet.getString("H_decorate");
            String Hdirect=resultSet.getString("H_direct");
            String Hrate=resultSet.getString("H_rate");
            int Hlayer=resultSet.getInt("H_layer");
            String Cid=resultSet.getString("C_id");
            InputStream img=resultSet.getBinaryStream("H_img");
            sql="select * from Second_House where H_id=?";
            prestm=connection.prepareStatement(sql);
            prestm.setString(1,H_id);
            resultSet=prestm.executeQuery();
            if(resultSet.next()){//是二手房
                int S_price=resultSet.getInt("S_price");
                return new Second_House(H_id,Htype,Harea,Htime,Hdecorate,Hdirect,Hrate,Hlayer,Cid,img,S_price);
            }
            else{//是租房
                sql="select * from Renting where H_id=?";
                prestm=connection.prepareStatement(sql);
                prestm.setString(1,H_id);
                resultSet=prestm.executeQuery();
                if(resultSet.next()){
                    int R_price=resultSet.getInt("R_price");
                    return new Renting(H_id,Htype,Harea,Htime,Hdecorate,Hdirect,Hrate,Hlayer,Cid,img,R_price);
                }
                else return null;
            }
        }
        return null;
    }



    /**
     * 根据房源号查找房源图片
     * @param H_id 房源号
     * @return 二进制流图片，若没有找到返回null
     */
    public InputStream FindImgById(String H_id) throws SQLException {
        String sql="select H_img from House where H_id='"+H_id+"'";
        Statement stm=connection.createStatement();
        ResultSet resultSet=stm.executeQuery(sql);
        if(resultSet.next()){
            return resultSet.getBinaryStream("H_img");
        }
        return null;
    }

    /**
     * 插入带看记录，对于相同的用户、经纪人、房源每天只记录一次
     * @param U_id 用户号
     * @param A_id 经纪人号
     * @param H_id 房源号
     * @return 1-插入成功，0-插入失败，-1-记录已存在
     */
    public int InsertUAH(String U_id,String A_id,String H_id) throws SQLException {
        //首先检查是否存在相应的记录
        String sql="select * from UAH where U_id=? and A_id=? and H_id=? " +
                "and DateName(YEAR,Time)=DateName(YEAR,GetDate()) " +
                "and DateName(MONTH,Time)=DateName(MONTH,GetDate()) " +
                "and DateName(DAY,Time)=DateName(DAY,GetDate())";
        PreparedStatement prestm=connection.prepareStatement(sql);
        prestm.setString(1,U_id);
        prestm.setString(2,A_id);
        prestm.setString(3,H_id);
        ResultSet resultSet=prestm.executeQuery();
        if(resultSet.next()){
            return -1;
        }
        //然后插入记录
        sql="insert into UAH values(?,?,?,?)";
        prestm=connection.prepareStatement(sql);
        prestm.setString(1,U_id);
        prestm.setString(2,A_id);
        prestm.setString(3,H_id);
        Date date=new Date(System.currentTimeMillis());
        prestm.setDate(4,date);
        return prestm.executeUpdate();
    }

    /**
     * 查找某个小区的二手房
     * @param C_id 小区号
     * @param Num 查找个数
     * @return 二手房列表
     */
    public List<Second_House> FindSecondByCId(String C_id,int Num) throws SQLException {
        String sql="select House.*, Second_House.S_price " +
                "from House,Second_House " +
                "where House.H_id=Second_House.H_id and House.C_id=?";
        PreparedStatement prestm=connection.prepareStatement(sql);
        prestm.setString(1,C_id);
        ResultSet resultSet=prestm.executeQuery();
        List<Second_House> results=new ArrayList<>();
        while(resultSet.next()&&results.size()<Num){
            String Hid=resultSet.getString("H_id");
            String Htype=resultSet.getString("H_type");
            Float Harea=resultSet.getFloat("H_area");
            Date Htime=resultSet.getDate("H_time");
            String Hdecorate=resultSet.getString("H_decorate");
            String Hdirect=resultSet.getString("H_direct");
            String Hrate=resultSet.getString("H_rate");
            int Hlayer=resultSet.getInt("H_layer");
            InputStream img=resultSet.getBinaryStream("H_img");
            int Sprice=resultSet.getInt("S_price");
            results.add(new Second_House(Hid,Htype,Harea,Htime,Hdecorate,Hdirect,Hrate,Hlayer,C_id,img,Sprice));
        }
        return results;
    }


    /**
     * 查找某个小区的租房
     * @param C_id 小区号
     * @param Num 查找个数
     * @return 租房列表
     */
    public List<Renting> FindRentingByCId(String C_id,int Num) throws SQLException {
        String sql="select House.*, Renting.R_price " +
                "from House,Renting " +
                "where House.H_id=Renting.H_id and House.C_id=?";
        PreparedStatement prestm=connection.prepareStatement(sql);
        prestm.setString(1,C_id);
        ResultSet resultSet=prestm.executeQuery();
        List<Renting> results=new ArrayList<>();
        while(resultSet.next()&&results.size()<Num){
            String Hid=resultSet.getString("H_id");
            String Htype=resultSet.getString("H_type");
            Float Harea=resultSet.getFloat("H_area");
            Date Htime=resultSet.getDate("H_time");
            String Hdecorate=resultSet.getString("H_decorate");
            String Hdirect=resultSet.getString("H_direct");
            String Hrate=resultSet.getString("H_rate");
            int Hlayer=resultSet.getInt("H_layer");
            InputStream img=resultSet.getBinaryStream("H_img");
            int Rprice=resultSet.getInt("R_price");
            results.add(new Renting(Hid,Htype,Harea,Htime,Hdecorate,Hdirect,Hrate,Hlayer,C_id,img,Rprice));
        }
        return results;
    }

    /**
     * 根据条件查询满足的二手房个数
     * @param adminArea 行政区
     * @param community 小区
     * @param MinPrice 最低价格
     * @param MaxPrice 最高价格
     * @param H_type 户型
     * @param MinArea 最小面积
     * @param MaxArea 最大面积
     * @return 满足的二手房个数，查找失败返回-1
     * @throws SQLException
     */
    public int FindSecondNum(Admin_Area adminArea,Community community,Integer MinPrice,Integer MaxPrice,String H_type,
                                               Float MinArea,Float MaxArea) throws SQLException {
        //通过字符串拼接，形成最终条件
        String sql="select count(*) from House,Second_House where House.H_id=Second_House.H_id and ";
        if(adminArea!=null){
            String sub_sql="(select C_id from AdminCom where id=?) ";
            sql+=("C_id in "+sub_sql+"and ");
        }
        if(community!=null) sql+="C_id=? and ";
        if(MinPrice!=null) sql+="S_price>=? and ";
        if(MaxPrice!=null) sql+="S_price<=? and ";
        if(H_type!=null) sql+="H_type like ? and ";
        if(MinArea!=null) sql+="H_area>=? and ";
        if(MaxArea!=null) sql+="H_area<=? and ";
        sql=sql.substring(0,sql.length()-4);
        PreparedStatement preState=connection.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        int index=1;//'?'索引
        if(adminArea!=null){
            preState.setString(index, adminArea.id);
            index+=1;
        }
        if(community!=null){
            preState.setString(index,community.id);
            index+=1;
        }
        if(MinPrice!=null){
            preState.setInt(index,MinPrice);
            index+=1;
        }
        if(MaxPrice!=null){
            preState.setInt(index,MaxPrice);
            index+=1;
        }
        if(H_type!=null){
            preState.setString(index,H_type);
            index+=1;
        }
        if(MinArea!=null){
            preState.setFloat(index,MinArea);
            index+=1;
        }
        if(MaxArea!=null){
            preState.setFloat(index,MaxArea);
            index+=1;
        }
        //执行sql
        ResultSet resultSet=preState.executeQuery();
        if(resultSet.next()){
            return resultSet.getInt(1);
        }
        return -1;
    }


    /**
     * 根据条件查询满足的二手房个数
     * @param adminArea 行政区
     * @param community 小区
     * @param MinPrice 最低价格
     * @param MaxPrice 最高价格
     * @param H_type 户型
     * @param MinArea 最小面积
     * @param MaxArea 最大面积
     * @return 满足的二手房个数，查找失败返回-1
     * @throws SQLException
     */
    public int FindRentNum(Admin_Area adminArea,Community community,Integer MinPrice,Integer MaxPrice,String H_type,
                             Float MinArea,Float MaxArea) throws SQLException {
        //通过字符串拼接，形成最终条件
        String sql="select count(*) from House,Renting where House.H_id=Renting.H_id and ";
        if(adminArea!=null){
            String sub_sql="(select C_id from AdminCom where id=?) ";
            sql+=("C_id in "+sub_sql+"and ");
        }
        if(community!=null) sql+="C_id=? and ";
        if(MinPrice!=null) sql+="R_price>=? and ";
        if(MaxPrice!=null) sql+="R_price<=? and ";
        if(H_type!=null) sql+="H_type like ? and ";
        if(MinArea!=null) sql+="H_area>=? and ";
        if(MaxArea!=null) sql+="H_area<=? and ";
        sql=sql.substring(0,sql.length()-4);
        PreparedStatement preState=connection.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        int index=1;//'?'索引
        if(adminArea!=null){
            preState.setString(index, adminArea.id);
            index+=1;
        }
        if(community!=null){
            preState.setString(index,community.id);
            index+=1;
        }
        if(MinPrice!=null){
            preState.setInt(index,MinPrice);
            index+=1;
        }
        if(MaxPrice!=null){
            preState.setInt(index,MaxPrice);
            index+=1;
        }
        if(H_type!=null){
            preState.setString(index,H_type);
            index+=1;
        }
        if(MinArea!=null){
            preState.setFloat(index,MinArea);
            index+=1;
        }
        if(MaxArea!=null){
            preState.setFloat(index,MaxArea);
            index+=1;
        }
        //执行sql
        ResultSet resultSet=preState.executeQuery();
        if(resultSet.next()){
            return resultSet.getInt(1);
        }
        return -1;
    }


    /**
     * 查询用户发布的二手房
     * @param id 用户号
     * @param first 查询的第一条记录索引（从1开始）
     * @param num 查询的条数
     * @return 房源列表
     */
    public List<Second_House> FindSecondById(String id,int first,int num) throws SQLException {
        String sql="select House.*,S_price " +
                "from Post,House,Second_House " +
                "where Post.H_id=House.H_id and House.H_id=Second_House.H_id and U_id=?";
        PreparedStatement prestm=connection.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        prestm.setString(1,id);
        ResultSet resultSet=prestm.executeQuery();
        resultSet.absolute(first-1);
        List<Second_House> results=new ArrayList<>();
        while(resultSet.next()&&results.size()<num){
            String Hid=resultSet.getString("H_id");
            String Htype=resultSet.getString("H_type");
            Float Harea=resultSet.getFloat("H_area");
            Date Htime=resultSet.getDate("H_time");
            String Hdecorate=resultSet.getString("H_decorate");
            String Hdirect=resultSet.getString("H_direct");
            String Hrate=resultSet.getString("H_rate");
            int Hlayer=resultSet.getInt("H_layer");
            String C_id=resultSet.getString("C_id");
            InputStream img=resultSet.getBinaryStream("H_img");
            int Sprice=resultSet.getInt("S_price");
            results.add(new Second_House(Hid,Htype,Harea,Htime,Hdecorate,Hdirect,Hrate,Hlayer,C_id,img,Sprice));
        }
        return results;
    }


    /**
     * 查询用户发布的租房
     * @param id 用户号
     * @param first 查询的第一条记录索引（从1开始）
     * @param num 查询的条数
     * @return 房源列表
     */
    public List<Renting> FindRentById(String id,int first,int num) throws SQLException {
        String sql="select House.*,R_price " +
                "from Post,House,Renting " +
                "where Post.H_id=House.H_id and House.H_id=Renting.H_id and U_id=?";
        PreparedStatement prestm=connection.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        prestm.setString(1,id);
        ResultSet resultSet=prestm.executeQuery();
        resultSet.absolute(first-1);
        List<Renting> results=new ArrayList<>();
        while(resultSet.next()&&results.size()<num){
            String Hid=resultSet.getString("H_id");
            String Htype=resultSet.getString("H_type");
            Float Harea=resultSet.getFloat("H_area");
            Date Htime=resultSet.getDate("H_time");
            String Hdecorate=resultSet.getString("H_decorate");
            String Hdirect=resultSet.getString("H_direct");
            String Hrate=resultSet.getString("H_rate");
            int Hlayer=resultSet.getInt("H_layer");
            String C_id=resultSet.getString("C_id");
            InputStream img=resultSet.getBinaryStream("H_img");
            int Rprice=resultSet.getInt("R_price");
            results.add(new Renting(Hid,Htype,Harea,Htime,Hdecorate,Hdirect,Hrate,Hlayer,C_id,img,Rprice));
        }
        return results;
    }

    /**
     * 查询用户发布的二手房个数
     * @param id 用户号
     * @return 二手房个数,查询失败返回-1
     */
    public int FindSecondNumById(String id) throws SQLException {
        String sql="select count(*) " +
                "from Post,House,Second_House " +
                "where Post.H_id=House.H_id and House.H_id=Second_House.H_id and U_id=?";
        PreparedStatement prestm=connection.prepareStatement(sql);
        prestm.setString(1,id);
        ResultSet resultSet=prestm.executeQuery();
        if(resultSet.next()){
            return resultSet.getInt(1);
        }
        return -1;
    }


    /**
     * 查询用户发布的租房个数
     * @param id 用户号
     * @return 租房个数,查询失败返回-1
     */
    public int FindRentNumById(String id) throws SQLException {
        String sql="select count(*) " +
                "from Post,House,Renting " +
                "where Post.H_id=House.H_id and House.H_id=Renting.H_id and U_id=?";
        PreparedStatement prestm=connection.prepareStatement(sql);
        prestm.setString(1,id);
        ResultSet resultSet=prestm.executeQuery();
        if(resultSet.next()){
            return resultSet.getInt(1);
        }
        return -1;
    }




}
