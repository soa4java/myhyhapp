package com.hyh.www.entity;
//值的判断类
public class FieldVal {
	
	public static String value(String val){
		if( val == null || val.equals("") || val.equals("null") ){
			return "";
		}else{
			return val;
		}
	} 
	
	//性别的返回
	public static String getSex(int sex){
		
		if( sex == 0 ){
			return "请选择";
		}else if( sex == 1 ){
			return "男";
		}else if( sex == 2 ){
			return "女";
		}
		return "请选择";
	}
	//外卖的返回
	
	public static String getSend(int send){
		
		if( send == 0 ){
			return "否";
		}else if( send == 1 ){
			return "是";
		}
		return "请选择";
	}
}
