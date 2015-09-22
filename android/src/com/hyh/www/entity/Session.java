package com.hyh.www.entity;

import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.FieldInfo;
import com.gezitech.basic.GezitechEntity.TableInfo;
//会话
@TableInfo(tableName="session")
public class Session extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	
	@FieldInfo
	public long sender;//
	@FieldInfo
	public long receiver;//"81",
	@FieldInfo
	public String createdate;// "2014-12-08 18:39:45",
	@FieldInfo
	public long length;//"2",
	@FieldInfo
	public String content;// "cf",
	@FieldInfo
	public int  state;// "0",
	@FieldInfo
	public int type;// "0",
	@FieldInfo
	public String sid;//"739"
	
	public Session( JSONObject jo ){
		super( jo );
	}
	public Session(){
		super();
	}
}
