package com.gezitech.service.managers;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.gezitech.basic.GezitechApplication;
import com.gezitech.basic.GezitechException;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListArrayListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.entity.PageList;
import com.gezitech.entity.User;
import com.gezitech.http.HttpClient;
import com.gezitech.http.HttpUtil;
import com.gezitech.http.PostParameter;
import com.gezitech.http.Response;
import com.gezitech.service.GezitechService;
import com.gezitech.service.managers.AsynTaskOAuth2Manager.OnAsynCallBackListener;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.gezitech.util.DateUtils;
import com.gezitech.util.NetUtil;
import com.hyh.www.R;
import com.hyh.www.entity.Chat;
import com.hyh.www.entity.ChatContent;
import com.hyh.www.entity.Contacts;
import com.hyh.www.entity.Friend;
import com.hyh.www.entity.NearHintMsg;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 
 * @author xiaobai 2014-11-7
 * @todo( 聊天管理器 )
 */
public class ChatManager {
	private ChatManager _this = this;
	static ChatManager instance = null;
	static GezitechDBHelper<Chat> chatDB;
	static GezitechDBHelper<ChatContent> chatContentDB;
	private static GezitechDBHelper<Friend> friendDB;
	
	private static GezitechDBHelper<Contacts> contactsDB;
	private static GezitechDBHelper<NearHintMsg> nearHintMsgDB;

	public static ChatManager getInstance() {
		if (instance == null) {
			instance = new ChatManager();
			chatDB = new GezitechDBHelper<Chat>(Chat.class);
			chatContentDB = new GezitechDBHelper<ChatContent>(ChatContent.class);
			friendDB = new GezitechDBHelper<Friend>(Friend.class);
			contactsDB = new GezitechDBHelper<Contacts>(Contacts.class);
			nearHintMsgDB = new GezitechDBHelper<NearHintMsg>(NearHintMsg.class);
			
		}
		return instance;
	}

	/*
	 * 获取聊天的列表 isfriend 1 朋友 2非朋友
	 */
	public ArrayList<GezitechEntity_I> getChatList(int isfriend) {
		User currUser = GezitechService.getInstance().getCurrentLoginUser(
				GezitechApplication.getContext());
		String where = "";
		if (isfriend == 1) {
			//where = "isfriend in (1,3) and myuid=" + currUser.id +" and hyhid = 0 ";
			where = " myuid = "+ currUser.id +" and hyhid = 0 ";
		} else if (isfriend == 2) {
			//where = "isfriend=2 and myuid=" + currUser.id;
			where = " myuid = "+currUser.id+" and hyhid > 0 ";
		}
		ArrayList<Chat> chatList = chatDB.query(where, 0,
				"istop desc,ctime desc");
		PageList pl = new PageList();
		pl.addAll(chatList);
		return pl;
	}
	// 获取本地的聊天记录  聊天界面
	public ArrayList<GezitechEntity_I> getChatContentList(long uid, int pageSize,long hyhid ) {
		return getChatContentList(uid, pageSize,hyhid, null, true );
	}
	public ArrayList<GezitechEntity_I> getChatContentList(long uid,
			int pageSize,long hyhid, ChatContent chatContent, boolean isNew ) {
		User currUser = GezitechService.getInstance().getCurrentLoginUser(
				GezitechApplication.getContext());
		//sql + " and iswelcome != 1 " 
//		ArrayList<ChatContent> chatContentList = chatContentDB.query("chatid="
//				+ uid + " and myuid=" + currUser.id + " and type != 9 and "+( hyhid > 0 ? " hyhid = "+ hyhid :  " isfriend= "+isfriend  )  , pageSize, "ctime desc",
//				chatContent, isNew);
		ArrayList<ChatContent> chatContentList = chatContentDB.query("chatid="
				+ uid + " and myuid=" + currUser.id + " and type != 9 and  hyhid = "+ hyhid    , pageSize, "ctime desc",
				chatContent, isNew);

		PageList pl = new PageList();
		if( chatContentList !=null )
			pl.addAll(chatContentList);

		Collections.reverse(pl);

		return pl;
	}

