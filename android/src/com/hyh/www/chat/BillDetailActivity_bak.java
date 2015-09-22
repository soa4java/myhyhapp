package com.hyh.www.chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynInsertListener;
import com.gezitech.service.managers.ChatManager;
import com.gezitech.service.managers.ShoutManager;
import com.gezitech.util.CalendarUtil;
import com.gezitech.util.DateUtils;
import com.gezitech.util.IOUtil;
import com.gezitech.util.ImageUtil;
import com.gezitech.util.StringUtil;
import com.gezitech.widget.OptionDialog;
import com.gezitech.widget.RemoteImageView;
import com.gezitech.widget.OptionDialog.DialogSelectDataCallBack;
import com.gezitech.widget.OptionDialog.ItemType;
import com.hyh.www.R;
import com.hyh.www.entity.Bill;
import com.hyh.www.entity.ChatContent;
import com.hyh.www.entity.FieldVal;
import com.hyh.www.entity.Friend;
import com.hyh.www.entity.Validtimelist;
import com.hyh.www.widget.ImageShow;
import com.loopj.android.http.RequestParams;
/**
 * 
 * @author xiaobai
 * 2014-11-15
 * @todo(查看账单详情 / 查看订单详情 )
 */
public class BillDetailActivity_bak extends GezitechActivity {
	private BillDetailActivity_bak _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_number;
	private TextView tv_merchantsname;
	private TextView tv_merchantsaddress;
	private TextView tv_merchantscontact;
	private TextView tv_buyersname;
	private RelativeLayout rl_releaseHyh_photo;
	private RemoteImageView iv_releaseHyh_photo;
	private ImageView play_del_xx;
	private Button bt_editbill_send;
	private TextView tv_buyersaddress;
	private EditText ed_edit_beizhu;
	private TextView tv_buyerscontact;
	private EditText ed_editbill_jine;
	private RelativeLayout editbill_pay;
	private TextView tv_editbill_choose;
	private ImageButton iv_camera;
	private String litpicUrl = "";
	private Bill billdetail;
	private ImageView iv_editbill_pay;
	private TextView tv_editbill_pay;
	private Handler mHandler = new Handler();
	private LinearLayout ll_action_box;
	private int type;
	private Button bt_jujue;
	private Button bt_zhifu;
	private int from;
	private long curr;
	private long startTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bill_detail_bak);
		Intent intent = _this.getIntent();
		Bundle bundle = intent.getExtras();
		
		billdetail = (Bill) bundle.getSerializable("billdetail");
		
		//0 是来自账单   1 来自创建付款
		from = intent.getIntExtra("from", 0);
		//状态类型
		type = intent.getIntExtra("type", 0);
		
		_init();
	}

	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility( View.GONE );

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				returnData();
			}
		});

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		if( from == 0 ){
			tv_title.setText( "来自“"+billdetail.company_name+"”的账单" );
		}else{
			tv_title.setText( "来自“"+billdetail.user_name+"”的付款" );
		}
		tv_number = (TextView) findViewById(R.id.tv_number);
		tv_number.setText( billdetail.tradecode+"" );

		tv_merchantsname = (TextView) findViewById(R.id.tv_merchantsname);
		tv_merchantsname.setText( FieldVal.value( billdetail.company_name ) );

		tv_merchantsaddress = (TextView) findViewById(R.id.tv_merchantsaddress);
		tv_merchantsaddress.setText(billdetail.company_address);

		tv_merchantscontact = (TextView) findViewById(R.id.tv_merchantscontact);
		tv_merchantscontact.setText(billdetail.company_tel);

		// 买家
		tv_buyersname = (TextView) findViewById(R.id.tv_buyersname);
		tv_buyersaddress = (TextView) findViewById(R.id.tv_buyersaddress);
		tv_buyerscontact = (TextView) findViewById(R.id.tv_buyerscontact);
		

		tv_buyersname.setText( FieldVal.value( billdetail.user_name ) );
		tv_buyersaddress.setText( FieldVal.value( billdetail.user_address ) );
		tv_buyerscontact.setText( billdetail.user_phone );

		
		
		ed_edit_beizhu = (EditText) findViewById(R.id.ed_edit_beizhu);
		ed_edit_beizhu.setText( billdetail.notes );
		ed_edit_beizhu.setEnabled( false );
	
		
		ed_editbill_jine = (EditText) findViewById(R.id.ed_editbill_jine);
		
		ed_editbill_jine.setText( billdetail.money+"" );
		ed_editbill_jine.setEnabled( false );

		editbill_pay = (RelativeLayout) findViewById(R.id.editbill_pay);
		tv_editbill_choose = (TextView) findViewById(R.id.tv_editbill_choose);
		iv_editbill_pay = (ImageView) findViewById( R.id.iv_editbill_pay );
		iv_editbill_pay.setVisibility( View.GONE );
		tv_editbill_pay = (TextView) findViewById( R.id.tv_editbill_pay );
		tv_editbill_pay.setText( "剩余时间" );

		rl_releaseHyh_photo = (RelativeLayout) findViewById(R.id.rl_releaseHyh_photo);	
		ll_action_box = (LinearLayout) findViewById( R.id.ll_action_box );
		if( !billdetail.litpicUrl.equals("") ){
			rl_releaseHyh_photo.setVisibility( View.VISIBLE );
			iv_releaseHyh_photo = (RemoteImageView) findViewById(R.id.iv_releaseHyh_photo);
			iv_releaseHyh_photo.setImageUrl( billdetail.litpicUrl );
			iv_releaseHyh_photo.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//画廊展示图片
					final String[] images = new String[1];
					String[] pic = billdetail.litpicUrl.split("src=");
					images[0] = StringUtil.stringDecode( pic[1] );
					ImageShow.jumpDisplayPic(images, 0, _this);
				}
			});
		}
		if( billdetail.state == 0 || billdetail.state == 1 ){
			setTiem();
			mHandler.postDelayed( countdown, 1000);
		}else if( billdetail.state == 2 ){
			if( from == 0 ){
				tv_editbill_choose.setText("已付款");
			}else if( from == 1 ){
				tv_editbill_choose.setText("已确定收款");
			}
		}
		//没有付款 或者 没有确定收款  显示 
		//0,2 是账单  1,2是付款
		if( from == 0 ){//来自账单
			if( billdetail.bid != user.id && billdetail.state == 0 && startTime > curr ){
				ll_action_box.setVisibility( View.VISIBLE );
			}else{
				ll_action_box.setVisibility( View.GONE );
			}
		}else if( from == 1 ){//来自付款
			if( billdetail.bid == user.id && billdetail.state == 1 && startTime > curr ){
				ll_action_box.setVisibility( View.VISIBLE );
			}else{
				ll_action_box.setVisibility( View.GONE );
			}
		}
			
		bt_jujue = (Button)findViewById( R.id.bt_jujue );

		bt_zhifu = (Button)findViewById( R.id.bt_zhifu );
		if( from == 1 ){
			bt_jujue.setText( "拒绝收款" );
			bt_zhifu.setText( "确定收款" );
		}
		//拒绝支付
		bt_jujue.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				returnData();
			}
		});
		//支付
		bt_zhifu.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if( from == 0 ){//账单
				
					//跳转到支付界面
					Intent intent = new Intent(_this,PayActivity.class);
					
					intent.putExtra("id", billdetail.id );
					intent.putExtra("tradecode", billdetail.tradecode );
					intent.putExtra("money", billdetail.money);
					_this.startActivityForResult(intent, 1001 );
					
					_this.overridePendingTransition(R.anim.out_to_down, R.anim.exit_anim);
				}else if( from == 1 ){//付款  确定收款
					 RequestParams params = new RequestParams();
					 params.put("id", billdetail.id );
					 GezitechAlertDialog.loadDialog( _this );
					 ShoutManager.getInstance().certaincollect(params,  new OnAsynInsertListener() {
						
						@Override
						public void OnAsynRequestFail(String errorCode, String errorMsg) {
							// TODO Auto-generated method stub
							GezitechAlertDialog.closeDialog();
							Toast( errorMsg );
						}
						
						@Override
						public void onInsertDone(String id) {
							// TODO Auto-generated method stub
							GezitechAlertDialog.closeDialog();
							Intent intent = new Intent();
							Bundle bundle = new Bundle();
							bundle.putSerializable("bill", billdetail);
							intent.putExtras( bundle );
							_this.setResult( 1009 , intent);
							returnData();
						}
					});
					
				}
			}
		});
	}
	private void setTiem(){
		curr = System.currentTimeMillis();
		startTime = billdetail.activetime * 1000 + billdetail.ctime * 1000;
		if (startTime > curr) {
			long dtime = startTime - curr;
			tv_editbill_choose.setText(getDateStr(dtime / 1000));
			tv_editbill_choose.setTextColor(Color.parseColor("#ff340c"));
			
		} else {
			tv_editbill_choose.setText("已过期");
			tv_editbill_choose.setTextColor(Color.parseColor("#949494"));
			ll_action_box.setVisibility( View.GONE );
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null)
			return;
		String action = data.getAction();
		switch (requestCode) {
		case 1001:// 如果是未付款 要有去付款的返回
			if(  action.equals("1") ){
				Intent intent = new Intent();
				intent.setAction("1");
				Bundle bundle = new Bundle();
				bundle.putSerializable("bill", billdetail);
				intent.putExtras( bundle );
				_this.setResult( 1007 , intent);
				returnData();
			}
			break;
		}

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
	private String getDateStr(long time) {
		long h = time / 3600;
		long m = (time % 3600) / 60;
		long s = (time % 3600) % 60;
		return (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":"
				+ (s < 10 ? "0" + s : s);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mHandler.removeCallbacks( countdown );
	}
	private Runnable  countdown = new Runnable()
	{
		public void run()
		{
			setTiem();
			
			mHandler.postDelayed(countdown, 1000);
		}
	};

}
