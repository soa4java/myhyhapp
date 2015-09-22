package com.hyh.www.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 企业类型实体  )
 */
@TableInfo(tableName="companytype")
public class Companytype extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public String typename; //名称
	@FieldInfo
	public int sort; //排序
	@FieldInfo(jsonName="rangeid")
	public long  range;//	发布范围id
	@FieldInfo(jsonName="activetimeid")
	public long activetimeName;//	有效时间id
	@FieldInfo
	public int answernumber;//	回复人数
	@FieldInfo(jsonName="rangename")
	public String  range_rangename	;//范围名称
	@FieldInfo(jsonName="range")
	public long range_range;//	实际范围
	@FieldInfo(jsonName="activetimeName")
	public String range_activetimeName;//	有效时间名称
	@FieldInfo(jsonName="activetime")
	public long range_activetime;//	实际有效时间
	@FieldInfo
	public String rangeoption;  //范围
	@FieldInfo
	public String  activetimeoption; //时间
	@FieldInfo
	public long  pid; //分类id
	
	public JSONArray  childtype; //二级分类
	
	public int isselected;

	public Companytype( JSONObject jo ){
		super( jo );
	}
	public Companytype(){
		super();
	}
}
