package com.gezitech.basic;
import java.util.ArrayList;
import java.util.HashMap;

import org.jivesoftware.smack.XMPPConnection;

import android.content.SharedPreferences;
import android.util.Log;

import com.baidu.frontia.FrontiaApplication;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.gezitech.entity.AppInfo;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.gezitech.util.IOUtil;

/**
 * 
 * @author xiaobai
 * 2014-5-6
 * @todo( app应用类 )
 */
public class GezitechApplication extends FrontiaApplication{
	public static GezitechApplication instance;
	public int new_alarm_count = 0;
	public boolean isShow  = false;
	public GezitechApplication _this = this;
	private LocationClient mLocationClient;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	public static BDLocation mLocation;
	public static SharedPreferences systemSp;
	public static HashMap<String, String> selectPic = new HashMap<String, String>(); //选择图片的建值对
	public static int selectPhontCount = 9;//选择图片的数量
	public static long currUid = 0;
	public static long hyhId = 0;
	public static boolean isActive = true;//app是否激活 前台后台
	public static long verifyTime = 0; //验证码倒计时
	public static XMPPConnection connection = null;
	public static int isConnection  = 0 ; //是否正在连接
    public static GezitechApplication getContext() {
        return instance;
    }
    public static GezitechApplication getInstance() {
		return instance;
	}
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;     
        systemSp = this.getSharedPreferences("systemSetting", 0);
        
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext  百度地图
     	SDKInitializer.initialize(this);
        
     	
    }
    
    
    /** 
	* @Title: _getLocation 
	* @Description: TODO(获取当前地理位置经度和纬度) 
	* @return void    返回类型 
	* @throws 
	*/
    public BDLocation getLocation() {
		return mLocation;
	}
	/**
	 * 获取百度的经纬度
	 * @param listener
	 */
	public void getBDLocation(BDLocationListener listener){
		mLocationClient = new LocationClient(this.getApplicationContext());
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType( "bd09ll" );//返回的定位结果是百度经纬度,默认值gcj02 bd09ll百度定位SDK可以返回三种坐标系，分别是bd09, bd09ll和gcj02，其中bd09ll能无偏差地显示在百度地图上。 
		option.setLocationMode( tempMode );//设置定位模式
		option.setScanSpan( 5000 );//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress( true );//反地理编码
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(listener);
		mLocationClient.start();
	}  
	public void setBDLocation(BDLocation location){
		mLocation = location;
		if(mLocationClient!=null && mLocationClient.isStarted()){
			mLocationClient.stop();
			mLocationClient = null;
		}
		
	}
	//获取计时的时间与现在的时间差
	public static int getTimeDeff(){
		
		long timeDeff = System.currentTimeMillis() - verifyTime;
		
		return (int)(timeDeff/1000);
		
	}
	
}
