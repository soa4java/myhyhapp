package com.hyh.www.entity;

import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 附近人发布半径 /想发布更远 )
 */
@TableInfo(tableName="pubRange")
public class PubRange extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public String rangetitle; //名称
	@FieldInfo
	public long range;
	@FieldInfo
	public String description ;//: "10元每月起",
	@FieldInfo
	public long sort;//: "10"

	public PubRange( JSONObject jo ){
		super( jo );
	}
	public PubRange(){
		super();
	}
}
