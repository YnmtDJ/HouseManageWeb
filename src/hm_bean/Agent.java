package hm_bean;

/**
 * 经纪人结构
 */
public class Agent {
    public String id;//经纪人号
    public String name;//姓名

    /**
     * 构造函数
     * @param id 经纪人号
     * @param name 姓名
     */
    public Agent(String id,String name){
        this.id=id;
        this.name=name;
    }

}
