package com.hyh.www.user;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynRequestFailListener;
import com.gezitech.contract.GezitechManager_I.OnAsynUpdateListener;
import com.gezitech.entity.User;
import com.gezitech.photo.PhotoAlbumActivity;
import com.gezitech.service.managers.SystemManager;
import com.gezitech.service.managers.UserManager;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.gezitech.util.IOUtil;
import com.gezitech.util.ImageUtil;
import com.gezitech.util.StringUtil;
import com.gezitech.widget.MoreOptionDialog;
import com.gezitech.widget.OptionDialog;
import com.gezitech.widget.OptionDialog.DialogSelectDataCallBack;
import com.gezitech.widget.OptionDialog.ItemType;
import com.gezitech.widget.RemoteImageView;
import com.hyh.www.R;
import com.hyh.www.ServiceActivity;
import com.hyh.www.ZhuyeActivity;
import com.hyh.www.chat.ChatActivity;
import com.hyh.www.entity.Companytype;
import com.hyh.www.entity.FieldVal;
import com.hyh.www.entity.Friend;
import com.hyh.www.widget.ImageShow;
import com.hyh.www.widget.YMDialog;
import com.loopj.android.http.RequestParams;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
/**
 * 
 * @author xiaobai
 * 2015-2-11
 * @todo( 成为服务者 )
 */
