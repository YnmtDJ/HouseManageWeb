package hm_bean;

public class SubUser extends User{
    public int value;
    public SubUser(String Id,String Name,String Password,String Type,int value){
        super(Id,Name,Password,Type);
        this.value=value;
    }

    public static void main(String[] args){
       System.out.println("胡楠");


    }

}
