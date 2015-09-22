package com.hyh.www.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 提示消息的存储   )
 */
@TableInfo(tableName="nearHintMsg")
public class NearHintMsg extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public long uid; 
	@FieldInfo
	public String nickname;
	@FieldInfo
	public String head ;
	@FieldInfo
	public String content;
	@FieldInfo
	public int isLike;
	@FieldInfo
	public long ctime;
	@FieldInfo
	public String nearbycontent;
	@FieldInfo
	public String nearbyimage;
	@FieldInfo
	public long nid;
	@FieldInfo
	public int isRead; //是否已读
	
	public int position ;// 索引
	
	
//	@property NSNumber *uid;//用户uid
//	@property NSString *nickname;//[string]:昵称
//	@property NSString *head;
//	@property NSString *content;
//	@property NSNumber *isLike;
//	@property NSNumber *ctime;//发布时间
//	@property NSString *nearbycontent; //截取部分内容
//	@property NSString *nearbyimage;// 取第一张
//	@property NSNumber *nid; //附近人id
	
	public NearHintMsg( JSONObject jo ){
		
		super( jo );
	}
	public NearHintMsg(){
		super();
	}
}
