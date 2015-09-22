package com.hyh.www;

import java.util.Date;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import cn.sharesdk.framework.ShareSDK;

//import com.baidu.android.pushservice.CustomPushNotificationBuilder;
//import com.baidu.android.pushservice.PushConstants;
//import com.baidu.android.pushservice.PushManager;
import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.entity.User;
import com.gezitech.service.GezitechService;
import com.gezitech.service.xmpp.XmppConnectionManager;
import com.gezitech.util.IOUtil;
import com.hyh.www.entity.FieldVal;
import com.hyh.www.user.BecomeShangjiaActivity;
import com.hyh.www.user.EditDataActivity;
import com.igexin.sdk.PushManager;


import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
//欢迎界面
public class WelcomeActivity extends GezitechActivity{
	private WelcomeActivity _this = this;
	private ImageView iv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IOUtil.init();//初始化缓存图片文件的目录
		_this.setContentView(R.layout.activity_welcome);
		//测试代码
		/*if( !Test() ){
			return;
		}*/
		//百度推送
		//_initBaiduPush();
		//初始化个推推送
		PushManager.getInstance().initialize(this.getApplicationContext());
		
		//umeng间隔多少秒视为一次启动   这里是60000 毫秒
		MobclickAgent.setSessionContinueMillis(60000);
		//umeng更新
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
		
		//分享初始化
		ShareSDK.initSDK(this);
		ShareSDK.setConnTimeout(5000);
		ShareSDK.setReadTimeout(10000);
		
		
		//开启服务进行初始化工作
		GezitechService.getInstance().longitude();
		GezitechService.getInstance().configuration();
		
		iv=(ImageView)findViewById(R.id.iv_welcome);
		iv.setVisibility(View.INVISIBLE);
		final AlphaAnimation bb = new AlphaAnimation(1.0f, 1.0f);
        bb.setDuration(1500);//设置动画的持续时间
        bb.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				}
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				}
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				//未登录跳转到登录
        		//User user = GezitechService.getInstance().getCurrentLoginUser( _this );
        		Intent inten = new Intent();
        		Date t = new Date();
        		long currTime = t.getTime()/1000;
        		if( user!= null && user.expires_in > currTime ){//已经登录
        			
        			//推送数据回传
            		GezitechService.getInstance().pushInfo( false );
            		
        			//如果是商家就要完成商家的资料
					if( user.isbusiness> 0  && user.companystate<=0 ){
						inten.setClass(_this, EditDataActivity.class);
						inten.putExtra("from", 1);
						
					}else if( FieldVal.value( user.phone ).equals("")  ){//微信登陆是否填写手机号码
						
						inten.setClass(_this, WechatDataActivity.class); 
						inten.putExtra("userHead", GezitechApplication.systemSp.contains("userHead") ? GezitechApplication.systemSp.getString("userHead", "") : ""  );
						
					}else{//普通的用户直接进入主页
						
						inten.setClass(_this, ZhuyeActivity.class);
						
					}
        			
        		}else{
        			inten.setClass(_this, LoginActivity.class);
        		}
        		//已登录跳转到个人中心界面
        		_this.startActivity(inten);
            	_this.finish();
			}
		});
        AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
        aa.setDuration(1500);
        aa.setAnimationListener(new AnimationListener() {

            public void onAnimationStart(Animation animation) {
            }
            public void onAnimationRepeat(Animation animation) {
            }
            public void onAnimationEnd(Animation animation) {
                iv.startAnimation(bb);
            }
        });
        iv.setVisibility(View.VISIBLE);
        iv.startAnimation(aa);
        //加载配置
        
	}

	/* @Title: setStartState 
	* @Description: TODO(设置是否已经启动过状态) 
	* @param @param state    设定文件 
	* @return void    返回类型 
	* @throws
	* 
	 */
	public void setStartState( int state){
		SharedPreferences sp = _this.getSharedPreferences("setStartState", 0); 
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt("state", state);
		editor.commit();
	}
	/***
	 * 
	* @Title: getStartState 
	* @Description: TODO(获取是否已经启动的状态值) 
	* @param @return    设定文件 
	* @return int    返回类型 
	* @throws
	 */
	public int getStartState(){
		SharedPreferences sp = _this.getSharedPreferences("setStartState", 0);
		return sp.getInt("state", 0);
	}
	public static String getDeviceInfo(Context context) {
	    try{
	      org.json.JSONObject json = new org.json.JSONObject();
	      android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
	          .getSystemService(Context.TELEPHONY_SERVICE);
	  
	      String device_id = tm.getDeviceId();
	      
	      android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	          
	      String mac = wifi.getConnectionInfo().getMacAddress();
	      json.put("mac", mac);
	      
	      if( TextUtils.isEmpty(device_id) ){
	        device_id = mac;
	      }
	      
	      if( TextUtils.isEmpty(device_id) ){
	        device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
	      }
	      
	      json.put("device_id", device_id);
	      
	      return json.toString();
	    }catch(Exception e){
	      e.printStackTrace();
	    }
	  return null;
	}
	/**
	 * 初始化百度推送
	 * */
//	private void _initBaiduPush(){
//		
//		//验证
//		PushManager.startWork(_this.getApplicationContext(),
//				PushConstants.LOGIN_TYPE_API_KEY, this.getResources().getString(R.string.api_key) );	
//		
//		 Resources resource = this.getResources();
//	        String pkgName = this.getPackageName();
//
//		//设置自定义的通知样式，如果想使用系统默认的可以不加这段代码
//		 CustomPushNotificationBuilder cBuilder = new CustomPushNotificationBuilder(
//	                getApplicationContext(), resource.getIdentifier(
//	                        "notification_custom_builder", "layout", pkgName),
//	                resource.getIdentifier("notification_icon", "id", pkgName),
//	                resource.getIdentifier("notification_title", "id", pkgName),
//	                resource.getIdentifier("notification_text", "id", pkgName));
//	        cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
//	        cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND
//	                | Notification.DEFAULT_VIBRATE);
//	        cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
//	       /* Uri sound = Uri.parse("android.resource://" + pkgName + "/" +R.raw.ok_1 ); 
//	        cBuilder.setNotificationSound( sound  );*/
//	        cBuilder.setLayoutDrawable(resource.getIdentifier(
//	                "logo_144", "drawable", pkgName));
//	        PushManager.setNotificationBuilder(this, 1, cBuilder);
//	}
}
