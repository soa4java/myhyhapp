package com.gezitech.service.managers;

import java.util.Date;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gezitech.basic.GezitechApplication;
import com.gezitech.basic.GezitechException;
import com.gezitech.contract.GezitechManager_I.OnAsynRequestFailListener;
import com.gezitech.http.HttpUtil;
import com.gezitech.http.Response;
import com.hyh.www.R;
import com.hyh.www.entity.Pay;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 
 * @author xiaobai
 * 2014-4-22
 * @todo(  百度推送 回传推送信息  )
 */
public class PushManager {
	private PushManager _this = this;
	static PushManager instance = null;
	public static PushManager getInstance(){
		if(instance == null ){
			instance =  new PushManager();
		}
		return instance;
	}
	
	/**
	 * 
	 * TODO( 回传推送信息 )
	 */
	public void setPushInfo(RequestParams params,final OnAsynRequestFailListener listener ){
		HttpUtil.get("api/Common/setPushInfo", true, params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				if( arg0 != 200 ){
					listener.OnAsynRequestFail("-1", GezitechApplication.getContext().getString( R.string.data_error ) );
				}
				try {
					Response response = new Response( new String( arg2 ) );
					JSONObject root = response.asJSONObject();
					int state = root.getInt("state");
					String msg = root.has("msg") ? root.getString("msg") : "";
					if( state == 1 ){
						listener.OnAsynRequestFail("1", msg);
					}else{
						listener.OnAsynRequestFail("-1", msg);
					}
				} catch (JSONException e) {
					listener.OnAsynRequestFail("-1", GezitechApplication.getContext().getString(R.string.save_fail) );
				} catch (GezitechException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				listener.OnAsynRequestFail("-1", GezitechApplication.getContext().getString( R.string.data_error ) );
			}
			
            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要掉
            	
            }
		});
	}
}
