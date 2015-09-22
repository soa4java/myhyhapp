package com.hyh.www.chat;

import java.io.File;
import java.io.IOException;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.util.IOUtil;
import com.hyh.www.R;
import com.hyh.www.widget.YMDialog;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * @author xiaobai (map)
 */
public class MyMapActivity extends GezitechActivity implements OnClickListener {
	private MyMapActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private BaiduMap mp;
	protected MapView bmapView;
	boolean isFirstLoc = true;// 是否首次定位
	private LocationMode mCurrentMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mymap);
		_init();
	}

	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setText("发送");
		bt_my_post.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final YMDialog ymdialog = new YMDialog(_this);
				ymdialog.setHintMsg(_this.getString(R.string.send_address));
				ymdialog.setConfigButton(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ymdialog.dismiss();

						// TODO Auto-generated method stub
						if (mp != null) {

							GezitechAlertDialog.loadDialog(_this);
							mp.snapshot(new BaiduMap.SnapshotReadyCallback() {
								@Override
								public void onSnapshotReady(Bitmap arg0) {
									try {
										GezitechAlertDialog.closeDialog();
										File originFile = IOUtil
												.makeLocalImage(arg0, null);
										Intent intent = new Intent();
										intent.setAction(originFile.getPath());
										_this.setResult(1005, intent);
										_this.finish();
										overridePendingTransition(
												R.anim.in_anim,
												R.anim.in_from_down);

									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
							});
						}
					}
				}).setCloseButton(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ymdialog.dismiss();
					}
				});

			}
		});

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("分享我的位置");

		// 初始化百度地图的切图
		bmapView = (MapView) findViewById(R.id.bmapView);
		bmapView.showZoomControls(false);
		mp = bmapView.getMap();
		mp.setMyLocationEnabled(true);
		mCurrentMode = LocationMode.NORMAL;
		
		//BDLocation oldmap = GezitechApplication.getInstance().getLocation();
		
		// 初始化百度地图的位置
		GezitechApplication.getInstance().getBDLocation(
				new BDLocationListener() {

					@Override
					public void onReceiveLocation(BDLocation arg0) {
						if (arg0 == null || bmapView == null)
							return;
						GezitechApplication.getInstance().setBDLocation(arg0);
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

						MyLocationData locData = new MyLocationData.Builder()
								.accuracy(arg0.getRadius()).direction(100)
								.latitude(arg0.getLatitude())
								.longitude(arg0.getLongitude()).build();
						mp.setMyLocationData(locData);
						if (isFirstLoc) {
							isFirstLoc = false;
							LatLng ll = new LatLng(arg0.getLatitude(), arg0
									.getLongitude());
							// MapStatusUpdate u =
							// MapStatusUpdateFactory.newLatLng(ll);

							OverlayOptions ooA = new MarkerOptions()
									.position(ll).icon(mCurrentMarker)
									.zIndex(9).draggable(true);
							mp.addOverlay(ooA);

							MapStatus mMapStatus = new MapStatus.Builder()
									.target(ll).zoom(16).build();
							MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
									.newMapStatus(mMapStatus);
							mp.animateMapStatus(mMapStatusUpdate);
							// mp.setMapStatus( mMapStatusUpdate );
						}
					}
				});
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {

		case R.id.bt_home_msg:
			finish();
			overridePendingTransition(R.anim.in_anim, R.anim.in_from_down);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mp.setMyLocationEnabled(false);
		bmapView.onDestroy();
		bmapView = null;
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
		super.onBackPressed();
		overridePendingTransition(R.anim.in_anim, R.anim.in_from_down);
	}
}
