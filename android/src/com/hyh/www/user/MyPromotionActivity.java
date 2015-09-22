package com.hyh.www.user;

import java.text.DecimalFormat;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.entity.User;
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
 * @author xiaobai
 * (我的推广)
 */
public class MyPromotionActivity extends GezitechActivity implements OnClickListener {
    
	private MyPromotionActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_mypromotion_yaoqingmanumber;
	private Button btn_mypromotion_congshangjiashouyi;
	private Button btn_mypromotion_conggerenshouyi;
	private User spreadcount;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_promotion);
		Intent intent = _this.getIntent();
		spreadcount = (User) intent.getExtras().getSerializable("spreadcount");
		_init();
	}
    
	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);
		
		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);
		
		TextView tv_title = (TextView) findViewById( R.id.tv_title );
		tv_title.setText( _this.getResources().getString( R.string.my_promotion ) );
		
		//我的邀请码
		tv_mypromotion_yaoqingmanumber = (TextView) findViewById( R.id.tv_mypromotion_yaoqingmanumber );
		tv_mypromotion_yaoqingmanumber.setText( user.inviteCode );
		
		btn_mypromotion_congshangjiashouyi = (Button) findViewById( R.id.btn_mypromotion_congshangjiashouyi );
		btn_mypromotion_congshangjiashouyi.setOnClickListener( this );
		btn_mypromotion_conggerenshouyi = (Button) findViewById( R.id.btn_mypromotion_conggerenshouyi );
		btn_mypromotion_conggerenshouyi.setOnClickListener( this );
		
		//商家
		DecimalFormat df = new DecimalFormat("0.00");
		( (TextView)findViewById( R.id.tv_mypromotion_shangjiamoney ) ).setText( df.format( spreadcount.gotmoney )+"" );
		//个人
		( (TextView)findViewById( R.id.tv_mypromotion_gerenmoney ) ).setText( df.format( spreadcount.invite_money  ) +"" );
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		
		case R.id.bt_home_msg:
			finish();
			break;
			
		case R.id.btn_mypromotion_congshangjiashouyi: //商家记录
			Intent intent = new Intent( _this, IncomeActivity.class );
			intent.putExtra("type", 1 );
			intent.putExtra("title", "从商家收益");
			startActivity( intent );
			break;
		case R.id.btn_mypromotion_conggerenshouyi : //个人记录
			Intent intent1 = new Intent( _this, IncomeActivity.class );
			intent1.putExtra("type", 2 );
			intent1.putExtra("title", "从个人收益");
			startActivity( intent1 );
			break;
		default:
			break;
		}
	}
 
}
