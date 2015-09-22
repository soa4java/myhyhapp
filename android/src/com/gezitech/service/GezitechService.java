package com.gezitech.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

//import com.baidu.android.pushservice.PushManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.basic.GezitechEntity;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynRequestFailListener;
import com.gezitech.entity.User;
import com.gezitech.service.xmpp.IMChatService.LocalBinder;
import com.gezitech.service.lbs.NotificationUtil;
import com.gezitech.service.managers.ChatManager;
import com.gezitech.service.managers.SystemManager;
import com.gezitech.service.managers.UserManager;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.gezitech.service.xmpp.Constant;
import com.gezitech.service.xmpp.IMChatService;
import com.gezitech.service.xmpp.XmppConnectionManager;
import com.gezitech.util.IOUtil;
import com.gezitech.util.NetUtil;
import com.hyh.www.LoginActivity;
import com.hyh.www.R;
import com.hyh.www.ZhuyeActivity;
import com.hyh.www.chat.ChatUtils;
import com.hyh.www.entity.Chat;
import com.hyh.www.entity.ChatContent;
import com.hyh.www.entity.FieldVal;
import com.hyh.www.entity.Friend;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

/**
 * 格子科技客户端服务总线，提供对请求任务、Activity的集中管理
 * 
 * @author Administrator
 */
public class GezitechService extends Service implements ServiceConnection {
	private ArrayList<Activity> allActivity = new ArrayList<Activity>();
	private ArrayList<Runnable> queue = new ArrayList<Runnable>();// 请求任务队列
	private ArrayList<Runnable> active = new ArrayList<Runnable>();// 当前正在执行的请求任务
	private User currentUser = null;
	public static final int MAX_CONNECTIONS = 10;// 支持客户端最大并发请求数
	private static GezitechService instance;

	private GezitechService() {
		super();
		// bindBackgroundService(GezitechApplication.getContext());
	}

	/***
	 * 返回当前类的实例
	 * 
	 * @return
	 */
	public synchronized static GezitechService getInstance() {
		if (instance == null)
			instance = new GezitechService();
		return instance;
	}

	/***
	 * 请求队列
	 * 
	 * @param runnable
	 */
	public void push(Runnable runnable) {
		queue.add(runnable);
		if (active.size() < MAX_CONNECTIONS)
			startNextThread();
	}

	private void startNextThread() {
		if (!queue.isEmpty()) {
			Runnable nextThread = (Runnable) queue.get(0);
			queue.remove(0);
			active.add(nextThread);
			Thread thread = new Thread(nextThread);
			thread.start();
		}
	}

	/**
	 * 结束请求任务
	 * 
	 * @param runnable
	 */
	public void didComplete(Runnable runnable) {
		active.remove(runnable);
		startNextThread();
	}

	/***
	 * 把当前实例插入服务中
	 */
	public void appendActivity(Activity activity) {
		this.allActivity.add(activity);
	}

	/***
	 * 获取活动类
	 * 
	 * @param name
	 * @return
	 */
	public Activity getActivityByName(String name) {

		for (Activity ac : allActivity) {
			if (ac.getClass().getName().indexOf(name) >= 0) {
				return ac;
			}
		}
		return null;
	}

