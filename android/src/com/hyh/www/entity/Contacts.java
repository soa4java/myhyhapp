package com.hyh.www.entity;

import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.FieldInfo;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 通讯录实体 )
 */
@TableInfo(tableName="contacts")
public class Contacts extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public String username;
	@FieldInfo
	public long usertype; //用户类型    1 星标  2 商家  3 个人好友  4 客服  5非好友的缓存资料
	@FieldInfo
	public long ctime;
	@FieldInfo
	public String nickname;
	@FieldInfo
	public String head;
	@FieldInfo
	public int isLine; //是否有线条
	@FieldInfo
	public int isblacklist; //是否加入黑名单
	
	@FieldInfo
	public long uid;
	
	@FieldInfo
	public long fid;
	@FieldInfo
	public String notes; //备注

	public  Contacts(JSONObject jo ){
		super( jo );
	}
	public Contacts(){
		super();
	}
}
