package com.hyh.www.user.post;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

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
import com.hyh.www.entity.NearHintMsg;
import com.hyh.www.entity.NearMsg;
import com.hyh.www.entity.PubRange;
import com.hyh.www.nearby.CommentBoxDialog;
import com.loopj.android.http.RequestParams;

/**
 * 
 * @author xiaobai 2015-6-2
 * @todo( 附近人详情 )
 */
public class NearDetailActiviy extends GezitechActivity implements OnClickListener {
	private NearDetailActiviy _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private MyListView list_view;
	private NearbyAdapter nearbyAdapter;
	private int page = 1, pageSize = 15;
	private NearHintMsg nearHintMsg =  null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_post_msg);
		nearHintMsg  = (NearHintMsg)_this.getIntent().getExtras().getSerializable("nearHintMsg");
		__init();
	}

	private void __init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility( View.GONE );

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("附近人详情");

		// 附近人的列表
		list_view = (MyListView) findViewById(R.id.list_view);

		nearbyAdapter = new NearbyAdapter(_this);
		nearbyAdapter.isShowHead(true);
		nearbyAdapter.setListView(list_view);
		list_view.setAdapter(nearbyAdapter);
		// 初始化下来加载
		list_view.onClickRefreshComplete();
		list_view.footerShowState( 2 );
		// 下拉刷新
		list_view.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				page = 1;
				_initGetData();

			}
		});
		_initGetData();
		
	}


	// 获取数据的初始化
	private void _initGetData() {

		RequestParams params = new RequestParams();
		params.put("page", page);
		params.put("pageSize", pageSize);
		params.put("nid", nearHintMsg.nid );
		NearManager.getInstance().getUserPubNearBy(params,
				new OnAsynGetListListener() {

					@Override
					public void OnAsynRequestFail(String errorCode,
							String errorMsg) {
						new ToastMakeText(_this).Toast(errorMsg);
						list_view.onRefreshComplete();
						list_view.footerShowState(1);
						if (errorCode.equals("-1")) {
							_this.Toast(errorMsg);
						}
					}

					@Override
					public void OnGetListDone(ArrayList<GezitechEntity_I> list) {
						int count = list.size();
						if (page == 1) {// 下拉刷新 首次加载
							if (count > 0)
								nearbyAdapter.removeAll();
							list_view.onRefreshComplete();
						}

						nearbyAdapter.addList(list, false);
						
						if( list ==null || list.size() <= 0 ) return;
						final NearMsg item = (NearMsg)list.get(0);

						CommentBoxDialog cbd = new CommentBoxDialog(
								_this, R.style.dialog_load1);
						cbd.nid = item.id;
						long ruid = nearHintMsg.uid;
						cbd.ruid = ruid;
						cbd.replayHint = "回复"+nearHintMsg.nickname;
						cbd.itemData = item;
						cbd.initData();
						cbd.setCommentListener(new CommentBoxDialog.CommentListener() {

							@Override
							public void setCommentCallBack(JSONObject jo) {
								// 评论回掉
								returnData();
							}
						});
						
						
					}
				});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.bt_home_msg:
			returnData();
			break;
		default:
			break;
		}

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void returnData(){
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("nearHintMsg", nearHintMsg);
		intent.putExtras( bundle );
		this.setResult( 10001 , intent) ;
		finish();
	}
	@Override
	public void onBackPressed() {
		returnData();
	}

}
