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
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynInsertListener;
import com.gezitech.service.managers.ChatManager;
import com.gezitech.service.managers.ShoutManager;
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
import com.loopj.android.http.RequestParams;

/**
 * 
 * @author xiaobai 2014-11-15
 * @todo( 创建付款 )
 */
public class CreatePayActivity extends GezitechActivity {
	private CreatePayActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private String title;
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
	private EditText ed_edit_beizhu;
	private TextView tv_buyerscontact;
	private EditText ed_editbill_jine;
	private RelativeLayout editbill_pay;
	private TextView tv_editbill_choose;
	private ImageButton iv_camera;
	private File litpic = null;
	private String litpicUrl = "";
	private long fid;
	private TextView tv_editbill_pay;
	private boolean isSubmitSuccess = false;
	private long sid = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editbill);
		Intent intent = _this.getIntent();
		// Bundle bundle = intent.getExtras();
		title = intent.getStringExtra("title");
		fid = intent.getLongExtra("fid", 0);
		sid    = intent.getLongExtra("sid", 0); //喊一喊id
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
		tv_title.setText(title);

		tv_number = (TextView) findViewById(R.id.tv_number);
		// 随机账单的序号
		int y = CalendarUtil.getCalendarString(Calendar.YEAR);
		int m = CalendarUtil.getCalendarString(Calendar.MONTH);
		int d = CalendarUtil.getCalendarString(Calendar.DAY_OF_MONTH);
		int mm = CalendarUtil.getCalendarString(Calendar.SECOND);
		int random = (int) (Math.random() * Math.random() * 10000)+(int)user.id+mm;
		String number = String.format("%04d%02d%02d%04d", y, m, d, random);
		tv_number.setText(number);

		tv_merchantsname = (TextView) findViewById(R.id.tv_merchantsname);
		tv_merchantsaddress = (TextView) findViewById(R.id.tv_merchantsaddress);
		tv_merchantscontact = (TextView) findViewById(R.id.tv_merchantscontact);

		ChatManager.getInstance().getFriendData(fid,
				new OnAsynGetOneListener() {

					@Override
					public void OnAsynRequestFail(String errorCode,
							String errorMsg) {
					}

					@Override
					public void OnGetOneDone(GezitechEntity_I entity) {
						Friend firend = (Friend) entity;
						tv_merchantsname.setText(FieldVal.value( firend.company_name ) );
						tv_merchantsaddress.setText( FieldVal.value(firend.address) );
						tv_merchantscontact.setText( FieldVal.value(firend.company_tel) );
					}
				});

		// 买家
		tv_buyersname = (TextView) findViewById(R.id.tv_buyersname);
		tv_buyersaddress = (TextView) findViewById(R.id.tv_buyersaddress);
		tv_buyerscontact = (TextView) findViewById(R.id.tv_buyerscontact);
		tv_buyersname
				.setText(FieldVal.value(user.nickname).equals("") ? user.username
						: user.nickname);
		tv_buyersaddress.setText(FieldVal.value(user.address));
		tv_buyerscontact.setText(user.phone);

		ed_edit_beizhu = (EditText) findViewById(R.id.ed_edit_beizhu);
		ed_editbill_jine = (EditText) findViewById(R.id.ed_editbill_jine);
		tv_editbill_pay = (TextView) findViewById(R.id.tv_editbill_pay);
		tv_editbill_pay.setText("限时收款");
		editbill_pay = (RelativeLayout) findViewById(R.id.editbill_pay);
		tv_editbill_choose = (TextView) findViewById(R.id.tv_editbill_choose);

		editbill_pay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GezitechAlertDialog.loadDialog(_this);
				ShoutManager.getInstance().validtimelist(1,
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

		iv_camera = (ImageButton) findViewById(R.id.iv_camera);
		rl_releaseHyh_photo = (RelativeLayout) findViewById(R.id.rl_releaseHyh_photo);
		iv_releaseHyh_photo = (RemoteImageView) findViewById(R.id.iv_releaseHyh_photo);
		play_del_xx = (ImageView) findViewById(R.id.iv_del);

		iv_camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent localIntent = new Intent();
				localIntent.setType("image/*");
				localIntent.setAction("android.intent.action.GET_CONTENT");
				Intent localIntent2 = Intent.createChooser(localIntent, "上传图片");
				startActivityForResult(localIntent2, 1002);
			}
		});
		// 删除
		play_del_xx.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rl_releaseHyh_photo.setVisibility(View.GONE);
				litpic = null;
				litpicUrl = "";
			}
		});

		bt_editbill_send = (Button) findViewById(R.id.bt_editbill_send);
		bt_editbill_send.setText("确认付款");
		bt_editbill_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				_submitData();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null)
			return;
		Bitmap originBitMap = null, reduceBitMap = null;
		File originFile = null;
		String filePath = null;
		switch (requestCode) {
		case 1001:// 支付的返回

			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bill.paytime = System.currentTimeMillis()/1000;
			bundle.putSerializable("bill", bill );
			intent.putExtras(bundle);
			_this.setResult(1008, intent);
			returnData();
			break;
		case 1002:// 选择图片
			ContentResolver resolver3 = getContentResolver();
			Uri selectedImageUri3 = data.getData();

			if (selectedImageUri3 != null) {
				FileInputStream fis = null;
				try {
					fis = (FileInputStream) resolver3
							.openInputStream(selectedImageUri3);
					originBitMap = BitmapFactory.decodeStream(fis);
					originFile = IOUtil.makeLocalImage(originBitMap, null);
					filePath = originFile.getPath();
				} catch (Exception e) {
					e.printStackTrace();
				}
				int scale = ImageUtil.reckonThumbnail(originBitMap.getWidth(),
						originBitMap.getHeight(), 600, 600);
				reduceBitMap = ImageUtil.PicZoom(originBitMap,
						(int) (originBitMap.getWidth() / scale),
						(int) (originBitMap.getHeight() / scale));

				iv_releaseHyh_photo.setImageBitmap(originBitMap);
				litpic = new File(filePath);
				rl_releaseHyh_photo.setVisibility(View.VISIBLE);

			}
			break;
		}

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

	// 弹出发布有效时间选择框
	private HashMap<String, String> validtimelist = new HashMap<String, String>();
	protected int activetime;
	protected long activetimeVal;
	protected Bill bill = null;

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
					tv_editbill_choose.setText("请选择");
				} else {
					Collection<String> b = selectedList.values();
					String[] strArray = (String[]) b.toArray(new String[] {});
					tv_editbill_choose.setText(StringUtil.stringArrayJoin(
							strArray, ","));
				}
			}
		});
	}

	private void _submitData() {

		if (isSubmitSuccess) {

			jumpPay();

		} else {

			String tv_numberVal = tv_number.getText().toString().trim();
			/*
			 * if( tv_numberVal.equals("") ){ Toast("账单编号未生成"); return; }
			 */
			String tv_merchantsnameVal = tv_merchantsname.getText().toString()
					.trim();
			/*
			 * if( tv_merchantsnameVal.equals("") ){ Toast("商家名称不能为空"); return;
			 * }
			 */

			String tv_merchantsaddressVal = tv_merchantsaddress.getText()
					.toString().trim();
			/*
			 * if( tv_merchantsaddressVal.equals("") ){ Toast("商家地址不能为空");
			 * return; }
			 */

			String tv_merchantscontactVal = tv_merchantscontact.getText()
					.toString().trim();
			/*
			 * if( tv_merchantsaddressVal.equals("") ){ Toast("商家联系方式不能为空");
			 * return; }
			 */

			String tv_buyersnameVal = tv_buyersname.getText().toString().trim();
			String tv_buyersaddressVal = tv_buyersaddress.getText().toString()
					.trim();
			String tv_buyerscontactVal = tv_buyerscontact.getText().toString()
					.trim();

			String ed_edit_beizhuVal = ed_edit_beizhu.getText().toString()
					.trim();
			if (ed_edit_beizhuVal.equals("")) {
				Toast("备注不能为空");
				return;
			}
			double ed_editbill_jineVal = 0;
			try{				
				ed_editbill_jineVal = Double.parseDouble(ed_editbill_jine.getText().toString().trim());
			}catch(Exception ex){
				ed_editbill_jineVal = 0;
			}
			if (ed_editbill_jineVal <= 0) {
				
				Toast("支付金额至少大于0.01");
				return;
				
			}
			

			if (activetimeVal <= 0) {
				Toast("有效时间未选择");
				return;
			}
			RequestParams params = new RequestParams();
			if (litpic != null) {
				try {
					params.put("litpic", litpic);
					params.put("w", 300);
					params.put("h", 300);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (!litpicUrl.equals("")) {
				params.put("litpic", litpicUrl);
				params.put("w", 300);
				params.put("h", 300);
			}

			params.put("tradecode", tv_numberVal);
			params.put("bid", fid);
			params.put("notes", ed_edit_beizhuVal);
			params.put("money", ed_editbill_jineVal);
			params.put("activetime", activetimeVal);
			params.put("sid",  sid);
			GezitechAlertDialog.loadDialog(this);
			ShoutManager.getInstance().addpai(params,
					new OnAsynGetOneListener() {

						@Override
						public void OnAsynRequestFail(String errorCode,
								String errorMsg) {
							// TODO Auto-generated method stub
							Toast(errorMsg);
							GezitechAlertDialog.closeDialog();
						}

						@Override
						public void OnGetOneDone(GezitechEntity_I entity) {
							// TODO Auto-generated method stub
							GezitechAlertDialog.closeDialog();
							bill = (Bill) entity;
							isSubmitSuccess = true;
							jumpPay();
						}
					});
		}
	}

	private void jumpPay() {
		if (bill == null)
			return;
		// 跳转到支付界面
		Intent intent = new Intent(_this, PayActivity.class);

		intent.putExtra("id", bill.id);
		intent.putExtra("tradecode", bill.tradecode+"");
		double ed_editbill_jineVal = Double.parseDouble(ed_editbill_jine
				.getText().toString().trim());
		intent.putExtra("money", ed_editbill_jineVal);
		
		_this.startActivityForResult(intent, 1001);

		_this.overridePendingTransition(R.anim.out_to_down, R.anim.exit_anim);

	}
}
