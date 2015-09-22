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

//修改备注
public class NotesActivity extends GezitechActivity implements OnClickListener {
	private NotesActivity _this = this;
	private String notes;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_title;
	private EditText et_notes;
	private long fid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notes);
		
		Intent  intent = _this.getIntent();
			
		notes = intent.getStringExtra("notes");
		fid = intent.getLongExtra("fid", 0);
		
		_init();
	}
	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setText("完成");
		

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

	    tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText( "修改备注" );
		
		
		et_notes = (EditText) _this.findViewById( R.id.et_notes );
		et_notes.setText( notes );
		
		bt_my_post.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String et_notesVal = et_notes.getText().toString().trim();
				
				if( et_notesVal.equals( notes ) || et_notesVal.equals("") ){
					return;
				}
				
				notes = et_notesVal ;
				
				GezitechAlertDialog.loadDialog( _this );
				FriendManager.getInstance().setnotes(fid, notes, new OnAsynRequestListener() {
					
					@Override
					public void OnAsynRequestFail(String errorCode, String errorMsg) {
						// TODO Auto-generated method stub
						GezitechAlertDialog.closeDialog();
						Toast( errorMsg );
					}
					
					@Override
					public void OnAsynRequestCallBack(Object o) {
						// TODO Auto-generated method stub
						GezitechAlertDialog.closeDialog();
						returnData();
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
			returnData();
			break;

		default:
			break;
		}
	}
	private void returnData(){
		Intent intent = new Intent();
		intent.setAction( notes );
		this.setResult(1001,intent);
		
		finish();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		returnData();
	}
	
}
