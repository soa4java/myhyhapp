package com.hyh.www.adapter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynInsertListener;
import com.gezitech.entity.PageList;
import com.gezitech.entity.User;
import com.gezitech.service.GezitechService;
import com.gezitech.service.managers.ShoutManager;
import com.gezitech.service.managers.UserManager;
import com.gezitech.service.xmpp.MessageSend;
import com.gezitech.service.xmpp.XmppConnectionManager;
import com.gezitech.util.DateUtil;
import com.gezitech.util.StringUtil;
import com.gezitech.util.ToastMakeText;
import com.gezitech.widget.MyListView;
import com.gezitech.widget.RemoteImageView;
import com.gezitech.widget.listgroup.PinnedHeaderListView;
import com.gezitech.widget.listgroup.PinnedHeaderListView.PinnedHeaderAdapter;
import com.hyh.www.R;
import com.hyh.www.chat.BillDetailActivity_bak;
import com.hyh.www.chat.ChatActivity;
import com.hyh.www.entity.Bill;
import com.hyh.www.entity.Businesslist;
import com.hyh.www.entity.Contacts;
import com.hyh.www.entity.FieldVal;
import com.hyh.www.entity.Friend;
import com.hyh.www.entity.Incomelist;
import com.hyh.www.entity.Shout;
import com.hyh.www.user.PersonDetailedInformationActivity;
import com.hyh.www.widget.ImageShow;
import com.hyh.www.widget.YMDialog;
import com.hyh.www.widget.YMDialog2;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DebugUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

/**
 * 
 * @author xiaobai 2014-10-15
 * @todo( 订单列表 )
 */
public class OrderAdapter extends BasicAdapter {
	int type;
	private User user;
	private GezitechActivity activity;