	// 获取聊天
	public Chat getChatItem( long uid, long hyhid ) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		User currUser = GezitechService.getInstance().getCurrentLoginUser(
				GezitechApplication.getContext());
//		ArrayList<Chat> chatList = chatDB.query("uid=" + uid + " and myuid="
//				+ currUser.id + " and hyhid = "+ hyhid+" and isfriend = "+isfriend , 1, "");
		ArrayList<Chat> chatList = chatDB.query("uid=" + uid + " and myuid="
				+ currUser.id + " and hyhid = "+ hyhid , 1, "");
		//Log.v("cshi", "xiaobai:====="+"uid=" + uid + " and myuid="
		//		+ currUser.id + " and hyhid = "+ hyhid+" and isfriend = "+isfriend );
		
		if (chatList != null && chatList.size() > 0) {
		//	Log.v("cshi", "xiaobai:=====存在了" );
			return chatList.get(0);
		}
		return null;
	}
	//整理聊天的数据
	//主要是防止 加为好友 多个chat列表的出现
/*	public void chatDataProcess( long uid ){
		User currUser = GezitechService.getInstance().getCurrentLoginUser(
				GezitechApplication.getContext());
		ArrayList<Chat> chatList = chatDB.query("uid=" + uid + " and myuid="
				+ currUser.id , 0, "ctime desc");
		
		if( chatList!=null && chatList.size()>1 ){
			Chat oneChat = chatList.get(0);
			oneChat.isfriend = 1;
			for( int i = 1 ; i<chatList.size(); i++ ){
				Chat chat = chatList.get( i );
				oneChat.unreadcount += chat.unreadcount;
				try{
					chatDB.delete("id = " + chat.id + " and uid=" + uid + " and myuid=" + currUser.id);
				}catch(Exception e){}
				
			}
			chatDB.save( oneChat );
		}
		
		
	}*/
	// 获取用户的信息 没有则去服务端获取
	public void getFriendData(long uid, final OnAsynGetOneListener listener) {
		User currUser = GezitechService.getInstance().getCurrentLoginUser(
				GezitechApplication.getContext());
		ArrayList<Friend> firendList = friendDB.query("fid=" + uid
				+ " and uid=" + currUser.id, 0, "");
		if (firendList != null && firendList.size() > 0) {
			listener.OnGetOneDone(firendList.get(0));
		} else {
			try {

				String path = HttpUtil.getAbsoluteUrl("api/user/getfrienddata")
						+ "/fid/" + uid + "/oauth_token/"
						+ currUser.access_token;
				// 新建一个URL对象
				URL url = new URL(path);
				// 打开一个HttpURLConnection连接
				HttpURLConnection urlConn = (HttpURLConnection) url
						.openConnection();
				// 设置连接超时时间
				urlConn.setConnectTimeout(10 * 1000);
				// 开始连接
				urlConn.connect();
				// 判断请求是否成功
				if (urlConn.getResponseCode() == 200) {
					// 获取返回的数据
					// byte[] data = readStream();
					InputStream is = urlConn.getInputStream();

					byte buf[] = new byte[2048];

					do {
						int numread = is.read(buf);
						if (numread <= 0) {
							break;
						}
					} while (true);
					urlConn.disconnect();
					is.close();
					Response res = new Response(new String(buf, "UTF-8"));
					JSONObject jsonObj = res.asJSONObject();

					int state = jsonObj.getInt("state");
					if (state != 1) {
						listener.OnAsynRequestFail("-1",
								jsonObj.getString("msg"));
						return;
					}
					if (!jsonObj.has("data") || jsonObj.isNull("data")) {
						listener.OnAsynRequestFail("-1", GezitechApplication
								.getContext().getString(R.string.data_error));
						return;
					}

					JSONObject data = jsonObj.getJSONObject("data");

					Friend user = new Friend(data);

					long cuid = 0;
					User curruser = GezitechService.getInstance()
							.getCurrentUser();
					if (curruser != null) {
						cuid = curruser.id;
					}

					user.uid = cuid;
					user.fid = user.id;
					user.id = 0;
					try {
						ArrayList<Friend> isExists = friendDB.query("fid="
								+ user.fid + " and uid=" + user.uid, 0, "");
						if (isExists != null && isExists.size() > 0)
							friendDB.delete("fid=" + user.fid + " and uid="
									+ user.uid);
					} catch (Exception e) {
						
					}

					friendDB.insert(user);

					listener.OnGetOneDone(user);

				}
				// 关闭连接
				urlConn.disconnect();
			} catch (MalformedURLException e) {
			} catch (IOException e) {
			} catch (GezitechException e) {
			} catch (JSONException e) {
			}
		}
	}
	// 计算未读的消息条数
	// 1 标示 喊一喊已读
	public int[] unreadcount(int flag) {
		int[] unread = new int[3];
		unread[0] = 0;
		unread[1] = 0;
		unread[2] = 0;
		User currUser = GezitechService.getInstance().getCurrentLoginUser(
				GezitechApplication.getContext());
		//私聊
		//ArrayList<Chat> chatList = chatDB.query("myuid=" + currUser.id+" and isfriend in (1,3) ", 0, "");
		ArrayList<Chat> chatList = chatDB.query("myuid=" + currUser.id+" and hyhid = 0 ", 0, "");
		if (chatList != null && chatList.size() > 0) {
			for (int i = 0; i < chatList.size(); i++) {
				Chat chat = chatList.get(i);
				unread[1] += chat.unreadcount;
			}
		}
		//喊一喊聊天数据
		//chatList = chatDB.query("myuid=" + currUser.id+" and isfriend=2 ", 0, "");
		chatList = chatDB.query("myuid=" + currUser.id+" and hyhid > 0 ", 0, "");
		if (chatList != null && chatList.size() > 0) {
			for (int i = 0; i < chatList.size(); i++) {
				Chat chat = chatList.get(i);
				unread[0] += chat.unreadcount;
			}
		}
		//喊一喊数据
		ArrayList<ChatContent> chatContentList = chatContentDB.query("myuid=" + currUser.id+" and type=9 ", 0, "");
		if ( chatContentList != null && chatContentList.size() > 0) {
			for (int i = 0; i < chatContentList.size(); i++) {
				ChatContent chatContent = chatContentList.get(i);
				
				
				if( flag == 1 && chatContent.unreadcount >0 ){
					chatContent.unreadcount = 0;
					chatContentDB.save( chatContent );
				}else{
					unread[2] += chatContent.unreadcount;
				}
			}
		}
		
		return unread;
	}
	
	
	// 删除本地聊天记录
	public void deleteChatContent(long uid,int isfriend) {
		User currUser = GezitechService.getInstance().getCurrentLoginUser(
				GezitechApplication.getContext());
		//chatContentDB.delete("chatid=" + uid + " and myuid=" + currUser.id+" and isfriend = "+isfriend );
		if(  isfriend  == 1  ){
			chatContentDB.delete("chatid=" + uid + " and myuid=" + currUser.id+" and hyhid=0 " );
		}else if (  isfriend == 2 ){
			chatContentDB.delete("chatid=" + uid + " and myuid=" + currUser.id+" and hyh>0 ");
		}
		
	}

	// 删除friend表的所有信息
	public void deleteFriend() {
		friendDB.delete("");
	}

	// 删除friend表的单条信息
	public void deleleFriendOne(long uid) {
		friendDB.delete("fid=" + uid);
	}

	// 删除chat表的信息
	public void deleteChat(long uid,int isfriend) {
		User currUser = GezitechService.getInstance().getCurrentLoginUser(
				GezitechApplication.getContext());
		//chatDB.delete("uid=" + uid + " and myuid= " + currUser.id+" and isfriend = "+isfriend );
		if(  isfriend == 1 ){
			chatDB.delete("uid=" + uid + " and myuid= " + currUser.id+" and hyhid =0 ");
		}else if( isfriend == 2 ){
			chatDB.delete("uid=" + uid + " and myuid= " + currUser.id+" and hyhid > 0 ");
		}
	}

	// 删除contacts表的所有信息
	public void deleteContacts() {
		contactsDB.delete("");
	}

	// 删除contacts表的所有信息
	public void deleteChatContent() {
		chatContentDB.delete("");
	}
	
	//设置好友资料 是好有和非好友
	public void setFriendInfo(String field, String value, long fid ){
		User currUser = GezitechService.getInstance().getCurrentLoginUser(
				GezitechApplication.getContext());
		ArrayList<Friend> firendList = friendDB.query("fid=" + fid
				+ " and uid=" + currUser.id, 0, "");
		if (firendList != null && firendList.size() > 0) {
			Friend friend = firendList.get(0);
			if( field.equals( "isfriend" ) ){
				friend.isfriend = Integer.parseInt( value );
			}
			friendDB.save( friend );
		}
	}
	//设置聊天列表 是否是好友和非好友  uid 朋友id
	/*public void setChatInfo(String field, String value, long uid ){
		User currUser = GezitechService.getInstance().getCurrentLoginUser(
				GezitechApplication.getContext());
		ArrayList<Chat> chatList = chatDB.query("uid=" + uid + " and myuid="
				+ currUser.id, 0, "");
		if (chatList != null && chatList.size() > 0) {
			Chat chat = chatList.get(0);
			if( field.equals( "isfriend" ) ){
				chat.isfriend = Integer.parseInt( value );
			}
			chatDB.save( chat );
		}
	}*/
	
	
	// 搜索聊天记录
	/*
	 * 获取聊天记录 friend/getchatrecord 参数 oauth_token 不能为空 page 当前页数 content 搜索内容
	 * pageSize 每页显示数量 fid 好友id
	 */

	public void getchatrecord(int page, int pageSize, long fid, String content,
			final OnAsynGetListListener listener) {
		RequestParams params = new RequestParams();
		params.put("page", page);
		params.put("fid", fid);
		params.put("content", content);
		params.put("pageSize", pageSize);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/friend/getchatrecord", true, params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							if (arg0 != 200) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));
							}
							try {
								Response response = new Response(new String(
										arg2));
								JSONObject root = response.asJSONObject();
								if (root.getInt("state") != 1) {// 0
									if (listener != null)
										listener.OnAsynRequestFail("-1",
												root.getString("msg"));
									return;
								}
								PageList pl = new PageList();
								ChatContent chatContent = null;

								JSONObject data = root.getJSONObject("data");
								User currUser = GezitechService.getInstance().getCurrentLoginUser(
										GezitechApplication.getContext());
								if (data.has("datas") && !data.isNull("datas")) {
									JSONArray datas = data
											.getJSONArray("datas");
									for (int i = 0; i < datas.length(); i++) {
										JSONObject jo = datas.getJSONObject(i);
										chatContent = new ChatContent(jo);
										// 做和本地数据同字段处理 公用适配器
										if (chatContent.sender == currUser.id) {
											chatContent.uid = currUser.id;
										} else {
											chatContent.uid = chatContent.receiver;
										}
										// 时间处理 把时间字符串转为 时间戳
										chatContent.ctime = DateUtils
												.getTimeStamp(
														chatContent.createdate,
														"yyyy-MM-dd HH:mm:ss");

										pl.add(chatContent);
									}

								}
								listener.OnGetListDone(pl);

							} catch (Exception ex) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.data_error));
						}

						@Override
						public void onFinish() { // 完成后调用，失败，成功，都要掉

						}
					});
		} else {
			if (listener != null) {
				listener.OnAsynRequestFail("-1", GezitechApplication
						.getContext().getString(R.string.network_error));
			}
		}
	}
	//更改聊天所属的
