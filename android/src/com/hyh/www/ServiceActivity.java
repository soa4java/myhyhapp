package com.hyh.www;

import java.util.ArrayList;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.config.Conf;
import com.gezitech.config.Configuration;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynRequestFailListener;
import com.gezitech.contract.GezitechManager_I.OnAsynUpdateListener;
import com.gezitech.entity.PageList;
import com.gezitech.service.managers.SystemManager;
import com.gezitech.service.managers.UserManager;
import com.gezitech.widget.ExtendViewFlipper;
import com.gezitech.widget.RemoteImageView;
import com.gezitech.widget.ExtendViewFlipper.OnViewFlipperFackFunction;
import com.hyh.www.entity.Adv;
import com.hyh.www.user.SystemMessageActivity;
import com.loopj.android.http.RequestParams;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author xiaobai
 * (服务协议)
 */
public class ServiceActivity extends GezitechActivity implements OnClickListener{
	
	private ServiceActivity _this = this;
	private Button registered;
	private Button registered_account;
	private ExtendViewFlipper pager_slide;
	private LinearLayout pager_control;
	private PageList advList;
	private ImageView[] contorls;
	private Animation in;
	private Animation out;
	private Button bt_my_post;
	private Button bt_send_verification_code;
	private EditText ed_verification_code;
	private EditText ed_phonenumber;
	private EditText ed_enter_password;
	private EditText ed_input_again;
	private Button bt_home_msg;
	private WebView wv_content;
	private int from = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_this.setContentView(R.layout.activity_service );
		Intent intent  = this.getIntent();
		from  = intent.getIntExtra("from", 0);
		_init();
	}
	private void _init() {
		
		bt_my_post = (Button) _this.findViewById( R.id.bt_my_post );
		bt_my_post.setVisibility( View.GONE );
		
		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_this.finish();
			}
		});
		
		TextView tv_title = (TextView) findViewById( R.id.tv_title );
		tv_title.setText( from == 0   ? "服务协议" : "商家服务协议" );
		
		wv_content = ( WebView ) _this.findViewById( R.id.wv_content );
		
		GezitechAlertDialog.loadDialog(_this);
		wv_content.getSettings().setPluginState(PluginState.ON);
		wv_content.getSettings().setJavaScriptEnabled(true);//设置支持脚本  
		wv_content.getSettings().setSupportZoom( false );
		wv_content.getSettings().setBuiltInZoomControls( false );
		wv_content.getSettings().setUseWideViewPort(true);
		wv_content.getSettings().setLoadWithOverviewMode(false);
		wv_content.setInitialScale(2);
		wv_content.setWebViewClient(new WebViewClient(){   
            @Override
            public void onPageFinished(WebView view, String url){
            	GezitechAlertDialog.closeDialog();
                super.onPageFinished(view, url);
            }
        });
		wv_content.loadUrl( Configuration.getScheme()+Configuration.getAppPath()+ ( from  == 0  ? "uploads/reg.htm" : "uploads/bizreg.htm" ) );
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}


}
