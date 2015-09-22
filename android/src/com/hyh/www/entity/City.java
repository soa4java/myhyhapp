package com.hyh.www.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 城市实体  )
 */
@TableInfo(tableName="city")
public class City extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public long parentId; //父类id
	
	@FieldInfo
	public String name; //名称描述
	
	@FieldInfo
	public int level ; //级别

	public City( JSONObject jo ){
		super( jo );
	}
	public City(){
		super();
	}
}
