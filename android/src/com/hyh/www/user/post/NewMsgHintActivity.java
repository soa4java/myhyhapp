package com.hyh.www.user.post;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.service.GezitechService;
import com.gezitech.service.GezitechService.CallBDLocation;
import com.gezitech.service.managers.ChatManager;
import com.gezitech.service.managers.NearManager;
import com.gezitech.service.xmpp.Constant;
import com.gezitech.util.ToastMakeText;
import com.gezitech.widget.MyListView;
import com.gezitech.widget.MyListView.OnMoreListener;
import com.gezitech.widget.MyListView.OnRefreshListener;
import com.hyh.www.R;
import com.hyh.www.adapter.NearbyAdapter;
import com.hyh.www.adapter.NewMsgHintAdapter;
import com.hyh.www.entity.NearHintMsg;
import com.hyh.www.entity.NearMsg;
import com.hyh.www.entity.PubRange;
import com.loopj.android.http.RequestParams;

/**
 * 
 * @author xiaobai 2015-6-2
 * @todo( 新消息的提示 )
 */
public class NewMsgHintActivity extends GezitechActivity implements OnClickListener {
	private NewMsgHintActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private MyListView list_view;
	private NewMsgHintAdapter newMsgHintAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_msg_hint);
		__init();
	}

	private void __init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setText("清除");
		
		bt_my_post.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ChatManager.getInstance().deleteNearHintMsg(0);
				Intent intent4 = new Intent();
				intent4.setAction(Constant.LIKE_COMMENT_ACTION);
				sendBroadcast(intent4);	
				finish();
			}
		});

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("信息");

		// 消息列表
		list_view = (MyListView) findViewById(R.id.list_view);

		newMsgHintAdapter = new NewMsgHintAdapter( _this );
		list_view.setAdapter( newMsgHintAdapter );
		
		ArrayList<NearHintMsg> list = ChatManager.getInstance().getNearHintMsg();
		ArrayList<GezitechEntity_I> list_I = new ArrayList<GezitechEntity_I>();
		if( list != null && list.size()> 0 ){
			list_I.addAll( list );
		}
		newMsgHintAdapter.addList( list_I , false);
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.bt_home_msg:
			finish();
			break;
		default:
			break;
		}

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	// 返回
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case 10001:
			if (resultCode == Activity.RESULT_OK && data != null) {
				NearHintMsg nearHintMsg = (NearHintMsg) data.getExtras().getSerializable("nearHintMsg");
				newMsgHintAdapter.remove( nearHintMsg.position );
			}
			break;
		}
	}

}
