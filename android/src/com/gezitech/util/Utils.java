/**   
* @Title: Utils.java 
* @Package com.beelnn.util 
* @Description: TODO(用一句话描述该文件做什么) 
* @author xiaobai   
* @date 2013-5-22 上午9:54:54 
* @version V1.0   
*/
package com.gezitech.util;

import com.gezitech.basic.GezitechApplication;
import com.hyh.www.R;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

/**
 * @author Administrator
 *
 */
public class Utils {
		// 在百度开发者中心查询应用的API Key
		public static final String API_KEY = GezitechApplication.getContext().getString(R.string.api_key);
		public static final String TAG = "MainActivity";
		public static final String RESPONSE_METHOD = "method";
		public static final String RESPONSE_CONTENT = "content";
		public static final String RESPONSE_ERRCODE = "errcode";
		protected static final String ACTION_LOGIN = "com.baidu.pushdemo.action.LOGIN";
		public static final String ACTION_MESSAGE = "com.baiud.pushdemo.action.MESSAGE";
		public static final String ACTION_RESPONSE = "bccsclient.action.RESPONSE";
		public static final String ACTION_SHOW_MESSAGE = "bccsclient.action.SHOW_MESSAGE";
		protected static final String EXTRA_ACCESS_TOKEN = "access_token";
		public static final String EXTRA_MESSAGE = "message";
		
		// 获取AppKey
	    public static String getMetaValue(Context context, String metaKey) {
	        Bundle metaData = null;
	        String apiKey = null;
	        if (context == null || metaKey == null) {
	        	return null;
	        }
	        try {
	            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
	                    context.getPackageName(), PackageManager.GET_META_DATA);
	            if (null != ai) {
	                metaData = ai.metaData;
	            }
	            if (null != metaData) {
	            	apiKey = metaData.getString(metaKey);
	            }
	        } catch (NameNotFoundException e) {

	        }
	        return apiKey;
	    }

}
