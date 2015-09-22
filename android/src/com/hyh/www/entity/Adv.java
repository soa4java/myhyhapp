package com.hyh.www.entity;

import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.FieldInfo;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( banner广告实体  )
 */
@TableInfo(tableName="adv")
public class Adv extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public String ad_name;//	广告名称
	@FieldInfo
	public String ad_litpic	;//广告图片
	@FieldInfo
	public long  ctime;//	添加时间
	@FieldInfo
	public String city;//	投放城市
	@FieldInfo
	public int isPause;//	是否暂停
	@FieldInfo
	public int sort;//	排序
	@FieldInfo
	public int isdefault;//是否默认
	
	public int drawable; //资源id

	public Adv( JSONObject jo ){
		super( jo );
	}
	public Adv(){
		super();
	}
}
