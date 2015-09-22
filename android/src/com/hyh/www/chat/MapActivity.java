package com.hyh.www.chat;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.gezitech.basic.GezitechActivity;
import com.hyh.www.R;
import com.hyh.www.entity.ChatContent;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * @author xiaobai (map)
 */
public class MapActivity extends GezitechActivity implements OnClickListener {
	private MapActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private ChatContent map;
	private BaiduMap mp;
	protected MapView bmapView;
	private LocationMode mCurrentMode;
	boolean isFirstLoc = true;// 是否首次定位

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		Intent intent = _this.getIntent();
		Bundle bundle = intent.getExtras();
		map = (ChatContent) bundle.getSerializable("map");
		if (map == null){
			_this.finish();
			overridePendingTransition(R.anim.in_anim, R.anim.in_from_down );
		}
		_init();
	}

	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("查看好友位置");
		// 初始化百度地图的切图
		bmapView = (MapView) findViewById(R.id.bmapView);
		bmapView.showZoomControls(true);
		mCurrentMode = LocationMode.NORMAL;
		mp = bmapView.getMap();
		// 自定义
		BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
				.fromResource(R.drawable.public_bd_marka);
		/*
		 * mp.setMyLocationConfigeration(new
		 * MyLocationConfiguration( mCurrentMode, true,
		 * mCurrentMarker));
		 */
		mp.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		// 系统图标
		// mp.setMyLocationConfigeration(new
		// MyLocationConfiguration(mCurrentMode, true, null));
		mp.setMyLocationEnabled(true);
	/*	MyLocationData locData = new MyLocationData.Builder()
				.accuracy(arg0.getRadius())
				// 此处设置开发者获取到的方向信息，顺时针0-360
				.direction(100).latitude( map.latitude )
				.longitude( map.longitude ).build();*/
		//mp.setMyLocationData(locData);
		if (isFirstLoc) {
			isFirstLoc = false;
			LatLng ll = new LatLng(  map.latitude ,  map.longitude );
			// MapStatusUpdate u =
			// MapStatusUpdateFactory.newLatLng(ll);

			OverlayOptions ooA = new MarkerOptions()
					.position(ll).icon(mCurrentMarker)
					.zIndex(9).draggable(true);
			mp.addOverlay(ooA);
			MapStatus mMapStatus = new MapStatus.Builder()
					.target(ll).zoom(18).build();
			MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
					.newMapStatus(mMapStatus);
			mp.animateMapStatus(mMapStatusUpdate);
			// mp.setMapStatus( mMapStatusUpdate );
			Button button = new Button(_this);
			button.setBackgroundResource(R.drawable.popup);
			button.setText(map.locationAddress);
			InfoWindow mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -47, null);
			mp.showInfoWindow(mInfoWindow);
		}
					

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {

		case R.id.bt_home_msg:
			finish();
			overridePendingTransition(R.anim.in_anim, R.anim.in_from_down );
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		bmapView.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		bmapView.onPause();
		super.onPause();

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub

		bmapView.onResume();
		super.onResume();
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		_this.finish();
		overridePendingTransition(R.anim.in_anim, R.anim.in_from_down );
	}
}
