package com.hyh.www.user;

import java.util.ArrayList;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.entity.User;
import com.gezitech.service.managers.AccountManager;
import com.gezitech.widget.MyListView;
import com.gezitech.widget.MyListView.OnMoreListener;
import com.gezitech.widget.MyListView.OnRefreshListener;
import com.hyh.www.R;
import com.hyh.www.RegisteredActivity;
import com.hyh.www.adapter.IncomeAdapter;

import android.os.Bundle;
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
 * ( 收益列表  ) /商家 或者个人
 */
public class IncomeActivity extends GezitechActivity implements OnClickListener {
    
	private IncomeActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private int type; //1 从商家收益  2 从个人收益 3销售账单  4 购买账单
	private String title;
	private LinearLayout ll_head1;
	private LinearLayout ll_head2;
	private MyListView list_view;
	private IncomeAdapter incomeAdapter;
	int page = 1;
	int pageSize = 20;
	private LinearLayout ll_head3;
	private LinearLayout ll_head4;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_income);
		Intent intent = _this.getIntent();
		type = intent.getIntExtra("type",1);
		title = intent.getStringExtra("title");
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
		
		ll_head1 = (LinearLayout) findViewById( R.id.ll_head1 );
		ll_head2 = (LinearLayout) findViewById( R.id.ll_head2 );
		ll_head3 = (LinearLayout) findViewById( R.id.ll_head3 );
		switch( type ){
			case 1:
				
				//break;
			case 2:
				ll_head1.setVisibility( View.VISIBLE );
				//ll_head2.setVisibility( View.VISIBLE );
				break;
			case 3:
			case 4:
				ll_head3.setVisibility( View.VISIBLE );
				break;
		}
		
		list_view = (MyListView) findViewById( R.id.list_view );
		incomeAdapter = new IncomeAdapter( type );
		list_view.setAdapter( incomeAdapter );
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
		GezitechAlertDialog.loadDialog( this );
		loadData();
	}
	//加载数据
	public void loadData(){
		if( type == 1 || type == 2 ){
			AccountManager.getInstance().businesslist(page, pageSize, type == 1 ? 0 : 1, new OnAsynGetListListener() {
				
				@Override
				public void OnAsynRequestFail(String errorCode, String errorMsg) {
					requestFail(errorCode, errorMsg );
				}
				
				@Override
				public void OnGetListDone(ArrayList<GezitechEntity_I> list) {
					getListDone( list );
				}
			});
			
		}/*else if ( type == 2 ){
			
			AccountManager.getInstance().incomelist(page, pageSize, new OnAsynGetListListener() {
				
				@Override
				public void OnAsynRequestFail(String errorCode, String errorMsg) {
					requestFail(errorCode, errorMsg );
				}
				
				@Override
				public void OnGetListDone(ArrayList<GezitechEntity_I> list) {
					getListDone( list );
				}
			});
		}*/else if( type == 3 ){//销售
			
			AccountManager.getInstance().salelist(page,pageSize, user.id, new OnAsynGetListListener() {
				
				@Override
				public void OnAsynRequestFail(String errorCode, String errorMsg) {
					requestFail(errorCode, errorMsg );
				}
				
				@Override
				public void OnGetListDone(ArrayList<GezitechEntity_I> list) {
					getListDone( list );
				}
			});
		}else if( type == 4 ){//购买
			
			AccountManager.getInstance().salelist(page,pageSize,-1,new OnAsynGetListListener() {
				
				@Override
				public void OnAsynRequestFail(String errorCode, String errorMsg) {
					requestFail(errorCode, errorMsg );
				}
				
				@Override
				public void OnGetListDone(ArrayList<GezitechEntity_I> list) {
					getListDone( list );
				}
			});
		}
	}
	private void requestFail(String errorCode, String errorMsg){
		GezitechAlertDialog.closeDialog();
		list_view.onRefreshComplete();	
		list_view.footerShowState( 1 );	
		if( errorCode.equals("-1") ){
			_this.Toast( errorMsg );
		}
	}
	private void getListDone( ArrayList<GezitechEntity_I> list ){
		GezitechAlertDialog.closeDialog();
		int count = list.size();
		GezitechAlertDialog.closeDialog();
		if( page == 1 ){//下拉刷新 首次加载
			if( count>0 ) incomeAdapter.removeAll();
			list_view.onRefreshComplete();	
		}
		if( count >= pageSize ){
			list_view.footerShowState( 1 );	
		}else if( count < pageSize ){
			list_view.footerShowState( -1 );	
		}
		
		incomeAdapter.addList(list, false);
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
 
}
