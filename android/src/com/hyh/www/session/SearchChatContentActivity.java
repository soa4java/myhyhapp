package com.hyh.www.session;

import java.util.ArrayList;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.service.managers.AccountManager;
import com.gezitech.service.managers.ChatManager;
import com.gezitech.widget.MyListView;
import com.gezitech.widget.MyListView.OnMoreListener;
import com.gezitech.widget.MyListView.OnRefreshListener;
import com.hyh.www.R;
import com.hyh.www.adapter.ChatContentAdapter;
import com.hyh.www.entity.ChatContent;
import com.hyh.www.entity.Friend;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
/**
 * 
 * @author xiaobai (搜索聊天记录)
 */
public class SearchChatContentActivity extends GezitechActivity implements OnClickListener {
	
	private SearchChatContentActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_title;
	private EditText ed_newFriend_phone_account;
	private Button btn_newFriend_phone_account;
	private MyListView list_view;
	private Friend friend;
	private int page = 1,pageSize = 10;
	private ChatContentAdapter chatContentAdapter;
	protected String content;
	private boolean isLoading = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_chat_content);		
		Bundle bundle = _this.getIntent().getExtras();
		friend = (Friend) bundle.getSerializable("friend");
		_init();
	}
	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

	    tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("搜索聊天记录");
		
		//搜索
		ed_newFriend_phone_account = ( EditText ) findViewById( R.id.ed_newFriend_phone_account );
		btn_newFriend_phone_account = (Button) findViewById( R.id.btn_newFriend_phone_account );
		btn_newFriend_phone_account.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				content = ed_newFriend_phone_account.getText().toString().trim();
				if( content.equals("") ){
					Toast("输入搜索聊天内容");
					return;
				}
				GezitechAlertDialog.loadDialog( _this );
				loadData();
			}
		});
		list_view = (MyListView) findViewById( R.id.list_view );
		
		chatContentAdapter = new ChatContentAdapter(_this,list_view, friend.head, friend.uid );

		list_view.setAdapter(chatContentAdapter);

		list_view.footerShowState(2);
		list_view.setOnMoreListener( new OnMoreListener() {
			
			@Override
			public void OnMore() {
			}
			@Override
			public void OnMore(int firstVisiableItem, int displayItemCount, int count,
					int lastVisibleItem) {
				Log.v( "haha",firstVisiableItem+"="+displayItemCount+"="+count+"="+lastVisibleItem );
				if( !isLoading  && (lastVisibleItem+1 == count) && count-2 > 0 && ( (count-2)%pageSize == 0 && (count-2)/pageSize>0 )  ){
					isLoading = true;
					list_view.footerShowState( 0 );
					page++;
					loadData();
				}
				
			}
		});
	}
	//加载数据
	public void loadData(){
		
		ChatManager.getInstance().getchatrecord(page, pageSize,friend.fid, content, new OnAsynGetListListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				GezitechAlertDialog.closeDialog();
				list_view.footerShowState( 2 );	
				if( errorCode.equals("-1") ){
					_this.Toast( errorMsg );
				}
				isLoading = false;
			}
			
			@Override
			public void OnGetListDone(ArrayList<GezitechEntity_I> list) {
				GezitechAlertDialog.closeDialog();
				list_view.footerShowState( 2 );	
				isLoading = false;
				chatContentAdapter.addList(list, false);
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
		}
	}

}
