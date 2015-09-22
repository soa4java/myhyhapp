package com.hyh.www.entity;

import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 联系我们的配置  )
 */
@TableInfo(tableName="configuration")
public class Configuration extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public int system_id; 
	@FieldInfo
	public String variable;
	@FieldInfo
	public String value;
	@FieldInfo
	public String name;
	@FieldInfo
	public String descript;
	@FieldInfo
	public String ctime;

	public Configuration( JSONObject jo ){
		super( jo );
	}
	public Configuration(){
		super();
	}
}
