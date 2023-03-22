package hm_bean;

public class Building {
    public String id;//栋号
    public float height;//层高
    public int num;//楼层数
    public String name;//单元号

    /**
     * 构造函数
     * @param id 栋号
     * @param height 层高
     * @param num 楼层数
     * @param name 单元号
     */
    public Building(String id,float height,int num,String name){
        this.id=id;
        this.height=height;
        this.num=num;
        this.name=name;
    }

}
