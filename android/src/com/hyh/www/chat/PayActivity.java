package com.hyh.www.chat;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.basic.GezitechException;
import com.gezitech.config.Conf;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynInsertListener;
import com.gezitech.contract.GezitechManager_I.OnAsynRequestListener;
import com.gezitech.http.Response;
import com.gezitech.service.managers.ChatManager;
import com.gezitech.service.managers.FriendManager;
import com.gezitech.service.managers.PayManager;
import com.gezitech.service.managers.ShoutManager;
import com.gezitech.service.managers.UserManager;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.gezitech.service.xmpp.Constant;
import com.hyh.www.R;
import com.hyh.www.entity.Bill;
import com.hyh.www.entity.Chat;
import com.hyh.www.entity.ChatContent;
import com.hyh.www.entity.FieldVal;
import com.hyh.www.entity.Friend;
import com.hyh.www.entity.Pay;
import com.hyh.www.pay.BaseHelper;
import com.hyh.www.pay.Constants;
import com.hyh.www.pay.MobileSecurePayer;
import com.hyh.www.pay.ResultChecker;
import com.hyh.www.user.PersonDetailedInformationActivity;
import com.hyh.www.widget.YMDialog;
import com.hyh.www.widget.YMDialog2;
import com.loopj.android.http.RequestParams;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author xiaobai ( 支付列表 )
 */
