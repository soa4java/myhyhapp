package com.hyh.www.user;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.contract.GezitechManager_I.OnAsynUpdateListener;
import com.gezitech.service.managers.SystemManager;
import com.hyh.www.R;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author xiaobai (意见反馈)
 */

public class FeedbackActivity extends GezitechActivity implements OnClickListener {
	
	private FeedbackActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_title;
	private EditText ed_feedback_content;
	private Button feedback_tijiao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		_init();
	}

	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(_this.getResources().getString(
				R.string.yijianfankui));

		ed_feedback_content = (EditText) findViewById( R.id.ed_feedback_content) ;
		
		feedback_tijiao = (Button) findViewById( R.id.feedback_tijiao );
		
		feedback_tijiao.setOnClickListener( this );
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bt_home_msg:
			finish();
			break;
		case R.id.feedback_tijiao: //提交意见反馈
			
			_submitData();		
			
			break;
		}
	}
	//提交意见反馈
	private void _submitData(){
		
		String ed_feedback_contentVal = ed_feedback_content.getText().toString().trim();
		if( ed_feedback_contentVal.equals("") ){
			Toast( GezitechApplication.getContext().getString(R.string.fankuineirong) );
			return;
		}
		RequestParams params = new RequestParams();
		params.put("content", ed_feedback_contentVal );
		GezitechAlertDialog.loadDialog( this );
		//添加意见反馈
		SystemManager.getInstance().addFeedback(params, new OnAsynUpdateListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				// TODO Auto-generated method stub
				GezitechAlertDialog.closeDialog();
				Toast( errorMsg );
			}
			
			@Override
			public void onUpdateDone(String msg) {
				GezitechAlertDialog.closeDialog();
				ed_feedback_content.setText("");
				Toast( msg  );
			}
		});
		
	}
}
