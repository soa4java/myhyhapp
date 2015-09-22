/**   
* @Title: Location.java 
* @Package com.beelnn.util 
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobai   
* @date 2013-3-12 上午11:51:33 
* @version V1.0   
*/
package com.gezitech.util;

import java.util.Iterator;

import android.content.Context;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
public class LocationUtil {
	public double latitude =0.0,longitude =0.0;
	private LocationManager lm;
	private Location location;
	public  LocationUtil(Context context){
		//获取经纬度 
		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE); 
		if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			String bestProvider = lm.getBestProvider(getCriteria(), true); 
			lm.requestLocationUpdates(bestProvider, 1000, 1, locationListener);
			location = lm.getLastKnownLocation(bestProvider);
			if(location == null){// 判断Location对象是否为空，为空，则使用网络定位
				location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
			if(location != null){
				latitude = location.getLatitude(); //经度 
				longitude = location.getLongitude(); //纬度 
			}	
			lm.addGpsStatusListener(listener);    
		}
	}
	/** 
	* @Title: getCriteria 
	* @Description: TODO(gps条件的设置) 
	* @param @return    设定文件 
	* @return Criteria    返回类型 
	* @throws 
	*/
	private Criteria getCriteria() {
		 Criteria criteria=new Criteria();   
         //设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细       
         criteria.setAccuracy(Criteria.ACCURACY_FINE);         
         //设置是否要求速度       
         criteria.setSpeedRequired(false);    
         // 设置是否允许运营商收费          
         criteria.setCostAllowed(false);      
         //设置是否需要方位信息        
         criteria.setBearingRequired(false);  
         //设置是否需要海拔信息    
         criteria.setAltitudeRequired(false);      
         // 设置对电源的需求         
         criteria.setPowerRequirement(Criteria.POWER_LOW);    
         return criteria; 
	}
	GpsStatus.Listener listener = new GpsStatus.Listener() { 
        public void onGpsStatusChanged(int event) {    
            switch (event) {        
                //第一次定位       
                case GpsStatus.GPS_EVENT_FIRST_FIX:   
                    break;           
                //卫星状态改变          
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:      
                      //获取当前状态           
                      GpsStatus gpsStatus=lm.getGpsStatus(null);    
                      //获取卫星颗数的默认最大值       
                      int maxSatellites = gpsStatus.getMaxSatellites();       
                      //创建一个迭代器保存所有卫星       
                      Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();     
                      int count = 0;                
                      while (iters.hasNext() && count <= maxSatellites) {         
                          GpsSatellite s = iters.next();                    
                          count++; 
                      }       
                      break;            //定位启动     
                case GpsStatus.GPS_EVENT_STARTED:      
                      break;           
                //定位结束        
                case GpsStatus.GPS_EVENT_STOPPED:      
                      break;      
                }       
            }
      };
      //位置监听  
      private LocationListener locationListener=new LocationListener() {  
              /***位置信息变化时触发*/    
              public void onLocationChanged(Location location) { 
            	  LocationUtil.this.location = location;
              }             
              /***GPS状态变化时触发 */  
              public void onStatusChanged(String provider, int status, Bundle extras) {     
                      switch (status) {      
		                  //GPS状态为可见时            
		                  case LocationProvider.AVAILABLE:     
		                       break;           
		                  //GPS状态为服务区外时       
		                  case LocationProvider.OUT_OF_SERVICE:       
		                       break;          
		                  //GPS状态为暂停服务时     
		                  case LocationProvider.TEMPORARILY_UNAVAILABLE: 
		                       break;         
		                  }     
                      }           
              /** GPS开启时触发*/   
              public void onProviderEnabled(String provider) { 
                   location=lm.getLastKnownLocation(provider);  
                      
              }        
              /**         * GPS禁用时触发         */     
              public void onProviderDisabled(String provider) {
            	  
            	  location = null;
              }     
       };
}
