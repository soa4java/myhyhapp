package com.gezitech.util;

import com.gezitech.basic.GezitechApplication;
import com.gezitech.service.GezitechService;
import com.gezitech.service.managers.ChatManager;
import com.gezitech.service.xmpp.XmppConnectionManager;
import com.hyh.www.LoginActivity;
import com.hyh.www.widget.YMDialog2;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

public class ToastMakeText {
	
	public Activity InsThis = null;
	
	public ToastMakeText( Activity activity ){
		this.InsThis = activity;
	}
	/***
     * 线程安全弹出信息框
     */
	Handler toastHandler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			if(msg.obj == null)return;
			String text = msg.obj.toString();
			
			
			if( text.equals("令牌错误") || text.equals("令牌过期")  ){
				//判断是否异地登陆
				final YMDialog2 ymdialog =  new YMDialog2(  InsThis );
				ymdialog.setHead("提示")
				.setHintMsg( text.equals("令牌过期") ? "很久没有登录了,重新登录" : "您的账户在其它设备上已登录,点击确定重新登录")
				.setConfigText("确定")
				.setCloseButton( new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ymdialog.dismiss();
						//情况当前用户friend用户信息
						try{
							ChatManager.getInstance().deleteContacts(); //删除当前用户的联系列表
							GezitechService.getInstance().clearCurrentUser();
						}catch( Exception ex){}
						Intent intent = new Intent(InsThis, LoginActivity.class);		
						InsThis.startActivity( intent );
						
						XmppConnectionManager.getInstance().disconnect();
						GezitechApplication.connection = null;
						
					/*	
						Intent chatServer = new Intent(_this, IMChatService.class);
						_this.stopService(chatServer);
						*/
						GezitechService.getInstance().unbindBackgroundService( GezitechApplication.getContext() );
						
						GezitechService.getInstance().exitApp( InsThis );
					}
				});
			}else{
				android.widget.Toast.makeText(InsThis, text, msg.arg1).show();
			}
			
			
		};
	};
	public void Toast(CharSequence text,int duration){
		toastHandler.sendMessage(ThreadUtil.createMessage(duration, 0, text));
	}
	//线程安全
	public void Toast(CharSequence text){	
		this.Toast(text, 1000);
	}	
	public void Toast(int rid){	
		this.Toast(InsThis.getString(rid));
	}	
	public void Toast(int rid,int duration){	
		this.Toast(InsThis.getString(rid),duration);
	}
}
