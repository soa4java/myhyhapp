package com.hyh.www.entity;

import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 有效时间  )
 */
@TableInfo(tableName="validtimelist")
public class Validtimelist extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public String activetimeName; //有效时间显示，如（3天）
	@FieldInfo
	public long activetime;//有效时间 单位秒
	@FieldInfo
	public int sort; //排序

	public Validtimelist( JSONObject jo ){
		super( jo );
	}
	public Validtimelist(){
		super();
	}
}
