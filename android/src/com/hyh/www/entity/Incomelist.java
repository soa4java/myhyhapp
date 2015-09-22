package com.hyh.www.entity;

import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 个人记录推广列表  )
 */
@TableInfo(tableName="incomelist")
public class Incomelist extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public long uid; 
	@FieldInfo
	public long invite_uid;
	@FieldInfo
	public double invite_money;
	@FieldInfo
	public long coupon_id;	
	@FieldInfo
	public long ctime;
	@FieldInfo
	public String invite_username;
	@FieldInfo
	public String invite_nickname;

	public Incomelist( JSONObject jo ){
		super( jo );
	}
	public Incomelist(){
		super();
	}
}
