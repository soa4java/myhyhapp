package com.hyh.www.entity;

import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 发布范围  )
 */
@TableInfo(tableName="releasescope")
public class Releasescope extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public String rangename; //名称
	@FieldInfo
	public long range;
	@FieldInfo
	public int sort; //排序

	public Releasescope( JSONObject jo ){
		super( jo );
	}
	public Releasescope(){
		super();
	}
}
