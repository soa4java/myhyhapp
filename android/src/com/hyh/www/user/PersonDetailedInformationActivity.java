package com.hyh.www.user;

import org.jivesoftware.smack.Chat;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynRequestListener;
import com.gezitech.entity.User;
import com.gezitech.service.GezitechService;
import com.gezitech.service.managers.ChatManager;
import com.gezitech.service.managers.FriendManager;
import com.gezitech.service.managers.UserManager;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.gezitech.service.xmpp.Constant;
import com.gezitech.service.xmpp.MessageSend;
import com.gezitech.service.xmpp.XmppConnectionManager;
import com.gezitech.util.StringUtil;
import com.gezitech.widget.RemoteImageView;
import com.hyh.www.R;
import com.hyh.www.chat.ChatActivity;
import com.hyh.www.entity.FieldVal;
import com.hyh.www.entity.Friend;
import com.hyh.www.session.NotesActivity;
import com.hyh.www.widget.ImageShow;
import com.hyh.www.widget.YMDialog;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author xiaobai (个人加好友的详细资料)
 */
public class PersonDetailedInformationActivity extends GezitechActivity implements OnClickListener{
	
	private PersonDetailedInformationActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_title;
	private Friend friendinfo = null;
	private RelativeLayout personDetailed_withoutfriend;
	private Button personDetailed_send;
	private LinearLayout personDetailed_havefriends;
	private RemoteImageView personDetailed_photo;
	private TextView personDetailed_nickname;
	private TextView personDetailed_accountnumber;
	private ImageView personDetailed_xxzl_star;
	private TextView text_personDetailed_diqu;
	private TextView text_personDetailed_laiyuan;
	private TextView tv_personDetailed_setting;
	private CheckBox Check_personDetailed_common_on;
	private TextView jiaruheimingdan;
	private TextView tv_personDetailed_blacklist;
	private TextView tv_personDetailed_del;
	private String action = "0";
	private CheckBox Check_personDetailedarrow_blacklist;
	private LinearLayout ll_company_box;
	private TextView text_shangjiaDetailed_enterprisename;
	private TextView text_shangjiaDetailed_enterprisetype;
	private Button personDetailed_addfriend;
	private ImageView iv_sex;
	private TextView tv_shangjiaDetailed_enterprisename;
	private TextView tv_shangjiaDetailed_enterprisetype;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_detailed_information);
		Intent intent = _this.getIntent();
		
		Bundle bundle = intent.getExtras();
		
		friendinfo = (Friend) bundle.getSerializable("friendinfo");
		//判断是否已经链接
		/*if(  GezitechApplication.connection == null || ( !GezitechApplication.connection.isConnected() ) || (  XmppConnectionManager.getInstance().getConnection() !=null && !XmppConnectionManager.getInstance().getConnection().isConnected() ) ){
			XmppConnectionManager.getInstance().login();
		}
		*/
		
		_init();
	}

	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		
		if( friendinfo.isbusiness > 0 ){		
			bt_my_post.setVisibility(View.VISIBLE );
			bt_my_post.setText( "商家资料" );
		}else{
			bt_my_post.setVisibility(View.GONE );
		}
		
		bt_my_post.setOnClickListener( new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				GezitechAlertDialog.loadDialog( _this );
				UserManager.getInstance().getcompanyinfo(friendinfo.fid, false, new OnAsynGetOneListener(){
					@Override
					public void OnAsynRequestFail(String errorCode, String errorMsg) {
						// TODO Auto-generated method stub
						GezitechAlertDialog.closeDialog();
						Toast( errorMsg );
					}
					@Override
					public void OnGetOneDone(GezitechEntity_I entity) {
						GezitechAlertDialog.closeDialog();
						User shangjia =  (User) entity ;
						Intent intent = null;
						if( shangjia.auth_type == 0  ){
							intent = new Intent(_this, LookShangjiaActivity.class);
						}else{
							intent = new Intent(_this, LookServiceActivity.class);
						}
						Bundle bundle = new Bundle();
						bundle.putSerializable( "shangjia", shangjia  );
						intent.putExtras( bundle );
						_this.startActivity( intent );
					}
				});
				
			}
		});
		

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

	    tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(_this.getResources().getString(
				R.string.xiangxiziliao));
		
		//未加好友
		personDetailed_withoutfriend = (RelativeLayout) findViewById(R.id.personDetailed_withoutfriend);
		personDetailed_addfriend = ( Button ) findViewById( R.id.personDetailed_addfriend );
		//if( friendinfo.isfriend == 0 && friendinfo.fid != user.id && user.isbusiness<1  && !(friendinfo.groupId<3) ){
		if( friendinfo.isfriend == 0 && friendinfo.fid != user.id && !(friendinfo.groupId<3) ){
				
			personDetailed_withoutfriend.setVisibility( View.VISIBLE );
			personDetailed_addfriend.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					GezitechAlertDialog.loadDialog( _this );
					FriendManager.getInstance().addFriend( friendinfo.fid, new OnAsynRequestListener() {
						
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
							
							GezitechService.sendMessage( friendinfo.fid, 15);
							
							
							_this.Toast("添加好友请求已发送,请等待对方同意");
						}
					});
				}
			});
		}
		
		
		//发消息
		personDetailed_send = (Button) findViewById( R.id.personDetailed_send );
		//如果是商家和用户发消息 除开是 好友否则不能发消息 不能加好友
		//自己查看自己的资料
		if( friendinfo.isfriend != 1  ||  friendinfo.fid == user.id ){
			personDetailed_send.setVisibility( View.GONE );
		}
		
		//加了好友的信息框
		personDetailed_havefriends = (LinearLayout) findViewById( R.id.personDetailed_havefriends );
		if( friendinfo.isfriend == 1  && friendinfo.fid != user.id ){
			personDetailed_havefriends.setVisibility( View.VISIBLE );
		}
		personDetailed_send.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(_this, ChatActivity.class );
				intent.putExtra("uid", friendinfo.fid);
				
				intent.putExtra("username", friendinfo.nickname == null || friendinfo.nickname.equals("null") || friendinfo.nickname.equals("") ? friendinfo.username : friendinfo.nickname );
				
				intent.putExtra("head", friendinfo.head);
				
				intent.putExtra("isfriend", friendinfo.groupId <3 ? 3 : ( friendinfo.isfriend >0 ? 1 : 2 ) );
				intent.putExtra("isbusiness", friendinfo.isbusiness );
				_this.startActivity( intent );
				
			}
		});
		
		//头像
		personDetailed_photo = (RemoteImageView) findViewById( R.id.personDetailed_photo );
		personDetailed_photo.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//画廊展示图片
				final String[] images = new String[1];
				String[] pic = friendinfo.head.split("src=");
				
				images[0] = StringUtil.stringDecode( pic.length > 1 ?  pic[1] : pic[0] );
				ImageShow.jumpDisplayPic(images, 0, _this);
			}
		});
		personDetailed_photo.setImageUrl( friendinfo.head );
		//昵称
		personDetailed_nickname = (TextView) findViewById( R.id.personDetailed_nickname );
		personDetailed_nickname.setText( friendinfo.nickname == null || friendinfo.nickname.equals("null") || friendinfo.nickname.equals("") ? "" : friendinfo.nickname );
		//用户性别
		iv_sex = (ImageView) findViewById( R.id.iv_sex );
		if( friendinfo.sex == 1 ){
			iv_sex.setImageResource( R.drawable.icon_male );
			iv_sex.setVisibility( View.VISIBLE );
		}else if( friendinfo.sex == 2 ){
			iv_sex.setVisibility( View.VISIBLE );
		}
		
		//账户 
		personDetailed_accountnumber = (TextView) findViewById( R.id.personDetailed_accountnumber );
		personDetailed_accountnumber.setText( friendinfo.username );
		//星标 
		personDetailed_xxzl_star  = (ImageView) findViewById( R.id.personDetailed_xxzl_star );
		if( friendinfo.isstar == 1 ){
			personDetailed_xxzl_star.setVisibility( View.VISIBLE );
		}else{
			personDetailed_xxzl_star.setVisibility( View.GONE );
		}
		
		//地区
		text_personDetailed_diqu = (TextView) findViewById( R.id.text_personDetailed_diqu );
		text_personDetailed_diqu.setText( FieldVal.value( friendinfo.city ).equals("") ? "未填写" : friendinfo.city  );
		//来源
		text_personDetailed_laiyuan = (TextView) findViewById( R.id.text_personDetailed_laiyuan ); 
		text_personDetailed_laiyuan.setText( FieldVal.value( friendinfo.Source ).equals("") ? "搜索" : friendinfo.Source  );
		
		//设置备注
		tv_personDetailed_setting = (TextView) findViewById( R.id.tv_personDetailed_setting ); 
		tv_personDetailed_setting.setText( FieldVal.value( friendinfo.notes ).equals("") ? "设置备注" : friendinfo.notes );
		tv_personDetailed_setting.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent( _this, NotesActivity.class );
				intent.putExtra("notes", friendinfo.notes.equals("") || friendinfo.notes.equals("null") || friendinfo.notes == null ? "" : friendinfo.notes);
				intent.putExtra("fid", friendinfo.fid );
				_this.startActivityForResult(intent , 1001);
				
			}
		});
		
		//星标
		Check_personDetailed_common_on = (CheckBox) findViewById( R.id.Check_personDetailed_common_on );
		if(friendinfo.isstar == 1){
			Check_personDetailed_common_on.setChecked( true );
		}else{
			Check_personDetailed_common_on.setChecked( false );
		}
		Check_personDetailed_common_on.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				GezitechAlertDialog.loadDialog( _this );
				
				FriendManager.getInstance().adddelisstar(friendinfo.fid, friendinfo.isstar == 1 ? 0:1,  new OnAsynRequestListener() {
					
					@Override
					public void OnAsynRequestFail(String errorCode, String errorMsg) {
						GezitechAlertDialog.closeDialog();
						if( friendinfo.isstar == 1 ){
							personDetailed_xxzl_star.setVisibility( View.VISIBLE );
							Check_personDetailed_common_on.setChecked( true );
						}else{
							personDetailed_xxzl_star.setVisibility( View.GONE );
							Check_personDetailed_common_on.setChecked( false );
						}
						Toast( errorMsg );
					}
					
					@Override
					public void OnAsynRequestCallBack(Object o) {
						GezitechAlertDialog.closeDialog();
						if( friendinfo.isstar == 1 ){
							personDetailed_xxzl_star.setVisibility( View.GONE );
							Check_personDetailed_common_on.setChecked( false );
						}else{
							personDetailed_xxzl_star.setVisibility( View.VISIBLE );
							Check_personDetailed_common_on.setChecked( true );
						}
						friendinfo.isstar = friendinfo.isstar == 1 ? 0:1;
						//联系人变动广播
						Intent intent = new Intent();
						intent.setAction( Constant.UPDATE_CONTACTS_ACTION );
						intent.putExtra("fid", friendinfo.fid ); //如果列表中有用户的列表则更新
						_this.sendBroadcast( intent );
					}
				});				
			}
		});
		
		
		//黑名单
		Check_personDetailedarrow_blacklist = (CheckBox) findViewById( R.id.Check_personDetailedarrow_blacklist ); 
		tv_personDetailed_blacklist = (TextView) findViewById(R.id.tv_personDetailed_blacklist );
		if(friendinfo.isblacklist == 1){
			Check_personDetailedarrow_blacklist.setChecked( true );
			tv_personDetailed_blacklist.setText("已加入黑名单");
		}else{
			Check_personDetailedarrow_blacklist.setChecked( false );
			tv_personDetailed_blacklist.setText("加入黑名单");
		}
		Check_personDetailedarrow_blacklist.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				GezitechAlertDialog.loadDialog( _this );
				
				FriendManager.getInstance().isblacklist(friendinfo.fid, friendinfo.isblacklist == 1 ? 0:1,  new OnAsynRequestListener() {
					
					@Override
					public void OnAsynRequestFail(String errorCode, String errorMsg) {
						GezitechAlertDialog.closeDialog();
						if( friendinfo.isblacklist == 1 ){
							Check_personDetailedarrow_blacklist.setChecked( true );
						}else{
							Check_personDetailedarrow_blacklist.setChecked( false );
						}
						Toast( errorMsg );
					}
					
					@Override
					public void OnAsynRequestCallBack(Object o) {
						GezitechAlertDialog.closeDialog();
						if( friendinfo.isblacklist == 1 ){
							Check_personDetailedarrow_blacklist.setChecked( false );
							tv_personDetailed_blacklist.setText("已加入黑名单");
						}else{
							Check_personDetailedarrow_blacklist.setChecked( true );
							tv_personDetailed_blacklist.setText("加入黑名单");
						}
						friendinfo.isblacklist = friendinfo.isblacklist == 1 ? 0:1;
						//联系人变动广播
						Intent intent = new Intent();
						intent.setAction( Constant.UPDATE_CONTACTS_ACTION );
						intent.putExtra("fid", friendinfo.fid ); //如果列表中有用户的列表则更新
						_this.sendBroadcast( intent );
					}
				});			
			}
		});
		//删除
		tv_personDetailed_del = (TextView) findViewById( R.id.tv_personDetailed_del );
		tv_personDetailed_del.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				final YMDialog ymdialog = new YMDialog( _this );
				ymdialog.setHintMsg("你确定要删除?").setCloseButton( new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						ymdialog.dismiss();
					}
				}).setConfigButton( new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ymdialog.dismiss();
				
						GezitechAlertDialog.loadDialog( _this );
						FriendManager.getInstance().deleteFriend(friendinfo.fid, new OnAsynRequestListener() {
							
							@Override
							public void OnAsynRequestFail(String errorCode, String errorMsg) {
								GezitechAlertDialog.closeDialog();
								Toast( errorMsg );
							}
							
							@Override
							public void OnAsynRequestCallBack( Object o ) {
								GezitechAlertDialog.closeDialog();
								if( o.equals("1") ){
									Toast( "删除成功" );
									//删除用户成功
								}
								try{
									//删除用户信息
									ChatManager.getInstance().deleleFriendOne( friendinfo.fid );
									//删除聊天记录
									ChatManager.getInstance().deleteChatContent( friendinfo.fid,1 );
									//删除聊天列表
									ChatManager.getInstance().deleteChat( friendinfo.fid,1  );
									
								}catch(Exception ex){}
								//联系人变动广播
								Intent intent = new Intent();
								intent.setAction( Constant.UPDATE_CONTACTS_ACTION );
								intent.putExtra("fid", friendinfo.fid ); //如果列表中有用户的列表则更新
								_this.sendBroadcast( intent );
								
								GezitechService.sendMessage( friendinfo.fid, 18 );
							}
						});
					}
				});
			}
		});
		
		//如果是企业用户 则显示企业的信息
		
		if( friendinfo.isbusiness  >0  ){
			ll_company_box = (LinearLayout) findViewById( R.id.ll_company_box);
			tv_shangjiaDetailed_enterprisename = ( TextView ) findViewById( R.id.tv_shangjiaDetailed_enterprisename );
			tv_shangjiaDetailed_enterprisetype = ( TextView ) findViewById( R.id.tv_shangjiaDetailed_enterprisetype );
			
			tv_shangjiaDetailed_enterprisename.setText( "商家名称" );
			tv_shangjiaDetailed_enterprisetype.setText( "商家类型" );
			
			text_shangjiaDetailed_enterprisename = ( TextView ) findViewById( R.id.text_shangjiaDetailed_enterprisename );
			text_shangjiaDetailed_enterprisetype = ( TextView ) findViewById( R.id.text_shangjiaDetailed_enterprisetype );
			ll_company_box.setVisibility( View.VISIBLE );
			text_shangjiaDetailed_enterprisename.setText( FieldVal.value( friendinfo.company_name ) );
			text_shangjiaDetailed_enterprisetype.setText( FieldVal.value( friendinfo.company_type ) );
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		String action = data.getAction();
		switch( requestCode ){
		case 1001://设置备注回调
			tv_personDetailed_setting.setText( action );
			break;
		
		}
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bt_home_msg:
			returnData();
			break;

		default:
			break;
		}
	}
	private void returnData(){
		String notes = tv_personDetailed_setting.getText().toString().trim();
		if( !notes.equals("设置备注") ){
			Intent intent = new Intent();
			intent.setAction(  notes );
			this.setResult( 2001 , intent) ;
		}
		finish();
	}
	@Override
	public void onBackPressed() {
		returnData();
	}
}
