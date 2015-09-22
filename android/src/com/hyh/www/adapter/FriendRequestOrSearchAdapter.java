package com.hyh.www.adapter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.jivesoftware.smack.Chat;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynRequestListener;
import com.gezitech.entity.PageList;
import com.gezitech.entity.User;
import com.gezitech.service.GezitechService;
import com.gezitech.service.managers.ChatManager;
import com.gezitech.service.managers.FriendManager;
import com.gezitech.service.managers.UserManager;
import com.gezitech.service.xmpp.Constant;
import com.gezitech.service.xmpp.MessageSend;
import com.gezitech.service.xmpp.XmppConnectionManager;
import com.gezitech.widget.RemoteImageView;
import com.gezitech.widget.listgroup.PinnedHeaderListView;
import com.gezitech.widget.listgroup.PinnedHeaderListView.PinnedHeaderAdapter;
import com.hyh.www.R;
import com.hyh.www.entity.Bill;
import com.hyh.www.entity.Businesslist;
import com.hyh.www.entity.Contacts;
import com.hyh.www.entity.Friend;
import com.hyh.www.entity.Incomelist;
import com.hyh.www.session.NewFriendActivity;
import com.hyh.www.session.SearchFriendActivity;
import com.hyh.www.user.PersonDetailedInformationActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author xiaobai
 * 2014-10-15
 * @todo( 好友的请求 和  好友的搜索的适配器   )
 */
