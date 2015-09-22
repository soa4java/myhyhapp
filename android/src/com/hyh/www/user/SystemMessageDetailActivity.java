package com.hyh.www.user;

import java.util.ArrayList;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.service.managers.SystemManager;
import com.gezitech.util.DateUtil;
import com.gezitech.widget.MyListView;
import com.gezitech.widget.MyListView.OnMoreListener;
import com.gezitech.widget.MyListView.OnRefreshListener;
import com.hyh.www.R;
import com.hyh.www.adapter.IncomeAdapter;
import com.hyh.www.adapter.NewsAdapter;
import com.hyh.www.entity.News;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 
 * @author xiaobai
 * (系统消息详情)
 */
public class SystemMessageDetailActivity extends GezitechActivity implements OnClickListener{
	
	private SystemMessageDetailActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private News news = null;
	private TextView tv_system_title;
	private TextView tv_time;
	private TextView tv_content;
	private WebView wv_content;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_message_detail);
		
		Intent intent = _this.getIntent();
		
		news = (News) intent.getExtras().getSerializable("news");
		
		_init();
	}
	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setText( "联系我们" );
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
				R.string.system_message_detail));
		
		tv_system_title = (TextView) findViewById( R.id.tv_system_title );
		tv_time = (TextView) findViewById( R.id.tv_time );
		//tv_content = (TextView) findViewById( R.id.tv_content );
		
		tv_system_title.setText( news.title );
		
		tv_time.setText( DateUtil.getShortTime( news.ctime*1000 ) );
//		
//		tv_content.setText( Html.fromHtml( news.content ) );
		wv_content = (WebView) findViewById( R.id.wv_content );
		final String cssstyle ="<style> *{font-size:14px;line-height:20px;color:#323232;} p,span{color:#323232;} img{max-width:100%;}</style>";
		wv_content.getSettings().setJavaScriptEnabled(true);//设置支持脚本   
		wv_content.setBackgroundColor( _this.getResources().getColor( R.color.colorf8f8f8 ) ); // 设置背景色   
		//wv_content.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255   
		wv_content.getSettings().setLayoutAlgorithm(android.webkit.WebSettings.LayoutAlgorithm.SINGLE_COLUMN);     
		wv_content.loadDataWithBaseURL (null, cssstyle+news.content, "text/html", "utf-8",null);
		
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
