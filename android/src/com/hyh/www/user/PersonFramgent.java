package com.hyh.www.user;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynProgressListener;
import com.gezitech.entity.User;
import com.gezitech.service.managers.AccountManager;
import com.gezitech.service.managers.UserManager;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.gezitech.service.xmpp.Constant;
import com.gezitech.util.StringUtil;
import com.gezitech.widget.RemoteImageView;
import com.gezitech.widget.RemoteImageView.setBitmapListener;
import com.hyh.www.BaseFragment;
import com.hyh.www.NewsHint;
import com.hyh.www.ZhuyeActivity;

import com.hyh.www.R;
import com.hyh.www.entity.Bill;
import com.hyh.www.entity.FieldVal;
import com.hyh.www.user.post.MyPostMsg;
import com.hyh.www.widget.ImageShow;
import com.loopj.android.http.RequestParams;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 
 * @author xiaobai
 * 2014-10-14
 * @todo( 个人中心 )
 */
public class PersonFramgent extends BaseFragment implements OnClickListener{
	public static PersonFramgent fragment = null;
	private View view;
	private PersonFramgent _this = this;
	private Button bt_my_post;
	
	private RelativeLayout person_editdata;//编辑资料
	private RelativeLayout person_account;//个人账户
	private RelativeLayout person_promotion;//我的推广
	private RelativeLayout person_setting;//系统设置
	private RemoteImageView person_photo;
	private TextView person_nickname;
	private TextView person_accountnumber;
	private Button person_sales;
	private Button person_buy;
	private Button bt_home_msg;
	private ImageView iv_sex;
	private LinearLayout ll_all_order;
	private LinearLayout ll_end_pay;
	private LinearLayout ll_service;
	private TextView tv_gps;
	private String userHead = "";
	private GezitechDBHelper<User> db;
	private RelativeLayout person_my_post;
	private TextView tv_like_comment_count;
	
