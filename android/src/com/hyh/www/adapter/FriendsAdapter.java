package com.hyh.www.adapter;

import java.util.Arrays;
import java.util.List;

import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.entity.PageList;
import com.gezitech.widget.RemoteImageView;
import com.gezitech.widget.listgroup.PinnedHeaderListView;
import com.gezitech.widget.listgroup.PinnedHeaderListView.PinnedHeaderAdapter;
import com.hyh.www.R;
import com.hyh.www.entity.Contacts;
import com.hyh.www.entity.FieldVal;
import com.hyh.www.entity.Friend;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
/**
 * 
 * @author xiaobai
 * 2014-10-15
 * @todo( 通讯录适配器 )
 */
public class FriendsAdapter extends BasicAdapter implements SectionIndexer,
		PinnedHeaderAdapter, OnScrollListener {
	private int mLocationPosition = -1;
	// 首字母集
	private List<String> mFriendsSections;
	private List<Integer> mFriendsPositions;
	private int count = 0;
	//设置分组标题
	public void setFriendsSections( List<String> friendsSections ){
		mFriendsSections = friendsSections;
	}
	//设置分组的序号
	public void setFriendsPositions( List<Integer> friendsPositions ){
		mFriendsPositions = friendsPositions;
	}
	//设置朋友的请求条数
	public void setCount( int count){
		this.count = count;
	}
	//获取之前的条数
	public int  getNumberCount(){
		return this.count;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		int section = getSectionForPosition(position);
		final Contacts item = ( Contacts ) getItem( position );
		convertView = inflater.inflate(R.layout.list_contacts_item, null);
		View v_head_height = (View) convertView.findViewById( R.id.v_head_height );
		TextView titled_text = (TextView) convertView.findViewById( R.id.titled_text );
		View v_line = (View) convertView.findViewById( R.id.v_line );
		TextView tv_session_item_name = (TextView) convertView.findViewById( R.id.tv_session_item_name );
		RemoteImageView iv_session_item = (RemoteImageView) convertView.findViewById( R.id.iv_session_item );
		RelativeLayout session_item = ( RelativeLayout ) convertView.findViewById( R.id.session_item );
		TextView tv_count = (TextView) convertView.findViewById( R.id.tv_count );
		TextView tv_black = (TextView) convertView.findViewById( R.id.tv_black );
		if (getPositionForSection(section) == position) {
			if(position==0){
				titled_text.setVisibility(View.GONE);
				v_head_height.setVisibility( View.VISIBLE );
				iv_session_item.setImageResource( R.drawable.hh_newfriends );
				if(count>0){
					tv_count.setVisibility( View.VISIBLE );
					tv_count.setText( count+"" );
				}
			}else{
				titled_text.setVisibility(View.VISIBLE);
				titled_text.setText(mFriendsSections.get(section));
			}
		} else {
			titled_text.setVisibility(View.GONE);
		}	
		if( item.isLine == 1 ){
			v_line.setVisibility( View.VISIBLE );
		}else{
			v_line.setVisibility( View.GONE );
		}
		
		if( position> 0 && item.isblacklist > 0 ){
			tv_black.setVisibility( View.VISIBLE );
			
		}
		
		session_item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onClickDataPress.onDataPerss( item ,position );
			}
		});
		//昵称
		tv_session_item_name.setText( FieldVal.value( item.notes ).equals("") ? ( FieldVal.value( item.nickname ).equals("") ? item.username : item.nickname )  : item.notes );
		//头像
		if(position>0) iv_session_item.setImageUrl( item.head,true );
		return convertView;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if (view instanceof PinnedHeaderListView) {
			((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
		}
	}

	@Override
	public int getPinnedHeaderState(int position) {
		int realPosition = position;
		if (realPosition < 0
				|| (mLocationPosition != -1 && mLocationPosition == realPosition)) {
			return PINNED_HEADER_GONE;
		}
		mLocationPosition = -1;
		int section = getSectionForPosition(realPosition);
		int nextSectionPosition = getPositionForSection(section + 1);
		if (nextSectionPosition != -1
				&& realPosition == nextSectionPosition - 1) {
			return PINNED_HEADER_PUSHED_UP;
		}
		return PINNED_HEADER_VISIBLE;
	}

	@Override
	public void configurePinnedHeader(View header, int position, int alpha) {
		// TODO Auto-generated method stub
		int realPosition = position;
		int section = getSectionForPosition(realPosition);
		String title = (String) getSections()[section];
		TextView textViewTitle=  (TextView) header.findViewById(R.id.friends_list_header_text);
		textViewTitle.setText(title);
		if( position == 0 && title.equals("") ){
			textViewTitle.setBackgroundColor(Color.parseColor("#00000000"));
		}else{
			textViewTitle.setBackgroundColor(Color.parseColor("#F8F8F8"));
		}
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return mFriendsSections.toArray();
	}

	@Override
	public int getPositionForSection(int section) {
		if (section < 0 || section >= mFriendsSections.size()) {
			return -1;
		}
		return mFriendsPositions.get(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		if (position < 0 || position >= getCount()) {
			return -1;
		}
		int index = Arrays.binarySearch(mFriendsPositions.toArray(), position);
		return index >= 0 ? index : -index - 2;
	}

}
