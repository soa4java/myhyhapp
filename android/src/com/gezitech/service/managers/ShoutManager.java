package com.gezitech.service.managers;

import java.util.ArrayList;
import java.util.Date;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.gezitech.basic.GezitechApplication;
import com.gezitech.basic.GezitechException;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListArrayListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynInsertListener;
import com.gezitech.contract.GezitechManager_I.OnAsynProgressListener;
import com.gezitech.contract.GezitechManager_I.OnAsynRequestFailListener;
import com.gezitech.contract.GezitechManager_I.OnAsynRequestListener;
import com.gezitech.contract.GezitechManager_I.OnAsynUpdateListener;
import com.gezitech.entity.PageList;
import com.gezitech.entity.User;
import com.gezitech.http.HttpUtil;
import com.gezitech.http.Response;
import com.gezitech.service.GezitechService;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.gezitech.util.NetUtil;
import com.hyh.www.R;
import com.hyh.www.chat.ChatUtils;
import com.hyh.www.entity.Bill;
import com.hyh.www.entity.Chat;
import com.hyh.www.entity.ChatContent;
import com.hyh.www.entity.Companytype;
import com.hyh.www.entity.FieldVal;
import com.hyh.www.entity.Friend;
import com.hyh.www.entity.News;
import com.hyh.www.entity.Releasescope;
import com.hyh.www.entity.Session;
import com.hyh.www.entity.Shout;
import com.hyh.www.entity.Validtimelist;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 喊一喊管理器
 */
public class ShoutManager {
	private ShoutManager _this = this;
	static ShoutManager instance = null;

	public static ShoutManager getInstance() {
		if (instance == null) {
			instance = new ShoutManager();
		}
		return instance;
	}
	/*发布范围列表	shout/releasescope
	参数	
	page	当前页、
	pageSize	每页显示数量*/

