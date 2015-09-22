package com.hyh.www.adapter;

import org.json.JSONException;
import org.json.JSONObject;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechException;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynInsertListener;
import com.gezitech.entity.User;
import com.gezitech.http.Response;
import com.gezitech.service.GezitechService;
import com.gezitech.service.managers.ShoutManager;
import com.gezitech.service.managers.UserManager;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.gezitech.util.DateUtil;
import com.gezitech.util.IOUtil;
import com.gezitech.util.StringUtil;
import com.gezitech.util.IOUtil.CacheCompleteListener;
import com.gezitech.util.ToastMakeText;
import com.gezitech.widget.MyListView;
import com.gezitech.widget.RemoteImageView;
import com.hyh.www.R;
import com.hyh.www.chat.ChatActivity;
import com.hyh.www.entity.Chat;
import com.hyh.www.entity.ChatContent;
import com.hyh.www.entity.FieldVal;
import com.hyh.www.entity.Friend;
import com.hyh.www.session.SessionFramgent;
import com.hyh.www.widget.ImageShow;
import com.hyh.www.widget.YMDialog;
import com.hyh.www.widget.YMDialog2;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.ClipData.Item;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 
 * @author xiaobai
 * 2014-10-15
 * @todo(  回话 中的 喊一喊 )
 */
