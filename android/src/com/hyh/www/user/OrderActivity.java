package com.hyh.www.user;

import java.util.ArrayList;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.entity.User;
import com.gezitech.service.managers.AccountManager;
import com.gezitech.service.managers.ShoutManager;
import com.gezitech.widget.MyListView;
import com.gezitech.widget.MyListView.OnMoreListener;
import com.gezitech.widget.MyListView.OnRefreshListener;
import com.hyh.www.R;
import com.hyh.www.RegisteredActivity;
import com.hyh.www.adapter.BasicAdapter.OnClickDataPress;
import com.hyh.www.adapter.IncomeAdapter;
import com.hyh.www.adapter.OrderAdapter;
import com.hyh.www.chat.BillDetailActivity_bak;
import com.hyh.www.chat.OrderDetailActivity;
import com.hyh.www.entity.Bill;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 
 * @author xiaobai
 * ( 订单列表 ) 
 */
public class OrderActivity extends GezitechActivity implements OnClickListener {
    
	private OrderActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private int type; //1所有订单 2已付款订单 3 服务中订单
	private String title;
	private MyListView list_view;
	int page = 1;
	int pageSize = 20;
	private OrderAdapter orderAdapter;
	protected int position = -1;
	private long uid = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);
		Intent intent = _this.getIntent();
		type = intent.getIntExtra("type",1);
		title = intent.getStringExtra("title");
		uid  = intent.getLongExtra("uid", 0);
		_init();
	}
    
	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);
		
		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);
		
		TextView tv_title = (TextView) findViewById( R.id.tv_title );
		tv_title.setText( title );

		
		list_view = (MyListView) findViewById( R.id.list_view );
		orderAdapter = new OrderAdapter( type, this );
		list_view.setAdapter( orderAdapter );
		list_view.footerShowState( 2 );
		list_view.setonRefreshListener( new OnRefreshListener() {		
			@Override
			public void onRefresh() {
				page = 1;
				loadData();
			}
		});
		list_view.setOnMoreListener( new OnMoreListener() {
			
			@Override
			public void OnMore() {
				page++;
				list_view.footerShowState( 0 );
				loadData();
			}
			@Override
			public void OnMore(int firstVisiableItem, int displayItemCount, int count,
					int lastVisibleItem) {}
		});
		orderAdapter.setOnClickDataPress( new OnClickDataPress() {
			@Override
			public void onDataPerss(GezitechEntity_I item, int position) {
				if( item == null ) return;
				_this.position  = position;
				loadBillDetail( (Bill)item, position);
			}
		});
		GezitechAlertDialog.loadDialog( this );
		loadData();
		mHandler.postDelayed( countdown, 1000);
	}
	private Handler mHandler = new Handler();
	private Runnable  countdown = new Runnable()
	{
		public void run()
		{
			orderAdapter.refreshcountdown( list_view );
			
			mHandler.postDelayed(countdown, 1000);
		}
	};
	//加载数据
	public void loadData(){
		
		AccountManager.getInstance().tradenumberlist(page, pageSize, type ,uid, new OnAsynGetListListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				// TODO Auto-generated method stub
				GezitechAlertDialog.closeDialog();
				list_view.onRefreshComplete();	
				list_view.footerShowState( 1 );	
				if( errorCode.equals("-1") ){
					_this.Toast( errorMsg );
				}
			}
			
			@Override
			public void OnGetListDone(ArrayList<GezitechEntity_I> list) {
				GezitechAlertDialog.closeDialog();
				int count = list.size();
				GezitechAlertDialog.closeDialog();
				if( page == 1 ){//下拉刷新 首次加载
					if( count>0 ) orderAdapter.removeAll();
					list_view.onRefreshComplete();	
				}
				if( count >= pageSize ){
					list_view.footerShowState( 1 );	
				}else if( count < pageSize ){
					list_view.footerShowState( -1 );	
				}
				
				orderAdapter.addList(list, false);				
			}
		});
		
	}
	// 加载账单的详情
	private void loadBillDetail( Bill bill, int position ) {
		RequestParams params = new RequestParams();
		params.put("id", bill.id);
		GezitechAlertDialog.loadDialog( _this );
		ShoutManager.getInstance().getrbillList(params,
			new OnAsynGetOneListener() {
	
				@Override
				public void OnAsynRequestFail(String errorCode,
						String errorMsg) {
					GezitechAlertDialog.closeDialog();
					_this.Toast(errorMsg);
				}
	
				@Override
				public void OnGetOneDone(GezitechEntity_I entity) {
					GezitechAlertDialog.closeDialog();
					Intent intent = new Intent( _this, OrderDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("billdetail", (Bill) entity);
					intent.putExtras(bundle);
					
					_this.startActivityForResult(intent, 1007);
					_this.overridePendingTransition(R.anim.out_to_down,R.anim.exit_anim);
				}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if( data == null ) return;
		switch( requestCode ){
		case 1007 :
			if( _this.position < 0 ) return;
			//适配器中的值
			Bill oldbill = (Bill)orderAdapter.getItem( _this.position );
			
			//返回的值
			Bundle bundle = data.getExtras();
			Bill bill = (Bill) bundle.getSerializable("bill");
			
			oldbill.state = bill.state;
			orderAdapter.setItem(oldbill, _this.position);
			break;
		
		}
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		
		case R.id.bt_home_msg:
			finish();
			break;
		default:
			break;
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mHandler.removeCallbacks( countdown );
	}
}
