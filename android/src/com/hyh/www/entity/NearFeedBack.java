package com.hyh.www.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 附近人回复消息的实体 )
 */
@TableInfo(tableName="nearfeedback")
public class NearFeedBack extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public String content; 
	@FieldInfo
	public long nid;
	@FieldInfo
	public long uid;
	@FieldInfo
	public long ruid;
	@FieldInfo
	public String address ;
	@FieldInfo(jsonName="long")
	public String longs;
	@FieldInfo
	public String lat ;
	@FieldInfo
	public long ctime;
	@FieldInfo
	public JSONObject publisher;
	@FieldInfo
	public JSONObject replyer;

	public NearFeedBack( JSONObject jo ){
		super( jo );
	}
	public NearFeedBack(){
		super();
	}
}