public class BecomeServiceActivity extends GezitechActivity implements
		OnClickListener {

	private BecomeServiceActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_title;
	private Button Login_login;
	private RelativeLayout becomeshangjia_qiyemingcheng;
	private TextView tv_becomeshangjia_qiyeleixingchoose;
	//private HashMap<String, String> typeList = new HashMap<String, String>();
	private Companytype typeList = new Companytype();
	private RemoteImageView iv_yinyezhizhao;
	private RemoteImageView iv_jingyingxukezheng;
	private RemoteImageView iv_changdizhaopian_one;
	private RemoteImageView iv_changdizhaopian_two;
	private RemoteImageView iv_changdizhaopian_three;
	private RemoteImageView iv_lianxirenzhaopian;
	private LinearLayout ll_changdizhaopian_box;
	private int isSubmit;
	private Button btn_becomeshangjia_yinyezhizhao;
	private Button btn_becomeshangjia_jingyingxukezheng;
	private Button btn_becomeshangjia_lianxirenzhaopian;
	private Button btn_becomeshangjia_changdizhaopian;
	public RequestParams params = new RequestParams();
	private String[] images = {};
	private int from = 0; ///0 来自 个人中心的编辑资料  1是来自 注册
	private EditText ed_shop_name;
	private EditText ed_numbers;
	private EditText ed_company_phone;
	//private EditText ed_time;
	private RelativeLayout rl_issend;
	private TextView tv_issend_val;
	private GezitechDBHelper<User> db;
	private TextView ed_time_start;
	private TextView ed_time_end;
	private CheckBox bt_remember_agree;
	private int bt_remember_agreeVal = 1;
	private EditText ed_company_zhanghao;
	private EditText ed_company_kaihuname;
	private EditText ed_company_kaihuhang;
	private final static int TIME_DIALOG_START = 0;
    private final static int TIME_DIALOG_END = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_become_service);
		isSubmit = _this.getIntent().getIntExtra("isSubmit", -1);
		from   = _this.getIntent().getIntExtra("from", 0); 
		db = new GezitechDBHelper<User>(User.class);
		GezitechApplication.selectPhontCount = 2;
		_init();
	}

	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);
		

		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText( "服务者认证" );//_this.getResources().getString(R.string.chengweishangjia));

		Login_login = (Button) findViewById(R.id.Login_login);
		Login_login.setOnClickListener(this);

		// 上传商家的认证资料
		ed_shop_name = (EditText) findViewById(R.id.ed_shop_name);
		ed_numbers = (EditText) findViewById(R.id.ed_numbers);
		ed_company_phone = (EditText) findViewById(R.id.ed_company_phone);
		//ed_time = (EditText) findViewById(R.id.ed_time);
		
		ed_time_start = (TextView) findViewById(R.id.ed_time_start);
		ed_time_end = (TextView) findViewById(R.id.ed_time_end);
		
		ed_time_start.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				_this.showDialog( TIME_DIALOG_START );
			}
		});
		ed_time_end.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				_this.showDialog( TIME_DIALOG_END );
			}
		});
		
		//用户的开户行 等信息
		
		ed_company_zhanghao = (EditText) findViewById( R.id.ed_company_zhanghao );
		ed_company_kaihuname = (EditText) findViewById( R.id.ed_company_kaihuname );
		ed_company_kaihuhang = (EditText) findViewById( R.id.ed_company_kaihuhang );
		
		
		rl_issend = (RelativeLayout) findViewById(R.id.rl_issend);
		tv_issend_val = (TextView) findViewById(R.id.tv_issend_val);
		tv_issend_val.setText("请选择");
		rl_issend.setOnClickListener(  this );
		
		// 类型
		tv_becomeshangjia_qiyeleixingchoose = (TextView) findViewById(R.id.tv_becomeshangjia_qiyeleixingchoose);
		tv_becomeshangjia_qiyeleixingchoose.setText("请选择");

		becomeshangjia_qiyemingcheng = (RelativeLayout) findViewById(R.id.becomeshangjia_qiyemingcheng);
		becomeshangjia_qiyemingcheng.setOnClickListener(this);

		// 营业执照
		iv_yinyezhizhao = (RemoteImageView) findViewById(R.id.iv_yinyezhizhao);
		btn_becomeshangjia_yinyezhizhao = (Button) findViewById(R.id.btn_becomeshangjia_yinyezhizhao);
		btn_becomeshangjia_yinyezhizhao
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent localIntent = new Intent();
						localIntent.setType("image/*");
						localIntent
								.setAction("android.intent.action.GET_CONTENT");
						Intent localIntent2 = Intent.createChooser(localIntent,
								"手持身份证照片");
						startActivityForResult(localIntent2, 1001);
					}
				});
		// 许可证
		iv_jingyingxukezheng = (RemoteImageView) findViewById(R.id.iv_jingyingxukezheng);
		btn_becomeshangjia_jingyingxukezheng = (Button) findViewById(R.id.btn_becomeshangjia_jingyingxukezheng);
		btn_becomeshangjia_jingyingxukezheng
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent localIntent = new Intent();
						localIntent.setType("image/*");
						localIntent
								.setAction("android.intent.action.GET_CONTENT");
						Intent localIntent2 = Intent.createChooser(localIntent,
								"上传特殊行业许可证");
						startActivityForResult(localIntent2, 1002);
					}
				});

		// 联系人照片
		iv_lianxirenzhaopian = (RemoteImageView) findViewById(R.id.iv_lianxirenzhaopian);
		btn_becomeshangjia_lianxirenzhaopian = (Button) findViewById(R.id.btn_becomeshangjia_lianxirenzhaopian);
		btn_becomeshangjia_lianxirenzhaopian
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent localIntent = new Intent();
						localIntent.setType("image/*");
						localIntent
								.setAction("android.intent.action.GET_CONTENT");
						Intent localIntent2 = Intent.createChooser(localIntent,
								"上传联系人照片");
						startActivityForResult(localIntent2, 1003);
					}
				});
		// 身份证正反面照片
		ll_changdizhaopian_box = (LinearLayout) findViewById(R.id.ll_changdizhaopian_box);
		btn_becomeshangjia_changdizhaopian = (Button) findViewById(R.id.btn_becomeshangjia_changdizhaopian);
		btn_becomeshangjia_changdizhaopian
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						/*// TODO Auto-generated method stub
						Intent intent = new Intent(_this,
								PhotoAlbumActivity.class);

						startActivityForResult(intent, 1004);*/
						
						if( params.has("company_placeshowone")  &&  params.has("company_placeshowtwo")  ){
							Toast("只能上传2张");
							return;
						}
						
						
						
						Intent localIntent = new Intent();
						localIntent.setType("image/*");
						localIntent
								.setAction("android.intent.action.GET_CONTENT");
						Intent localIntent2 = Intent.createChooser(localIntent,
								"身份证正反面照片");
						startActivityForResult(localIntent2, 1004);
						
		
					}
				});
		if (user != null && isSubmit > 0) {
			// 初始化企业类型
			if (user.companyTypeId > 0) {
				//typeList.put(user.companyTypeId + "", user.companyTypeName);
				typeList.id =   user.companyTypeId;
				typeList.typename = user.companyTypeName;
				typeList.pid = -1;
			}
			
			_initData();
		}
		
		if( from == 1 ){//来自注册
			bt_home_msg.setVisibility( View.GONE );
		}
		
		
		bt_remember_agree = (CheckBox) findViewById(R.id.bt_remember_agree);
		bt_remember_agree.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if( isChecked ){
					bt_remember_agreeVal = 1;
				}else{
					bt_remember_agreeVal = 0;
				}
				
			}
		});
		TextView tv_shop_service = (TextView) findViewById( R.id.tv_shop_service );
		tv_shop_service.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent( _this, ServiceActivity.class );
				intent.putExtra("from", 1);
				_this.startActivity( intent );
			}
		});
		
	}
	/**
     * 创建日期及时间选择对话框
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
        	case TIME_DIALOG_START://开始时间
        		String ed_time_startVal = ed_time_start.getText().toString().trim();
	            String start_time = ed_time_startVal.equals("") || ed_time_startVal.equals("开始") ? "09:00" : ed_time_startVal;
	            String[] start_time_arr = start_time.split(":");
	            dialog=new TimePickerDialog(
	                this,new TimePickerDialog.OnTimeSetListener(){
	                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	                    	String start_time_t = ( hourOfDay/10 <= 0 ? "0"+hourOfDay : hourOfDay )  +":"+ ( minute/10 <= 0 ? "0"+minute : minute );
	                    	ed_time_start.setText(  start_time_t  );
	                    }
	                },Integer.parseInt( start_time_arr[0] ), Integer.parseInt( start_time_arr[1] ) ,true
	            );
	            break;
        	case TIME_DIALOG_END://结束时间
        		String ed_time_endVal = ed_time_end.getText().toString().trim();
	            String end_time = ed_time_endVal.equals("") || ed_time_endVal.equals("结束") ? "21:00" : ed_time_endVal;
	            String[] end_time_arr = end_time.split(":");
	            dialog=new TimePickerDialog(
	                this, 
	                new TimePickerDialog.OnTimeSetListener(){
	                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	                    	String end_time_t = ( hourOfDay/10 <= 0 ? "0"+hourOfDay : hourOfDay )  +":"+ ( minute/10 <= 0 ? "0"+minute : minute );
	                    	ed_time_end.setText(  end_time_t  );
	                    }
	                },
	                Integer.parseInt( end_time_arr[0] ), Integer.parseInt( end_time_arr[1] ) ,
	                true
	            );
	            break;
        }
        return dialog;
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bitmap originBitMap = null, reduceBitMap = null;
		File originFile = null;
		String filePath = null;
		switch (requestCode) {
		case 1001:// 单选
			if (data == null)return;
			ContentResolver resolver = getContentResolver();
			Uri selectedImageUri = data.getData();
			if (selectedImageUri != null) {
				FileInputStream fis = null;
				try {
					
				
					
					/*fis = (FileInputStream) resolver.openInputStream(selectedImageUri);
					originBitMap = BitmapFactory.decodeStream(fis);

					int scale = ImageUtil.reckonThumbnail(originBitMap.getWidth(), originBitMap.getHeight(),600, 600);
					originBitMap = ImageUtil.PicZoom(originBitMap,(int) (originBitMap.getWidth() / scale),(int) (originBitMap.getHeight() / scale));
					originFile = IOUtil.makeLocalImage(originBitMap, null);*/
					
					try {
						 fis = (FileInputStream) resolver.openInputStream( selectedImageUri );
						 
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

					iv_yinyezhizhao.setImageBitmap(originBitMap);
					if (params.has("company_license")) {
						params.remove("company_license");
					}
					params.put("company_license", originFile);
					System.gc();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case 1002:// 单选
			ContentResolver resolver2 = getContentResolver();
			if (data == null)
				return;
			Uri selectedImageUri2 = data.getData();

			if (selectedImageUri2 != null) {
				FileInputStream fis = null;
				try {
					/*fis = (FileInputStream) resolver2
							.openInputStream(selectedImageUri2);
					originBitMap = BitmapFactory.decodeStream(fis);
					int scale = ImageUtil.reckonThumbnail(
							originBitMap.getWidth(), originBitMap.getHeight(),
							600, 600);
					originBitMap = ImageUtil.PicZoom(originBitMap,
							(int) (originBitMap.getWidth() / scale),
							(int) (originBitMap.getHeight() / scale));
					originFile = IOUtil.makeLocalImage(originBitMap, null);*/
					try {
						 fis = (FileInputStream) resolver2.openInputStream( selectedImageUri2 );
						 
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
					
					iv_jingyingxukezheng.setImageBitmap(originBitMap);
					if (params.has("company_certificate")) {
						params.remove("company_certificate");
					}
					params.put("company_certificate", originFile);
					System.gc();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case 1003:// 单选
			ContentResolver resolver3 = getContentResolver();
			if (data == null)
				return;
			Uri selectedImageUri3 = data.getData();

			if (selectedImageUri3 != null) {
				FileInputStream fis = null;
				try {
					/*fis = (FileInputStream) resolver3
							.openInputStream(selectedImageUri3);
					originBitMap = BitmapFactory.decodeStream(fis);
					int scale = ImageUtil.reckonThumbnail(
							originBitMap.getWidth(), originBitMap.getHeight(),
							600, 600);
					originBitMap = ImageUtil.PicZoom(originBitMap,
							(int) (originBitMap.getWidth() / scale),
							(int) (originBitMap.getHeight() / scale));
					originFile = IOUtil.makeLocalImage(originBitMap, null);*/
					
					try {
						 fis = (FileInputStream) resolver3.openInputStream( selectedImageUri3 );
						 
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
					
					
					iv_lianxirenzhaopian.setImageBitmap(originBitMap);
					if (params.has("company_userphoto")) {
						params.remove("company_userphoto");
					}
					params.put("company_userphoto", originFile );
					
					System.gc();
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case 1004:// 多选
			
			ContentResolver resolver4 = getContentResolver();
			if (data == null)
				return;
			Uri selectedImageUri4 = data.getData();

			if (selectedImageUri4 != null) {
				FileInputStream fis = null;
				try {
					
					try {
						 fis = (FileInputStream) resolver4.openInputStream( selectedImageUri4 );
						 
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
					//都不存在 则清空
					if(  !params.has("company_placeshowone")  &&  !params.has("company_placeshowtwo")   ){
						
						ll_changdizhaopian_box.removeAllViews();
						
					}
					int index  = -1;
					if ( !params.has("company_placeshowone") ) {
						//params.remove("company_placeshowone");
						params.put("company_placeshowone",  originFile );
						index = 0;
					}else if ( !params.has("company_placeshowtwo")) {
						//params.remove("company_placeshowtwo");
						params.put("company_placeshowtwo",  originFile );
						index = 1;
					}
					
					
					select_photo_item( "", originBitMap, true ,index );
					
					System.gc();
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			break;
		default:
			break;
		}
	}

/*
	private void addSelectPhotoParams(String paramsName, final int i) {
		Bitmap originBitMap = null, reduceBitMap = null;
		File originFile = null;
		String filePath = null;

		try {

			originBitMap = BitmapFactory.decodeFile(GezitechApplication.paths
					.get(i));
			int scale = ImageUtil.reckonThumbnail(originBitMap.getWidth(),
					originBitMap.getHeight(), 500, 500);
			originBitMap = ImageUtil.PicZoom(originBitMap,
					(int) (originBitMap.getWidth() / scale),
					(int) (originBitMap.getHeight() / scale));
			originFile = IOUtil.makeLocalImage(originBitMap, null);
			File file = new File( GezitechApplication.paths.get(i) ) ;
			FileInputStream inStream = null;
			try {
				inStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				 
				 byte[] buffer = new byte[inStream.available()];
				 inStream.read(buffer);
				 
				 originBitMap = ImageUtil.PicZoom2(buffer,600,600 );
				 
				 originFile = IOUtil.makeLocalImage(originBitMap, null);
				 
				 
			} catch (Exception e) {
				 e.printStackTrace();
			} finally {
				 try {
					 if (inStream != null)
						 inStream.close();
				   	} catch (IOException e) {
				   		e.printStackTrace();
				   }
			}	
			
		} catch (Exception e) {

			e.printStackTrace();

		}

		try {
			params.put(paramsName,  originFile );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
*/
	// 初始化数据
	private void _initData() {
		tv_becomeshangjia_qiyeleixingchoose
				.setText( !FieldVal.value( user.companyTypeName ).equals("") ? user.companyTypeName: "请选择");
		ed_shop_name.setText(  FieldVal.value( user.company_name ) );
		ed_numbers.setText( FieldVal.value(  user.IDnumber ) ) ;
		ed_company_phone.setText(FieldVal.value(user.company_tel) );
		//ed_time.setText( FieldVal.value(user.businesstime) );
		
		
		ed_company_zhanghao.setText( FieldVal.value(  user.account_number ) );
		ed_company_kaihuname.setText( FieldVal.value(  user.account_name) );
		ed_company_kaihuhang.setText( FieldVal.value(  user.account_bankname ) );
		
		
		if( !FieldVal.value(user.businesstime).equals("") ){
			String[] businesstime = ( FieldVal.value(user.businesstime) ).split("-");
			if( businesstime.length == 2 ){
				ed_time_start.setText( businesstime[0] );
				ed_time_end.setText( businesstime[1] );
			}
		}
		 
		tv_issend_val.setText(  FieldVal.getSend( user.isdelivery )   );
		sendVal = user.isdelivery;
		iv_yinyezhizhao.setImageUrl(user.company_license);
		if (!FieldVal.value(user.company_license).equals("")) {
			iv_yinyezhizhao.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 画廊展示图片
					final String[] images = new String[1];
					String[] pic = user.company_license.split("src=");
					images[0] = StringUtil.stringDecode(pic[1]);
					ImageShow.jumpDisplayPic(images, 0, _this);
				}
			});
		}
		iv_jingyingxukezheng.setImageUrl(user.company_certificate);
		if (!FieldVal.value(user.company_certificate).equals("")) {
			iv_jingyingxukezheng.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 画廊展示图片
					final String[] images = new String[1];
					String[] pic = user.company_certificate.split("src=");
					images[0] = StringUtil.stringDecode(pic[1]);
					ImageShow.jumpDisplayPic(images, 0, _this);
				}
			});
		}
		iv_lianxirenzhaopian.setImageUrl(user.company_userphoto);
		if (!FieldVal.value(user.company_userphoto).equals("")) {
			iv_lianxirenzhaopian.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 画廊展示图片
					final String[] images = new String[1];
					String[] pic = user.company_userphoto.split("src=");
					images[0] = StringUtil.stringDecode(pic[1]);
					ImageShow.jumpDisplayPic(images, 0, _this);
				}
			});
		}
		ll_changdizhaopian_box.removeAllViews();
		int index = -1;
		if (user.state == 1 || (user.state == 0 && isSubmit == 1)) {
			if (user.company_placeshowone != null
					&& !user.company_placeshowone.equals("")
					&& !user.company_placeshowone.equals("null")) {
				index++;
				select_photo_item(user.company_placeshowone, null, false, index);
			}
			if (user.company_placeshowtwo != null
					&& !user.company_placeshowtwo.equals("")
					&& !user.company_placeshowtwo.equals("null")) {
				index++;
				select_photo_item(user.company_placeshowtwo, null, false, index);
			}
			/*if (user.company_placeshowthree != null
					&& !user.company_placeshowthree.equals("")
					&& !user.company_placeshowthree.equals("null")) {
				index++;
				select_photo_item(user.company_placeshowthree, null, false,
						index);
			}*/
		}

		String msg = "";
		if (user.state == 1) {
			
			Login_login.setBackgroundResource(R.drawable.button_common_btn_gree);
			TextView tv_yuanyin = (TextView) findViewById(R.id.tv_yuanyin);
			tv_yuanyin.setVisibility(View.VISIBLE);
			tv_yuanyin.setText("审核通过,如需修改需要再次审核,谢谢合作!");
			Login_login.setEnabled(true);
			msg = "确认修改";
		} else if (user.state == -1) {
			TextView tv_yuanyin = (TextView) findViewById(R.id.tv_yuanyin);
			tv_yuanyin.setVisibility(View.VISIBLE);
			tv_yuanyin.setText("审核失败,请重新填写资料再次审核,谢谢合作!");
			Login_login.setEnabled(true);
			msg = "重新提交";
		} else if (isSubmit == 1 && user.state == 0) {
			msg = "等待审核";
			Login_login.setBackgroundResource(R.drawable.button_common_btn_red);
			// Login_login.setTextColor(_this.getResources().getColor(
			// R.color.white ) );
			Login_login.setEnabled(false);
			buttonEnabled();
		} else if (user.state == -2) {
			Login_login
					.setBackgroundResource(R.drawable.button_common_btn_blue);
			// Login_login.setTextColor(_this.getResources().getColor(
			// R.color.white ) );
			Login_login.setEnabled(true);
			msg = "提交认证";
		}

		Login_login.setText(msg);

	}

	private void buttonEnabled() {
		btn_becomeshangjia_yinyezhizhao.setEnabled(false);
		btn_becomeshangjia_jingyingxukezheng.setEnabled(false);
		btn_becomeshangjia_lianxirenzhaopian.setEnabled(false);
		btn_becomeshangjia_changdizhaopian.setEnabled(false);
	}

	// 图片的显示
	public void select_photo_item(final String imgUrl, Bitmap bitmap,
			boolean isDel, final int index) {
		LayoutInflater inflater = LayoutInflater.from(this);
		final View view = inflater.inflate(R.layout.select_photo_item, null);
		RemoteImageView iv_changdizhaopian = (RemoteImageView) view
				.findViewById(R.id.iv_changdizhaopian);
		ImageView play_del_xx = (ImageView) view.findViewById(R.id.iv_del);

		if (isDel) {// 是否显示del;
			play_del_xx.setVisibility(View.VISIBLE);
			play_del_xx.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ll_changdizhaopian_box.removeView( view );
					if( index == 0 ){
						if( params.has("company_placeshowone") ){
							params.remove("company_placeshowone");
						}
					}else if( index == 1 ){
						if( params.has("company_placeshowtwo") ){
							params.remove("company_placeshowtwo");
						}
					}
				}
			});
		} else {
			play_del_xx.setVisibility(View.GONE);
		}
		if (imgUrl.equals("")) {
			iv_changdizhaopian.setImageBitmap(bitmap);
		} else {
			int length = images.length;
			String[] imagess = new String[length + 1];
			for (int i = 0; i < images.length; i++) {
				imagess[i] = images[i];
			}
			String[] pic = imgUrl.split("src=");
			imagess[length] = StringUtil.stringDecode(pic[1]);
			images = imagess;
			iv_changdizhaopian.setImageUrl(imgUrl);
			iv_changdizhaopian.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 画廊展示图片

					ImageShow.jumpDisplayPic(images, index, _this);
				}
			});
		}
		ll_changdizhaopian_box.addView(view);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {

		case R.id.bt_home_msg:// 返回
			finish();
			break;
		case R.id.Login_login:// 提交商家认证
			_submitData();
			break;
		case R.id.becomeshangjia_qiyemingcheng:// 企业名称

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
			break;
		case R.id.rl_issend ://是否外送
			ArrayList<GezitechEntity_I> list = new ArrayList<GezitechEntity_I>();
			Companytype ct = new Companytype();
			ct.id = 1;
			ct.typename = "是";
			list.add( ct );
			ct = new Companytype();
			ct.id = 2;
			ct.typename = "否";
			list.add( ct );
			sendDialog( list );
			break;
		default:
			break;
		}

	}
	private HashMap<String, String> sendList = new HashMap<String, String>();
	protected int sendVal = -1;

	// 弹出是否外送选择框
	public void sendDialog(final ArrayList<GezitechEntity_I> list) {
		
		OptionDialog optionDialog = new OptionDialog(_this,
				R.style.dialog_load1, list, "是否上门服务", sendList, true,
				ItemType.Companytype);
		optionDialog.setOnOKButtonListener(new DialogSelectDataCallBack() {

			@Override
			public void onDataCallBack(HashMap<String, String> selectedList) {
				_this.sendList = selectedList;
				Set<String> a = (Set<String>) selectedList.keySet();
				String[] keyArray = (String[]) a.toArray(new String[] {});
				if (keyArray.length >= 1) {
					sendVal = Integer.parseInt(keyArray[0]);
				}

				if (selectedList.size() == 0) {
					tv_issend_val.setText("请选择");
				} else {
					Collection<String> b = selectedList.values();
					String[] strArray = (String[]) b.toArray(new String[] {});
					tv_issend_val.setText(StringUtil
							.stringArrayJoin(strArray, ","));
				}
			}
		});
	}
	// 弹出企业类型选择框
	public void typeDialog(ArrayList<GezitechEntity_I> list) {
		/*OptionDialog optionDialog = new OptionDialog(_this,
				R.style.dialog_load1, list, "企业类型选择", typeList, true,
				ItemType.Companytype);
		optionDialog.setOnOKButtonListener(new DialogSelectDataCallBack() {

			@Override
			public void onDataCallBack(HashMap<String, String> selectedList) {
				_this.typeList = selectedList;
				Set<String> a = (Set<String>) selectedList.keySet();
				String[] keyArray = (String[]) a.toArray(new String[] {});
				if (keyArray.length >= 1) {
					if (params.has("company_typeid"))
						params.remove("company_typeid");
					params.put("company_typeid", Integer.parseInt(keyArray[0]));
				}

				if (selectedList.size() == 0) {
					tv_becomeshangjia_qiyeleixingchoose.setText("请选择");
				} else {
					Collection<String> b = selectedList.values();
					String[] strArray = (String[]) b.toArray(new String[] {});
					tv_becomeshangjia_qiyeleixingchoose.setText(StringUtil
							.stringArrayJoin(strArray, ","));
				}
			}
		});*/
		MoreOptionDialog optionDialog = new MoreOptionDialog(_this,
				R.style.dialog_load1, list, "服务者类型选择", typeList);
		optionDialog.setOnOKButtonListener(new MoreOptionDialog.DialogSelectDataCallBack() {

			@Override
			public void onDataCallBack( GezitechEntity_I  selectedList) {
				typeList =  ( Companytype ) selectedList;
				if( typeList == null  ){
					tv_becomeshangjia_qiyeleixingchoose.setText("请选择");
					return;
				}else{
					params.remove("company_typeid");
					params.put("company_typeid", typeList.id );
					tv_becomeshangjia_qiyeleixingchoose.setText( typeList.typename );
				}
			}
		});
	}

	// 提交数据
	private void _submitData() {
		String ed_shop_nameval = ed_shop_name.getText().toString()
				.trim();
		if (ed_shop_nameval.equals("")) {
			Toast("服务者名称不能为空");
			return;
		}
		params.put("company_name", ed_shop_nameval);
		
		if (typeList == null ) {
			Toast("服务者类型未选择");
			return;
		}
		
		/*String ed_linkmanval = ed_linkman.getText().toString().trim();
		if (ed_linkmanval.equals("")) {
			Toast("联系人不能为空");
			return;
		}*/
		//params.put("touchname", ed_linkmanval);
		String ed_numbersval = ed_numbers.getText().toString()
				.trim();
		/*if (ed_numbersval.equals("") ) {
		Toast("身份证号码不能为空");
		return;
		}*/
		if( !ed_numbersval.equals("") && ed_numbersval.length()<17 ){
			Toast("身份证号码格式错误");
			return;
		}
		
		params.put("IDnumber", ed_numbersval);
		
		String ed_company_phoneval = ed_company_phone.getText()
				.toString().trim();
		if (ed_company_phoneval.equals("")) {
			Toast("联系号码不能为空");
			return;
		}
		params.put("company_tel", ed_company_phoneval);
		
		/*String ed_timeval = ed_time.getText()
				.toString().trim();*/
		
		//开户行信息		
		String ed_company_zhanghaoVal = ed_company_zhanghao.getText().toString().trim(); 
		if( ed_company_zhanghaoVal.equals("") || ed_company_zhanghaoVal.length()<8 ){
			Toast( "开户账号格式错误" );
			return;
		}
		String ed_company_kaihunameVal = ed_company_kaihuname.getText().toString().trim(); 
		if( ed_company_kaihunameVal.equals("") || ed_company_kaihunameVal.length()<2 ){
			Toast( "开户名格式错误" );
			return;
		}
		String ed_company_kaihuhangVal = ed_company_kaihuhang.getText().toString().trim(); 
		if( ed_company_kaihuhangVal.equals("") || ed_company_kaihuhangVal.length()<4 ){
			Toast( "开户行格式错误" );
			return;
		}
		params.put("account_name", ed_company_kaihunameVal );
		params.put("account_number",  ed_company_zhanghaoVal  );
		params.put("account_bankname", ed_company_kaihuhangVal );
				
		
		String ed_time_startVal = ed_time_start.getText().toString().trim();
		String ed_time_endVal = ed_time_end.getText().toString().trim();
		
		if (ed_time_startVal.equals("") || ed_time_startVal.equals("开始") ) {
			Toast("营业开始时间不能为空");
			return;
		}
		if (ed_time_endVal.equals("") || ed_time_endVal.equals("结束") ) {
			Toast("营业结束时间不能为空");
			return;
		}
		params.put("businesstime", ed_time_startVal+"-"+ed_time_endVal );
		
		if( sendVal < 0 ){
			
			Toast("是否上门服务未选择");
			return;
		}
		params.put("isdelivery", sendVal);

		if ( FieldVal.value(user.company_license).equals("") && !params.has("company_license")) {
			Toast("手持身份证照片未上传");
			return;
		}
		//选填
		/*if ( FieldVal.value(user.company_certificate).equals("") &&  !params.has("company_certificate")) {
			Toast("经营许可证未上传");
			return;
		}*/
		if ( FieldVal.value(user.company_userphoto).equals("") &&  !params.has("company_userphoto")) {
			Toast("联系人照片未上传");
			return;
		}
		if (  FieldVal.value(user.company_placeshowone).equals("") && FieldVal.value(user.company_placeshowtwo).equals("") &&  !params.has("company_placeshowone")) {
			Toast("身份证正反面照片未上传");
			return;
		}
		
		if( bt_remember_agreeVal<=0 ){
			
			Toast("未同意商家服务协议");
			return;
			
		}
		
		if( from == 1 ){
			_submitDataServer();
		}else{
			String hintMsg = "";
			String buttonMsg = "";
			if( user.state == -2 ){
				hintMsg = "确定要提交审核?";
				buttonMsg = "确认提交";
			}else if( user.state == -1 ) { 
				hintMsg = "确定要重新提交审核?";
				buttonMsg = "确认提交";
			}else{
				hintMsg = "修改资料需要重新审核,您确定要修改资料?";
				buttonMsg = "确认修改";
			}
			final YMDialog ym = new YMDialog( _this );
			ym.setHintMsg( hintMsg )
			.setCloseButton("不了,谢谢", new OnClickListener() {
				@Override
				public void onClick(View v) {
					ym.dismiss();
					//loadKefuList();
					
				}
			}).setConfigButton( buttonMsg, new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ym.dismiss();
					_submitDataServer();
				}
			});
			
		}
	
	}
	private void _submitDataServer(){
		GezitechAlertDialog.loadDialog(this);
		params.put("auth_type", 1 );
		UserManager.getInstance().submitretailers(params,
				new OnAsynUpdateListener() {

					@Override
					public void OnAsynRequestFail(String errorCode,
							String errorMsg) {
						// TODO Auto-generated method stub
						GezitechAlertDialog.closeDialog();
						Toast(errorMsg);
					}

					@Override
					public void onUpdateDone(String id) {
						// TODO Auto-generated method stub
						GezitechAlertDialog.closeDialog();
						//修改本地的状态
						user.companystate = 1;
						user.auth_type = 1;
						db.save( user );
						
						//Toast("提交资料成功,等待审核");
						if( from == 1 ){//来自注册的提交
							final YMDialog ymdialog1 = new YMDialog( _this );
							ymdialog1.setHintMsg("你已成功申请成为喊一喊服务者，你可以使用喊一喊部分服务者功能,如需使用全部功能，可以申请认证，成为认证服务者，是否立即认证?")
							.setCloseButton("立即认证", new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									updateauthenticatestate( ymdialog1 );
									//registerSubmit();
								}
							})
							.setConfigButton("不了,谢谢", new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									ymdialog1.dismiss();
									_this.startActivity( new Intent(_this,ZhuyeActivity.class ) );
									finish();
								}
							});
						}else{
							Toast("提交资料成功,等待审核");
							Login_login.setBackgroundResource(R.drawable.button_common_btn_red);
							Login_login.setEnabled(false);
							buttonEnabled();
							updateauthenticatestate( null );
						}
					}
				});
		
	}
	private void registerSubmit(){
		final YMDialog ym = new YMDialog( _this );
		ym.setHintMsg("您已成功提交申请服务者认证，稍后会有我们当地代理商与您联系，请确认您的联系方式正确，详情可联系我们的客服咨询。")
		.setCloseButton("联系客服", new OnClickListener() {
			@Override
			public void onClick(View v) {
				//ym.dismiss();
				loadKefuList();
				
			}
		}).setConfigButton("确认申请", new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				ym.dismiss();
				_this.startActivity( new Intent(_this,ZhuyeActivity.class ) );
				finish();
				
			}
		});
	}
	private void updateauthenticatestate(final YMDialog ym){
		GezitechAlertDialog.loadDialog( _this );
		RequestParams paramss = new RequestParams();
		UserManager.getInstance().updateauthenticatestate(paramss, new OnAsynRequestFailListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				GezitechAlertDialog.closeDialog();
				if( errorCode.equals("1") ){
					if( from == 1){
						ym.dismiss();
						/*_this.startActivity( new Intent(_this,ZhuyeActivity.class ) );
						finish();*/
						registerSubmit();
					}else{
						
					}
				}else{
					//Toast( errorMsg );
				}
			}
		});
	}
	/**
	 * 
	 * TODO(联系客服)
	 */
	public void loadKefuList(){
		GezitechAlertDialog.loadDialog( _this );
		UserManager.getInstance().getkefu( new OnAsynGetOneListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				GezitechAlertDialog.closeDialog();
				Toast( errorMsg );
			}
			
			@Override
			public void OnGetOneDone(GezitechEntity_I entity) {
				GezitechAlertDialog.closeDialog();
				Friend item = (Friend) entity;
				//跳转到聊天界面
				Intent intent = new Intent(_this, ChatActivity.class );
				intent.putExtra("uid", item.fid);
				
				intent.putExtra("username", item.nickname == null || item.nickname.equals("null") || item.nickname.equals("") ? item.username : item.nickname );
				
				intent.putExtra("head", item.head);
				
				intent.putExtra("isfriend", 3 );
				intent.putExtra("isbusiness", item.isbusiness );
				
				_this.startActivity( intent );
				
				
			}
		});
	}
}
