package com.hyh.www.adapter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.entity.PageList;
import com.gezitech.widget.listgroup.PinnedHeaderListView;
import com.gezitech.widget.listgroup.PinnedHeaderListView.PinnedHeaderAdapter;
import com.hyh.www.R;
import com.hyh.www.entity.Bill;
import com.hyh.www.entity.Businesslist;
import com.hyh.www.entity.Contacts;
import com.hyh.www.entity.FieldVal;
import com.hyh.www.entity.Incomelist;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
 * @todo( 从商家或者个人收益适配器 )
 */
public class IncomeAdapter extends BasicAdapter{
	int type ;
	public IncomeAdapter( int type ){
		this.type = type;
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
	public View getView(int position, View view, ViewGroup parent) {
		view = inflater.inflate(R.layout.list_income, null);
		LinearLayout ll_head1 = (LinearLayout) view.findViewById( R.id.ll_head1 );
		LinearLayout ll_head2 = (LinearLayout) view.findViewById( R.id.ll_head2 );
		LinearLayout ll_head3 = (LinearLayout) view.findViewById( R.id.ll_head3 );
		LinearLayout ll_head4 = (LinearLayout) view.findViewById( R.id.ll_head4 );
		
		if( type == 1 ||  type == 2  ){
			Businesslist item = (Businesslist) getItem( position ) ;
			ll_head1.setVisibility( View.VISIBLE );
			TextView tv_shangjia = (TextView) view.findViewById( R.id.tv_shangjia );
			TextView tv_goumaiyonghu = (TextView) view.findViewById( R.id.tv_goumaiyonghu );
			TextView tv_jiaoyi = (TextView) view.findViewById( R.id.tv_jiaoyi );
			TextView tv_ticheng = (TextView) view.findViewById( R.id.tv_ticheng );
			TextView tv_shijian1 = (TextView) view.findViewById( R.id.tv_shijian1 );
			tv_shangjia.setText( FieldVal.value( item.company_name ) );
			tv_goumaiyonghu.setText( FieldVal.value( item.invite_nickname ).equals("") ? item.invite_username : item.invite_nickname );
			tv_jiaoyi.setText( item.trademoney+"" );
			tv_ticheng.setText( item.gotmoney+"" );
			Date time = new Date(item.ctime * 1000L);//开始时间
			String timeStr = String.format("%02d/%02d %02d:%02d", time.getMonth() + 1, time.getDate(), time.getHours(), time.getMinutes() );
			tv_shijian1.setText( timeStr );
		}/*
		else if(){
			Incomelist item = (Incomelist) getItem(position);
			ll_head2.setVisibility( View.VISIBLE );
			TextView tv_yonghu = (TextView) view.findViewById( R.id.tv_yonghu );
			TextView tv_huodongjuan = (TextView) view.findViewById( R.id.tv_huodongjuan );
			TextView tv_shijian2 = (TextView) view.findViewById( R.id.tv_shijian2 );
			tv_yonghu.setText( item.invite_username );
			tv_huodongjuan.setText( item.invite_money+""  );
			Date time = new Date(item.ctime * 1000L);//开始时间
			String timeStr = String.format("%02d/%02d %02d:%02d", time.getMonth() + 1, time.getDate(), time.getHours(), time.getMinutes() );
			tv_shijian2.setText( timeStr );
		}*/
		else if( type == 3 || type == 4 ){
			Bill item = (Bill) getItem(position);
			ll_head3.setVisibility( View.VISIBLE );
			TextView tv_bianhao3 = (TextView) view.findViewById( R.id.tv_bianhao3 );
			TextView tv_username3 = (TextView) view.findViewById( R.id.tv_username3 );
			TextView tv_jine3 = (TextView) view.findViewById( R.id.tv_jine3 );
			TextView tv_leixing3 = (TextView) view.findViewById( R.id.tv_leixing3 );
			TextView tv_time_3 = (TextView) view.findViewById( R.id.tv_time_3 );
			
			tv_bianhao3.setText( item.tradecode+"" );
			tv_username3.setText( item.invite_username );
			tv_jine3.setText( item.money +"") ;
			tv_leixing3.setText( item.tradetype == 0 ? "账单" : "付款" );
			Date time = new Date(item.ctime * 1000L);//开始时间
			String timeStr = String.format("%02d/%02d %02d:%02d", time.getMonth() + 1, time.getDate(), time.getHours(), time.getMinutes() );
			tv_time_3.setText( timeStr );
		}else if( type == 5 ){
			Bill item = (Bill) getItem(position);
			ll_head4.setVisibility( View.VISIBLE );
			TextView tv_jine4 = (TextView) view.findViewById( R.id.tv_jine4 );
			TextView tv_beizhu4 = (TextView) view.findViewById( R.id.tv_beizhu4 );
			TextView tv_leixing4 = (TextView) view.findViewById( R.id.tv_leixing4 );
			TextView tv_time_4 = (TextView) view.findViewById( R.id.tv_time_4 );
			
			tv_jine4.setText( item.money +"") ;
			tv_beizhu4.setText( item.notes +"") ;
			tv_leixing4.setText( item.typename );
			Date time = new Date(item.ctime * 1000L);//开始时间
			String timeStr = String.format("%02d/%02d %02d:%02d", time.getMonth() + 1, time.getDate(), time.getHours(), time.getMinutes() );
			tv_time_4.setText( timeStr );
		}
		return view;
	}

}
