package hm_bean;

/**
 * 用户数据结构
 * @author 胡楠
 *
 */
public class User {
	public String U_id;//用户号
	public String U_name;//用户名
	public String U_password;//密码
	public String U_type;//用户类别
	
	/**
	 * 构造函数
	 * @param Id 用户号
	 * @param Name 用户名
	 * @param Password 密码
	 * @param Type 用户类型
	 */
	public User(String Id,String Name,String Password,String Type) {
		U_id=Id;
		U_name=Name;
		U_password=Password;
		U_type=Type;
	}
	
	
	
	
	
}
