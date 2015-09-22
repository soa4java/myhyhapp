package com.hyh.www.user;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynProgressListener;
import com.gezitech.contract.GezitechManager_I.OnAsynUpdateListener;
import com.gezitech.entity.User;
import com.gezitech.service.GezitechService;
import com.gezitech.service.managers.SystemManager;
import com.gezitech.service.managers.UserManager;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.gezitech.util.DateUtils;
import com.gezitech.util.StringUtil;
import com.gezitech.widget.OptionDialog;
import com.gezitech.widget.RemoteImageView;
import com.gezitech.widget.RemoteImageView.setBitmapListener;
import com.gezitech.widget.SelectPicPopupWindow;
import com.gezitech.widget.OptionDialog.DialogSelectDataCallBack;
import com.gezitech.widget.OptionDialog.ItemType;
import com.hyh.www.R;
import com.hyh.www.ZhuyeActivity;
import com.hyh.www.entity.City;
import com.hyh.www.entity.Companytype;
import com.hyh.www.entity.FieldVal;
import com.hyh.www.entity.Releasescope;
import com.hyh.www.widget.YMDialog;
import com.hyh.www.widget.YMDialog2;
import com.loopj.android.http.RequestParams;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 
 * @author xiaobai
 * (编辑资料)
 */
public class EditDataActivity extends GezitechActivity implements OnClickListener{