	public static PersonFramgent newInstance(){
		
		if( fragment!=null ){
			return fragment; 
		}else{
			fragment = new PersonFramgent();  
		}
        return fragment;  
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		view=inflater.inflate(R.layout.framgent_person, null);
		userHead = GezitechApplication.systemSp.getString("userHead","");
		db = new GezitechDBHelper<User>(User.class);
		_init();
		return view;
	}
	//初始化数据
	private void _init() {
		// TODO Auto-generated method stub
		bt_my_post = (Button) _this.view.findViewById( R.id.bt_my_post );
		bt_my_post.setVisibility( View.GONE );
		
		TextView tv_title = (TextView) view.findViewById( R.id.tv_title );
		tv_title.setText( _this.getResources().getString( R.string.person ) );
		
		
		person_my_post=(RelativeLayout) view.findViewById(R.id.person_my_post);
		person_my_post.setOnClickListener(this);
		
		
		person_editdata=(RelativeLayout) view.findViewById(R.id.person_editdata);
		person_editdata.setOnClickListener(this);
		
		person_account=(RelativeLayout) view.findViewById(R.id.person_account);
		person_account.setOnClickListener(this);
		
		person_setting=(RelativeLayout) view.findViewById(R.id.person_setting);
		person_setting.setOnClickListener(this);
		
		person_promotion=(RelativeLayout) view.findViewById(R.id.person_promotion);
		person_promotion.setOnClickListener(this);
		
		person_photo = ( RemoteImageView  ) view.findViewById( R.id.person_photo );
		//显示头像大图
		person_photo.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if( user !=null && user.head != null && !user.head.equals("") ){
					//画廊展示图片
					final String[] images = new String[1];
					String[] pic = user.head.split("src=");
					
					images[0] = StringUtil.stringDecode( pic.length > 1 ?  pic[1] : pic[0] );
					ImageShow.jumpDisplayPic(images, 0, activity);
				}
			}
		});
		person_nickname = ( TextView ) view.findViewById( R.id.person_nickname );
		person_accountnumber = ( TextView ) view.findViewById( R.id.person_accountnumber );
		iv_sex = (ImageView) view.findViewById( R.id.iv_sex );
		//所有订单
		ll_all_order = (LinearLayout) view.findViewById( R.id.ll_all_order );
		ll_all_order.setOnClickListener( this );
		//已付款
		ll_end_pay = (LinearLayout) view.findViewById( R.id.ll_end_pay );
		ll_end_pay.setOnClickListener( this );
		//服务中
		ll_service = (LinearLayout) view.findViewById( R.id.ll_service );
		ll_service.setOnClickListener( this );
		
		bt_home_msg = (Button) view.findViewById( R.id.bt_home_msg );
		bt_home_msg.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new NewsHint().setNewsUpdate(activity, bt_home_msg );
				activity.startActivity( new Intent (activity, SystemMessageActivity.class ) );
			}
		} );
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.SYSTEM_REQUEST );
		filter.addAction(Constant.LIKE_COMMENT_ACTION );
		activity.registerReceiver(receiver, filter);
		_initUserData(false);
		
		tv_gps = 	(TextView)view.findViewById( R.id.tv_gps ) ;
		
		tv_like_comment_count  = (TextView)view.findViewById( R.id.tv_like_comment_count);
		
		((ZhuyeActivity)(_this.getActivity())).like_comment_count( tv_like_comment_count , null );
		
	}
	//初始化用户数据
	private void _initUserData(boolean isResetDown ) {
		if( user != null ){
			if( FieldVal.value( user.head ).equals("") ||  user.head.lastIndexOf( "head" ) >= user.head.length()-8  ){
				if( !userHead.equals("") ){
					person_photo.setImageUrl( userHead ,false , false, new setBitmapListener() {
						
						@Override
						public void bitmap(Bitmap bm) {
							uploadHead( bm );
						}
						
					});
				}
			}else{
				person_photo.setImageUrl( user.head, isResetDown );
			}
			
			person_nickname.setText( user.nickname !=null && !user.nickname.equals("") && !user.nickname.equals("null") ?  user.nickname : "快占个好听的名字" );
			person_accountnumber.setText( user.username );
			if( user.sex == 1 ){
				iv_sex.setImageResource( R.drawable.icon_male );
				iv_sex.setVisibility( View.VISIBLE );
			}else if( user.sex == 2 ){
				iv_sex.setVisibility( View.VISIBLE );
			}
		}
		loadOrderNumber();
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new NewsHint().getNewsUpdate(activity, bt_home_msg );
		gpsAction(  tv_gps );
	}
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if( !hidden ){
			loadOrderNumber();
			new NewsHint().getNewsUpdate(activity, bt_home_msg );
		}
		gpsAction(  tv_gps );
	}
	private void loadOrderNumber(){
		UserManager.getInstance().usertrtradenumber( new OnAsynGetOneListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				Toast( errorMsg );
			}
			
			@Override
			public void OnGetOneDone(GezitechEntity_I entity) {
				Bill bill = (Bill) entity;
				( (TextView)view.findViewById( R.id.tv_all_order ) ).setText( bill.alltradenumber +"");
				( (TextView)view.findViewById( R.id.tv_end_pay ) ).setText( bill.collecttradenumber +"");
				( (TextView)view.findViewById( R.id.tv_service ) ).setText( bill.servicetradenumber +"");
			}
		});
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.person_my_post://我发布的信息
			activity.startActivity( new Intent(activity,MyPostMsg.class) );
			break;
		case R.id.person_editdata://编辑个人资料
			GezitechAlertDialog.loadDialog( activity );
			UserManager.getInstance().gainuserinfo( new OnAsynGetOneListener() {
				
				@Override
				public void OnAsynRequestFail(String errorCode, String errorMsg) {
					GezitechAlertDialog.closeDialog();
					Toast(errorMsg);
				}
				@Override
				public void OnGetOneDone(GezitechEntity_I entity) {
					GezitechAlertDialog.closeDialog();
					// TODO Auto-generated method stub
					Intent	intent = new Intent(getActivity(), EditDataActivity.class);
					_this.startActivityForResult( intent, 1001 );
				}
			});		
			break;
			
		case R.id.person_account://个人账户
			GezitechAlertDialog.loadDialog( activity );
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
						Intent	intent = new Intent(getActivity(), IndividualAccountActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("account", (User)entity );
						intent.putExtras( bundle );
						startActivity(intent);
					}
					
				}
			});
			
			break;
		case R.id.person_promotion://我的推广
			GezitechAlertDialog.loadDialog( activity );
			AccountManager.getInstance().spreadcount( new OnAsynGetOneListener() {
				
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
						Intent	tent = new Intent(getActivity(), MyPromotionActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("spreadcount", (User)entity );
						tent.putExtras( bundle );
						startActivity( tent );
					}
					
				}
			});
			
			break;
		case R.id.person_setting:
			Intent	inte = new Intent(getActivity(), SystemSettingActivity.class);
			startActivity(inte);
			break;
		case R.id.ll_all_order://所有订单
			Intent intent = new Intent( activity, OrderActivity.class );
			intent.putExtra("type", 1 );
			intent.putExtra("title", "所有订单");
			startActivity( intent );
			break;
		case R.id.ll_end_pay://
			Intent intent1 = new Intent( activity, OrderActivity.class );
			intent1.putExtra("type", 2  );
			intent1.putExtra("title", "已付款订单");
			startActivity( intent1 );
			break;
		case R.id.ll_service://购买账单
			Intent intent2 = new Intent( activity, OrderActivity.class );
			intent2.putExtra("type", 3  );
			intent2.putExtra("title", "服务中订单");
			startActivity( intent2 );
			break;
		default:
			break;
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(data == null)return;
		String result = data.getAction();
		switch (requestCode) {
		case 1001://用户资料返回
			_initUserData(true);
			break;
		default:
			break;
		}
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		activity.unregisterReceiver(receiver);
	}
	// 新系统消息的接受广播
	private BroadcastReceiver receiver = new BroadcastReceiver() {
	
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Constant.SYSTEM_REQUEST.equals(action)) {// 返回解接受的消息
				bt_home_msg.setBackgroundResource(R.drawable.common_msg_yes );
				new NewsHint().getNewsUpdate(activity, bt_home_msg, true );
	
			} else if ( Constant.LIKE_COMMENT_ACTION.equals( action ) ){
				
				((ZhuyeActivity)(_this.getActivity())).like_comment_count( tv_like_comment_count , null );
				
			}
		}
	
	};
	//上传头像至服务器
		public void uploadHead(final Bitmap photo){
			//把位图存储为路径
			final File tempImageName = new File(Environment.getExternalStorageDirectory()+"/" + getStringToday() + ".jpg");
			try {
				tempImageName.createNewFile();//创建新的文件路径
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			FileOutputStream fOut = null;
			try {
				fOut = new FileOutputStream( tempImageName ); //创建文件输出流
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			//图片压缩到输出流中
			photo.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			try {
				fOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			RequestParams params = new RequestParams();
			try {
				params.put("avatar", tempImageName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			UserManager.getInstance().uploadhead(params, new OnAsynProgressListener() {
				
				@Override
				public void OnAsynRequestFail(String errorCode, String errorMsg) {}
				@Override
				public void onUpdateDone(String id) {
					if( !id.equals("") ){
						
						user.head = id;	
						db.save(user);
					}
				}
				@Override
				public void OnProgress(int bytesWritten, int totalSize) {}
			});
		}
		//获取时间字符串
		public static String getStringToday() {
			Date currentTime = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			String dateString = formatter.format(currentTime);
			String s = ""; 
			Random ran =new Random(System.currentTimeMillis()); 
			for (int i = 0; i < 10; i++) { 
			s =s  + ran.nextInt(100); 
			} 
			return dateString+s;
		}
		
		
		
}
