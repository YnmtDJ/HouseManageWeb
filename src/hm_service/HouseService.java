package hm_service;

import hm_bean.House;
import hm_bean.Renting;
import hm_bean.Second_House;
import hm_dao.House_Dao;
import hm_db.DBUtil;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Date;
import java.util.Random;

public class HouseService {








    /**
     * 初始化房源数据
     * @param args
     * @throws SQLException
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws SQLException, FileNotFoundException {
        //插入房源数据
        Connection connection= DBUtil.getConnection();
        House_Dao houseDao=new House_Dao(connection);
        connection.setAutoCommit(true);
        //随机生成部分属性
        int num=5069;//房源数
        String[] types={"二手房","租房"};
        String[] decorate={"精装","简装","毛胚房"};
        String[] direct={"东","南","西","北","东北","东南","西北","西南"};
        Random random=new Random();
        for(int i=0;i<num;i++){
            int room=random.nextInt(1,5);
            int living_room=random.nextInt(1,3);
            int bathroom=random.nextInt(1,4);
            int ele= random.nextInt(1,5);
            int door=random.nextInt(ele,7);
            String type=types[random.nextInt(0,2)];
            String dec=decorate[random.nextInt(0,3)];
            String H_type=room+"室"+ living_room+"厅"+ bathroom+"卫";
            Float area=  (float)(16*room+30*living_room+7.5*bathroom);
            Date time = new Date(System.currentTimeMillis());
            String B_id=String.format("%09d",i);
            InputStream img = new BufferedInputStream(
                    new FileInputStream(dec+".jpg"));
            House house=null;
            if(type.equals("二手房")){
                house=new Second_House(null,H_type,area,time,dec,direct[random.nextInt(0,8)],
                        ele+"梯"+door+"户", random.nextInt(1,13), B_id,img,
                        random.nextInt(8000,40001));
            }
            else{
                house=new Renting(null,H_type,area,time,dec,direct[random.nextInt(0,8)],
                        ele+"梯"+door+"户", random.nextInt(1,13), B_id,img,
                        random.nextInt(800,5001));
            }
            houseDao.InsertHouse(house,type,B_id,"000000002");
        }

    }
}
