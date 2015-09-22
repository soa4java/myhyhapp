package com.hyh.www.adapter;

import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynInsertListener;
import com.gezitech.service.managers.ChatManager;
import com.gezitech.service.managers.ShoutManager;
import com.gezitech.util.DateUtil;
import com.gezitech.util.DateUtils;
import com.gezitech.util.ToastMakeText;
import com.gezitech.widget.MyListView;
import com.gezitech.widget.RemoteImageView;
import com.hyh.www.R;
import com.hyh.www.chat.ChatUtils;
import com.hyh.www.entity.Chat;
import com.hyh.www.entity.ChatContent;
import com.hyh.www.widget.YMDialog;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 
 * @author xiaobai
 * 2014-10-15
 * @todo(  回话 和 喊一喊 )
 */
public class ChatAdapter extends BasicAdapter{

	
	private int isfriend; //1 是朋友  2 是非朋友 喊一喊界面适配器
	private Activity activity;

	public ChatAdapter(int isfriend, Activity activity  ){
		super();
		this.isfriend = isfriend;
		this.activity = activity;
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
	public View getView( final int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		Hv  hv;
		if( view == null ){
			view =  inflater.inflate( R.layout.list_session_item , null);
			hv = new Hv( view );
			view.setTag( hv );
		}else{
			hv = (Hv)view.getTag();
		}
		final Chat item = (Chat)this.getItem(position);
		
		hv.iv_session_item.setImageUrl( item.head );
		
		if( item.unreadcount >0 ){
			hv.bt_session_item.setVisibility( View.VISIBLE );
			hv.bt_session_item.setText( item.unreadcount+"" );
		}else{
			hv.bt_session_item.setVisibility( View.GONE );
		}
		hv.tv_session_item_name.setText( item.username );
		hv.tv_session_item_time.setText( item.ctime >0 ? DateUtil.getShortTime( item.ctime ) : "" );
		hv.tv_session_item_context.setText( item.lastcontent );
		
		//分割线

		hv.v_line.setBackgroundColor( Color.parseColor("#ececec") );
		
		hv.session_item.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onClickDataPress.onDataPerss(item, position );
			}
		});
		hv.session_item.setOnLongClickListener( new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				
				final YMDialog ym = new YMDialog(activity);
				ym.setHintMsg("确定删除会话?").setConfigButton(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ym.dismiss();
						try{
							ChatManager.getInstance().deleteChat( item.uid  , 1  );
							remove( position );
						}catch( Exception ex ){}
						
					}
				}).setCloseButton(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ym.dismiss();
					}
				});
				
				
				return false;
			}
		});
		return view;
	}
	//缓存
	public class Hv{
		public RemoteImageView  iv_session_item;
		public TextView bt_session_item;
		public TextView tv_session_item_name;
		public TextView tv_session_item_time;
		public TextView tv_session_item_context;
		public RelativeLayout session_item;
		public View v_line;
		public Hv( View view){
			iv_session_item = ( RemoteImageView ) view.findViewById( R.id.iv_session_item );
			bt_session_item = (TextView) view.findViewById( R.id.bt_session_item );
			tv_session_item_name = (TextView) view.findViewById( R.id.tv_session_item_name );
			tv_session_item_time = (TextView) view.findViewById( R.id.tv_session_item_time );
			tv_session_item_context = (TextView) view.findViewById( R.id.tv_session_item_context );
			v_line = (View) view.findViewById( R.id.v_line );
			session_item = (RelativeLayout) view.findViewById( R.id.session_item );
		}
		
	}
/*	
	//判断当前uid在listView 中position 位置
	public void setItemViewDataAction(ChatContent chatContent, MyListView listView){
		if( chatContent == null ) return;
		int position = -1;
		int topposition = -1; //有用户是置顶 计算位置
		Chat chat = null,tempChat = null;
		for( int i = 0; i<list.size(); i++ ){
			tempChat = (Chat)list.get( i );
			if( tempChat.uid == chatContent.chatid ){
				position = i;
				chat = tempChat;
			}
			if( tempChat.istop == 1 ) topposition = i;
		}
		if( position >-1 ){
			
			Chat chatTemp = ChatManager.getInstance().getChatItem(chatContent.chatid);
			chat.unreadcount = chatTemp.unreadcount;
			chat.ctime = chatContent.ctime;
			chat.lastcontent = ChatUtils.getTypeStr(chatContent.body, chatContent.type);
			
			list.set(position, chat);
			
			getViewByPosition(position, listView, chatTemp  );
			
		}else{
			//创建新的聊天
			Chat chatTemp = ChatManager.getInstance().getChatItem(chatContent.chatid);
			if( (topposition>-1 && chatTemp.istop >0) || topposition<0 ){//有置顶聊天 并且 该用户也是置顶 聊天的 或者没有置顶的
				list.add( 0, chatTemp );
			}else if( topposition>-1 && chatTemp.istop <=0 ){
				if( list.size() > topposition+1 ) list.add( topposition+1, chatTemp );
				else list.add( chatTemp );
			}
			this.notifyDataSetChanged();
		}
	}
	//获取item view
	public void getViewByPosition(int pos,MyListView listView,Chat chatTemp ) {
		 final int firstListItemPosition = listView.getFirstVisiblePosition();
		 final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
		 pos += 2;
		 View view = null;
		 if (pos < firstListItemPosition || pos > lastListItemPosition ) {
			 view =  listView.getAdapter().getView(pos, null, listView);
		 } else {
		     final int childIndex = pos - firstListItemPosition;
		     view =   listView.getChildAt(childIndex);
		 }
		try{
			 Hv hv = new Hv( view );
		    	if( chatTemp.unreadcount >0 ){
					hv.bt_session_item.setVisibility( View.VISIBLE );
					hv.bt_session_item.setText( chatTemp.unreadcount+"" );
				}else{
					hv.bt_session_item.setVisibility( View.GONE );
				}
		    	hv.tv_session_item_time.setText( chatTemp.ctime >0 ? DateUtil.getShortTime( chatTemp.ctime ) : "" );
				hv.tv_session_item_context.setText( chatTemp.lastcontent );
				
		}catch(Exception ex){
			ex.printStackTrace();
		}
	} */

}
