package com.hyh.www.entity;

import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 支付实体  )
 */
@TableInfo(tableName="pay")
public class Pay extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public String sign;
	public Pay( JSONObject jo ){
		super( jo );
	}
	public Pay(){
		super();
	}
}
