package com.hyh.www.nearby;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.System;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.config.Conf;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.service.GezitechService;
import com.gezitech.service.GezitechService.CallBDLocation;
import com.gezitech.service.managers.NearManager;
import com.gezitech.service.xmpp.Constant;
import com.gezitech.util.ToastMakeText;
import com.gezitech.widget.MyListView;
import com.gezitech.widget.MyListView.OnMoreListener;
import com.gezitech.widget.MyListView.OnRefreshListener;
import com.hyh.www.BaseFragment;
import com.hyh.www.NewsHint;
import com.hyh.www.R;
import com.hyh.www.ZhuyeActivity;
import com.hyh.www.adapter.NearbyAdapter;
import com.hyh.www.entity.Emotion;
import com.hyh.www.entity.NearMsg;
import com.hyh.www.entity.PubRange;
import com.hyh.www.user.SystemMessageActivity;
import com.hyh.www.user.post.PostMsg;
import com.loopj.android.http.RequestParams;
import com.sina.weibo.sdk.component.view.AttentionComponentView.RequestParam;
import java.util.ArrayList;

public class NearbyFramgent extends BaseFragment {
	public static NearbyFramgent fragment = null;
	private View view;
	private NearbyFramgent _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_gps;
	private MyListView list_view;
	private NearbyAdapter nearbyAdapter;
	private int page = 1, pageSize = 15;
	private TextView tv_near_new_msg;

	public static NearbyFramgent newInstance() {

		if (fragment != null) {
			return fragment;
		} else {
			fragment = new NearbyFramgent();
		}
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.framgent_nearby, null);
		Emotion.getDefaultList();
		_init();
		return view;
	}

	private void _init() {
		TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_title.setText(activity.getResources().getString(R.string.nearby));

		bt_my_post = (Button) _this.view.findViewById(R.id.bt_my_post);
		bt_my_post.setText("发布");

		Drawable nav_up = getResources().getDrawable(R.drawable.fabu_add);
		nav_up.setBounds(0, 0, nav_up.getMinimumWidth(),
				nav_up.getMinimumHeight());
		bt_my_post.setCompoundDrawables(null, null, nav_up, null);
		bt_my_post.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				GezitechAlertDialog.loadDialog(activity);
				NearManager.getInstance().getPubRangeList(
						new OnAsynGetListListener() {

							@Override
							public void OnAsynRequestFail(String errorCode,
									String errorMsg) {
								GezitechAlertDialog.closeDialog();
								new ToastMakeText(activity).Toast(errorMsg);
							}

							@Override
							public void OnGetListDone(
									ArrayList<GezitechEntity_I> list) {
								GezitechAlertDialog.closeDialog();
								if (list == null || list.size() <= 0) {
									new ToastMakeText(activity)
											.Toast("系统出错，请稍后再试！");
								} else {
									PubRange pr = (PubRange) list.get(list
											.size() - 1);
									if (pr == null)
										return;
									Intent intent = new Intent(activity,
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

		// 系统消息的通知
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.SYSTEM_REQUEST);
		filter.addAction(Constant.NEAR_NEW_MSG_HINT);
		activity.registerReceiver(receiver, filter);
		bt_home_msg = (Button) view.findViewById(R.id.bt_home_msg);
		bt_home_msg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new NewsHint().setNewsUpdate(activity, bt_home_msg);
				activity.startActivity(new Intent(activity,
						SystemMessageActivity.class));
			}
		});
		// 定位
		tv_gps = (TextView) view.findViewById(R.id.tv_gps);

		// 附近人的列表
		list_view = (MyListView) view.findViewById(R.id.list_view);

		nearbyAdapter = new NearbyAdapter(activity);
		nearbyAdapter.setFragmentVal(_this);
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

		// 新消息的提示
		tv_near_new_msg = (TextView) view.findViewById(R.id.tv_near_new_msg);
		tv_near_new_msg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tv_near_new_msg.setVisibility(View.GONE);
				page = 1;
				// 初始化下来加载
				list_view.onClickRefreshComplete();
				list_view.setSelection( 0 );
				_initGetData();

			}
		});
		
		_initGetData();

		mHandler.postDelayed(countdown, 1000 * 60 );
		
	}

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

	// 获取数据列表
	private void _getData(String longs, String lat, String address) {
		RequestParams params = new RequestParams();
		params.put("page", page);
		params.put("pageSize", pageSize);
		params.put("long", longs);
		params.put("lat", lat);
		params.put("address", address);
		NearManager.getInstance().getNearByList(params,
				new OnAsynGetListListener() {

					@Override
					public void OnAsynRequestFail(String errorCode,
							String errorMsg) {
						new ToastMakeText(activity).Toast(errorMsg);
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
							// 存储最后一次获取附近人时间
							GezitechApplication.systemSp
									.edit()
									.putLong(
											"lastGetTime",
											(long) java.lang.System
													.currentTimeMillis() / 1000)
									.commit();
							((ZhuyeActivity) _this.getActivity())
									.hideNewMsgHint();
							tv_near_new_msg.setVisibility(View.GONE);
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

	@Override
	public void onResume() {
		super.onResume();
		new NewsHint().getNewsUpdate(activity, bt_home_msg);
		gpsAction(tv_gps);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden) {
			new NewsHint().getNewsUpdate(activity, bt_home_msg);
			mHandler.postDelayed(countdown, 1000 * 60 );
		} else {
			if (mHandler != null && countdown != null)
				mHandler.removeCallbacks(countdown);
		}
		((ZhuyeActivity) _this.getActivity()).getNewNearByCount();
		gpsAction(tv_gps);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (receiver != null)
			activity.unregisterReceiver(receiver);
		if (mHandler != null && countdown != null)
			mHandler.removeCallbacks(countdown);
	}

	// 新系统消息的接受广播
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constant.SYSTEM_REQUEST.equals(action)) {// 返回解接受的消息
				bt_home_msg.setBackgroundResource(R.drawable.common_msg_yes);
				new NewsHint().getNewsUpdate(activity, bt_home_msg, true);

			} else if (Constant.NEAR_NEW_MSG_HINT.equals(action)) {// 主页的新消息广播的提示

				int count = intent.getIntExtra("count", 0);
				if (count > 0) {
					tv_near_new_msg.setVisibility(View.VISIBLE);
					tv_near_new_msg.setText("附近有" + count + "条动态");

				}

			}
		}

	};
	// 显示和隐藏的请求附近有多少条动态
	private Handler mHandler = new Handler();
	private Runnable countdown = new Runnable() {
		public void run() {
			((ZhuyeActivity) _this.getActivity()).getNewNearByCount();
			mHandler.postDelayed(countdown, 1000 * 60 );

		}
	};
}
