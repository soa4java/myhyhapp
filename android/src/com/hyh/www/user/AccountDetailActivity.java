package com.hyh.www.user;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.service.managers.AccountManager;
import com.gezitech.widget.MyListView;
import com.gezitech.widget.MyListView.OnMoreListener;
import com.gezitech.widget.MyListView.OnRefreshListener;
import com.hyh.www.R;
import com.hyh.www.adapter.IncomeAdapter;
/**
 * 
 * @author xiaobai
 * 2015-5-13
 * @todo( 账户明细  )
 */
public class AccountDetailActivity extends GezitechActivity implements OnClickListener {
	private AccountDetailActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	int page = 1;
	int pageSize = 20;
	private MyListView list_view;
	private IncomeAdapter incomeAdapter;
	private LinearLayout ll_head4;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_income);
		_init();
	}
    
	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);
		
		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);		
		
		TextView tv_title = (TextView) findViewById( R.id.tv_title );
		tv_title.setText( "账户明细" );
		ll_head4 = (LinearLayout) _this.findViewById( R.id.ll_head4 );
		ll_head4.setVisibility( View.VISIBLE );
		list_view = (MyListView) findViewById( R.id.list_view );
		incomeAdapter = new IncomeAdapter( 5 );
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
	private void loadData(){
		

		AccountManager.getInstance().getAccountRecordList(page,pageSize,new OnAsynGetListListener() {
			
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
