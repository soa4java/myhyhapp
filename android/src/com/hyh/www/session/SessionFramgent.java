package com.hyh.www.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListArrayListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynRequestListener;
import com.gezitech.entity.PageList;
import com.gezitech.entity.User;
import com.gezitech.service.managers.ChatManager;
import com.gezitech.service.managers.FriendManager;
import com.gezitech.service.managers.ShoutManager;
import com.gezitech.service.managers.SystemManager;
import com.gezitech.service.managers.UserManager;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.gezitech.service.xmpp.Constant;
import com.gezitech.util.DateUtil;
import com.gezitech.util.ToastMakeText;
import com.gezitech.widget.MyListView;
import com.gezitech.widget.MyListView.OnMoreListener;
import com.gezitech.widget.MyListView.OnRefreshListener;
import com.gezitech.widget.RemoteImageView;
import com.gezitech.widget.listgroup.PinnedHeaderListView;
import com.hyh.www.BaseFragment;
import com.hyh.www.NewsHint;
import com.hyh.www.R;
import com.hyh.www.adapter.BasicAdapter.OnClickDataPress;
import com.hyh.www.adapter.ChatAdapter;
import com.hyh.www.adapter.ChatAdapter.Hv;
import com.hyh.www.adapter.ChatHYHAdapter;
import com.hyh.www.adapter.FriendsAdapter;
import com.hyh.www.chat.ChatActivity;
import com.hyh.www.entity.Chat;
import com.hyh.www.entity.ChatContent;
import com.hyh.www.entity.Contacts;
import com.hyh.www.entity.FieldVal;
import com.hyh.www.entity.Friend;
import com.hyh.www.entity.Shout;
import com.hyh.www.home.MyReleaseActivity;
import com.hyh.www.user.PersonDetailedInformationActivity;
import com.hyh.www.user.SystemMessageActivity;
import com.hyh.www.widget.ActivityCommon;

import android.app.Activity;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.hyh.www.adapter.MyPagerAdapter;

/**
 * 
 * @author xiaobai 2014-10-14
 * @todo( 会话 )
 */
public class SessionFramgent extends BaseFragment {
	private View view;
	private SessionFramgent _this = this;
	private Button bt_my_post;
	private ViewPager vp_session_box;
	private RadioGroup session_radioGroup;
	private RadioButton menu_session_hyh;
	private RadioButton menu_session_privatechat;
	private RadioButton menu_session_contacts;
	private RadioButton[] tabRadioButton;
	private LayoutInflater mInflater;
	private ViewGroup viewLayoutListView_hyh;
	private ViewGroup viewLayoutListView_chat;
	private ViewGroup viewLayoutListView_contacts;
	private MyListView list_hyh;
	private MyListView list_chat;
	private PinnedHeaderListView list_contacts;
	private ArrayList<View> mPageViews;
	private MyPagerAdapter myPagerAdapter;
	private ChatHYHAdapter chatAdapter1;
	private ChatAdapter chatAdapter2;
	public static SessionFramgent fragment = null;
	// 分组
	private List<String> mSections;
	// 分组位置集
	private List<Integer> mPositions;
	private FriendsAdapter FriendsAdapter;
	private Button bt_home_msg;
	private View myshoutView;
	private View kefuView;
	private GezitechDBHelper<Chat> chatDb;
	private GezitechDBHelper<ChatContent> chatContentDB;
	protected int contactPosition = -1;
	protected int count = 5;
	private Thread thread;
	private int start = 0;
	private LinearLayout hyh_loading_bg;
	private TextView tv_unreadcount_hyh;
	private TextView tv_unreadcount_private;
	private TextView tv_friend_request_count;
	private boolean isHidden = false;

