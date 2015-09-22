package com.hyh.www.chat;

public class ChatUtils {
	//获取类型对应的字符串描述  
	public  static String getTypeStr(String content, int type) {

		String str = "";
		switch (type) {
		case 0:
			str = content;
			break;
		case 1:
			str = "[图片]";
			break;
		case 2:
			str = "[语音]";
			break;
		case 3:
			str = "[位置]";
			break;
		case 4:
			str = "[名片]";
			break;
		case 5://账单部分
			str = "[账单]";
			break;
		case 6:
			str = "[账单已支付]";
			break;
		case 7: //创建付款部分
			str = "[已收款]";
			break;
		case 8:
			str = "[付款]";
			break;
		case 9:
			str = "[喊一喊]";
			break;
		case 10: //喊一喊取消不能与客户对话的消息
			str = content;
			break;
		case 11: //用户撤销付款;
			str = "[用户撤销付款]";
			break;
		case 12: //商家拒绝收款;
			str = "[商家拒绝收款]";
			break;
		case 13: //用户申请退款;
			str = "[用户申请退款]";
			break;
		case 14: //用户确认服务
			str = "[用户确认服务]";
			break;
		}

		return str;
	}
	public static String getOrderStr( int type ){
		String str = "";
		switch (type) {
			case 11:
				str = "用户撤销付款";
				break;
			case 12:
				str = "商家拒绝收款";
				break;
			case 13:
				str = "用户申请退款";
				break;
			case 14:
				str = "用户确认服务";
				break;
			case 7:
				str = "账单已收款";
				break;
		}
		return str;
	}
}
