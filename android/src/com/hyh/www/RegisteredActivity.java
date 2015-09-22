package com.hyh.www;

import java.util.ArrayList;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.config.Conf;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynRequestFailListener;
import com.gezitech.contract.GezitechManager_I.OnAsynUpdateListener;
import com.gezitech.entity.PageList;
import com.gezitech.service.managers.SystemManager;
import com.gezitech.service.managers.UserManager;
import com.gezitech.service.xmpp.Constant;
import com.gezitech.service.xmpp.XmppConnectionManager;
import com.gezitech.widget.ExtendViewFlipper;
import com.gezitech.widget.ExtendViewFlipper.OnViewFlipperFackFunction;
import com.gezitech.widget.RemoteImageView;
import com.hyh.www.entity.Adv;
import com.hyh.www.entity.Configuration;
import com.hyh.www.user.EditDataActivity;
import com.hyh.www.user.SystemMessageActivity;
import com.loopj.android.http.RequestParams;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * 
 * @author xiaobai
 * (注册页面)
 */
public class RegisteredActivity extends GezitechActivity implements OnClickListener{
	
	private RegisteredActivity _this = this;
	private Button registered;
	private Button registered_account;
	private ExtendViewFlipper pager_slide;
	private LinearLayout pager_control;
	private PageList advList;
	private ImageView[] contorls;
	private Animation in;
	private Animation out;
	private Button bt_my_post;
	private Button bt_send_verification_code;
	private EditText ed_verification_code;
	private EditText ed_phonenumber;
	private EditText ed_enter_password;
	private EditText ed_input_again;
	private EditText ed_invite_code;
	private Button bt_home_msg;
	private RadioGroup rg_user_type;
	private int isbusiness = 0 ;
	private TextView tv_service;
	private TextView xuantian;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_this.setContentView(R.layout.activity_registered);
		
