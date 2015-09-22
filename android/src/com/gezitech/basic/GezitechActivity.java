package com.gezitech.basic;
import java.util.LinkedList;
import android.app.*;

import com.gezitech.entity.User;
import com.gezitech.service.GezitechService;
import com.gezitech.service.lbs.AppUtils;
import com.gezitech.service.managers.ChatManager;
import com.gezitech.service.xmpp.IMChatService;
import com.gezitech.service.xmpp.XmppConnectionManager;
import com.gezitech.util.ThreadUtil;
import com.hyh.www.LoginActivity;
import com.hyh.www.WelcomeActivity;
import com.hyh.www.widget.YMDialog;
import com.hyh.www.widget.YMDialog2;
import com.umeng.analytics.MobclickAgent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
/**
 * 
 * @author xiaobai
 * activity 基类
 */
public class GezitechActivity extends Activity {
	protected GezitechActivity InsThis = this;
	public User user = null;
	private static LinkedList<GezitechActivity> activities = new LinkedList<GezitechActivity>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GezitechService.getInstance().appendActivity(this);		
		
		long uid = GezitechApplication.systemSp.getLong("uid", 0);
		if( uid > 0 ){
			user  = GezitechService.getInstance().getCurrentLoginUser( this );
		}
	}
	@SuppressWarnings("unused")
	@Override
	public void onBackPressed() {
		if( false ){
			/*final MyAlertDialog dialog = new MyAlertDialog( InsThis );
			dialog.setTitle("确定离开客户端?").setPositiveButton("确定", new OnClickListener(){
				@Override
				public void onClick(View v) {
					InsThis.finish();
					GezitechService.getInstance().exitApp(InsThis);//finish activity;
					Intent startMain = new Intent(Intent.ACTION_MAIN);
		           startMain.addCategory(Intent.CATEGORY_HOME);
		           startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		           startActivity(startMain);
		           System.exit(0);
				}
			}).setNegativeButton("取消", new OnClickListener(){

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					return;
				}
				
			});	*/
			//隐藏而不退出程序
			Intent i= new Intent(Intent.ACTION_MAIN);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addCategory(Intent.CATEGORY_HOME);
			startActivity(i); 
		}else {
			super.onBackPressed();
		}
		
	}
	@Override
	public void finish() {
		super.finish();
	}

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }
    /***
     * 线程安全弹出信息框
     */
	Handler toastHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.obj == null)return;
			String text = msg.obj.toString();
			if( text.equals("令牌错误") || text.equals("令牌过期")  ){
				//判断是否异地登陆
				final YMDialog2 ymdialog =  new YMDialog2( InsThis );
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
		this.Toast(getString(rid));
	}	
	public void Toast(int rid,int duration){	
		this.Toast(getString(rid),duration);
	}
	
	public void exit(){
		while(activities.size()>0){									
			if(!activities.getLast().isFinishing()){
				activities.getLast().finish();																			
			}																	
			activities.removeLast();
		}
	}
	public void onResume() {
	    super.onResume();
	    //umeng统计
		MobclickAgent.onResume(this);
		//if ( AppUtils.isAppOnForeground()  ) {  
			//app 从后台唤醒，进入前台  
		   // GezitechApplication.isActive = true;  
		    //判断xmpp是否链接
		    //判断是否已经链接
		    if( user != null ){ 
		    	if(  GezitechApplication.connection == null || ( !GezitechApplication.connection.isConnected() ) || (  XmppConnectionManager.getInstance().getConnection() !=null && !XmppConnectionManager.getInstance().getConnection().isConnected() ) ){
		    		XmppConnectionManager.getInstance().login();
				}
		    	
		    }
		    //是否重新启动app
			/*if( GezitechApplication.runtime>0 && (System.currentTimeMillis() - GezitechApplication.runtime) > 30*60*1000  ){
				Intent intent = new Intent(this, WelcomeActivity.class);		
				startActivity( intent );
				GezitechService.getInstance().exitApp( this );
			}*/
	//	}
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
/*		if (!AppUtils.isAppOnForeground()) {  
            //app 进入后台  
			//全局变量isActive = false 记录当前已经进入后台  
			GezitechApplication.isActive = false;   
			//GezitechApplication.runtime = System.currentTimeMillis();   
		}*/
	}
	public void onPause() {
	    super.onPause();
	  //umeng统计
	   MobclickAgent.onPause(this);
	}
	//测试代码
	public boolean Test(){
		SharedPreferences sp = InsThis.getSharedPreferences("gezitechTest", 0);
		int testCount = sp.getInt("testCount", 0);
		if( testCount>100 ){
			Toast("糟糕，领导还没发工资，程序造反了");
			return false;
		}else{
			SharedPreferences.Editor editor = sp.edit();
			editor.putInt("testCount", ++testCount );
			editor.commit();
			return true;
		}
		
	}
}
