package com.hyh.www.entity;

import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.FieldInfo;
import com.gezitech.basic.GezitechEntity.TableInfo;
import com.gezitech.entity.PageList;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 聊天记录内容表   )
 */
@TableInfo(tableName="chatcontent")
public class ChatContent extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo(jsonName="content")
	public String body	;//聊天记录
	@FieldInfo
	public long ctime	;//聊天时间
	@FieldInfo
	public String audiourl;//语音
	@FieldInfo
	public long audiolength;//语音时长 
	@FieldInfo
	public String city;//城市
	@FieldInfo
	public String locationAddress;//地址
	@FieldInfo
	public double latitude;//纬度
	@FieldInfo
	public String locationPic;//地图切图
	@FieldInfo
	public double longitude;//经度
	@FieldInfo
	public String province;//省
	
	@FieldInfo
	public int type; //消息分类
	
	@FieldInfo
	public long chatid; //消息关联id  (关联 chat )
	
	@FieldInfo
	public long uid ;  //等于自己uid是自己发送      不等于 是对方发送
	
	
	@FieldInfo 
	public long myuid; //属于谁的聊天  区分客户端有多个用户登录的情况
	
	@FieldInfo
	public long hyhid; //喊一喊id
	
	@FieldInfo 
	public int isfriend ;//朋友类型  1 是朋友 还是2非朋友  过度参数   3是客服

	//搜索聊天记录用到的字段
	@FieldInfo 
	public long  sender;//发送方
	@FieldInfo 
	public long  receiver;//接受方
	@FieldInfo
	public String createdate;//创建时间
	
	public PageList chatUser; //回复的聊天人的列表
	
	
	public int isunfold = 0;
	
	@FieldInfo
	public int iswelcome ;
	
	@FieldInfo
	public int unreadcount; //未读消息条数
	
	public ChatContent( JSONObject jo ){
		super( jo );
	}
	public ChatContent(){
		super();
	}
}
