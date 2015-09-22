package com.hyh.www.wxapi;


import com.gezitech.config.Conf;
import com.gezitech.service.xmpp.Constant;
import com.hyh.www.R;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	
    private IWXAPI api;

	private TextView tv_fabuchenggong;

	private TextView tv_content;

	private Button Login_login;
	private WXPayEntryActivity _this = this;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wechat_pay_result);
        
    	api = WXAPIFactory.createWXAPI(this, Conf.wechat_pay_app_id );

        api.handleIntent(getIntent(), this);
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
		this.getWindow().setLayout( dm.widthPixels , dm.heightPixels );
		
		
		tv_fabuchenggong=(TextView) findViewById(R.id.tv_fabuchenggong);
		tv_fabuchenggong.setText("提示");

		tv_content=(TextView) findViewById(R.id.tv_content);
		Login_login=(Button) findViewById(R.id.Login_login);
		
		
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		tv_content=(TextView) findViewById(R.id.tv_content);
		Login_login=(Button) findViewById(R.id.Login_login);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			final String errCode = String.valueOf(resp.errCode);
			String errStr = "微信支付:";
			
			//0成功 -1错误 -2用户取消 
			if( errCode.equals("0") ){
				errStr += "支付成功！";
			}else if( errCode.equals("-1") ){
				errStr += "支付失败！";
			}else if( errCode.equals("-2") ){
				errStr += "用户取消微信支付！";
				
			}
			tv_content.setText( errStr );
			//关闭回掉
			Login_login.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("errCode", errCode );
					intent.setAction( Constant.WE_CHAT_PAY_CALLBACK );
					sendBroadcast( intent );
					_this.finish();
				}
			});
		}
	}
	@Override
	public void onBackPressed() {
		return;
	}
}