		_init();
	}
	private void _init() {
		
		bt_my_post = (Button) _this.findViewById( R.id.bt_my_post );
		bt_my_post.setVisibility( View.GONE );
		TextView tv_title = (TextView) findViewById( R.id.tv_title );
		tv_title.setText( _this.getResources().getString( R.string.register) );
		
		// TODO Auto-generated method stub
		registered=(Button)findViewById(R.id.Registered_registered);
		registered.setOnClickListener(this);
		
		registered_account=(Button)findViewById(R.id.Registered_zhanghao);
		registered_account.setOnClickListener(this);
		
		//选填优惠券
		xuantian = (TextView) findViewById(R.id.xuantian );
		Configuration config = SystemManager.getInstance().getConfiguration( 1001 ); 
		String value = this.getResources().getString( R.string.default_coupons );
		if( config != null ){
			value = config.value;
		}
		xuantian.setText( String.format(this.getResources().getString( R.string.xuantian ),  value  )  );
		
		
		
		//发送验证码
		ed_verification_code = (EditText) findViewById( R.id.ed_verification_code );
		ed_phonenumber = (EditText) findViewById( R.id.ed_phonenumber );
		bt_send_verification_code = ( Button ) findViewById( R.id.bt_send_verification_code );
		if( GezitechApplication.getTimeDeff() < Conf.timedeff ){
			bt_send_verification_code.setText( ( Conf.timedeff - GezitechApplication.getTimeDeff() ) + "秒后");
			bt_send_verification_code.setEnabled( false );
			mHandler.postDelayed( countdown, 1000);
		}
		bt_send_verification_code.setOnClickListener( new OnClickListener() {		
			@Override
			public void onClick(View v) {
				String ed_phonenumber_val = ed_phonenumber.getText().toString().trim();
				if( ed_phonenumber_val.equals("") || ed_phonenumber_val.length() != 11 ){
					Toast("手机号码格式错误");
					return;
				}else{
					bt_send_verification_code.setText("正在发送");
					bt_send_verification_code.setEnabled( false );
					UserManager.getInstance().phonecode(ed_phonenumber_val,0, new OnAsynUpdateListener() {						
						@Override
						public void OnAsynRequestFail(String errorCode, String errorMsg) {
							Toast( errorMsg );
							bt_send_verification_code.setEnabled( true );
							bt_send_verification_code.setText( _this.getString( R.string.fasongyanzhengma ) );
						}
						@Override
						public void onUpdateDone(String id) {
							bt_send_verification_code.setText(Conf.timedeff+"秒后");
							GezitechApplication.verifyTime = System.currentTimeMillis();
							mHandler.postDelayed( countdown, 1000);
						}
					});
				}
			}
		});
		//密码
		ed_enter_password = (EditText) _this.findViewById( R.id.ed_enter_password );
		//再次密码
		ed_input_again = (EditText) _this.findViewById( R.id.ed_input_again );
		//邀请码
		ed_invite_code = ( EditText ) _this.findViewById( R.id.ed_invite_code );
		
		bt_home_msg = (Button) findViewById( R.id.bt_home_msg );
		bt_home_msg.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new NewsHint().setNewsUpdate(_this, bt_home_msg );
				_this.startActivity( new Intent (_this, SystemMessageActivity.class ) );
			}
		} );
		//用户类型的选择
		rg_user_type = (RadioGroup) _this.findViewById( R.id.rg_user_type );
		rg_user_type.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if( checkedId == R.id.rb_business_no ){
					isbusiness = 0;
				}else if( checkedId == R.id.rb_business_yes ){
					isbusiness = 1;
				}
			}
		});
		//服务协议
		tv_service = (TextView) _this.findViewById( R.id.tv_service );
		tv_service.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				_this.startActivity( new Intent( _this, ServiceActivity.class ) );
			}
		});
		
		//初始化广告
		_initAdv();
		new NewsHint().getNewsUpdate(_this, bt_home_msg );
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.SYSTEM_REQUEST );
		registerReceiver(receiver, filter);
		
	}
	private Handler mHandler = new Handler();
	private Runnable  countdown = new Runnable()
	{
		public void run()
		{
			if( GezitechApplication.getTimeDeff() > Conf.timedeff ){
				mHandler.removeCallbacks( countdown );
				bt_send_verification_code.setEnabled( true );
				bt_send_verification_code.setText( _this.getString( R.string.fasongyanzhengma ) );
			}else{
				bt_send_verification_code.setText( ( Conf.timedeff - GezitechApplication.getTimeDeff() )  + "秒后");
				mHandler.postDelayed(countdown, 1000);
			}
		}
	};
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.Registered_registered://注册
			
			String ed_phonenumber_val = ed_phonenumber.getText().toString().trim();
			if( ed_phonenumber_val.equals("") || ed_phonenumber_val.length() != 11 ){
				Toast("手机号码格式错误");
				return;
			}
			String ed_verification_code_val = ed_verification_code.getText().toString().trim();
			
			if( ed_verification_code_val.equals("") ){
				Toast("验证码格式错误");
				return;
			}
			
			String ed_enter_password_val = ed_enter_password.getText().toString().trim();
			if( ed_enter_password_val.equals("") ){
				Toast("密码不能为空");
				return;
			}
			if( ed_enter_password_val.length()<6 || ed_enter_password_val.length()>20){
				Toast("密码长度6-20位");
				return;
			}
			String ed_input_again_val = ed_input_again.getText().toString().trim();
			if( ed_input_again_val.equals("") ){
				Toast("两次输入密码不一样");
				return;
			}
			String ed_invite_code_val = ed_invite_code.getText().toString().trim();
			
			final RequestParams params = new RequestParams();
			params.put("username", ed_phonenumber_val);
			params.put("password", ed_enter_password_val);
			params.put("oldpassword", ed_input_again_val);
			params.put("phone", ed_phonenumber_val);
			params.put("code", ed_verification_code_val);
			params.put("inviteCode", ed_invite_code_val);
			params.put("isbusiness", isbusiness );
			GezitechAlertDialog.loadDialog( this );
			GezitechApplication.getInstance().getBDLocation( new BDLocationListener() {
				
				@Override
				public void onReceiveLocation(BDLocation arg0) {
					GezitechApplication.getInstance().setBDLocation( arg0 );
					params.put("long", arg0.getLongitude()+"" );
					params.put("lat", arg0.getLatitude() +"");
					params.put("city", arg0.getCity() );
					UserManager.getInstance().register(params, new OnAsynRequestFailListener() {
						
						@Override
						public void OnAsynRequestFail(String errorCode, String errorMsg) {
							GezitechAlertDialog.closeDialog();
							if( errorCode.equals("1") ){
								//_this.startActivity(new Intent(_this, ZhuyeActivity.class));
								XmppConnectionManager.getInstance().login();
								//跳转到资料编辑
								Intent intent = new Intent(_this, EditDataActivity.class);
								intent.putExtra("from", 1);
								_this.startActivity( intent );
								_this.finish();
							}else{
								Toast( errorMsg );
							}
						}
					});
				}
			});
			break;
			
	    case R.id.Registered_zhanghao://已有帐号
	    	_this.startActivity(new Intent(_this, LoginActivity.class));
			_this.finish();	
			break;
		default:
			break;
		}
	}
	//初始化广告
	private void _initAdv()	{	
		pager_slide = ( ExtendViewFlipper ) findViewById( R.id.vf_ad );
		pager_control = ( LinearLayout ) findViewById( R.id.pager_control );		
		
		advList = SystemManager.getInstance().getClientAdvList();
		if( advList == null || advList.size()<= 0 ){
			Adv advDefault = new Adv();
			advDefault.isdefault = 1;
			advDefault.drawable = R.drawable.sy_ad_01;	
			advList.add( advDefault );
		}
		
		advData();
		GezitechApplication.getInstance().getBDLocation(  new BDLocationListener() {
			
			@Override
			public void onReceiveLocation(BDLocation arg0) {
				GezitechApplication.getInstance().setBDLocation( arg0 );
				SystemManager.getInstance().advlist(1, 5, arg0.getCity(), new OnAsynGetListListener() {
					
					@Override
					public void OnAsynRequestFail(String errorCode, String errorMsg) {}
					@Override
					public void OnGetListDone(ArrayList<GezitechEntity_I> list) {
						if( list!=null && list.size()> 0 ){
							advList.clear();
							advList = (PageList)list;
							mHandler.removeCallbacks( advtime );
							advData();
						}
					}
				});			
			}
		});
	}
	private void advData(){
		pager_slide.removeAllViews();
		pager_control.removeAllViews();
		contorls = new ImageView[ advList.size() ];
		DisplayMetrics dm = this.getResources().getDisplayMetrics();
		for( int i = 0; i<advList.size(); i++ ){
			//追加小原点
			contorls[i] = new ImageView( this );
			if( i == 0 ){
				contorls[i].setImageDrawable( _this.getResources().getDrawable( R.drawable.sy_ad_dot_selected ) );						
			}else{
				contorls[i].setImageDrawable( _this.getResources().getDrawable( R.drawable.sy_ad_dot_normal ) );					
			}
			pager_control.addView( contorls[i] );		
			//追加广告的内容
			View view = LayoutInflater.from( this ).inflate(R.layout.item_adv, null);			
			RemoteImageView iv_ad = (RemoteImageView)view.findViewById( R.id.iv_ad );
			//iv_ad.setBackgroundResource( R.drawable.sy_ad_01 );
			Adv adv = (Adv) advList.get(i);
			if( adv.isdefault > 0 ){
				iv_ad.setBackgroundDrawable( _this.getResources().getDrawable(  adv.drawable  ) );
			}else{
				iv_ad.setImageUrl(adv.ad_litpic,false,true);
			}
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( dm.widthPixels, (int)(dm.density*114) );
			view.setLayoutParams( lp );
			pager_slide.addView( view );
		}
		_viewFlipper();
	}
	
	//幻灯片的切换
	private void _viewFlipper() {
		//初始化参数
		in = AnimationUtils.loadAnimation(this, R.anim.left_in);
		out = AnimationUtils.loadAnimation(this, R.anim.left_out);
		mHandler.postDelayed(advtime,  Conf.pauseTime);
	    pager_slide.setOnViewFlipperFackFunction( new OnViewFlipperFackFunction(){
			@Override
			public void selectStyle(  boolean isPre  ) {
				_this.setectStyle(  isPre );
			}
			@Override
			public void countDownTimerAction(boolean isAction) { //false 取消  true 开始
				if( isAction ){
					mHandler.postDelayed(advtime, Conf.pauseTime );
				}else {
					mHandler.removeCallbacks( advtime );
				}
			}    	
	    });
	}
	private Runnable  advtime = new Runnable()
	{
		public void run()
		{
			pager_slide.setInAnimation(in);
        	pager_slide.setOutAnimation(out);
        	_this.setectStyle( true );
        	pager_slide.showNext();
        	mHandler.postDelayed(advtime,  Conf.pauseTime);
		}
	};
   //圆点选中样式
   public void setectStyle(  boolean isPre  ){	   
	    int index = pager_slide.getDisplayedChild();
	   	//圆点的选中
	    if( isPre ){
	    	index = index+1; 
	    	if( index >contorls.length-1 ) index = 0;
	    }else{
	    	index = index-1; 
	    	if( index<0) index = contorls.length-1;
	    }
	    for( int i =0; i<contorls.length; i++){
	    	if( i == index ){
	    		contorls[i].setImageDrawable( _this.getResources().getDrawable( R.drawable.sy_ad_dot_selected ) );	
	    	}else{
	    		contorls[i].setImageDrawable( _this.getResources().getDrawable( R.drawable.sy_ad_dot_normal ) );	
	    	}
	    }	   	
   }
   @Override
  	protected void onDestroy() {
  		super.onDestroy();
  		mHandler.removeCallbacks( countdown );
		mHandler.removeCallbacks( advtime );
		unregisterReceiver( receiver );
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
}
