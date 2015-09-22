package com.gezitech.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.gezitech.basic.GezitechException;
import com.gezitech.http.Response;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

/**
 * 
 * @author xiaobai
 * 2015-6-30
 * @todo( 个推三方接受  )
 */
public class GetuiPushReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
            	
            	//Log.v("nihao","===cid===1");
            	
            	 byte[] payload = bundle.getByteArray("payload");
                 if (payload != null)
                 {
                	 //Log.v("nihao","===cid===1"+new String( payload ));
                	String payloadStr = new String( payload ) ;
                	if( !payloadStr.contains( "{" ) ){
                		return ;
                	}
	        		Response response = new Response( new String( payload ) );
	        		JSONObject root;
	        		try {
	        			root = response.asJSONObject();
	
	        			String title = root.has("title") ? root.getString("title") : "";
	        			String description = root.has("description") ? root.getString("description") : "";
	        			String key_type = root.has("key_type") ? root.getString("key_type") : "";
	        			String key_value = root.has("key_value") ? root.getString("key_value") : "";
	        					
	        			GezitechService.getInstance().NotificationAction(key_type, key_value, title,description );
	        					
	        		} catch (GezitechException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		} catch (JSONException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		}
                 }
                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
            	String clientid = bundle.getString("clientid");
            	
            	final SharedPreferences sp = context.getSharedPreferences(
        				"baiduPushInfo", Context.MODE_PRIVATE);
        		SharedPreferences.Editor editor = sp.edit();
        		if ( clientid != null && !clientid.equals("") ) {
        			editor.putString("clientid", clientid);
        			editor.commit();
        		}
            	
            	
                break;

            case PushConsts.THIRDPART_FEEDBACK:
            	Log.v("nihao","===cid===3");
                /*
                 * String appid = bundle.getString("appid"); String taskid =
                 * bundle.getString("taskid"); String actionid = bundle.getString("actionid");
                 * String result = bundle.getString("result"); long timestamp =
                 * bundle.getLong("timestamp");
                 * 
                 * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo", "taskid = " +
                 * taskid); Log.d("GetuiSdkDemo", "actionid = " + actionid); Log.d("GetuiSdkDemo",
                 * "result = " + result); Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
                 */
                break;
           
            default:
                break;
        }
		
	}

}