	public void releasescope(String rangeoption, final OnAsynGetListListener listener) {
		RequestParams params = new RequestParams();
		params.put("pageSize", 10000);
		params.put("page", 1);
		params.put("rangeoption", rangeoption);
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/shout/releasescope", true, params, new AsyncHttpResponseHandler(){    	
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
						JSONArray ja = data.getJSONArray("datas");
						PageList pl = new PageList();
						Releasescope ct = null;
						for( int i = 0; i<ja.length(); i++){
							JSONObject jo = ja.getJSONObject( i );
							ct  = new Releasescope( jo );
							pl.add( ct );
						}
						
						listener.OnGetListDone( pl );
						
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
	/*有效时间列表	shout/validtimelist
	参数	
	page	当前页、
	pageSize	每页显示数量
	*/
	public void validtimelist(int type ,final OnAsynGetListListener listener){
		validtimelist( type,"",listener);
	}
	public void validtimelist(int type ,String typeid, final OnAsynGetListListener listener) {
		RequestParams params = new RequestParams();
		params.put("pageSize", 10000);
		params.put("page", 1);
		if( type >0 ){
			params.put("typetime", type );
		}
		if( !typeid.equals("") ){
			
			params.put("activetimeoption", typeid );
		}
		
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/shout/validtimelist", true, params, new AsyncHttpResponseHandler(){    	
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
						JSONArray ja = data.getJSONArray("datas");
						PageList pl = new PageList();
						Validtimelist ct = null;
						for( int i = 0; i<ja.length(); i++){
							JSONObject jo = ja.getJSONObject( i );
							ct  = new Validtimelist( jo );
							pl.add( ct );
						}
						
						listener.OnGetListDone( pl );
						
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
	/*发布喊一喊	shout/releaseshout
	参数	
	oauth_token	不能为空
	speechtime	语音时长-单位秒
	typeid	商家类型
	range	发布范围
	maxReplycount	回复人数
	activetime	有效时间
	caption	文字描述
	litpic	图片
	speech	语音*/
	public void releaseshout(RequestParams params , final OnAsynGetOneListener listener) {
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/shout/releaseshout", true, params, new AsyncHttpResponseHandler(){    	
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
						Shout ct = new Shout( data );
						listener.OnGetOneDone( ct );
						
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
	/*我发布的喊一喊	shout/myshout
	参数	
	oauth_token	不能为空
	page	当前页、
	pageSize	每页显示数量*/

	public void myshout(int page, int pageSize,final OnAsynGetListListener listener) {
		RequestParams params = new RequestParams();
		params.put("pageSize", pageSize);
		params.put("page", page);
		params.put("order", "desc");
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/shout/myshout", true, params, new AsyncHttpResponseHandler(){    	
	    		@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					if( arg0 != 200 ){
						listener.OnAsynRequestFail("-1", GezitechApplication.getContext().getString( R.string.data_error ) );
					}
	    			try{
	    				Response response = new Response( new String( arg2 ) );
	    				JSONObject root = response.asJSONObject();
						if( root.getInt("state")!= 1 ){//0
							if(listener != null) listener.OnAsynRequestFail("-1", root.getString("msg") );
							return;
							
						}
						JSONObject data = root.getJSONObject("data");
						PageList pl = new PageList();
						Shout ct = null;
						if( data.has("datas") && !data.isNull("datas") ){
							JSONArray ja = data.getJSONArray("datas");
							
							for( int i = 0; i<ja.length(); i++){
								JSONObject jo = ja.getJSONObject( i );
								ct  = new Shout( jo );
								pl.add( ct );
							}
						}
						
						listener.OnGetListDone( pl );
						
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
	/*添加账单模板	trade/addTrade
	参数	
	notes	账单备注
	oauth_token	不能为空
	money	账单金额
	activetime	限时支付
	litpic	图片*/
	public void addTrade(RequestParams params , final OnAsynInsertListener listener) {
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/trade/addTrade", true, params, new AsyncHttpResponseHandler(){    	
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
						
						listener.onInsertDone("1");
						
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
	/*获取账单模版列表	trade/getrTradeList
	参数	
	page	当前页、
	pageSize	每页显示数量
	oauth_token	不能为空
	uid	用户id*/

	public void getrTradeList(int page, int pageSize,final OnAsynGetListListener listener) {
		RequestParams params = new RequestParams();
		params.put("pageSize", pageSize);
		params.put("page", page);
		params.put("order", "desc");
		params.put("w", 300);
		params.put("h", 300);
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/trade/getrTradeList", true, params, new AsyncHttpResponseHandler(){    	
	    		@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					if( arg0 != 200 ){
						listener.OnAsynRequestFail("-1", GezitechApplication.getContext().getString( R.string.data_error ) );
					}
	    			try{
	    				Response response = new Response( new String( arg2 ) );
	    				JSONObject root = response.asJSONObject();
						if( root.getInt("state") != 1){//0
							if(listener != null) listener.OnAsynRequestFail("-1", root.getString("msg") );
							return;
							
						}
						JSONObject data = root.getJSONObject("data");
						PageList pl = new PageList();
						Bill ct = null;
						if( data.has("datas") && !data.isNull("datas") ){
							JSONArray ja = data.getJSONArray("datas");
							
							for( int i = 0; i<ja.length(); i++){
								JSONObject jo = ja.getJSONObject( i );
								ct  = new Bill( jo );
								pl.add( ct );
							}
						}
						
						listener.OnGetListDone( pl );
						
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
	/*创建账单	Trade/addbill
	参数	
	oauth_token	不能为空
	uid	商家id
	tradecode	账单编号
	notes	账单备注
	money	账单金额
	activetime	限时支付时间
	litpic	图片
	 */
	public void addbill(RequestParams params , final OnAsynGetOneListener listener) {
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/trade/addbill", true, params, new AsyncHttpResponseHandler(){    	
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
						JSONObject data = root.has("data") ? root.getJSONObject("data") : null;
						if( data != null ){
							
							Bill bill = new Bill( data );
							
							listener.OnGetOneDone( bill );
							
						}else{
							
							if(listener != null) listener.OnAsynRequestFail("-1", GezitechApplication.getContext().getString( R.string.data_error ) );
							return;
							
						}
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
/*	获取账单	trade/getrbillList
	参数	
	oauth_token	不能为空
	id	账单id*/

	public void getrbillList(RequestParams params , final OnAsynGetOneListener listener) {
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/trade/getrbillList", true, params, new AsyncHttpResponseHandler(){    	
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
						JSONObject data = root.has("data") ? root.getJSONObject("data") : null;
						if( data != null ){
							
							Bill bill = new Bill( data );
							
							listener.OnGetOneDone( bill );
							
						}else{
							
							if(listener != null) listener.OnAsynRequestFail("-1", GezitechApplication.getContext().getString( R.string.data_error ) );
							return;
							
						}
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
	/*余额支付接口	trade/balancepayment
	参数	
	oauth_token	不能为空
	id	账单id
	tradecode	账单编号*/
	public void balancepayment(RequestParams params , final OnAsynInsertListener listener) {
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/trade/balancepayment", true, params, new AsyncHttpResponseHandler(){    	
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
						
						listener.onInsertDone("1");
						
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
	/*付款	Trade/addpai
	参数	
	oauth_token	不能为空
	bid	商家id
	tradecode	账单编号
	notes	账单备注
	money	账单金额
	activetime	限时支付时间
	litpic	图片*/
	public void addpai(RequestParams params , final OnAsynGetOneListener listener) {
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/trade/addpai", true, params, new AsyncHttpResponseHandler(){    	
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
						JSONObject data = root.has("data") ? root.getJSONObject("data") : null;
						if( data != null ){
							
							Bill bill = new Bill( data );
							
							listener.OnGetOneDone( bill );
							
						}else{
							
							if(listener != null) listener.OnAsynRequestFail("-1", GezitechApplication.getContext().getString( R.string.data_error ) );
							return;
							
						}
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
/*	确定收款接口	trade/certaincollect
	参数	
	oauth_token	不能为空
	id	账单id*/
	public void certaincollect(RequestParams params , final OnAsynInsertListener listener) {
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/trade/certaincollect", true, params, new AsyncHttpResponseHandler(){    	
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
						listener.onInsertDone( "1" );
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
	/*删除账单模板接口	trade/deletetemplets
	参数	
	oauth_token	不能为空
	id	账单模版id*/
	public void deletetemplets(RequestParams params , final OnAsynInsertListener listener) {
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/trade/deletetemplets", true, params, new AsyncHttpResponseHandler(){    	
	    		@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					if( arg0 != 200 ){
						listener.OnAsynRequestFail("-1", GezitechApplication.getContext().getString( R.string.data_error ) );
					}
	    			try{
	    				Response response = new Response( new String( arg2 ) );
	    				JSONObject root = response.asJSONObject();
						if( root.getInt("state")!= 1 ){//0
							if(listener != null) listener.OnAsynRequestFail("-1", root.getString("msg") );
							return;
						}
						listener.onInsertDone("1");
						
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
/*	删除喊一喊	shout/deleteshout
	参数	
	oauth_token	不能为空
	id	喊一喊id*/
	public void deleteshout(RequestParams params , final OnAsynInsertListener listener) {
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/shout/deleteshout", true, params, new AsyncHttpResponseHandler(){    	
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
						listener.onInsertDone("1");
						
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
/*	用户撤销付款	trade/usercancelpay
	参数	
	oauth_token	不能为空
	id	订单id*/
	public void usercancelpay(RequestParams params , final OnAsynInsertListener listener) {
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/trade/usercancelpay", true, params, new AsyncHttpResponseHandler(){    	
	    		@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					if( arg0 != 200 ){
						listener.OnAsynRequestFail("-1", GezitechApplication.getContext().getString( R.string.data_error ) );
					}
	    			try{
	    				Response response = new Response( new String( arg2 ) );
	    				JSONObject root = response.asJSONObject();
						if( root.getInt("state")!= 1 ){//0
							if(listener != null) listener.OnAsynRequestFail("-1", root.getString("msg") );
							return;
						}
						
						listener.onInsertDone("1");
						
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
/*	商家取消收款	trade/storecancelcollect
	参数	
	oauth_token	不能为空
	id	订单id*/
	public void storecancelcollect(RequestParams params , final OnAsynInsertListener listener) {
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/trade/storecancelcollect", true, params, new AsyncHttpResponseHandler(){    	
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
						
						listener.onInsertDone("1");
						
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
/*	用户确认服务	Trade/userconfirmservice
	参数	
	oauth_token	不能为空
	id	订单id*/
	public void userconfirmservice(RequestParams params , final OnAsynInsertListener listener) {
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/Trade/userconfirmservice", true, params, new AsyncHttpResponseHandler(){    	
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
						
						listener.onInsertDone("1");
						
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
/*	用户申请退款	trade/userapplyrefund
	参数	
	oauth_token	不能为空
	id	订单id*/
	public void userapplyrefund(RequestParams params , final OnAsynInsertListener listener) {
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/trade/userapplyrefund", true, params, new AsyncHttpResponseHandler(){    	
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
						
						listener.onInsertDone("1");
						
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
	//获取我的喊一喊列表，包含自己发的，和别人发给我的
	//api/Shout/getAllMyShoutList  start 0   count 10 timeflag 时间
	public void getAllMyShoutList(int start, int count, long timeflag, final OnAsynGetListListener listener) {
		RequestParams params = new RequestParams();
		params.put("start", start);
		params.put("count", count);
		params.put("timeflag", timeflag);
		
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/Shout/getAllMyShoutList", true, params, new AsyncHttpResponseHandler(){    	
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
						PageList pl = new PageList();
						final User curruser = GezitechService.getInstance().getCurrentUser();
						final GezitechDBHelper<ChatContent> chatContentDB = new GezitechDBHelper<ChatContent>(ChatContent.class);
						final GezitechDBHelper<Chat> chatDB = new GezitechDBHelper<Chat>(Chat.class);
						if( data.has("datas") && !data.isNull("datas") ){
							JSONArray ja = data.getJSONArray("datas");
							for( int i = 0; i<ja.length(); i++){
								JSONObject jo = ja.getJSONObject( i );
								final Shout shout  = new Shout( jo );
								//组装聊天的信息
								String messageContent = "{\"range\":"+shout.range+",\"id\":"+shout.id+",\"long\":\""+shout.longitude+"\"" +
										",\"ctime\":"+shout.ctime+",\"uid\":"+shout.uid+",\"typeid\":\""+shout.typeid+"\",\"caption\":\""+shout.caption+"\"," +
										"\"activetime\":\""+shout.activetime+"\",\"maxReplycount\":\""+shout.maxReplycount+"\",\"lat\":\""+shout.latitude+"\"," +
												"\"litpic\":\""+shout.litpic+"\",\"speechtime\":\""+shout.speechtime+"\",\"speech\":\""+shout.speech+"\"}"; 
								
								
								// 存储聊天记录
								ChatContent chantContent = new ChatContent();
								chantContent.chatid = shout.uid;
								chantContent.type = 9;
								chantContent.ctime =  shout.ctime*1000;
								chantContent.body = messageContent;
								chantContent.uid = shout.uid;
								chantContent.isfriend = 4;
								chantContent.myuid = curruser.id;
								chantContent.hyhid = shout.id;
								chatContentDB.insert( chantContent );
								/*if( shout.uid != curruser.id ){//如果不是当前用户
									//获取用户信息 没有则去服务端获取
									ChatManager.getInstance().getFriendData( shout.uid, new OnAsynGetOneListener() {				
										@Override
										public void OnAsynRequestFail(String errorCode, String errorMsg) {
										}		
										@Override
										public void OnGetOneDone(GezitechEntity_I entity) {}
									});
								}*/
								try{
									shout.sessionlist = jo.has("sessionlist") ? jo.getJSONArray("sessionlist") : null;
								}catch( Exception e){
									shout.sessionlist = null;
								}
								if( shout.sessionlist!= null && shout.sessionlist.length()> 0 ){
									//处理会话列表
									for( int j=0; j< shout.sessionlist.length() ; j++ ){
										
										JSONObject joo = shout.sessionlist.getJSONObject( j ); 
										final Session session = new Session( joo );
										final User user  = new User( !joo.isNull("user") && joo.has("user") ? joo.getJSONObject("user") : null ) ; 
										
										//final long fuid = session.sender==curruser.id ? session.receiver : session.sender;
										//获取用户信息 没有则去服务端获取
										/*ChatManager.getInstance().getFriendData( fuid, new OnAsynGetOneListener() {				
											@Override
											public void OnAsynRequestFail(String errorCode, String errorMsg) {
											}		
											@Override
											public void OnGetOneDone(GezitechEntity_I entity) {
												Friend friend = (Friend) entity;*/
												//创建聊天
												Chat chat = ChatManager.getInstance().getChatItem( user.id, shout.id);
												if( chat==null  ){
													chat = new Chat();
													chat.uid = user.id;
													chat.username = FieldVal.value( user.nickname ).equals("") ? user.username : user.nickname  ;
													chat.isfriend = 2;
													chat.lastcontent = ChatUtils.getTypeStr( session.content, session.type );
													chat.head = user.head;
													chat.ctime =  System.currentTimeMillis();
													if(  GezitechApplication.currUid == user.id  ){
														chat.unreadcount = 0;
													}else{
														chat.unreadcount = 1;
													}
													chat.istop = user.istop;
													chat.myuid = curruser !=null ? curruser.id : 0;
													chat.hyhid = shout.id;
													chatDB.insert( chat );
												}else{
													chat.lastcontent = ChatUtils.getTypeStr( session.content, session.type);
													chat.ctime =  System.currentTimeMillis();
													if(  GezitechApplication.currUid == user.id  ){
														chat.unreadcount = 0;
													}else{
														chat.unreadcount = chat.unreadcount+1;
													}
													chat.isfriend = 2;//user.groupId <3 ? 3 : ( user.isfriend >0 ? 1 : 2 );
													chatDB.save( chat );
												}
												//存储聊天记录
												ChatContent chantContents = new ChatContent();
												chantContents.chatid = user.id;
												chantContents.type =  session.type;
												chantContents.ctime =  System.currentTimeMillis();
												chantContents.body = session.content;
												chantContents.isfriend = 2;//user.groupId <3 ? 3 : ( user.isfriend >0 ? 1 : 2 );
												chantContents.uid = user.id;
												chantContents.myuid = curruser !=null ? curruser.id : 0;
												chantContents.hyhid = shout.id;
												
												chatContentDB.insert( chantContents );
										//	}
										//});
									}
								}
								
								pl.add( shout );
							}
						}
						
						listener.OnGetListDone( pl );
						
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
	/**
	 * 删除喊一喊会话
	 */
	public void deleteShoutSession(RequestParams params , final OnAsynInsertListener listener) {
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/Shout/deleteShoutSession", true, params, new AsyncHttpResponseHandler(){    	
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
						
						listener.onInsertDone("1");
						
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
	/**
	 * 删除喊一喊会话
	 */
	public void getShoutSessionTime(RequestParams params , final OnAsynInsertListener listener) {
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/shout/getShoutSessionTime", true, params, new AsyncHttpResponseHandler(){    	
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
						
						int time = root.has("time") ? root.getInt("time") : 0 ;						
						listener.onInsertDone( time +"" );
						
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