//	public void updateChatGroup( long uid ){
//		User currUser = GezitechService.getInstance().getCurrentLoginUser(
//				GezitechApplication.getContext());
//		ArrayList<Chat> chatList = chatDB.query("myuid=" + currUser.id+" and isfriend=2 and uid = "+uid +" and hyhid > 0 " , 1, "ctime desc");
//		long hyhid = 0;
//		if ( chatList != null && chatList.size() > 0) {
//			for (int i = 0; i < chatList.size(); i++) {
//				Chat chat = chatList.get(i);
//				ArrayList<Chat> chatListExiste = chatDB.query("myuid=" + currUser.id+" and isfriend=1 and uid = "+uid  , 0, "ctime desc");
//				if( chatListExiste !=null  && chatListExiste.size() > 0 ){
//					continue;
//				}
//				hyhid = chat.hyhid;
//				chat.id = 0;
//				chat.isfriend = 1;
//				chat.hyhid = 0;
//				chatDB.insert( chat );
//			}
//		}
//		//把加为好友的聊天信息 转为是朋友的
//		if( chatList!=null  && chatList.size()>0 ){
//			ArrayList<ChatContent> chatContentList = chatContentDB.query("myuid=" + currUser.id+"  and type != 9 and hyhid = "+hyhid , 0, "");
//			if ( chatContentList != null && chatContentList.size() > 0) {
//				for (int i = 0; i < chatContentList.size(); i++) {
//					ChatContent chatContent = chatContentList.get(i);
//					chatContent.isfriend = 1;
//					chatContentDB.save( chatContent );
//				}
//			}
//		}
//	}
	//获取是否有喜欢喝评论的新消息
	public ArrayList<NearHintMsg> getNearHintMsg(){
		
		ArrayList<NearHintMsg> nearHintMsgList = nearHintMsgDB.query("isRead=0", 0 , "ctime desc");
		
		return nearHintMsgList;
		
	}
	public void deleteNearHintMsg(long id){
		
		nearHintMsgDB.delete( id > 0 ? "id="+id : "" );
	
	}
	
	

}
