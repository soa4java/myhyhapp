package com.hyh.www.home;

import java.util.ArrayList;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.service.managers.ShoutManager;
import com.gezitech.widget.MyListView;
import com.gezitech.widget.MyListView.OnMoreListener;
import com.gezitech.widget.MyListView.OnRefreshListener;
import com.hyh.www.R;
import com.hyh.www.adapter.MyPostAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * @author xiaobai
 * (我的发布)
 */
public class MyReleaseActivity extends GezitechActivity implements OnClickListener {
	
	private MyReleaseActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_title;
	int page = 1;
	int pageSize = 10;
	private MyListView list_view;
	private MyPostAdapter myPostAdapter;
	private Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_release);
		_init();
	}
	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);
		
		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);
		
		TextView tv_title = (TextView) findViewById( R.id.tv_title );
		tv_title.setText( _this.getResources().getString( R.string.my_release ) );
		
		
		list_view = (MyListView) findViewById( R.id.list_view );
		myPostAdapter = new MyPostAdapter( this, list_view );
		list_view.setAdapter( myPostAdapter );
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
		mHandler.postDelayed( countdown, 1000);
	}
	private Runnable  countdown = new Runnable()
	{
		public void run()
		{
			myPostAdapter.refreshcountdown( list_view );
			
			mHandler.postDelayed(countdown, 1000);
		}
	};
	private void loadData() {
		ShoutManager.getInstance().myshout(page, pageSize, new OnAsynGetListListener() {
			
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
				// TODO Auto-generated method stub
				GezitechAlertDialog.closeDialog();
				int count = list.size();
				GezitechAlertDialog.closeDialog();
				if( page == 1 ){//下拉刷新 首次加载
					if( count>0 ) myPostAdapter.removeAll();
					list_view.onRefreshComplete();	
				}
				if( count >= pageSize ){
					list_view.footerShowState( 1 );	
				}else if( count < pageSize ){
					list_view.footerShowState( 2 );	
				}
				
				myPostAdapter.addList(list, false);
			}
		});
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