	public OrderAdapter(int type, GezitechActivity activity) {
		this.type = type;
		user = GezitechService.getInstance().getCurrentUser();
		this.activity = activity;
		// 判断是否已经链接
		if (GezitechApplication.connection == null
				|| (!GezitechApplication.connection.isConnected())
				|| (XmppConnectionManager.getInstance().getConnection() != null && !XmppConnectionManager
						.getInstance().getConnection().isConnected())) {
			XmppConnectionManager.getInstance().login();
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public GezitechEntity_I getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final Bill item = (Bill) list.get(position);
		Hv hv;
		if (view == null) {
			view = inflater.inflate(R.layout.list_order, null);
			hv = new Hv(view);
			view.setTag(hv);
		} else {
			hv = (Hv) view.getTag();
		}
		hv.tv_order_number.setText(item.tradecode + "");

		hv.tv_order_time.setText(DateUtil.getShortTime(item.ctime * 1000));
		hv.tv_myrelease_beizhu.setText(item.notes);
		//hv.tv_linkman.setText(user.isbusiness > 0 ? "联系买家" : "联系卖家");
		hv.tv_linkman.setText( user.id == item.bid ? "联系买家" : "联系卖家");
		hv.tv_company_name
				.setText(FieldVal.value(item.nickname).equals("") ? item.username
						: item.nickname);
		// 聊天
		hv.ll_chat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转到聊天界面
				/*
				 * Intent intent = new Intent(activity, ChatActivity.class);
				 * intent.putExtra("uid", (user.id == item.bid) ? item.uid :
				 * item.bid);
				 * 
				 * intent.putExtra("username", FieldVal.value(item.nickname)
				 * .equals("") ? item.username : item.nickname);
				 * 
				 * intent.putExtra("head", item.head);
				 * 
				 * intent.putExtra("isfriend", item.isfriend>0 ? 1 : 2 );
				 * 
				 * activity.startActivity(intent);
				 */
				lookFriendData( (user.id == item.bid) ? item.uid : item.bid );
			}
		});
		String stateStr = "";
		layoutShow(false, hv.myrelease_shengyu);
		layoutShow(false, hv.myrelease_wdfb_cancel);
		item.common_time = 0;
		switch (item.state) {
		case 0:
			stateStr = "未付款";

			//if (user.isbusiness > 0) {
			if( user.id == item.bid ){
				
			} else {
				layoutShow(true, hv.myrelease_wdfb_cancel);
				hv.tv_cancel.setText("去付款");
				hv.tv_cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {// click事件的逻辑
						onClickDataPress.onDataPerss(item, position);
					}
				});
			}
			break;
		case 1:
			stateStr = "已付款";
			layoutShow(true, hv.myrelease_shengyu);
			layoutShow(true, hv.myrelease_wdfb_cancel);
			//if (user.isbusiness > 0) {
			if( user.id == item.bid ){
				layoutShow(true, hv.ll_two_button);
				hv.tv_cancel.setText("确认收款");
				hv.tv_cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {// click事件的逻辑

						final YMDialog ymd = new YMDialog(activity);
						ymd.setHintMsg("确定收款?");
						ymd.setConfigButton(new OnClickListener() {

							@Override
							public void onClick(View v) {
								ymd.dismiss();
								RequestParams params = new RequestParams();
								params.put("id", item.id);
								GezitechAlertDialog.loadDialog(activity);
								ShoutManager.getInstance().certaincollect(
										params, new OnAsynInsertListener() {

											@Override
											public void OnAsynRequestFail(
													String errorCode,
													String errorMsg) {
												GezitechAlertDialog
														.closeDialog();
												activity.Toast(errorMsg);
											}

											@Override
											public void onInsertDone(String id) {
												GezitechAlertDialog
														.closeDialog();
												// 成功后刷新当前item
												item.state = 2;
												setItem(item, position);
												String body4 = "{\"id\":"
														+ item.id
														+ ",\"tradecode\":\""
														+ item.tradecode
														+ "\",\"notes\":\""
														+ item.notes
														+ "\",\"money\":\""
														+ item.money
														+ "\",\"paytime\":"
														+ item.paytime
														+ ",\"activechecktime\":"
														+ item.activechecktime
														+ "}";
												// "确定收款";
												GezitechService.sendMessage(
														item.uid, 7, body4,
														item.sid);
											}
										});
							}
						}).setCloseButton(new OnClickListener() {

							@Override
							public void onClick(View v) {
								ymd.dismiss();
							}
						});

					}
				});
				hv.tv_refund.setText("取消收款");
				hv.tv_refund.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {// click事件的逻辑
						final YMDialog ymd = new YMDialog(activity);
						ymd.setHintMsg("确定取消收款?");
						ymd.setConfigButton(new OnClickListener() {
							@Override
							public void onClick(View v) {
								ymd.dismiss();
								RequestParams params = new RequestParams();
								params.put("id", item.id);
								GezitechAlertDialog.loadDialog(activity);
								ShoutManager.getInstance().storecancelcollect(
										params, new OnAsynInsertListener() {

											@Override
											public void OnAsynRequestFail(
													String errorCode,
													String errorMsg) {
												GezitechAlertDialog
														.closeDialog();
												activity.Toast(errorMsg);
											}

											@Override
											public void onInsertDone(String id) {
												GezitechAlertDialog
														.closeDialog();
												// 成功后刷新当前item
												item.state = 4;
												setItem(item, position);
												String body4 = "{\"id\":"
														+ item.id
														+ ",\"tradecode\":\""
														+ item.tradecode
														+ "\",\"notes\":\""
														+ item.notes
														+ "\",\"money\":\""
														+ item.money
														+ "\",\"paytime\":"
														+ item.paytime
														+ ",\"activechecktime\":"
														+ item.activechecktime
														+ "}";
												// "商家拒绝收款"
												GezitechService.sendMessage(
														item.uid, 12, body4,
														item.sid);
											}
										});

							}
						}).setCloseButton(new OnClickListener() {

							@Override
							public void onClick(View v) {
								ymd.dismiss();
							}
						});
					}
				});

			} else {

				layoutShow(false, hv.ll_two_button);
				hv.tv_cancel.setText("撤销付款");
				hv.tv_cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {// click事件的逻辑
						final YMDialog ymd = new YMDialog(activity);
						ymd.setHintMsg("确定撤销付款?");
						ymd.setConfigButton(new OnClickListener() {
							@Override
							public void onClick(View v) {
								ymd.dismiss();
								RequestParams params = new RequestParams();
								params.put("id", item.id);
								GezitechAlertDialog.loadDialog(activity);
								ShoutManager.getInstance().usercancelpay(
										params, new OnAsynInsertListener() {

											@Override
											public void OnAsynRequestFail(
													String errorCode,
													String errorMsg) {
												GezitechAlertDialog
														.closeDialog();
												activity.Toast(errorMsg);
											}

											@Override
											public void onInsertDone(String id) {
												GezitechAlertDialog
														.closeDialog();
												// 成功后刷新当前item
												item.state = 3;
												setItem(item, position);
												String body4 = "{\"id\":"
														+ item.id
														+ ",\"tradecode\":\""
														+ item.tradecode
														+ "\",\"notes\":\""
														+ item.notes
														+ "\",\"money\":\""
														+ item.money
														+ "\",\"paytime\":"
														+ item.paytime
														+ ",\"activechecktime\":"
														+ item.activechecktime
														+ "}";
												// "撤销付款"
												GezitechService.sendMessage(
														item.bid, 11, body4,
														item.sid);
											}
										});
							}
						}).setCloseButton(new OnClickListener() {

							@Override
							public void onClick(View v) {
								ymd.dismiss();
							}
						});
					}
				});

			}
			// 有效确认时间
			item.common_time = item.paytime + item.activechecktime;
			break;
		case 2:
			stateStr = "服务中";
			layoutShow(true, hv.myrelease_shengyu);
			if ( user.id == item.bid ) {

			} else {
				layoutShow(true, hv.myrelease_wdfb_cancel);
				layoutShow(true, hv.ll_two_button);
				hv.tv_cancel.setText("确认服务");
				hv.tv_cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {// click事件的逻辑
						final YMDialog ymd = new YMDialog(activity);
						ymd.setHintMsg("确定确认服务?");
						ymd.setConfigButton(new OnClickListener() {
							@Override
							public void onClick(View v) {
								ymd.dismiss();
								RequestParams params = new RequestParams();
								params.put("id", item.id);
								GezitechAlertDialog.loadDialog(activity);
								ShoutManager.getInstance().userconfirmservice(
										params, new OnAsynInsertListener() {

											@Override
											public void OnAsynRequestFail(
													String errorCode,
													String errorMsg) {
												GezitechAlertDialog
														.closeDialog();
												activity.Toast(errorMsg);
											}

											@Override
											public void onInsertDone(String id) {
												GezitechAlertDialog
														.closeDialog();
												// 成功后刷新当前item
												item.state = 5;
												setItem(item, position);
												String body4 = "{\"id\":"
														+ item.id
														+ ",\"tradecode\":\""
														+ item.tradecode
														+ "\",\"notes\":\""
														+ item.notes
														+ "\",\"money\":\""
														+ item.money
														+ "\",\"paytime\":"
														+ item.paytime
														+ ",\"activechecktime\":"
														+ item.activechecktime
														+ "}";
												// 用户确认服务

												GezitechService.sendMessage(
														item.bid, 14, body4,
														item.sid);
											}
										});
							}
						}).setCloseButton(new OnClickListener() {

							@Override
							public void onClick(View v) {
								ymd.dismiss();
							}
						});
					}
				});
				hv.tv_refund.setText("申请退款");
				hv.tv_refund.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {// click事件的逻辑
						final YMDialog ymd = new YMDialog(activity);
						ymd.setHintMsg("确定申请退款?");
						ymd.setConfigButton(new OnClickListener() {
							@Override
							public void onClick(View v) {
								ymd.dismiss();
								RequestParams params = new RequestParams();
								params.put("id", item.id);
								GezitechAlertDialog.loadDialog(activity);
								ShoutManager.getInstance().userapplyrefund(
										params, new OnAsynInsertListener() {

											@Override
											public void OnAsynRequestFail(
													String errorCode,
													String errorMsg) {
												GezitechAlertDialog
														.closeDialog();
												activity.Toast(errorMsg);
											}

											@Override
											public void onInsertDone(String id) {
												GezitechAlertDialog
														.closeDialog();
												// 成功后刷新当前item
												item.state = 6;
												setItem(item, position);
												String body4 = "{\"id\":"
														+ item.id
														+ ",\"tradecode\":\""
														+ item.tradecode
														+ "\",\"notes\":\""
														+ item.notes
														+ "\",\"money\":\""
														+ item.money
														+ "\",\"paytime\":"
														+ item.paytime
														+ ",\"activechecktime\":"
														+ item.activechecktime
														+ "}";
												// 用户申请退款;
												GezitechService.sendMessage(
														item.bid, 13, body4,
														item.sid);
											}
										});
							}
						}).setCloseButton(new OnClickListener() {

							@Override
							public void onClick(View v) {
								ymd.dismiss();
							}
						});
					}
				});

			}
			// 剩余7天时间
			item.common_time = item.surplustime;
			break;
		case 3:
			stateStr = "已撤销"; // 已撤销

			break;
		case 4:
			stateStr = "商家拒绝收款"; // 商家拒绝收款
			break;
		case 5:
			stateStr = "已确认服务"; // 已确认服务
			break;
		case 6:
			stateStr = "退款中"; // 6退款请求
			break;
		case 7:
			stateStr = "已同意退款"; // 7已同意退款
			break;
		case 8:
			stateStr = "拒绝退款"; // 8请求已作废
			break;
		}
		// 计算剩余时间
		if (item.common_time > 0 && (item.state == 1 || item.state == 2)) {
			final long curr = System.currentTimeMillis();
			final long startTime = item.common_time * 1000;
			if (startTime > curr) {
				long dtime = startTime - curr;
				hv.tv_shengyu.setText(getDateStr(dtime / 1000));
				hv.tv_shengyu.setTextColor(Color.parseColor("#ff340c"));
			} else {
				hv.tv_shengyu.setText("已过期");
				hv.tv_shengyu.setTextColor(Color.parseColor("#949494"));
				// 如果时间结束 隐藏下面的按钮 替换上面的状态描述
				layoutShow(false, hv.myrelease_wdfb_cancel);
				stateStr = item.surplustime > 0 ? "已确认服务" : "已撤销";
			}
		}
		hv.tv_state.setText(stateStr);
		// 点击调整详情
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickDataPress.onDataPerss(item, position);
			}
		});
		if (FieldVal.value(item.litpicUrl).equals("")) {
			hv.iv_img.setVisibility(View.GONE);
		} else {
			hv.iv_img.setVisibility(View.VISIBLE);
			hv.iv_img.setImageUrl(item.litpicUrl);
			hv.iv_img.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 画廊展示图片
					final String[] images = new String[1];
					String[] pic = item.litpicUrl.split("src=");
					images[0] = StringUtil.stringDecode(pic[1]);
					ImageShow.jumpDisplayPic(images, 0, activity);
				}
			});
		}
		return view;
	}

	// 缓存布局
	public class Hv {

		private TextView tv_order_number;
		private TextView tv_state;
		private TextView tv_order_time;
		private TextView tv_shengyu;
		private TextView tv_myrelease_beizhu;
		private TextView tv_linkman;
		private TextView tv_company_name;
		private LinearLayout ll_chat;
		private TextView tv_cancel;
		private LinearLayout ll_two_button;
		private TextView tv_refund;
		private LinearLayout myrelease_wdfb_cancel;
		private LinearLayout myrelease_shengyu;
		private RemoteImageView iv_img;

		Hv(View view) {
			tv_order_number = (TextView) view
					.findViewById(R.id.tv_order_number);
			tv_state = (TextView) view.findViewById(R.id.tv_state);
			tv_order_time = (TextView) view.findViewById(R.id.tv_order_time);
			myrelease_shengyu = (LinearLayout) view
					.findViewById(R.id.myrelease_shengyu);
			tv_shengyu = (TextView) view.findViewById(R.id.tv_shengyu);
			tv_myrelease_beizhu = (TextView) view
					.findViewById(R.id.tv_myrelease_beizhu);
			tv_linkman = (TextView) view.findViewById(R.id.tv_linkman);
			tv_company_name = (TextView) view
					.findViewById(R.id.tv_company_name);
			ll_chat = (LinearLayout) view.findViewById(R.id.ll_chat);
			tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
			ll_two_button = (LinearLayout) view
					.findViewById(R.id.ll_two_button);
			tv_refund = (TextView) view.findViewById(R.id.tv_refund);
			myrelease_wdfb_cancel = (LinearLayout) view
					.findViewById(R.id.myrelease_wdfb_cancel);
			iv_img = (RemoteImageView) view.findViewById(R.id.iv_img);
		}

	}

	private void layoutShow(boolean isShow, View v) {
		if (isShow)
			v.setVisibility(View.VISIBLE);
		else
			v.setVisibility(View.GONE);
	}

	private String getDateStr(long time) {
		long d = time / (24 * 3600);
		long h = time % (24 * 3600) / 3600;
		long m = time % (24 * 3600) % 3600 / 60;
		long s = time % (24 * 3600) % 3600 % 60;
		return (d <= 0 ? "" : d + "天") + (h < 10 ? "0" + h : h) + "时"
				+ (m < 10 ? "0" + m : m) + "分" + (s < 10 ? "0" + s : s) + "秒";
	}

	public void refreshcountdown(MyListView list_view) {

		int firstVisibleItemIndex = list_view.getFirstVisiblePosition();

		for (int i = 0; i < list_view.getChildCount(); i++) {
			try {
				View v = list_view.getChildAt(i);
				Bill item = (Bill) this.getItem(firstVisibleItemIndex + i - 1);
				Hv hv = new Hv(v);
				if (hv.tv_shengyu != null) {
					// 计算剩余时间
					if (item.common_time > 0) {
						final long curr = System.currentTimeMillis();
						final long startTime = item.common_time * 1000;
						if (startTime > curr) {
							long dtime = startTime - curr;
							hv.tv_shengyu.setText(getDateStr(dtime / 1000));
							hv.tv_shengyu.setTextColor(Color
									.parseColor("#ff340c"));
						} else {
							hv.tv_shengyu.setText("已过期");
							hv.tv_shengyu.setTextColor(Color
									.parseColor("#949494"));
							// 如果时间结束 隐藏下面的按钮 替换上面的状态描述
							layoutShow(false, hv.myrelease_wdfb_cancel);
							hv.tv_state.setText(item.surplustime > 0 ? "已确认服务"
									: "已撤销");
						}
					}
				}
			} catch (Exception e) {

			}
		}

	}

	// 查看资料
	public void lookFriendData(long fid) {
		// TODO Auto-generated method stub
		GezitechAlertDialog.loadDialog(activity);
		UserManager.getInstance().getfrienddata(fid,
				new OnAsynGetOneListener() {

					@Override
					public void OnAsynRequestFail(String errorCode,
							String errorMsg) {
						// TODO Auto-generated method stub
						GezitechAlertDialog.closeDialog();
						new ToastMakeText(activity).Toast(errorMsg);
					}

					@Override
					public void OnGetOneDone(GezitechEntity_I entity) {
						// TODO Auto-generated method stub
						GezitechAlertDialog.closeDialog();
						Intent intent = new Intent(activity,
								PersonDetailedInformationActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("friendinfo", (Friend) entity);
						intent.putExtras(bundle);
						activity.startActivityForResult(intent, 2001);
					}
				});

	}
}
