package com.hyh.www.entity;

import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 类型价格列表 )
 */
@TableInfo(tableName="priceRange")
public class PriceRange extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public double price; //j价格
	@FieldInfo
	public long days;  //天数
	@FieldInfo
	public String description ;//: "描述
	@FieldInfo
	public long sort;//: "10"

	public PriceRange( JSONObject jo ){
		super( jo );
	}
	public PriceRange(){
		super();
	}
}
