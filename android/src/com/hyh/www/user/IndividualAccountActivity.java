package com.hyh.www.user;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynUpdateListener;
import com.gezitech.entity.User;
import com.gezitech.service.managers.AccountManager;
import com.hyh.www.R;
import com.hyh.www.RegisteredActivity;
import com.hyh.www.ZhuyeActivity;
import com.hyh.www.widget.YMDialog;
import com.hyh.www.widget.YMDialog2;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author xiaobai (个人账户)
 */
public class IndividualAccountActivity extends GezitechActivity implements
		OnClickListener {

	private IndividualAccountActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_title;
	private User account = null;
	private RelativeLayout individualaccount_chongzhi;//充值
	private RelativeLayout individualaccount_tixian; //提现
	private TextView tv_yuenumber;
	private TextView tv_shourunumber;
	private TextView tv_zhichunumber;
	private TextView text_individualaccount_huodongjuan;
	private TextView tv_individualaccount_tixian_balance;
	private RelativeLayout individualaccount_huodongjuan;
	private RelativeLayout individualaccount_detail;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_individual_account);
		Intent intent = _this.getIntent();
		account = (User) intent.getExtras().getSerializable("account");
		_init();
	}

	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(_this.getResources().getString(R.string.person_account));

		individualaccount_chongzhi = (RelativeLayout) findViewById(R.id.individualaccount_chongzhi);
		individualaccount_chongzhi.setOnClickListener(this);
		
		individualaccount_tixian = (RelativeLayout) findViewById(R.id.individualaccount_tixian);
		individualaccount_tixian.setOnClickListener(this);
		
		//账户明细
		individualaccount_detail = (RelativeLayout) findViewById(R.id.individualaccount_detail);
		individualaccount_detail.setOnClickListener( this );
		
		tv_individualaccount_tixian_balance = (TextView) findViewById( R.id.tv_individualaccount_tixian_balance );
		
		
		tv_yuenumber = (TextView) _this.findViewById( R.id.tv_yuenumber );
		tv_shourunumber = (TextView) _this.findViewById( R.id.tv_shourunumber );
		tv_zhichunumber = (TextView) _this.findViewById( R.id.tv_zhichunumber );
		text_individualaccount_huodongjuan = (TextView) _this.findViewById( R.id.text_individualaccount_huodongjuan );
		
		individualaccount_huodongjuan = ( RelativeLayout ) findViewById( R.id.individualaccount_huodongjuan );
		individualaccount_huodongjuan.setOnClickListener( this );
		_initData();
	}
	private void _initData(){		
		tv_yuenumber.setText( account.money+"" );	
		tv_shourunumber.setText( account.earn+"" );	
		tv_zhichunumber.setText( account.pay+"" );
		text_individualaccount_huodongjuan.setText( account.coupon+"" );
		tv_individualaccount_tixian_balance.setText( account.cash+"" );
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {

		case R.id.bt_home_msg:
			finish();
			break;

		case R.id.individualaccount_chongzhi:
			_this.startActivityForResult( new Intent(_this, TopupActivity.class) , 1001);
			
			break;
			
		case R.id.individualaccount_tixian:
			
			/*Intent intent =  new Intent(_this, WithdrawalActivity.class);
			intent.putExtra("cash",  account.cash );
			_this.startActivityForResult( intent , 1002 );*/
			
			final YMDialog2 ymdialog2 = new YMDialog2( _this );
			ymdialog2.setHintMsg("现暂时未开通提现服务。")
			.setHead("提示")
			.setCloseButton( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ymdialog2.dismiss();
				}
			});
			
			break;
		case R.id.individualaccount_huodongjuan:
			final YMDialog ymdialog = new YMDialog( _this );
			ymdialog.setHintMsg("确定把活动劵充值到余额?")
			.setConfigButton( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ymdialog.dismiss();
					spendCoupon();
				}
			}).setCloseButton( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ymdialog.dismiss();
				}
			});
			
			
			break;
		case R.id.individualaccount_detail://账户明细
			
			_this.startActivity( new Intent(_this,AccountDetailActivity.class) );
			
			break;
		default:
			break;
		}
	}
	//活动劵充值
	private void spendCoupon(){
		GezitechAlertDialog.loadDialog( _this );
		AccountManager.getInstance().spendCoupon( new OnAsynUpdateListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				// TODO Auto-generated method stub
				Toast( errorMsg );
				GezitechAlertDialog.closeDialog();
			}
			
			@Override
			public void onUpdateDone(String id) {
				GezitechAlertDialog.closeDialog();
				Toast( id );
				_loadData();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if( data == null ) return;
		String action = data.getAction();
		if( requestCode == 1001 && action.equals("1") ){//支付返回成功刷新界面
			_loadData();
		}
		
	}
	private void _loadData(){
		GezitechAlertDialog.loadDialog( _this );
		AccountManager.getInstance().accountlist( new OnAsynGetOneListener() {
			
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
					account = (User)entity ;
					_initData();
				}
				
			}
		});
	}
}
