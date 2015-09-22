package com.hyh.www.user;

import java.util.ArrayList;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.service.managers.UserManager;
import com.hyh.www.R;
import com.hyh.www.ZhuyeActivity;
import com.hyh.www.chat.ChatActivity;
import com.hyh.www.entity.Configuration;
import com.hyh.www.entity.Friend;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
/**
 * 
 * @author xiaobai (联系我们)
 */
public class ContactsActivity extends GezitechActivity implements OnClickListener {
	
	
	private ContactsActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_title;
	private ArrayList<GezitechEntity_I> config = new ArrayList<GezitechEntity_I>();
	private TextView tv_contacts_title;
	private TextView tv_contacts_company;
	private TextView tv_contacts_phonenumber;
	private TextView tv_contacts_chuanzhennumber;
	//private TextView tv_contacts_youxiangnumber;
	//private TextView tv_contacts_dizhinumber;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		Bundle extras = this.getIntent().getExtras();
		config = ( ArrayList<GezitechEntity_I> )( extras.getSerializable("config") );
		_init();
	}
	
	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);
		
		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);
		
		TextView tv_title = (TextView) findViewById( R.id.tv_title );
		tv_title.setText( _this.getResources().getString( R.string.lianxiwomen ) );
		
		//版本
		tv_contacts_title = (TextView) findViewById( R.id.tv_contacts_title );
		tv_contacts_company = (TextView) findViewById( R.id.tv_contacts_company );
		
		tv_contacts_phonenumber = (TextView) findViewById( R.id.tv_contacts_phonenumber );
		tv_contacts_chuanzhennumber = (TextView) findViewById( R.id.tv_contacts_chuanzhennumber );
		tv_contacts_chuanzhennumber.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				loadKefuList();
			}
		});
		//tv_contacts_youxiangnumber = (TextView) findViewById( R.id.tv_contacts_youxiangnumber );
		
		//tv_contacts_dizhinumber = (TextView) findViewById( R.id.tv_contacts_dizhinumber );
		
		//获取版本
		Context context = GezitechApplication.getContext();
		PackageInfo pi = null;
		try {
			pi = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String version = "1.0";
		if( pi != null) version = pi.versionName;
		tv_contacts_title.setText( this.getString( R.string.hyh_banben )+"v"+version );
		
		for(int i = 0 ; i< config.size(); i++){
			Configuration cg = (Configuration) config.get(i);
			final String value = cg.value;
			switch( cg.system_id ){
			case 30: //公司名称
				tv_contacts_company.setText( value );
				break;
			case 31://电话
				tv_contacts_phonenumber.setText( value );
				tv_contacts_phonenumber.setOnClickListener( new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+value));  
		                startActivity(intent);						
					}
				});
				break;
			case 32://传真
				//tv_contacts_chuanzhennumber.setText( value );
				break;
			case 33://邮箱
				//tv_contacts_youxiangnumber.setText( value );
				break;
			case 34://地址
				//tv_contacts_dizhinumber.setText( "  "+value );
				break;
			}
			
		}
		
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		
		case R.id.bt_home_msg:
			finish();
			break;

		default:
			break;
		}
	}
	public void loadKefuList(){
		GezitechAlertDialog.loadDialog( _this );
		UserManager.getInstance().getkefu( new OnAsynGetOneListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				GezitechAlertDialog.closeDialog();
				Toast( errorMsg );
			}
			
			@Override
			public void OnGetOneDone(GezitechEntity_I entity) {
				GezitechAlertDialog.closeDialog();
				Friend item = (Friend) entity;
				//跳转到聊天界面
				Intent intent = new Intent(_this, ChatActivity.class );
				intent.putExtra("uid", item.id);
				
				intent.putExtra("username", item.nickname == null || item.nickname.equals("null") || item.nickname.equals("") ? item.username : item.nickname );
				
				intent.putExtra( "head", item.head );
				
				intent.putExtra("isfriend", 3 );
				intent.putExtra("isbusiness", item.isbusiness );
				_this.startActivity( intent );
				
				
			}
		});
	}

}
