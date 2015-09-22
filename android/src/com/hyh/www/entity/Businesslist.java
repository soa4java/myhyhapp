package com.hyh.www.entity;

import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.FieldInfo;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 商家收益  )
 */
@TableInfo(tableName="businesslist")
public class Businesslist extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public long bid;
	@FieldInfo
	public long uid; 
	@FieldInfo
	public long buyuid;
	@FieldInfo
	public double trademoney;
	@FieldInfo
	public double gotmoney;	
	@FieldInfo
	public long ctime;
	@FieldInfo
	public String invite_username;
	@FieldInfo
	public String invite_nickname;

	@FieldInfo
	public String businesslist; //商家名称
	
	@FieldInfo
	public String company_name; //商家名称
	
	
	
	public Businesslist( JSONObject jo ){
		super( jo );
	}
	public Businesslist(){
		super();
	}
}