public class ChatHYHAdapter extends BasicAdapter{

	
	private int isfriend; //1 是朋友  2 是非朋友 喊一喊界面适配器
	private Activity activity = null;
	private int lastTimeposition = -1;
	private int currTimeposition = -1;
	private MyListView listView;
	private String currentAudioPath;
	public MediaPlayer mediaPlayer;
	private User user;
	private GezitechDBHelper<Chat> chatDb;
	private GezitechDBHelper<ChatContent> chatContentDB;
	private SessionFramgent sessionFramgent;
	public ChatHYHAdapter(Activity activity, int isfriend , MyListView listView, SessionFramgent sessionFramgent){
		super();
		this.isfriend = isfriend;
		this.activity  = activity;
		this.listView = listView;
		this.sessionFramgent = sessionFramgent;
		user = GezitechService.getInstance().getCurrentLoginUser( activity );
		chatDb = new GezitechDBHelper<Chat>( Chat.class );
		chatContentDB = new GezitechDBHelper<ChatContent>( ChatContent.class );
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
		final ChatContent itme = ( ChatContent ) getItem( position );
		final Hv  hv;
		if( view == null ){
			view =  inflater.inflate( R.layout.list_session_hyh_item , null);
			hv = new Hv( view );
			view.setTag( hv );
		}else{
			hv = (Hv)view.getTag();
		}
		String timeStr = DateUtil.getShortTime( itme.ctime );
		hv.tv_receive_time.setText( itme.uid == user.id ? "于"+timeStr+"发布" : "于"+timeStr+"收到");
		hv.tv_reply.setTextColor( activity.getResources().getColor( R.color.color949494  ) );
		hv.tv_reply.setBackgroundResource( android.R.color.transparent );
		//聊天列表
		boolean isreplay = false; //是否回复
		//是否有人回复
		boolean isUserReplay = false;
		
		if( itme.chatUser.size()> 0 ){
			hv.ll_chat_person.setVisibility( View.VISIBLE );
			hv.ll_chat_person.removeAllViews();
			if( user.isbusiness > 0 ) isreplay = true;
			isUserReplay = true;
			
			for( int i = 0; i<itme.chatUser.size(); i++){
				final int index = i;
				final View chat_user_view = LayoutInflater.from( activity).inflate(R.layout.list_session_item, null);
				final Chat chat = (Chat)itme.chatUser.get( i );
				RemoteImageView iv_session_item = ( RemoteImageView ) chat_user_view.findViewById( R.id.iv_session_item );
				TextView bt_session_item = (TextView) chat_user_view.findViewById( R.id.bt_session_item );
				TextView tv_session_item_name = (TextView) chat_user_view.findViewById( R.id.tv_session_item_name );
				TextView tv_session_item_time = (TextView) chat_user_view.findViewById( R.id.tv_session_item_time );
				TextView tv_session_item_context = (TextView) chat_user_view.findViewById( R.id.tv_session_item_context );
				View v_line = (View) chat_user_view.findViewById( R.id.v_line );
				RelativeLayout session_item = (RelativeLayout) chat_user_view.findViewById( R.id.session_item );
				iv_session_item.setImageUrl( chat.head );
				
				if( chat.unreadcount >0 ){
					bt_session_item.setVisibility( View.VISIBLE );
					bt_session_item.setText( chat.unreadcount+"" );
				}else{
					bt_session_item.setVisibility( View.GONE );
				}
				tv_session_item_name.setText( chat.username );
				tv_session_item_time.setText( chat.ctime >0 ? DateUtil.getShortTime( chat.ctime ) : "" );
				tv_session_item_context.setText( chat.lastcontent );
				
				//分割线

				v_line.setBackgroundColor( Color.parseColor("#ececec") );
				
				session_item.setOnClickListener( new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
					
						GezitechAlertDialog.loadDialog(activity);
						UserManager.getInstance().getfrienddata(chat.uid, new OnAsynGetOneListener(){
							@Override
							public void OnAsynRequestFail(String errorCode, String errorMsg) {
								GezitechAlertDialog.closeDialog();
								new ToastMakeText(activity).Toast( errorMsg );
							}
							
							@Override
							public void OnGetOneDone(GezitechEntity_I entity) {
								GezitechAlertDialog.closeDialog();
								Friend friend = (Friend) entity;
								//是朋友
//								if( friend.isfriend >0  ){
//									
//									final YMDialog2 ymdialog = new YMDialog2( activity );
//									ymdialog.setHead("提示")
//									.setHintMsg("已经是好友,请在私聊中回复!")
//									.setConfigText( "关闭" )
//									.setCloseButton( new OnClickListener() {
//										
//										@Override
//										public void onClick(View v) {
//											// TODO Auto-generated method stub
//											ymdialog.dismiss();
//										}
//									});
//									
//								}else{
									//跳转到聊天界面
									Intent intent = new Intent(activity, ChatActivity.class );
									intent.putExtra("uid", friend.fid);
									
									intent.putExtra("username", FieldVal.value( friend.nickname ).equals("") ? friend.username : friend.nickname );
									
									intent.putExtra("head", friend.head);
									
									intent.putExtra("hyhid", chat.hyhid );
									
									intent.putExtra("isbusiness", friend.isbusiness );
									intent.putExtra("isfriend", friend.isfriend );
									
									intent.putExtra("body", itme.body );
									
									activity.startActivity( intent );
									
									
								//}
								//更新未读条数
								chat.unreadcount = 0;
								chatDb.save( chat );
								
								itme.chatUser.set(index, chat);
								
								setItem( itme , position );
								
								sessionFramgent.updateUnreadcount();
							}
						});
						
					}
				});
				//删除回话 只有用户才能删除
				if(  user.id == itme.uid ){
					
					session_item.setOnLongClickListener( new OnLongClickListener() {
						
						@Override
						public boolean onLongClick(View v) {
							final YMDialog ym = new YMDialog(activity);
							ym.setHintMsg("确定删除会话?").setConfigButton(new OnClickListener() {
			
								@Override
								public void onClick(View v) {
									ym.dismiss();
									RequestParams params = new RequestParams();
									params.put("bid", user.isbusiness > 0 ? user.id : chat.uid );
									params.put("sid", chat.hyhid );
									GezitechAlertDialog.loadDialog(activity);
									ShoutManager.getInstance().deleteShoutSession(params,
											new OnAsynInsertListener() {
			
												@Override
												public void OnAsynRequestFail(
														String errorCode, String errorMsg) {
													new ToastMakeText(activity).Toast( errorMsg );
													GezitechAlertDialog.closeDialog();
												}
			
												@Override
												public void onInsertDone(String id) {
													GezitechAlertDialog.closeDialog();
													try{
														chatDb.delete("hyhid="+itme.hyhid+" and myuid="+user.id + " and uid = "+ chat.uid);
														chatContentDB.delete("hyhid="+itme.hyhid+" and myuid="+user.id + " and chatid = " + chat.uid );
													
													}catch(Exception ex){
														
													}
													hv.ll_chat_person.removeViewAt( index );
													hv.tv_reply.setText( ( itme.chatUser.size()-1 ) + "人回复" );
													sessionFramgent.updateUnreadcount();
												}
											});
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
				}
				hv.ll_chat_person.addView( chat_user_view );
			}
		}else{
			hv.ll_chat_person.removeAllViews();
			hv.ll_chat_person.setVisibility( View.GONE );			
		}
		
		//立即回复
		if( user.isbusiness > 0 &&  !isreplay && user.id != itme.uid ){//是商家 并没有回复
			hv.tv_reply.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {//立即回复跳转到聊天界面	
					GezitechAlertDialog.loadDialog(activity);
					UserManager.getInstance().getfrienddata(itme.hyhid , itme.uid, new OnAsynGetOneListener(){
					//ChatManager.getInstance().getFriendData(itme.uid, new OnAsynGetOneListener() {
						
						@Override
						public void OnAsynRequestFail(String errorCode, String errorMsg) {
							GezitechAlertDialog.closeDialog();
							new ToastMakeText(activity).Toast( errorMsg );
						}
						
						@Override
						public void OnGetOneDone(GezitechEntity_I entity) {
							
							
							GezitechAlertDialog.closeDialog();
							Friend friend = (Friend) entity;
							
							if( friend.hasactivitysession >0  ){
								
								final YMDialog2 ymdialog = new YMDialog2( activity );
								ymdialog.setHead("提示")
								.setHintMsg("您和该用户有其他会话未结束，暂时不能再回复新的喊一喊!")
								.setConfigText( "关闭" )
								.setCloseButton( new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										ymdialog.dismiss();
									}
								});
								
							}else{
								
								
								//跳转到聊天界面
								Intent intent = new Intent(activity, ChatActivity.class );
								intent.putExtra("uid", friend.fid);
								
								intent.putExtra("username", FieldVal.value( friend.nickname ).equals("") ? friend.username : friend.nickname );
								
								intent.putExtra("head", friend.head);
								
								intent.putExtra("isbusiness", friend.isbusiness );
								
								intent.putExtra("isfriend", 2 );
								intent.putExtra("hyhid", itme.hyhid );
								intent.putExtra("body", itme.body );
								
								activity.startActivity( intent );
							}
						}
					});
					
				}
			});
		}else{
			hv.tv_reply.setOnClickListener( null );
		}
		
		hyhAction( hv.tv_diff_time, hv.tv_beizhu,hv.myrelease_NUMBER, hv.iv_pic, hv.rl_yuyin_bg, hv.iv_voice_sign, hv.tv_reply,hv.ll_chat_person, hv.myrelease_bg, hv.v_line1, hv.v_line2,hv.tv_diff_time_name, isreplay, itme, position,isUserReplay  );
		
		if( user.id == itme.uid ){
			view.setOnLongClickListener(new OnLongClickListener() {
	
				@Override
				public boolean onLongClick(View v) {
					final YMDialog ym = new YMDialog(activity);
					ym.setHintMsg("确定删除喊一喊?").setConfigButton(new OnClickListener() {
	
						@Override
						public void onClick(View v) {
							ym.dismiss();
							
							RequestParams params = new RequestParams();
							params.put("id", itme.hyhid);
							GezitechAlertDialog.loadDialog( activity );
							ShoutManager.getInstance().deleteshout(params, new OnAsynInsertListener() {
								
								@Override
								public void OnAsynRequestFail(String errorCode, String errorMsg) {
									new ToastMakeText(activity).Toast( errorMsg );
									GezitechAlertDialog.closeDialog();
								}
								
								@Override
								public void onInsertDone(String id) {
									GezitechAlertDialog.closeDialog();
									remove(position);
									try{
										chatDb.delete("hyhid="+itme.hyhid+" and myuid="+user.id );
										chatContentDB.delete("hyhid="+itme.hyhid+" and myuid="+user.id );
									}catch(Exception ex){
										
									}
								}
							});
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
		}
		//展开/关闭
		view.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if( itme.isunfold == 1 ){
					hv.ll_chat_person.setVisibility( View.GONE );
					itme.isunfold = 0;
				}else{
					hv.ll_chat_person.setVisibility( View.VISIBLE );
					itme.isunfold = 1;
				}
				//更新数据
				setItem(itme, position, false);
			}
		});
		
		return view;
	}
	private void hyhAction(TextView tv_diff_time,TextView tv_beizhu,TextView myrelease_NUMBER,
			RemoteImageView iv_pic, RelativeLayout rl_yuyin_bg ,final ImageView iv_voice_sign, 
			TextView tv_reply, LinearLayout ll_chat_person,
			LinearLayout myrelease_bg, View v_line1, View v_line2,TextView tv_diff_time_name,
			final boolean isReplay, ChatContent itme,  final int position,final boolean isUserReplay ) {
		//{"range":500,"id":45,"long":"106.4703","ctime":1415692183,"uid":"37","typeid":"1","caption":"12","activetime":"1","maxReplycount":"12","app":"api","lat":"29.56274","litpic":"","oauth_token":"","speechtime":"2048","speech":""}
		try {
			Response response = new Response( new String( itme.body ) );
			JSONObject root = response.asJSONObject();
			if( position != -1 ){
				tv_beizhu.setText(  root.has("caption") ? root.getString("caption") : "" );
				myrelease_NUMBER.setText( root.has("speechtime") ? (int)(root.getLong("speechtime")/1000)+"''" : 0 +"''" );
				final String litpic = root.has("litpic") ? root.getString("litpic") : "";
				if( !litpic.equals("") ){//这个是定时调用 如果是-1则 不用掉用下面的数据
					iv_pic.setVisibility( View.VISIBLE );
					iv_pic.setImageUrl(  litpic );
					iv_pic.setOnClickListener( new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							//画廊展示图片
							final String[] images = new String[1];
							String[] pic = litpic.split("src=");
							images[0] = StringUtil.stringDecode( pic[1] );
							ImageShow.jumpDisplayPic(images, 0, activity);
						}
					});
				}else{
					iv_pic.setVisibility( View.GONE );
				}	
				final String speech = root.has("speech") ? root.getString("speech") : "";
				// 播放语音
				rl_yuyin_bg.setOnClickListener(new OnClickListener() {
	
					@Override
					public void onClick(View v) {
						if( speech.equals("") ) return;
						IOUtil.downloadAndCacheFile(speech, false,
								new CacheCompleteListener() {
									@Override
									public void onCacheComplete(String fileName,
											boolean success, String msg, Object tag) {
										currTimeposition = position+1;
										if( lastTimeposition != -1 ){//停止之前的动画播音效果
											
											refreshAnim( lastTimeposition );
										}
										playAudio(fileName, iv_voice_sign );
									}
								});
					}
				});
			}
			//限时回复
			final long ctime = root.has("ctime") ? root.getLong("ctime") : 0;
			final long activetime = root.has("activetime") ? root.getLong("activetime") : 0;		
			final long curr = System.currentTimeMillis();
			final long startTime = activetime * 1000 + ctime * 1000;
			if (startTime > curr ) {
				long dtime = startTime - curr;
				tv_diff_time_name.setText( (user.id != itme.uid && isReplay)  || ( isUserReplay  ) ? "": "剩余时间：" );
				tv_diff_time.setText( (user.id != itme.uid && isReplay ) || ( isUserReplay  )  ? "" : getDateStr(dtime / 1000) );
				tv_diff_time.setTextColor(Color.parseColor("#ff340c"));
				itme.isunfold = 1;
				
				ll_chat_person.setVisibility( View.VISIBLE );
				
				if( user.id == itme.uid  ){
					tv_reply.setText( itme.chatUser.size()+"人回复" );
					
				}else{
					if( isReplay  ){
						tv_reply.setText( "已回复" );
						tv_reply.setBackgroundResource( android.R.color.transparent );
					}else{
						tv_reply.setBackgroundResource(R.drawable.button_common_btn_blue);
						tv_reply.setText( "立即回复" );
						tv_reply.setTextColor( activity.getResources().getColor( R.color.white ) );
					}
				}
				myrelease_bg.setBackgroundColor( Color.parseColor("#d9efff") );
				v_line1.setBackgroundColor( Color.parseColor("#c1e1fa") );
				v_line2.setBackgroundColor( Color.parseColor("#c1e1fa") );
			} else {
				itme.isunfold = 0;
				tv_diff_time_name.setText( ( user.id != itme.uid && isReplay ) || ( isUserReplay  )  ? "": "剩余时间：" );
				tv_diff_time.setText( ( user.id != itme.uid && isReplay ) || ( isUserReplay  ) ? "": "已过期" );
				tv_diff_time.setTextColor(Color.parseColor("#949494"));
				tv_reply.setText( itme.chatUser.size()+"人回复" );
				tv_reply.setBackgroundResource( android.R.color.transparent );
				tv_reply.setOnClickListener( null );
				ll_chat_person.setVisibility( View.GONE );

				myrelease_bg.setBackgroundColor( Color.parseColor("#e5e5e5") );
				v_line1.setBackgroundColor( Color.parseColor("#ececec") );
				v_line2.setBackgroundColor( Color.parseColor("#ececec") );
				
			}
			//更新数据
			this.setItem(itme, position, false);
		} catch (JSONException e) {
			
		} catch (GezitechException e) {
			
		}
	}
	private String getDateStr(long time) {
		long h = time / 3600;
		long m = (time % 3600) / 60;
		long s = (time % 3600) % 60;
		return (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":"
				+ (s < 10 ? "0" + s : s);
	}
	public void refreshAnim( int position ){
		int firstVisibleItemIndex = listView.getFirstVisiblePosition();
		//Log.v("position", position+"position===="+ listView.getChildCount()+"==="+firstVisibleItemIndex);
		int ii = position-firstVisibleItemIndex;
		for (int i = 0; i < listView.getChildCount(); i++) {    
			if( i != ii ) continue;
			View v = listView.getChildAt( i );
			if( v == null ) continue;
			try{
				ImageView iv_my_voice_sign = (ImageView) v.findViewById(R.id.iv_voice_sign);
				
				if( iv_my_voice_sign != null ){
					iv_my_voice_sign.setImageResource(R.drawable.right_dhck_voice_blue_03);
					 AnimationDrawable animPlay = (AnimationDrawable) iv_my_voice_sign.getDrawable();
	                 if (animPlay!= null && !animPlay.isRunning()) {
	                     animPlay.stop();
	                 }
				}
			  }catch(Exception e){
			    	
			  }
		}
	}
	/**
	 * 播放音频文件
	 * 
	 * @param audioPath
	 * @param iv_voice_sign
	 */
	private void playAudio(String audioPath,final ImageView iv_voice_sign ) {
		if (audioPath.equals(currentAudioPath)) {
			stopAudio();
		} else {
			try {
				if (mediaPlayer != null) {
					if (mediaPlayer.isPlaying()) {
						mediaPlayer.stop();
					}
					mediaPlayer.release();
					mediaPlayer = null;
				}
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setDataSource(audioPath);
				mediaPlayer.prepare();
				mediaPlayer.start();
				currentAudioPath = audioPath;
				iv_voice_sign.setImageResource( R.drawable.right_progress_voice_blue );
				 AnimationDrawable animPlay = (AnimationDrawable) iv_voice_sign.getDrawable();
                 if (!animPlay.isRunning()) {
                     animPlay.start();
                 }
				mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mp) {
								stopAudio();
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
			}
			lastTimeposition = currTimeposition;
		}
	}
	/**
	 * 停止播放音频
	 */
	public void stopAudio() {
		currentAudioPath = null;
		try {
			if (mediaPlayer != null) {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
				}
				mediaPlayer.release();
				mediaPlayer = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		refreshAnim( currTimeposition );
	}
	//缓存
	public class Hv{
		
		private TextView tv_receive_time;
		private TextView tv_diff_time;
		private RelativeLayout myrelease_yuyin;
		private TextView myrelease_NUMBER;
		private RelativeLayout rl_yuyin_bg;
		private ImageView iv_voice_sign;
		private RemoteImageView iv_pic;
		private TextView tv_beizhu;
		private TextView tv_reply;
		private LinearLayout ll_chat_person;
		private View v_line1;
		private View v_line2;
		private LinearLayout myrelease_bg;
		private TextView tv_diff_time_name;

		public Hv( View view){
			tv_receive_time = (TextView) view.findViewById( R.id.tv_receive_time );
			tv_diff_time = (TextView) view.findViewById( R.id.tv_diff_time );
			myrelease_yuyin = (RelativeLayout)view.findViewById( R.id.myrelease_yuyin );
			myrelease_NUMBER = (TextView) view.findViewById( R.id.myrelease_NUMBER );
			rl_yuyin_bg = (RelativeLayout) view.findViewById( R.id.rl_yuyin_bg );
			iv_voice_sign = (ImageView)view.findViewById( R.id.iv_voice_sign );
			iv_pic = (RemoteImageView) view.findViewById( R.id.iv_pic );
			tv_beizhu = (TextView) view.findViewById( R.id.tv_beizhu );
			tv_reply = (TextView) view.findViewById( R.id.tv_reply );
			ll_chat_person = (LinearLayout) view.findViewById( R.id.ll_chat_person );
			v_line1 = (View) view.findViewById( R.id.v_line1 );
			v_line2 = (View) view.findViewById( R.id.v_line2 );
			tv_diff_time_name = (TextView) view.findViewById( R.id.tv_diff_time_name );
			myrelease_bg = (LinearLayout) view.findViewById( R.id.myrelease_bg );
		}
		
	}
	//定时刷新
	public void refreshcountdown() {

		int firstVisibleItemIndex = listView.getFirstVisiblePosition();

		for (int i = 0; i < listView.getChildCount(); i++) {    
		    try{
		    	View v = listView.getChildAt( i );
		    	ChatContent item = (ChatContent)this .getItem( firstVisibleItemIndex + i -1 );
			    Hv hv = new Hv( v ); 
				Response response = new Response( new String( item.body ) );
				JSONObject root = response.asJSONObject();
			  //限时回复
				final long ctime = root.has("ctime") ? root.getLong("ctime") : 0;
				final long activetime = root.has("activetime") ? root.getLong("activetime") : 0;		
				final long curr = System.currentTimeMillis();
				final long startTime = activetime * 1000 + ctime * 1000;
				boolean isreplay = false; //是否回复
				//是否有人回复
				boolean isUserReplay = false;
				if( item.chatUser.size()> 0 ){
					if( user.isbusiness > 0 ) isreplay = true;
					isUserReplay = true;
				}
				if (startTime > curr) {
					long dtime = startTime - curr;
					//不是当前的用户  则已经回复  则不显示剩余时间
					hv.tv_diff_time_name.setText( ( user.id != item.uid && isreplay ) || isUserReplay  ? "": "剩余时间：" );
					hv.tv_diff_time.setText( ( user.id != item.uid && isreplay ) || isUserReplay ? "" : getDateStr(dtime / 1000) );
					hv.tv_diff_time.setTextColor(Color.parseColor("#ff340c"));
					//item.isunfold = 1;
					
					//hv.ll_chat_person.setVisibility( View.VISIBLE );
					
					if( user.id == item.uid  ){
						hv.tv_reply.setText( item.chatUser.size()+"人回复" );
						
					}else{
						if( isreplay ){
							hv.tv_reply.setText( "已回复" );
							hv.tv_reply.setBackgroundResource( android.R.color.transparent );
						}else{
							hv.tv_reply.setBackgroundResource(R.drawable.button_common_btn_blue);
							hv.tv_reply.setText( "立即回复" );
							hv.tv_reply.setTextColor( activity.getResources().getColor( R.color.white ) );
						}
					}
					hv.myrelease_bg.setBackgroundColor( Color.parseColor("#d9efff") );
					hv.v_line1.setBackgroundColor( Color.parseColor("#c1e1fa") );
					hv.v_line2.setBackgroundColor( Color.parseColor("#c1e1fa") );
				} else {
					//item.isunfold = 0;
					hv.tv_diff_time_name.setText( ( user.id != item.uid && isreplay ) || isUserReplay  ? "": "剩余时间：" );
					hv.tv_diff_time.setText( ( user.id != item.uid && isreplay ) || isUserReplay  ? "": "已过期" );
					
					hv.tv_diff_time.setTextColor(Color.parseColor("#949494"));
					hv.tv_reply.setText( item.chatUser.size()+"人回复" );
					hv.tv_reply.setBackgroundResource( android.R.color.transparent );
					hv.tv_reply.setOnClickListener( null );
					//hv.ll_chat_person.setVisibility( View.GONE );

					hv.myrelease_bg.setBackgroundColor( Color.parseColor("#e5e5e5") );
					hv.v_line1.setBackgroundColor( Color.parseColor("#ececec") );
					hv.v_line2.setBackgroundColor( Color.parseColor("#ececec") );
					
				}
				//更新数据
				this.setItem(item, firstVisibleItemIndex + i -1, false);
				
		    }catch(Exception e){
		    	
		    }
		}
		
	}
}
