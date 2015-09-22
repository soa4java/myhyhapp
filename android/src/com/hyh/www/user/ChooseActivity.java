package com.hyh.www.user;

import com.gezitech.basic.GezitechActivity;
import com.hyh.www.R;
import com.hyh.www.RegisteredActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
/**
 * 
 * @author xiaobai (选择模版)
 */
public class ChooseActivity extends GezitechActivity implements OnClickListener {
   
	private ChooseActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose);
		_init();
	}
	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setText(_this.getResources().getString(R.string.tianjiamoban));
		bt_my_post.setOnClickListener(this);

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(_this.getResources().getString(R.string.xuanzemoban));
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		 switch (arg0.getId()) {

			case R.id.bt_home_msg:
				finish();
				break;
				
			case R.id.bt_my_post:
				 _this.startActivity(new Intent(_this, AddActivity.class));
				break;
		default:
			break;
		}
	}


}
