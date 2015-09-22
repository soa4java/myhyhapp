package com.gezitech.entity;

import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.FieldInfo;
import com.gezitech.basic.GezitechEntity.TableInfo;
/**
 * 
 * @author xiaobai
 * 2014-4-21
 * @todo( 用户的信息 )
 * 
 */
@TableInfo(tableName="user")
public class User extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public String username;
	@FieldInfo
	public int groupId;
	@FieldInfo
	public long ctime;
	@FieldInfo
	public long utime;	
	@FieldInfo
	public String nickname;
	@FieldInfo
	public String phone;
	@FieldInfo
	public int isbusiness; //是否是商家
	@FieldInfo
	public int auth_type;  //0表示企业认证、1表示个人认证
	
	@FieldInfo
	public int online; //是否在线
	@FieldInfo
	public String city;
	@FieldInfo(jsonName="long")
	public String longitude; //经度
	@FieldInfo
	public String lat;//纬度
	@FieldInfo
	public long lasttime;//上次登录时间
	@FieldInfo
	public long thistime; //本次登录时间
	@FieldInfo
	public int isPause; //是否被暂停发布喊一喊,0是没有、1是被暂停
	@FieldInfo
	public long storetotal;//该用户推广的商家总数
	@FieldInfo
	public double personaltotal;//该用户推广的个人总数
	@FieldInfo 
	public String head; //头像
	@FieldInfo
	public String access_token;
	@FieldInfo
	public long expires_in;
	@FieldInfo
	public String refresh_token;
	@FieldInfo
	public int islogin;
	@FieldInfo
	public int uid;
	@FieldInfo
	public String realname;
	@FieldInfo
	public String IDnumber;
	@FieldInfo
	public String tel;
	@FieldInfo
	public String email;
	@FieldInfo
	public String address;
	@FieldInfo
	public String inviteCode;//邀请码
	
	@FieldInfo
	public int isfriend;//是否为朋友
	@FieldInfo
	public int isstar; //是否星标
	@FieldInfo
	public int isblacklist; //是否黑名单
	@FieldInfo 
	public String Source;//来源
	@FieldInfo
	public String notes;//备注
	
	@FieldInfo
	public int  friendtype; //好友类型，0是个人，1是商家,2星标
	
	@FieldInfo
	public long fctime;
	
	@FieldInfo
	public int istop;
	
	@FieldInfo
	public int isremind; //是否通知
	
	@FieldInfo
	public int isclose;//是否关闭聊天
	
	@FieldInfo
	public int sex;//用户性别
	
	@FieldInfo
	public int companystate;  //是否登记商家资料 1为有 0为没有
	
	
	//企业类型部分字段
	@FieldInfo(jsonName="company_typeid")
	public int companyTypeId;
	@FieldInfo(jsonName="typename")
	public String companyTypeName;
	@FieldInfo
	public String company_address;
	@FieldInfo
	public String company_name;
	@FieldInfo
	public String company_tel;
	@FieldInfo
	public String company_shopname;
	@FieldInfo
	public String company_license;
	@FieldInfo
	public String company_certificate;
	@FieldInfo
	public String company_placeshowone;
	@FieldInfo
	public String company_placeshowtwo;
	@FieldInfo
	public String company_placeshowthree;
	@FieldInfo
	public String company_userphoto;
	@FieldInfo
	public int state;//商家审核状态
	@FieldInfo
	public long  passtime;//通过时间

	@FieldInfo
	public double money; //个人余额
	@FieldInfo
	public double earn; //个人收入
	@FieldInfo
	public double pay; //个人支出
	@FieldInfo 
	public double cash;//可提现余额
	@FieldInfo
	public double coupon; //活动券余额
	
	@FieldInfo
	public double gotmoney;	//从商家提成收益
	@FieldInfo
	public double invite_money;	//从个人收益

	@FieldInfo
	public String businesstime;//	营业时间 
	
	@FieldInfo
	public int isdelivery;//	是否外送  1是外送 0不外送
	
	@FieldInfo
	public String touchname;//	联系人
	
	//账户信息的字段
	@FieldInfo
	public String account_name ;//开户姓名
	@FieldInfo
	public String account_number ;/// 收款账号
	@FieldInfo
	public String account_bankname ;// 开户行
	
	//省市区
	@FieldInfo
	public String provinces_name; //省市
	@FieldInfo
	public String provinces;
	@FieldInfo
	public String urban_name; //市区
	@FieldInfo
	public String urban;
	@FieldInfo
	public String county_name; //区县
	@FieldInfo
	public String county;
	@FieldInfo
	public String streets_name; //街道
	@FieldInfo
	public String streets;
	@FieldInfo
	public String country_name; //国家
	@FieldInfo
	public String country;
	
	public  User(JSONObject jo ){
		super( jo );
	}
	public User(){
		super();
	}
	
	
}
