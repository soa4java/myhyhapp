package com.hyh.www.user;

import java.util.ArrayList;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.service.GezitechService;
import com.gezitech.service.managers.ChatManager;
import com.gezitech.service.managers.SystemManager;
import com.gezitech.service.managers.UserManager;
import com.gezitech.service.xmpp.IMChatService;
import com.gezitech.service.xmpp.XmppConnectionManager;
import com.hyh.www.LoginActivity;
import com.hyh.www.R;
import com.hyh.www.chat.ChatActivity;
import com.hyh.www.entity.Friend;
import com.hyh.www.widget.YMDialog;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author xiaobai (系统设置)
 */
public class SystemSettingActivity extends GezitechActivity implements
		OnClickListener {

	private SystemSettingActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_title;
	
	private RelativeLayout systemsetting_xiugaimima;//修改密码
	private RelativeLayout systemsetting_xinxiaoxitixing;//新消息提醒
	private RelativeLayout systemsetting_banbengengxin;//版本更新
	private RelativeLayout systemsetting_feedback;//意见反馈
	private RelativeLayout systemsetting_contact;//联系我们
	private RelativeLayout systemsetting_exit;//退出当前账号
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_setting);

		_init();
	}

	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(_this.getResources().getString(
				R.string.system_settings));

		systemsetting_feedback = (RelativeLayout) findViewById(R.id.systemsetting_feedback);
		systemsetting_feedback.setOnClickListener(this);
		
		systemsetting_xinxiaoxitixing = (RelativeLayout) findViewById(R.id.systemsetting_xinxiaoxitixing);
		systemsetting_xinxiaoxitixing.setOnClickListener(this);
		
		systemsetting_xiugaimima = (RelativeLayout) findViewById(R.id.systemsetting_xiugaimima);
		systemsetting_xiugaimima.setOnClickListener(this);
		
		systemsetting_contact = (RelativeLayout) findViewById(R.id.systemsetting_contact);
		systemsetting_contact.setOnClickListener(this);
		
		systemsetting_banbengengxin=(RelativeLayout) findViewById(R.id.systemsetting_banbengengxin);
		systemsetting_banbengengxin.setOnClickListener( this );
		
		systemsetting_exit=(RelativeLayout) findViewById(R.id.systemsetting_exit);
		systemsetting_exit.setOnClickListener( this );
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bt_home_msg:
			finish();
			break;

		case R.id.systemsetting_xiugaimima://修改密码
			_this.startActivity(new Intent(_this, ChangePassworldActivity.class));
			break;
		case R.id.systemsetting_banbengengxin://版本更新
			UmengUpdateAgent.update(_this);
			UmengUpdateAgent.setUpdateAutoPopup(false);
			UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
				@Override
				public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
					switch (updateStatus) {
		            case 0: 
		                UmengUpdateAgent.showUpdateDialog(_this, updateInfo);
		                break;
		            case 1: 
		            	_this.Toast("您已经是最新的版本了");
		                break;
		            case 2:
//		            	_this.Toast("没有wifi连接， 只在wifi下更新");
		            	UmengUpdateAgent.showUpdateDialog(_this, updateInfo);
		                break;
		            case 3: 
		            	_this.Toast("访问超时");
		                break;
		            }
				}
			});
			break;
		case R.id.systemsetting_xinxiaoxitixing: //新消息提示
			_this.startActivity(new Intent(_this, NewMessageActivity.class));
			break;
			
		case R.id.systemsetting_feedback: //意见反馈
			//_this.startActivity(new Intent(_this, FeedbackActivity.class));
			
			loadKefuList();
			
			break;
			
		case R.id.systemsetting_contact: //联系我们
			
			GezitechAlertDialog.loadDialog( this );
			SystemManager.getInstance().configuration(new OnAsynGetListListener() {
				
				@Override
				public void OnAsynRequestFail(String errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					GezitechAlertDialog.closeDialog();
					Toast( errorMsg );
				}
				
				@Override
				public void OnGetListDone(ArrayList<GezitechEntity_I> list) {
					GezitechAlertDialog.closeDialog();
					Intent intent = new Intent(_this, ContactsActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("config", list );
					intent.putExtras( bundle );
					_this.startActivity( intent );
				}
			});
			break;
		case R.id.systemsetting_exit: //退出登录
			
			
			final YMDialog ymdialog = new YMDialog( this );
			ymdialog.setHintMsg("你确定要退出吗?").setConfigButton( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					ymdialog.dismiss();
					
					//退出登录设置用户离线状态
					UserManager.getInstance().exitLogin( user.id );
					
					//情况当前用户friend用户信息
					try{
						ChatManager.getInstance().deleteContacts(); //删除当前用户的联系列表
						GezitechService.getInstance().clearCurrentUser();
					}catch( Exception ex){}
					Intent intent = new Intent(_this, LoginActivity.class);		
					_this.startActivity( intent );
					
					XmppConnectionManager.getInstance().disconnect();
					GezitechApplication.connection = null;
					
				/*	
					Intent chatServer = new Intent(_this, IMChatService.class);
					_this.stopService(chatServer);
					*/
					GezitechService.getInstance().unbindBackgroundService( GezitechApplication.getContext() );
					
					GezitechService.getInstance().exitApp( _this );
					
					
					
					
					
				}
			}).setCloseButton( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ymdialog.dismiss();
				}
			});
			break;

		default:
			break;
		}

	}
	public void loadKefuList() {
		GezitechAlertDialog.loadDialog( _this );
		UserManager.getInstance().getkefu( new OnAsynGetOneListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				GezitechAlertDialog.closeDialog();
				Toast( errorMsg );
			}
			
			@Override
			public void OnGetOneDone(GezitechEntity_I entity) {
				GezitechAlertDialog.closeDialog();
				Friend item = (Friend) entity;
				//跳转到聊天界面
				Intent intent = new Intent(_this, ChatActivity.class );
				intent.putExtra("uid", item.id);
				
				intent.putExtra("username", item.nickname == null || item.nickname.equals("null") || item.nickname.equals("") ? item.username : item.nickname );
				
				intent.putExtra( "head", item.head );
				
				intent.putExtra("isfriend", 3 );
				intent.putExtra("isbusiness", item.isbusiness );
				_this.startActivity( intent );
				
				
			}
		});
	}

}
