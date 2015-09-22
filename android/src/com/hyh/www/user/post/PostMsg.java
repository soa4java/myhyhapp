package com.hyh.www.user.post;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynInsertListener;
import com.gezitech.contract.GezitechManager_I.OnAsynProgressListener;
import com.gezitech.photo.PhotoAlbumActivity;
import com.gezitech.service.GezitechService;
import com.gezitech.service.GezitechService.CallBDLocation;
import com.gezitech.service.managers.NearManager;
import com.gezitech.service.managers.SystemManager;
import com.gezitech.util.DateUtils;
import com.gezitech.util.IOUtil;
import com.gezitech.util.ImageUtil;
import com.gezitech.util.StringUtil;
import com.gezitech.util.ToastMakeText;
import com.gezitech.widget.FlowLayout;
import com.gezitech.widget.LoadingCircleView;
import com.gezitech.widget.OptionDialog;
import com.gezitech.widget.SelectPicPopupWindow;
import com.gezitech.widget.OptionDialog.DialogSelectDataCallBack;
import com.gezitech.widget.OptionDialog.ItemType;
import com.hyh.www.R;
import com.hyh.www.entity.Bill;
import com.hyh.www.entity.NearMsg;
import com.hyh.www.entity.PubRange;
import com.hyh.www.entity.Releasescope;
import com.loopj.android.http.RequestParams;

/**
 * 
 * @author xiaobai 2015-6-2
 * @todo( 发布信息 )
 */
