package com.gezitech.photo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gezitech.adapter.PhotoAlbumAdapter;
import com.gezitech.adapter.PhotoAlbumAdapter.OnAlbumListener;
import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.entity.PhotoAlbumModel;
import com.gezitech.entity.PhotoItemModel;
import com.gezitech.widget.MyListView;
import com.hyh.www.R;
/**
 * 
 * @author xiaobai
 * 2014-9-24
 * @todo( 相册列表  )
 */
public class PhotoAlbumActivity extends GezitechActivity implements OnClickListener{
	private PhotoAlbumActivity _this = this;
	private ImageButton bt_reveal;
	private ImageButton ib_back;
	private TextView tv_head_center_title;
	private MyListView list_view;
	private Button bt_my_post;
	private Button bt_home_msg;
	private int chooseNum; //已经上传过图片
	// 设置获取图片的字段信息
	private static final String[] STORE_IMAGES = {
			MediaStore.Images.Media.DISPLAY_NAME, // 显示的名称
			MediaStore.Images.Media.DATA,
			MediaStore.Images.Media.LONGITUDE, // 经度
			MediaStore.Images.Media._ID, // id
			MediaStore.Images.Media.BUCKET_ID, // dir id 目录
			MediaStore.Images.Media.BUCKET_DISPLAY_NAME // dir name 目录名字
		   
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = this.getIntent();
		//之前已经选择的条数
		chooseNum = intent.hasExtra("chooseNum") ? intent.getIntExtra("chooseNum", 0) : 0;
		//清除之前选择的数据
		GezitechApplication.selectPic.clear();
		setContentView( R.layout.activity_album );
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
	//数据的初始化
	private void _init(){
		
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);
		
		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);
		
		TextView tv_title = (TextView) findViewById( R.id.tv_title );
		tv_title.setText( "选择图片" );
		
		list_view = (MyListView) this.findViewById( R.id.list_view );
		ArrayList<GezitechEntity_I> aibumList = getPhotoAlbum();
		
		DisplayMetrics dm = _this.getResources().getDisplayMetrics();
		
		int h = dm.heightPixels;
		int ih = (int) ( 60 * dm.density );
		
		int lineCount = ( int ) h / ih;
		
		PhotoAlbumAdapter photoAlbumAdapter = new PhotoAlbumAdapter( this, lineCount );
		
		list_view.setAdapter( photoAlbumAdapter );
		list_view.footerShowState( 2 );
		photoAlbumAdapter.removeAll();
		
		photoAlbumAdapter.addList(aibumList, false);
		
		photoAlbumAdapter.setOnAlbumListener( new OnAlbumListener() {
			
			@Override
			public void albumListener(PhotoAlbumModel item) {
				if( item != null ){
					Intent intent = new Intent( _this, PhotoActivity.class );
					Bundle  bundle = new Bundle();
					bundle.putSerializable("album", item );
					intent.putExtras( bundle );
					intent.putExtra("chooseNum",  chooseNum );
					_this.startActivityForResult( intent , 10004 );
				}
			}
		});
		
	}
	/**
	 * 方法描述：按相册获取图片信息
	 * 
	 * @author: why
	 * @time: 2013-10-18 下午1:35:24
	 */
	private ArrayList<GezitechEntity_I> getPhotoAlbum() {
		ArrayList<GezitechEntity_I> aibumList = new ArrayList<GezitechEntity_I>();
		Cursor cursor = MediaStore.Images.Media.query(getContentResolver(),
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES);
		Map<String, PhotoAlbumModel> countMap = new HashMap<String, PhotoAlbumModel>();
		PhotoAlbumModel pam = null;
		while (cursor.moveToNext()) {
			String path=cursor.getString(1);
			String id = cursor.getString(3);
			String dir_id = cursor.getString(4);
			String dir = cursor.getString(5);
			if (!countMap.containsKey(dir_id)) {
				pam = new PhotoAlbumModel();
				pam.name = dir;
				pam.bitmap = Integer.parseInt(id);
				pam.count = "1";
				pam.path = path;
				
				PhotoItemModel pim= new PhotoItemModel();
				pim.photoID = Integer.valueOf(id);
				pim.path = path;
				pam.bitList.add( pim );
				countMap.put( dir_id, pam );
			} else {
				pam = countMap.get(dir_id);
				pam.count = String.valueOf( Integer.parseInt(pam.count) + 1 );
				
				PhotoItemModel pim= new PhotoItemModel();
				pim.photoID = Integer.valueOf(id);
				pim.path = path;
				pam.bitList.add( pim );
			}
		}
		cursor.close();
		Iterable<String> it = countMap.keySet();
		for (String key : it) {
			aibumList.add(countMap.get(key));
		}
		return aibumList;
	}
	//click事件集合
	@Override
	public void onClick(View v) {
		switch( v.getId() ){
		case R.id.bt_home_msg	: // 返回
			_this.finish();
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch ( requestCode ) {
		case 10004: //选择图片返回
			
			if(data != null && resultCode == Activity.RESULT_OK ){
				
				chooseNum = data.getIntExtra( "chooseNum", 0 );
				int iscomplete = data.getIntExtra( "iscomplete", 0 );
				if( iscomplete  == 1 ){//完成准备上传
					Intent intent = new Intent();
					_this.setResult(Activity.RESULT_OK, intent);

					_this.finish();
					
				}
				
			}
			
			break;
		default:
			break;
		}
		
	}
}
