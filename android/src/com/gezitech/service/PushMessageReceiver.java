/**   
 * @Title: PushMessageReceiver.java 
 * @Package com.beelnn.service 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author xiaobai   
 * @date 2013-5-21 下午2:42:02 
 * @version V1.0   
 */
package com.gezitech.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.gezitech.basic.GezitechException;
import com.gezitech.entity.User;
import com.gezitech.http.Response;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.hyh.www.WelcomeActivity;
import com.hyh.www.ZhuyeActivity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;

public class PushMessageReceiver extends FrontiaPushMessageReceiver {

	@Override
	public void onBind(Context context, int errorCode, String appid,
			String userId, String channelId, String requestId) {
		// TODO Auto-generated method stub
		// 百度推送生成的参数传递到客户端
		 String responseString = "onBind errorCode=" + errorCode + " appid="  
	                + appid + " userId=" + userId + " channelId=" + channelId  
	                + " requestId=" + requestId;  
	        Log.e(TAG, responseString);  
		final SharedPreferences sp = context.getSharedPreferences(
				"baiduPushInfo", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		if (userId != null && !userId.equals("") && channelId != null
				&& !channelId.equals("")) {
			editor.putString("push_user_id", userId);
			editor.putString("push_channel_id", channelId);
			editor.commit();
		}
	}

	@Override
	public void onDelTags(Context arg0, int arg1, List<String> arg2,
			List<String> arg3, String arg4) {

	}

	@Override
	public void onListTags(Context arg0, int arg1, List<String> arg2,
			String arg3) {

	}

	@Override
	public void onMessage(Context arg0, String arg1, String arg2) {

		if( arg1 == null ) return;

		Response response = new Response( new String( arg1 ) );
		JSONObject root;
		try {
			root = response.asJSONObject();

			String title = root.has("title") ? root.getString("title") : "";
			String description = root.has("description") ? root.getString("description") : "";
			JSONObject customContentString = root.has("custom_content") ? root
					.getJSONObject("custom_content") : null;

			if (customContentString == null)
				return;

			String key_type = customContentString.has("key_type") ? customContentString
					.getString("key_type") : "";
			String key_value = customContentString.has("key_value") ? customContentString
					.getString("key_value") : "";
					
			GezitechService.getInstance().NotificationAction(key_type, key_value, title,description );
					
		} catch (GezitechException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onNotificationClicked(Context context, String title,
			String description, String customContentString) {

		Intent intent = new Intent();
		if (isTopActivity(context) || checkBrowser(context)) {
			intent.setClass(context.getApplicationContext(),
					startApp() == null ? WelcomeActivity.class
							: ZhuyeActivity.class);
		} else {
			intent.setClass(context.getApplicationContext(),
					startApp() == null ? WelcomeActivity.class
							: ZhuyeActivity.class);
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		context.getApplicationContext().startActivity(intent);

	}

	private User startApp() {
		GezitechDBHelper<User> dbHelper = new GezitechDBHelper<User>(User.class);
		ArrayList<User> list = dbHelper.query("islogin=1", 1, "id desc");
		dbHelper.close();
		if (list == null || list.size() < 1)
			return null;

		return list.get(0);

	}

	@Override
	public void onSetTags(Context arg0, int arg1, List<String> arg2,
			List<String> arg3, String arg4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnbind(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * TODO( 判断应用是否应用 )
	 */
	private boolean isTopActivity(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			// 应用程序位于堆栈的顶层
			if ("com.hyh.www".equals(tasksInfo.get(0).topActivity
					.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkBrowser(Context context) {
		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo("com.hyh.www",
							PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
}
