package com.hyh.www.chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;
import com.baidu.location.BDLocation;
import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.basic.GezitechException;
import com.gezitech.config.Conf;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynInsertListener;
import com.gezitech.http.Response;
import com.gezitech.service.managers.ChatManager;
import com.gezitech.service.managers.ShoutManager;
import com.gezitech.service.managers.SystemManager;
import com.gezitech.service.managers.UserManager;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.gezitech.service.xmpp.Constant;
import com.gezitech.service.xmpp.XmppConnectionManager;
import com.gezitech.util.CalendarUtil;
import com.gezitech.util.DateUtils;
import com.gezitech.util.IOUtil;
import com.gezitech.util.ImageUtil;
import com.gezitech.util.SoundMeter;
import com.gezitech.util.VibratorUtil;
import com.gezitech.widget.MyListView;
import com.gezitech.widget.MyListView.OnRefreshListener;
import com.gezitech.widget.SelectPicPopupWindow;
import com.hyh.www.R;
import com.hyh.www.adapter.ChatContentAdapter;
import com.hyh.www.adapter.FaceAdapter;
import com.hyh.www.adapter.ViewPagerAdapter;
import com.hyh.www.entity.Bill;
import com.hyh.www.entity.Chat;
import com.hyh.www.entity.ChatContent;
import com.hyh.www.entity.Configuration;
import com.hyh.www.entity.Emotion;
import com.hyh.www.entity.FieldVal;
import com.hyh.www.entity.Friend;
import com.hyh.www.session.ChatMessagesActivity;
import com.hyh.www.user.OrderActivity;
import com.hyh.www.widget.YMDialog2;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

/**
 * 
 * @author xiaobai 2014-10-15
 * @todo( 聊天界面 )
 */
