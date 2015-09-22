package com.hyh.www.chat;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynInsertListener;
import com.gezitech.service.managers.AccountManager;
import com.gezitech.service.managers.ShoutManager;
import com.gezitech.widget.MyListView;
import com.gezitech.widget.MyListView.OnMoreListener;
import com.gezitech.widget.MyListView.OnRefreshListener;
import com.hyh.www.R;
import com.hyh.www.ZhuyeActivity;
import com.hyh.www.adapter.BasicAdapter.OnClickDataPress;
import com.hyh.www.adapter.TemplateAdapter;
import com.hyh.www.entity.Bill;
import com.hyh.www.entity.ChatContent;
import com.hyh.www.widget.YMDialog;
import com.loopj.android.http.RequestParams;

public class SelectTemplateActivity extends GezitechActivity {
	private SelectTemplateActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private MyListView list_view;
	private TemplateAdapter templateAdapter;
	private int page = 1, pageSize = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_template);

		_init();
	}

	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.VISIBLE);
		bt_my_post.setText("添加模版");
		bt_my_post.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(_this, AddTemplateActivity.class);
				_this.startActivityForResult(intent, 1001);
				_this.overridePendingTransition(R.anim.out_to_down,
						R.anim.exit_anim);
			}
		});

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				returnData();
			}
		});

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("选择模版");

		list_view = (MyListView) findViewById(R.id.list_view);

		templateAdapter = new TemplateAdapter( _this );

		list_view.setAdapter(templateAdapter);

		list_view.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				page = 1;
				loadData();
			}
		});
		list_view.setOnMoreListener(new OnMoreListener() {

			@Override
			public void OnMore() {
				page++;
				list_view.footerShowState(0);
				loadData();
			}

			@Override
			public void OnMore(int firstVisiableItem, int displayItemCount,
					int count, int lastVisibleItem) {
			}
		});
		templateAdapter.setOnClickDataPress( new OnClickDataPress() {
			
			@Override
			public void onDataPerss(GezitechEntity_I item, int position) {
				if( item!= null ){
					
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putSerializable("template", (Bill)item );
					intent.putExtras( bundle );
					setResult(1001, intent );
					returnData();
				}
			}
		});
		GezitechAlertDialog.loadDialog(this);
		loadData();
	}// 加载数据

	public void loadData() {
		ShoutManager.getInstance().getrTradeList(page, pageSize,
				new OnAsynGetListListener() {

					@Override
					public void OnAsynRequestFail(String errorCode,
							String errorMsg) {
						GezitechAlertDialog.closeDialog();
						list_view.onRefreshComplete();
						list_view.footerShowState(1);
						if (errorCode.equals("-1")) {
							_this.Toast(errorMsg);
						}
					}

					@Override
					public void OnGetListDone(ArrayList<GezitechEntity_I> list) {
						GezitechAlertDialog.closeDialog();
						int count = list.size();
						GezitechAlertDialog.closeDialog();
						if (page == 1) {// 下拉刷新 首次加载
							if (count > 0)
								templateAdapter.removeAll();
							list_view.onRefreshComplete();
						}
						if (count >= pageSize) {
							list_view.footerShowState(1);
						} else if (count < pageSize) {
							list_view.footerShowState(-1);
						}

						templateAdapter.addList(list, false);
					}
				});
	}

	private void returnData() {

		_this.finish();
		overridePendingTransition(R.anim.in_anim, R.anim.in_from_down);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		returnData();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if( data == null ) return;
		if( requestCode  == 1001 ){
			list_view.setSelection(0);
			list_view.onClickRefreshComplete();
			page = 1;
			loadData();
		}
		
	}
}
