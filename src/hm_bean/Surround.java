package hm_bean;

import com.microsoft.sqlserver.jdbc.Geography;

/**
 * 周边结构
 */
public class Surround {
    public String id;//周边号
    public String name;//周边名称
    public String type;//周边类型
    public Geography loc;//周边位置

    /**
     * 构造函数
     * @param id 周边号
     * @param name 周边名称
     * @param type 周边类型
     * @param loc 周边位置
     */
    public Surround(String id,String name,String type,Geography loc){
        this.id=id;
        this.name=name;
        this.type=type;
        this.loc=loc;
    }
}
