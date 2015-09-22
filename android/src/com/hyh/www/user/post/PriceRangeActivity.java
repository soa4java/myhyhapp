package com.hyh.www.user.post;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynInsertListener;
import com.gezitech.service.managers.NearManager;
import com.gezitech.service.managers.ShoutManager;
import com.gezitech.service.xmpp.MessageSend;
import com.gezitech.util.StringUtil;
import com.gezitech.util.ToastMakeText;
import com.gezitech.widget.MyListView;
import com.gezitech.widget.OptionDialog;
import com.gezitech.widget.OptionDialog.DialogSelectDataCallBack;
import com.gezitech.widget.OptionDialog.ItemType;
import com.hyh.www.R;
import com.hyh.www.adapter.BasicAdapter.OnClickDataPress;
import com.hyh.www.adapter.PriceRangeAdapter;
import com.hyh.www.chat.PayActivity;
import com.hyh.www.entity.PriceRange;
import com.hyh.www.entity.PubRange;
import com.hyh.www.entity.RangeBuyRecord;
import com.hyh.www.entity.Releasescope;
import com.hyh.www.widget.YMDialog;
import com.loopj.android.http.RequestParams;

/**
 * 
 * @author xiaobai 2015-6-2
 * @todo( 范围的价格列表 )
 */
public class PriceRangeActivity extends GezitechActivity implements
		OnClickListener {
	private PriceRangeActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private long typeid;
	private String rangetitle;
	private String description;
	private ListView listView;
	private PriceRangeAdapter priceRangeAdapter;
	private PriceRange selectItem = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_price_range);
		Intent intent = _this.getIntent();
		typeid = intent.getLongExtra("typeid", 0);
		rangetitle = intent.getStringExtra("rangetitle");
		description = intent.getStringExtra("description");

		__init();
	}

	private void __init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("选择费用");

		((TextView) findViewById(R.id.tv_type_price_range_title))
				.setText(rangetitle + "：" + description);
		listView = (ListView) findViewById(R.id.list_view);

		priceRangeAdapter = new PriceRangeAdapter(_this);

		listView.setAdapter(priceRangeAdapter);
		GezitechAlertDialog.loadDialog(_this);
		NearManager.getInstance().getRangepriceList(typeid,
				new OnAsynGetListListener() {

					@Override
					public void OnAsynRequestFail(String errorCode,
							String errorMsg) {
						Toast(errorMsg);
						GezitechAlertDialog.closeDialog();
					}

					@Override
					public void OnGetListDone(ArrayList<GezitechEntity_I> list) {
						GezitechAlertDialog.closeDialog();
						priceRangeAdapter.removeAll();
						priceRangeAdapter.addList(list, false);
					}
				});
		priceRangeAdapter.setOnClickDataPress(new OnClickDataPress() {

			@Override
			public void onDataPerss(GezitechEntity_I item, int position) {
				selectItem = (PriceRange) item;
				priceRangeAdapter.notifyDataSetChanged();
			}
		});
		findViewById(R.id.btn_editdata).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (selectItem != null) {

							final YMDialog ymd = new YMDialog(_this);
							ymd.setHintMsg("确定购买？");
							ymd.setConfigButton(new OnClickListener() {
								@Override
								public void onClick(View v) {
									ymd.dismiss();

									GezitechAlertDialog.loadDialog(_this);
									NearManager.getInstance().addRangeBuyRecord(selectItem.id,
													new OnAsynGetOneListener() {

														@Override
														public void OnAsynRequestFail(
																String errorCode,
																String errorMsg) {
															GezitechAlertDialog
																	.closeDialog();
															Toast(errorMsg);
														}

														@Override
														public void OnGetOneDone(
																GezitechEntity_I entity) {
															GezitechAlertDialog
																	.closeDialog();
															RangeBuyRecord rbr = (RangeBuyRecord) entity;
															// 跳转到支付界面
															Intent intent = new Intent(
																	_this,
																	PayActivity.class);

															intent.putExtra(
																	"id",
																	rbr.id);
															intent.putExtra(
																	"tradecode",
																	rbr.tradecode);
															intent.putExtra(
																	"money",
																	rbr.price);
															intent.putExtra(
																	"paytype",
																	"nearbyconsume");
															_this.startActivityForResult(
																	intent,
																	1001);

															_this.overridePendingTransition(
																	R.anim.out_to_down,
																	R.anim.exit_anim);
														}
													});
								}
							}).setCloseButton(new OnClickListener() {

								@Override
								public void onClick(View v) {
									ymd.dismiss();
								}
							});
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
