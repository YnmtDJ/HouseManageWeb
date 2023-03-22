package hm_bean;

import com.microsoft.sqlserver.jdbc.Geography;

/**
 * 行政区结构
 */
public class Admin_Area {
    public String id;//行政区号
    public String name;//行政区名
    public Geography loc;//行政区位置

    /**
     * 构造函数
     * @param id 行政区号
     * @param name 行政区名
     * @param loc 行政区位置
     */
    public Admin_Area(String id,String name,Geography loc){
        this.id=id;
        this.name=name;
        this.loc=loc;
    }
}
