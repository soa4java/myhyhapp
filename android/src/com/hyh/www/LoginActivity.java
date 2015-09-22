package com.hyh.www;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.wechat.friends.Wechat;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynRequestFailListener;
import com.gezitech.service.GezitechService;
import com.gezitech.service.managers.SystemManager;
import com.gezitech.service.managers.UserManager;
import com.gezitech.service.xmpp.Constant;
import com.gezitech.service.xmpp.XmppConnectionManager;
import com.hyh.www.entity.ChatContent;
import com.hyh.www.entity.FieldVal;
import com.hyh.www.entity.News;
import com.hyh.www.user.BecomeShangjiaActivity;
import com.hyh.www.user.EditDataActivity;
import com.hyh.www.user.SystemMessageActivity;
import com.hyh.www.user.SystemMessageDetailActivity;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.os.Message;
import android.os.Handler.Callback;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 
 * @author xiaobai 2014-10-14
 * @todo( 登录界面 )
 */
public class LoginActivity extends GezitechActivity implements PlatformActionListener, Callback, OnClickListener {

	private LoginActivity _this = this;
	private Button login;
	private Button login_registered;
	private Button bt_my_post;
	private EditText ed_zhanghao;
	private EditText ed_passworld;
	private Button bt_home_msg;
	private Button bt_forgot_password;
	private CheckBox bt_remember_pass;
	private int rememberPass = 1;
	private String flag_global;
	private String userHead = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		rememberPass  = GezitechApplication.systemSp.getInt("rememberPass", 1 );
		// 分享初始化 //解决报没有初始化bug，尽量多次调用
		ShareSDK.initSDK(this);
		ShareSDK.setConnTimeout(5000);
		ShareSDK.setReadTimeout(10000);
		_init();
	}

	// 初始化数据
	private void _init() {

		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(_this.getResources().getString(R.string.login));

		// TODO Auto-generated method stub
		login = (Button) findViewById(R.id.Login_login);
		login.setOnClickListener(this);

		login_registered = (Button) findViewById(R.id.Login_registered);
		login_registered.setOnClickListener(this);

		ed_zhanghao = (EditText) findViewById(R.id.ed_zhanghao);
		ed_zhanghao.setText( GezitechApplication.systemSp.getString("zhanghao", "" ) );
		ed_passworld = (EditText) findViewById(R.id.ed_passworld);
		if( rememberPass >0 )
		ed_passworld.setText( GezitechApplication.systemSp.getString("passworld", "" ) );
		bt_home_msg = (Button) findViewById(R.id.bt_home_msg);
		bt_home_msg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				new NewsHint().setNewsUpdate(_this, bt_home_msg );
				
				_this.startActivity(new Intent(_this,
						SystemMessageActivity.class));
			}
		});
		bt_forgot_password = (Button) findViewById(R.id.bt_forgot_password);
		bt_forgot_password.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(_this, ForgetActivity.class);
				_this.startActivity(intent);
			}
		});
		bt_remember_pass = (CheckBox) findViewById(R.id.bt_remember_pass );
		if( rememberPass > 0 ) bt_remember_pass.setChecked( true );
		else bt_remember_pass.setChecked( false );
		bt_remember_pass.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if( isChecked ){
					rememberPass = 1;
				}else{
					rememberPass = 0;
				}
				Editor edit = GezitechApplication.systemSp.edit();
				edit.putInt("rememberPass", rememberPass );
				edit.commit();
			}
		});
		new NewsHint().getNewsUpdate(_this, bt_home_msg );
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.SYSTEM_REQUEST );
		registerReceiver(receiver, filter);
		
		_jumpActivity( this.getIntent() );
		
		//微信
		( (ImageButton) findViewById( R.id.ib_chat ) ).setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				authorize(new Wechat(_this));
			}
		});
		
	}
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
		_jumpActivity( intent );
		
		
	}
	private void _jumpActivity( Intent intent ){
		flag_global = intent.getStringExtra("flag");
		String title = intent.getStringExtra("title");
		String key_type = intent.getStringExtra("key_type");
		String key_value = intent.getStringExtra("key_value");
		//$key_type  1打开活动，2打开网页
	    //$key_value  对应活动id，对应网页链接
		if( flag_global !=null && flag_global.equals("notification") && title != null && !title.equals("") && key_value!=null && !key_value.equals("") && key_type !=null && !key_type.equals("")  ){
		

			if( key_type.equals("1") ){//打开系统消息
				
				loadNewsDetail( Long.parseLong( key_value ) );
				
			}
		}
	}
	//加载新闻的详情
	private void loadNewsDetail( long id ){
		GezitechAlertDialog.loadDialog( this );
		SystemManager.getInstance().getannouncementdetails( id ,  new OnAsynGetOneListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				// TODO Auto-generated method stub
				GezitechAlertDialog.closeDialog();
				Toast( errorMsg );
			}
			
			@Override
			public void OnGetOneDone(GezitechEntity_I entity) {
				// TODO Auto-generated method stub
				GezitechAlertDialog.closeDialog();
				if( entity != null ){
					Intent intent = new Intent( _this, SystemMessageDetailActivity.class );
					
					Bundle bundle = new Bundle();
					
					bundle.putSerializable("news", (News)entity );
					intent.putExtras( bundle );
					
					_this.startActivity( intent );
					
				}
				
			}
		});
	}

	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.Login_login:

			final String ed_zhanghao_val = ed_zhanghao.getText().toString().trim();
			final String ed_passworld_val = ed_passworld.getText().toString().trim();
			if (ed_zhanghao_val.equals("")) {
				Toast("帐号不能为空");
				return;
			}
			if (ed_passworld_val.equals("")) {
				Toast("密码不能为空");
				return;
			}
			
			Editor edit = GezitechApplication.systemSp.edit();
			edit.putString("zhanghao", ed_zhanghao_val );
			edit.putString("passworld", ed_passworld_val );
			edit.commit();
			
			GezitechAlertDialog.loadDialog(this);
			/*GezitechApplication.getInstance().getBDLocation(
					new BDLocationListener() {

						@Override
						public void onReceiveLocation(final BDLocation arg0) {*/
							UserManager.getInstance().Login(ed_zhanghao_val,
									ed_passworld_val,
									new OnAsynRequestFailListener() {

										@Override
										public void OnAsynRequestFail(
												String errorCode,
												String errorMsg) {
											GezitechAlertDialog.closeDialog();
											if (errorCode.equals("-1")) {
												Toast(errorMsg);
												return;
											} else {
												
												//XmppConnectionManager.getInstance().login();
												//更新经度纬度
												GezitechService.getInstance().longitude();
												user  = GezitechService.getInstance().getCurrentLoginUser( _this );
												//如果是商家就要完成商家的资料
												if( user.isbusiness> 0  && user.companystate<=0 ){
													Intent intent = new Intent(_this, EditDataActivity.class);
													intent.putExtra("from", 1);
													_this.startActivity( intent );//未提交
													
												}else{//普通的用户直接进入主页
													_this.startActivity(new Intent(
															_this,
															ZhuyeActivity.class));
												}
												
												_this.finish();
											}
										}
									});
							//GezitechApplication.getInstance().setBDLocation( arg0 );

					//	}
				//	});

			break;
		case R.id.Login_registered:
			_this.startActivity(new Intent(_this, RegisteredActivity.class));
			_this.finish();
			break;
		default:
			break;
		}
	}
	// 新系统消息的接受广播
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constant.SYSTEM_REQUEST.equals(action)) {// 返回解接受的消息
				bt_home_msg.setBackgroundResource(R.drawable.common_msg_yes );
				new NewsHint().getNewsUpdate(_this, bt_home_msg, true );

			} 
		}

	};
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver( receiver );
	};
	private static final int MSG_USERID_FOUND = 1;
	private static final int MSG_LOGIN = 2;
	private static final int MSG_AUTH_CANCEL = 3;
	private static final int MSG_AUTH_ERROR= 4;
	private static final int MSG_AUTH_COMPLETE = 5;
	private void authorize(Platform plat) {	
		GezitechAlertDialog.loadDialog( _this );
		if(plat.isValid()) {//已经登录
			String userId = plat.getDb().getUserId();
			if (!TextUtils.isEmpty(userId)) {
				UIHandler.sendEmptyMessage(MSG_USERID_FOUND, this);
				login( plat, null );
				return;
			}
		}
		plat.setPlatformActionListener(this);
		plat.SSOSetting( false );
		plat.showUser(null);
	}
	private void login(Platform platform, HashMap<String, Object> userInfo) {
		Message msg = new Message();
		msg.what = MSG_LOGIN;
		msg.obj = platform;
		UIHandler.sendMessage(msg, this);
	}
	@Override
	public void onComplete(Platform platform, int action,
			HashMap<String, Object> res) {
		if (action == Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, this);
			login( platform, res);
		}
	}
	@Override
	public void onError(Platform platform, int action, Throwable t) {
		if (action == Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, this);
		}
		t.printStackTrace();
	}
	@Override
	public void onCancel(Platform platform, int action) {
		if (action == Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, this);
		}
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		GezitechAlertDialog.closeDialog();
		switch(msg.what) {
			case MSG_USERID_FOUND: {
				//Toast.makeText(this, "用户信息已存在，正在跳转登录操作…", Toast.LENGTH_SHORT).show();
			}
			break;
			case MSG_LOGIN: {
				
				//Toast.makeText(this, "使用%s帐号登录中…", Toast.LENGTH_SHORT).show();
				
				
				Platform platform = (Platform) msg.obj;
				RequestParams params = new RequestParams();
				params.put("platform_uid",   platform.getDb().getUserId()  );
				//Toast.makeText( this, platform.getName() , Toast.LENGTH_SHORT).show();
				userHead =  platform.getDb().getUserIcon();
			
				params.put("platform",   platform.getName() );
				params.put("third_oauth_token",  platform.getDb().getToken() );
				params.put("third_oauth_token_secret",   platform.getDb().getTokenSecret()  );
				params.put("nickname",  platform.getDb().getUserName() ); 
				ThirdLogin( params );
				
				
			}
			break;
			case MSG_AUTH_CANCEL: {
				Toast("阿哦~授权取消");
			}
			break;
			case MSG_AUTH_ERROR: {
				Toast("阿哦~授权错误");
			}
			break;
			case MSG_AUTH_COMPLETE: {
				//Toast("阿哦~授权完成");
			}
			break;
		}
		return false;
	}
	private void ThirdLogin( RequestParams params ){
		GezitechAlertDialog.loadDialog( _this );
		UserManager.getInstance().thirdPartCheck(params, new OnAsynRequestFailListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				GezitechAlertDialog.closeDialog();
				if (errorCode.equals("-1")) {
					Toast(errorMsg);
					return;
				} else {
					//更新经度纬度
					GezitechService.getInstance().longitude();
					user  = GezitechService.getInstance().getCurrentLoginUser( _this );
					//跳转到资料编辑
					Intent inten = new Intent();
					GezitechApplication.systemSp.edit().putString("userHead", userHead ).commit();
					if( FieldVal.value( user.phone ).equals("")  ){//微信登陆是否填写手机号码
						
						//微信增加邀请码界面
						inten.setClass(_this, WechatDataActivity.class);
						inten.putExtra("userHead", userHead);
						
						
						
					}else{//普通的用户直接进入主页
						
						inten.setClass(_this, ZhuyeActivity.class);
						
					}
					_this.startActivity( inten );
					
					_this.finish();
				}
			}
		});
	}

}
