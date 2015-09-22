package com.hyh.www.adapter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import com.gezitech.adapter.OptionAdapter.OnClickDataPress;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.entity.PageList;
import com.gezitech.service.managers.ChatManager;
import com.gezitech.service.xmpp.Constant;
import com.gezitech.util.DateUtil;
import com.gezitech.util.ImageDownloader;
import com.gezitech.util.StringUtil;
import com.gezitech.util.ToastMakeText;
import com.gezitech.widget.listgroup.PinnedHeaderListView;
import com.gezitech.widget.listgroup.PinnedHeaderListView.PinnedHeaderAdapter;
import com.hyh.www.R;
import com.hyh.www.entity.Bill;
import com.hyh.www.entity.Businesslist;
import com.hyh.www.entity.Contacts;
import com.hyh.www.entity.Incomelist;
import com.hyh.www.entity.NearHintMsg;
import com.hyh.www.entity.NearMsg;
import com.hyh.www.entity.News;
import com.hyh.www.user.post.NearDetailActiviy;
import com.hyh.www.widget.ActivityCommon;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

/**
 * 
 * @author xiaobai 2014-10-15
 * @todo( 喜欢 和 评论 的新消息的提示 )
 */
public class NewMsgHintAdapter extends BasicAdapter {
	private ImageDownloader mImageDownloader;
	public boolean mImageDownloaderOpen = true;
	public NewMsgHintAdapter _this = this;
	private Activity activity;

	public NewMsgHintAdapter(Activity activity) {
		_this.activity = activity;
		mImageDownloader = new ImageDownloader(null);
		mImageDownloader.setMode(ImageDownloader.Mode.CORRECT);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public GezitechEntity_I getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final Hv hv;
		if (view == null) {
			view = inflater.inflate(R.layout.list_new_msg_hint, null);
			hv = new Hv(view);
			view.setTag(hv);
		} else {
			hv = (Hv) view.getTag();
		}
		final NearHintMsg item = (NearHintMsg) list.get(position);

		mImageDownloader.download(item.head, mImageDownloaderOpen, hv.iv_head);
		hv.iv_head.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ActivityCommon.lookFriendData(item.uid, activity);
			}
		});
		hv.tv_username.setText(item.nickname);
		hv.tv_username.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ActivityCommon.lookFriendData(item.uid, activity);

			}
		});
		if( item.isLike == 1 ){
			hv.tv_content.setVisibility( View.GONE );
			hv.iv_like.setVisibility( View.VISIBLE );
		}else{
			hv.tv_content.setVisibility( View.VISIBLE );
			hv.iv_like.setVisibility( View.GONE );
			hv.tv_content.setText( StringUtil.subString( item.content , 25 ) );
			
		}
		if( item.nearbyimage == null || item.nearbyimage.equals("") ){
			hv.iv_near_img.setVisibility( View.GONE );
			hv.tv_near_content.setVisibility( View.VISIBLE );
			hv.tv_near_content.setText( item.nearbycontent );
		} else{
			hv.iv_near_img.setVisibility( View.VISIBLE );
			hv.tv_near_content.setVisibility( View.GONE );
			mImageDownloader.download(item.nearbyimage, mImageDownloaderOpen, hv.iv_near_img);
		}
		
		hv.ll_list_nearby.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//附近人详情跳转
				ChatManager.getInstance().deleteNearHintMsg( item.id );
				Intent intent4 = new Intent();
				intent4.setAction(Constant.LIKE_COMMENT_ACTION);
				activity.sendBroadcast(intent4);	
				
				Intent intent = new Intent( activity, NearDetailActiviy.class );
				Bundle bundle = new Bundle();
				item.position = position;
				bundle.putSerializable("nearHintMsg", item);
				intent.putExtras( bundle );
				activity.startActivityForResult(intent, 10001 );
			}
		});
		
		
		return view;
	}

	// 缓存布局
	class Hv {

		private ImageView iv_head;
		private TextView tv_username;
		private TextView tv_content;
		private TextView tv_near_content;
		private ImageView iv_near_img;
		private ImageView iv_like;
		private LinearLayout ll_list_nearby;

		public Hv(View v) {
			iv_head = (ImageView) v.findViewById(R.id.iv_head);
			tv_username = (TextView) v.findViewById(R.id.tv_username);
			tv_content = (TextView) v.findViewById(R.id.tv_content);
			tv_near_content = (TextView) v.findViewById(R.id.tv_near_content);
			iv_near_img = (ImageView) v.findViewById(R.id.iv_near_img);
			iv_like =  (ImageView) v.findViewById(R.id.iv_like);
			ll_list_nearby = (LinearLayout) v.findViewById( R.id.ll_list_nearby);
		}
	}

}
