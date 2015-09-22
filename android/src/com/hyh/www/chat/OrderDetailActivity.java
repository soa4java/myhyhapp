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
import com.gezitech.basic.GezitechApplication;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynInsertListener;
import com.gezitech.service.GezitechService;
import com.gezitech.service.managers.ChatManager;
import com.gezitech.service.managers.ShoutManager;
import com.gezitech.service.xmpp.MessageSend;
import com.gezitech.service.xmpp.XmppConnectionManager;
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
import com.hyh.www.widget.YMDialog;
import com.loopj.android.http.RequestParams;
/**
 * 
 * @author xiaobai
 * 2014-11-15
 * @todo( 查看订单详情 )
 */
public class OrderDetailActivity extends GezitechActivity {
	private OrderDetailActivity _this = this;
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
	private TextView ed_edit_beizhu;
	private TextView tv_buyerscontact;
	private TextView ed_editbill_jine;
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
	private int from;  //来自哪里  0 来自 个人中心 订单列表  聊天中的订单中心   1 来自聊天中的
	private long curr;
	private long startTime;
	private RelativeLayout rl_shengyu_time;
	private TextView tv_shengyu_time_val;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_detail);
		Intent intent = _this.getIntent();
		Bundle bundle = intent.getExtras();
		from = intent.getIntExtra("from", 0);
		
		billdetail = (Bill) bundle.getSerializable("billdetail");
		billdetail.common_time = 0;
		//判断是否已经链接
		if(  GezitechApplication.connection == null || ( !GezitechApplication.connection.isConnected() ) || (  XmppConnectionManager.getInstance().getConnection() !=null && !XmppConnectionManager.getInstance().getConnection().isConnected() ) ){
			XmppConnectionManager.getInstance().login();
		}
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
				returnData(1007, from == 1 ? 0 : 1 );
			}
		});
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText( "来自“"+ ( FieldVal.value( billdetail.user_nickname ).equals("") ? billdetail.user_name : billdetail.user_nickname ) +"”的付款" );
		tv_number = (TextView) findViewById(R.id.tv_number);
		tv_number.setText( billdetail.tradecode+"" );

		//卖家
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
		tv_buyersname.setText( FieldVal.value( billdetail.user_nickname).equals("") ? billdetail.user_name : billdetail.user_nickname  );
		tv_buyersaddress.setText( FieldVal.value( billdetail.user_address ) );
		tv_buyerscontact.setText( billdetail.user_phone );
		//备注
		ed_edit_beizhu = (TextView) findViewById(R.id.ed_edit_beizhu);
		ed_edit_beizhu.setText( billdetail.notes );
		//金额
		ed_editbill_jine = (TextView) findViewById(R.id.ed_editbill_jine);
		ed_editbill_jine.setText( billdetail.money+"" );

		//订单状态
		editbill_pay = (RelativeLayout) findViewById(R.id.editbill_pay);
		//订单状态值
		tv_editbill_choose = (TextView) findViewById(R.id.tv_editbill_choose);

		//图片展示
		rl_releaseHyh_photo = (RelativeLayout) findViewById(R.id.rl_releaseHyh_photo);	
		ll_action_box = (LinearLayout) findViewById( R.id.ll_action_box );
		bt_jujue = (Button)findViewById( R.id.bt_jujue );
		bt_zhifu = (Button)findViewById( R.id.bt_zhifu );
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
		
		//剩余时间
		rl_shengyu_time = (RelativeLayout) findViewById(R.id.rl_shengyu_time);	
		tv_shengyu_time_val = (TextView) findViewById(R.id.tv_shengyu_time_val );		
		stateSwitch();
	}
	private void stateSwitch(){
		ll_action_box.setVisibility( View.GONE );
		rl_shengyu_time.setVisibility( View.GONE );
		String stateStr = "";
		switch (billdetail.state) {
		case 0:
			stateStr = "未付款";
			//if (user.isbusiness > 0) {
			if ( user.id == billdetail.bid ) {
				
			} else {
				ll_action_box.setVisibility( View.VISIBLE );
				bt_jujue.setVisibility( View.GONE );
				bt_zhifu.setText("去付款");
				bt_zhifu.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {// click事件的逻辑
						//跳转到支付界面
						Intent intent = new Intent(_this,PayActivity.class);
						
						intent.putExtra("id", billdetail.id );
						intent.putExtra("tradecode", billdetail.tradecode+"" );
						intent.putExtra("money", billdetail.money);
						_this.startActivityForResult(intent, 1001 );
						
						_this.overridePendingTransition(R.anim.out_to_down, R.anim.exit_anim);
					}
				});
			}
			break;
		case 1:
			ll_action_box.setVisibility( View.VISIBLE );
			stateStr = "已付款";
			//if (user.isbusiness > 0) {
			if( user.id == billdetail.bid ){
				bt_zhifu.setText("确认收款");
				bt_zhifu.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {// click事件的逻辑
						
						final YMDialog ymd = new YMDialog( _this );
						ymd.setHintMsg("确定收款?");
						ymd.setConfigButton( new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								ymd.dismiss();
								RequestParams params = new RequestParams();
								params.put("id", billdetail.id );
								GezitechAlertDialog.loadDialog(_this);
								ShoutManager.getInstance().certaincollect(params, new OnAsynInsertListener() {
									
									@Override
									public void OnAsynRequestFail(String errorCode, String errorMsg) {
										GezitechAlertDialog.closeDialog();
										_this.Toast( errorMsg );
									}
									@Override
									public void onInsertDone(String id) {
										GezitechAlertDialog.closeDialog();
										billdetail.state = 2;	
										billdetail.surplustime = System.currentTimeMillis()/1000+7*24*60*60;
										/*if( from == 1 ){//来自对话的确定收款
											returnData( 1009,1 );
										}*/
										stateSwitch();
										String body4 = "{\"id\":"+billdetail.id+",\"tradecode\":\""+billdetail.tradecode+"\",\"notes\":\""+billdetail.notes+"\",\"money\":\""+billdetail.money+"\",\"paytime\":"+billdetail.paytime+",\"activechecktime\":"+billdetail.activechecktime+"}";
										//"确定收款";
										GezitechService.sendMessage(billdetail.uid, 7, body4 , billdetail.sid );
									}
								});
							}
						})
						.setCloseButton( new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								ymd.dismiss();
							}
						});
						
					}
				});
				bt_jujue.setText("取消收款");
				bt_jujue.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {// click事件的逻辑
						final YMDialog ymd = new YMDialog( _this );
						ymd.setHintMsg("确定取消收款?");
						ymd.setConfigButton( new OnClickListener() {
							@Override
							public void onClick(View v) {
								ymd.dismiss();
								RequestParams params = new RequestParams();
								params.put("id", billdetail.id );
								GezitechAlertDialog.loadDialog(_this);
								ShoutManager.getInstance().storecancelcollect(params, new OnAsynInsertListener() {
									
									@Override
									public void OnAsynRequestFail(String errorCode, String errorMsg) {
										GezitechAlertDialog.closeDialog();
										_this.Toast( errorMsg );
									}
									@Override
									public void onInsertDone(String id) {
										GezitechAlertDialog.closeDialog();
										//成功后刷新当前item
										billdetail.state = 4;
										stateSwitch();
										String body4 = "{\"id\":"+billdetail.id+",\"tradecode\":\""+billdetail.tradecode+"\",\"notes\":\""+billdetail.notes+"\",\"money\":\""+billdetail.money+"\",\"paytime\":"+billdetail.paytime+",\"activechecktime\":"+billdetail.activechecktime+"}";
										//"商家拒绝收款"
										GezitechService.sendMessage(billdetail.uid, 12, body4, billdetail.sid  );
									}
								});

							}
						})
						.setCloseButton( new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								ymd.dismiss();
							}
						});
					}
				});

			} else {
				bt_zhifu.setVisibility( View.GONE ); 
				bt_jujue.setText("撤销付款");
				bt_jujue.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {// click事件的逻辑
						final YMDialog ymd = new YMDialog( _this );
						ymd.setHintMsg("确定撤销付款?");
						ymd.setConfigButton( new OnClickListener() {
							@Override
							public void onClick(View v) {
								ymd.dismiss();
								RequestParams params = new RequestParams();
								params.put("id", billdetail.id );
								GezitechAlertDialog.loadDialog(_this);
								ShoutManager.getInstance().usercancelpay(params, new OnAsynInsertListener() {
									
									@Override
									public void OnAsynRequestFail(String errorCode, String errorMsg) {
										GezitechAlertDialog.closeDialog();
										_this.Toast( errorMsg );
									}
									@Override
									public void onInsertDone(String id) {
										GezitechAlertDialog.closeDialog();
										//成功后刷新当前item
										billdetail.state = 3;
										stateSwitch();
										String body4 = "{\"id\":"+billdetail.id+",\"tradecode\":\""+billdetail.tradecode+"\",\"notes\":\""+billdetail.notes+"\",\"money\":\""+billdetail.money+"\",\"paytime\":"+billdetail.paytime+",\"activechecktime\":"+billdetail.activechecktime+"}";
										//"用户撤销付款"
										GezitechService.sendMessage(billdetail.bid, 11, body4, billdetail.sid  );
									}
								});
							}
						})
						.setCloseButton( new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								ymd.dismiss();
							}
						});
					}
				});

			}
			// 有效确认时间
			billdetail.common_time = billdetail.paytime + billdetail.activechecktime;
			rl_shengyu_time.setVisibility( View.VISIBLE );
			break;
		case 2:
			stateStr = "服务中";
			//if (user.isbusiness > 0) {
			if( user.id == billdetail.bid  ){	
				
			} else {
				ll_action_box.setVisibility( View.VISIBLE );
				bt_zhifu.setText("确认服务");
				bt_zhifu.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {// click事件的逻辑
						final YMDialog ymd = new YMDialog( _this );
						ymd.setHintMsg("确定确认服务?");
						ymd.setConfigButton( new OnClickListener() {
							@Override
							public void onClick(View v) {
								ymd.dismiss();
								RequestParams params = new RequestParams();
								params.put("id", billdetail.id );
								GezitechAlertDialog.loadDialog(_this);
								ShoutManager.getInstance().userconfirmservice(params, new OnAsynInsertListener() {
									
									@Override
									public void OnAsynRequestFail(String errorCode, String errorMsg) {
										GezitechAlertDialog.closeDialog();
										_this.Toast( errorMsg );
									}
									@Override
									public void onInsertDone(String id) {
										GezitechAlertDialog.closeDialog();
										//成功后刷新当前item
										billdetail.state = 5;
										stateSwitch();
										String body4 = "{\"id\":"+billdetail.id+",\"tradecode\":\""+billdetail.tradecode+"\",\"notes\":\""+billdetail.notes+"\",\"money\":\""+billdetail.money+"\",\"paytime\":"+billdetail.paytime+",\"activechecktime\":"+billdetail.activechecktime+"}";
										//用户确认服务
										GezitechService.sendMessage(billdetail.bid, 14, body4, billdetail.sid  );
									}
								});
							}
						})
						.setCloseButton( new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								ymd.dismiss();
							}
						});
					}
				});
				bt_jujue.setText("申请退款");
				bt_jujue.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {// click事件的逻辑
						final YMDialog ymd = new YMDialog( _this );
						ymd.setHintMsg("确定申请退款?");
						ymd.setConfigButton( new OnClickListener() {
							@Override
							public void onClick(View v) {
								ymd.dismiss();
								RequestParams params = new RequestParams();
								params.put("id", billdetail.id );
								GezitechAlertDialog.loadDialog(_this);
								ShoutManager.getInstance().userapplyrefund(params, new OnAsynInsertListener() {
									
									@Override
									public void OnAsynRequestFail(String errorCode, String errorMsg) {
										GezitechAlertDialog.closeDialog();
										_this.Toast( errorMsg );
									}
									@Override
									public void onInsertDone(String id) {
										GezitechAlertDialog.closeDialog();
										//成功后刷新当前item
										billdetail.state = 6;
										stateSwitch();
										String body4 = "{\"id\":"+billdetail.id+",\"tradecode\":\""+billdetail.tradecode+"\",\"notes\":\""+billdetail.notes+"\",\"money\":\""+billdetail.money+"\",\"paytime\":"+billdetail.paytime+",\"activechecktime\":"+billdetail.activechecktime+"}";
										//用户申请退款;
										GezitechService.sendMessage(billdetail.bid, 13, body4 , billdetail.sid );
									}
								});
							}
						})
						.setCloseButton( new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								ymd.dismiss();
							}
						});
					}
				});

			}
			// 剩余7天时间
			billdetail.common_time = billdetail.surplustime;
			rl_shengyu_time.setVisibility( View.VISIBLE );
			break;
		case 3:		
			stateStr = "已撤销";  // 已撤销
			break;
		case 4:
			stateStr = "商家拒绝收款"; // 商家拒绝收款
			break;
		case 5:
			stateStr = "已确认服务"; // 已确认服务
			break;
		case 6:
			stateStr = "退款中"; // 6退款请求
			break;
		case 7:
			stateStr = "已同意退款"; // 7已同意退款
			break;
		case 8:
			stateStr = "拒绝退款"; // 8请求已作废
			break;
		}
		tv_editbill_choose.setText( stateStr );
		if( billdetail.common_time > 0  && ( billdetail.state == 1 || billdetail.state == 2 )  ){
			setTiem();
			mHandler.postDelayed( countdown, 1000);
		}
		
	}
	private void setTiem(){
		
		curr = System.currentTimeMillis()/1000;
		startTime = billdetail.common_time;
		if (startTime > curr) {
			long dtime = startTime - curr;
			tv_shengyu_time_val.setText(getDateStr(dtime));
			tv_shengyu_time_val.setTextColor(Color.parseColor("#ff340c"));
			
		} else {
			tv_shengyu_time_val.setText("已过期");
			tv_shengyu_time_val.setTextColor(Color.parseColor("#949494"));
			tv_editbill_choose.setText( billdetail.surplustime > 0 ? "已确认服务" : "已撤销" );
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
				billdetail.state = 1;
				returnData( 1007, 1 );
			}
			break;
		}

	}
	//1007  1009 
	private void returnData( int requestCode, int action  ) {
		if( action == 1 ){
			Intent intent = new Intent();
			intent.setAction("1");
			Bundle bundle = new Bundle();
			bundle.putSerializable("bill", billdetail);
			intent.putExtras( bundle );
			_this.setResult( requestCode , intent);
		}
		_this.finish();
		overridePendingTransition(R.anim.in_anim, R.anim.in_from_down);
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		returnData(1007, from == 1 ? 0 : 1 );
	}
	private String getDateStr(long time) {
		long d = time / (24 * 3600);
		long h = time % (24 * 3600) / 3600;
		long m = time % (24 * 3600) % 3600  / 60;
		long s = time % (24 * 3600) % 3600  % 60;
		return ( d<=0 ? "" : d+"天" )  + (h < 10 ? "0" + h : h) + "时" + (m < 10 ? "0" + m : m) + "分"
				+ (s < 10 ? "0" + s : s)+"秒";
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
