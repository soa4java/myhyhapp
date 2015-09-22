package com.gezitech.service.managers;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gezitech.basic.GezitechApplication;
import com.gezitech.basic.GezitechException;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynInsertListener;
import com.gezitech.contract.GezitechManager_I.OnAsynRequestListener;
import com.gezitech.contract.GezitechManager_I.OnAsynUpdateListener;
import com.gezitech.entity.PageList;
import com.gezitech.http.HttpUtil;
import com.gezitech.http.Response;
import com.gezitech.util.NetUtil;
import com.hyh.www.R;
import com.hyh.www.entity.Companytype;
import com.hyh.www.entity.Configuration;
import com.hyh.www.entity.News;
import com.hyh.www.entity.Pay;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

//支付管理器
public class PayManager {
	private PayManager _this = this;
	static PayManager instance = null;
	public static PayManager getInstance() {
		if (instance == null) {
			instance = new PayManager();
		}
		return instance;
	}
	
	//获取支付验签数据
	//consume(支付)/recharge(充值)
	//交易id/money
	public void getannouncementdetails(String paytype, String paykey , double money, int payway, final OnAsynGetOneListener listener){
		RequestParams params = new RequestParams();
		params.put("paytype", paytype);
		params.put("paykey", paykey);
		params.put("money", money);
		params.put("payway", payway );
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/Pay/getPayInfo", true, params, new AsyncHttpResponseHandler(){    	
	    		@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					if( arg0 != 200 ){
						listener.OnAsynRequestFail("-1", GezitechApplication.getContext().getString( R.string.data_error ) );
					}
	    			try{
	    				Response response = new Response( new String( arg2 ) );
	    				JSONObject root = response.asJSONObject();
						if( root.getInt("state") != 1 ){//0
							if(listener != null) listener.OnAsynRequestFail("-1", root.getString("msg") );
							return;
						}
						JSONObject data = root.getJSONObject("data");
						Pay pay  = new Pay( data );;
						
						listener.OnGetOneDone( pay );
						
					}catch(Exception ex){
						listener.OnAsynRequestFail("-1", GezitechApplication.getContext().getString( R.string.data_error ));
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
	   }else{
	    	if(listener!=null){
	    		listener.OnAsynRequestFail("-1", GezitechApplication.getContext().getString( R.string.network_error ) );
	    	} 	
	   } 
	}


}
