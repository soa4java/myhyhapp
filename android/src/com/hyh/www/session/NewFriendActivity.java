package com.hyh.www.session;

import java.util.ArrayList;

import cn.sharesdk.onekeyshare.ShareTools;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.widget.ListViewNoScroll;
import com.hyh.www.R;
import com.hyh.www.adapter.FriendRequestOrSearchAdapter;
import com.hyh.www.entity.FieldVal;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
/**
 * 
 * @author xiaobai (新的朋友)
 */
public class NewFriendActivity extends GezitechActivity implements OnClickListener {
	
	private NewFriendActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_title;
	private EditText ed_newFriend_phone_account;
	private Button btn_newFriend_phone_account;
	private ListViewNoScroll list_view_no_scroll;
	private Button newFriend_weixin;
	private Button newFriend_tongxunlu;
	private Button personDetailed_weibo;
	private TextView newFriend_no;
	private ArrayList<GezitechEntity_I> friendRequestList = new ArrayList<GezitechEntity_I>();
	private FriendRequestOrSearchAdapter friendRequestOrSearchAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_friend);
		
		Intent  intent = _this.getIntent();
		friendRequestList  = ( ArrayList<GezitechEntity_I> ) ( intent.getExtras().getSerializable("friendRequestList") ) ;
		
		
		_init();
	}
	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

	    tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(_this.getResources().getString(
				R.string.xindepengyou));
		
		//搜索
		ed_newFriend_phone_account = ( EditText ) findViewById( R.id.ed_newFriend_phone_account );
		btn_newFriend_phone_account = (Button) findViewById( R.id.btn_newFriend_phone_account );
		btn_newFriend_phone_account.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String ed_newFriend_phone_accountVal = ed_newFriend_phone_account.getText().toString().trim();
				if( ed_newFriend_phone_accountVal.equals("") ){
					Toast("请输入手机号/喊一喊账号");
					return;
				}
				Intent intent = new Intent(_this,SearchFriendActivity.class );
				intent.putExtra("key", ed_newFriend_phone_accountVal );
				
				_this.startActivity( intent );
			}
		});
		
		//好友请求
		list_view_no_scroll = (ListViewNoScroll) findViewById( R.id.list_view_no_scroll );
		newFriend_no = (TextView) findViewById( R.id.newFriend_no );
		if( friendRequestList != null && friendRequestList.size()>0  ){ //有好友的请求
			setShowState( false );			
			friendRequestOrSearchAdapter = new FriendRequestOrSearchAdapter( 1, _this );			
			list_view_no_scroll.setAdapter( friendRequestOrSearchAdapter );			
			friendRequestOrSearchAdapter.addList( friendRequestList, false );
		}else{//没有好友请求
			setShowState( true );
		}
		
		
		
		//分享
		newFriend_weixin = (Button) findViewById( R.id.newFriend_weixin );
		newFriend_tongxunlu = (Button) findViewById( R.id.newFriend_tongxunlu );
		personDetailed_weibo = (Button) findViewById( R.id.personDetailed_weibo );
		newFriend_weixin.setOnClickListener( this );
		newFriend_tongxunlu.setOnClickListener( this );
		personDetailed_weibo.setOnClickListener( this );
		
		
	}
	//显示状态
	public void setShowState(boolean isEmpty){
		if( isEmpty ){
			list_view_no_scroll.setVisibility( View.GONE );
			newFriend_no.setVisibility( View.VISIBLE );
		}else{
			list_view_no_scroll.setVisibility( View.VISIBLE );
			newFriend_no.setVisibility( View.GONE );
		}
		
	}	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bt_home_msg:
			finish();
			break;
		case R.id.newFriend_weixin:
			ShareTools.getInstance().share(_this, Wechat.NAME, getResources().getString( R.string.evenote_title), getResources().getString( R.string.share_url), String.format( getResources().getString( R.string.share_content)+"", FieldVal.value( user.inviteCode )+"" ), "" ); 
			break;
		case R.id.newFriend_tongxunlu:
			_phoneBook();		
			break;
		case R.id.personDetailed_weibo:
			ShareTools.getInstance().share(_this, SinaWeibo.NAME, getResources().getString( R.string.evenote_title), getResources().getString( R.string.share_url),  String.format( getResources().getString( R.string.share_content),user.inviteCode ), "" ); 
			break;
		default:
			break;
		}
	}
	//电话簿邀请
	private void _phoneBook(){
		_this.startActivity( new Intent(_this,ContactsAddBuddyPhoneActivity.class) );
	}
	private void returnData() {
		
		_this.finish();
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		returnData();
		
	}
}
