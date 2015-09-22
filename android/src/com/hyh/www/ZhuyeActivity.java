package com.hyh.www;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.config.Conf;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetJsonObjectListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.entity.User;
import com.gezitech.service.GezitechService;
import com.gezitech.service.GezitechService.CallBDLocation;
import com.gezitech.service.lbs.AppUtils;
import com.gezitech.service.managers.ChatManager;
import com.gezitech.service.managers.NearManager;
import com.gezitech.service.managers.SystemManager;
import com.gezitech.service.xmpp.Constant;
import com.gezitech.service.xmpp.IMChatService;
import com.gezitech.service.xmpp.LoginTask;
import com.gezitech.service.xmpp.XmppConnectionManager;
import com.gezitech.util.ToastMakeText;
import com.hyh.www.entity.ChatContent;
import com.hyh.www.entity.NearHintMsg;
import com.hyh.www.entity.News;
import com.hyh.www.home.HomeFramgent;
import com.hyh.www.nearby.NearbyFramgent;
import com.hyh.www.session.SessionFramgent;
import com.hyh.www.user.PersonFramgent;
import com.hyh.www.user.SystemMessageDetailActivity;
import com.igexin.sdk.PushManager;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author xiaobai 2014-10-14
 * @todo( fragment组装主界面 )
 */
public class ZhuyeActivity extends FragmentActivity implements OnClickListener {
	private RadioGroup group;
	private RadioButton home, person, session;
	private FragmentManager fragmentManager;
	private Fragment[] mFragments;
	private HomeFramgent fghome;
	private SessionFramgent fsession;
	private PersonFramgent fperson;
	//private User user;
	private LinearLayout ll_unreadcount;
	private TextView tv_unreadcount;
	private String flag_global;
	private RadioButton nearby;
	private NearbyFramgent fNearby;
	private LinearLayout ll_nearbycount;
	private LinearLayout ll_like_comment_count;
	private TextView tv_like_comment_count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GezitechService.getInstance().appendActivity(this);
		setContentView(R.layout.activity_zhuye);

		//user = GezitechService.getInstance().getCurrentLoginUser(this);
		
		home = (RadioButton) findViewById(R.id.home);
		session = (RadioButton) findViewById(R.id.session);
		person = (RadioButton) findViewById(R.id.person);
		group = (RadioGroup) findViewById(R.id.zhuye_group);
		nearby = (RadioButton) findViewById(R.id.nearby);
		
		//初始化个推推送
		PushManager.getInstance().initialize(this.getApplicationContext());
		
		//提示消息
		ll_unreadcount = (LinearLayout) findViewById( R.id.ll_unreadcount );
		tv_unreadcount = (TextView) findViewById( R.id.tv_unreadcount );
		//附近人有新消息提示 只显示红点
		ll_nearbycount = (LinearLayout) findViewById( R.id.ll_nearbycount );
		//喜欢 和 评论的提示条数 
		ll_like_comment_count = (LinearLayout) findViewById( R.id.ll_like_comment_count);
		tv_like_comment_count = (TextView) findViewById(R.id.tv_like_comment_count);
		//计算未读的消息
		unreadcount();
		//计算 喜欢和 评论的未读消息
		like_comment_count(tv_like_comment_count,ll_like_comment_count);

		home.setOnClickListener(this);
		session.setOnClickListener(this);
		person.setOnClickListener(this);
		nearby.setOnClickListener( this );
		fragmentManager = getSupportFragmentManager();

		mFragments = new Fragment[4];
		fghome = HomeFramgent.newInstance();
		fsession = SessionFramgent.newInstance();
		fperson = PersonFramgent.newInstance();
		fNearby = NearbyFramgent.newInstance();

