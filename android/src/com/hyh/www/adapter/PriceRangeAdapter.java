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
import com.hyh.www.entity.PriceRange;
import com.hyh.www.widget.YMDialog;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 
 * @author xiaobai
 * 2014-10-15
 * @todo( 价格距离适配器  )
 */
public class PriceRangeAdapter extends BasicAdapter{

	private Activity activity;
	public int selectedID = -1 ;
	public PriceRangeAdapter _this = this;
	public PriceRangeAdapter( Activity activity  ){
		super();
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
			view =  inflater.inflate( R.layout.list_price_range, null);
			hv = new Hv( view );
			view.setTag( hv );
		}else{
			hv = (Hv)view.getTag();
		}
		final PriceRange pr = ( PriceRange ) list.get( position );
		if( selectedID == position ){
			hv.rb_pay_item.setChecked( true );
		}else{
			hv.rb_pay_item.setChecked( false );
		}
		hv.rb_pay_item.setText( Html.fromHtml( "<font color=\"#ff340c\">"+pr.price+"元</font> "+"<font color=\"#323232\">("+pr.description+")</font>" ) );
		hv.rb_pay_item.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectedID = position;
				onClickDataPress.onDataPerss( pr , position);
				
			}
		});
		
		return view;
	}
	//缓存
	public class Hv{
		
		private RadioButton rb_pay_item;

		public Hv( View view){
			rb_pay_item = (RadioButton) view.findViewById(R.id.rb_pay_item);
		}
		
	}

}