public class FriendRequestOrSearchAdapter extends BasicAdapter{
	private int type;
	private GezitechActivity activity;
	private User user;
	public FriendRequestOrSearchAdapter(int type,GezitechActivity activity ){
		this.type = type;
		this.activity = activity;
		this.user = GezitechService.getInstance().getCurrentUser();
		//判断是否已经链接
		if(  GezitechApplication.connection == null || ( !GezitechApplication.connection.isConnected() ) || (  XmppConnectionManager.getInstance().getConnection() !=null && !XmppConnectionManager.getInstance().getConnection().isConnected() ) ){
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
		return list.get( position );
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		view = inflater.inflate(R.layout.list_add_request, null);
		final GezitechEntity_I item = getItem( position  );
		RemoteImageView newFriend_photo_one = (RemoteImageView) view.findViewById( R.id.newFriend_photo_one );
		TextView newFriend_nickname_one = (TextView) view.findViewById( R.id.newFriend_nickname_one );
		TextView newFriend_account_one = (TextView) view.findViewById( R.id.newFriend_account_one );
		
		final Button newFriend_agreedone = (Button)view.findViewById( R.id.newFriend_agreedone );
		final Button newFriend_refusedone = (Button)view.findViewById( R.id.newFriend_refusedone );
		
		
		
		if( type == 1 ){//请求
			
			Friend item_ = (Friend) item;
			
			newFriend_photo_one.setImageUrl( item_.head );
			newFriend_nickname_one.setText( item_.nickname==null || item_.nickname.equals("null") || item_.nickname.equals("") ? "" : item_.nickname );
			newFriend_account_one.setText( item_.username );
			
		}else if( type == 2 ){//搜索
			
			final User item_ = (User) item;
			
			newFriend_photo_one.setImageUrl( item_.head );
			newFriend_nickname_one.setText( ( item_.nickname==null || item_.nickname.equals("null") || item_.nickname.equals("") ) ? "" : item_.nickname );
			newFriend_account_one.setText( item_.username );
			
			newFriend_refusedone.setVisibility( View.GONE );
			//if( user.isbusiness <1 && !(item_.groupId <3) ){
			if( !(item_.groupId <3) ){
				newFriend_agreedone.setText("添加");
				newFriend_agreedone.setVisibility( View.VISIBLE );
			}else{
				newFriend_agreedone.setVisibility( View.GONE );
			}
			//点击item
			RelativeLayout newFriend_Relative_one = (RelativeLayout) view.findViewById( R.id.newFriend_Relative_one );
			
			newFriend_Relative_one.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					LookFriendData( item_.id );
				}
			});
			
		}
		//同意/添加
		newFriend_agreedone.setOnClickListener(  new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				agreeRequest( item ,position, newFriend_agreedone );
			}
		});
		//拒绝
		newFriend_refusedone.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				refusedRequest( ( Friend) item, position );
			}
		});
		
		
		return view;
	}
	//查看资料
	protected void LookFriendData(long fid) {
		// TODO Auto-generated method stub
		GezitechAlertDialog.loadDialog( activity );
		UserManager.getInstance().getfrienddata(fid,  new OnAsynGetOneListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				// TODO Auto-generated method stub
				GezitechAlertDialog.closeDialog();
				activity.Toast( errorMsg );
			}
			
			@Override
			public void OnGetOneDone(GezitechEntity_I entity) {
				// TODO Auto-generated method stub
				GezitechAlertDialog.closeDialog();
				Intent intent = new Intent(activity, PersonDetailedInformationActivity.class ) ;
				Bundle bundle = new Bundle();
				bundle.putSerializable("friendinfo", (Friend)entity );
				intent.putExtras( bundle );
				activity.startActivity(intent);
			}
		});
		
		
	}
	//拒绝
	protected void refusedRequest(final Friend item, final int position ) {
		// TODO Auto-generated method stub
		GezitechAlertDialog.loadDialog( activity );
		//Log.v("拒绝好友", item.fid+"==拒绝好友");
		FriendManager.getInstance().denyaddFriend(item.fid, new OnAsynRequestListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				// TODO Auto-generated method stub
				GezitechAlertDialog.closeDialog();
				activity.Toast( errorMsg );
			}
			
			@Override
			public void OnAsynRequestCallBack(Object o) {
				// TODO Auto-generated method stub
				GezitechAlertDialog.closeDialog();
				activity.Toast("已被拒绝");
				remove( position );
				if( list.size()<= 0  ){
					( (NewFriendActivity) activity).setShowState( true );
				}
				GezitechService.sendMessage( item.fid, 17 );
			}
		});
	}
	//同意
	protected void agreeRequest(final GezitechEntity_I item, final int position, final Button newFriend_agreedone ) {
		// TODO Auto-generated method stub
		GezitechAlertDialog.loadDialog( activity );
		
		if( type == 1 ){//好友请求
			
			final Friend item_ = (Friend) item;
			
			FriendManager.getInstance().agreeaddFriend( item_.fid, new OnAsynRequestListener() {
				
				@Override
				public void OnAsynRequestFail(String errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					GezitechAlertDialog.closeDialog();
					activity.Toast( errorMsg );
				}
				
				@Override
				public void OnAsynRequestCallBack(Object o) {
					// TODO Auto-generated method stub
					GezitechAlertDialog.closeDialog();
					activity.Toast("已同意");
					remove( position );
					if( list.size()<= 0  ){
						( (NewFriendActivity) activity).setShowState( true );
					}
					//把好友的资料设置为已经是好友的缓存
					//ChatManager.getInstance().updateChatGroup( item_.fid );
					
					ChatManager.getInstance().setFriendInfo("isfriend","1",item_.fid);
				//	ChatManager.getInstance().setChatInfo("isfriend",( item_.groupId<3 ? 3 : 1 )+"" ,item_.fid);
					
					//联系人变动广播
					Intent intent = new Intent();
					intent.setAction( Constant.UPDATE_CONTACTS_ACTION );
					intent.putExtra("fid", 0);
					activity.sendBroadcast( intent );
					GezitechService.sendMessage( item_.fid , 16 );
				}
			});
		}else if( type == 2 ){//好友的搜索添加
			final User item_ = (User) item;
			FriendManager.getInstance().addFriend( item_.id, new OnAsynRequestListener() {
				
				@Override
				public void OnAsynRequestFail(String errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					GezitechAlertDialog.closeDialog();
					activity.Toast( errorMsg );
				}
				
				@Override
				public void OnAsynRequestCallBack(Object o) {
					// TODO Auto-generated method stub
					GezitechAlertDialog.closeDialog();
					activity.Toast("添加好友请求已发送,请等待对方同意");
					GezitechService.sendMessage( item_.id,15 );
					newFriend_agreedone.setText( "等待验证" );
					newFriend_agreedone.setOnClickListener( null );
				}
			});
			
			
		}
	}

}
