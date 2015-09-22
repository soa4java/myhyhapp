package com.hyh.www.user;

import java.util.ArrayList;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.service.managers.SystemManager;
import com.gezitech.widget.MyListView;
import com.gezitech.widget.MyListView.OnMoreListener;
import com.gezitech.widget.MyListView.OnRefreshListener;
import com.hyh.www.R;
import com.hyh.www.ZhuyeActivity;
import com.hyh.www.adapter.BasicAdapter.OnClickDataPress;
import com.hyh.www.adapter.IncomeAdapter;
import com.hyh.www.adapter.NewsAdapter;
import com.hyh.www.entity.News;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 
 * @author xiaobai
 * (系统消息)
 */
public class SystemMessageActivity extends GezitechActivity implements OnClickListener{
	
	private SystemMessageActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_title;
	private MyListView list_view;
	int page = 1;
	int pageSize = 20;
	private NewsAdapter newsAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_message);
		_init();
	}
	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setText("联系我们");
		
		bt_my_post.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GezitechAlertDialog.loadDialog( _this );
				SystemManager.getInstance().configuration(new OnAsynGetListListener() {
					
					@Override
					public void OnAsynRequestFail(String errorCode, String errorMsg) {
						// TODO Auto-generated method stub
						GezitechAlertDialog.closeDialog();
						Toast( errorMsg );
					}
					
					@Override
					public void OnGetListDone(ArrayList<GezitechEntity_I> list) {
						GezitechAlertDialog.closeDialog();
						Intent intent = new Intent(_this, ContactsActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("config", list );
						intent.putExtras( bundle );
						_this.startActivity( intent );
					}
				});				
			}
		});

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(_this.getResources().getString(
				R.string.system_message));
		
		list_view = (MyListView) findViewById( R.id.list_view );
		newsAdapter = new NewsAdapter(  );
		list_view.setAdapter( newsAdapter );
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
		newsAdapter.setOnClickDataPress( new OnClickDataPress(){

			@Override
			public void onDataPerss(GezitechEntity_I item ,int position ) {
				if( item != null ) loadNewsDetail( (News)item );
			}
			
		});
		GezitechAlertDialog.loadDialog( this );
		loadData();
		
	}
	//加载数据
	private void loadData() {
		SystemManager.getInstance().getannouncementlist(page, pageSize, new OnAsynGetListListener() {			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
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
					if( count>0 ) newsAdapter.removeAll();
					list_view.onRefreshComplete();	
				}
				if( count >= pageSize ){
					list_view.footerShowState( 1 );	
				}else if( count < pageSize ){
					list_view.footerShowState( 2 );	
				}
				newsAdapter.addList(list, false);
			}
		});
	}
	//加载新闻的详情
	private void loadNewsDetail(News item){
		GezitechAlertDialog.loadDialog( this );
		SystemManager.getInstance().getannouncementdetails( item.id,  new OnAsynGetOneListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				// TODO Auto-generated method stub
				GezitechAlertDialog.closeDialog();
				Toast( errorMsg );
			}
			
			@Override
			public void OnGetOneDone(GezitechEntity_I entity) {
				// TODO Auto-generated method stub
				GezitechAlertDialog.closeDialog();
				if( entity != null ){
					Intent intent = new Intent( _this, SystemMessageDetailActivity.class );
					
					Bundle bundle = new Bundle();
					
					bundle.putSerializable("news", (News)entity );
					intent.putExtras( bundle );
					
					_this.startActivity( intent );
					
				}
				
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
