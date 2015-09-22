package com.hyh.www.adapter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.gezitech.adapter.OptionAdapter.OnClickDataPress;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.entity.PageList;
import com.gezitech.util.DateUtil;
import com.gezitech.widget.listgroup.PinnedHeaderListView;
import com.gezitech.widget.listgroup.PinnedHeaderListView.PinnedHeaderAdapter;
import com.hyh.www.R;
import com.hyh.www.entity.Bill;
import com.hyh.www.entity.Businesslist;
import com.hyh.www.entity.Contacts;
import com.hyh.www.entity.Incomelist;
import com.hyh.www.entity.News;

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
import android.widget.SectionIndexer;
import android.widget.TextView;
/**
 * 
 * @author xiaobai
 * 2014-10-15
 * @todo( 新闻 )
 */
public class NewsAdapter extends BasicAdapter{

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
		view = inflater.inflate(R.layout.list_news, null);
		final News item = (News) this.getItem( position );
		
		TextView tv_systemmessage_banben = (TextView) view.findViewById( R.id.tv_systemmessage_banben );
		
		TextView text_systemmessage_banben = (TextView) view.findViewById( R.id.text_systemmessage_banben );
		
		LinearLayout systemmessage_banben = (LinearLayout) view.findViewById( R.id.systemmessage_banben );
		
		tv_systemmessage_banben.setText( item.title );
		text_systemmessage_banben.setText( DateUtil.getShortTime( item.ctime*1000 ) );
		
		systemmessage_banben.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				onClickDataPress.onDataPerss( item,position );
			}
		});
		
		return view;
	}
	

}
