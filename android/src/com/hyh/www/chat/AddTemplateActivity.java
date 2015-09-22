package com.hyh.www.chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
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
import com.gezitech.contract.GezitechManager_I.OnAsynInsertListener;
import com.gezitech.service.managers.ShoutManager;
import com.gezitech.util.IOUtil;
import com.gezitech.util.ImageUtil;
import com.gezitech.util.StringUtil;
import com.gezitech.widget.OptionDialog;
import com.gezitech.widget.RemoteImageView;
import com.gezitech.widget.OptionDialog.DialogSelectDataCallBack;
import com.gezitech.widget.OptionDialog.ItemType;
import com.hyh.www.R;
import com.hyh.www.entity.ChatContent;
import com.hyh.www.entity.Validtimelist;
import com.loopj.android.http.RequestParams;

/***
 * 
 * @author xiaobai 2014-11-15
 * @todo( 添加模版 )
 */
public class AddTemplateActivity extends GezitechActivity {
	private AddTemplateActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private String title;
	private EditText ed_edit_beizhu;
	private EditText ed_editbill_jine;
	private RelativeLayout editbill_pay;
	private ImageButton iv_camera;
	private RelativeLayout rl_releaseHyh_photo;
	private RemoteImageView iv_releaseHyh_photo;
	private ImageView play_del_xx;
	private File litpic = null;
	private TextView tv_editbill_choose;
	private Button bt_editbill_send;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_template);
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
		tv_title.setText("添加模版");

		ed_edit_beizhu = (EditText) findViewById(R.id.ed_edit_beizhu);

		ed_editbill_jine = (EditText) findViewById(R.id.ed_editbill_jine);

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
				startActivityForResult(localIntent2, 1001);
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

		bt_editbill_send = (Button) findViewById( R.id.bt_editbill_send );
		bt_editbill_send.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				_submitData();
			}
		});
	}
	/**
	 * 
	 * TODO( 提交 )
	 */
	private void _submitData(){
		String ed_edit_beizhuVal = ed_edit_beizhu.getText().toString().trim();
		if( ed_edit_beizhuVal.equals("") ) {
			Toast("备注不能为空");
			return;
		}
		
		double ed_editbill_jineVal = Double.parseDouble( ed_editbill_jine.getText().toString().trim() );
		
		if( ed_editbill_jineVal <= 0 ){
			Toast("支付金额格式错误");
			return;
		}
		
		if ( activetime <= 0) {
			Toast("有效时间未选择");
			return;
		}
		RequestParams params = new RequestParams();
		if( litpic != null ){
			try {
				params.put("litpic", litpic);
				params.put("w", 300);
				params.put("h", 300);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		params.put("notes", ed_edit_beizhuVal);
		params.put("money", ed_editbill_jineVal);
		params.put("activetime", activetime);
		GezitechAlertDialog.loadDialog( this );
		ShoutManager.getInstance().addTrade(params, new OnAsynInsertListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				Toast( errorMsg );
				GezitechAlertDialog.closeDialog();
			}
			
			@Override
			public void onInsertDone(String id) {
				GezitechAlertDialog.closeDialog();
				Intent intent = new  Intent ();
				_this.setResult( 1001 , intent);
				returnData();
			}
		});
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null)
			return;
		/* String result = data.getAction(); */
		Bitmap originBitMap = null, reduceBitMap = null;
		File originFile = null;
		String filePath = null;
		switch (requestCode) {
		case 1001:// 单选
			ContentResolver resolver3 = getContentResolver();
			Uri selectedImageUri3 = data.getData();

			if (selectedImageUri3 != null) {
				FileInputStream fis = null;
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
				}
				iv_releaseHyh_photo.setImageBitmap(originBitMap);
				litpic = originFile;
				rl_releaseHyh_photo.setVisibility(View.VISIBLE);

			}
			break;
		default:
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
					//计算选中时间秒数
					for(int i = 0; i<list.size(); i++){
						Validtimelist item = (Validtimelist) list.get(i);
						if( item.id == activetime ){
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
					tv_editbill_choose.setText(StringUtil
							.stringArrayJoin(strArray, ","));
				}
			}
		});
	}
}
