package com.hyh.www.entity;

import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 聊天记录关系表   )
 */
@TableInfo(tableName="chat")
public class Chat extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public long uid;//与谁聊天的用户id
	@FieldInfo
	public String username;//	昵称或者用户名
	@FieldInfo
	public String lastcontent	;//最后一次聊天记录
	@FieldInfo
	public long ctime	;//最后一次聊天时间
	@FieldInfo
	public String head; //头像
	@FieldInfo
	public int unreadcount; //未读消息条数
	@FieldInfo
	public int isfriend;  //是否是朋友还是私聊的聊天 1是朋友  2是非朋友 3 客户
	@FieldInfo
	public int istop; //是否置顶
	
	@FieldInfo
	public long myuid;  //当前登录用户id

	@FieldInfo
	public long hyhid; //喊一喊id

	public Chat( JSONObject jo ){
		super( jo );
	}
	public Chat(){
		super();
	}
}
