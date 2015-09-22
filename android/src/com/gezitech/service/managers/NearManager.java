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
import com.gezitech.contract.GezitechManager_I.OnAsynGetJsonObjectListener;
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
import com.hyh.www.entity.NearFeedBack;
import com.hyh.www.entity.NearMsg;
import com.hyh.www.entity.News;
import com.hyh.www.entity.PriceRange;
import com.hyh.www.entity.PubRange;
import com.hyh.www.entity.RangeBuyRecord;
import com.hyh.www.entity.Releasescope;
import com.hyh.www.entity.Session;
import com.hyh.www.entity.Shout;
import com.hyh.www.entity.Validtimelist;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 附近人的管理器
 */
public class NearManager {
	private NearManager _this = this;
	static NearManager instance = null;

	public static NearManager getInstance() {
		if (instance == null) {
			instance = new NearManager();
		}
		return instance;
	}
	/*获取能发布的半径 
	参数	
	page	当前页、
	pageSize	每页显示数量*/

	public void getPubRangeList( final OnAsynGetListListener listener ) {
		RequestParams params = new RequestParams();
		params.put("pageSize", 10000);
		params.put("page", 1);
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/Nearby/getPubRangeList", true, params, new AsyncHttpResponseHandler(){    	
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
						JSONArray ja = root.getJSONArray("data");
						PageList pl = new PageList();
						PubRange ct = null;
						for( int i = 0; i<ja.length(); i++){
							JSONObject jo = ja.getJSONObject( i );
							ct  = new PubRange( jo );
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
	/*获取范围列表
	参数	
	page	当前页、
	pageSize	每页显示数量*/

	public void getRangetypeList( final OnAsynGetListListener listener ) {
		RequestParams params = new RequestParams();
		params.put("pageSize", 10000);
		params.put("page", 1);
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/Nearby/getRangetypeList", true, params, new AsyncHttpResponseHandler(){    	
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
						JSONArray ja = root.getJSONArray("data");
						PageList pl = new PageList();
						PubRange ct = null;
						for( int i = 0; i<ja.length(); i++){
							JSONObject jo = ja.getJSONObject( i );
							ct  = new PubRange( jo );
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
	/*获取范围价格列表
	参数	
	page	当前页、
	pageSize	每页显示数量*/

	public void getRangepriceList( long typeid , final OnAsynGetListListener listener ) {
		RequestParams params = new RequestParams();
		params.put("typeid", typeid);
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/Nearby/getRangepriceList", true, params, new AsyncHttpResponseHandler(){    	
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
						JSONArray ja = root.getJSONArray("data");
						PageList pl = new PageList();
						PriceRange ct = null;
						for( int i = 0; i<ja.length(); i++){
							JSONObject jo = ja.getJSONObject( i );
							ct  = new PriceRange( jo );
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
	/**
	 * 
	 * TODO( 生成范围购买订单 )
	 */
	public void addRangeBuyRecord( long  pid, final OnAsynGetOneListener listener ) {
		RequestParams params = new RequestParams();
		params.put("pid", pid);
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/Nearby/addRangeBuyRecord", true, params, new AsyncHttpResponseHandler(){    	
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
						JSONObject ja = root.getJSONObject("data");
						RangeBuyRecord ct =  new RangeBuyRecord( ja );
						
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
	//发布信息 
	public void addNearbycontent(RequestParams params, final OnAsynGetOneListener listener  ){
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/Nearby/addNearbycontent", true, params, new AsyncHttpResponseHandler(){    	
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
						JSONObject ja = root.getJSONObject("data");
						NearMsg ct =  new NearMsg( ja );
						
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
	/**
	 * 
	 * TODO(获取附近人的消息列表)
	 */
	public void getNearByList(RequestParams params, final OnAsynGetListListener listener  ){
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/Nearby/getNearByList", true, params, new AsyncHttpResponseHandler(){    	
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
						if( root.has("data") && !root.isNull("data") ){
							
							JSONObject data = root.getJSONObject( "data" );
							if( data.has("datas") && !data.isNull("datas") ){
								JSONArray datas = data.getJSONArray("datas");
								PageList pl = new PageList();
								NearMsg ct = null;
								for( int i = 0; i< datas.length(); i++){
									JSONObject jo = datas.getJSONObject( i );
									ct  = new NearMsg( jo );
									try{
										ct.attachment = jo.has("attachment") ? jo.getJSONArray("attachment") : null;
									}catch (Exception e) {
										ct.attachment = null;
									}
									try{
										ct.likeList = jo.has("likeList") ? jo.getJSONArray("likeList") : null;
									}catch (Exception e) {
										ct.likeList = null;
									}
									try{
										ct.feedbackList = jo.has("feedbackList") ? jo.getJSONArray("feedbackList") : null;
									}catch (Exception e) {
										ct.feedbackList = null;
									}
									try{
										ct.publisher = jo.has("publisher") ? jo.getJSONObject("publisher") : null;
									}catch (Exception e) {
										ct.publisher = null;
									}
									
									pl.add( ct );
								}
								
								listener.OnGetListDone( pl );
								
							}else{
								listener.OnAsynRequestFail("-1", GezitechApplication.getContext().getString( R.string.data_error ));
							}
						}else{
							listener.OnAsynRequestFail("-1", GezitechApplication.getContext().getString( R.string.data_error ));
							
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
	//获取用户发布的附近人，包括用户的或者自己的
	public void getUserPubNearBy(RequestParams params, final OnAsynGetListListener listener  ){
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/Nearby/getUserPubNearBy", true, params, new AsyncHttpResponseHandler(){    	
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
						if( root.has("data") && !root.isNull("data") ){
							
							JSONObject data = root.getJSONObject( "data" );
							if( data.has("datas") && !data.isNull("datas") ){
								JSONArray datas = data.getJSONArray("datas");
								PageList pl = new PageList();
								NearMsg ct = null;
								for( int i = 0; i< datas.length(); i++){
									JSONObject jo = datas.getJSONObject( i );
									ct  = new NearMsg( jo );
									try{
										ct.attachment = jo.has("attachment") ? jo.getJSONArray("attachment") : null;
									}catch (Exception e) {
										ct.attachment = null;
									}
									try{
										ct.likeList = jo.has("likeList") ? jo.getJSONArray("likeList") : null;
									}catch (Exception e) {
										ct.likeList = null;
									}
									try{
										ct.feedbackList = jo.has("feedbackList") ? jo.getJSONArray("feedbackList") : null;
									}catch (Exception e) {
										ct.feedbackList = null;
									}
									try{
										ct.publisher = jo.has("publisher") ? jo.getJSONObject("publisher") : null;
									}catch (Exception e) {
										ct.publisher = null;
									}
									
									pl.add( ct );
								}
								
								listener.OnGetListDone( pl );
								
							}else{
								listener.OnAsynRequestFail("-1", GezitechApplication.getContext().getString( R.string.data_error ));
							}
						}else{
							listener.OnAsynRequestFail("-1", GezitechApplication.getContext().getString( R.string.data_error ));
							
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
	/**
	 * 
	 * TODO(添加附近人回复  )
	 */
	public void addNearbyFeedback( RequestParams params, final OnAsynGetJsonObjectListener listener  ){
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/Nearby/addNearbyFeedback", true, params, new AsyncHttpResponseHandler(){    	
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
						JSONObject ja = root.getJSONObject("data");
//						NearFeedBack ct =  new NearFeedBack( ja );
//						try{
//							ct.publisher = ja.has("")?ja.getJSONObject("publisher") : null;
//						}catch (Exception e) {
//							ct.publisher = null;
//						}
//						try{
//							ct.replyer = ja.has("replyer") ? ja.getJSONObject("replyer") : null;
//						}catch (Exception e) {
//							ct.replyer = null;
//						}
						listener.OnGetJSONObjectDone( ja );
						
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
	 * 
	 * TODO(添加附近人喜欢接口  )
	 */
	public void addNearbyLike( RequestParams params, final OnAsynGetJsonObjectListener listener  ){
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/Nearby/addNearbyLike", true, params, new AsyncHttpResponseHandler(){    	
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
						JSONObject ja  = null;
						try{
							ja = root.getJSONObject("data");
						}catch (Exception e) {
							// TODO: handle exception
						}
//						NearFeedBack ct =  new NearFeedBack( ja );
//						try{
//							ct.publisher = ja.has("")?ja.getJSONObject("publisher") : null;
//						}catch (Exception e) {
//							ct.publisher = null;
//						}
//						try{
//							ct.replyer = ja.has("replyer") ? ja.getJSONObject("replyer") : null;
//						}catch (Exception e) {
//							ct.replyer = null;
//						}
						listener.OnGetJSONObjectDone( ja );
						
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
	 * 
	 * TODO( 删除附近人的接口  )
	 */
	public void delNearBy( RequestParams params, final OnAsynGetJsonObjectListener listener  ){
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/Nearby/delNearBy", true, params, new AsyncHttpResponseHandler(){    	
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
						listener.OnGetJSONObjectDone( root );
						
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
	//删除回复 
	public void delFeedBack( RequestParams params, final OnAsynGetJsonObjectListener listener ){
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/Nearby/delFeedBack", true, params, new AsyncHttpResponseHandler(){    	
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
						listener.OnGetJSONObjectDone( root );
						
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
	 * 
	 * TODO( 获取附近人最新数量信息 )
	 */
	public void getNewNearByCount( RequestParams params, final OnAsynGetJsonObjectListener listener  ){
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.post("api/Nearby/getNewNearByCount", true, params, new AsyncHttpResponseHandler(){    	
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
						listener.OnGetJSONObjectDone( root );
						
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
