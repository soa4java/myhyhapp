package com.hyh.www.home;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.config.Conf;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.entity.PageList;
import com.gezitech.service.managers.SystemManager;
import com.gezitech.service.util.SAXParserContentHandler;
import com.gezitech.service.xmpp.Constant;
import com.gezitech.util.IOUtil;
import com.gezitech.util.NetUtil;
import com.gezitech.util.SoundMeter;
import com.gezitech.widget.ExtendViewFlipper;
import com.gezitech.widget.RemoteImageView;
import com.gezitech.widget.ExtendViewFlipper.OnViewFlipperFackFunction;
import com.hyh.www.BaseFragment;
import com.hyh.www.NewsHint;
import com.hyh.www.R;
import com.hyh.www.entity.Adv;
import com.hyh.www.entity.ChatContent;
import com.hyh.www.entity.Friend;
import com.hyh.www.user.EditDataActivity;
import com.hyh.www.user.SystemMessageActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 
 * @author xiaobai
 * 2014-10-13
 * @todo( 首页   )
 */
public class HomeFramgent extends BaseFragment{

	private View view;
	private ImageView bt_home_circle;
	private ExtendViewFlipper pager_slide;
	private LinearLayout pager_control;
	private PageList advList;
	private ImageView[] contorls;
	private HomeFramgent _this = this;
	private Animation in;
	private Animation out;
	private Button bt_my_post;
	private Button bt_home_msg;
	private SoundMeter mSensor;
	private LinearLayout ll_voice;
	private View i_voice;
	private ImageView iv_size;
	protected int flag = 1;
	protected String path;
	protected long startVoiceT;
	protected String voiceName;
	protected String voicefile;
	protected long endVoiceT;
	protected long isClickTemp;
	protected boolean isclick = true;
	private TextView tv_count_down_voice_time;
	protected boolean istimeout = false;
	private TextView tv_gps;
	