public class PostMsg extends GezitechActivity implements OnClickListener {
	private PostMsg _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private EditText ed_post_content;
	private DisplayMetrics dm;
	private TextView tv_post_far;
	private PubRange pubRange;
	private long pubRangeId;
	private long pubRangeVal;
	private TextView tv_post_distance;
	private ImageButton ib_add_select;
	private int screenWidth;
	private FlowLayout ll_photo_select;
	private int itemWith;
	private Button btn_post;
	private String et_contentVal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_msg);
		pubRange = (PubRange) _this.getIntent().getExtras()
				.getSerializable("pubRange");
		pubRangeId = pubRange.id;
		pubRangeVal = pubRange.range;
		dm = _this.getResources().getDisplayMetrics();
		__init();
	}

	private void __init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("发布");

		btn_post = (Button) _this.findViewById(R.id.btn_post);

		tv_post_distance = (TextView) findViewById(R.id.tv_post_distance);
		tv_post_distance.setText(pubRange.rangetitle);
		ed_post_content = (EditText) findViewById(R.id.ed_post_content);
		ed_post_content.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				int LineCount = ed_post_content.getLineCount();
				if (LineCount <= 1) {
					ViewGroup.LayoutParams lp = ed_post_content
							.getLayoutParams();
					lp.height = (int) (48 * dm.density);
					ed_post_content.setLayoutParams(lp);
				} else {
					ViewGroup.LayoutParams lp = ed_post_content
							.getLayoutParams();
					lp.height = (int) ((48 + ((LineCount - 1) * 15)) * dm.density);
					ed_post_content.setLayoutParams(lp);
				}
			}
		});

		findViewById(R.id.rl_post_distance).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						GezitechAlertDialog.loadDialog(_this);
						NearManager.getInstance().getPubRangeList(
								new OnAsynGetListListener() {

									@Override
									public void OnAsynRequestFail(
											String errorCode, String errorMsg) {
										GezitechAlertDialog.closeDialog();
										new ToastMakeText(_this)
												.Toast(errorMsg);
									}

									@Override
									public void OnGetListDone(
											ArrayList<GezitechEntity_I> list) {
										GezitechAlertDialog.closeDialog();
										releasescopeDialog(list);

									}
								});
					}
				});
		// 发布更远
		tv_post_far = (TextView) findViewById(R.id.tv_post_far);
		tv_post_far.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				GezitechAlertDialog.loadDialog(_this);
				NearManager.getInstance().getRangetypeList(
						new OnAsynGetListListener() {

							@Override
							public void OnAsynRequestFail(String errorCode,
									String errorMsg) {
								GezitechAlertDialog.closeDialog();
								new ToastMakeText(_this).Toast(errorMsg);
							}

							@Override
							public void OnGetListDone(
									ArrayList<GezitechEntity_I> list) {
								GezitechAlertDialog.closeDialog();
								wantPostRange(list);
							}
						});

			}
		});

		_postContent();

	}

	/**
	 * 
	 * TODO(发送信息的提交)
	 */
	private void _postContent() {
		ll_photo_select = (FlowLayout) findViewById(R.id.ll_photo_select);
		screenWidth = dm.widthPixels;
		// /每个item的宽高
		itemWith = (int) ((screenWidth - dm.density * 36 - 30 * dm.density) / 3);
		// 按钮
		View view = LayoutInflater.from(_this).inflate(
				R.layout.list_image_button, null);
		ib_add_select = (ImageButton) view.findViewById(R.id.ib_add_select);
		LayoutParams paramss = ib_add_select.getLayoutParams();
		paramss.width = itemWith;
		paramss.height = itemWith;
		ib_add_select.setLayoutParams(paramss);
		ib_add_select.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 添加图片
				_this.startActivityForResult(new Intent(_this,
						SelectPicPopupWindow.class), 10002);
				_this.overridePendingTransition(R.anim.out_to_down,
						R.anim.exit_anim);

			}
		});
		ll_photo_select.addView(view);

		// 提交信息
		btn_post.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				__postMessage();
			}
		});

	}

	public void __postMessage() {
		et_contentVal = ed_post_content.getText().toString().trim();
		if (et_contentVal.length() > 0 && et_contentVal.length() <= 400) { // 当字符串小于140
			GezitechAlertDialog.loadDialog(_this);
			if (uploadFile.size() > 0) {

				ii = 0;
				imageTag = "";
				step = "";
				_update();

			} else {
				if (et_contentVal.length() > 0) { // 当字符串大于0

					_postMessage();

				} else {
					Toast("请输入发布的内容！");
					return;
				}
			}
		} else {
			Toast("请输入发布的内容！");
			return;
		}
	}

	int ii = 0;
	//private Map<String, String> isUpload = new HashMap<String, String>();
	protected String imageTag = "", step = "";

	private void _update() {
		// 大于本身长度
		if (ii >= uploadFile.size()) {

			_postMessage();

		} else {
//
//			if (isUpload.containsKey(StringUtil.MD5(uploadFile.get(ii).getPath()))) { // 已经上传
//				ii++;
//				_update();
//			} else {
				_updatePhotoSelect(ii, true);
//			}

		}
	}

	/**
	 * 
	 * TODO( 上传实体 )
	 */
	public void _updatePhotoSelect(final int i, final boolean isLoop) {
		RequestParams params = new RequestParams();
		try {
			params.put("litpic", uploadFile.get(i));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		params.put("custompath", "nearby");
		SystemManager.getInstance().fileclearProcess(params,
				new OnAsynProgressListener() {

					@Override
					public void OnAsynRequestFail(String errorCode,
							String errorMsg) {
						// TODO Auto-generated method stub
						Toast(errorMsg);
						ii++;
						_update();
					}

					@Override
					public void onUpdateDone(String id) {
						// Log.v("返回", id+"=====");
						imageTag += step + id;
						step = "|||";
//						// 设置已经上传过
//						isUpload.put(
//								StringUtil.MD5(uploadFile.get(i).getPath()),
//								"1");
						if (isLoop) {// 如果是(true)循环上传  false点击单上传

								ii++;
								_update();
								
						}
					}

					@Override
					public void OnProgress(int bytesWritten, int totalSize) {
//						View v = ll_photo_select.getChildAt(i);
//						LoadingCircleView loading_cirle_view = (LoadingCircleView) v
//								.findViewById(R.id.loading_cirle_view);
//						loading_cirle_view.setProgress(bytesWritten * 100
//								/ totalSize, 100);
					}
				});

	}

	// 提交数据
	private void _postMessage() {
		// 获取经度纬度
		String longs = GezitechApplication.systemSp.getString("long", "");
		String lat = GezitechApplication.systemSp.getString("lat", "");
		String city = GezitechApplication.systemSp.getString("city", "");
		if (longs.equals("") || lat.equals("")) {

			GezitechService.getInstance().longitude(new CallBDLocation() {

				@Override
				public void callfunction(String longs, String lat, String city) {
					_submitData(longs, lat, city);
				}
			});

		} else {
			_submitData(longs, lat, city);

		}

	}

	private void _submitData(String longs, String lat, String address) {

		RequestParams params = new RequestParams();
		params.put("attachment", imageTag);
		params.put("content", et_contentVal);
		params.put("long", longs);
		params.put("lat", lat);
		params.put("address", address);
		params.put("range", pubRangeVal);
		NearManager.getInstance().addNearbycontent(params,
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
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putSerializable("nearMsg", (NearMsg) entity);
						intent.putExtras(bundle);
						_this.setResult(Activity.RESULT_OK, intent);
						_this.finish();

					}
				});

	}

	private String ImageName = "";
	private Bitmap originBitMap = null;
	private Bitmap reduceBitMap = null;
	private File originFile = null;
	private String filePath = null;
	private ArrayList<File> uploadFile = new ArrayList<File>();//实际上传文件的地址
	private ArrayList<File> recordFile = new ArrayList<File>();//上传选择的记录包括 删除的，主要获取索引 防止重复

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		String action = "";
		if (requestCode != 1004) {
			if (data == null)
				return;
			action = data.getAction();
		}

		switch (requestCode) {
		case 1001:// 如果是删除聊天 则回调改变界面

			break;
		case 10002:// 选择上传图片
			if (action.equals("10001")) {// 拍照
				ImageName = "/" + DateUtils.getStringToday() + ".jpg";
				// 启动相机
				Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// 来存储图片
				// Uri.fromFile 把文件路径地址转为url地址
				// Uri.parse(uriString); 把url地址转为文件存储地址
				intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri
						.fromFile(new File(Environment
								.getExternalStorageDirectory(), ImageName)));
				startActivityForResult(intent1, 1004);

			} else if (action.equals("10002")) {// 相册
				// 单选
				// Intent localIntent = new Intent();
				// localIntent.setType("image/*");
				// localIntent.setAction("android.intent.action.GET_CONTENT");
				// Intent localIntent2 = Intent.createChooser(localIntent,
				// "选择相片");
				// startActivityForResult(localIntent2, 1003);
				// 多选
				Intent intent = new Intent(_this, PhotoAlbumActivity.class);
				intent.putExtra("chooseNum", uploadFile.size());
				this.startActivityForResult(intent, 10004);
			}
			break;
		case 10004:// 相册
			if (Activity.RESULT_OK != resultCode)
				return;

			// ContentResolver resolver3 = getContentResolver();
			// Uri selectedImageUri3 = data.getData();
			// if (selectedImageUri3 != null) {
			// FileInputStream fis = null;
			// try {
			// fis = (FileInputStream)
			// resolver3.openInputStream(selectedImageUri3);
			//
			// byte[] buffer = new byte[fis.available()];
			// fis.read(buffer);
			//
			// originBitMap = ImageUtil.PicZoom2(buffer,600,600 );
			//
			// originFile = IOUtil.makeLocalImage(originBitMap, null);
			//
			// } catch (Exception e) {
			// e.printStackTrace();
			// } finally {
			// try {
			// if (fis != null)
			// fis.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// }
			// uploadFile.add( originFile );
			// addView( originBitMap, originFile );
			// }

			// 多选
			final Iterator<Entry<String, String>> iter = GezitechApplication.selectPic
					.entrySet().iterator();
			while (iter.hasNext()) {

				Map.Entry<String, String> entry = (Map.Entry<String, String>) iter
						.next();
				String path = entry.getValue();

				FileInputStream fis = null;
				try {
					fis = new FileInputStream(new File(path));
					byte[] buffer = new byte[fis.available()];
					fis.read(buffer);

					originBitMap = ImageUtil.PicZoom2(buffer, 600, 600);

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

				uploadFile.add(originFile);
				recordFile.add(originFile);
				addView(originBitMap, originFile,recordFile.size()-1);

			}
			break;
		case 1004:// 相机
			try {
				File picture = new File(
						Environment.getExternalStorageDirectory() + ImageName);
				 FileInputStream fis = null;
				 try {
				 fis = new FileInputStream(picture);
				 byte[] buffer = new byte[fis.available()];
				 fis.read(buffer);
				 originBitMap = ImageUtil.PicZoom2(buffer, 600, 600);
				
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
				uploadFile.add(originFile);
				recordFile.add(originFile);
				addView(originBitMap, originFile, recordFile.size()-1 );

			} catch (Exception e) {
				e.printStackTrace();
			}

			break;
		}
	}

	// 显示的位图，当前插入list的索引值  index 当前索引
	private void addView( Bitmap originBitMap, File path, final int index ) {
		final View view = LayoutInflater.from(_this).inflate(
				R.layout.list_image_add, null);
		ImageView iv_del = (ImageView) view.findViewById(R.id.iv_del);
		ImageView iv_img = (ImageView) view.findViewById(R.id.iv_img);
//		LoadingCircleView loading_cirle_view = (LoadingCircleView) view
//				.findViewById(R.id.loading_cirle_view);
		iv_del.setVisibility(View.VISIBLE);
		final String pathStr = StringUtil.MD5( path.getPath()+index );
		iv_del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				for (int i = 0; i < uploadFile.size(); i++) {

					if (  StringUtil.MD5( uploadFile.get(i).getPath()+index ).equals( pathStr )) {
						Log.v("======", "=======测试======"+index  );
						uploadFile.remove(i);
						break;
					}
				}
				ll_photo_select.removeView(view);
				// 当删除任意一个都显示添加图片的按钮
				ll_photo_select.getChildAt(ll_photo_select.getChildCount() - 1)
						.setVisibility(View.VISIBLE);
			}
		});
		// 计算图片宽度
		LayoutParams paramss = iv_img.getLayoutParams();
		paramss.width = itemWith;
		paramss.height = itemWith;
		iv_img.setLayoutParams(paramss);
		iv_img.setImageBitmap(originBitMap);

		// 计算精度
