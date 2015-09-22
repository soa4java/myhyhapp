package com.hyh.www.adapter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynInsertListener;
import com.gezitech.entity.PageList;
import com.gezitech.service.managers.ShoutManager;
import com.gezitech.util.DateUtils;
import com.gezitech.util.StringUtil;
import com.gezitech.widget.RemoteImageView;
import com.gezitech.widget.listgroup.PinnedHeaderListView;
import com.gezitech.widget.listgroup.PinnedHeaderListView.PinnedHeaderAdapter;
import com.hyh.www.R;
import com.hyh.www.ZhuyeActivity;
import com.hyh.www.entity.Bill;
import com.hyh.www.entity.Businesslist;
import com.hyh.www.entity.Contacts;
import com.hyh.www.entity.FieldVal;
import com.hyh.www.entity.Incomelist;
import com.hyh.www.widget.ImageShow;
import com.hyh.www.widget.YMDialog;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

/**
 * 
 * @author xiaobai 2014-10-15
 * @todo( 选择模版适配器 )
 */
public class TemplateAdapter extends BasicAdapter {

	private GezitechActivity activity;

	public TemplateAdapter(GezitechActivity activity) {
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
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		view = inflater.inflate(R.layout.list_template, null);
		final Bill item = (Bill) getItem(position);
		Hv hv = new Hv(view);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickDataPress.onDataPerss(item, position);
			}
		});
		view.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				final Bill bill = (Bill) getItem(position);
				final YMDialog ym = new YMDialog(activity);
				ym.setHintMsg("确定删除模版?").setConfigButton(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ym.dismiss();
						RequestParams params = new RequestParams();
						params.put("id", bill.id);
						GezitechAlertDialog.loadDialog(activity);
						ShoutManager.getInstance().deletetemplets(params,
								new OnAsynInsertListener() {

									@Override
									public void OnAsynRequestFail(
											String errorCode, String errorMsg) {
										activity.Toast(errorMsg);
										GezitechAlertDialog.closeDialog();
									}

									@Override
									public void onInsertDone(String id) {
										GezitechAlertDialog.closeDialog();
										remove(position);
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
		
		if( FieldVal.value( item.litpicUrl ).equals("") ){
			hv.iv_pic.setImageUrl( null );
			hv.iv_pic.setVisibility( View.GONE );
			hv.myrelease_nopic.setVisibility( View.VISIBLE );
		}else{
			hv.iv_pic.setImageUrl(item.litpicUrl);
			hv.iv_pic.setVisibility( View.VISIBLE );
			hv.myrelease_nopic.setVisibility( View.GONE );
			hv.iv_pic.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//画廊展示图片
					final String[] images = new String[1];
					String[] pic = item.litpicUrl.split("src=");
					images[0] = StringUtil.stringDecode( pic[1] );
					ImageShow.jumpDisplayPic(images, 0, activity);
				}
			});
		}
		
		hv.tv_shangjia.setText(item.money + "");
		hv.tv_fabu.setText( item.activetimeName  );
		hv.tv_myrelease_beizhu.setText(item.notes);

		return view;
	}

	public class Hv {

		private TextView tv_myrelease_beizhu;
		private TextView tv_shangjia;
		private TextView tv_fabu;
		private RemoteImageView iv_pic;
		private RelativeLayout myrelease_yuyin;
		private TextView myrelease_nopic;
		public Hv(View v) {

			tv_myrelease_beizhu = (TextView) v
					.findViewById(R.id.tv_myrelease_beizhu);
			tv_shangjia = (TextView) v.findViewById(R.id.tv_shangjia);
			tv_fabu = (TextView) v.findViewById(R.id.tv_fabu);
			iv_pic = (RemoteImageView) v.findViewById(R.id.iv_pic);
			myrelease_yuyin = (RelativeLayout )v.findViewById(R.id.myrelease_yuyin);
			myrelease_nopic =  (TextView) v.findViewById(R.id.myrelease_nopic);
		}
	}

}
