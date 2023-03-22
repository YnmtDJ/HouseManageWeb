package hm_bean;

import java.io.InputStream;
import java.sql.Date;

public class House {
    public String id;//房源号
    public String type;//户型
    public Float area;//面积
    public Date time;//发布时间
    public String decorate;//装修
    public String direct;//朝向
    public String rate;//梯户
    public int layer;//所在楼层
    public String C_id;//所属小区号
    public InputStream img;//房源图片

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
     */
    public House(String id,String type,Float area,Date time,String decorate,String direct, String rate,int layer,String C_id,
                 InputStream img){
        this.id=id;
        this.type=type;
        this.area=area;
        this.time=time;
        this.decorate=decorate;
        this.direct=direct;
        this.rate=rate;
        this.layer=layer;
        this.C_id=C_id;
        this.img=img;
    }
}