//		LayoutParams paramsss = loading_cirle_view.getLayoutParams();
//		paramsss.width = itemWith - 30;
//		paramsss.height = itemWith - 30;
//		loading_cirle_view.setLayoutParams(paramsss);

		ll_photo_select.addView(view, ll_photo_select.getChildCount() - 1);

		// 当大于9张的时候隐藏添加图片的按钮
		if (uploadFile.size() >= 9) {

			ll_photo_select.getChildAt(ll_photo_select.getChildCount() - 1)
					.setVisibility(View.GONE);

		}

	}

	private HashMap<String, String> releasescopeList = new HashMap<String, String>();

	// 弹出发布范围选择框(发布半径)
	public void releasescopeDialog(final ArrayList<GezitechEntity_I> list) {

		OptionDialog optionDialog = new OptionDialog(_this,
				R.style.dialog_load1, list, "发布半径选择", releasescopeList, true,
				ItemType.PubRange);
		optionDialog.setOnOKButtonListener(new DialogSelectDataCallBack() {

			@Override
			public void onDataCallBack(HashMap<String, String> selectedList) {
				_this.releasescopeList = selectedList;
				Set<String> a = (Set<String>) selectedList.keySet();
				String[] keyArray = (String[]) a.toArray(new String[] {});
				if (keyArray.length >= 1) {
					pubRangeId = Integer.parseInt(keyArray[0]);
					for (int i = 0; i < list.size(); i++) {
						PubRange obj = (PubRange) list.get(i);
						if (obj.id == pubRangeId) {
							pubRangeVal = obj.range;
						}
					}

				}
				if (selectedList.size() == 0) {
					tv_post_distance.setText("请选择");
				} else {
					Collection<String> b = selectedList.values();
					String[] strArray = (String[]) b.toArray(new String[] {});
					tv_post_distance.setText(StringUtil.stringArrayJoin(
							strArray, ","));
				}

			}
		});
	}

	// 想发布更远
	public void wantPostRange(final ArrayList<GezitechEntity_I> list) {

		OptionDialog optionDialog = new OptionDialog(_this,
				R.style.dialog_load1, list, "想发送更远？",
				new HashMap<String, String>(), true, ItemType.PubRange, 1);
		optionDialog.setOnOKButtonListener(new DialogSelectDataCallBack() {

			@Override
			public void onDataCallBack(HashMap<String, String> selectedList) {
				Set<String> a = (Set<String>) selectedList.keySet();
				String[] keyArray = (String[]) a.toArray(new String[] {});

				Collection<String> b = selectedList.values();
				String[] strArray = (String[]) b.toArray(new String[] {});

				if (keyArray.length > 0) {
					long typeid = Long.parseLong(keyArray[0].equals("des") ? keyArray[1]
							: keyArray[0]);
					Intent intent = new Intent(_this, PriceRangeActivity.class);
					intent.putExtra("typeid", typeid);
					intent.putExtra("rangetitle", strArray[0]);
					intent.putExtra("description", strArray[1]);
					_this.startActivity(intent);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.bt_home_msg:
			finish();
			break;
		default:
			break;
		}

	}

}
