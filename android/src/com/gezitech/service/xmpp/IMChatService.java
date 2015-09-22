package com.gezitech.service.xmpp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.json.JSONObject;

import com.gezitech.basic.GezitechApplication;
import com.gezitech.basic.GezitechEntity.FieldInfo;
import com.gezitech.basic.GezitechException;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.entity.User;
import com.gezitech.http.Response;
import com.gezitech.service.BackgroundService;
import com.gezitech.service.GezitechService;
import com.gezitech.service.BackgroundService.LocalBinder;
import com.gezitech.service.lbs.AppUtils;
import com.gezitech.service.lbs.NotificationUtil;
import com.gezitech.service.managers.ChatManager;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.gezitech.util.CalendarUtil;
import com.gezitech.util.IOUtil;
import com.gezitech.util.VibratorUtil;
import com.hyh.www.R;
import com.hyh.www.WelcomeActivity;
import com.hyh.www.chat.ChatUtils;
import com.hyh.www.entity.Chat;
import com.hyh.www.entity.ChatContent;
import com.hyh.www.entity.FieldVal;
import com.hyh.www.entity.Friend;
import com.hyh.www.entity.NearHintMsg;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

/**
 * 聊天服务.
 */
public class IMChatService extends Service {
	private Context context;
	private static User user;
	private static GezitechDBHelper<ChatContent> chatContentDB;
	private static GezitechDBHelper<Chat> chatDB;
	private static int msgtype = 0;
	private static long hyhid = 0;
	private Binder binder = new LocalBinder();
	private GezitechDBHelper<NearHintMsg> nearHintMsgDB = null;
	public static XMPPConnection conn = null;

	@Override
	public void onCreate() {
		context = this;
		super.onCreate();
		initChatManager();
		chatDB = new GezitechDBHelper<Chat>(Chat.class);
		chatContentDB = new GezitechDBHelper<ChatContent>(ChatContent.class);
		nearHintMsgDB  = new GezitechDBHelper<NearHintMsg>(NearHintMsg.class);
		user = GezitechService.getInstance().getCurrentLoginUser(this);/*
		Intent intent = new Intent(getApplicationContext(), IMChatService.class);        
        AlarmManager mAlarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent mPendingIntent = PendingIntent.getService(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        long now = System.currentTimeMillis();
        mAlarmManager.setInexactRepeating(AlarmManager.RTC, now, 60000, mPendingIntent);*/
	}


	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	/**
	 * 绑定器
	 * 
	 */
	public class LocalBinder extends Binder {
		public IMChatService getService() {
			return IMChatService.this;
		}
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    return  START_STICKY;
	}
	@Override
	public void onDestroy() {
		stopForeground(true);
		super.onDestroy();
	}

	private void initChatManager() {
		conn = XmppConnectionManager.getInstance().getConnection();
		if (conn != null) {
			conn.addPacketListener(pListener, new MessageTypeFilter(
					Message.Type.chat));
		} else {
			XmppConnectionManager.getInstance().login();
		}

	}

