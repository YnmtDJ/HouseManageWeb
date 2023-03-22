package hm_bean;

import com.microsoft.sqlserver.jdbc.Geography;
import com.microsoft.sqlserver.jdbc.Geometry;
import com.microsoft.sqlserver.jdbc.spatialdatatypes.Point;
import com.microsoft.sqlserver.jdbc.spatialdatatypes.Shape;
import org.locationtech.jts.io.WKTReader;

import java.sql.Date;

/**
 * 社区结构
 */
public class Community {
    public String id;//小区号
    public String name;//小区名
    public Geography loc;//小区位置
    public float rate;//容积率
    public float green_rate;//绿化率
    public String pro_name;//物业名称
    public String type;//户型
    public Date time;//建造年代


    /**
     * 构造函数
     * @param id 小区号
     * @param name 小区名
     * @param loc 位置
     * @param rate 容积率
     * @param green_rate 绿化率
     * @param pro_name 物业名称
     * @param type 户型
     * @param time 建造年代
     */
    public Community(String id,String name,Geography loc,float rate,
                     float green_rate,String pro_name,String type,Date time){
        this.id=id;
        this.name=name;
        this.loc=loc;
        this.rate=rate;
        this.green_rate=green_rate;
        this.pro_name=pro_name;
        this.type=type;
        this.time=time;

    }
}
