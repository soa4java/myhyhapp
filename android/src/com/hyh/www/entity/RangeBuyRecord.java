package com.hyh.www.entity;

import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 范围订单记录  )
 */
@TableInfo(tableName="rangeBuyRecord")
public class RangeBuyRecord extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public double price; //j价格
	@FieldInfo
	public long uid;  //
	@FieldInfo
	public long pid;  
	@FieldInfo
	public String tradecode ;//: "描述
	@FieldInfo
	public long ctime;
	@FieldInfo
	public String  note ; 
	

	public RangeBuyRecord( JSONObject jo ){
		super( jo );
	}
	public RangeBuyRecord(){
		super();
	}
}
