package com.hyh.www.session;

import java.util.ArrayList;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynRequestListener;
import com.gezitech.service.GezitechService;
import com.gezitech.service.managers.ChatManager;
import com.gezitech.service.managers.FriendManager;
import com.gezitech.service.managers.UserManager;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.gezitech.service.xmpp.Constant;
import com.gezitech.service.xmpp.MessageSend;
import com.gezitech.service.xmpp.XmppConnectionManager;
import com.hyh.www.R;
import com.hyh.www.ZhuyeActivity;
import com.hyh.www.entity.Chat;
import com.hyh.www.entity.ChatContent;
import com.hyh.www.entity.Friend;
import com.hyh.www.user.PersonDetailedInformationActivity;
import com.hyh.www.widget.YMDialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author xiaobai (聊天信息)
 */
public class ChatMessagesActivity extends GezitechActivity implements
		OnClickListener {

	private ChatMessagesActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_title;
	private GezitechDBHelper<Friend> DB;
	private long uid;
	private Friend friend;
	private CheckBox Check_top_chat;
	private CheckBox Check_new_alerts;
	private TextView tv_chatmessages_close;
	private GezitechDBHelper<Chat> chatDb;
	private int isfriend;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_messages);
		uid = _this.getIntent().getLongExtra("uid",0);
		isfriend = _this.getIntent().getIntExtra("isfriend", 3);
		DB = new GezitechDBHelper<Friend>(Friend.class);
		chatDb = new GezitechDBHelper<Chat>( Chat.class );
		ArrayList<Friend> list = DB.query("fid="+uid, 1, "");
		if( list!= null && list.size()> 0  ){
			friend = list.get( 0 );
			_init();
		}else{ //如果没有资料 去获取
			
			GezitechAlertDialog.loadDialog( this );
			UserManager.getInstance().getfrienddata(uid,  new OnAsynGetOneListener() {
				
				@Override
				public void OnAsynRequestFail(String errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					GezitechAlertDialog.closeDialog();
					Toast( errorMsg );
				}
				
				@Override
				public void OnGetOneDone(GezitechEntity_I entity) {
					// TODO Auto-generated method stub
					GezitechAlertDialog.closeDialog();
					friend = (Friend)entity;
					_init();
				}
			});
		}
		//判断是否已经链接
		if(  GezitechApplication.connection == null || ( !GezitechApplication.connection.isConnected() ) || (  XmppConnectionManager.getInstance().getConnection() !=null && !XmppConnectionManager.getInstance().getConnection().isConnected() ) ){
			XmppConnectionManager.getInstance().login();
		}
	}

	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(_this.getResources().getString(R.string.liaotianxinxi));
		
		
		//是否置顶聊天
		Check_top_chat = (CheckBox) findViewById( R.id.Check_top_chat );
		Check_top_chat.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GezitechAlertDialog.loadDialog( _this );
				FriendManager.getInstance().istop( uid, friend.istop > 0 ? 0 : 1, new OnAsynRequestListener() {
					
					@Override
					public void OnAsynRequestFail(String errorCode, String errorMsg) {
						// TODO Auto-generated method stub
						GezitechAlertDialog.closeDialog();
						Toast( errorMsg );
						_initData();
					}
					
					@Override
					public void OnAsynRequestCallBack(Object o) {
						GezitechAlertDialog.closeDialog();
						 friend.istop  = friend.istop  > 0 ? 0 : 1;
						 DB.save( friend );
						 //更新聊天列表的信息
						Chat chat = ChatManager.getInstance().getChatItem(uid, 0);
						if( chat != null ){
							chat.istop = friend.istop  > 0 ? 0 : 1;
						    chatDb.save( chat );
						}
					}
				});
			}
		});

		//新消息通知
		Check_new_alerts = (CheckBox) findViewById( R.id.Check_new_alerts );
		Check_new_alerts.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GezitechAlertDialog.loadDialog( _this );
				FriendManager.getInstance().isremind( uid, friend.isremind > 0 ? 0 : 1, new OnAsynRequestListener() {
					
					@Override
					public void OnAsynRequestFail(String errorCode, String errorMsg) {
						// TODO Auto-generated method stub
						GezitechAlertDialog.closeDialog();
						Toast( errorMsg );
						_initData();
					}
					
					@Override
					public void OnAsynRequestCallBack(Object o) {
						GezitechAlertDialog.closeDialog();
						 friend.isremind  = friend.isremind  > 0 ? 0 : 1;
						 DB.save( friend );
					}
				});
			}
		});
				
		//查找聊天记录
		RelativeLayout chatmessages_chazhao = (RelativeLayout) findViewById( R.id.chatmessages_chazhao );
		chatmessages_chazhao.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent =  new Intent( _this,SearchChatContentActivity.class ) ;
				Bundle bundle = new Bundle();
				bundle.putSerializable("friend", friend);
				intent.putExtras( bundle );
				_this.startActivity( intent );
			}
		});
		//清空聊天记录
		RelativeLayout chatmessages_qingkong = (RelativeLayout) findViewById( R.id.chatmessages_qingkong );
		chatmessages_qingkong.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final YMDialog ymdialog = new YMDialog( _this );
				ymdialog.setHintMsg("你确定要清空聊天记录吗?").setCloseButton( new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						ymdialog.dismiss();
					}
				}).setConfigButton( new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ymdialog.dismiss();
						//清空
						GezitechAlertDialog.loadDialog( _this );
						/*Thread thread = new Thread( new Runnable() {
							
							@Override
							public void run() {
								try{
								GezitechDBHelper<ChatContent> chatContent = new GezitechDBHelper<ChatContent>( ChatContent.class );
								chatContent.delete("chatid="+uid);
								}catch( Exception ex ){
									
								}
								handle.obtainMessage(100).sendToTarget();
							}
						});
						thread.start();*/
						
						FriendManager.getInstance().emptyrecord( uid,  new OnAsynRequestListener() {
							
							@Override
							public void OnAsynRequestFail(String errorCode, String errorMsg) {
								// TODO Auto-generated method stub
								GezitechAlertDialog.closeDialog();
								Toast( errorMsg );
							}
							
							@Override
							public void OnAsynRequestCallBack(Object o) {
								GezitechAlertDialog.closeDialog();
								//删除本地的聊天记录
								//删除本地的聊天列表
								try{
									ChatManager.getInstance().deleteChatContent( uid, 1 );
									ChatManager.getInstance().deleteChat( uid , 1  );
								}catch( Exception ex ){}
								Toast("清空聊天记录成功");
								//删除聊天记录的广播
								//用于聊天列表
								//情况聊天界面的信息
								Intent intent = new Intent();
								intent.setAction( Constant.UPDATE_CONTACTS_ACTION );
								intent.putExtra("fid", uid ); //如果列表中有用户的列表则更新
								_this.sendBroadcast( intent );
								
							}
						});
					}
				});
			}
		});
		
		//关闭聊天
		RelativeLayout chatmessages_close = ( RelativeLayout )findViewById( R.id.chatmessages_close );
	    tv_chatmessages_close = (TextView) findViewById( R.id.tv_chatmessages_close );
		
		chatmessages_close.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GezitechAlertDialog.loadDialog( _this );
				FriendManager.getInstance().isclose( uid, friend.isclose > 0 ? 0 : 1, new OnAsynRequestListener() {
					
					@Override
					public void OnAsynRequestFail(String errorCode, String errorMsg) {
						// TODO Auto-generated method stub
						GezitechAlertDialog.closeDialog();
						Toast( errorMsg );
					}
					
					@Override
					public void OnAsynRequestCallBack(Object o) {
						GezitechAlertDialog.closeDialog();
						 friend.isclose  = friend.isclose  > 0 ? 0 : 1;
						 DB.save( friend );
						 _initData();
					}
				});
			}
		});
		
		//投诉
		RelativeLayout chatmessages_tousu = ( RelativeLayout ) findViewById( R.id.chatmessages_tousu );
		chatmessages_tousu.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(_this,UsercomplainActivity.class );
				intent.putExtra("fid", uid);
				_this.startActivity(  intent );	
			}
		});
		//如果不是好友则添加好友
		RelativeLayout personDetailed_withoutfriend = ( RelativeLayout ) findViewById( R.id.personDetailed_withoutfriend );
		Button personDetailed_addfriend =  ( Button ) findViewById( R.id.personDetailed_addfriend );
		//if( isfriend == 2 && user.isbusiness <1 ){
		if( isfriend == 2 ){
			personDetailed_withoutfriend.setVisibility( View.VISIBLE );
			personDetailed_addfriend.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					GezitechAlertDialog.loadDialog( _this );
					FriendManager.getInstance().addFriend( friend.fid, new OnAsynRequestListener() {
						
						@Override
						public void OnAsynRequestFail(String errorCode, String errorMsg) {
							// TODO Auto-generated method stub
							GezitechAlertDialog.closeDialog();
							_this.Toast( errorMsg );
						}
						
						@Override
						public void OnAsynRequestCallBack(Object o) {
							// TODO Auto-generated method stub
							GezitechAlertDialog.closeDialog();
							_this.Toast("添加好友请求已发送,请等待对方同意");
							GezitechService.sendMessage( friend.fid , 15 );
						}
					});
				}
			});
		}
		
		_initData();
	}
	/*Handler handle = new Handler( new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Object obj = msg.obj;
			int what = msg.what;
			switch (what) {
			// 清空聊天记录
			case 100:
				GezitechAlertDialog.closeDialog();
				Toast("清除完成");
			break;	
			}
			
			
			
			return false;
		}
	});*/
	private void _initData(){
		if( friend.istop>0 ){
			Check_top_chat.setChecked( true );
		}else{
			Check_top_chat.setChecked( false );
		}
		if( friend.isremind >0 ){
			Check_new_alerts.setChecked( true );
			
		}else{
			Check_new_alerts.setChecked( false );
			
		}
		if( friend.isclose>0 ){
			tv_chatmessages_close.setText("开启聊天") ; 
		}else{
			tv_chatmessages_close.setText( "关闭聊天" );
		}
	}
	

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {

		case R.id.bt_home_msg:
			finish();
			break;

		default:
			break;
		}
	}

}
