package com.hyh.www.home;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jivesoftware.smack.XMPPException;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.config.Conf;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynInsertListener;
import com.gezitech.service.managers.ShoutManager;
import com.gezitech.service.managers.SystemManager;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.gezitech.service.xmpp.Constant;
import com.gezitech.service.xmpp.XmppConnectionManager;
import com.gezitech.util.IOUtil;
import com.gezitech.util.ImageUtil;
import com.gezitech.util.SoundMeter;
import com.gezitech.util.StringUtil;
import com.gezitech.widget.MoreOptionDialog;
import com.gezitech.widget.OptionDialog;
import com.gezitech.widget.SelectPicPopupWindow;
import com.gezitech.widget.OptionDialog.DialogSelectDataCallBack;
import com.gezitech.widget.OptionDialog.ItemType;
import com.gezitech.widget.RemoteImageView;
import com.hyh.www.R;
import com.hyh.www.entity.ChatContent;
import com.hyh.www.entity.City;
import com.hyh.www.entity.Companytype;
import com.hyh.www.entity.Releasescope;
import com.hyh.www.entity.Shout;
import com.hyh.www.entity.Validtimelist;
import com.hyh.www.widget.YMDialog2;
import com.loopj.android.http.RequestParams;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author xiaobai (发布喊一喊）
 */
public class ReleaseHyhActivity extends GezitechActivity implements
		OnClickListener {

	private ReleaseHyhActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_title;
	private RelativeLayout releaseHyh_shangjialexing;
	private long company_typeid = 0;
	private long range = 0;
	private int activetime = 0;
	private TextView tv_releaseHyh_shangjialexing;
	private RelativeLayout releaseHyh_fabufanwei;
	private TextView tv_releaseHyh_fabufanwei;
	private EditText ed_releaseHyh_huifurenshu;
	private RelativeLayout releaseHyh_youxiaoshijian;
	private TextView tv_releaseHyh_youxiaoshijian;
	private EditText ed_releaseHyh_content;
	private ImageButton iv_releaseHyh_camera;
	private RemoteImageView iv_releaseHyh_photo;
	private RelativeLayout rl_releaseHyh_photo;
	private ImageView play_del_xx;
	private File litpic = null;
	private TextView releaseHyh_fbhyh_speak;
	private LinearLayout ll_voice;
	private SoundMeter mSensor;
	private Handler mHandler = new Handler();
	private ImageView iv_size;
	protected int flag = 1;
	protected int isverify = 1;
	protected long startVoiceT;
	protected String voiceName;
	protected long endVoiceT;
	protected String path;
	protected String voicefile;
	private RequestParams params;
	private org.jivesoftware.smack.Chat chat;
	private View i_voice;
	private String Prevoicefile = "";
	private File PrevoicefileFile = null;
	private boolean isVoice  = false;
	private long speechtime = 0 ;
	private TextView tv_count_down_voice_time;
	protected boolean istimeout = false;
	private RadioGroup rg_pubway_box;
	private int pubway = 0 ; //0是按范围发送 1是按地区
	private String provinces_nameVal="0",urban_nameVal="0",county_nameVal="0",streets_nameVal="0";
	private LinearLayout ll_juli_box;
	private LinearLayout ll_diqu_box;
	private TextView ed_shengshi;
	private TextView ed_shiqu;
	private TextView ed_quxian;
	private TextView ed_jiedao;
	private TextView ed_guojia;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_release_hyh);
		Prevoicefile = _this.getIntent().getStringExtra("voicefile");
		speechtime  = _this.getIntent().getLongExtra("speechtime", 0);
		//判断是否已经链接
		if(  GezitechApplication.connection == null || ( !GezitechApplication.connection.isConnected() ) || (  XmppConnectionManager.getInstance().getConnection() !=null && !XmppConnectionManager.getInstance().getConnection().isConnected() ) ){
			XmppConnectionManager.getInstance().login();
		}
		_init();
	}
	private void _init() {
	
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);
		
		if( Prevoicefile!= null && !Prevoicefile.equals("") ){
			PrevoicefileFile  = new File( Prevoicefile );
			if( PrevoicefileFile.exists()  ){
			isVoice = true;
			
			bt_my_post.setVisibility( View.VISIBLE );
			bt_my_post.setText("取消");
			bt_my_post.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();					
				}
			});
			}
		}

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(_this.getResources().getString(R.string.fabuhanyihan));

		//发布范围方式的选择
		//rg_pubway_box = ( RadioGroup ) findViewById(R.id.rg_pubway_box);
		ll_juli_box = (LinearLayout) findViewById(R.id.ll_juli_box );
		ll_diqu_box =  (LinearLayout) findViewById(R.id.ll_diqu_box );
		/*rg_pubway_box.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if( checkedId == R.id.rb_juli ){
					pubway = 0;
					ll_juli_box.setVisibility( View.VISIBLE );
					ll_diqu_box.setVisibility( View.GONE  );
				}else if( checkedId == R.id.rb_diqu ){
					pubway = 1;
					ll_juli_box.setVisibility( View.GONE );
					ll_diqu_box.setVisibility( View.VISIBLE  );
				}
			}
		});*/
		//地区的选择
		/*ed_guojia  = (TextView) _this.findViewById( R.id.ed_guojia );
		ed_guojia.setOnClickListener( this );*/
		ed_shengshi  = (TextView) _this.findViewById( R.id.ed_shengshi );
		ed_shengshi.setOnClickListener( this );
		ed_shiqu = (TextView) _this.findViewById( R.id.ed_shiqu );
		ed_shiqu.setOnClickListener( this );
		ed_quxian = (TextView) _this.findViewById( R.id.ed_quxian );
		ed_quxian.setOnClickListener( this );
		ed_jiedao = (TextView) _this.findViewById( R.id.ed_jiedao ); 
		ed_jiedao.setOnClickListener( this );
		
		tv_releaseHyh_shangjialexing = (TextView) findViewById(R.id.tv_releaseHyh_shangjialexing_val);
		releaseHyh_shangjialexing = (RelativeLayout) findViewById(R.id.releaseHyh_shangjialexing);
		// 选择商家类型
		releaseHyh_shangjialexing.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GezitechAlertDialog.loadDialog(_this);
				SystemManager.getInstance().companytypelist(
						new OnAsynGetListListener() {
							@Override
							public void OnAsynRequestFail(String errorCode,
									String errorMsg) {
								GezitechAlertDialog.closeDialog();
								Toast(errorMsg);
							}

							@Override
							public void OnGetListDone(
									ArrayList<GezitechEntity_I> list) {
								GezitechAlertDialog.closeDialog();
								if (list != null && list.size() > 0) {
									typeDialog(list);
								} else {
									Toast("没有数据");
								}
							}
						});
			}
		});
		// 发布范围
		releaseHyh_fabufanwei = (RelativeLayout) findViewById(R.id.releaseHyh_fabufanwei);
		tv_releaseHyh_fabufanwei = (TextView) findViewById(R.id.tv_releaseHyh_fabufanwei_val);
		releaseHyh_fabufanwei.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if( rangeoption.equals("") ){
					Toast( "先选择商家类型" );
					return;
				}
				GezitechAlertDialog.loadDialog(_this);
				ShoutManager.getInstance().releasescope(rangeoption,
						new OnAsynGetListListener() {
							@Override
							public void OnAsynRequestFail(String errorCode,
									String errorMsg) {
								GezitechAlertDialog.closeDialog();
								Toast(errorMsg);
							}

							@Override
							public void OnGetListDone(
									ArrayList<GezitechEntity_I> list) {
								GezitechAlertDialog.closeDialog();
								if (list != null && list.size() > 0) {
									releasescopeDialog(list);
								} else {
									Toast("没有数据");
								}
							}
						});

			}
		});
		ed_releaseHyh_huifurenshu = (EditText) findViewById(R.id.ed_releaseHyh_huifurenshu);
		// 有效时间
		releaseHyh_youxiaoshijian = (RelativeLayout) findViewById(R.id.releaseHyh_youxiaoshijian);
		tv_releaseHyh_youxiaoshijian = (TextView) findViewById(R.id.tv_releaseHyh_youxiaoshijian_val);
		releaseHyh_youxiaoshijian.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if( activetimeoption.equals("")  ){
					Toast( "先选择商家类型" );
					return;
				}
				GezitechAlertDialog.loadDialog(_this);
				ShoutManager.getInstance().validtimelist(0,activetimeoption,
						new OnAsynGetListListener() {
							@Override
							public void OnAsynRequestFail(String errorCode,
									String errorMsg) {
								GezitechAlertDialog.closeDialog();
								Toast(errorMsg);
							}

							@Override
							public void OnGetListDone(
									ArrayList<GezitechEntity_I> list) {
								GezitechAlertDialog.closeDialog();
								if (list != null && list.size() > 0) {
									validtimelistDialog(list);
								} else {
									Toast("没有数据");
								}
							}
						});
			}
		});
		// 文字描述
		ed_releaseHyh_content = (EditText) findViewById(R.id.ed_releaseHyh_content);
		//添加文字的事件监听
		final int maxLen = 25;
		ed_releaseHyh_content.addTextChangedListener( new TextWatcher(){
			@Override
			public void afterTextChanged(Editable arg0) {}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				//int selectionStart = etBlog.getSelectionStart();  
	           /* int selectionEnd = ed_releaseHyh_content.getSelectionEnd();
	            Editable editable = ed_releaseHyh_content.getText();
	            int maxLen = 25;
	            if( selectionEnd > maxLen )  
	            {  
	            	
	                int selEndIndex = Selection.getSelectionEnd( editable );  
	                String str = editable.toString();  
	                //截取新字符串  
	                String newStr = str.substring(0,maxLen);  
	                ed_releaseHyh_content.setText(newStr);  
	                editable = ed_releaseHyh_content.getText();  
	                  
	                //新字符串的长度  
	                int newLen = editable.length();  
	                //旧光标位置超过字符串长度  
	                if(selEndIndex > newLen)  
	                {  
	                    selEndIndex = editable.length();  
	                }  
	                //设置新光标所在的位置  
	                Selection.setSelection(editable, selEndIndex);  
	                  
	            }  */
			}
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
				 	Editable editable = ed_releaseHyh_content.getText();  
			        int len = editable.length();  
			          
			        if(len > maxLen)  
			        {  
			            int selEndIndex = Selection.getSelectionEnd(editable);  
			            String str = editable.toString();  
			            //截取新字符串  
			            String newStr = str.substring(0,maxLen);  
			            ed_releaseHyh_content.setText(newStr);  
			            editable = ed_releaseHyh_content.getText();  
			              
			            //新字符串的长度  
			            int newLen = editable.length();  
			            //旧光标位置超过字符串长度  
			            if(selEndIndex > newLen)  
			            {  
			                selEndIndex = editable.length();  
			            }  
			            //设置新光标所在的位置  
			            Selection.setSelection(editable, selEndIndex);  
			            Toast("已经超过25个字符");
			        } 
				
			}			
		});

		// 相机
		iv_releaseHyh_camera = (ImageButton) findViewById(R.id.iv_releaseHyh_camera);

		rl_releaseHyh_photo = (RelativeLayout) findViewById(R.id.rl_releaseHyh_photo);
		iv_releaseHyh_photo = (RemoteImageView) findViewById(R.id.iv_releaseHyh_photo);
		play_del_xx = (ImageView) findViewById(R.id.iv_del);
		_initPhoto();

		// 语音
		mSensor = new SoundMeter();
		ll_voice = (LinearLayout) findViewById(R.id.ll_voice);
		i_voice = (View) findViewById( R.id.i_voice );
		iv_size = (ImageView) findViewById( R.id.iv_size );
		releaseHyh_fbhyh_speak = (TextView) findViewById(R.id.releaseHyh_fbhyh_speak);	
		tv_count_down_voice_time = (TextView) findViewById( R.id.tv_count_down_voice_time );
		if( isVoice ){//发布过  点击发送
			releaseHyh_fbhyh_speak.setBackgroundResource( R.drawable.button_fbhyh_speak2 );
			releaseHyh_fbhyh_speak.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if( _initYuyinData() ){
						try {
							params.put("speech",  PrevoicefileFile );
							params.put("speechtime", speechtime);
							
							_initYuyinDataSubmit( speechtime );
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
			});
			
		}else{//没有发布过  按住说话
			releaseHyh_fbhyh_speak.setOnTouchListener(new OnTouchListener() {
	
				@Override
				public boolean onTouch(View v, MotionEvent event) {
						
						if (Environment.getExternalStorageDirectory().exists() ) {
							switch(event.getAction() ){
							case MotionEvent.ACTION_DOWN:
								if( flag == 1){
									flag = 2;
									istimeout  = false;
									if( _initYuyinData() ){
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
								}
								break;
							case MotionEvent.ACTION_UP:
							case MotionEvent.ACTION_CANCEL:
								if( flag == 2 && isSubmit && !istimeout ){
									postVoice();
								}
								flag = 1;
								break;
							default: break;
							}
					}
					
					return true;
				}
			});
		}
	}
	private void postVoice(){
		i_voice.setVisibility( View.GONE );
		mHandler.removeCallbacks(mPollTask);
		mHandler.removeCallbacks(count_down_voice_time);
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
			try {
				File f = new File(voicefile) ;
				params.put("speech",f );
				params.put("speechtime", time);
				_initYuyinDataSubmit( time );
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	//time  语音时长
	public void _initYuyinDataSubmit(final long time){
		GezitechAlertDialog.loadDialog( this );
		GezitechApplication.getInstance().getBDLocation( new BDLocationListener() {
			
			@Override
			public void onReceiveLocation(final BDLocation arg0) {
				GezitechApplication.getInstance().setBDLocation( arg0 );
				final double Longitude = arg0.getLongitude();
				final double Latitude = arg0.getLatitude();
				params.put("long", Longitude+"" );
				params.put("lat", Latitude+"" );
				releaseHyh_fbhyh_speak.setEnabled( false );
				ShoutManager.getInstance().releaseshout(params, new OnAsynGetOneListener() {
					
					@Override
					public void OnAsynRequestFail(String errorCode, String errorMsg) {
						// TODO Auto-generated method stub
						Toast( errorMsg );
						GezitechAlertDialog.closeDialog();
						releaseHyh_fbhyh_speak.setEnabled( true );
					}

					@Override
					public void OnGetOneDone(GezitechEntity_I entity) {
						GezitechAlertDialog.closeDialog();
						final Shout shout = (Shout) entity;
						try{
							chat = XmppConnectionManager.getInstance().getConnection()
									.getChatManager().createChat("shout@shout/android", null);
						}catch(Exception ex){
							
						}
						// 发送消息
						org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
						String messageContent = "{\"range\":"+rangeVal+",\"id\":"+shout.id+",\"long\":\""+Longitude+"\"" +
								",\"ctime\":"+shout.ctime+",\"uid\":"+shout.uid+",\"typeid\":\""+shout.typeid+"\",\"caption\":\""+shout.caption+"\"," +
								"\"activetime\":\""+activetimeVal+"\",\"maxReplycount\":\""+shout.maxReplycount+"\",\"lat\":\""+Latitude+"\"," + 
										"\"litpic\":\""+shout.litpic+"\",\"speechtime\":\""+time+"\",\"speech\":\""+shout.speech+"\",\"pubway\":"+pubway+",\"provinces\":\""+provinces_nameVal+"\",\"urban\":\""+urban_nameVal+"\",\"county\":\""+county_nameVal+"\",\"streets\":\""+streets_nameVal+"\",\"country\":\""+range+"\"}"; 
						message.setBody(messageContent);
						message.setProperty("msgtype", 9);
						message.setProperty("hyhid", shout.id  );
						try {
							chat.sendMessage(message);
							
							GezitechDBHelper<ChatContent> chatContentDB = new GezitechDBHelper<ChatContent>(ChatContent.class);
							// 存储聊天记录
							ChatContent chantContent = new ChatContent();
							chantContent.chatid = user.id;
							chantContent.type = 9;
							chantContent.ctime = shout.ctime*1000;
							chantContent.body = messageContent;
							chantContent.uid = user.id;
							chantContent.isfriend = 4;
							chantContent.myuid = user.id;
							chantContent.hyhid = shout.id;
							chatContentDB.insert(chantContent);
							
							// 通知聊天列表的广播
							Intent intent = new Intent();
							intent.setAction(Constant.SEND_UPDATE_MESSAGE_ACTION);
							Bundle bundle = new Bundle();
							bundle.putSerializable(Constant.CHAT_CONTENT, chantContent);
							intent.putExtras(bundle);
							sendBroadcast(intent);
							
						} catch (XMPPException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						final YMDialog2 ymdialog2 = new YMDialog2(_this);
						ymdialog2.setHintMsg( _this.getString( R.string.fabu_xuqiu ) );
						ymdialog2.setCloseButton( new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								releaseHyh_fbhyh_speak.setEnabled( true );
								ymdialog2.dismiss();
								_this.finish();
							}
						});
						//Toast( "发布成功" );
						//_this.finish();
					}
				});
			}
		});
		
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
					tv_count_down_voice_time.setVisibility( View.GONE );
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
	
	// 传图片
	private void _initPhoto() {

		iv_releaseHyh_camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				
				_this.startActivityForResult(new Intent(_this,
						SelectPicPopupWindow.class), 10002);
				_this.overridePendingTransition(R.anim.out_to_down, R.anim.exit_anim);	
				
				
			}
		});
		// 删除
		play_del_xx.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rl_releaseHyh_photo.setVisibility(View.GONE);
				litpic = null;
			}
		});
	}
	private String ImageName;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bitmap originBitMap = null, reduceBitMap = null;
		File originFile = null;
		String filePath = null;
		switch (requestCode) {
		case 10002:
			
			if( data == null ) return;
			String action = data.getAction();
			
			if (action.equals("10001")) {// 拍照
				ImageName = IOUtil.generateRandomFilename()+ ".jpg";
				Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(
						new File( IOUtil.IMAGEPATH, ImageName) ) );
				startActivityForResult(intent1, 1004);

			} else if (action.equals("10002")) {// 相册
				Intent localIntent = new Intent();
				localIntent.setType("image/*");
				localIntent.setAction("android.intent.action.GET_CONTENT");
				Intent localIntent2 = Intent.createChooser(localIntent, "上传图片");
				startActivityForResult(localIntent2, 1003);
			}
			break;
		case 1003:// 单选
			ContentResolver resolver3 = getContentResolver();
			if(data == null) return;
			Uri selectedImageUri3 = data.getData();

			if (selectedImageUri3 != null) {
				/*FileInputStream fis = null;
				try {
					fis = (FileInputStream) resolver3
							.openInputStream(selectedImageUri3);
					originBitMap = BitmapFactory.decodeStream(fis);
					int scale = ImageUtil.reckonThumbnail(originBitMap.getWidth(),
							originBitMap.getHeight(), 600, 600);
					originBitMap = ImageUtil.PicZoom(originBitMap,
							(int) (originBitMap.getWidth() / scale),
						(int) (originBitMap.getHeight() / scale));
					originFile = IOUtil.makeLocalImage(originBitMap, null);
				} catch (Exception e) {
					e.printStackTrace();
				}*/
				FileInputStream fis = null;
				try {
					 fis = (FileInputStream) resolver3.openInputStream(selectedImageUri3);
					 
					 byte[] buffer = new byte[fis.available()];
					 fis.read(buffer);
					 
					 originBitMap = ImageUtil.PicZoom2(buffer,600,600 );
					 
					 originFile = IOUtil.makeLocalImage(originBitMap, null);
					 
					 
				} catch (Exception e) {
					 e.printStackTrace();
				} finally {
					 try {
						 if (fis != null)
						    fis.close();
					   	} catch (IOException e) {
					   		e.printStackTrace();
					   }
				}
				iv_releaseHyh_photo.setImageBitmap(originBitMap);
				litpic = originFile ;
				rl_releaseHyh_photo.setVisibility(View.VISIBLE);
				System.gc();
			}
			break;
		case 1004:
			/*try {
				File pic = new File( IOUtil.IMAGEPATH, ImageName) ;
				
				originBitMap = BitmapFactory.decodeFile( pic.getPath() );

				int scale = ImageUtil.reckonThumbnail(originBitMap.getWidth(),
						originBitMap.getHeight(), 600, 600);
				originBitMap = ImageUtil.PicZoom(originBitMap,
						(int) (originBitMap.getWidth() / scale),
					(int) (originBitMap.getHeight() / scale));
				originFile = IOUtil.makeLocalImage(originBitMap, null);
				
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			FileInputStream fis = null;
			try {
				 File pic = new File( IOUtil.IMAGEPATH, ImageName) ;
				 fis = new FileInputStream( pic );
				 byte[] buffer = new byte[fis.available()];
				 fis.read(buffer);
				 
				 originBitMap = ImageUtil.PicZoom2(buffer,600,600 );
				 
				 originFile = IOUtil.makeLocalImage(originBitMap, null);
				 
				 
			} catch (Exception e) {
				 e.printStackTrace();
			} finally {
				 try {
					 if (fis != null)
					    fis.close();
				   	} catch (IOException e) {
				   		e.printStackTrace();
				   }
			}
			
			
			iv_releaseHyh_photo.setImageBitmap(originBitMap);
			litpic = originFile;
			
			rl_releaseHyh_photo.setVisibility(View.VISIBLE);
			System.gc();
			
			break;
		default:
			break;
		}
	}

	// 初始化语言信息
	private boolean isSubmit = false;
	private boolean  _initYuyinData() {	
		if ( company_typeid <= 0 ) {
			Toast("商家类型未选择");
			return false;
		}
		params = new RequestParams();

		
		if (range <= 0) {
			Toast("发布范围未选择");
			return false;
		}
		params.put("range", range);
			
		if( pubway == 1 ){
			
			if( range<=0  ){
				
				Toast("国家未选择");
				return false ;
				
			}
			
			/*if( provinces_nameVal.equals("")  ){
				
				Toast("省市未选择");
				return false ;
				
			}*/
			params.put("country", range+"" );
			params.put("provinces",  provinces_nameVal.equals("") ? "-1" : provinces_nameVal );
			params.put("urban", urban_nameVal.equals("") ? "-1" : urban_nameVal );
			params.put("county", county_nameVal.equals("") ? "-1" : county_nameVal  );
			params.put("streets", streets_nameVal.equals("") ? "-1" : streets_nameVal );
		}
		
		String ed_releaseHyh_huifurenshuValStr = ed_releaseHyh_huifurenshu.getText().toString().trim();
		int ed_releaseHyh_huifurenshuVal = ed_releaseHyh_huifurenshuValStr.equals("") ? 0 : Integer.parseInt( ed_releaseHyh_huifurenshuValStr );
		if (ed_releaseHyh_huifurenshuVal <= 0) {
			Toast("至少1人");
			return false;
		}
		if (activetime <= 0) {
			Toast("有效时间未选择");
			return false;
		}
		String ed_releaseHyh_contentVal = ed_releaseHyh_content.getText()
				.toString().trim();
