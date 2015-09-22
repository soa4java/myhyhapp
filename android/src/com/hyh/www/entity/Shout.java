package com.hyh.www.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 喊一喊列表   )
 */
@TableInfo(tableName="shout")
public class Shout extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public String type;//	类型
	@FieldInfo
	public String rangename	;//范围单位m，列表选择获取
	@FieldInfo
	public int  maxReplycount	;//最多回复人数
	@FieldInfo
	public long activetime	;//有效时间单位秒，列表选择获取
	@FieldInfo
	public String caption	;//说明文字
	@FieldInfo
	public String litpic	;//图片，可能是多张，序列化字符串存储
	@FieldInfo
	public int replycount;//回复数量
	@FieldInfo
	public String speech	;//语音
	@FieldInfo
	public long ctime	;//创建时间
	@FieldInfo
	public int speechtime;//	语音的时间，单位秒
	@FieldInfo 
	public int typeid;
	@FieldInfo 
	public long range;//	发布范围
	@FieldInfo  
	public long  uid;//	用户
	@FieldInfo(jsonName="long")
	public double longitude;
	
	@FieldInfo(jsonName="lat")
	public double latitude;

	@FieldInfo
	public long bid;
	
	//回话列表
	public JSONArray sessionlist;
	//聊天用户
	public JSONObject user;
	
	
	public Shout( JSONObject jo ){
		super( jo );
	}
	public Shout(){
		super();
	}
}
