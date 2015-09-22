package com.hyh.www.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.TableInfo;
import com.gezitech.contract.GezitechEntity_I;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 系统新闻 )
 */
public class News  implements java.io.Serializable ,GezitechEntity_I {
	private static final long serialVersionUID = 1L;
	public long id; //主键id
	public long ctime;//添加时间
	public String title;//标题
	public String content; //内容
	public String userids; //用户
}
