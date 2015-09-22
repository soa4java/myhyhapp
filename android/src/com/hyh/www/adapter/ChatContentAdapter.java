package com.hyh.www.adapter;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechException;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.entity.User;
import com.gezitech.http.Response;
import com.gezitech.image.ImageDetailActivity;
import com.gezitech.service.GezitechService;
import com.gezitech.service.managers.ShoutManager;
import com.gezitech.service.managers.UserManager;
import com.gezitech.util.DateUtil;
import com.gezitech.util.DateUtils;
import com.gezitech.util.DimensionUtility;
import com.gezitech.util.IOUtil;
import com.gezitech.util.StringUtil;
import com.gezitech.util.IOUtil.CacheCompleteListener;
import com.gezitech.widget.MyListView;
import com.gezitech.widget.RemoteImageView;
import com.hyh.www.R;
import com.hyh.www.ZhuyeActivity;
import com.hyh.www.chat.BillDetailActivity_bak;
import com.hyh.www.chat.ChatUtils;
import com.hyh.www.chat.MapActivity;
import com.hyh.www.chat.OrderDetailActivity;
import com.hyh.www.entity.Bill;
import com.hyh.www.entity.Chat;
import com.hyh.www.entity.ChatContent;
import com.hyh.www.entity.Emotion;
import com.hyh.www.entity.Friend;
import com.hyh.www.entity.Shout;
import com.hyh.www.user.PersonDetailedInformationActivity;
import com.hyh.www.widget.ActivityCommon;
import com.hyh.www.widget.ImageShow;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 
 * @author xiaobai
 * 2014-10-15
 * @todo(  聊天界面的适配器   )
 */
public class ChatContentAdapter extends BasicAdapter{

