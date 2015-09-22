package com.gezitech.service;
import java.util.HashMap;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
/**
 * 运行在后台的服务进程，注意，与当前应用不是同一进程
 * @author Administrator
 *
 */
public class BackgroundService extends Service {

	private Binder binder = new LocalBinder();
	private HashMap<String, Object> dataStorage;//数据中转站，在一些不能直接存数据的地方，通过此处中转数据。
	public static BackgroundService instance;
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		
	}
	private void _init(){
		//链接 xmpp 服务器
		
		
		
	}

    /**
     * 往中转站中存入数据
     * @param key
     * @param value
     */
    public void addStorageData(String key, Object value){
    	if(dataStorage == null)dataStorage = new HashMap<String, Object>();
    	if(dataStorage.containsKey(key))dataStorage.remove(key);
    	dataStorage.put(key, value);
    }
    /**
     * 从中转站中提取数据
     * @param key
     * @return
     */
    public Object getStorageData(String key){
    	if(dataStorage == null)return null;
    	return dataStorage.remove(key);
    }
    /**
     * 绑定器
     * @author Administrator
     * 
     */
	public class LocalBinder extends Binder{
		BackgroundService getService(){
		 	return BackgroundService.this;
		}
	}
	
}