		mFragments[0] = fghome;
		mFragments[1] = null;
		mFragments[2] = null;
		mFragments[3] = null;
		fragmentManager.beginTransaction().add(R.id.zhuye_content, fghome)
				.show(fghome).commit();

		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.NEW_MESSAGE_ACTION );
		filter.addAction(Constant.LIKE_COMMENT_ACTION );
		registerReceiver(receiver, filter);
				
		_jumpActivity( this.getIntent() );
		
		setSelectFragment();
		
	}
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
		_jumpActivity( intent );
		setSelectFragment();
	}
	private void _jumpActivity( Intent intent ){
		flag_global = intent.getStringExtra("flag");
		String title = intent.getStringExtra("title");
		String key_type = intent.getStringExtra("key_type");
		String key_value = intent.getStringExtra("key_value");
		//$key_type  1打开活动，2打开网页
	    //$key_value  对应活动id，对应网页链接
		if( flag_global !=null && flag_global.equals("notification") && title != null && !title.equals("") && key_value!=null && !key_value.equals("") && key_type !=null && !key_type.equals("")  ){
		

			if( key_type.equals("1") ){//打开系统消息
				
				loadNewsDetail( Long.parseLong( key_value ) );
				
			}
		}
	}
	//加载新闻的详情
	private void loadNewsDetail( long id ){
		GezitechAlertDialog.loadDialog( this );
		SystemManager.getInstance().getannouncementdetails( id ,  new OnAsynGetOneListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				// TODO Auto-generated method stub
				GezitechAlertDialog.closeDialog();
				new ToastMakeText( ZhuyeActivity.this ).Toast( errorMsg );
			}
			
			@Override
			public void OnGetOneDone(GezitechEntity_I entity) {
				// TODO Auto-generated method stub
				GezitechAlertDialog.closeDialog();
				if( entity != null ){
					Intent intent = new Intent( ZhuyeActivity.this, SystemMessageDetailActivity.class );
					
					Bundle bundle = new Bundle();
					
					bundle.putSerializable("news", (News)entity );
					intent.putExtras( bundle );
					
					ZhuyeActivity.this.startActivity( intent );
					
				}
				
			}
		});
	}

	//选中
	private void setSelectFragment(){
		 if (flag_global != null && flag_global.equals("session")) {
		    	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		    	if (mFragments[1] != null) {
					fragmentTransaction.show(mFragments[1]);
				} else {
					fragmentTransaction.add(R.id.zhuye_content, fsession).show(
							fsession);
					mFragments[1] = fsession;
				}
				fragmentTransaction.commit();
				session.setChecked( true );
		    }
	}
	
	@Override
	public void onClick(View arg0) {
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		for (int i = 0; i < mFragments.length; i++) {
			if (mFragments[i] != null) {
				fragmentTransaction.hide(mFragments[i]);
			}
		}
		switch (arg0.getId()) {
		case R.id.home:

			if (mFragments[0] != null) {
				fragmentTransaction.show(mFragments[0]);
			} else {
				fragmentTransaction.add(R.id.zhuye_content, fghome)
						.show(fghome);
				mFragments[0] = fghome;
			}
			fragmentTransaction.commit();

			break;

		case R.id.session:
			if (mFragments[1] != null) {
				fragmentTransaction.show(mFragments[1]);
			} else {
				fragmentTransaction.add(R.id.zhuye_content, fsession).show(
						fsession);
				mFragments[1] = fsession;
			}
			fragmentTransaction.commit();
			break;

		case R.id.person:
			if (mFragments[2] != null) {
				fragmentTransaction.show(mFragments[2]);
			} else {
				fragmentTransaction.add(R.id.zhuye_content, fperson).show(
						fperson);
				mFragments[2] = fperson;
			}
			fragmentTransaction.commit();
			break;
		case R.id.nearby:
			if (mFragments[3] != null) {
				fragmentTransaction.show(mFragments[3]);
			} else {
				fragmentTransaction.add(R.id.zhuye_content, fNearby).show(
						fNearby);
				mFragments[3] = fNearby;
			}
			fragmentTransaction.commit();
			break;

		default:
			break;
		}
	}
	public void onResume() {
		super.onResume();
		// umeng统计
		MobclickAgent.onResume(this);
	//	if ( !GezitechApplication.isActive ) {  
			//app 从后台唤醒，进入前台  
		 //   GezitechApplication.isActive = true;  
		    //判断xmpp是否链接
		    User user = GezitechService.getInstance().getCurrentLoginUser( this );
		    if( user != null ){ 
		    	if(  GezitechApplication.connection == null || ( !GezitechApplication.connection.isConnected() ) || (  XmppConnectionManager.getInstance().getConnection() !=null && !XmppConnectionManager.getInstance().getConnection().isConnected() ) ){
					XmppConnectionManager.getInstance().login();
				}
		    	
		    }
			/*//是否重新启动app
			if( GezitechApplication.runtime>0 && (System.currentTimeMillis() - GezitechApplication.runtime) > 30*60*1000  ){
				Intent intent = new Intent(this, WelcomeActivity.class);		
				startActivity( intent );
				GezitechService.getInstance().exitApp( this );
			}*/
	//	}  
		    //过半个小时定位一次
		    long currTime = System.currentTimeMillis();
		    long LBSCtime = GezitechApplication.systemSp.getLong("LBSCtime", currTime );
		    if ( AppUtils.isAppOnForeground()  &&  (currTime-LBSCtime) > 35*60 ) {     
		    	
		    	GezitechService.getInstance().longitude();    
		    
		    }
		    getNewNearByCount();
	}
	@Override
	protected void onResumeFragments() {
		// TODO Auto-generated method stub
		super.onResumeFragments();
	}
	@Override
	protected void onPostResume() {
		// TODO Auto-generated method stub
		super.onPostResume();
	}
	public void onPause() {
		super.onPause();
		// umeng统计
		MobclickAgent.onPause(this);
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
/*		if (!AppUtils.isAppOnForeground()) {  
            //app 进入后台  
			//全局变量isActive = false 记录当前已经进入后台  
			GezitechApplication.isActive = false;   
			//GezitechApplication.runtime = System.currentTimeMillis();   
		}*/
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
		fghome.destroyThread();
	}

	// 新消息底部的提示的接受广播
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constant.NEW_MESSAGE_ACTION.equals(action) ) {// 返回解接受的消息
				
				unreadcount();
				
			}else if( Constant.LIKE_COMMENT_ACTION.equals(action)){//喜欢和评论的新消息提示
				
				like_comment_count(tv_like_comment_count,ll_like_comment_count);
			}
		}

	};
	//评论喝喜欢的新消息提示
	public void like_comment_count(TextView tv, LinearLayout ll ){
		ArrayList<NearHintMsg> nearHintMsg = ChatManager.getInstance().getNearHintMsg();
		if( nearHintMsg != null && nearHintMsg.size() > 0 ){
			
			if( ll != null ) ll.setVisibility( View.VISIBLE );
			else tv.setVisibility( View.VISIBLE ); 
			tv.setText( nearHintMsg.size() +"");
			
		}else{
			
			if(ll!=null) ll.setVisibility( View.INVISIBLE );
			else tv.setVisibility( View.GONE );
			
		}
		
	}
	//回话的未读的消息提示
	public void unreadcount(){
		int[] unread  = ChatManager.getInstance().unreadcount( 0 );
		if(  (unread[0] + unread[1] + unread[2] ) > 0  ){
			ll_unreadcount.setVisibility( View.VISIBLE );
			tv_unreadcount.setText( ( unread[0] + unread[1] + unread[2] )+"" );
		}else{
			ll_unreadcount.setVisibility( View.INVISIBLE );
		}
	}
	//@Override
	//public void onBackPressed() {
	/*	if( this instanceof com.hyh.www.ZhuyeActivity ){
			//隐藏而不退出程序
			Intent i= new Intent(Intent.ACTION_MAIN);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addCategory(Intent.CATEGORY_HOME);
			startActivity(i); 
		}else {*/
			//super.onBackPressed();
	//	}
		
	//}
	/** 
	 * 双击退出函数 
	 */  
	private static Boolean isExit = false;  	  
	private void exitBy2Click() {  
	    Timer tExit = null;  
	    if (isExit == false) {  
	        isExit = true; // 准备退出  
	        Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();  
	        tExit = new Timer();  
	        tExit.schedule(new TimerTask() {  
	            @Override  
	            public void run() {  
	                isExit = false; // 取消退出  
	            }  
	        }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务  
	  
	    } else {  
	        finish();  
	       // System.exit(0);  
	    }  
	}  
	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event) {  		
	    if(keyCode == KeyEvent.KEYCODE_BACK){    
	           //exitBy2Click();      //调用双击退出函数  
	    	Intent i= new Intent(Intent.ACTION_MAIN);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addCategory(Intent.CATEGORY_HOME);
			startActivity(i); 
	     }  
	    return false;  
	} 
	//获取是否有最新消息
	public void getNewNearByCount(){
		//获取经度纬度
		String longs = GezitechApplication.systemSp.getString("long","");
		String lat = GezitechApplication.systemSp.getString("lat","");
		if( longs.equals("") || lat.equals("") ){
			
			GezitechService.getInstance().longitude( new CallBDLocation() {
				
				@Override
				public void callfunction(String longs, String lat, String city) {
					_getData(longs ,lat);
				}
			});
			
		}else{
			_getData(longs ,lat);
			
		}
	}
	private void _getData(String longs, String lat){
		RequestParams params = new RequestParams();
		params.put("long", longs);
		params.put("lat", lat);
		long lastGetTime = GezitechApplication.systemSp.getLong("lastGetTime", 0);
		params.put("ctime",lastGetTime);
		NearManager.getInstance().getNewNearByCount(params, new OnAsynGetJsonObjectListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {}
			
			@Override
			public void OnGetJSONObjectDone(JSONObject jo) {
				if( jo!=null ){
					try {
						int count = jo.has("data")?jo.getInt("data") : 0 ;
						if( count > 0 ){
							ll_nearbycount.setVisibility( View.VISIBLE );
							Intent intent3 = new Intent();
							intent3.setAction(Constant.NEAR_NEW_MSG_HINT);
							intent3.putExtra("count", count);
							sendBroadcast(intent3);	
						}else{
							hideNewMsgHint();
						}
						
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
			}
		});
	}
	//关闭红点提示
	public void hideNewMsgHint(){
		ll_nearbycount.setVisibility( View.INVISIBLE );
	}
}