	public static SessionFramgent newInstance() {

		if (fragment != null) {
			return fragment;
		} else {
			fragment = new SessionFramgent();
		}
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mInflater = activity.getLayoutInflater();
		chatDb = new GezitechDBHelper<Chat>(Chat.class);
		chatContentDB = new GezitechDBHelper<ChatContent>(ChatContent.class);
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.NEW_MESSAGE_ACTION);
		filter.addAction(Constant.SEND_UPDATE_MESSAGE_ACTION);
		filter.addAction(Constant.UPDATE_CONTACTS_ACTION);
		filter.addAction(Constant.FRIEND_REQUEST_COUNT);
		filter.addAction(Constant.SYSTEM_REQUEST );
		activity.registerReceiver(receiver, filter);
		
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (hidden) {// 如果是隐藏
			if (chatAdapter1 != null)
				chatAdapter1.stopAudio();
			isHidden = true;
		} else {
			isHidden = false;
			if (tabRadioButton[0].isChecked())
				updateUnreadcount();
			new NewsHint().getNewsUpdate(activity, bt_home_msg );
		}
		gpsAction(  tv_gps );
	}

	// 新消息聊天列表的广播
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constant.NEW_MESSAGE_ACTION.equals(action)
					|| Constant.SEND_UPDATE_MESSAGE_ACTION.equals(action)) {// 返回解接受的消息
				loadHyhListCommone(false);
				refreshChatList(1);
				updateUnreadcount();
			}
			// 私聊联系人变动的广播 /同意加好友，
			else if (Constant.UPDATE_CONTACTS_ACTION.equals(action)) {
				loadHyhListCommone(false);
				refreshChatList(1);
				loadFriendList();
				updateUnreadcount();
			}
			// 好友的请求
			else if (Constant.FRIEND_REQUEST_COUNT.equals(action)) {
				getfriendnumber();
			} else if (Constant.FRIEND_REQUEST_COUNT.equals(action)) {
				getfriendnumber();
			}else if (Constant.SYSTEM_REQUEST.equals(action)) {// 返回解接受的消息
				bt_home_msg.setBackgroundResource(R.drawable.common_msg_yes );
				new NewsHint().getNewsUpdate(activity, bt_home_msg, true );
	
			} 
		}
	};
	private TextView tv_gps;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.framgent_session, null);

		_init();
		updateUnreadcount();
		return view;
	}

	// 更新未读的消息条数
	public void updateUnreadcount() {
		int[] unreadCount = ChatManager.getInstance().unreadcount(
				!isHidden && tabRadioButton[0].isChecked() ? 1 : 0);
		if ((unreadCount[0] + unreadCount[2]) > 0) {
			tv_unreadcount_hyh.setVisibility(View.VISIBLE);
			tv_unreadcount_hyh.setText((unreadCount[0] + unreadCount[2]) + "");
		} else {
			tv_unreadcount_hyh.setVisibility(View.GONE);
		}
		if (unreadCount[1] > 0) {
			tv_unreadcount_private.setVisibility(View.VISIBLE);
			tv_unreadcount_private.setText(unreadCount[1] + "");
		} else {
			tv_unreadcount_private.setVisibility(View.GONE);
		}
		fc.unreadcount();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new NewsHint().getNewsUpdate(activity, bt_home_msg);
		gpsAction(  tv_gps );
	}

	// 初始化数据
	private void _init() {

		bt_my_post = (Button) _this.view.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);
		/*
		 * bt_my_post.setOnClickListener( new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * 
		 * Intent intent = new Intent(getActivity(), MyReleaseActivity.class);
		 * startActivity(intent); } });
		 */
		TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_title.setText(_this.getResources().getString(R.string.session));

		session_radioGroup = (RadioGroup) view
				.findViewById(R.id.session_radioGroup);

		tabRadioButton = new RadioButton[3];
		tabRadioButton[0] = (RadioButton) view
				.findViewById(R.id.menu_session_hyh);
		tabRadioButton[0].setChecked(true);
		tabRadioButton[1] = (RadioButton) view
				.findViewById(R.id.menu_session_privatechat);
		tabRadioButton[2] = (RadioButton) view
				.findViewById(R.id.menu_session_contacts);

		tv_unreadcount_hyh = (TextView) view
				.findViewById(R.id.tv_unreadcount_hyh);
		tv_unreadcount_private = (TextView) view
				.findViewById(R.id.tv_unreadcount_private);
		tv_friend_request_count = (TextView) view
				.findViewById(R.id.tv_friend_request_count);

		vp_session_box = (ViewPager) view.findViewById(R.id.vp_session_box);

		viewLayoutListView_hyh = (ViewGroup) mInflater.inflate(
				R.layout.viewpage_item, null);
		viewLayoutListView_chat = (ViewGroup) mInflater.inflate(
				R.layout.viewpage_item, null);
		viewLayoutListView_contacts = (ViewGroup) mInflater.inflate(
				R.layout.viewpage_contacts, null);

		list_hyh = (MyListView) viewLayoutListView_hyh
				.findViewById(R.id.list_view);
		hyh_loading_bg = (LinearLayout) viewLayoutListView_hyh
				.findViewById(R.id.loading_bg);

		/*
		 * myshoutView = LayoutInflater.from( getActivity()
		 * ).inflate(R.layout.list_session_item, null); list_hyh.addHeaderView(
		 * myshoutView );
		 */

		list_chat = (MyListView) viewLayoutListView_chat
				.findViewById(R.id.list_view);
		kefuView = LayoutInflater.from(getActivity()).inflate(
				R.layout.list_session_item, null);
		list_chat.addHeaderView(kefuView);

		list_contacts = (PinnedHeaderListView) viewLayoutListView_contacts
				.findViewById(R.id.list_view);
		// 初始化ViewPager的内容
		mPageViews = new ArrayList<View>();
		// 添加3个页面
		mPageViews.add(viewLayoutListView_hyh);
		mPageViews.add(viewLayoutListView_chat);
		mPageViews.add(viewLayoutListView_contacts);
		// 组装视图
		myPagerAdapter = new MyPagerAdapter(mPageViews);
		vp_session_box.setAdapter(myPagerAdapter);

		vp_session_box.setOnPageChangeListener(new OnPageChangeListener() {

			public void onPageSelected(int arg0) {
				if (arg0 == 0) {
					tabRadioButton[0].setChecked(true);
					updateUnreadcount();
					_initHyhData();
				}
				if (arg0 == 1) {
					tabRadioButton[1].setChecked(true);
					_initChatData();
				}
				if (arg0 == 2) {
					tabRadioButton[2].setChecked(true);
					_initContactsData();
				}
				if (chatAdapter1 != null)
					chatAdapter1.stopAudio();
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			public void onPageScrollStateChanged(int arg0) {
			}
		});
		session_radioGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup arg0, int arg1) {
						switch (arg1) {
						case R.id.menu_session_hyh:
							vp_session_box.setCurrentItem(0);
							break;
						case R.id.menu_session_privatechat:
							vp_session_box.setCurrentItem(1);
							break;
						case R.id.menu_session_contacts:
							vp_session_box.setCurrentItem(2);
							break;
						}

					}
				});
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

		_initHyhData();
		tv_gps = 	(TextView)view.findViewById( R.id.tv_gps ) ;
		
		
	}

	// 初始化喊一喊数据
	// 没有加入好友的聊天
	private void _initHyhData() {
		if (chatAdapter1 == null) {
			chatAdapter1 = new ChatHYHAdapter(activity, 2, list_hyh, this);
			list_hyh.setAdapter(chatAdapter1);
			list_hyh.footerShowState(2);
			// 数据回调
			chatAdapter1.setOnClickDataPress(new OnClickDataPress() {

				@Override
				public void onDataPerss(GezitechEntity_I item, int position) {

				}
			});
			list_hyh.setOnMoreListener(new OnMoreListener() {
				@Override
				public void OnMore() {

					list_hyh.footerShowState(0);
					loadHyhList(false);

				}

				@Override
				public void OnMore(int firstVisiableItem, int displayItemCount,
						int count, int lastVisibleItem) {
				}
			});
			list_hyh.setonRefreshListener(new OnRefreshListener() {

				@Override
				public void onRefresh() {
					loadHyhListCommone(true);
				}
			});
			hyh_loading_bg.setVisibility(View.VISIBLE);
			loadHyhListCommone(true);

			mHandler.postDelayed(countdown, 1000);
		}
	}

	private Handler mHandler = new Handler();
	private Runnable countdown = new Runnable() {
		public void run() {
			chatAdapter1.refreshcountdown();

			mHandler.postDelayed(countdown, 1000);
		}
	};

	/**
	 * TODO(如果本地没有喊一喊数据 加载服务端的喊一喊数据)
	 */
	private void loadHyhListCommone(boolean isremoteLoadData) {
		if (chatAdapter1 == null)
			return;
		start = 0;
		ArrayList<ChatContent> listtemp = chatContentDB.query(" myuid="
				+ user.id + " and type=9", 0, "ctime desc");
		if ((listtemp != null && listtemp.size() > 0) || !isremoteLoadData) {
			loadHyhList(true);
		} else {
			remoteLoadData(System.currentTimeMillis() / 1000);
		}
	}

	/**
	 * 
	 * TODO( 远程加载hyh的数据 )
	 */
	private void remoteLoadData(long timeflag) {
		ShoutManager.getInstance().getAllMyShoutList(start, count, timeflag,
				new OnAsynGetListListener() {

					@Override
					public void OnAsynRequestFail(String errorCode,
							String errorMsg) {
						hyh_loading_bg.setVisibility(View.GONE);
						Toast(errorMsg);
					}

					@Override
					public void OnGetListDone(ArrayList<GezitechEntity_I> list) {
						if (list != null && list.size() > 0) {
							loadHyhList(false);
						} else {
							hyh_loading_bg.setVisibility(View.GONE);
							list_hyh.footerShowState(1);
							list_hyh.onRefreshComplete();
						}
					}
				});
	}

	/**
	 * 
	 * TODO(加载列表)
	 */
	private ArrayList<GezitechEntity_I> hyhList = new ArrayList<GezitechEntity_I>();

	private void loadHyhList(final boolean isnew) {
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				GezitechEntity_I firstItem = null;
				firstItem = chatAdapter1.getItemList(chatAdapter1.getList()
						.size() - 1);
				ArrayList<ChatContent> listtemp;
				if (firstItem == null || isnew) {
					listtemp = chatContentDB.query(" myuid=" + user.id
							+ " and type=9 ", count, "ctime desc");
				} else {
					listtemp = chatContentDB.query(" myuid=" + user.id
							+ " and type=9", count, "ctime desc",
							(ChatContent) firstItem, false);
				}
				if (listtemp != null && listtemp.size() > 0) {

					for (int i = 0; i < listtemp.size(); i++) {

						ChatContent chatcontent = (ChatContent) listtemp.get(i);
						chatcontent.chatUser = new PageList();
						ArrayList<Chat> chat = chatDb.query(" myuid=" + user.id
								+ " and hyhid=" + chatcontent.hyhid, 0,
								"istop desc,ctime desc");
						if (chat != null && chat.size() > 0) {
							chatcontent.chatUser.addAll(chat);
						}
					}

					hyhList.addAll(listtemp);
				}
				handler.obtainMessage(UPDATE_CHAT_CONTENT).sendToTarget();
				if (thread != null && !thread.isInterrupted())
					thread.interrupt();
			}
		});
		thread.start();
	}

	// 回调更新界面
	private final static int UPDATE_CHAT_CONTENT = 1;
	Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Object obj = msg.obj;
			int what = msg.what;
			switch (what) {

			case UPDATE_CHAT_CONTENT: // 刷新聊天记录 下拉

				if (start <= 0) {// 刷新

					chatAdapter1.removeAll();
					list_hyh.onRefreshComplete();

				}

				if (hyhList != null && hyhList.size() > 0) {
					chatAdapter1.addList(hyhList, false);
				}

				list_hyh.footerShowState(1);

				start = start + hyhList.size();
				// 如果本地的数据小于count则去服务端加载
				if (hyhList.size() < count) {
					remoteLoadData(System.currentTimeMillis() / 1000);

				}

				hyhList.clear();

				hyh_loading_bg.setVisibility(View.GONE);

				break;
			}
			return false;
		}
	});

	// 初始化私聊数据 已经是朋友的聊天
	// /////////////////////////////////////////////////////
	private void _initChatData() {
		if (chatAdapter2 == null) {
			chatAdapter2 = new ChatAdapter(1, activity);
			list_chat.setAdapter(chatAdapter2);
			list_chat.footerShowState(2);
			refreshChatList(1);
			initKefu();
			// 数据回调
			chatAdapter2.setOnClickDataPress(new OnClickDataPress() {

				@Override
				public void onDataPerss(GezitechEntity_I item, final int position) {
					// TODO Auto-generated method stub
					final Chat chat = (Chat) item;
					GezitechAlertDialog.loadDialog(activity);
					UserManager.getInstance().getfrienddata(  chat.uid ,
							new OnAsynGetOneListener() {
								@Override
								public void OnAsynRequestFail(String errorCode,
										String errorMsg) {
									GezitechAlertDialog.closeDialog();
									Toast(errorMsg);
								}

								@Override
								public void OnGetOneDone(GezitechEntity_I entity) {
									GezitechAlertDialog.closeDialog();
									Friend friend = (Friend) entity;
									
									// 跳转到聊天界面
									Intent intent = new Intent(activity, ChatActivity.class);
									intent.putExtra("uid", chat.uid);
									intent.putExtra("username", chat.username);

									intent.putExtra("head", chat.head);

									intent.putExtra("isfriend", friend.groupId < 3 ? 3
											: (friend.isfriend > 0 ? 1
													: 2) );
									intent.putExtra("isbusiness", friend.isbusiness );
									
									_this.startActivity(intent);
									// 更新缓存
									chat.unreadcount = 0;
									chatDb.save(chat);
									chatAdapter2.setItem(chat, position);
									updateUnreadcount();
									

								}
							});

					
				}
			});
		}
	}

	// 初始化我发布喊一喊的头布局
	public void initKefu() {
		Hv hv = chatAdapter2.new Hv(kefuView);
		hv.iv_session_item.setImageResource(R.drawable.service_photo);
		hv.bt_session_item.setVisibility(View.GONE);
		hv.tv_session_item_name.setText("喊一喊客服");
		hv.tv_session_item_time.setVisibility(View.GONE);
		hv.tv_session_item_context.setText("欢迎注册喊一喊,有问题请联系我");
		hv.v_line.setBackgroundColor(Color.parseColor("#d4d4d4"));
		hv.session_item.setOnClickListener(new OnClickListener() {// 点击跳转到我发布的喊一喊

					@Override
					public void onClick(View v) {
						loadKefuList();
					}
				});
	}

	public void loadKefuList() {
		GezitechAlertDialog.loadDialog(activity);
		UserManager.getInstance().getkefu(new OnAsynGetOneListener() {

			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				GezitechAlertDialog.closeDialog();
				Toast(errorMsg);
			}

			@Override
			public void OnGetOneDone(GezitechEntity_I entity) {

				Friend item = (Friend) entity;

				UserManager.getInstance().getfrienddata(item.id,
						new OnAsynGetOneListener() {
							@Override
							public void OnAsynRequestFail(String errorCode,
									String errorMsg) {
								GezitechAlertDialog.closeDialog();
								Toast(errorMsg);
							}

							@Override
							public void OnGetOneDone(GezitechEntity_I entity) {
								GezitechAlertDialog.closeDialog();
								Friend friend = (Friend) entity;
								// 跳转到聊天界面
								Intent intent = new Intent(activity,
										ChatActivity.class);
								intent.putExtra("uid", friend.fid);

								intent.putExtra(
										"username",
										friend.nickname == null
												|| friend.nickname
														.equals("null")
												|| friend.nickname.equals("") ? friend.username
												: friend.nickname);

								intent.putExtra("head", friend.head);
								intent.putExtra("isbusiness", friend.isbusiness );
								intent.putExtra("isfriend",
										friend.groupId < 3 ? 3
												: (friend.isfriend > 0 ? 1 : 2));

								_this.startActivity(intent);

							}
						});

			}
		});
	}

	// isfriend 1 是朋友 2是非朋友
	private void refreshChatList(int isfriend) {
		if (isfriend == 1) {
			if (chatAdapter2 == null)
				return;
			ArrayList<GezitechEntity_I> pl = ChatManager.getInstance()
					.getChatList(1);
			if (pl != null) {
				chatAdapter2.removeAll();
				chatAdapter2.addList(pl, false);
			}
		}
	}

	// 初始化通讯录数据数据
	private void _initContactsData() {
		if (FriendsAdapter == null) {
			FriendsAdapter = new FriendsAdapter();
			list_contacts.setAdapter(FriendsAdapter);
			list_contacts.setOnScrollListener(FriendsAdapter);
			list_contacts.setPinnedHeaderView(LayoutInflater.from(activity)
					.inflate(R.layout.viewpage_titled, list_contacts, false));

			FriendManager.getInstance().getClientCacheFriendList(
					new OnAsynGetListArrayListListener() {
						@Override
						public void OnAsynRequestFail(String errorCode,
								String errorMsg) {
						}

						@Override
						public void OnGetListDone(
								ArrayList<GezitechEntity_I> list,
								ArrayList<String> mSections,
								ArrayList<Integer> mPositions) {
							FriendsAdapter.setFriendsPositions(mPositions);
							FriendsAdapter.setFriendsSections(mSections);
							FriendsAdapter.addList(list, false);
						}
					});

			loadFriendList();

			FriendsAdapter.setOnClickDataPress(new OnClickDataPress() {

				@Override
				public void onDataPerss(GezitechEntity_I item, int position) {
					Contacts items = (Contacts) item;
					if (item != null) {
						if (items.nickname.equals("添加新朋友")) {

							GezitechAlertDialog.loadDialog(activity);
							addNewFriend();
							if (FriendsAdapter.getNumberCount() > 0) {
								FriendsAdapter.setCount(0);
								FriendsAdapter.notifyDataSetChanged();
								tv_friend_request_count
										.setVisibility(View.GONE);
							}
						} else {
							contactPosition = position;
							lookFriendData(items.id);
						}
					}
				}
			});
			getfriendnumber();
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
						_this.startActivityForResult(intent, 2001);
					}
				});

	}

	// 加载朋友的列表
	public void loadFriendList() {

		if (FriendsAdapter == null)
			return;

		// 网络
		FriendManager.getInstance().getfriends(1, 10000000,
				new OnAsynGetListArrayListListener() {

					@Override
					public void OnAsynRequestFail(String errorCode,
							String errorMsg) {
					}

					@Override
					public void OnGetListDone(ArrayList<GezitechEntity_I> list,
							ArrayList<String> mSections,
							ArrayList<Integer> mPositions) {
						// TODO Auto-generated method stub
						FriendsAdapter.removeAll();
						FriendsAdapter.setFriendsPositions(mPositions);
						FriendsAdapter.setFriendsSections(mSections);
						FriendsAdapter.addList(list, false);
					}
				});

	}

	// 加载朋友的请求好友条数
	public void getfriendnumber() {
		FriendManager.getInstance().getfriendnumber(
				new OnAsynRequestListener() {
					@Override
					public void OnAsynRequestFail(String errorCode,
							String errorMsg) {
					}

					@Override
					public void OnAsynRequestCallBack(Object o) {
						if (FriendsAdapter != null) {
							int count = (Integer) o;
							if (count > 0
									|| FriendsAdapter.getNumberCount() > 0) {
								FriendsAdapter.setCount(count);
								FriendsAdapter.notifyDataSetChanged();
								tv_friend_request_count
										.setVisibility(View.VISIBLE);
								tv_friend_request_count.setText(count + "");
							} else {
								tv_friend_request_count
										.setVisibility(View.GONE);
							}
						}
					}
				});

	}

	// 添加好友的页面跳转 获取好友的请求列表
	public void addNewFriend() {

		FriendManager.getInstance().untreatedFriendlist(1, 1000000,
				new OnAsynGetListListener() {

					@Override
					public void OnAsynRequestFail(String errorCode,
							String errorMsg) {
						GezitechAlertDialog.closeDialog();

					}

					@Override
					public void OnGetListDone(ArrayList<GezitechEntity_I> list) {

						GezitechAlertDialog.closeDialog();

						Intent intent = new Intent(activity,
								NewFriendActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("friendRequestList", list);
						intent.putExtras(bundle);
						_this.startActivityForResult(intent, 1002);

					}
				});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null)
			return;
		switch (requestCode) {
		/*
		 * case 1002: //添加新朋友回调 loadFriendList(); break;
		 */
		case 2001: // 备注的返回
			if (FriendsAdapter != null && contactPosition >= 0) {

				Contacts item = (Contacts) FriendsAdapter
						.getItem(contactPosition);

				item.notes = data.getAction();

				FriendsAdapter.setItem(item, contactPosition);

				contactPosition = -1;

			}
			break;
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		FriendsAdapter = null;
		chatAdapter2 = null;
		chatAdapter1 = null;
		activity.unregisterReceiver(receiver);
		mHandler.removeCallbacks(countdown);
	}
	
}
