package com.hyh.www.user;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.contract.GezitechManager_I.OnAsynUpdateListener;
import com.gezitech.service.managers.UserManager;
import com.hyh.www.R;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 
 * @author xiaobai (修改密码)
 */
public class ChangePassworldActivity extends GezitechActivity implements OnClickListener {
	
	private ChangePassworldActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private Button changepassworld_OK;
	private EditText ed_changepassworld_yuanmima;
	private EditText ed_changepassworld_xinmima;
	private EditText ed_changepassworld_zaicishuru;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changepassworld);
		_init();
	}
	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(_this.getResources().getString(R.string.xiugaimima));
		
		
		changepassworld_OK = (Button) findViewById( R.id.changepassworld_OK );
		changepassworld_OK.setOnClickListener( this );
		
		
		ed_changepassworld_yuanmima = (EditText) findViewById( R.id.ed_changepassworld_yuanmima );
		ed_changepassworld_xinmima = (EditText) findViewById( R.id.ed_changepassworld_xinmima );		
		ed_changepassworld_zaicishuru = (EditText) findViewById( R.id.ed_changepassworld_zaicishuru );
		
		
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		
		case R.id.bt_home_msg:
			finish();
			break;
		case R.id.changepassworld_OK:
			_submitData();
			break;
		default:
			break;
		}
	}
	//提交数据
	private void _submitData(){
		String ed_changepassworld_yuanmimaVal = ed_changepassworld_yuanmima.getText().toString().trim();
		String ed_changepassworld_xinmimaVal = ed_changepassworld_xinmima.getText().toString().trim();
		String ed_changepassworld_zaicishuruVal = ed_changepassworld_zaicishuru.getText().toString().trim();
		if( ed_changepassworld_yuanmimaVal.equals("") ){
			Toast("原密码不能为空");
			return;
		}
		if( ed_changepassworld_xinmimaVal.equals("") ){
			Toast("新密码不能为空");
			return;
		}
		if( !ed_changepassworld_zaicishuruVal.equals( ed_changepassworld_xinmimaVal ) ){
			Toast("两次密码输入不一样");
			return;
		}
		if( ed_changepassworld_yuanmimaVal.equals( ed_changepassworld_xinmimaVal ) ){
			
			Toast("新密码与旧密码不能相同,请重新输入");
			return;
		}
		RequestParams params = new RequestParams();
		params.put("oldpass", ed_changepassworld_yuanmimaVal );
		params.put("newpass", ed_changepassworld_xinmimaVal );
		params.put("quenewpass", ed_changepassworld_zaicishuruVal );
		GezitechAlertDialog.loadDialog( this );
		UserManager.getInstance().updatepassword(params, new OnAsynUpdateListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				// TODO Auto-generated method stub
				GezitechAlertDialog.closeDialog();
				Toast( errorMsg );
			}
			
			@Override
			public void onUpdateDone(String id) {
				// TODO Auto-generated method stub
				GezitechAlertDialog.closeDialog();
				Toast( GezitechApplication.getContext().getString( R.string.save_userinfoPass_success )  );
				_this.finish();
			}
		});
	}
}
