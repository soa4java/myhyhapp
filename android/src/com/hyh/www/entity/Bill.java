package com.hyh.www.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.TableInfo;

/**
 * 
 * @author xiaobai
 * 2014-4-19
 * @todo( 账单的记录 销售账单  购买账单  账单模版 )
 */
@TableInfo(tableName="bill")
public class Bill extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public long tradecode;  //账单编号
	@FieldInfo
	public long bid; //商家id
	@FieldInfo
	public long uid;//用户id
	@FieldInfo
	public String notes; //账单备注
	@FieldInfo
	public double money; //账单金额
	@FieldInfo
	public long activetime	; //有效时间
	
	@FieldInfo(jsonName="litpic")
	public String litpicUrl;  //	图片地址
	
	public JSONArray litpic; //	图片列表
	@FieldInfo
	public long ctime;//	添加时间
	@FieldInfo
	public int state;//	状态，0创建、1已付款、2已确定收款
	//状态，0创建、1已付款、2已确定收款 ,3已撤销,4商家已拒绝收款，5已确认服务,6退款请求,7已同意退款，8请求已作废
	@FieldInfo
	public long paytime	;//付款时间
	@FieldInfo
	public long checktime;//	确定收款时间
	@FieldInfo
	public long activechecktime	;//有效确认时间
	@FieldInfo
	public String invite_username	;//用户账号

	@FieldInfo
	public String invite_nickname;//	用户昵称
	@FieldInfo
	public String company_name;//商家名称
	@FieldInfo
	public int tradetype	;//交易类型，0是账单，1是付款

	//某个账单信息返回字段
	@FieldInfo
	public String user_name	;//用户名
	@FieldInfo
	public String user_nickname; //用户昵称
	@FieldInfo
	public String user_phone	;//用户电话
	@FieldInfo
	public String user_address	;//用户地址
	@FieldInfo
	public String company_tel	;//商家电话
	@FieldInfo
	public String company_address	;//商家地址

	@FieldInfo
	public String activetimeName;//有效时间的显示
	
	@FieldInfo
	public int alltradenumber;//	所有订单数量
	@FieldInfo
	public int collecttradenumber;//	已收款订单数量
	@FieldInfo
	public int servicetradenumber;//	服务中订单数量
	@FieldInfo
	public long surplustime	;//剩余时间
	@FieldInfo
	public long sid;//	喊一喊id


	public JSONObject info; //对方用户数据
	
	@FieldInfo
	public String username	;//对方用户名
	@FieldInfo
	public String head	; //对方头像
	@FieldInfo
	public int isfriend; //	是否为好友

	@FieldInfo 
	public String nickname; //用户昵称
	
	public long common_time;//剩余时间
	
	//账户明细
	@FieldInfo
	public String typename;//类型名称
	
	public Bill( JSONObject jo ){
		super( jo );
	}
	public Bill(){
		super();
	}
}