public class PayActivity extends GezitechActivity {
	private PayActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private RadioGroup rg_pay_list;
	private Button bt_jujue;
	private Button bt_zhifu;
	private int payway = -1;
	private String tradecode;
	private long id;
	private double money;
	private String paytype="";
	final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay);
		Intent intent = _this.getIntent();
		Bundle bundle = intent.getExtras();
		tradecode = intent.getStringExtra("tradecode");
		id = intent.getLongExtra("id", 0);
		money = intent.getDoubleExtra("money", 0);
		//范围价格的支付
		paytype = intent.hasExtra( "paytype" ) ? intent.getStringExtra("paytype") : "";
		msgApi.registerApp( Conf.wechat_pay_app_id );
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.WE_CHAT_PAY_CALLBACK );
		this.registerReceiver(receiver, filter);
		_init();
	}

	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				returnData();
			}
		});

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("支付");

		rg_pay_list = (RadioGroup) findViewById(R.id.rg_pay_list);
		rg_pay_list.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == R.id.rb_yue_pay) { //余额支付0
					payway = 0;
				} else if (checkedId == R.id.rb_online_pay) { //连连支付
					payway = 1;
				}else if( checkedId == R.id.rb_wx_pay ){//微信支付
					payway = 2;
				}
			}
		});
		bt_jujue = (Button) findViewById(R.id.bt_jujue);
		bt_jujue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				returnData();
			}
		});
		bt_zhifu = (Button) findViewById(R.id.bt_zhifu);
		bt_zhifu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ( payway < 0) {
					Toast("选择支付方式");
					return;
				}
				if (payway == 0) {// 余额支付
					GezitechAlertDialog.loadDialog(_this);
					RequestParams params = new RequestParams();
					params.put("id", id);
					params.put("tradecode", tradecode);
					ShoutManager.getInstance().balancepayment(params,
							new OnAsynInsertListener() {

								@Override
								public void OnAsynRequestFail(String errorCode,
										String errorMsg) {
									Toast(errorMsg);
									GezitechAlertDialog.closeDialog();
								}

								@Override
								public void onInsertDone(String id) {
									GezitechAlertDialog.closeDialog();
									final YMDialog2 dialog2 = new YMDialog2( _this );
									BaseHelper.showDialog(_this, "提示",
											"余额支付成功",
											android.R.drawable.ic_dialog_alert, 
											dialog2,
											new OnClickListener() {
												
												@Override
												public void onClick(View v) {
													dialog2.dismiss();
													Intent intent = new Intent();
													intent.setAction("1");
													_this.setResult(1001, intent);
													returnData();
												}
											});
									
									
								}
							});
				} else if (payway == 1 || payway == 2 ) {// 连连支付 or 微信支付

					GezitechAlertDialog.loadDialog(_this);
					PayManager.getInstance().getannouncementdetails( paytype.equals("") || paytype == null ?  "consume" : paytype,
							id+"", money, payway, new OnAsynGetOneListener() {

								@Override
								public void OnAsynRequestFail(String errorCode,
										String errorMsg) {
									Toast(errorMsg);
									GezitechAlertDialog.closeDialog();
								}

								@Override
								public void OnGetOneDone(GezitechEntity_I entity) {
									GezitechAlertDialog.closeDialog();
									Pay pay = (Pay) entity;
									if (!FieldVal.value(pay.sign).equals("")) {

										if( payway == 1 ){//连连支付返回
										
											MobileSecurePayer msp = new MobileSecurePayer();
											boolean bRet = msp.pay(pay.sign, mHandler,
													Constants.RQF_PAY, _this, false);
										}else if( payway == 2 ){//微信支付返回数据处理
											
											
											Response response = new Response( new String( pay.sign  ) );
						    				try {
												JSONObject sign = response.asJSONObject();

												PayReq request = new PayReq();
												request.appId = sign.has("appid") ? sign.getString("appid"):"";
												request.partnerId = sign.has("partnerid") ? sign.getString("partnerid"):"";
												request.prepayId= sign.has("prepayid") ? sign.getString("prepayid"):"";
												request.packageValue = sign.has("package") ? sign.getString("package"):"";
												request.nonceStr= sign.has("noncestr") ? sign.getString("noncestr"):"";
												request.timeStamp= sign.has("timestamp") ? sign.getString("timestamp"):"";
												request.sign= sign.has("sign") ? sign.getString("sign"):"";
												msgApi.sendReq( request );
												
												
											} catch (GezitechException e) {
												e.printStackTrace();
												Toast("支付失败,重新提交!");
												return;
											}catch (JSONException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
												Toast("支付失败,重新提交!");
												return;
											}
											
										}
									} else {
										Toast("支付失败,重新提交!");
									}
								}
							});

				}
			}
		});
	}
	private Handler mHandler = createHandler();

	private Handler createHandler() {
		return new Handler() {
			public void handleMessage(Message msg) {
				String strRet = (String) msg.obj;
				final YMDialog2 dialog2 = new YMDialog2( _this );
				switch (msg.what) {
				case Constants.RQF_PAY: {
					JSONObject objContent = BaseHelper.string2JSON(strRet);
					String retCode = objContent.optString("ret_code");
					String retMsg = objContent.optString("ret_msg");

					// 先判断状态码，状态码为 成功或处理中 的需要 验签
					if (Constants.RET_CODE_SUCCESS.equals(retCode)) {
						ResultChecker resultChecker = new ResultChecker(strRet);
						/*
						 * 支付结果校验，注意校验分为MD5和RSA两种，是根据支付时的签名方式来的。 TODO
						 * ResultChecker
						 * 内部配置了签名key，请注意修改。另外如果是使用的MD5验签，MD5的key建议放到服务器
						 * ，这样会再次请求服务器.
						 * 所以如果使用MD5的可以直接把SDK支付返回结果的校验去掉。前端直接提示SDK的结果就可以了
						 * 。后台会以我们异步通知为依据的。RSA验签可以将RSA_YT_PUBLIC银通的公钥配置到客户端。
						 */
						//int retVal = resultChecker.checkSign();

						//if (retVal == ResultChecker.RESULT_CHECK_SIGN_SUCCEED) {// 验签成功。验签成功后再判断
							// TODO 卡前置模式返回的银行卡绑定协议号，用来下次支付时使用，此处仅作为示例使用。正式接入时去掉

							String resulPay = objContent.optString("result_pay");
							if (Constants.RESULT_PAY_SUCCESS.equalsIgnoreCase(resulPay)) {
								
								BaseHelper.showDialog(_this, "提示",
										"支付成功，交易状态码：" + retCode,
										android.R.drawable.ic_dialog_alert, 
										dialog2,
										new OnClickListener() {
											
											@Override
											public void onClick(View v) {
												dialog2.dismiss();
												Intent intent = new Intent();
												intent.setAction("1");
												_this.setResult( 1001 , intent );
												returnData();
											}
										});
							} else {
								BaseHelper.showDialog(_this, "提示", retMsg
										+ "，交易状态码:" + retCode,
										android.R.drawable.ic_dialog_alert,
										dialog2,
										new OnClickListener() {
											
											@Override
											public void onClick(View v) {
												dialog2.dismiss();
											}
										});
							}
						//} else {
						//	BaseHelper.showDialog(_this, "提示", "您的订单信息已被非法篡改。",
						//			android.R.drawable.ic_dialog_alert);
						//}
					} else if (Constants.RET_CODE_PROCESS.equals(retCode)) {
						String resulPay = objContent.optString("result_pay");
						if (Constants.RESULT_PAY_PROCESSING
								.equalsIgnoreCase(resulPay)) {
							BaseHelper.showDialog(_this, "提示",
									objContent.optString("ret_msg") + "交易状态码："
											+ retCode,
									android.R.drawable.ic_dialog_alert,
									dialog2,
									new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											dialog2.dismiss();
										}
									});
						}

					} else {
						BaseHelper.showDialog(_this, "提示", retMsg + "，交易状态码:"
								+ retCode, android.R.drawable.ic_dialog_alert,
								dialog2,
								new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										dialog2.dismiss();
									}
								});
					}
				}
					break;
				}
				super.handleMessage(msg);
			}
		};

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
	//微信支付的回掉广播
	private BroadcastReceiver receiver = new BroadcastReceiver() {
	
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constant.WE_CHAT_PAY_CALLBACK.equals(action)) {// 返回解接受的消息
				
				String errCode = intent.hasExtra("errCode") ? intent.getStringExtra("errCode") : "";
				if( errCode.equals("0") ){
					Intent intentsucc = new Intent();
					intentsucc.setAction("1");
					_this.setResult( 1001 , intentsucc );
					_this.finish();
				}
			} 
		}
	
	};
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(receiver != null) this.unregisterReceiver(receiver);
	}
}
