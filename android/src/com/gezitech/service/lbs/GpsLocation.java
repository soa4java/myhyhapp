package com.gezitech.service.lbs;


import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class GpsLocation implements Location_I, Runnable,LocationListener{
	
	GpsLocation oThis = null;
	private boolean TIME_OUT = false;
	private boolean DATA_CONNTECTED = false;
	private LocationManager locationManager = null;
	private Context context = null;
	private ItudeCallBack itudeCallBack = null;
	private long timeOut;
	public GpsLocation(Context context,ItudeCallBack itudeCallBack,long timeOut){	
		this.context = context;
		this.itudeCallBack = itudeCallBack;
		this.timeOut = timeOut;
		oThis = this;
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);	
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 100, this);
		if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			TIME_OUT = true;
		}
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(!TIME_OUT)
				TIME_OUT = true;
				Message msg = handler.obtainMessage();
				msg.what = 1;
				handler.sendMessage(msg);
			}
		}, timeOut);
	}
	@Override
	public SItude getItude() {
        // 查找到服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗

        String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
        Location location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
       // updateToNewLocation(location);
        // 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米
        //locationManager.requestLocationUpdates(provider, 100 * 1000, 500,locationListener);
        if(location != null){
	        SItude itude = new SItude();
	        itude.latitude = String.valueOf(location.getLatitude());
	        itude.longitude = String.valueOf(location.getLongitude());   
	        return itude;
        }
        return null;
        
	}

	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){	
				case 0:
					SItude sItude = (SItude)msg.obj;
					if(itudeCallBack != null)
						itudeCallBack.OnItudeGetDone(sItude);
				break;
				case 1:
					if(itudeCallBack != null)
						itudeCallBack.OnItudeGetFail("GPS定位失败");
				break;
			}
			
		}
	};
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while(!DATA_CONNTECTED && !TIME_OUT){	
			Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location != null && this.itudeCallBack != null) {
				//获得上一次数据
				SItude itude = new SItude();
		        itude.latitude = String.valueOf(location.getLatitude());
		        itude.longitude = String.valueOf(location.getLongitude());   
				Message msg = handler.obtainMessage();
				msg.what = 0;
				msg.obj = itude;
				handler.sendMessage(msg);
				break;
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		SItude itude = new SItude();
        itude.latitude = String.valueOf(location.getLatitude());
        itude.longitude = String.valueOf(location.getLongitude());   
		DATA_CONNTECTED = true;
		Message msg = handler.obtainMessage();
		msg.what = 0;
		msg.obj = itude;
		handler.sendMessage(msg);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