	private EditDataActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_title;
	private Button shangjia;
	private GezitechDBHelper<User> db;
	private RemoteImageView iv_replace_picture;
	private TextView tv_replace_picture;
	private EditText ed_nickname;
	private RelativeLayout replace_picture;
	private EditText ed_real_name;
	//private EditText ed_Idcard_number;
	/*private EditText ed_telephone;*/
	private EditText ed_zuoji;
	private EditText ed_youxiang;
	private EditText ed_dizhi;
	private Button btn_editdata;
	private RelativeLayout rl_sex;
	private TextView tv_sex_val;
	private int from = 0; ///0 来自 个人中心的编辑资料  1是来自 注册
	private EditText ed_phone;
	private Button service;
	private TextView tv_chengweishangjia;
	private LinearLayout tv_chengweiservice;
	private TextView ed_shengshi;
	private TextView ed_shiqu;
	private TextView ed_quxian;
	private TextView ed_jiedao;
	private String provinces_nameVal="",urban_nameVal="",county_nameVal="",streets_nameVal="",country_nameVal="";
	private TextView ed_guojia;
	private String userHead = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_data);
		db = new GezitechDBHelper<User>(User.class);
		from   = _this.getIntent().getIntExtra("from", 0); 
		userHead  = _this.getIntent().hasExtra("userHead") ? _this.getIntent().getStringExtra("userHead") : "";
		_init();
		if( from == 1 || from == 2 ){
			GezitechAlertDialog.loadDialog( _this );
			UserManager.getInstance().gainuserinfo( new OnAsynGetOneListener() {
				
				@Override
				public void OnAsynRequestFail(String errorCode, String errorMsg) {
					GezitechAlertDialog.closeDialog();
				}
				@Override
				public void OnGetOneDone(GezitechEntity_I entity) {
					GezitechAlertDialog.closeDialog();
					user =  GezitechService.getInstance().getCurrentLoginUser( _this );
					initUserData( false );
				}
			});
		}
	}

	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);
		
		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);
		
		TextView tv_title = (TextView) findViewById( R.id.tv_title );
		tv_title.setText( _this.getResources().getString( R.string.bianjiziliao ) );
		
		shangjia=(Button)findViewById(R.id.btn_chengweishangjia);
		service=(Button)findViewById(R.id.btn_chengweiservice);
		
		tv_replace_picture = (TextView) _this.findViewById( R.id.tv_replace_picture );
		tv_replace_picture.setOnClickListener( uploadHead );		
		iv_replace_picture = (RemoteImageView) _this.findViewById( R.id.iv_replace_picture );		
		iv_replace_picture.setOnClickListener( uploadHead );
		replace_picture = (RelativeLayout) _this.findViewById( R.id.replace_picture );
		replace_picture.setOnClickListener( uploadHead );
		
		btn_editdata = (Button)findViewById(R.id.btn_editdata);
		btn_editdata.setOnClickListener( this );
		
		ed_nickname = (EditText) _this.findViewById( R.id.ed_nickname ); 
		ed_real_name = (EditText) _this.findViewById( R.id.ed_real_name ); 
		//ed_Idcard_number= (EditText) _this.findViewById( R.id.ed_Idcard_number ); 
		ed_phone= (EditText) _this.findViewById( R.id.ed_phone );
		ed_zuoji= (EditText) _this.findViewById( R.id.ed_zuoji ); 
		ed_youxiang= (EditText) _this.findViewById( R.id.ed_youxiang ); 
		ed_dizhi= (EditText) _this.findViewById( R.id.ed_dizhi ); 
		tv_chengweishangjia = (TextView) _this.findViewById( R.id.tv_chengweishangjia );
		tv_chengweiservice = (LinearLayout) _this.findViewById( R.id.ll_chengweiservice );
		//地区的选择
		ed_guojia  = (TextView) _this.findViewById( R.id.ed_guojia );
		ed_guojia.setOnClickListener( this );
		ed_shengshi  = (TextView) _this.findViewById( R.id.ed_shengshi );
		ed_shengshi.setOnClickListener( this );
		ed_shiqu = (TextView) _this.findViewById( R.id.ed_shiqu );
		ed_shiqu.setOnClickListener( this );
		ed_quxian = (TextView) _this.findViewById( R.id.ed_quxian );
		ed_quxian.setOnClickListener( this );
		ed_jiedao = (TextView) _this.findViewById( R.id.ed_jiedao ); 
		ed_jiedao.setOnClickListener( this );
		//性别
		rl_sex = ( RelativeLayout ) _this.findViewById( R.id.rl_sex );
		tv_sex_val = (TextView) _this.findViewById( R.id.tv_sex_val );
		//选择性别
		rl_sex.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ArrayList<GezitechEntity_I> list = new ArrayList<GezitechEntity_I>();
				Companytype ct = new Companytype();
				ct.id = 1;
				ct.typename = "男";
				list.add( ct );
				ct = new Companytype();
				ct.id = 2;
				ct.typename = "女";
				list.add( ct );
				sexDialog( list );
			}
		});
			
		
		if( from == 1 ){ //来自注册
			shangjia.setVisibility( View.GONE );
			service.setVisibility( View.GONE );
			bt_home_msg.setVisibility( View.GONE );
			btn_editdata.setText("确定");
		}else if(  from == 2  ){
			
			shangjia.setVisibility( View.GONE );
			service.setVisibility( View.GONE );
			bt_home_msg.setVisibility( View.GONE );
			ed_phone.setEnabled( true );
			btn_editdata.setText("确定");
			
			
		} else if( from == 0 ){//来自个人中心
					
			shangjia.setOnClickListener(this);
			service.setOnClickListener(this);
			tv_chengweishangjia.setVisibility( View.VISIBLE );
			tv_chengweiservice.setVisibility( View.VISIBLE );
			if( user.isbusiness>0 ){//是商家
				tv_chengweiservice.setVisibility( View.GONE );
				tv_chengweishangjia.setVisibility( View.GONE );
				if( user.auth_type == 0 ){ 
					shangjia.setText("已是商家");
					service.setVisibility( View.GONE );
				}
				else if( user.auth_type == 1 ){
					service.setText("已是服务者");
					
					shangjia.setVisibility( View.GONE );
				}
			}else if( user.companystate == 1  ){//登记过
				if( user.auth_type == 0 ){ 
					shangjia.setText("成为商家");
					tv_chengweiservice.setVisibility( View.GONE );
					service.setVisibility( View.GONE );
				}
				else if( user.auth_type == 1 ){
					service.setText("成为服务者");
					tv_chengweishangjia.setVisibility( View.GONE );
					shangjia.setVisibility( View.GONE );
				}
				
			}
			
		}
		initUserData( true );
	}
	private void initUserData(final boolean isupload ){
		if( user != null ){ //填充值
			if( FieldVal.value( user.head ).equals("") || user.head.lastIndexOf( "head" ) >= user.head.length()-8  ){
				if( userHead!=null && !userHead.equals("") ){
					
					iv_replace_picture.setImageUrl( userHead ,false , false, new setBitmapListener() {
						
						@Override
						public void bitmap(Bitmap bm) {
							if( isupload ) uploadHead( bm , isupload );
						}
						
					});
				}
			}else{
				iv_replace_picture.setImageUrl( user.head );
			}
			ed_nickname.setText( !FieldVal.value( user.nickname ).equals("") ?  user.nickname : "" );
			ed_real_name.setText( !FieldVal.value( user.realname ).equals("") ?  user.realname : "" );
			//ed_Idcard_number.setText( user.IDnumber !=null && !user.IDnumber.equals("") && !user.IDnumber.equals("null") ?  user.IDnumber : "" );
			/*ed_telephone.setText( user.phone !=null && !user.phone.equals("") && !user.phone.equals("null") ?  user.phone : "" );*/
			//ed_zuoji.setText( user.tel !=null && !user.tel.equals("") && !user.tel.equals("null") ?  user.tel : "" );
			//ed_youxiang.setText( user.email !=null && !user.email.equals("") && !user.email.equals("null") ?  user.email : "" );
			
			tv_sex_val.setText( FieldVal.getSex( user.sex ) );
			ed_phone.setText( FieldVal.value( user.phone ) );
			sexVal = user.sex ;
			ed_shengshi.setText( FieldVal.value( user.provinces_name ).equals("")  ? "未选择" : user.provinces_name );
			provinces_nameVal  = FieldVal.value( user.provinces );
			ed_shiqu.setText( FieldVal.value( user.urban_name ).equals("") ? "未选择" : user.urban_name );
			urban_nameVal = FieldVal.value( user.urban );
			ed_quxian.setText( FieldVal.value( user.county_name ).equals("") ? "未选择" : user.county_name  );
			county_nameVal = FieldVal.value( user.county );
			ed_jiedao.setText( FieldVal.value( user.streets_name ).equals("") ? "未选择" : user.streets_name );
			streets_nameVal = FieldVal.value( user.streets );
			
			ed_guojia.setText( FieldVal.value( user.country_name ).equals("") ? "未选择" : user.country_name );
			country_nameVal = FieldVal.value( user.country );
			
		}
		//自动搜索当前地址
		if( user != null &&  !FieldVal.value( user.address ).equals("") ){
			ed_dizhi.setText( FieldVal.value( user.address ) );
		}else{
			GezitechApplication.getInstance().getBDLocation( new BDLocationListener() {
				
				@Override
				public void onReceiveLocation(final BDLocation arg0) {
					GezitechApplication.getInstance().setBDLocation( arg0 );
					String addrStr = arg0.getAddrStr();
					ed_dizhi.setText( addrStr == null ? "" : addrStr );
					
				};
			});
		}
	}
	private HashMap<String, String> sexList = new HashMap<String, String>();
	protected int sexVal = 0;

	// 弹出发布范围选择框
	public void sexDialog(final ArrayList<GezitechEntity_I> list) {
		
		OptionDialog optionDialog = new OptionDialog(_this,
				R.style.dialog_load1, list, "性别选择", sexList, true,
				ItemType.Companytype);
		optionDialog.setOnOKButtonListener(new DialogSelectDataCallBack() {

			@Override
			public void onDataCallBack(HashMap<String, String> selectedList) {
				_this.sexList = selectedList;
				Set<String> a = (Set<String>) selectedList.keySet();
				String[] keyArray = (String[]) a.toArray(new String[] {});
				if (keyArray.length >= 1) {
					sexVal = Integer.parseInt(keyArray[0]);
				}

				if (selectedList.size() == 0) {
					tv_sex_val.setText("请选择");
				} else {
					Collection<String> b = selectedList.values();
					String[] strArray = (String[]) b.toArray(new String[] {});
					tv_sex_val.setText(StringUtil
							.stringArrayJoin(strArray, ","));
				}
			}
		});
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bt_home_msg: //返回
			_returnData();
			break;
		case R.id.btn_chengweishangjia: //成为商家
			GezitechAlertDialog.loadDialog( _this );
			UserManager.getInstance().getcompanyinfo(user.id, true , new OnAsynGetOneListener(){
				@Override
				public void OnAsynRequestFail(String errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					GezitechAlertDialog.closeDialog();
					Intent intent = new Intent(_this, BecomeShangjiaActivity.class);
					intent.putExtra("isSubmit", -1);
					_this.startActivity( intent );//未提交
				}
				@Override
				public void OnGetOneDone(GezitechEntity_I entity) {
					GezitechAlertDialog.closeDialog();
					Intent intent = new Intent(_this, BecomeShangjiaActivity.class);
					intent.putExtra("isSubmit", 1);
					_this.startActivity( intent );//提交过
				}
			});
			
			break;
		case R.id.btn_chengweiservice: //成为服务者
			GezitechAlertDialog.loadDialog( _this );
			UserManager.getInstance().getcompanyinfo(user.id, true , new OnAsynGetOneListener(){
				@Override
				public void OnAsynRequestFail(String errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					GezitechAlertDialog.closeDialog();
					Intent intent = new Intent(_this, BecomeServiceActivity.class);
					intent.putExtra("isSubmit", -1);
					_this.startActivity( intent );//未提交
				}
				@Override
				public void OnGetOneDone(GezitechEntity_I entity) {
					GezitechAlertDialog.closeDialog();
					Intent intent = new Intent(_this, BecomeServiceActivity.class);
					intent.putExtra("isSubmit", 1);
					_this.startActivity( intent );//提交过
				}
			});
			
			break;
		case R.id.btn_editdata : //修改资料
			_updateUserInfo();
			break;
		case R.id.ed_guojia  : //国家
			
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
			
			
			break;
			
		case R.id.ed_shengshi : //省市
			if( country_nameVal.equals("") || country_nameVal.equals("-1") || Long.parseLong( country_nameVal )<=0 ) return;
			getCityList( "0" ,shengshiList, 1,"省市选择");
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
	
	
	private void getCityList( final String typeName, final   HashMap<String, String> hm,final int type, final String title ){
		long parentIdVal = Long.parseLong( typeName );
		GezitechAlertDialog.loadDialog(_this);
		long nationalityidVal = Long.parseLong( country_nameVal );
		SystemManager.getInstance().getCityAreaStreet( parentIdVal,  nationalityidVal,  new OnAsynGetListListener() {
			
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
	//修改用户资料
	private void _updateUserInfo(){
		final String ed_nickname_val = ed_nickname.getText().toString().trim();
		final String ed_real_name_val = ed_real_name.getText().toString().trim();
		//final String ed_Idcard_number_val = ed_Idcard_number.getText().toString().trim();
		final String ed_telephone_val = ed_phone.getText().toString().trim();
		final String ed_zuoji_val = ed_zuoji.getText().toString().trim();
		final String ed_youxiang_val = ed_youxiang.getText().toString().trim();
		final String ed_dizhi_val = ed_dizhi.getText().toString().trim();
		if( user.isbusiness>0 ){ //如果是商家
			
			if( ed_nickname_val.equals("")  ){
				Toast("昵称不能为空");
				return;
			}
			if( ed_nickname_val.length()<2 || ed_nickname_val.length()>10 ){
				Toast("昵称2-10位");
				return;
			}
			if( ed_real_name_val.equals("") ){
				Toast("真实姓名不能为空");
				return;
			}
			/*if( ed_zuoji_val.equals("") ){
				Toast("座机不能为空");
				return;
			}*/
			
			/*if( ed_youxiang_val.equals("") ){
				Toast("邮箱不能为空");
				return;
			}*/
			if( ed_dizhi_val.equals("") ){
				Toast("地址不能为空");
				return;
			}
			if( sexVal <= 0 ){
				Toast("性别未选择");
				return;
			}
		} 
		RequestParams params = new RequestParams();
		params.put("nickname", ed_nickname_val);
		params.put("realname", ed_real_name_val);
		if( ed_telephone_val.length()<10 || ed_telephone_val.length() >12 ){
			Toast("手机格式错误！");
			return;
		}
		
		if( country_nameVal.equals("")  || country_nameVal.equals("0") ){
			
			Toast("国家未选择");
			return;
			
		}
		
		/*if( provinces_nameVal.equals("")  ){
			
			Toast("省市未选择");
			return;
			
		}*/
		
		params.put("country", country_nameVal );
		params.put("provinces", provinces_nameVal.equals("") ? "-1" : provinces_nameVal );
		params.put("urban", urban_nameVal.equals("") ? "-1" : urban_nameVal );
		params.put("county", county_nameVal.equals("") ? "-1" : county_nameVal  );
		params.put("streets", streets_nameVal.equals("") ? "-1" : streets_nameVal );
		
		params.put("phone", ed_telephone_val);
		params.put("address", ed_dizhi_val);
		//params.put("IDnumber", ed_Idcard_number_val);
		params.put("email", ed_youxiang_val);
		params.put("tel", ed_zuoji_val);
		params.put("sex", sexVal );
		GezitechAlertDialog.loadDialog( this );
		UserManager.getInstance().updateUserInfo(params, new OnAsynUpdateListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				GezitechAlertDialog.closeDialog();
				Toast( errorMsg );
			}
			
			@Override
			public void onUpdateDone(String id) {
				GezitechAlertDialog.closeDialog();
				//Toast( _this.getString( R.string.save_userinfo_success) );
				user.nickname = ed_nickname_val;
				user.realname = ed_real_name_val;
				user.phone = ed_telephone_val;
				user.address = ed_dizhi_val;
				//user.IDnumber = ed_Idcard_number_val;
				user.email = ed_youxiang_val;
				user.tel = ed_zuoji_val;	
				user.sex = sexVal;
				//地域的保存
				user.provinces = provinces_nameVal;
				user.provinces_name = ed_shengshi.getText().toString();
				user.urban = urban_nameVal;
				user.urban_name =  ed_shiqu.getText().toString();
				user.county = county_nameVal;
				user.county_name =  ed_quxian.getText().toString();
				user.streets = streets_nameVal;
				user.streets_name =  ed_jiedao.getText().toString();
				user.country = country_nameVal;
				user.country_name =  ed_guojia.getText().toString();
				db.save( user );	
				if( from == 1 ){//来自注册
					
					//如果是商家 则跳转到商家的认证
					if( user.isbusiness > 0 ){
						ArrayList<GezitechEntity_I> list = new ArrayList<GezitechEntity_I>();
						Companytype ct = new Companytype();
						ct.id = 1;
						ct.typename = "商家认证";
						list.add( ct );
						ct = new Companytype();
						ct.id = 2;
						ct.typename = "服务者认证";
						list.add( ct );
						authDialog( list );
						
						
						
						
						
						/*final YMDialog ymdialog1 = new YMDialog( _this );
						ymdialog1.setHintMsg("你已成功申请成为喊一喊商家，你可以使用喊一喊部分商家功能,如需使用全部功能，可以申请认证，成为认证商家，是否立即认证?")
						.setCloseButton("立即认证", new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								ymdialog1.dismiss();*/
						/*Intent intent = new Intent(_this, BecomeShangjiaActivity.class);
						intent.putExtra("isSubmit", -1);
						intent.putExtra("from", 1);
						_this.startActivity( intent );*/
						/*	}
						})
						.setConfigButton("不了,谢谢", new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								ymdialog1.dismiss();
							}
						});*/
					}else{
					
						//如果是普通用户则进去主页
						_this.startActivity(new Intent(_this, ZhuyeActivity.class));
						_this.finish();
					}
				}else if( from == 2 ){
					
					_this.startActivity(new Intent(_this, ZhuyeActivity.class));
					_this.finish();
					
				}else{
					_returnData();
				}
				
			}
		});	
	}
	private HashMap<String, String> authList = new HashMap<String, String>();
	protected int authVal = 0;

	// 弹出发布范围选择框
	public void authDialog(final ArrayList<GezitechEntity_I> list) {
		
		OptionDialog optionDialog = new OptionDialog(_this,
				R.style.dialog_load1, list, "选择认证类型", authList, true,
				ItemType.Companytype);
		optionDialog.setOnOKButtonListener(new DialogSelectDataCallBack() {

			@Override
			public void onDataCallBack(HashMap<String, String> selectedList) {
				_this.authList = selectedList;
				Set<String> a = (Set<String>) selectedList.keySet();
				String[] keyArray = (String[]) a.toArray(new String[] {});
				if (keyArray.length >= 1) {
					authVal = Integer.parseInt(keyArray[0]);
				}
				if( authVal == 1){//商家认证
					Intent intent = new Intent(_this, BecomeShangjiaActivity.class);
					intent.putExtra("isSubmit", -1);
					intent.putExtra("from", 1);
					_this.startActivity( intent );
				}else if( authVal == 2 ){//个人服务者认证
					Intent intent = new Intent(_this, BecomeServiceActivity.class);
					intent.putExtra("isSubmit", -1);
					intent.putExtra("from", 1);
					_this.startActivity( intent );
					
				}
				
			}
		});
	}
	/***
	 * 头像的上传实例
	 */
	private String ImageName;
	OnClickListener  uploadHead = new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			
			_this.startActivityForResult(new Intent(_this,
					SelectPicPopupWindow.class), 10002);
			_this.overridePendingTransition(R.anim.out_to_down, R.anim.exit_anim);	
		}	
	};
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if( requestCode != 102 )
		if(data == null )return;
		
		switch (requestCode) {
		case 102://相机
			// 设置文件保存路径这里放在跟目录下
			File picture = new File(Environment.getExternalStorageDirectory()
					+ ImageName);
			startPhotoZoom(Uri.fromFile(picture));
			break;
		case 103://相册
			startPhotoZoom(data.getData());
			break;
		case 104://上传图片
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				uploadHead(photo, false );
				iv_replace_picture.setImageBitmap( photo );
			}
			break;
		case 10002://上传图片
			
			String action = data.getAction();
			
			if (action.equals("10001")) {// 拍照
				ImageName = "/" + getStringToday() + ".jpg";
	         	//启动相机
				Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				//来存储图片   
				//Uri.fromFile  把文件路径地址转为url地址
				//Uri.parse(uriString);  把url地址转为文件存储地址
				intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(
						new File( Environment.getExternalStorageDirectory(), ImageName) ) );
				startActivityForResult(intent1, 102);

			} else if (action.equals("10002")) {// 相册
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
				// 调用剪切功能
				startActivityForResult(intent,103);
			}
			
			
			break;
		default:
			break;
		}
	}
	//相片的剪辑
	public void startPhotoZoom(Uri uri) {
		Log.v("uri", uri+"");
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");  //打开文件
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 104);
	}
	//上传头像至服务器
	public void uploadHead(final Bitmap photo, final boolean isupload){
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
		if( !isupload ) GezitechAlertDialog.loadDialog( _this );
		UserManager.getInstance().uploadhead(params, new OnAsynProgressListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				GezitechAlertDialog.closeDialog();
				if( errorCode.equals("-1") ){
					Toast( errorMsg );
				}
			}
			@Override
			public void onUpdateDone(String id) {
				GezitechAlertDialog.closeDialog();
				if( !id.equals("") ){
					
					user.head = id;	
					db.save(user);
					Toast( _this.getString( R.string.upload_head_success ) );
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
	@Override
	public void onBackPressed() {
		if( from == 1 || from == 2 ){
			super.onBackPressed();
		}else{
			_returnData();
		}
	}
	//返回数据
	private void _returnData() {
		Intent intent = new Intent();	
		
		_this.setResult( 1001, intent );
		
		_this.finish();
	}

}
