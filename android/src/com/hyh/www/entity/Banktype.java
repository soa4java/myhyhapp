package com.hyh.www.entity;

import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 银行的列表实体  )
 */
@TableInfo(tableName="banktype")
public class Banktype extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public String name; //名称
	@FieldInfo
	public String litpic; //银行图标
	@FieldInfo
	public int sort; //排序

	public Banktype( JSONObject jo ){
		super( jo );
	}
	public Banktype(){
		super();
	}
}