	PacketListener pListener = new PacketListener() {

		@Override
		public void processPacket(Packet arg0) {
			Message message = (Message) arg0;
			Log.v("niha", "添加好友的请求0"+message.toString()+"=="+message.toXML());
			if (message != null && message.getBody() != null
					&& !message.getBody().equals("null")) {
				Object msgtypeObj = null ;
				Object hyhidObj = null;
				try {
					msgtypeObj = message.getProperty("msgtype");
					hyhidObj = message.getProperty("hyhid");
					msgtype = Integer.parseInt(msgtypeObj.toString());
					hyhid = Long.parseLong(hyhidObj.toString());
				} catch (NumberFormatException e) {
					msgtype = 0;
					hyhid = 0;
				}catch(Exception e){
					msgtype = 0;
					hyhid = 0;
				}
				String from = message.getFrom().split("/")[0];
				final long uid = Long.valueOf(from.split("@")[0]);
				if( uid <= 0 ) return;
				
				final String body = message.getBody();

				// 15//好友请求，
				// 16//同意加好友，
				// 17//拒绝加好友
				// 18,//删除好友
				switch (msgtype) {
				case 15://好友请求，
					Intent intent1 = new Intent();
					intent1.setAction(Constant.FRIEND_REQUEST_COUNT);
					sendBroadcast(intent1);		
					Log.v("niha", "添加好友的请求2");
					break;
				case 16://同意加好友，
					//把喊一喊中聊天信息改为朋友的聊天到私聊中
					//更新数据
					//ChatManager.getInstance().updateChatGroup( uid );
					ChatManager.getInstance().setFriendInfo("isfriend","1", uid );
					Intent intent2 = new Intent();
					intent2.setAction(Constant.UPDATE_CONTACTS_ACTION);
					sendBroadcast(intent2);	
					break;
				case 17://拒绝加好友
					break;
				case 18://删除好友
					//删除用户信息
					ChatManager.getInstance().deleleFriendOne( uid );
					//删除聊天记录
					ChatManager.getInstance().deleteChatContent( uid,1 );
					//删除聊天列表
					ChatManager.getInstance().deleteChat( uid , 1 );
					Intent intent3 = new Intent();
					intent3.setAction(Constant.UPDATE_CONTACTS_ACTION);
					sendBroadcast(intent3);	
					break;
				case 19: //附近人的喜欢和评论
				case 20:
					Response response = new Response( body );
    				try {
						JSONObject root = response.asJSONObject();
						NearHintMsg nhm = new NearHintMsg( root );
						nhm.isRead = 0;
						nearHintMsgDB.insert( nhm );
						//发送广播
						Intent intent4 = new Intent();
						intent4.setAction(Constant.LIKE_COMMENT_ACTION);
						sendBroadcast(intent4);	
					} catch (GezitechException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				default:
					
					Log.v("收到新消息了", "收到新消息了======22uid=" + uid + ";hyhid="
							+ hyhid + ";msgtype=" + msgtype);
					// 获取用户信息 没有则去服务端获取
					ChatManager.getInstance().getFriendData(uid,
							new OnAsynGetOneListener() {
								@Override
								public void OnAsynRequestFail(String errorCode,
										String errorMsg) {
								}

								@Override
								public void OnGetOneDone(GezitechEntity_I entity) {
									//Log.v("收到新消息了", "收到新消息了======22uid=" + uid
									//		+ ";hyhid=" + hyhid + ";msgtype="
									//		+ msgtype);
									Friend friend = (Friend) entity;
									// 是否关闭聊天
									if (friend.isclose > 0)
										return;
									// 通知消息是否有声音和震动
									boolean isNotificationVoice = false;
									boolean isNotificationVibration = false;
									// 判断时间段
									boolean isdisturb = true;
									int disturb = GezitechApplication.systemSp
											.getInt("disturb", 0);
									if (disturb > 0) {// 开启免打扰
										String start_time = GezitechApplication.systemSp
												.getString("start_time",
														"22:00");
										String end_time = GezitechApplication.systemSp
												.getString("end_time", "08:00");
										int currH = CalendarUtil
												.getCalendarString(Calendar.HOUR_OF_DAY);
										int currM = CalendarUtil
												.getCalendarString(Calendar.MINUTE);

										String[] start_time_arr = start_time
												.split(":");
										String[] end_time_arr = end_time
												.split(":");

										if (currH > Integer
												.parseInt(start_time_arr[0])
												|| currH < Integer
														.parseInt(end_time_arr[0])) {
											isdisturb = false;
										}
									}
									// 新消息通知
									int receive_notifications = GezitechApplication.systemSp
											.getInt("receive_notifications", 1);
									if (receive_notifications > 0
											&& friend.isremind > 0 && isdisturb) {

										int voice = GezitechApplication.systemSp
												.getInt("voice", 1);
										if (voice > 0) {
											// 播放声音
											try {
												ring();
											} catch (IOException e) {
											} catch (Exception e) {
											}
											isNotificationVoice = true;
										}
										int vibration = GezitechApplication.systemSp
												.getInt("vibration", 1);
										if (vibration > 0) {
											// 震动
											new VibratorUtil(context);
											isNotificationVibration = true;
										}

									}
									long ctime = System.currentTimeMillis();
									if (msgtype != 9) { // 如果 消息类型不是9的话 则创建/更新回话
										
										// 创建聊天
										Chat chat = ChatManager.getInstance()
												.getChatItem( uid, hyhid );
										//Log.v("cshi", "xiaobai:"+uid+"=="+hyhid+"=="+( friend.groupId < 3 ? 3
										//		: (friend.isfriend > 0 ? 1
										//				: 2) ) + "====是否存在："+ ( chat == null ? "不存在":"存在" ) );
										if (chat == null) {
											chat = new Chat();
											chat.uid = uid;
											chat.username = FieldVal.value(
													friend.nickname).equals("") ? friend.username
													: friend.nickname;
											chat.isfriend = friend.groupId < 3 ? 3
													: (friend.isfriend > 0 ? 1
															: 2);
											chat.lastcontent = ChatUtils
													.getTypeStr(body, msgtype);
											chat.head = friend.head;
											chat.ctime = ctime;
											if ( GezitechApplication.currUid == uid && ( hyhid <= 0  || hyhid == GezitechApplication.hyhId ) ) {
												chat.unreadcount = 0;
											} else {
												chat.unreadcount = 1;
											}
											chat.istop = friend.istop;
											chat.myuid = user != null ? user.id
													: 0;
											chat.hyhid = hyhid;
											chatDB.insert(chat);
										} else {
											chat.lastcontent = ChatUtils
													.getTypeStr(body, msgtype);
											chat.ctime = ctime;
											if (  GezitechApplication.currUid == uid && ( hyhid <= 0  || hyhid == GezitechApplication.hyhId ) ) {
												chat.unreadcount = 0;
											} else {
												chat.unreadcount = chat.unreadcount + 1;
											}
											chat.isfriend = friend.groupId < 3 ? 3
													: (friend.isfriend > 0 ? 1
															: 2);
											chatDB.save(chat);
										}
									}
									// 存储聊天记录
									ChatContent chantContent = new ChatContent();
									chantContent.chatid = uid;
									chantContent.type = msgtype;
									chantContent.ctime = ctime;
									chantContent.body = body;
									if(  msgtype == 9  ){
										chantContent.unreadcount = 1;
									}
									chantContent.isfriend = msgtype == 9 ? 4
											: (friend.groupId < 3 ? 3
													: (friend.isfriend > 0 ? 1
															: 2));
									chantContent.uid = uid;
									chantContent.hyhid = hyhid;
									chantContent.myuid = user != null ? user.id
											: 0;
									chatContentDB.insert(chantContent);
									/*Log.v("收到新消息了", "收到新消息了======"
											+ chantContent.isfriend + ";uid="
											+ uid + ";chantContent.myuid="
											+ chantContent.myuid);*/
									// 发送通知
									if (!AppUtils.isAppOnForeground()) { // 后台运行

										NotificationUtil
												.sendNotification(
														"喊一喊有一条新消息",
														FieldVal.value(
																friend.nickname)
																.equals("") ? friend.username
																: friend.nickname,
														ChatUtils.getTypeStr(
																body, msgtype),
														isNotificationVoice,
														isNotificationVibration);

									}

									Log.v("chantContent", "chantContent=="
											+ chantContent.isfriend);
									// 新消息内容 直接更新 聊天的界面的广播
									Intent intent = new Intent();
									intent.setAction(Constant.NEW_MESSAGE_ACTION);
									Bundle bundle = new Bundle();
									bundle.putSerializable(
											Constant.CHAT_CONTENT, chantContent);
									intent.putExtras(bundle);
									sendBroadcast(intent);
								}
							});

				}
			}

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
		// final MediaPlayer player = new MediaPlayer();
		// player.setDataSource(this, alert);
		/*
		 * AssetFileDescriptor fileDescriptor =
		 * getAssets().openFd("soundwav.wav");
		 * player.setDataSource(fileDescriptor
		 * .getFileDescriptor(),fileDescriptor.getStartOffset(),
		 * fileDescriptor.getLength()); final AudioManager audioManager =
		 * (AudioManager) getSystemService(Context.AUDIO_SERVICE); if
		 * (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0)
		 * { player.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
		 * 
		 * //player.setLooping(true);
		 * 
		 * player.prepare();
		 * 
		 * player.start(); player.setOnCompletionListener( new
		 * OnCompletionListener() {
		 * 
		 * @Override public void onCompletion(MediaPlayer mp) { player.stop(); }
		 * }); } Log.v("播放", "哈哈"); return player;
		 */
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