	/**
	 * 退出应用程序
	 * 
	 * @param context
	 */
	public void exitApp(Context context) {
		for (Activity ac : allActivity) {
			if (ac != null)
				ac.finish();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 清空数据库 清除缓存图片
	 */
	public void clearCache() {
		try {
			GezitechDBHelper<GezitechEntity> dbHelper = new GezitechDBHelper<GezitechEntity>(
					GezitechEntity.class);
			dbHelper.dropAllTables();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			String cacheDir = IOUtil.getCacheFilePath();
			IOUtil.deletePath(cacheDir);
			if (IOUtil.hasSDCard()) {
				cacheDir = IOUtil.getCacheFilePath(false);
				IOUtil.deletePath(cacheDir);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 获取当前登陆用户
	 * 
	 * @param context
	 * @return
	 */
	public User getCurrentLoginUser(Context context) {
		if (currentUser == null) {
			GezitechDBHelper<User> dbHelper = new GezitechDBHelper<User>(
					User.class);
			ArrayList<User> list = dbHelper.query("islogin=1", 1, "id desc");
			if (list == null || list.size() < 1)
				return null;
			currentUser = list.get(0);
			dbHelper.close();
		}
		return currentUser;
	}

	/**
	 * 退出用户登录
	 */
	public void clearCurrentUser() {
		GezitechService.getInstance().setCurrentUser(null);
		GezitechDBHelper<User> dbHelper = new GezitechDBHelper<User>(User.class);
		dbHelper.delete("islogin=1");
		this.currentUser = null;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	// 推送的数据回传 isLogin true 要刷新数据的回传
	public void pushInfo(boolean isLogin) {
		User user = getCurrentLoginUser(GezitechApplication.getContext());
		if (user == null)
			return;
		// 判断用户是否提交
		final SharedPreferences user_submit = GezitechApplication.getContext()
				.getSharedPreferences("baiduPushInfo_user_" + user.id,
						Context.MODE_PRIVATE);
		int isUserSubmit = user_submit.getInt("isUserSubmit", 0);
		if (!isLogin && isUserSubmit > 0)
			return; // 提交过
		// 百度推送需要的key
		SharedPreferences sp = GezitechApplication.getContext()
				.getSharedPreferences("baiduPushInfo", Context.MODE_PRIVATE);
		//百度
		//String push_user_id = sp.getString("push_user_id", "");
		//String push_channel_id = sp.getString("push_channel_id", "");
		//个推
		String clientid = sp.getString("clientid",  "" );
		
		// 提交数据到服务器
//		if (NetUtil.isNetworkAvailable() && push_user_id != null
//				&& !push_user_id.equals("")) {
		if( NetUtil.isNetworkAvailable() && clientid != null && !clientid.equals("") ){ 
			// 提交推送识别数据
			RequestParams params = new RequestParams();
			//百度
			params.put("push_type", "android");
			params.put("devicetoken", "");
			params.put("user_id", "");
			params.put("channel_id", "");
			//个推
			params.put("getui_clientid", clientid);
			if (user.isbusiness == 1) {
				Tag[] tags = new Tag[1];
				Tag t = new Tag();
                t.setName( "business" );
                tags[0] = t;
				///PushManager.setTags(GezitechApplication.getContext(), tags); 百度
				PushManager.getInstance().setTag(GezitechApplication.getContext(), tags); //个推
				// 商家
			} else {
				Tag[] tags = new Tag[1];
				Tag t = new Tag();
                t.setName( "user" );
                tags[0] = t;
				//PushManager.setTags(GezitechApplication.getContext(), tags);
				PushManager.getInstance().setTag(GezitechApplication.getContext(), tags); //个推
				// 用户
			}
			// 传递百度推送的标识数据到服务端
			com.gezitech.service.managers.PushManager.getInstance()
					.setPushInfo(params, new OnAsynRequestFailListener() {
						@Override
						public void OnAsynRequestFail(String errorCode,
								String errorMsg) {
							Log.v("channelId", "channelId====成功");
							if (errorCode.equals("1")) {// 成功
								SharedPreferences.Editor editor = user_submit
										.edit();
								editor.putInt("isUserSubmit", 1);
								editor.commit();
							} else {

							}
						}
					});

		}
	}

	// 更新经度纬度
	public void longitude() {
		longitude(null);
	}

	public void longitude(final CallBDLocation callBDLocation) {
		GezitechApplication.getInstance().getBDLocation(
				new BDLocationListener() {

					@Override
					public void onReceiveLocation(BDLocation arg0) {
						GezitechApplication.getInstance().setBDLocation(arg0);
						if (arg0.getLongitude() != 0) {

							RequestParams params = new RequestParams();
							String longs = arg0.getLongitude() + "";
							String lat = arg0.getLatitude() + "";
							String city = arg0.getCity();
							params.put("long", longs);
							params.put("lat", lat);
							params.put("city", city);
							UserManager.getInstance().longitude(params, null);
							Editor edit = GezitechApplication.systemSp.edit();
							edit.putString("long", longs);
							edit.putString("lat", lat);
							edit.putString("city", city);
							edit.commit();
							if (callBDLocation != null) {
								callBDLocation.callfunction(longs, lat, city);

							}
						}
					}
				});
	}

	// 加载配置文件
	public void configuration() {
		SystemManager.getInstance().configuration(new OnAsynGetListListener() {

			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {

			}

			@Override
			public void OnGetListDone(ArrayList<GezitechEntity_I> list) {

			}
		});
	}

	// 绑定后台推送服务
	public IMChatService bindBackgroundService(Context context) {
		if (isBound && service != null)
			return service;
		if (context == null)
			return null;
		Intent intent = new Intent(context, IMChatService.class);
		boolean res = context.bindService(intent, this,
				Context.BIND_AUTO_CREATE);
		return service;
	}

	// 解绑后台推送服务
	public void unbindBackgroundService(Context context) {
		if (context == null)
			return;
		try {
			context.unbindService(this);
		} catch (Exception ex) {

		}
		isBound = false;
		service = null;
	}

	IMChatService service;
	boolean isBound;

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		LocalBinder binder = (LocalBinder) service;
		GezitechService.this.service = binder.getService();
		isBound = true;
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {// 连接断开服务里的线程就会摧毁
		isBound = false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	// 通知处理
	// $key_type 1打开活动，2打开网页
	// $key_value 对应活动id，对应网页链接
	public void NotificationAction(String key_type, String key_value,
			String title, String description) {

		Context context = GezitechApplication.getContext();
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(ns);
		int icon = R.drawable.launcher_96;
		CharSequence tickerText = title;
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);

		// notification.defaults |= Notification.FLAG_NO_CLEAR;
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		// notification.defaults |= Notification.DEFAULT_SOUND;

		AssetManager assetManager = context.getAssets();

		File file = new File(Environment.getExternalStorageDirectory(),
				"/myRingtonFolder/Audio/");
		if (!file.exists()) {
			file.mkdirs();
		}
		File out = new File(file + "/", "soundmp3.mp3");
		if (!out.exists()) {
			try {
				NotificationUtil.copyAssetsToFilesystem(
						assetManager.open("soundmp3.mp3"),
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
		// notification.sound=Uri.parse("android.resource://" +
		// context.getPackageName() + "/" +R.raw.soundmp3);
		notification.sound = urii;

		// notification.defaults |= Notification.DEFAULT_VIBRATE;

		long[] vibrate = { 0, 0, 0, 0 };
		vibrate[0] = 0;
		vibrate[1] = 100;
		vibrate[2] = 100;
		vibrate[3] = 100;
		notification.vibrate = vibrate;

		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.putExtra("flag", "notification");
		intent.putExtra("title", title);
		intent.putExtra("key_value", key_value);
		intent.putExtra("key_type", key_type);

		User user = getCurrentLoginUser(GezitechApplication.getContext());
		if (user == null) {

			intent.setClass(GezitechApplication.getContext(),
					LoginActivity.class);

		} else {

			intent.setClass(GezitechApplication.getContext(),
					ZhuyeActivity.class);

		}

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent contextIntent = PendingIntent.getActivity(context, 0,
				intent, 0);

		notification.setLatestEventInfo(context, title, description,
				contextIntent);
		mNotificationManager.notify(1, notification);

		// 系统消息广播
		Intent intent2 = new Intent();
		intent2.setAction(Constant.SYSTEM_REQUEST);
		GezitechApplication.getContext().sendBroadcast(intent2);

	}

	// 经度纬度的回掉
	public interface CallBDLocation {
		void callfunction(String longs, String lat, String city);
	}

	// 发送消息订单 // 喜欢评论 发出消息 
	public static void sendMessage(long uid, int msgtype) {
		sendMessage(uid, msgtype, msgtype + "", 0);
	}
	public static void sendMessage(int msgtype,String body, HashMap<String, Long> uids ) {
		Iterator<Entry<String, Long>> iter = uids.entrySet().iterator();
		while (iter.hasNext()) {
			
			Map.Entry<String, Long> entry = (Map.Entry<String, Long>) iter.next();
			long key = entry.getValue();
			//Log.v("uids", key +"=uids====="+ body);
			sendMessage(key, msgtype, body, 0);
		}
		
	}
	// 订单 // 喜欢评论 发出消息
	public static void sendMessage(final long uid, final int msgtype,
			final String body, final long hyhid) {
		try {
			org.jivesoftware.smack.Chat chat = XmppConnectionManager
					.getInstance().getConnection().getChatManager()
					.createChat(uid + "@shout/android", null);

			// 发送消息
			org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
			message.setBody(body + "");
			message.setProperty("msgtype", msgtype);
			message.setProperty("hyhid", hyhid);
			chat.sendMessage(message);
			if (msgtype < 15) {
				ChatManager.getInstance().getFriendData(uid,
						new OnAsynGetOneListener() {

							@Override
							public void OnAsynRequestFail(String errorCode,
									String errorMsg) {
							}

							@Override
							public void OnGetOneDone(GezitechEntity_I entity) {
								Friend friend = (Friend) entity;
								long ctime = System.currentTimeMillis();
								User user = GezitechService.getInstance()
										.getCurrentLoginUser(
												GezitechApplication
														.getContext());
								// TODO Auto-generated method stub
								// 存储聊天
								GezitechDBHelper<Chat> chatDb = new GezitechDBHelper<Chat>(
										Chat.class);
								GezitechDBHelper<ChatContent> chatContentDB = new GezitechDBHelper<ChatContent>(
										ChatContent.class);
								Chat chatitem = ChatManager
										.getInstance()
										.getChatItem(uid,hyhid);
								if (chatitem == null) {
									chatitem = new Chat();
									chatitem.uid = uid;
									chatitem.username = FieldVal.value(
											friend.nickname).equals("") ? friend.username
											: friend.nickname;
									chatitem.isfriend = friend.groupId < 3 ? 3
											: (friend.isfriend > 0 ? 1 : 2);
									chatitem.lastcontent = ChatUtils
											.getTypeStr(body, msgtype);
									chatitem.head = friend.head;
									chatitem.ctime = ctime;
									chatitem.istop = friend != null ? friend.istop
											: 0;
									chatitem.myuid = user.id;
									chatitem.hyhid = hyhid;
									chatDb.insert(chatitem);
								} else {
									chatitem.lastcontent = ChatUtils
											.getTypeStr(body, msgtype);
									chatitem.ctime = System.currentTimeMillis();
									chatitem.isfriend = friend.groupId < 3 ? 3
											: (friend.isfriend > 0 ? 1 : 2);
									chatDb.save(chatitem);
								}
								// 存储聊天记录
								ChatContent chantContent = new ChatContent();
								chantContent.chatid = uid;
								chantContent.type = msgtype;
								chantContent.ctime = ctime;
								chantContent.body = body;
								chantContent.uid = user.id;
								chantContent.isfriend = friend.groupId < 3 ? 3
										: (friend.isfriend > 0 ? 1 : 2);
								chantContent.myuid = user.id;
								chantContent.hyhid = hyhid;
								chatContentDB.insert(chantContent);

								// 通知聊天列表的广播
								Intent intent = new Intent();
								intent.setAction(Constant.NEW_MESSAGE_ACTION);
								Bundle bundle = new Bundle();
								bundle.putSerializable(Constant.CHAT_CONTENT,
										chantContent);
								intent.putExtras(bundle);
								GezitechApplication.getContext().sendBroadcast(
										intent);
							}
						});

			}

		} catch (Exception ex) {

		}
	}


}