public class ChatActivity extends GezitechActivity implements
		OnItemClickListener {
	private ChatActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private MyListView list_view;
	private LinearLayout ll_chat_action_box;
	private ImageButton ib_card;
	private ImageButton ib_switch_sort;
	private EditText et_post_msg;
	private ImageButton ib_post_smile;
	private Button bt_talk;
	private ImageButton ib_post_send;
	private ImageButton ib_post_select;
	private boolean isTalk = false;
	private LinearLayout ll_bottom_action_box;
	private boolean bottomBoxIsShow = false;
	private boolean SmileBoxIsShow = false;
	private ViewPager vp_emoji;
	private LinearLayout ll_select_origin;
	private ArrayList<ArrayList<GezitechEntity_I>> emojis;
	private ArrayList<FaceAdapter> faceAdapters;
	private ArrayList<View> pageViews;
	private ArrayList<ImageView> pointViews;
	private int current;
	private LinearLayout rl_emoji_action_box;
	private long uid;
	private String username;
	private String head;
	private int page = 1;
	private int pageSize = 15;
	private GezitechDBHelper<Chat> chatDb;
	private GezitechDBHelper<ChatContent> chatContentDB;
	private ArrayList<GezitechEntity_I> chatContentList;
	private ChatContentAdapter chatContentAdapter;
	protected Thread thread;
	private int isfriend;
	private org.jivesoftware.smack.Chat chat;
	private GezitechDBHelper<Friend> friendDB;
	private Friend friend = null;
	private int flag = 1;
	private View i_voice;
	protected String path;
	protected long startVoiceT;
	protected String voiceName;
	protected String voicefile;
	private SoundMeter mSensor;
	private Handler mHandler = new Handler();
	private ImageView iv_size;
	protected long endVoiceT;
	private long hyhid = 0;
	private LinearLayout ll_time_box;
	private TextView tv_time_title;
	private TextView tv_time_val;
	private long hyhtimeVal = 0;
	private String hyhbody = "";
	protected boolean istimeout = false;
	private TextView tv_count_down_voice_time;
	private int isbusiness;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		_this.setContentView(R.layout.activity_chat);
		Intent intent = _this.getIntent();
		uid = intent.getLongExtra("uid", 0);
		username = intent.getStringExtra("username");
		head = intent.getStringExtra("head");
		isfriend = intent.getIntExtra("isfriend", 0);
		hyhid = intent.hasExtra( "hyhid" ) ? intent.getLongExtra("hyhid", 0) : 0 ; // 喊一喊id
		
		isbusiness = intent.getIntExtra("isbusiness", 0); // 是否是商家
		// 如果是喊一喊聊天则获取喊一喊的基本数据
		hyhbody = intent.hasExtra("body") ? intent.getStringExtra("body") : "";

		chatDb = new GezitechDBHelper<Chat>(Chat.class);
		chatContentDB = new GezitechDBHelper<ChatContent>(ChatContent.class);
		
		
		
		GezitechApplication.currUid = uid;
		GezitechApplication.hyhId = hyhid;
		_init();
	}

	// 数据初始化
	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(username);
		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_this.finish();
			}
		});
		ib_card = (ImageButton) _this.findViewById(R.id.ib_card);
		ll_time_box = (LinearLayout) _this.findViewById(R.id.ll_time_box);
		tv_time_title = (TextView) _this.findViewById(R.id.tv_time_title);
		tv_time_val = (TextView) _this.findViewById(R.id.tv_time_val);
		ib_card.setVisibility(View.VISIBLE);
		// 聊天信息
		ib_card.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(_this, ChatMessagesActivity.class);
				intent.putExtra("uid", uid);
				intent.putExtra("isfriend", isfriend);
				_this.startActivityForResult(intent, 1001);
			}
		});
		if (hyhid > 0 || isfriend == 3) { // 如果是喊一喊的聊天 就去掉设置
			ib_card.setVisibility(View.GONE);
		}
		if (hyhid > 0) {
			surplusTime();
		}

		list_view = (MyListView) _this.findViewById(R.id.list_view);
		
		/*//添加金钱提示
		View meonyView = LayoutInflater.from( _this ).inflate(R.layout.list_chat_content_hint, null);
		list_view.addHeaderView( meonyView );
		*/
		//聊天顶部提示初始化
		TextView tv_time = (TextView) findViewById( R.id.tv_time );
		Configuration config = SystemManager.getInstance().getConfiguration( 39 ) ;
		if( config != null && !config.value.equals("") ){
			tv_time.setText( config.value  );
		}
		
		ll_chat_action_box = (LinearLayout) _this
				.findViewById(R.id.ll_chat_action_box);

		// 切换
		ib_switch_sort = (ImageButton) _this.findViewById(R.id.ib_switch_sort);
		// 发布的消息
		et_post_msg = (EditText) _this.findViewById(R.id.et_post_msg);
		// 笑脸
		ib_post_smile = (ImageButton) _this.findViewById(R.id.ib_post_smile);
		// 按住讲话
		// 语音
		mSensor = new SoundMeter();
		iv_size = (ImageView) findViewById(R.id.iv_size);
		bt_talk = (Button) _this.findViewById(R.id.bt_talk);
		i_voice = (View) _this.findViewById(R.id.i_voice);
		tv_count_down_voice_time = (TextView) findViewById( R.id.tv_count_down_voice_time );
		bt_talk.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (Environment.getExternalStorageDirectory().exists()) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						if (flag == 1) {
							flag = 2;
							istimeout   = false;
							i_voice.setVisibility(View.VISIBLE);

							path = IOUtil.FILEPATH + "/amr/";
							startVoiceT = System.currentTimeMillis();
							voiceName = startVoiceT + ".amr";
							voicefile = path + voiceName;
							new File(path).mkdirs();
							mSensor.start(path, voiceName);
							mHandler.postDelayed(mPollTask, 300);
							mHandler.postDelayed(count_down_voice_time, 1000);
						}
						break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_CANCEL:
						if (flag == 2 && !istimeout ) {
							postVoice();
						}
						flag = 1;
						
						break;
					default:
						break;
					}

				}
				return false;
			}
		});
		// 发送
		ib_post_send = (ImageButton) _this.findViewById(R.id.ib_post_send);
		ib_post_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String et_post_msgVal = et_post_msg.getText().toString().trim();
				if (et_post_msgVal.equals(""))
					return;
				try {
					sendMessage(et_post_msgVal, 0);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();

					return;
				}
				et_post_msg.setText("");
			}
		});
		// 选择其他项目
		ib_post_select = (ImageButton) _this.findViewById(R.id.ib_post_select);

		// 底部的盒子内容
		// 附加操作
		ll_bottom_action_box = (LinearLayout) _this
				.findViewById(R.id.ll_bottom_action_box);

		rl_emoji_action_box = (LinearLayout) _this
				.findViewById(R.id.rl_emoji_action_box);
		vp_emoji = (ViewPager) _this.findViewById(R.id.vp_emoji);
		ll_select_origin = (LinearLayout) _this
				.findViewById(R.id.ll_select_origin);

		// 语音和文本的切换
		//if( isfriend < 3 ){
			ib_switch_sort.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!isTalk) {
						isTalk = true;
						bt_talk.setVisibility(View.VISIBLE);
						et_post_msg.setVisibility(View.GONE);
						ib_post_smile.setVisibility(View.GONE);
						ib_switch_sort
								.setImageResource(R.drawable.dhck_sendtext_select);
	
						et_post_msg.clearFocus();
						et_post_msg.setCursorVisible(false);
						_setEditBackGround(false);
						_closeKeybordShow(false);
					} else {
						isTalk = false;
						bt_talk.setVisibility(View.GONE);
						et_post_msg.setVisibility(View.VISIBLE);
						ib_post_smile.setVisibility(View.VISIBLE);
						ib_switch_sort
								.setImageResource(R.drawable.dhck_sendvoice_select);
						et_post_msg.requestFocus();
						et_post_msg.setFocusable(true);
						et_post_msg.setCursorVisible(true);
						_setEditBackGround(true);
						_closeKeybordShow(true);
					}
					if (ll_bottom_action_box.getVisibility() == View.VISIBLE) {
	
						ll_bottom_action_box.setVisibility(View.GONE);
						bottomBoxIsShow = false;
					}
					if (rl_emoji_action_box.getVisibility() == View.VISIBLE) {
						rl_emoji_action_box.setVisibility(View.GONE);
						SmileBoxIsShow = false;
					}
				}
			});
		//}
		// 附加功能
		ib_post_select.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (ll_bottom_action_box.getVisibility() != View.VISIBLE) {
					_closeKeybordShow(false);
					et_post_msg.clearFocus();
					et_post_msg.setCursorVisible(false);
					_setEditBackGround(false);
					ll_bottom_action_box.setVisibility(View.VISIBLE);
					bottomBoxIsShow = true;
				} else {
					ll_bottom_action_box.setVisibility(View.GONE);
					bottomBoxIsShow = false;
					et_post_msg.requestFocus();
					et_post_msg.setFocusable(true);
					et_post_msg.setCursorVisible(true);
					_setEditBackGround(true);
					_closeKeybordShow(true);
				}

				if (rl_emoji_action_box.getVisibility() == View.VISIBLE) {
					rl_emoji_action_box.setVisibility(View.GONE);
					SmileBoxIsShow = false;
				}

				if (isTalk) {
					isTalk = false;
					bt_talk.setVisibility(View.GONE);
					et_post_msg.setVisibility(View.VISIBLE);
					ib_post_smile.setVisibility(View.VISIBLE);
					ib_switch_sort
							.setImageResource(R.drawable.dhck_sendvoice_select);
				}

			}
		});
		// 发布的消息获得焦点
		et_post_msg.setOnTouchListener( new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (ll_bottom_action_box.getVisibility() == View.VISIBLE) {
					ll_bottom_action_box.setVisibility(View.GONE);
					bottomBoxIsShow = false;
				}
				if (rl_emoji_action_box.getVisibility() == View.VISIBLE) {
					rl_emoji_action_box.setVisibility(View.GONE);
					SmileBoxIsShow = false;
				}
				Editable text = et_post_msg.getText();
				Spannable spanText = (Spannable)text;
				Selection.setSelection(spanText, text.length());
				et_post_msg.setEnabled( true );
				et_post_msg.requestFocus();
				et_post_msg.setFocusable(true);
				et_post_msg.setCursorVisible(true);
				_setEditBackGround(true);
				_closeKeybordShow(true);
				return false;
			}
		});
		// 追加文字监听
		et_post_msg.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				int et_post_msgVal = et_post_msg.getText().toString().trim()
						.length();
				if (et_post_msgVal > 0) {
					ib_post_select.setVisibility(View.GONE);
					ib_post_send.setVisibility(View.VISIBLE);
				} else {
					ib_post_select.setVisibility(View.VISIBLE);
					ib_post_send.setVisibility(View.GONE);
				}
			}
		});
		// 表情图片点击
		ib_post_smile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (rl_emoji_action_box.getVisibility() == View.VISIBLE) {
					rl_emoji_action_box.setVisibility(View.GONE);
					_closeKeybordShow(true);
					SmileBoxIsShow = false;
				} else {
					_closeKeybordShow(false);
					rl_emoji_action_box.setVisibility(View.VISIBLE);
					SmileBoxIsShow = true;
				}
				et_post_msg.requestFocus();
				et_post_msg.setFocusable(true);
				et_post_msg.setCursorVisible(true);
				_setEditBackGround(true);

				// 附件功能隐藏
				if (ll_bottom_action_box.getVisibility() == View.VISIBLE) {

					ll_bottom_action_box.setVisibility(View.GONE);
					bottomBoxIsShow = false;
				}
			}
		});
		// 如果是商家将隐藏付款
		//if (user.isbusiness > 0) {
		if ( isbusiness <= 0 ) { //判断对方是商家都可以付款 or 对方不是商家就隐藏
			findViewById(R.id.bt_card).setVisibility(View.INVISIBLE);
			Button ib_pay = (Button) findViewById(R.id.ib_pay);
			ib_pay.setText("地址");
			Drawable drawable = getResources().getDrawable(
					R.drawable.dhck_icon_address_select);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			ib_pay.setCompoundDrawables(null, drawable, null, null);
			Button ib_address = (Button) findViewById(R.id.ib_address);
			ib_address.setText("名片");
			drawable = getResources().getDrawable(
					R.drawable.dhck_icon_card_select);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			ib_address.setCompoundDrawables(null, drawable, null, null);
		}

		_initEmojiView();
		_initEmojiPoint();
		_initEmojiData();

		// 判断是否已经链接
		if (GezitechApplication.connection == null
				|| (!GezitechApplication.connection.isConnected())
				|| (XmppConnectionManager.getInstance().getConnection() != null && !XmppConnectionManager
						.getInstance().getConnection().isConnected())) {
			XmppConnectionManager.getInstance().login();
		}
		try {
			chat = XmppConnectionManager.getInstance().getConnection()
					.getChatManager().createChat(uid + "@shout/android", null);
		} catch (Exception ex) {

		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.NEW_MESSAGE_ACTION);
		filter.addAction(Constant.UPDATE_CONTACTS_ACTION);
		registerReceiver(receiver, filter);
		_initChatContent();
		
		//如果是客服发送欢迎消息
		if( isfriend == 3 ){
			
			try {
				Chat chat = ChatManager.getInstance().getChatItem(uid, hyhid);
				if( chat == null ){
					sendMessage( String.format(_this.getResources().getString(R.string.kefu_replay), username ), 0, 1 );
					ring();
				}
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	public void sendMessage( String messageContent, int type ) throws Exception {
		sendMessage( messageContent , type,  0  );
	}
	// 发送消息 和 存储消息
	public void sendMessage(String messageContent, int type, int sort ) throws Exception {
		// 判断是否是喊一喊消息 或者 是否是系统的提示消息
		ArrayList<GezitechEntity_I> list = chatContentAdapter.getList();
		if (list != null && list.size() > 0) {
			ChatContent cc = (ChatContent) list.get(list.size() - 1);
			if (cc.type == 9) {// 是喊一喊数据
				Response response = new Response(new String(cc.body));
				JSONObject root = response.asJSONObject();
				hyhid = root.has("id") ? root.getLong("id") : 0;
			} else if (cc.type == 10 && isfriend != 1) {// 不是朋友
				final YMDialog2 ymdialog = new YMDialog2(_this);
				ymdialog.setHead("提示");
				ymdialog.setHintMsg("客户喊一喊已取消,不能再回话,谢谢!");
				ymdialog.setCloseButton(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ymdialog.dismiss();
					}
				});
				return;
			}

		}

		long ctime = System.currentTimeMillis();
		// 发送消息
		org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
		message.setBody(messageContent);
		message.setProperty("msgtype", type);
		// if( hyhId> 0 ){//上面是喊一喊数据 则回复消息带喊一喊的id数据
		message.setProperty("hyhid", hyhid);
		// }
		chat.sendMessage(message);

		// 存储聊天
		Chat chat = ChatManager.getInstance().getChatItem(uid, hyhid);
		if (chat == null) {
			chat = new Chat();
			chat.uid = uid;
			chat.username = username;
			chat.isfriend = isfriend;
			chat.lastcontent = ChatUtils.getTypeStr(messageContent, type);
			chat.head = head;
			chat.ctime = ctime;
			chat.istop = friend != null ? friend.istop : 0;
			chat.myuid = user.id;
			chat.hyhid = hyhid;
			chatDb.insert(chat);
		} else {
			chat.lastcontent = ChatUtils.getTypeStr(messageContent, type);
			chat.ctime = System.currentTimeMillis();
			chat.isfriend = isfriend;
			chatDb.save(chat);
		}
		// 存储聊天记录
		ChatContent chantContent = new ChatContent();
		chantContent.chatid = uid;
		chantContent.type = type;
		chantContent.ctime = ctime;
		chantContent.body = messageContent;
		chantContent.uid =  sort == 0 ? user.id : uid ;;
		chantContent.isfriend = isfriend;
		chantContent.myuid =  user.id;
		chantContent.hyhid = hyhid;
		if(  sort > 0 ){
			chantContent.iswelcome = 1;
		}
		
		chatContentDB.insert(chantContent);

		// 发新消息 通知界面
		chatContentAdapter.addItem(chantContent, false);
		list_view
				.setSelection(chatContentAdapter.list != null ? chatContentAdapter.list
						.size() - 1 : 0);

		// 通知聊天列表的广播
		Intent intent = new Intent();
		intent.setAction(Constant.SEND_UPDATE_MESSAGE_ACTION);
		Bundle bundle = new Bundle();
		bundle.putSerializable(Constant.CHAT_CONTENT, chantContent);
		intent.putExtras(bundle);
		sendBroadcast(intent);

	}

	private void postVoice() {
		tv_count_down_voice_time.setVisibility(View.GONE);
		i_voice.setVisibility(View.GONE);
		mHandler.removeCallbacks(mPollTask);
		mHandler.removeCallbacks(count_down_voice_time);
		
		mSensor.stop();
		iv_size.setImageResource(R.drawable.fbhyh_sound_01);
		endVoiceT = System.currentTimeMillis();
		final long time = endVoiceT - startVoiceT;
		if (time < 1000) {

			Toast("语音太短,请重新发布");
			try {
				if (!new File(voicefile).getParentFile().exists()) {
					new File(voicefile).getParentFile().mkdirs();
				}
				if (new File(voicefile).exists()) {
					new File(voicefile).delete();
				}
			} catch (Exception ex) {
			}

		} else {
			try {
				File f = new File(voicefile);
				RequestParams params = new RequestParams();
				params.put("speech", f);
				GezitechAlertDialog.loadDialog(_this);
				SystemManager.getInstance().phonetic(params,
						new OnAsynInsertListener() {

							@Override
							public void OnAsynRequestFail(String errorCode,
									String errorMsg) {
								GezitechAlertDialog.closeDialog();
								Toast(errorMsg);
							}

							@Override
							public void onInsertDone(String id) {
								GezitechAlertDialog.closeDialog();
								String voiceJSON = "{\"audiolength\":\"" + time
										+ "\",\"audiourl\":\"" + id + "\"}";
								try {
									sendMessage(voiceJSON, 2);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		flag = 1;
	}

	// 初始化聊天的内容
	private void _initChatContent() {

		chatContentList = ChatManager.getInstance().getChatContentList(uid,
				pageSize, hyhid);

		chatContentAdapter = new ChatContentAdapter(_this, list_view, head, uid);

		list_view.setAdapter(chatContentAdapter);

		list_view.footerShowState(2);
		list_view.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				page++;
				thread = new Thread(new Runnable() {

					@Override
					public void run() {
						GezitechEntity_I firstItem = chatContentAdapter
								.getItemList(0);
						chatContentList = ChatManager
								.getInstance()
								.getChatContentList(
										uid,
										pageSize,
										hyhid );
						handler.obtainMessage(UPDATE_CHAT_CONTENT)
								.sendToTarget();
						if (thread != null && !thread.isInterrupted())
							thread.interrupt();
					}
				});
				thread.start();
			}
		});
		if (chatContentList != null && chatContentList.size() > 0) {
			chatContentAdapter.addList(chatContentList, true);
			list_view.setSelection(chatContentList.size() - 1);
		}
		mHandler.postDelayed(countdown, 1000);
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
				if (chatContentList != null && chatContentList.size() > 0) {
					chatContentAdapter.addList(chatContentList, true);
				}
				list_view.onRefreshComplete();

				break;

			}

			return false;
		}
	});

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		if (bottomBoxIsShow) {
			ll_bottom_action_box.setVisibility(View.GONE);
			bottomBoxIsShow = false;
			et_post_msg.requestFocus();
			et_post_msg.setFocusable(true);
			et_post_msg.setCursorVisible(true);
			_setEditBackGround(true);
			_closeKeybordShow(true);
		} else if (SmileBoxIsShow) {
			rl_emoji_action_box.setVisibility(View.GONE);
			SmileBoxIsShow = false;
			et_post_msg.requestFocus();
			et_post_msg.setFocusable(true);
			et_post_msg.setCursorVisible(true);
			_setEditBackGround(true);
			_closeKeybordShow(false);

		} else {
			_this.finish();
		}
	}

	// 判断软键盘是否打开 如果打开关闭
	private void _closeKeybordShow(boolean isOpen) {
		try {
			// 判断软键盘是否打开
			InputMethodManager im = (InputMethodManager) _this
					.getSystemService(Context.INPUT_METHOD_SERVICE);

			if (!isOpen) {
				im.hideSoftInputFromWindow(et_post_msg.getWindowToken(), 0);
			} else {
				im.showSoftInput(et_post_msg, InputMethodManager.SHOW_FORCED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void _setEditBackGround(boolean isFocus) {
		if (isFocus) {
			et_post_msg.setBackgroundResource(R.drawable.common_inputbox_on);
		} else {
			et_post_msg
					.setBackgroundResource(R.drawable.common_inputbox_normal);
		}
	}

	// 上传图片
	public void uploadImage(View view) {
		if (ll_bottom_action_box.getVisibility() == View.VISIBLE) {
			ll_bottom_action_box.setVisibility(View.GONE);
			bottomBoxIsShow = false;
		}
		_this.startActivityForResult(new Intent(_this,
				SelectPicPopupWindow.class), 10002);
		_this.overridePendingTransition(R.anim.out_to_down, R.anim.exit_anim);

	}

	// 账单 创建账单
	// 修改为 订单中心
	// 商家就是商家的订单
	// 用户就是自己的账单
	public void billList(View view) {
		if (ll_bottom_action_box.getVisibility() == View.VISIBLE) {
			ll_bottom_action_box.setVisibility(View.GONE);
			bottomBoxIsShow = false;
		}
		Intent intent = new Intent(_this, OrderActivity.class);
		intent.putExtra("type", 1);
		intent.putExtra("uid", uid);
		intent.putExtra("title", "订单中心");
		startActivity(intent);
	}

	// 付款
	public void payAction(View view) {
		if (ll_bottom_action_box.getVisibility() == View.VISIBLE) {
			ll_bottom_action_box.setVisibility(View.GONE);
			bottomBoxIsShow = false;
		}
		//if (user.isbusiness <= 0) { // 非商家
		if( isbusiness > 0 ){
			Intent intent = new Intent(_this, CreatePayActivity.class);
			intent.putExtra("title", "创建付款");
			intent.putExtra("fid", uid);
			intent.putExtra("sid", hyhid);
			_this.startActivityForResult(intent, 1008);

			_this.overridePendingTransition(R.anim.out_to_down,
					R.anim.exit_anim);
		} else if ( isbusiness <=  0) { // 商家 调整到 地址
		//} else if (user.isbusiness > 0) { // 商家 调整到 地址	

			shareAddress();
		}
	}

	private void shareAddress() {
		_this.startActivityForResult(new Intent(_this, MyMapActivity.class),
				1005);
		_this.overridePendingTransition(R.anim.out_to_down, R.anim.exit_anim);
	}

	// 地址分享
	public void addressShare(View view) {
		if (ll_bottom_action_box.getVisibility() == View.VISIBLE) {
			ll_bottom_action_box.setVisibility(View.GONE);
			bottomBoxIsShow = false;
		}
		//if (user.isbusiness <= 0) {
		if ( isbusiness > 0) {
			shareAddress();
		} else {
			cardShareCommon();
		}

	}

	private void cardShareCommon() {
		String body = "{\"nickname\":\""
				+ (FieldVal.value(user.nickname).equals("") ? user.username
						: user.nickname)
				+ "\",\"head\":\""
				+ user.head
				+ "\",\"shopname\":\""
				+ user.company_shopname
				+ "\",\"phone\":\""
				+ user.phone
				+ "\",\"address\":\""
				+ (FieldVal.value(user.address).equals("") ? "未填写"
						: user.address) + "\"}";
		try {
			sendMessage(body, 4);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 名片分享
	public void cardShare(View view) {
		//if ( user.isbusiness > 0)
		if ( isbusiness <= 0)
			return;
		if (ll_bottom_action_box.getVisibility() == View.VISIBLE) {
			ll_bottom_action_box.setVisibility(View.GONE);
			bottomBoxIsShow = false;
		}
		cardShareCommon();
	}

	// 初始化表情的视图
	private void _initEmojiView() {
		emojis = new Emotion().getParseEmojiList();
		pageViews = new ArrayList<View>();
		faceAdapters = new ArrayList<FaceAdapter>();
		for (int i = 0; i < emojis.size(); i++) {
			GridView view = new GridView(this);
			FaceAdapter adapter = new FaceAdapter(this, emojis.get(i));
			view.setAdapter(adapter);
			faceAdapters.add(adapter);
			view.setOnItemClickListener(this);
			view.setNumColumns(7);
			view.setBackgroundColor(Color.TRANSPARENT);
			view.setHorizontalSpacing(10);
			view.setVerticalSpacing(1);
			view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			view.setCacheColorHint(0);
			view.setPadding(5, 20, 5, 10);
			view.setSelector(new ColorDrawable(Color.TRANSPARENT));
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			view.setGravity(Gravity.CENTER);
			pageViews.add(view);
		}

	}

	// 初始化游标
	private void _initEmojiPoint() {
		// ll_select_origin
		pointViews = new ArrayList<ImageView>();
		ImageView imageView;
		for (int i = 0; i < pageViews.size(); i++) {
			imageView = new ImageView(this);
			imageView.setBackgroundResource(R.drawable.d1);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 10;
			layoutParams.rightMargin = 10;
			layoutParams.width = 8;
			layoutParams.height = 8;
			ll_select_origin.addView(imageView, layoutParams);
			if (i == 0) {
				imageView.setBackgroundResource(R.drawable.d2);
			}
			pointViews.add(imageView);
		}
	}

	/**
	 * 填充数据
	 */
	private void _initEmojiData() {
		vp_emoji.setAdapter(new ViewPagerAdapter(pageViews));

		vp_emoji.setCurrentItem(0);
		current = 0;
		vp_emoji.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				current = arg0;
				draw_Point(arg0);
				vp_emoji.setCurrentItem(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}

	/**
	 * 绘制游标背景
	 */
	public void draw_Point(int index) {
		for (int i = 0; i < pointViews.size(); i++) {
			if (index == i) {
				pointViews.get(i).setBackgroundResource(R.drawable.d2);
			} else {
				pointViews.get(i).setBackgroundResource(R.drawable.d1);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Emotion emoji = (Emotion) faceAdapters.get(current).getItem(arg2);
		if (emoji.drawable == R.drawable.face_del_icon_select) {
			int selection = et_post_msg.getSelectionStart();
			String text = et_post_msg.getText().toString();
			if (selection > 0) {
				String text2 = text.substring(selection - 1);
				if ("]".equals(text2)) {
					int start = text.lastIndexOf("[");
					int end = selection;
					et_post_msg.getText().delete(start, end);
					return;
				}
				et_post_msg.getText().delete(selection - 1, selection);
			}
		}
		if (!TextUtils.isEmpty(emoji.title)) {
			et_post_msg.append(emoji.emotion);
		}
	}

	private String ImageName;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		String action = "";
		if (requestCode != 1004) {
			if (data == null)
				return;
			action = data.getAction();
		}
		Bitmap originBitMap = null, reduceBitMap = null;
		File originFile = null;
		String filePath = null;
		switch (requestCode) {
		case 1001:// 如果是删除聊天 则回调改变界面

			break;
		case 10002:// 选择上传图片
			if (action.equals("10001")) {// 拍照
				ImageName = "/" + DateUtils.getStringToday() + ".jpg";
				// 启动相机
				Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// 来存储图片
				// Uri.fromFile 把文件路径地址转为url地址
				// Uri.parse(uriString); 把url地址转为文件存储地址
				intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri
						.fromFile(new File(Environment
								.getExternalStorageDirectory(), ImageName)));
				startActivityForResult(intent1, 1004);

			} else if (action.equals("10002")) {// 相册
				Intent localIntent = new Intent();
				localIntent.setType("image/*");
				localIntent.setAction("android.intent.action.GET_CONTENT");
				Intent localIntent2 = Intent.createChooser(localIntent, "选择相片");
				startActivityForResult(localIntent2, 1003);
			}
			break;
		case 1003:
			
			ContentResolver resolver3 = getContentResolver();
			Uri selectedImageUri3 = data.getData();

			if (selectedImageUri3 != null) {
				FileInputStream fis = null;
				try {
					 fis = (FileInputStream) resolver3.openInputStream(selectedImageUri3);
					 
					 byte[] buffer = new byte[fis.available()];
					 fis.read(buffer);
					 
					 originBitMap = ImageUtil.PicZoom2(buffer,600,600 );
					 
					 originFile = IOUtil.makeLocalImage(originBitMap, null);
					 buffer = null;
					
				} catch (Exception e) {
					 e.printStackTrace();
				} finally {
					 try {
						 if (fis != null)
						    fis.close();
						 	if( originBitMap.isRecycled() ) 
						 		originBitMap.recycle();
						 	 System.gc();
					   	} catch (IOException e) {
					   		e.printStackTrace();
					   }
				}
				RequestParams params = new RequestParams();
				try {
					params.put("litpic", originFile);
					GezitechAlertDialog.loadDialog(this);
					uploadPicCallBack(params, new OnAsynInsertListener() {

						@Override
						public void OnAsynRequestFail(String errorCode,
								String errorMsg) {
						}

						@Override
						public void onInsertDone(String id) {
							try {
								sendMessage(id, 1);
								
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			break;
		case 1004:
			try {
				File picture = new File(
						Environment.getExternalStorageDirectory() + ImageName);

				FileInputStream fis = null;
				/*originBitMap = BitmapFactory.decodeFile(picture.getPath());

				int scale = ImageUtil.reckonThumbnail(originBitMap.getWidth(),
						originBitMap.getHeight(), 600, 600);
				originBitMap = ImageUtil.PicZoom(originBitMap,
						(int) (originBitMap.getWidth() / scale),
						(int) (originBitMap.getHeight() / scale));
				originFile = IOUtil.makeLocalImage(originBitMap, null);*/
				try {
					 fis = new FileInputStream( picture );
					 byte[] buffer = new byte[fis.available()];
					 fis.read(buffer);
					 
					 
					 originBitMap = ImageUtil.PicZoom2(buffer,600,600 );
					 
					 
					 originFile = IOUtil.makeLocalImage(originBitMap, null);
					 
					 buffer = null;
					 
				} catch (Exception e) {
					 e.printStackTrace();
				} finally {
					 try {
						 if (fis != null)
							
						    fis.close();
						 	if( originBitMap.isRecycled() ) 
						 		originBitMap.recycle();
						 	 System.gc();
					   	} catch (IOException e) {
					   		e.printStackTrace();
					   }
				}
				
				

				RequestParams params = new RequestParams();
				params.put("litpic", originFile);
				GezitechAlertDialog.loadDialog(this);
				uploadPicCallBack(params, new OnAsynInsertListener() {

					@Override
					public void OnAsynRequestFail(String errorCode,
							String errorMsg) {
					}

					@Override
					public void onInsertDone(String id) {
						try {
							sendMessage(id, 1);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 1005:// 地图

			RequestParams params = new RequestParams();
			try {
				params.put("litpic", new File(action));
				GezitechAlertDialog.loadDialog(_this);
				params.put("w", 300);
				params.put("h", 300);
				uploadPicCallBack(params, new OnAsynInsertListener() {
					@Override
					public void OnAsynRequestFail(String errorCode,
							String errorMsg) {
					}

					@Override
					public void onInsertDone(String id) {
						GezitechAlertDialog.closeDialog();
						BDLocation l = GezitechApplication.getInstance()
								.getLocation();
						String body = "{\"city\":\"" + l.getCity()
								+ "\",\"locationAddress\":\"" + l.getAddrStr()
								+ "\",\"latitude\":" + l.getLatitude()
								+ ",\"locationPic\":\"" + id
								+ "\",\"longitude\":" + l.getLongitude()
								+ ",\"province\":\"" + l.getProvince() + "\"}";
						try {
							sendMessage(body, 3);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case 1006: // 创建账单的返回
			Bundle bundle = data.getExtras();
			Bill bill = (Bill) bundle.getSerializable("bill");
			String body = "{\"id\":" + bill.id + ",\"tradecode\":\""
					+ bill.tradecode + "\",\"notes\":\"" + bill.notes
					+ "\",\"money\":\"" + bill.money + "\",\"ctime\":"
					+ bill.ctime + ",\"activetime\":" + bill.activetime + "}";
			try {
				sendMessage(body, 5);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 1007: // 账单的支付返回 账单已经付款
			if (!action.equals("1"))
				return;
			Bundle bundle1 = data.getExtras();
			Bill bill2 = (Bill) bundle1.getSerializable("bill");
			String body1 = "{\"id\":" + bill2.id + ",\"tradecode\":\""
					+ bill2.tradecode + "\",\"notes\":\"" + bill2.notes
					+ "\",\"money\":\"" + bill2.money + "\",\"ctime\":"
					+ bill2.ctime + ",\"activetime\":" + bill2.activetime + "}";
			try {
				sendMessage(body1, 6);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 1008: // 创建付款
			Bundle bundle3 = data.getExtras();
			Bill bill3 = (Bill) bundle3.getSerializable("bill");
			String body3 = "{\"id\":" + bill3.id + ",\"tradecode\":\""
					+ bill3.tradecode + "\",\"notes\":\"" + bill3.notes
					+ "\",\"money\":\"" + bill3.money + "\",\"paytime\":"
					+ bill3.paytime + ",\"activechecktime\":"
					+ bill3.activechecktime + "}";
			try {
				sendMessage(body3, 8);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 1009: // 确定收款
			Bundle bundle4 = data.getExtras();
			Bill bill4 = (Bill) bundle4.getSerializable("bill");
			String body4 = "{\"id\":" + bill4.id + ",\"tradecode\":\""
					+ bill4.tradecode + "\",\"notes\":\"" + bill4.notes
					+ "\",\"money\":\"" + bill4.money + "\",\"paytime\":"
					+ bill4.paytime + ",\"activechecktime\":"
					+ bill4.activechecktime + "}";
			try {
				sendMessage(body4, 7);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}

	// 新消息的接受广播
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constant.NEW_MESSAGE_ACTION.equals(action)) {// 返回解接受的消息

				Bundle bundle = intent.getExtras();
				ChatContent chatContent = (ChatContent) bundle
						.getSerializable(Constant.CHAT_CONTENT);

				if (chatContent == null || uid != chatContent.chatid
						|| chatContent.type == 9 || (chatContent.hyhid>0 && hyhid >0 && chatContent.hyhid != hyhid ) )
					return;
				// 发新消息 通知界面
				// Toast(chatContent.body);
				chatContentAdapter.addItem(chatContent, false);
				list_view
						.setSelection(chatContentAdapter.list != null ? chatContentAdapter.list
								.size() - 1 : 0);

			} else if (Constant.UPDATE_CONTACTS_ACTION.equals(action)) {// 删除聊天记录
																		// 或者删除联系人的广播
																		// 刷新当前的界面
																		// 重新适配
				if (chatContentAdapter != null) {
					chatContentAdapter.removeAll();
				}
			}
		}

	};

	// 上传图片文件的回调处理
	public void uploadPicCallBack(RequestParams params,
			final OnAsynInsertListener listener) {

		SystemManager.getInstance().fileclear(params,
				new OnAsynInsertListener() {

					@Override
					public void OnAsynRequestFail(String errorCode,
							String errorMsg) {
						Toast(errorMsg);
						GezitechAlertDialog.closeDialog();
					}

					@Override
					public void onInsertDone(String id) {

						GezitechAlertDialog.closeDialog();
						if (id.equals(""))
							return;
						listener.onInsertDone(id);
					}
				});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		if (thread != null && !thread.isInterrupted())
			thread.interrupt();
		GezitechApplication.currUid = 0;
		GezitechApplication.hyhId  =0;
		unregisterReceiver(receiver);
		if (chatContentAdapter != null)
			chatContentAdapter.stopAudio();
		mHandler.removeCallbacks(countdown);
		mHandler.removeCallbacks(hyhtime);
		mHandler.removeCallbacks(count_down_voice_time);
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
	}

	private Runnable mPollTask = new Runnable() {
		public void run() {
			double amp = mSensor.getAmplitude();
			updateDisplay(amp);
			mHandler.postDelayed(mPollTask, 300);
		}
	};

	private void updateDisplay(double signalEMA) {

		switch ((int) signalEMA) {
		case 0:
		case 1:
			iv_size.setImageResource(R.drawable.fbhyh_sound_01);
			break;
		case 2:
		case 3:
			iv_size.setImageResource(R.drawable.fbhyh_sound_02);
			break;
		case 4:
		case 5:
		case 6:
			iv_size.setImageResource(R.drawable.fbhyh_sound_03);
			break;
		case 7:
		case 8:
		case 9:
			iv_size.setImageResource(R.drawable.fbhyh_sound_04);
			break;
		case 10:
		case 11:
		default:
			iv_size.setImageResource(R.drawable.fbhyh_sound_05);
			break;
		}
	}

	private Runnable countdown = new Runnable() {
		public void run() {
			chatContentAdapter.refreshcountdown(list_view);

			mHandler.postDelayed(countdown, 1000);
		}
	};

	private void surplusTime() {
		long hyh_uid = 0;
		try {
			Response response = new Response(new String(hyhbody));
			JSONObject root = response.asJSONObject();
			hyh_uid = root.has("uid") ? root.getLong("uid") : 0;
		} catch (JSONException e) {

		} catch (GezitechException e) {

		}
		RequestParams params = new RequestParams();
		params.put("uid", hyh_uid  );
		params.put("bid", user.isbusiness >0 ? user.id : uid );
		params.put("sid", hyhid);
		ShoutManager.getInstance().getShoutSessionTime(params,
				new OnAsynInsertListener() {

					@Override
					public void OnAsynRequestFail(String errorCode,
							String errorMsg) {
						Toast(errorMsg);
					}

					@Override
					public void onInsertDone(String id) {
						ll_time_box.setVisibility(View.VISIBLE);
						if (id.equals("0")) {
							tv_time_val.setText("已经结束");
						} else if (id.equals("-1")) {
							tv_time_val.setText("不限制");
						} else {
							hyhtimeVal = Long.parseLong(id);
							tv_time_val.setText(getDateStr(hyhtimeVal));

							mHandler.postDelayed(hyhtime, 1000);

						}
					}
				});
	}

	private String getDateStr(long time) {
		long h = time / 3600;
		long m = (time % 3600) / 60;
		long s = (time % 3600) % 60;
		return (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":"
				+ (s < 10 ? "0" + s : s);
	}

	private Runnable hyhtime = new Runnable() {
		public void run() {
			hyhtimeVal = hyhtimeVal - 1;
			if (hyhtimeVal <= 0) {
				tv_time_val.setText("已结束");
				try {
					Response response = new Response(new String(hyhbody));
					JSONObject root = response.asJSONObject();
					long hyh_uid = root.has("uid") ? root.getLong("uid") : 0;
					// 如果喊一喊是当前用户则提示过期
					if (user != null && hyh_uid == user.id) {
						final YMDialog2 ym2 = new YMDialog2(_this);
						ym2.setHead("提示").setHintMsg("当前喊一喊已经结束")
								.setConfigText("确定")
								.setCloseButton(new OnClickListener() {

									@Override
									public void onClick(View v) {
										ym2.dismiss();
									}
								});
					}
				} catch (JSONException e) {

				} catch (GezitechException e) {

				}
				mHandler.removeCallbacks(hyhtime);
			} else {
				tv_time_val.setText(getDateStr(hyhtimeVal));
				mHandler.postDelayed(hyhtime, 1000);
			}

		}
	};
	private Runnable count_down_voice_time = new Runnable() {
		public void run() {

			endVoiceT = System.currentTimeMillis();
			long time = endVoiceT - startVoiceT;
			if ((time / 1000) >= Conf.vioceTime - 10
					&& (time / 1000) <= Conf.vioceTime) {// 发送
				tv_count_down_voice_time.setVisibility(View.VISIBLE);
				tv_count_down_voice_time.setText("还剩下 "
						+ (Conf.vioceTime - (time / 1000)) + " 秒");
				if ((time / 1000) == Conf.vioceTime) {// 当时间等于60秒

					mHandler.removeCallbacks(count_down_voice_time);
					
					istimeout = true;
					postVoice();
				}
			}
			mHandler.postDelayed(count_down_voice_time, 1000);
		}
	};
	
	
	private void ring() throws Exception, IOException {
		// TODO Auto-generated method stub
		// Uri alert =
		// RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		AssetManager assetManager = getAssets();

		File file = new File(Environment.getExternalStorageDirectory(),
				"/myRingtonFolder/Audio/");
		if (!file.exists()) {
			file.mkdirs();
		}
		File out = new File(file + "/", "soundmp3.mp3");
		if (!out.exists()) {
			try {
				copyAssetsToFilesystem(assetManager.open("soundmp3.mp3"),
						new FileOutputStream(out));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Uri urii = Uri.parse(out.getPath());

		Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), urii);

		r.play();
	}
	private boolean copyAssetsToFilesystem(InputStream istream,
			OutputStream ostream) {
		// Log.i(tag, "Copy "+assetsSrc+" to "+des);
		// InputStream istream = null;
		// OutputStream ostream = null;
		try {
			// AssetManager am = context.getAssets();
			// istream = am.open(assetsSrc);
			// ostream = new FileOutputStream(des);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = istream.read(buffer)) > 0) {
				ostream.write(buffer, 0, length);
			}
			istream.close();
			ostream.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (istream != null)
					istream.close();
				if (ostream != null)
					ostream.close();
			} catch (Exception ee) {
				ee.printStackTrace();
			}
			return false;
		}
		return true;
	}
}