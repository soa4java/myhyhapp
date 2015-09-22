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
import com.hyh.www.entity.NearHintMsg;
import com.hyh.www.entity.NearMsg;
import com.hyh.www.entity.PubRange;
import com.loopj.android.http.RequestParams;

/**
 * 
 * @author xiaobai 2015-6-2
 * @todo( 我发布的信息 )
 */
public class MyPostMsg extends GezitechActivity implements OnClickListener {
	private MyPostMsg _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private MyListView list_view;
	private NearbyAdapter nearbyAdapter;
	private int page = 1, pageSize = 15;
	private TextView tv_new_msg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_post_msg);
		__init();
	}

	private void __init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setText("发布");
		// 新消息的提示
		tv_new_msg = (TextView) _this.findViewById(R.id.tv_new_msg);

		Drawable nav_up = getResources().getDrawable(R.drawable.fabu_add);
		nav_up.setBounds(0, 0, nav_up.getMinimumWidth(),
				nav_up.getMinimumHeight());
		bt_my_post.setCompoundDrawables(null, null, nav_up, null);
		bt_my_post.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				GezitechAlertDialog.loadDialog(_this);
				NearManager.getInstance().getPubRangeList(
						new OnAsynGetListListener() {

							@Override
							public void OnAsynRequestFail(String errorCode,
									String errorMsg) {
								GezitechAlertDialog.closeDialog();
								new ToastMakeText(_this).Toast(errorMsg);
							}

							@Override
							public void OnGetListDone(
									ArrayList<GezitechEntity_I> list) {
								GezitechAlertDialog.closeDialog();
								if (list == null || list.size() <= 0) {
									new ToastMakeText(_this)
											.Toast("系统出错，请稍后再试！");
								} else {
									PubRange pr = (PubRange) list.get(list
											.size() - 1);
									if (pr == null)
										return;
									Intent intent = new Intent(_this,
											PostMsg.class);
									Bundle bundle = new Bundle();
									bundle.putSerializable("pubRange", pr);
									intent.putExtras(bundle);
									_this.startActivityForResult(intent, 10000);
								}
							}
						});
			}
		});

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("我发布的信息");

		// 附近人的列表
		list_view = (MyListView) findViewById(R.id.list_view);

		nearbyAdapter = new NearbyAdapter(_this);
		nearbyAdapter.isShowHead(true);
		nearbyAdapter.setListView(list_view);
		list_view.setAdapter(nearbyAdapter);
		// 初始化下来加载
		list_view.onClickRefreshComplete();
		// 下拉刷新
		list_view.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				page = 1;
				_initGetData();

			}
		});
		// 加载更多
		list_view.setOnMoreListener(new OnMoreListener() {

			@Override
			public void OnMore() {
				page++;
				list_view.footerShowState(0);
				_initGetData();

			}

			@Override
			public void OnMore(int firstVisiableItem, int displayItemCount,
					int count, int lastVisibleItem) {
			}
		});

		_initGetData();

		like_comment_count();

		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.LIKE_COMMENT_ACTION);
		registerReceiver(receiver, filter);
		tv_new_msg.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity( new Intent(_this,NewMsgHintActivity.class ) );
			}
		});

	}

	// 新消息底部的提示的接受广播
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constant.LIKE_COMMENT_ACTION.equals(action)) {// 喜欢和评论的新消息提示

				like_comment_count();
			}
		}

	};
	// 获取数据的初始化
	private void _initGetData() {
		// 获取经度纬度
		String longs = GezitechApplication.systemSp.getString("long", "");
		String lat = GezitechApplication.systemSp.getString("lat", "");
		String city = GezitechApplication.systemSp.getString("city", "");
		if (longs.equals("") || lat.equals("")) {

			GezitechService.getInstance().longitude(new CallBDLocation() {

				@Override
				public void callfunction(String longs, String lat, String city) {
					_getData(longs, lat, city);
				}
			});

		} else {
			_getData(longs, lat, city);

		}
	}

	// 获取数据的初始化
	private void _getData(String longs, String lat, String address) {
		RequestParams params = new RequestParams();
		params.put("page", page);
		params.put("pageSize", pageSize);
		params.put("uid", user.id);
		params.put("long", longs);
		params.put("lat", lat);
		params.put("address", address);
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
						if (count >= pageSize) {
							list_view.footerShowState(1);
						} else if (count < pageSize) {
							list_view.footerShowState(-1);
						}

						nearbyAdapter.addList(list, false);

					}
				});

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
		unregisterReceiver(receiver);
	}
	// 返回
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case 10000:
			if (resultCode == Activity.RESULT_OK && data != null) {
				// NearMsg nearMsg =
				// (NearMsg)data.getExtras().getSerializable("nearMsg");
				page = 1;
				_initGetData();
			}
			break;
		case 2001:
			nearbyAdapter.setisJump(true);
			break;
		}

	}

	// 喜欢喝评论的提示
	public void like_comment_count() {
		ArrayList<NearHintMsg> nearHintMsg = ChatManager.getInstance()
				.getNearHintMsg();
		if (nearHintMsg != null && nearHintMsg.size() > 0) {

			tv_new_msg.setVisibility(View.VISIBLE);
			tv_new_msg.setText(nearHintMsg.size() + "条新消息");

		} else {

			tv_new_msg.setVisibility(View.GONE);

		}

	}

}
