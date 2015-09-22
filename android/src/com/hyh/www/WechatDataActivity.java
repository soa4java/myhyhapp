package com.hyh.www;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.contract.GezitechManager_I.OnAsynRequestFailListener;
import com.gezitech.entity.User;
import com.gezitech.service.managers.SystemManager;
import com.gezitech.service.managers.UserManager;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.hyh.www.entity.Configuration;
import com.hyh.www.user.EditDataActivity;
import com.loopj.android.http.RequestParams;

//微信登陆邀请码填写
public class WechatDataActivity extends GezitechActivity{
	private WechatDataActivity _this = this;
	private Button bt_my_post;
	private TextView xuantian;
	private EditText ed_phone;
	private EditText ed_yaoqingma;
	private GezitechDBHelper<User> db;
	private String userHead="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_this.setContentView(R.layout.activity_wechat_data);
		db = new GezitechDBHelper<User>(User.class);
		Intent intent = getIntent();
		userHead = intent.hasExtra("userHead") ? intent.getStringExtra("userHead") : "";
		_init();
		
	}
	private void _init() {
		bt_my_post = (Button) _this.findViewById( R.id.bt_my_post );
		bt_my_post.setVisibility( View.GONE );
		TextView tv_title = (TextView) findViewById( R.id.tv_title );
		tv_title.setText( "基本信息" );
		
		findViewById(R.id.bt_home_msg).setVisibility( View.GONE );
		
		//选填优惠券
		xuantian = (TextView) findViewById(R.id.xuantian );
		Configuration config = SystemManager.getInstance().getConfiguration( 1001 ); 
		String value = this.getResources().getString( R.string.default_coupons );
		if( config != null ){
			value = config.value;
		}
		xuantian.setText( String.format(this.getResources().getString( R.string.xuantian ),  value  )  );
		
		ed_phone = (EditText) findViewById(R.id.ed_phone);
		ed_yaoqingma = (EditText) findViewById( R.id.ed_yaoqingma );
		
		//下一步
		findViewById(R.id.btn_editdata).setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String phoneVal = ed_phone.getText().toString().trim();
				
				if( phoneVal.equals("") || phoneVal.length()<=0 ){
					Toast("手机号码不能为空");
					return;
				}
				
				String yaoqingmaVal = ed_yaoqingma.getText().toString().trim();
				
				RequestParams params = new RequestParams();
				params.put("phone", phoneVal );
				params.put("inviteCode", yaoqingmaVal );
				GezitechAlertDialog.loadDialog( _this );
				UserManager.getInstance().thirdPartAddPhone(params,  new OnAsynRequestFailListener() {
					
					@Override
					public void OnAsynRequestFail(String errorCode, String errorMsg) {
						GezitechAlertDialog.closeDialog();
						if( errorCode.equals("1") ){
							
							user.phone  = phoneVal ;	
							db.save( user );
							
							Intent inten = new Intent();
							inten.setClass(_this, EditDataActivity.class);
							inten.putExtra("from", 2);
							inten.putExtra("userHead", userHead);
							_this.startActivity( inten );
							
							_this.finish();
							
						}else{
							Toast( errorMsg );
						}
					}
				});
				
			}
		});
		
		
	}
	
}
