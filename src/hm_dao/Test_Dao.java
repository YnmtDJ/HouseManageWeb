package hm_dao;

import hm_bean.*;
import hm_db.DBUtil;



import java.sql.SQLException;
import java.util.List;

/**
 * 数据库接口测试
 */
public class Test_Dao {

    public static Admin_Area_Dao adminAreaDao=new Admin_Area_Dao(DBUtil.getConnection());
    public static Agent_Dao agentDao=new Agent_Dao(DBUtil.getConnection());
    public static Building_Dao buildingDao=new Building_Dao(DBUtil.getConnection());
    public static Community_Dao communityDao=new Community_Dao(DBUtil.getConnection());
    public static Comment_Dao commentDao=new Comment_Dao(DBUtil.getConnection());
    public static House_Dao houseDao=new House_Dao(DBUtil.getConnection());
    public static Surround_Dao surroundDao=new Surround_Dao(DBUtil.getConnection());
    public static User_Dao userDao=new User_Dao(DBUtil.getConnection());


    public static void main(String[] args) throws SQLException {
        //FindUserById
        System.out.println("FindUserById Test:\n>U_id:000000002");
        User user=userDao.FindUserById("000000002");
        System.out.println("user name:"+user.U_name+" user type:"+user.U_type+"\n");


        //InsertUser
        System.out.println("InsertUser Test:\nU_name:HuNan U_type:管理员 U_password:666");
        user=new User(null,"HuNan","666","管理员");
        System.out.println("Insert Result:"+userDao.InsertUser(user)+"\n");

        //FindSecondBySurround
        System.out.println("FindSecondBySurround Test:\nS_id=000004014");
        Surround surround=surroundDao.FindSurroundByName("中山公园").get(0);
        System.out.println("Find "+houseDao.FindSecondBySurround(surround).size()+" SecondHouse.\n");

        //FindRentBySurround
        System.out.println("FindRentBySurround Test:\nS_id=000004014");
        System.out.println("Find "+houseDao.FindRentBySurround(surround).size()+" Renting.\n");

        //FindSecond_House
        System.out.println("FindSecond_House Test:\nAdminArea:江汉区  MinPrice:20000 first:1 num:30");
        Admin_Area adminArea=adminAreaDao.FindAdminByCId("000000000");
        System.out.println("Find "+houseDao.FindSecond_House(adminArea,null,20000,null,
                null,null,null,1,30).size()+" SecondHouse.\n");

        //FindRenting
        System.out.println("FindRenting Test:\nAdminArea:江汉区  MinPrice:2000 first:1 num:30");
        System.out.println("Find "+houseDao.FindRenting(adminArea,null,2000,null,
                null,null,null,1,30).size()+" Renting.\n");

        //FindHouseById
        System.out.println("FindHouseById Test:\nH_id=000000009");
        House house=houseDao.FindHouseById("000000009");
        System.out.println("type:"+house.type+" area:"+house.area+" date:"+house.time+"\n");

        //InsertUAH
        System.out.println("InsertUAH Test:\nU_id:000000002 A_id:000000000 H_id:000000009");
        System.out.println("Insert Result:"+houseDao.InsertUAH("000000002","000000000","000000009")+"\n");

        //FindSurroundByHouse
        System.out.println("FindSurroundByHouse Test:\n"+"H_id:"+house.id);
        System.out.println("Find "+surroundDao.FindSurroundByHouse(house).size()+"Surround.\n");

        //FindSimilarSecondByPrice
        System.out.println("FindSimilarSecondByPrice Test:\nH_id:000000579");
        Second_House secondHouse=(Second_House) houseDao.FindHouseById("000000579");
        System.out.println("Find "+houseDao.FindSimilarSecondByPrice(secondHouse,4).size()+"SecondHouse.\n");

        //FindSimilarRentByPrice
        System.out.println("FindSimilarRentByPrice Test:\nH_id:000000009");
        Renting renting=(Renting) houseDao.FindHouseById("000000009");
        System.out.println("Find "+houseDao.FindSimilarRentByPrice(renting,4).size()+"Renting.\n");

        //FindSimilarSecondByType
        System.out.println("FindSimilarSecondByType Test:\nH_id:000000579");
        System.out.println("Find "+houseDao.FindSimilarSecondByType(secondHouse,4).size()+"SecondHouse.\n");

        //FindSimilarRentByType
        System.out.println("FindSimilarRentByType Test:\nH_id:000000009");
        System.out.println("Find "+houseDao.FindSimilarRentByType(renting,4).size()+"Renting.\n");

        //InsertComment
        System.out.println("InsertComment Test:\nU_id:000000002 H_id:000000009 content:nice!");
        System.out.println("Insert Result:"+commentDao.InsertComment("000000002","000000009","nice!")+"\n");

        //FindUserCommentByH_id
        System.out.println("FindUserCommentByH_id Test:\nH_id:000000009");
        List<User> userList = null;
        List<String> commentList = null;
        System.out.println("Find Result:"+commentDao.FindUserCommentsByH_id("000000009",userList,commentList)+"\n");

        //FindCommunityByName
        System.out.println("FindCommunityByName Test:\nC_name:花园");
        System.out.println("Find "+communityDao.FindCommunityByName("花园").size()+" community.\n");

        //FindCommunityById
        System.out.println("FindCommunityById Test:\nC_id:000000000");
        System.out.println("Find Results:"+communityDao.FindCommunityById("000000000")+"\n");


        //FindSurroundByCommunity
        System.out.println("FindSurroundByCommunity Test:\nC_id:000000000");
        Community community=communityDao.FindCommunityById("000000000");
        System.out.println("Find "+surroundDao.FindSurroundByCommunity(community).size()+"surround.\n");

        //FindSecondByCId
        System.out.println("FindSecondByCId Test:\nC_id:000000000");
        System.out.println("Find "+houseDao.FindSecondByCId("000000000",4).size()+" SecondHouse.\n");

        //FindRentByCId
        System.out.println("FindRentByCId Test:\nC_id:000000000");
        System.out.println("Find "+houseDao.FindRentingByCId("000000000",4).size()+" Renting.\n");

        //FindSecondById
        System.out.println("FindSecondById Test:\nId:000000002");
        System.out.println("Find "+houseDao.FindSecondById("000000000",1,30).size()+" SecondHouse.\n");

        //FindRentById
        System.out.println("FindRentById Test:\nId:000000002");
        System.out.println("Find "+houseDao.FindRentById("000000000",1,30).size()+" Renting.\n");

        //UpdateHouse
        System.out.println("UpdateHouse Test:\nH_id:000000009 H_type:3室1厅3卫 price:3000");
        System.out.println("Update Result:"+houseDao.UpdateHouse("000000009","3室1厅3卫",null,null,
                null,null,null,null,null,3000)+"\n");

        //DeleteHouse
        System.out.println("DeleteHouse Test:\nH_id:000000026");
        System.out.println("Delete Result:"+houseDao.DeleteHouse("000000026")+"\n");

        //FindSecondAVGPriceByAdmin
        System.out.println("FindSecondAVGPriceByAdmin Test:\nA_id:"+adminArea.id);
        System.out.println("Result "+adminAreaDao.FindSecondAVGPriceByAdmin(adminArea)+"\n");

        //FindRentAVGPriceByAdmin
        System.out.println("FindRentAVGPriceByAdmin Test:\nA_id:"+adminArea.id);
        System.out.println("Result "+adminAreaDao.FindRentAVGPriceByAdmin(adminArea)+"\n");

        //FindSecondNumByAdmin
        System.out.println("FindSecondNumByAdmin Test:\nA_id:"+adminArea.id);
        System.out.println("Result "+adminAreaDao.FindSecondNumByAdmin(adminArea)+"\n");

        //FindRentNumByAdmin
        System.out.println("FindRentNumByAdmin Test:\nA_id:"+adminArea.id);
        System.out.println("Result "+adminAreaDao.FindRentNumByAdmin(adminArea)+"\n");

        //FindSecondAVGPriceByC_id
        System.out.println("FindSecondAVGPriceByC_id Test:\nC_id:000000593");
        System.out.println("Result "+communityDao.FindSecondAVGPriceByCommunity("000000593")+"\n");

        //FindRentAVGPriceByC_id
        System.out.println("FindRentAVGPriceByC_id Test:\nC_id:000000593");
        System.out.println("Result "+communityDao.FindRentAVGPriceByCommunity("000000593")+"\n");

        //FindSecondNumByC_id
        System.out.println("FindSecondNumByC_id Test:\nC_id:000000593");
        System.out.println("Result "+communityDao.FindSecondNumByC_id("000000593")+"\n");


        //FindRentNumByC_id
        System.out.println("FindRentNumByC_id Test:\nC_id:000000593");
        System.out.println("Result "+communityDao.FindRentingNumByC_id("000000593")+"\n");

    }



}