/*		if (ed_releaseHyh_contentVal.equals("")) {
			Toast("添加文字描述,让需求更清晰");
			return false;
		}*/

		
		params.put("typeid", company_typeid);
		params.put("pubway", pubway);
		params.put("maxReplycount", ed_releaseHyh_huifurenshuVal);
		params.put("activetime", activetime);
		params.put("caption", ed_releaseHyh_contentVal);
		if( litpic != null ){
			try {
				params.put("litpic", litpic);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		isSubmit = true;
		return true;
	}

	private Companytype typeList = null;
	protected String rangeoption = "";
	protected String activetimeoption = "";

	// 弹出企业类型选择框
	public void typeDialog(final ArrayList<GezitechEntity_I> list) {
		MoreOptionDialog optionDialog = new MoreOptionDialog(_this,
				R.style.dialog_load1, list, "商家类型选择", typeList );
		optionDialog.setOnOKButtonListener(new MoreOptionDialog.DialogSelectDataCallBack() {

			@Override
			public void onDataCallBack( GezitechEntity_I  selectedList) {
				_this.typeList =  ( Companytype ) selectedList;
				if ( typeList == null ) {
					tv_releaseHyh_shangjialexing.setText("请选择");
					return;
				} else {
					
					tv_releaseHyh_shangjialexing.setText( typeList.typename );
					
				}
				
				company_typeid = _this.typeList.id;
				
				tv_releaseHyh_fabufanwei.setText( typeList.range_rangename  );
				rangeVal = typeList.range_range;
				range = typeList.range;
				
				ed_releaseHyh_huifurenshu.setText( typeList.answernumber+"" );
				
				tv_releaseHyh_youxiaoshijian.setText( typeList.range_activetimeName );
				
				activetime = (int)typeList.activetimeName;
				activetimeVal = typeList.range_activetime ;
				
				
				rangeoption = typeList.rangeoption;
				
				activetimeoption = typeList.activetimeoption;
				
				
				
				/*Set<String> a = (Set<String>) selectedList.keySet();
				String[] keyArray = (String[]) a.toArray(new String[] {});
				if (keyArray.length >= 1) {
					company_typeid = Integer.parseInt(keyArray[0]);
					//填充默认数据
					Companytype companytype = null;
					for( int i = 0 ; i< list.size(); i++ ){
						companytype = (Companytype)list.get( i );
						if(  companytype.id == company_typeid ){
							
							break;
						}else{
							companytype  = null;
						}
						
					}
					if( companytype != null ){
					
						
								
					}
				}

				if (selectedList.size() == 0) {
					tv_releaseHyh_shangjialexing.setText("请选择");
				} else {
					Collection<String> b = selectedList.values();
					String[] strArray = (String[]) b.toArray(new String[] {});
					tv_releaseHyh_shangjialexing.setText(StringUtil
							.stringArrayJoin(strArray, ","));
				}*/
			}
		});
	}

	private HashMap<String, String> releasescopeList = new HashMap<String, String>();
	protected long rangeVal = 0;

	// 弹出发布范围选择框
	public void releasescopeDialog(final ArrayList<GezitechEntity_I> list) {
		
		OptionDialog optionDialog = new OptionDialog(_this,
				R.style.dialog_load1, list, "发布范围选择", releasescopeList, true,
				ItemType.Releasescope);
		optionDialog.setOnOKButtonListener(new DialogSelectDataCallBack() {

			@Override
			public void onDataCallBack(HashMap<String, String> selectedList) {
				_this.releasescopeList = selectedList;
				Set<String> a = (Set<String>) selectedList.keySet();
				String[] keyArray = (String[]) a.toArray(new String[] {});
				if (keyArray.length >= 1) {
					range = Integer.parseInt(keyArray[0]);
					for( int i = 0 ; i<list.size(); i++){
						Releasescope obj = (Releasescope)list.get( i );
						if( obj.id == range ){
							rangeVal  = obj.range;
						}
					}
					
				}

				if (selectedList.size() == 0) {
					tv_releaseHyh_fabufanwei.setText("请选择");
				} else {
					Collection<String> b = selectedList.values();
					String[] strArray = (String[]) b.toArray(new String[] {});
					tv_releaseHyh_fabufanwei.setText(StringUtil
							.stringArrayJoin(strArray, ","));
				}
				
				pubway = rangeVal < 0 ? 1 : 0;
				if( pubway == 1 ){
					ll_diqu_box.setVisibility( View.VISIBLE );
				}else{
					ll_diqu_box.setVisibility( View.GONE );
				}
				
			}
		});
	}

	private HashMap<String, String> validtimelist = new HashMap<String, String>();
	protected long activetimeVal;

	// 弹出发布有效时间选择框
	public void validtimelistDialog(final ArrayList<GezitechEntity_I> list) {
		OptionDialog optionDialog = new OptionDialog(_this,
				R.style.dialog_load1, list, "有效时间选择", validtimelist, true,
				ItemType.Validtimelist);
		optionDialog.setOnOKButtonListener(new DialogSelectDataCallBack() {

			@Override
			public void onDataCallBack(HashMap<String, String> selectedList) {
				_this.validtimelist = selectedList;
				Set<String> a = (Set<String>) selectedList.keySet();
				String[] keyArray = (String[]) a.toArray(new String[] {});
				if (keyArray.length >= 1) {
					activetime = Integer.parseInt(keyArray[0]);
					// 计算选中时间秒数
					for (int i = 0; i < list.size(); i++) {
						Validtimelist item = (Validtimelist) list.get(i);
						if (item.id == activetime) {
							activetimeVal = item.activetime;
							break;
						}
					}
				}

				if (selectedList.size() == 0) {
					tv_releaseHyh_youxiaoshijian.setText("请选择");
				} else {
					Collection<String> b = selectedList.values();
					String[] strArray = (String[]) b.toArray(new String[] {});
					tv_releaseHyh_youxiaoshijian.setText(StringUtil
							.stringArrayJoin(strArray, ","));
				}
			}
		});
	}

	private void getCityList( final String typeName, final   HashMap<String, String> hm,final int type, final String title ){
		long parentIdVal = Long.parseLong( typeName );
		long nationalityidVal = range;//国家的id
		GezitechAlertDialog.loadDialog(_this);
		SystemManager.getInstance().getCityAreaStreet(parentIdVal, nationalityidVal ,new OnAsynGetListListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				Toast( errorMsg );
				GezitechAlertDialog.closeDialog();
			}
			
			@Override
			public void OnGetListDone(ArrayList<GezitechEntity_I> list) {
				GezitechAlertDialog.closeDialog();
				//if( type != 1 ){
					City city = new City();
					city.id = -1 ;
					city.name = "不限";
					city.parentId = Long.parseLong( typeName );
					city.level = type;
					list.add(0, city );
				//}
				
				regionalDialog( list,title ,hm , type );
			}
		});
	}
	
	private HashMap<String, String> guojiaList = new HashMap<String, String>();
	private HashMap<String, String> shengshiList = new HashMap<String, String>();
	private HashMap<String, String> shiquList = new HashMap<String, String>();
	private HashMap<String, String> quxianList = new HashMap<String, String>();
	private HashMap<String, String> jiedaoList = new HashMap<String, String>();

	// 弹出地域的选择框
	public void regionalDialog(final ArrayList<GezitechEntity_I> list,String typeName,  HashMap<String, String> hm,final int type ) {
		
		OptionDialog optionDialog = new OptionDialog(_this,
				R.style.dialog_load1, list, typeName, hm, true,
				ItemType.City);
		optionDialog.setOnOKButtonListener(new DialogSelectDataCallBack() {

			@Override
			public void onDataCallBack(HashMap<String, String> selectedList) {
				
				Set<String> a = (Set<String>) selectedList.keySet();
				String[] keyArray = (String[]) a.toArray(new String[] {});
				
				if( type == 1 ){
					_this.shengshiList = selectedList;
					if (keyArray.length >= 1) {
						provinces_nameVal = keyArray[0];
					}
	
					if (selectedList.size() == 0) {
						ed_shengshi.setText("未选择");
					} else {
						Collection<String> b = selectedList.values();
						String[] strArray = (String[]) b.toArray(new String[] {});
						ed_shengshi.setText(StringUtil
								.stringArrayJoin(strArray, ","));
					}
					
				}else if( type == 2 ){
					_this.shiquList = selectedList;
					if (keyArray.length >= 1) {
						urban_nameVal = keyArray[0];
					}
	
					if (selectedList.size() == 0) {
						ed_shiqu.setText("未选择");
					} else {
						Collection<String> b = selectedList.values();
						String[] strArray = (String[]) b.toArray(new String[] {});
						ed_shiqu.setText(StringUtil
								.stringArrayJoin(strArray, ","));
					}
				}else if( type == 3 ){
					_this.quxianList = selectedList;
					if (keyArray.length >= 1) {
						county_nameVal = keyArray[0];
					}
	
					if (selectedList.size() == 0) {
						ed_quxian.setText("未选择");
					} else {
						Collection<String> b = selectedList.values();
						String[] strArray = (String[]) b.toArray(new String[] {});
						ed_quxian.setText(StringUtil
								.stringArrayJoin(strArray, ","));
					}
				}else if( type == 4 ){
					_this.jiedaoList = selectedList;
					if (keyArray.length >= 1) {
						streets_nameVal = keyArray[0];
					}
	
					if (selectedList.size() == 0) {
						ed_jiedao.setText("未选择");
					} else {
						Collection<String> b = selectedList.values();
						String[] strArray = (String[]) b.toArray(new String[] {});
						ed_jiedao.setText(StringUtil
								.stringArrayJoin(strArray, ","));
					}
				}
				setDefaultValue( type );
			}
		});
	}
	private void setDefaultValue( int type ){
		
		switch( type ){
		case -1:
			ed_shengshi.setText("未选择");
			ed_shiqu.setText("未选择");
			ed_quxian.setText("未选择");
			ed_jiedao.setText( "未选择" );
			provinces_nameVal = "0";
			urban_nameVal = "0";
			county_nameVal = "0";
			streets_nameVal = "0";
			break;
		case 1:
			ed_shiqu.setText("未选择");
			ed_quxian.setText("未选择");
			ed_jiedao.setText( "未选择" );
			urban_nameVal = "0";
			county_nameVal = "0";
			streets_nameVal = "0";
			break;
		case 2:
			ed_quxian.setText("未选择");
			ed_jiedao.setText( "未选择" );
			county_nameVal = "0";
			streets_nameVal = "0";
			break;
		case 3:
			ed_jiedao.setText( "未选择" );
			streets_nameVal = "0";
			break;
		
		}
		
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {

		case R.id.bt_home_msg:
			finish();
			break;
		/*case R.id.ed_guojia  : //国家
			
			GezitechAlertDialog.loadDialog(_this);
			SystemManager.getInstance().getCountryList( new OnAsynGetListListener() {
				
				@Override
				public void OnAsynRequestFail(String errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					GezitechAlertDialog.closeDialog();
					Toast( errorMsg );
				}
				
				@Override
				public void OnGetListDone(ArrayList<GezitechEntity_I> list) {
					GezitechAlertDialog.closeDialog();
					OptionDialog optionDialog = new OptionDialog(_this,
							R.style.dialog_load1, list, "国家选择", guojiaList, true,
							ItemType.Country);
					optionDialog.setOnOKButtonListener(new DialogSelectDataCallBack() {

						@Override
						public void onDataCallBack(HashMap<String, String> selectedList) {
							
							Set<String> a = (Set<String>) selectedList.keySet();
							String[] keyArray = (String[]) a.toArray(new String[] {});
							
								_this.guojiaList = selectedList;
								if (keyArray.length >= 1) {
									country_nameVal = keyArray[0];
								}
								//Toast( selectedList.size()+"="+country_nameVal );
								if (selectedList.size() == 0) {
									ed_guojia.setText("未选择");
								} else {
									Collection<String> b = selectedList.values();
									String[] strArray = (String[]) b.toArray(new String[] {});
									ed_guojia.setText(StringUtil
											.stringArrayJoin(strArray, ","));
								}
								
							
							setDefaultValue( -1 );
						}
					});
					
					
				}
			});
			
			
			break;*/
			
		case R.id.ed_shengshi : //省市
			if( range<=0 ) return;
			getCityList("0",shengshiList, 1,"省市选择");
			break;
		case R.id.ed_shiqu: //市区
			if( provinces_nameVal.equals("") || provinces_nameVal.equals("-1") || Long.parseLong( provinces_nameVal )<=0 ) return;
			getCityList(provinces_nameVal,shiquList, 2,"市区选择");
			break;
		case R.id.ed_quxian://区县
			if( urban_nameVal.equals("") || urban_nameVal.equals("-1") || Long.parseLong( urban_nameVal )<=0 ) return;
			getCityList(urban_nameVal,quxianList, 3,"区县选择");
			break;
		case R.id.ed_jiedao://街道
			if( county_nameVal.equals("") || county_nameVal.equals("-1") || Long.parseLong( county_nameVal )<=0  ) return;
			getCityList(county_nameVal,jiedaoList, 4,"街道选择");
			break;
		default:
			break;
		}
	}
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		if( mHandler != null ){
			mHandler.removeCallbacks( count_down_voice_time );
			mHandler.removeCallbacks( mPollTask );
			
		}
	}
}
