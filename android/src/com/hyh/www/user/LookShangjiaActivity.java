package com.hyh.www.user;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.entity.User;
import com.gezitech.util.StringUtil;
import com.gezitech.widget.RemoteImageView;
import com.hyh.www.R;
import com.hyh.www.entity.FieldVal;
import com.hyh.www.widget.ImageShow;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 
 * @author xiaobai
 * 2015-2-11
 * @todo( 查看企业认证资料  )
 */
public class LookShangjiaActivity extends GezitechActivity implements
		OnClickListener {

	private LookShangjiaActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_title;
	private User shangjia;
	private RemoteImageView iv_yinyezhizhao;
	private RemoteImageView iv_jingyingxukezheng;
	private RemoteImageView iv_lianxirenzhaopian;
	private LinearLayout ll_changdizhaopian_box;
	private String[] images = {};
	private TextView ed_company_zhanghao;
	private TextView ed_company_kaihuname;
	private TextView ed_company_kaihuhang;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_look_shangjia);
		shangjia = (User) getIntent().getExtras().getSerializable("shangjia");
		_init();
	}

	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("商家资料");

		((TextView) findViewById(R.id.tv_becomeshangjia_qiyeleixingchoose))
				.setText(FieldVal.value(shangjia.companyTypeName));
		((TextView) findViewById(R.id.ed_shop_name)).setText(FieldVal
				.value(shangjia.company_name));
		((TextView) findViewById(R.id.ed_linkman)).setText(FieldVal
				.value(shangjia.touchname));
		((TextView) findViewById(R.id.ed_company_phone)).setText(FieldVal
				.value(shangjia.company_tel));
		((TextView) findViewById(R.id.ed_time)).setText(FieldVal
				.value(shangjia.businesstime));
		((TextView) findViewById(R.id.tv_issend_val)).setText(FieldVal
				.getSend(shangjia.isdelivery));

		iv_yinyezhizhao = (RemoteImageView) findViewById(R.id.iv_yinyezhizhao);

		iv_jingyingxukezheng = (RemoteImageView) findViewById(R.id.iv_jingyingxukezheng);
		iv_lianxirenzhaopian = (RemoteImageView) findViewById(R.id.iv_lianxirenzhaopian);
		ll_changdizhaopian_box = (LinearLayout) findViewById(R.id.ll_changdizhaopian_box);
		
		//用户的开户行 等信息
		
		ed_company_zhanghao = (TextView) findViewById( R.id.ed_company_zhanghao );
		ed_company_kaihuname = (TextView) findViewById( R.id.ed_company_kaihuname );
		ed_company_kaihuhang = (TextView) findViewById( R.id.ed_company_kaihuhang );
		
		ed_company_zhanghao.setText( FieldVal.value(  shangjia.account_number ) );
		ed_company_kaihuname.setText( FieldVal.value(  shangjia.account_name) );
		ed_company_kaihuhang.setText( FieldVal.value(  shangjia.account_bankname ) );
		
		_initData();

	}

	// 初始化数据
	private void _initData() {
		
		iv_yinyezhizhao.setImageUrl(shangjia.company_license);
		if (!FieldVal.value(shangjia.company_license).equals("")) {
			iv_yinyezhizhao.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 画廊展示图片
					final String[] images = new String[1];
					String[] pic = shangjia.company_license.split("src=");
					images[0] = StringUtil.stringDecode(pic[1]);
					ImageShow.jumpDisplayPic(images, 0, _this);
				}
			});
		}
		iv_jingyingxukezheng.setImageUrl(shangjia.company_certificate);
		if (!FieldVal.value(shangjia.company_certificate).equals("")) {
			iv_jingyingxukezheng.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 画廊展示图片
					final String[] images = new String[1];
					String[] pic = shangjia.company_certificate.split("src=");
					images[0] = StringUtil.stringDecode(pic[1]);
					ImageShow.jumpDisplayPic(images, 0, _this);
				}
			});
		}
		iv_lianxirenzhaopian.setImageUrl(shangjia.company_userphoto);
		if (!FieldVal.value(shangjia.company_userphoto).equals("")) {
			iv_lianxirenzhaopian.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 画廊展示图片
					final String[] images = new String[1];
					String[] pic = shangjia.company_userphoto.split("src=");
					images[0] = StringUtil.stringDecode(pic[1]);
					ImageShow.jumpDisplayPic(images, 0, _this);
				}
			});
		}
		ll_changdizhaopian_box.removeAllViews();
		int index = -1;
		if ( !shangjia.company_placeshowone.equals("")) {
			index++;
			select_photo_item(shangjia.company_placeshowone, null, false, index);
		}
		if ( !shangjia.company_placeshowtwo.equals("") ) {
			index++;
			select_photo_item(shangjia.company_placeshowtwo, null, false, index);
		}
		if ( !shangjia.company_placeshowthree.equals("")  ) {
			index++;
			select_photo_item(shangjia.company_placeshowthree, null, false,
					index);
		}
	}
	// 图片的显示
	public void select_photo_item(final String imgUrl, Bitmap bitmap,
			boolean isDel, final int index) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.select_photo_item, null);
		RemoteImageView iv_changdizhaopian = (RemoteImageView) view
				.findViewById(R.id.iv_changdizhaopian);
		ImageView play_del_xx = (ImageView) view.findViewById(R.id.iv_del);

		play_del_xx.setVisibility(View.GONE);
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
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.bt_home_msg:// 返回
			finish();
			break;
		default:
			break;
		}
	}
}
