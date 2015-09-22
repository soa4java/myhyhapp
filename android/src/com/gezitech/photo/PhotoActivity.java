package com.gezitech.photo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.gezitech.adapter.PhotoSelectAdaper;
import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.entity.PhotoAlbumModel;
import com.gezitech.entity.PhotoItemModel;
import com.hyh.www.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 
 * @author xiaobai 2014-9-25
 * @todo( 相册中的图片选择 )
 */
public class PhotoActivity extends GezitechActivity implements OnClickListener {
	private PhotoActivity _this = this;
	private GridView grid_view;
	private TextView tv_count;
	private Button bt_complete;
	private int chooseNum = 0;
	private PhotoAlbumModel album = null;
	private PhotoSelectAdaper photoSelectAdaper;
	private Button bt_my_post;
	private Button bt_home_msg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		Intent intent = this.getIntent();
		album = (PhotoAlbumModel) intent.getExtras().get("album");
		chooseNum = intent.hasExtra("chooseNum") ? intent.getIntExtra("chooseNum", 0) : 0;
		//判断是否有选中的
		for (int j = 0; j < album.bitList.size(); j++) {
			PhotoItemModel jo = (PhotoItemModel) album.bitList.get(j);
			if (  GezitechApplication.selectPic.containsValue( jo.path ) ) {
				jo.select = true;
				album.bitList.set(j, jo);
			}

		}
//		遍历hashmap
//		Iterator<Entry<String, String>> iter = GezitechApplication.selectPic.entrySet().iterator();
//		while (iter.hasNext()) {
//		Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
//		String key = entry.getKey();
//		String val = entry.getValue();
//		}

		_init();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	// 数据的初始化
	private void _init() {

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("选择图片");

		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.VISIBLE);
		bt_my_post.setText("取消");
		bt_my_post.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				_this.finish();
			}
		});

		tv_count = (TextView) this.findViewById(R.id.tv_count);
		tv_count.setText("可以添加"
				+ (GezitechApplication.selectPhontCount - chooseNum) + "张截图");
		bt_complete = (Button) this.findViewById(R.id.bt_complete);
		bt_complete.setOnClickListener(this);

		grid_view = (GridView) this.findViewById(R.id.grid_view);

		photoSelectAdaper = new PhotoSelectAdaper(this);

		grid_view.setAdapter(photoSelectAdaper);

		photoSelectAdaper.removeAll();
		photoSelectAdaper.addList(album.bitList, false);

		// 选中
		grid_view.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				PhotoItemModel item = (PhotoItemModel) photoSelectAdaper.getItem(arg2);
				if (item != null) {
					if (item.select) {
						item.select = false;
						
						if( GezitechApplication.selectPic.containsKey( item.photoID+"" ) ){
							GezitechApplication.selectPic.remove( item.photoID+"" );
						}
						chooseNum--;
					} else if (!item.select && chooseNum < GezitechApplication.selectPhontCount) {
						item.select = true;
						chooseNum++;
						GezitechApplication.selectPic.put( item.photoID+"" , item.path );
					}

					photoSelectAdaper.setItem(item, arg2);
					tv_count.setText("可以添加"
							+ (GezitechApplication.selectPhontCount - chooseNum)
							+ "张截图");

				}
			}
		});

	}

	// click事件集合
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_home_msg: // 返回
			returnData(0);
			break;
		case R.id.bt_complete: // 完成

			returnData( 1 );

			break;
		}
	}

	@Override
	public void onBackPressed() {
		returnData(0);
	}

	/**
	 * 
	 * TODO( 回调函数 )
	 */
	private void returnData( int iscomplete ) {

		Intent intent = new Intent();

		intent.putExtra("chooseNum",  chooseNum );
		intent.putExtra("iscomplete",  iscomplete );
		
		_this.setResult(Activity.RESULT_OK, intent);

		_this.finish();

	}
}