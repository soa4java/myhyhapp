package com.hyh.www.session;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynRequestListener;
import com.gezitech.service.managers.FriendManager;
import com.gezitech.util.StringUtil;
import com.gezitech.widget.MyListView;
import com.gezitech.widget.MyListView.OnMoreListener;
import com.hyh.www.R;
import com.hyh.www.adapter.FriendRequestOrSearchAdapter;

//投诉
public class UsercomplainActivity extends GezitechActivity implements OnClickListener {
	private UsercomplainActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_title;
	private long fid;
	private EditText ed_feedback_content;
	private Button feedback_tijiao;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_complain);
		
		Intent  intent = _this.getIntent();
			
		fid = intent.getLongExtra("fid", 0);
		
		_init();
	}
	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility( View.GONE);
		

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

	    tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText( "投诉举报" );
		
		
		ed_feedback_content = (EditText) _this.findViewById( R.id.ed_feedback_content );
		
		feedback_tijiao = (Button) _this.findViewById( R.id.feedback_tijiao );
		feedback_tijiao.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String ed_feedback_contentVal = ed_feedback_content.getText().toString().trim();
				if( ed_feedback_contentVal.equals("") ) {
					
					Toast("请输入投诉原因");
					return;
				}
				GezitechAlertDialog.loadDialog( _this );
				FriendManager.getInstance().usercomplain(fid, ed_feedback_contentVal,  new OnAsynRequestListener() {
					
					@Override
					public void OnAsynRequestFail(String errorCode, String errorMsg) {
						// TODO Auto-generated method stub
						GezitechAlertDialog.closeDialog();
						Toast( errorMsg );
					}
					
					@Override
					public void OnAsynRequestCallBack(Object o) {
						GezitechAlertDialog.closeDialog();
						Toast( "投诉成功,我们会尽快处理" );
						_this.finish();
					}
				});
				
			}
		});
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bt_home_msg:
			_this.finish();
			break;

		default:
			break;
		}
	}	
}