	private User user;
	private String head;
	private GezitechActivity activity;
	private int lastTimeposition = -1;
	private int currTimeposition = -1;
	private MyListView listView;
	private String currentAudioPath;
	public MediaPlayer mediaPlayer;
	private long fid;
	public ChatContentAdapter(GezitechActivity activity, MyListView listView, String head,long fid ){
		super();
		user = GezitechService.getInstance().getCurrentUser();
		this.head  = head;
		this.activity = activity;
		this.listView = listView;
		this.fid = fid;
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
		final ChatContent item = (ChatContent) getItem( position );
		final Hv  hv;
		//if( view == null ){
			view =  inflater.inflate( R.layout.list_chat_content , null);
			hv = new Hv( view );
		//	view.setTag( hv );
		//}else{
		//	hv = (Hv)view.getTag();
		//}
		//时间的显示与否
		boolean isShow = false;
		if( position > 0  ){
			ChatContent timeItem = (ChatContent) getItem( position-1 );
			if( ( item.ctime - timeItem.ctime ) > 5*60*1000 )  isShow = true;
			else isShow = false;
		}else{
			long time = System.currentTimeMillis();
			if( time - item.ctime > 5*60*1000 ) isShow = true;
			else isShow = false;
		}
		if( isShow ){
			hv.tv_time.setVisibility( View.VISIBLE );
			hv.tv_time.setText( DateUtil.getShortTime( item.ctime ) );
		}else{
			hv.tv_time.setVisibility( View.GONE );
		}
		hv.ll_my_box.setVisibility( View.GONE );
		hv.ll_f_box.setVisibility( View.GONE );
		hv.rl_bill.setVisibility( View.GONE );
		hv.rl_pay_bill.setVisibility( View.GONE );
		hv.rl_c_pay.setVisibility( View.GONE );
		hv.rl_hyh.setVisibility( View.GONE );
		if( item.uid == user.id ){//自己发送
			hv.ll_my_box.setVisibility( View.VISIBLE );
			Picasso.with(context)
            .load( user.head )
            .error(R.drawable.common_default_photo )
            .into( hv.iv_my_head  );
			
			
			hv.iv_my_head.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					ActivityCommon.lookFriendData(item.uid, activity );
				}
			});
			if( item.type == 0  ){//文本
				SpannableString bodyStr = Emotion.getInstace().getExpressionString( activity, item.body );
				hv.tv_my_text.setText( bodyStr );
				hv.tv_my_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				hv.tv_my_text.setVisibility( View.VISIBLE );
			}else if( item.type == 1 ){//图片
				hv.ll_my_pic.setVisibility( View.VISIBLE );
				int defaultW = 110;
				int defaultH = 110;
				try{
					Pattern pattern = Pattern.compile("^.*h=([0-9]*)&w=([0-9]*)&.*$");
					Matcher matcher = pattern.matcher( item.body );
				
					 while (matcher.find()){
						 defaultW = Integer.parseInt(matcher.group(2));
						 defaultH = Integer.parseInt(matcher.group(1));
					 }
				}catch(Exception ex){}
				android.view.ViewGroup.LayoutParams para = hv.iv_my_pic.getLayoutParams();
				para.width =  defaultW < 110 ? 110 : defaultW;
				para.height = defaultH < 110 ? 110 : defaultH;
				hv.iv_my_pic.setLayoutParams( para );
				
				Picasso.with(context)
	            .load( item.body )
	            .error(R.drawable.common_default_photo )
	            .into( hv.iv_my_pic  );
				//点击图片
				hv.iv_my_pic.setOnClickListener( new  OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//画廊展示图片
						final String[] images = new String[1];
						String[] pic = item.body.split("src=");
						images[0] = StringUtil.stringDecode( pic[1] );
						ImageShow.jumpDisplayPic(images, 0, activity);
					}
				});
			}else if( item.type == 2 ){//语音
				voiceAction(false,hv.rl_my_yuyin, hv.ll_my_yuyin,hv.tv_my_yuyin_len,hv.iv_my_voice_sign,position,item.body );
			}else if( item.type == 3 ){//地图
				mapAction(hv.ll_my_map,hv.iv_my_map,hv.tv_my_address, item.body );
			}else if( item.type == 4 ){//名片
				cardAction(hv.ll_my_card,hv.iv_my_card_head ,hv.tv_my_name_val, hv.tv_my_phone_val,hv.tv_my_address_val, item.body, user.id);
			}else if( item.type == 5 ){//创建账单
				hv.rl_bill.setVisibility( View.VISIBLE );
				hv.rl_bill.setLayoutParams(  _params(165,30,65,15,RelativeLayout.ALIGN_PARENT_RIGHT) );
				hv.rl_bill.setBackgroundResource( R.drawable.dhck_dialogbox_blue2_select );
				cbillAction(hv.rl_bill, hv.tv_bill_code_val, hv.tv_bill_notes,hv.tv_bill_money_val, hv.tv_bill_time_val, item.body );
			}else if( item.type == 6 ){//账单支付
				hv.rl_pay_bill.setVisibility( View.VISIBLE );
				hv.rl_pay_bill.setLayoutParams(  _params(180,30,65,15,RelativeLayout.ALIGN_PARENT_RIGHT) );
				hv.rl_pay_bill.setBackgroundResource( R.drawable.dhck_dialogbox_blue2_select );
				billpaycomplete(hv.rl_pay_bill,hv.tv_pay_tradecode, item.body);
			}else if( item.type == 8 ){//创建付款
				hv.rl_c_pay.setVisibility( View.VISIBLE );
				hv.rl_c_pay.setLayoutParams(  _params(180,30,65,15,RelativeLayout.ALIGN_PARENT_RIGHT) );
				hv.rl_c_pay.setBackgroundResource( R.drawable.dhck_dialogbox_blue2_select );
				hv.tv_c_pay.setVisibility( View.GONE );
				createPay(hv.rl_c_pay,hv.tv_c_txt,hv.tv_c_pay,hv.tv_c_code_val, hv.tv_c_notes,hv.tv_c_money_val, hv.tv_c_time_val, item.body );
			}else if( item.type == 7 || item.type == 11  || item.type == 12 || item.type == 13 || item.type == 14 ){//确认收款
				hv.rl_pay_bill.setVisibility( View.VISIBLE );
				hv.rl_pay_bill.setLayoutParams(  _params(180,30,65,15,RelativeLayout.ALIGN_PARENT_RIGHT) );
				hv.rl_pay_bill.setBackgroundResource( R.drawable.dhck_dialogbox_blue2_select );
				receiptcomplete(hv.rl_pay_bill,hv.tv_pay_head, hv.tv_pay_tradecode, item.body, item.type );
			}
		
		}else{//对方发送
			hv.ll_f_box.setVisibility( View.VISIBLE );
			hv.iv_sys_head.setVisibility( View.GONE );
			Picasso.with(context)
	        .load( head )
	        .error(R.drawable.common_default_photo )
	        .into( hv.iv_f_head );
			hv.iv_f_head.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					ActivityCommon.lookFriendData(item.uid, activity );
				}
			});
			if( item.type == 0  ){//文本
				SpannableString bodyStr = Emotion.getInstace().getExpressionString( activity, item.body );
				hv.tv_f_text.setText( bodyStr );
				hv.tv_f_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				hv.tv_f_text.setVisibility( View.VISIBLE );
			}else if( item.type == 1 ){//图片
				hv.ll_f_pic.setVisibility( View.VISIBLE );
				int defaultW = 110;
				int defaultH = 110;
				try{
					Pattern pattern = Pattern.compile("^.*h=([0-9]*)&w=([0-9]*)&.*$");
					Matcher matcher = pattern.matcher( item.body );
				
					 while (matcher.find()){
						 defaultW = Integer.parseInt(matcher.group(2));
						 defaultH = Integer.parseInt(matcher.group(1));
					 }
				}catch(Exception ex){}
				android.view.ViewGroup.LayoutParams para = hv.iv_f_pic.getLayoutParams();
				para.width = defaultW < 110 ? 110 : defaultW;
				para.height = defaultH < 110 ? 110 : defaultH ;
				hv.iv_f_pic.setLayoutParams( para );
				
				Picasso.with(context)
		        .load( item.body  )
		        .error(R.drawable.common_default_photo )
		        .into( hv.iv_f_pic  );
				
				hv.iv_f_pic.setOnClickListener( new  OnClickListener() {				
					@Override
					public void onClick(View v) {
						//画廊展示图片
						final String[] images = new String[1];
						String[] pic = item.body.split("src=");
						images[0] = StringUtil.stringDecode( pic[1] );
						ImageShow.jumpDisplayPic(images, 0, activity);
					}
				});
			}else if( item.type == 2 ){//语音
				voiceAction(true,hv.rl_f_yuyin, hv.ll_f_yuyin,hv.tv_f_yuyin_len,hv.iv_f_voice_sign,position,item.body );
			}else if( item.type == 3 ){//地图
				mapAction(hv.ll_f_map,hv.iv_f_map,hv.tv_f_address, item.body );
			}else if( item.type == 4 ){//名片
				cardAction(hv.ll_f_card,hv.iv_f_card_head ,hv.tv_f_name_val, hv.tv_f_phone_val,hv.tv_f_address_val, item.body, fid);
			}else if( item.type == 5 ){//创建账单
				hv.rl_bill.setVisibility( View.VISIBLE );
				hv.rl_bill.setLayoutParams(  _params(165,65,30,15,RelativeLayout.ALIGN_PARENT_LEFT) );
				hv.rl_bill.setBackgroundResource( R.drawable.dhck_dialogbox_white2_select );
				cbillAction( hv.rl_bill, hv.tv_bill_code_val, hv.tv_bill_notes,hv.tv_bill_money_val, hv.tv_bill_time_val, item.body );
			}else if( item.type == 6 ){//账单支付
				hv.rl_pay_bill.setVisibility( View.VISIBLE );
				hv.rl_pay_bill.setLayoutParams(  _params(180,65,30,15,RelativeLayout.ALIGN_PARENT_LEFT) );
				hv.rl_pay_bill.setBackgroundResource( R.drawable.dhck_dialogbox_white2_select );
				billpaycomplete(hv.rl_pay_bill,hv.tv_pay_tradecode, item.body);
			}else if( item.type == 8 ){//创建付款
				hv.rl_c_pay.setVisibility( View.VISIBLE );
				hv.rl_c_pay.setLayoutParams(  _params(180,65,30,15,RelativeLayout.ALIGN_PARENT_LEFT)  );
				hv.rl_c_pay.setBackgroundResource( R.drawable.dhck_dialogbox_white2_select );
				hv.tv_c_pay.setVisibility( View.VISIBLE );
				createPay(hv.rl_c_pay,hv.tv_c_txt,hv.tv_c_pay,hv.tv_c_code_val, hv.tv_c_notes,hv.tv_c_money_val, hv.tv_c_time_val, item.body );
			}else if( item.type == 7  || item.type == 11  || item.type == 12 || item.type == 13 || item.type == 14 ){//确认收款
				hv.rl_pay_bill.setVisibility( View.VISIBLE );
				hv.rl_pay_bill.setLayoutParams(  _params(180,65,30,15,RelativeLayout.ALIGN_PARENT_LEFT)  );
				hv.rl_pay_bill.setBackgroundResource( R.drawable.dhck_dialogbox_white2_select );
				receiptcomplete(hv.rl_pay_bill,hv.tv_pay_head, hv.tv_pay_tradecode, item.body, item.type );
			}else if( item.type == 9 ){//推送的喊一喊数据
				hv.rl_hyh.setVisibility( View.VISIBLE );				
				hyhAction(true, hv.rl_hyh_yuyin, hv.rl_hyh, hv.tv_hyh_notes,hv.tv_hyh_yuyin_len,hv.iv_hyh_voice_sign, hv.iv_hyh_pic ,hv.tv_hyh_time_val, item.body,position );
			}else if( item.type == 10 ){//喊一喊取消不能与客户对话的消息
				SpannableString bodyStr = Emotion.getInstace().getExpressionString( activity, item.body );
				hv.tv_f_text.setText( bodyStr );
				hv.tv_f_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				hv.tv_f_text.setVisibility( View.VISIBLE );
				hv.iv_sys_head.setVisibility( View.VISIBLE );
			}
			
		}
		return view;
	}
	//喊一喊
	private void hyhAction(final boolean isFriend,RelativeLayout rl_hyh_yuyin ,RelativeLayout rl_hyh, TextView tv_hyh_notes,
			TextView tv_hyh_yuyin_len, final ImageView iv_hyh_voice_sign,
			ImageView iv_hyh_pic, TextView tv_hyh_time_val, String body) {
		hyhAction(true,rl_hyh_yuyin, rl_hyh, tv_hyh_notes,tv_hyh_yuyin_len,iv_hyh_voice_sign, iv_hyh_pic ,tv_hyh_time_val, body, -1 );
	}
	private void hyhAction(final boolean isFriend,RelativeLayout rl_hyh_yuyin, RelativeLayout rl_hyh, TextView tv_hyh_notes,
			TextView tv_hyh_yuyin_len, final ImageView iv_hyh_voice_sign,
			ImageView iv_hyh_pic, TextView tv_hyh_time_val, String body,final int position ) {
		//{"range":500,"id":45,"long":"106.4703","ctime":1415692183,"uid":"37","typeid":"1","caption":"12","activetime":"1","maxReplycount":"12","app":"api","lat":"29.56274","litpic":"","oauth_token":"","speechtime":"2048","speech":""}
		try {
			Response response = new Response( new String( body ) );
			JSONObject root = response.asJSONObject();
			if( position != -1 ){
				tv_hyh_notes.setText(  root.has("caption") ? root.getString("caption") : "" );
				tv_hyh_yuyin_len.setText( root.has("speechtime") ? (int)(root.getLong("speechtime")/1000)+"''" : 0 +"''" );
				final String litpic = root.has("litpic") ? root.getString("litpic") : "";
				if( !litpic.equals("") ){//这个是定时调用 如果是-1则 不用掉用下面的数据
					iv_hyh_pic.setVisibility( View.VISIBLE );
					
					Picasso.with(context)
			        .load( litpic )
			        .error(R.drawable.common_default_photo )
			        .into( iv_hyh_pic  );
					
					iv_hyh_pic.setOnClickListener( new OnClickListener() {
						
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
					iv_hyh_pic.setVisibility( View.GONE );
				}	
				final String speech = root.has("speech") ? root.getString("speech") : "";
				// 播放语音
				rl_hyh_yuyin.setOnClickListener(new OnClickListener() {
	
					@Override
					public void onClick(View v) {
						IOUtil.downloadAndCacheFile(speech, false,
								new CacheCompleteListener() {
									@Override
									public void onCacheComplete(String fileName,
											boolean success, String msg, Object tag) {
										currTimeposition = position+1;
										if( lastTimeposition != -1 ){//停止之前的动画播音效果
											
											refreshAnim( lastTimeposition );
										}
										playAudio(fileName, iv_hyh_voice_sign, isFriend );
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
			if (startTime > curr) {
				long dtime = startTime - curr;
				tv_hyh_time_val.setText(getDateStr(dtime / 1000));
				tv_hyh_time_val.setTextColor(Color.parseColor("#ff340c"));
			} else {
				tv_hyh_time_val.setText("已过期");
				tv_hyh_time_val.setTextColor(Color.parseColor("#949494"));
			}
			/*final long id =  root.has("id") ? root.getLong( "id" ) : 0;
			rl_hyh.setOnClickListener(  new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if( id<= 0 ) return;
					
				}
			});*/
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GezitechException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	//确认收款
	private void receiptcomplete(LinearLayout rl_pay_bill,
			TextView tv_pay_head, TextView tv_pay_tradecode, String body, int type ) {
		try {
			Response response = new Response( new String( body ) );
			JSONObject root = response.asJSONObject();
			tv_pay_head.setText( ChatUtils.getOrderStr( type ) );//描述
			tv_pay_tradecode.setText( root.has("tradecode") ? root.getString("tradecode") : "" );
			final long id =  root.has("id") ? root.getLong( "id" ) : 0;
			rl_pay_bill.setOnClickListener(  new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if( id<= 0 ) return;
					loadBillDetail( id );
				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GezitechException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	//创建付款
	private void createPay(RelativeLayout rl_c_pay, TextView tv_c_txt,
			TextView tv_c_pay, TextView tv_bill_code_val,
			TextView tv_bill_notes, TextView tv_bill_money_val,
			TextView tv_bill_time_val, String body) {
		
		try {
			Response response = new Response( new String( body ) );
			JSONObject root = response.asJSONObject();
			tv_bill_code_val.setText( root.has("tradecode") ? root.getString("tradecode") : "" );
			tv_bill_notes.setText( root.has("notes") ? root.getString("notes") : "" );
			tv_bill_money_val.setText(  root.has("money") ? root.getDouble("money")+"" : 0.00+""  );
			
			long activetime = root.has("activechecktime") ? root.getLong( "activechecktime" ) : 0;
			long ctime =  root.has("paytime") ? root.getLong( "paytime" ) : 0;
			final long curr = System.currentTimeMillis();
			final long startTime = activetime * 1000 + ctime * 1000;
			if (startTime > curr) {
				long dtime = startTime - curr;
				tv_bill_time_val.setText(getDateStr(dtime / 1000));
				tv_bill_time_val.setTextColor(Color.parseColor("#ff340c"));
			} else {
				tv_bill_time_val.setText("已过期");
				tv_bill_time_val.setTextColor(Color.parseColor("#949494"));
			}
			
			
			final long id =  root.has("id") ? root.getLong( "id" ) : 0;
			rl_c_pay.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if( id<= 0 ) return;
					loadBillDetail( id );
				}
			});
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GezitechException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	//账单支付完成
	private void billpaycomplete(LinearLayout rl_pay_bill,
			TextView tv_pay_tradecode, String body) {
		try {
			Response response = new Response( new String( body ) );
			JSONObject root = response.asJSONObject();
			tv_pay_tradecode.setText( root.has("tradecode") ? root.getString("tradecode") : "" );
			final long id =  root.has("id") ? root.getLong( "id" ) : 0;
			rl_pay_bill.setOnClickListener(  new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if( id<= 0 ) return;
					//loadBillDetail( id, 6, 0 );
				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GezitechException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//创建账单
	private void cbillAction(RelativeLayout rl_bill ,TextView tv_bill_code_val, TextView tv_bill_notes,
			TextView tv_bill_money_val, TextView tv_bill_time_val, String body) {
		
		try {
			Response response = new Response( new String( body ) );
			JSONObject root = response.asJSONObject();
			tv_bill_code_val.setText( root.has("tradecode") ? root.getString("tradecode") : "" );
			tv_bill_notes.setText( root.has("notes") ? root.getString("notes") : "" );
			tv_bill_money_val.setText(  root.has("money") ? root.getDouble("money")+"" : 0.00+""  );
			long activetime = root.has("activetime") ? root.getLong( "activetime" ) : 0;
			long ctime =  root.has("ctime") ? root.getLong( "ctime" ) : 0;
			final long curr = System.currentTimeMillis();
			final long startTime = activetime * 1000 + ctime * 1000;
			final long id =  root.has("id") ? root.getLong( "id" ) : 0;
			if (startTime > curr) {
				long dtime = startTime - curr;
				tv_bill_time_val.setText(getDateStr(dtime / 1000));
				tv_bill_time_val.setTextColor(Color.parseColor("#ff340c"));
			} else {
				tv_bill_time_val.setText("已过期");
				tv_bill_time_val.setTextColor(Color.parseColor("#949494"));
			}
			rl_bill.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if( id<= 0 ) return;
					//loadBillDetail( id, 5, 0   );
				}
			});
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GezitechException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void cardAction(LinearLayout ll_f_card,
			ImageView iv_f_card_head, TextView tv_f_name_val,
			TextView tv_f_phone_val, TextView tv_f_address_val, String body,final long uid) {
		ll_f_card.setVisibility( View.VISIBLE );
		try {
			Response response = new Response( new String( body ) );
			JSONObject root = response.asJSONObject();
			
			Picasso.with(context)
	        .load( root.has("head") ? root.getString("head") : "" )
	        .error(R.drawable.common_default_photo )
	        .into( iv_f_card_head  );
			
			tv_f_name_val.setText( root.has("nickname") ? root.getString("nickname") : "未填写"  );
			
			tv_f_phone_val.setText( root.has("phone") ? root.getString("phone") : "未填写"  );
			
			tv_f_address_val.setText( root.has("address") ? root.getString("address"): "未填写"  );
			
			ll_f_card.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					LookFriendData( uid );
				}
			});
			
		} catch (GezitechException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void mapAction(RelativeLayout ll_f_map, ImageView iv_f_map,
			TextView tv_f_address, String body) {
		ll_f_map.setVisibility( View.VISIBLE );
		final ChatContent chatContent = parseData( body );
		Picasso.with(context)
        .load( chatContent.locationPic )
        .error(R.drawable.common_default_photo )
        .into( iv_f_map  );
		tv_f_address.setText( chatContent.locationAddress );
		iv_f_map.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent =  new Intent(activity, MapActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("map", chatContent );
				intent.putExtras( bundle );
				activity.startActivity( intent );
				activity.overridePendingTransition(R.anim.out_to_down, R.anim.exit_anim);
			}
		});
	}

	//语音
	private void voiceAction(final boolean isFriend, RelativeLayout rl_my_yuyin,  LinearLayout ll_yuyin, TextView tv_yuyin_len,final ImageView iv_voice_sign, final int position ,String body ){
		ll_yuyin.setVisibility( View.VISIBLE );
		final ChatContent chatContent = parseData( body );
		tv_yuyin_len.setText( (int)(chatContent.audiolength/1000)+"''" );
		// 播放语音
		rl_my_yuyin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				IOUtil.downloadAndCacheFile(chatContent.audiourl, false,
						new CacheCompleteListener() {
							@Override
							public void onCacheComplete(String fileName,
									boolean success, String msg, Object tag) {
								currTimeposition = position+1;
								if( lastTimeposition != -1 ){//停止之前的动画播音效果
									
									refreshAnim( lastTimeposition );
								}
								playAudio(fileName, iv_voice_sign, isFriend );
							}
						});
			}
		});
	}
	//缓存
	public class Hv{
		private TextView tv_time;
		private RelativeLayout ll_my_box;
		private ImageView iv_my_head;
		private TextView tv_my_text;
		private LinearLayout ll_my_yuyin;
		private TextView tv_my_yuyin_len;
		private ImageView iv_my_voice_sign;
		private LinearLayout ll_my_pic;
		private ImageView iv_my_pic;
		private LinearLayout ll_my_card;
		private ImageView iv_my_card_head;
		private TextView tv_my_name_val;
		private TextView tv_my_phone_val;
		private TextView tv_my_address_val;
		private RelativeLayout ll_f_box;
		private ImageView iv_f_head;
		private TextView tv_f_text;
		private LinearLayout ll_f_yuyin;
		private TextView tv_f_yuyin_len;
		private ImageView iv_f_voice_sign;
		private LinearLayout ll_f_pic;
		private ImageView iv_f_pic;
		private LinearLayout ll_f_card;
		private ImageView iv_f_card_head;
		private TextView tv_f_name_val;
		private TextView tv_f_phone_val;
		private TextView tv_f_address_val;
		private RelativeLayout ll_f_map;
		private ImageView iv_f_map;
		private TextView tv_f_address;
		private RelativeLayout ll_my_map;
		private ImageView iv_my_map;
		private TextView tv_my_address;
		private RelativeLayout rl_bill;
		private TextView tv_bill_code_val;
		private TextView tv_bill_notes;
		private TextView tv_bill_money_val;
		private TextView tv_bill_time_val;
		private RelativeLayout rl_bill_padding;
		private LinearLayout rl_pay_bill;
		private TextView tv_pay_tradecode;
		private RelativeLayout rl_c_pay;
		private TextView tv_c_txt;
		private TextView tv_c_pay;
		private TextView tv_c_code_val;
		private TextView tv_c_notes;
		private TextView tv_c_money_val;
		private TextView tv_c_time_val;
		private TextView tv_pay_head;
		private RelativeLayout rl_hyh;
		private TextView tv_hyh_notes;
		private TextView tv_hyh_yuyin_len;
		private ImageView iv_hyh_voice_sign;
		private ImageView iv_hyh_pic;
		private TextView tv_hyh_time_val;
		private RelativeLayout rl_f_yuyin;
		private RelativeLayout rl_my_yuyin;
		private RelativeLayout rl_hyh_yuyin;
		private ImageView iv_sys_head;
		public Hv( View view){
			
			
			tv_time = (TextView) view.findViewById( R.id.tv_time );
			ll_my_box = (RelativeLayout) view.findViewById( R.id.ll_my_box );
			iv_my_head = (ImageView) view.findViewById( R.id.iv_my_head );
			tv_my_text = (TextView) view.findViewById( R.id.tv_my_text );
			ll_my_yuyin = (LinearLayout) view.findViewById( R.id.ll_my_yuyin );
			tv_my_yuyin_len = (TextView) view.findViewById( R.id.tv_my_yuyin_len );
			iv_my_voice_sign = (ImageView)view.findViewById( R.id.iv_my_voice_sign );
			rl_my_yuyin = (RelativeLayout) view.findViewById( R.id.rl_my_yuyin );
			
			ll_my_pic = (LinearLayout) view.findViewById( R.id.ll_my_pic );
			iv_my_pic = (ImageView) view.findViewById( R.id.iv_my_pic );
			
			
			ll_my_card = (LinearLayout) view.findViewById( R.id.ll_my_card );
			
			iv_my_card_head = (ImageView) view.findViewById( R.id.iv_my_card_head );
			tv_my_name_val = (TextView) view.findViewById( R.id.tv_my_name_val );
			tv_my_phone_val =  (TextView) view.findViewById( R.id.tv_my_phone_val );
			tv_my_address_val =  (TextView) view.findViewById( R.id.tv_my_address_val );
			
			ll_my_map = (RelativeLayout) view.findViewById( R.id.ll_my_map );
			iv_my_map= (ImageView) view.findViewById( R.id.iv_my_map );
			tv_my_address =  (TextView) view.findViewById( R.id.tv_my_address );
			
			ll_f_box = (RelativeLayout) view.findViewById( R.id.ll_f_box );
			iv_f_head = (ImageView) view.findViewById( R.id.iv_f_head );
			iv_sys_head = (ImageView) view.findViewById( R.id.iv_sys_head );
			tv_f_text = (TextView) view.findViewById( R.id.tv_f_text );
			ll_f_yuyin = (LinearLayout) view.findViewById( R.id.ll_f_yuyin );
			tv_f_yuyin_len = (TextView) view.findViewById( R.id.tv_f_yuyin_len );
			iv_f_voice_sign = (ImageView)view.findViewById( R.id.iv_f_voice_sign );
			rl_f_yuyin = (RelativeLayout) view.findViewById( R.id.rl_f_yuyin );
			
			ll_f_pic = (LinearLayout) view.findViewById( R.id.ll_f_pic );
			iv_f_pic = (ImageView) view.findViewById( R.id.iv_f_pic );
			
			
			ll_f_card = (LinearLayout) view.findViewById( R.id.ll_f_card );
			
			iv_f_card_head = (ImageView) view.findViewById( R.id.iv_f_card_head );
			tv_f_name_val = (TextView) view.findViewById( R.id.tv_f_name_val );
			tv_f_phone_val =  (TextView) view.findViewById( R.id.tv_f_phone_val );
			tv_f_address_val =  (TextView) view.findViewById( R.id.tv_f_address_val );
			
			ll_f_map = (RelativeLayout) view.findViewById( R.id.ll_f_map );
			iv_f_map= (ImageView) view.findViewById( R.id.iv_f_map );
			tv_f_address =  (TextView) view.findViewById( R.id.tv_f_address );
			
			//账单
			rl_bill = (RelativeLayout) view.findViewById( R.id.rl_bill ); 
			rl_bill_padding = (RelativeLayout) view.findViewById( R.id.rl_bill_padding ); 
			tv_bill_code_val = (TextView) view.findViewById( R.id.tv_bill_code_val );
			tv_bill_notes = (TextView) view.findViewById( R.id.tv_bill_notes );
			tv_bill_money_val = (TextView) view.findViewById( R.id.tv_bill_money_val );
			tv_bill_time_val = (TextView) view.findViewById( R.id.tv_bill_time_val );
			
			//账单支付
			rl_pay_bill = (LinearLayout) view.findViewById( R.id.rl_pay_bill );
			tv_pay_head =  ( TextView ) view.findViewById( R.id.tv_pay_head );
			tv_pay_tradecode = ( TextView ) view.findViewById( R.id.tv_pay_tradecode );
			//创建付款
			rl_c_pay = (RelativeLayout) view.findViewById( R.id.rl_c_pay ); 
			tv_c_txt= (TextView) view.findViewById( R.id.tv_c_txt );
			tv_c_pay = (TextView) view.findViewById( R.id.tv_c_pay );
			tv_c_code_val = (TextView) view.findViewById( R.id.tv_c_code_val );
			tv_c_notes = (TextView) view.findViewById( R.id.tv_c_notes );
			tv_c_money_val = (TextView) view.findViewById( R.id.tv_c_money_val );
			tv_c_time_val = (TextView) view.findViewById( R.id.tv_c_time_val );
			
			//喊一喊
			rl_hyh = (RelativeLayout) view.findViewById( R.id.rl_hyh );
			tv_hyh_notes = (TextView) view.findViewById( R.id.tv_hyh_notes );
			tv_hyh_yuyin_len = (TextView) view.findViewById( R.id.tv_hyh_yuyin_len );
			iv_hyh_voice_sign = ( ImageView ) view.findViewById( R.id.iv_hyh_voice_sign );
			iv_hyh_pic = (ImageView) view.findViewById( R.id.iv_hyh_pic );
			tv_hyh_time_val = (TextView) view.findViewById( R.id.tv_hyh_time_val );
			rl_hyh_yuyin = ( RelativeLayout ) view.findViewById(R.id.rl_hyh_yuyin );
			
		}
	}
	private ChatContent parseData(String json){
		Response response = new Response( new String( json ) );
		try {
			JSONObject root = response.asJSONObject();
			return new ChatContent( root );		
		} catch (GezitechException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
				ImageView iv_my_voice_sign = (ImageView) v.findViewById(R.id.iv_my_voice_sign);
				
				if( iv_my_voice_sign != null ){
					iv_my_voice_sign.setImageResource(R.drawable.dhck_voice_blue_03);
					 AnimationDrawable animPlay = (AnimationDrawable) iv_my_voice_sign.getDrawable();
	                 if (animPlay!= null && !animPlay.isRunning()) {
	                     animPlay.stop();
	                 }
				}
			  }catch(Exception e){
			    	
			    }
			try{
				ImageView iv_f_voice_sign = (ImageView) v.findViewById(R.id.iv_f_voice_sign);
				if( iv_f_voice_sign != null ){
					iv_f_voice_sign.setImageResource(R.drawable.dhck_voice_gray_03);
					 AnimationDrawable animPlay = (AnimationDrawable) iv_f_voice_sign.getDrawable();
	                 if (animPlay!= null && !animPlay.isRunning()) {
	                     animPlay.stop();
	                 }
				}
			 }catch(Exception e){
			    	
			 }
			try{
				ImageView iv_hyh_voice_sign = (ImageView) v.findViewById(R.id.iv_hyh_voice_sign);
				if( iv_hyh_voice_sign != null ){
					iv_hyh_voice_sign.setImageResource(R.drawable.dhck_voice_gray_03);
					 AnimationDrawable animPlay = (AnimationDrawable) iv_hyh_voice_sign.getDrawable();
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
	private void playAudio(String audioPath,final ImageView iv_voice_sign,boolean isFriend) {
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
				iv_voice_sign.setImageResource( isFriend ? R.drawable.progress_voice_gray:R.drawable.progress_voice_blue);
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
				activity.startActivityForResult(intent, 1001);
			}
		});
	}
	private String getDateStr(long time) {
		long h = time / 3600;
		long m = (time % 3600) / 60;
		long s = (time % 3600) % 60;
		return (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":"
				+ (s < 10 ? "0" + s : s);
	}
	public void refreshcountdown(MyListView list_view) {

		int firstVisibleItemIndex = list_view.getFirstVisiblePosition();

		for (int i = 0; i < list_view.getChildCount(); i++) {    
		    try{
		    	View v = list_view.getChildAt( i );
		    	ChatContent item = (ChatContent)this .getItem( firstVisibleItemIndex + i -1 );
		    	
		    	if( item.type == 5 ){
			    	
		    		Hv hv = new Hv( v );
			    	
			    	cbillAction(hv.rl_bill, hv.tv_bill_code_val, hv.tv_bill_notes,hv.tv_bill_money_val, hv.tv_bill_time_val, item.body );
			    	
		    	}else if( item.type == 9 ){
		    		Hv hv = new Hv( v );
		    		hyhAction(true,hv.rl_hyh_yuyin, hv.rl_hyh, hv.tv_hyh_notes,hv.tv_hyh_yuyin_len,hv.iv_hyh_voice_sign, hv.iv_hyh_pic ,hv.tv_hyh_time_val, item.body);
		    	}else if( item.type == 8 ){// 创建付款
		    		Hv hv = new Hv( v );
		    		createPay(hv.rl_c_pay,hv.tv_c_txt,hv.tv_c_pay,hv.tv_c_code_val, hv.tv_c_notes,hv.tv_c_money_val, hv.tv_c_time_val, item.body );
		    	}
		    }catch(Exception e){
		    	
		    }
		}
		
	}
	//加载账单的详情
	private void loadBillDetail( long id ){
		RequestParams params = new RequestParams();
		params.put("id", id);
		GezitechAlertDialog.loadDialog( activity );
		ShoutManager.getInstance().getrbillList(params, new OnAsynGetOneListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				GezitechAlertDialog.closeDialog();
				activity.Toast( errorMsg );
			}
			
			@Override
			public void OnGetOneDone(GezitechEntity_I entity) {
				GezitechAlertDialog.closeDialog();
				Intent intent = new Intent(activity,OrderDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("billdetail", (Bill)entity);
				intent.putExtra("from", 1);
				intent.putExtras( bundle );
				activity.startActivityForResult(intent, 1007);
				
				activity.overridePendingTransition(R.anim.out_to_down, R.anim.exit_anim);
			}
		});
	}
	private RelativeLayout.LayoutParams  _params(int w,int lm, int rm,int tm, int align_parent ){
		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(DimensionUtility.dip2px(activity,  w ), ViewGroup.LayoutParams.WRAP_CONTENT);		
		lp1.addRule(align_parent,RelativeLayout.TRUE);
		lp1.leftMargin = DimensionUtility.dip2px(activity, lm );
		lp1.rightMargin = DimensionUtility.dip2px(activity,  rm );
		lp1.topMargin = DimensionUtility.dip2px(activity, tm );
		return lp1;
	}
}
