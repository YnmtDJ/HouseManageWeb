package hm_bean;

import java.io.InputStream;
import java.sql.Date;

public class Second_House extends House{
    public int price;//房价

    /**
     * 构造函数
     * @param id 房源号
     * @param type 户型
     * @param area 面积
     * @param time 发布日期
     * @param decorate 装修
     * @param rate 梯户
     * @param layer 所在楼层
     * @param C_id 所属小区号
     * @param img 房源图片
     * @param price 房价(元/平米)
     */
    public Second_House(String id, String type, Float area, Date time, String decorate, String direct, String rate, int layer, String C_id,
                   InputStream img, int price){
        super(id,type,area,time,decorate,direct,rate,layer,C_id,img);
        this.price=price;
    }
}
