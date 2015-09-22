
package com.hyh.www.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 国家实体  )
 */
@TableInfo(tableName="country")
public class Country extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public long range; //距离范围
	
	@FieldInfo
	public String rangename; //名称描述
	
	@FieldInfo
	public long ctime; //创建时间
	
	@FieldInfo
	public long sort ; //创建时间
	
	
	public Country( JSONObject jo ){
		super( jo );
	}
	public Country(){
		super();
	}
}
