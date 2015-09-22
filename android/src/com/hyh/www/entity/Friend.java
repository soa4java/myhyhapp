package com.hyh.www.entity;

import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.FieldInfo;
import com.gezitech.basic.GezitechEntity.TableInfo;
/**
 * 
 * @author xiaobai
 * 2014-4-21
 * @todo( 用户资料存储   )
 * 
 */
@TableInfo(tableName="friend")
public class Friend extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public String username;
	@FieldInfo
	public int groupId;//用户组 
	@FieldInfo
	public long ctime;
	@FieldInfo
	public String nickname;
	@FieldInfo
	public String head;
	@FieldInfo
	public int isLine; //是否有线条
	
	@FieldInfo
	public long uid;  //获取好友的请求列表用到此参数
	
	@FieldInfo
	public long fid; //获取好友的请求列表用到此参数
	
	@FieldInfo
	public long utime;	 //最近登录时间
	@FieldInfo
	public String phone;
	@FieldInfo
	public int isbusiness; //是否是商家
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
	public String access_token;
	@FieldInfo
	public long expires_in;
	@FieldInfo
	public String refresh_token;
	@FieldInfo
	public int islogin;
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
	public String company_name;//	好友商家名称
	@FieldInfo
	public String company_type;//	好友商家类型
	@FieldInfo
	public String company_address;//	好友公司地址
	@FieldInfo
	public String company_tel;//公司电话
	
	@FieldInfo
	public int sex; //性别
	@FieldInfo
	public int hasactivitysession ; //标示有没有活动的会话，0为没有 1为有
	
	public  Friend(JSONObject jo ){
		super( jo );
	}
	public Friend(){
		super();
	}
	
	
}
