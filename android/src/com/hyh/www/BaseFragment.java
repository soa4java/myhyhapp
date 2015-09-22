package com.hyh.www;

import com.gezitech.basic.GezitechApplication;
import com.gezitech.entity.User;
import com.gezitech.service.GezitechService;
import com.gezitech.util.NetUtil;
import com.gezitech.util.ToastMakeText;
import com.umeng.analytics.MobclickAgent;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.ContentObserver;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author xiaobai 2014-7-28
 * @todo( Fragment 基类 )
 */
public class BaseFragment extends Fragment {
	public static ZhuyeActivity fc;
	public static FragmentActivity activity;
	public static User user;


	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fc = (ZhuyeActivity) getActivity();

		activity = getActivity();
		user = GezitechService.getInstance().getCurrentLoginUser(activity);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return super.onCreateView(inflater, container, savedInstanceState);
	}
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	// 弹出消息
	public void Toast(String str) {

		new ToastMakeText(this.getActivity()).Toast(str);

	}

	// 统计
	@Override
	public void onResume() {
		super.onResume();
		
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(getActivity());
	}

	protected void gpsAction(TextView tv) {
		if (!NetUtil.isGpsEnabled(activity)) {
			tv.setVisibility(View.VISIBLE);
			tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					try {
						activity.startActivity(intent);

					} catch (ActivityNotFoundException ex) {

						// General settings activity
						intent.setAction(Settings.ACTION_SETTINGS);
						try {
							activity.startActivity(intent);
						} catch (Exception e) {
						}
					}
				}
			});
		} else {
			tv.setVisibility(View.GONE);
		}
	}
}