	public static HomeFramgent fragment = null;
	public static HomeFramgent newInstance(){
		
		if( fragment!=null ){
			return fragment; 
		}else{
			fragment = new HomeFramgent();  
		}
        return fragment;  
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		// TODO Auto-generated method stub
		view=inflater.inflate(R.layout.framgent_home, null);
		_init();
		return view;
	}
	private void _init(){
		bt_home_circle = (ImageView) view.findViewById( R.id.bt_home_circle );
		Animation operatingAnim = AnimationUtils.loadAnimation(this.getActivity(), R.anim.imgae_view_rotate); 
		LinearInterpolator lin = new LinearInterpolator(); 
		operatingAnim.setInterpolator(lin); 
		bt_home_circle.startAnimation(operatingAnim); 
		bt_my_post = (Button) view.findViewById( R.id.bt_my_post );
		bt_my_post.setVisibility( View.GONE );
		/*bt_my_post.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent	intent = new Intent(getActivity(), MyReleaseActivity.class);
				startActivity(intent);	
			}
		});*/
		
		bt_home_msg = (Button) view.findViewById( R.id.bt_home_msg );
		bt_home_msg.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new NewsHint().setNewsUpdate(activity, bt_home_msg );
				activity.startActivity( new Intent (activity, SystemMessageActivity.class ) );
			}
		} );
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.SYSTEM_REQUEST );
		activity.registerReceiver(receiver, filter);
		//RelativeLayout home_middle = (RelativeLayout) view.findViewById( R.id.home_middle);
		
		//按住说话
		// 语音
		mSensor = new SoundMeter();
		ll_voice = (LinearLayout) view.findViewById(R.id.ll_voice);
		i_voice = (View) view.findViewById( R.id.i_voice );
		iv_size = (ImageView) view.findViewById( R.id.iv_size );
		tv_count_down_voice_time = (TextView) view.findViewById( R.id.tv_count_down_voice_time );
		bt_home_circle.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
					if (Environment.getExternalStorageDirectory().exists() ) {
						switch(event.getAction() ){
						case MotionEvent.ACTION_DOWN:
							//记录点击事件
							startVoiceT = System.currentTimeMillis();
							isclick  = true;
							break;
						case MotionEvent.ACTION_MOVE:
							isClickTemp = System.currentTimeMillis();
							
							if( flag == 1 && ( isClickTemp - startVoiceT > 400 ) ){
								flag = 2;
								isclick = false;
								istimeout = false;
								i_voice.setVisibility( View.VISIBLE );
								
								path = IOUtil.FILEPATH	+ "/amr/";
								startVoiceT = System.currentTimeMillis();
								voiceName = startVoiceT + ".amr";
								voicefile = path+voiceName;
								new File(path).mkdirs();
								mSensor.start(path, voiceName);
								mHandler.postDelayed(mPollTask, 300 );
								mHandler.postDelayed(count_down_voice_time, 1000 );
							}
							break;
						case MotionEvent.ACTION_UP:
						case MotionEvent.ACTION_CANCEL:
							if( flag == 2 && !isclick && !istimeout  ){
								
								postVoice();
								
							}else if( isclick ){
								
								Intent intent = new Intent( activity,  ReleaseHyhActivity.class );
								activity.startActivity( intent  );	
								
							}
							flag = 1;
							break;
						default: break;
						}
				}
				
				return true;
			}
		});
		_initAdv();
		tv_gps = 	(TextView)view.findViewById( R.id.tv_gps ) ;
		
		
		
	}
	private void postVoice(){
		//隐藏剩余时间布局
		tv_count_down_voice_time.setVisibility( View.GONE );
		i_voice.setVisibility( View.GONE );
		mHandler.removeCallbacks(mPollTask);
		try{
			mSensor.stop();
		}catch(Exception ex){
			
		}
		
		iv_size.setImageResource(R.drawable.fbhyh_sound_01 );
		endVoiceT = System.currentTimeMillis();
		long time = endVoiceT - startVoiceT;
		if( time < 1200 ){
			
			Toast("语音太短,请重新发布");
			try{
				if (!new File(voicefile).getParentFile().exists())
				{
					new File(voicefile).getParentFile().mkdirs();
				}
				if (new File(voicefile).exists())
				{
					new File(voicefile).delete();
				}
			}catch(Exception ex){}
			
		}else{
			Intent intent = new Intent( activity,  ReleaseHyhActivity.class );
			intent.putExtra("voicefile", voicefile);
			intent.putExtra("speechtime", time );
			activity.startActivity( intent  );	
		}
		
	}
	//初始化广告
	private void _initAdv()	{	
		pager_slide = ( ExtendViewFlipper ) view.findViewById( R.id.vf_ad );
		pager_control = ( LinearLayout ) view.findViewById( R.id.pager_control );		
		
		advList = SystemManager.getInstance().getClientAdvList();
		if( advList == null || advList.size()<= 0 ){
			Adv advDefault = new Adv();
			advDefault.isdefault = 1;
			advDefault.drawable = R.drawable.sy_ad_01;	
			advList.add( advDefault );
		}
		
		advData();
		
		SystemManager.getInstance().advlist(1, 5, GezitechApplication.mLocation == null  ? "" : GezitechApplication.mLocation.getCity(), new OnAsynGetListListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {}
			@Override
			public void OnGetListDone(ArrayList<GezitechEntity_I> list) {
				if( list!=null && list.size()> 0 ){
					advList.clear();
					advList = (PageList)list;
					mHandler.removeCallbacks( advtime );
					advData();
				}
			}
		});		
	}
	private void advData(){
		pager_slide.removeAllViews();
		pager_control.removeAllViews();
		contorls = new ImageView[ advList.size() ];
		DisplayMetrics dm = this.getResources().getDisplayMetrics();
		for( int i = 0; i<advList.size(); i++ ){
			//追加小原点
			contorls[i] = new ImageView( activity );
			if( i == 0 ){
				contorls[i].setImageDrawable( _this.getResources().getDrawable( R.drawable.sy_ad_dot_selected ) );						
			}else{
				contorls[i].setImageDrawable( _this.getResources().getDrawable( R.drawable.sy_ad_dot_normal ) );					
			}
			pager_control.addView( contorls[i] );		
			//追加广告的内容
			View view = LayoutInflater.from( activity ).inflate(R.layout.item_adv, null);			
			RemoteImageView iv_ad = (RemoteImageView)view.findViewById( R.id.iv_ad );
			//iv_ad.setBackgroundResource( R.drawable.sy_ad_01 );
			Adv adv = (Adv) advList.get(i);
			if( adv.isdefault > 0 ){
				iv_ad.setBackgroundDrawable( _this.getResources().getDrawable(  adv.drawable  ) );
			}else{
				iv_ad.setImageUrl(adv.ad_litpic,false,true);
			}
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( dm.widthPixels, (int)(dm.density*114) );
			view.setLayoutParams( lp );
			pager_slide.addView( view );
		}
		_viewFlipper();
	}
	//幻灯片的切换
	private void _viewFlipper() {
		//初始化参数
		in = AnimationUtils.loadAnimation(activity, R.anim.left_in);
		out = AnimationUtils.loadAnimation(activity, R.anim.left_out);
		mHandler.postDelayed(advtime,  Conf.pauseTime);
	    pager_slide.setOnViewFlipperFackFunction( new OnViewFlipperFackFunction(){
			@Override
			public void selectStyle(  boolean isPre  ) {
				_this.setectStyle(  isPre );
			}
			@Override
			public void countDownTimerAction(boolean isAction) { //false 取消  true 开始
				if( isAction ){
					mHandler.postDelayed(advtime,  Conf.pauseTime);
				}else {
					mHandler.removeCallbacks( advtime );
				}
			}    	
	    });
	}
	private Handler mHandler = new Handler();
	private Runnable  advtime = new Runnable()
	{
		public void run()
		{
			pager_slide.setInAnimation(in);
        	pager_slide.setOutAnimation(out);
        	_this.setectStyle( true );
        	pager_slide.showNext();
        	mHandler.postDelayed(advtime,  Conf.pauseTime);
		}
	};
   //圆点选中样式
   public void setectStyle(  boolean isPre  ){	   
	    int index = pager_slide.getDisplayedChild();
	   	//圆点的选中
	    if( isPre ){
	    	index = index+1; 
	    	if( index >contorls.length-1 ) index = 0;
	    }else{
	    	index = index-1; 
	    	if( index<0) index = contorls.length-1;
	    }
	    for( int i =0; i<contorls.length; i++){
	    	if( i == index ){
	    		contorls[i].setImageDrawable( activity.getResources().getDrawable( R.drawable.sy_ad_dot_selected ) );	
	    	}else{
	    		contorls[i].setImageDrawable( activity.getResources().getDrawable( R.drawable.sy_ad_dot_normal ) );	
	    	}
	    }	   	
   }
   @Override
	public void onResume() { 
		super.onResume();
		if( mHandler != null  && advtime != null ) mHandler.postDelayed(advtime,  Conf.pauseTime);
		new NewsHint().getNewsUpdate(activity, bt_home_msg );
		gpsAction(  tv_gps );
	}
   @Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if( !hidden ){
			new NewsHint().getNewsUpdate(activity, bt_home_msg );
		}
		gpsAction(  tv_gps );
	}
    @Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		destroyThread();
	}
   @Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();		
		destroyThread();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		destroyThread();
		activity.unregisterReceiver( receiver );
	}
	public void destroyThread(){
		if( mHandler != null ){
			mHandler.removeCallbacks( advtime );
			mHandler.removeCallbacks( count_down_voice_time );
		}
	}
	@Override
	public void onDetach() {
		super.onDetach();
		
	}
	private Runnable mPollTask = new Runnable()
	{
		public void run()
		{
			double amp = mSensor.getAmplitude();
			updateDisplay(amp);
			mHandler.postDelayed(mPollTask, 300);
		}
	};
	private Runnable  count_down_voice_time  = new Runnable()
	{
		public void run()
		{
			
			endVoiceT = System.currentTimeMillis();
			long time = endVoiceT - startVoiceT;
			if( (time/1000) >= Conf.vioceTime-10 && (time/1000) <= Conf.vioceTime ){//发送
				tv_count_down_voice_time.setVisibility( View.VISIBLE );
				tv_count_down_voice_time.setText( "还剩下 "+( Conf.vioceTime-(time/1000) ) + " 秒" );
				if(  (time/1000) == Conf.vioceTime ){//当时间等于60秒
					
					mHandler.removeCallbacks( count_down_voice_time );
					
					istimeout = true;
					postVoice();
				}
			}
			mHandler.postDelayed(count_down_voice_time, 1000);
		}
	};

	private void updateDisplay(double signalEMA)
	{

		switch ((int) signalEMA)
		{
		case 0:
		case 1:
			iv_size.setImageResource(R.drawable.fbhyh_sound_01);
			break;
		case 2:
		case 3:
			iv_size.setImageResource(R.drawable.fbhyh_sound_02);
			break;
		case 4:
		case 5:
		case 6:
			iv_size.setImageResource(R.drawable.fbhyh_sound_03);
			break;
		case 7:
		case 8:
		case 9:	
			iv_size.setImageResource(R.drawable.fbhyh_sound_04);
			break;
		case 10:
		case 11:
		default:
			iv_size.setImageResource(R.drawable.fbhyh_sound_05);
			break;
		}
	}

	// 新系统消息的接受广播
	private BroadcastReceiver receiver = new BroadcastReceiver() {
	
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constant.SYSTEM_REQUEST.equals(action)) {// 返回解接受的消息
				bt_home_msg.setBackgroundResource(R.drawable.common_msg_yes );
				new NewsHint().getNewsUpdate(activity, bt_home_msg, true );
	
			} 
		}
	
	};
}
