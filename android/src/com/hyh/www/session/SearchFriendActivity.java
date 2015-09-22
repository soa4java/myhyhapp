package com.hyh.www.session;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.service.managers.FriendManager;
import com.gezitech.util.StringUtil;
import com.gezitech.widget.MyListView;
import com.gezitech.widget.MyListView.OnMoreListener;
import com.hyh.www.R;
import com.hyh.www.adapter.FriendRequestOrSearchAdapter;

//搜索朋友
public class SearchFriendActivity extends GezitechActivity implements OnClickListener {
	private SearchFriendActivity _this = this;
	private String key;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_title;
	private MyListView list_view;
	int page = 1;
	int pageSize = 20;
	private FriendRequestOrSearchAdapter friendRequestOrSearchAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_friend);
		
		Intent  intent = _this.getIntent();
			
		key = intent.getStringExtra("key");
		
		_init();
	}
	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

	    tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText( StringUtil.subString( "搜索“"+key+"”", 16 ) );
		
		list_view = (MyListView) findViewById( R.id.list_view );
		
		list_view.footerShowState( 2 );
		friendRequestOrSearchAdapter = new FriendRequestOrSearchAdapter( 2, _this );			
		list_view.setAdapter( friendRequestOrSearchAdapter );	
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
	private void loadData() {
		// TODO Auto-generated method stub
		FriendManager.getInstance().scanfriend(page, pageSize, key, new OnAsynGetListListener() {
			
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
					if( count>0 ) friendRequestOrSearchAdapter.removeAll();
					list_view.onRefreshComplete();	
				}
				if( count >= pageSize ){
					list_view.footerShowState( 1 );	
				}else if( count < pageSize ){
					list_view.footerShowState( 2 );	
				}
				
				friendRequestOrSearchAdapter.addList(list, false);
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
}
