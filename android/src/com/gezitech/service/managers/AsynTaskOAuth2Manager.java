package com.gezitech.service.managers;

import java.io.File;
import java.util.ArrayList;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.gezitech.basic.GezitechException;
import com.gezitech.config.Configuration;
import com.gezitech.entity.User;
import com.gezitech.http.PostParameter;
import com.gezitech.http.Response;
import com.gezitech.rpc.OAuth2Request;
import com.gezitech.service.GezitechService;
/*******************************************************************
 * 异步任务管理对象，，通过线程队列实现对异步请求的管理
 * @author qtby-heyao
 ******************************************************************/
public class AsynTaskOAuth2Manager {
	//排队中
	private ArrayList<Runnable> queue = new ArrayList<Runnable>();
	//运行中
	private ArrayList<Runnable> active = new ArrayList<Runnable>();
	private AsynTaskOAuth2Manager(){}
	static AsynTaskOAuth2Manager instance = null;
	
	public static AsynTaskOAuth2Manager getInstance(){
		if(instance == null)
			instance = new AsynTaskOAuth2Manager();
		return instance;
	}
	
	private void push(Runnable runnable){
		queue.add(runnable);
		startNext();
	}
	
	private void startNext(){	
		if(!queue.isEmpty()){
			Runnable runnable = (Runnable)queue.get(0);
			queue.remove(0);
			active.add(runnable);
			new Thread(runnable).start();
		}
	}
	
	private void finish(Runnable runnable){	
		active.remove(runnable);
		startNext();
	}

	public void doGet(String url,PostParameter [] params,boolean authenticate,OnAsynCallBackListener onAsynCallBackListener){
		if(authenticate)params = addAuthenticate(params);
		push(new AsynTask(Method.GET,url,params,false,onAsynCallBackListener));
	}
	
	public void doPost(String url,PostParameter [] params,boolean authenticate,OnAsynCallBackListener onAsynCallBackListener){
		if(authenticate)params = addAuthenticate(params);
		push(new AsynTask(Method.POST,url,params,false,onAsynCallBackListener));
	}
	public void doMultiRequest(String url,PostParameter [] params,boolean authenticate,OnAsynCallBackListener onAsynCallBackListener,String fileName,File file){
		if(authenticate)params = addAuthenticate(params);
		push(new AsynTask(Method.MULTIREQUEST,url,params,false,onAsynCallBackListener,fileName,file));
	}
	private PostParameter[] addAuthenticate(PostParameter[] params){
		ArrayList<PostParameter> list = new ArrayList<PostParameter>();
		for(PostParameter p : params)list.add(p);
		list.add(new PostParameter("client_id", Configuration.getProperty("gezitech.oauth2.clientId") ));
		list.add(new PostParameter("client_secret", Configuration.getProperty("gezitech.oauth2.clientSecret") ));
		
		//需要加入用户的oauth_token
		User me = GezitechService.getInstance().getCurrentUser();
		if(me != null)list.add(new PostParameter("oauth_token", me.access_token ) );
		return (PostParameter[])list.toArray(new PostParameter[list.size()]);	
	}
	
	/************************************************************************************
	 * 异步任务处理对象。。。将所有请求封装在该对象内，并由它调用协议层处理请求
	 * @author qtby-heyao
	 **************************************************************************************/
	class AsynTask implements Runnable{
		protected Method method = null;
		protected String url = null;
		protected PostParameter [] params = null;
		protected boolean authenticate = false;
		protected OAuth2Request beelnnRequest;
		protected OnAsynCallBackListener onAsynCallBackListener;
		private AsynTask IThis = this;
		protected String fileName = null;
		protected File file = null;
		public AsynTask(Method method,String url,PostParameter [] params,boolean authenticate,OnAsynCallBackListener onAsynCallBackListener){	
			this(method,url,params,authenticate,onAsynCallBackListener,null , null);
		}
		public AsynTask(Method method,String url,PostParameter [] params,boolean authenticate,OnAsynCallBackListener onAsynCallBackListener,String fileName,File file){	
			beelnnRequest = new OAuth2Request();
			this.method = method;
			this.url = url;
			this.params = params;
			this.authenticate = authenticate;
			this.onAsynCallBackListener = onAsynCallBackListener;
			this.fileName = fileName;
			this.file = file;
		}
		
		Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				Response res = null;
				if(msg != null){	
					res = (Response)msg.obj;
				}
				if(res != null){
					if(res.getStatusCode() == 200){
						if(onAsynCallBackListener != null){	
							onAsynCallBackListener.OnAsynCallBack(res,true,null);
						}
					}else{	
						try {
							onAsynCallBackListener.OnAsynCallBack(res,false,res.asString());
						} catch (GezitechException e) {
							onAsynCallBackListener.OnAsynCallBack(res,false,e.getMessage());
						}
					}
					res.disconnect();//这里断开连接，以防连接数过多，若委托中使用了多线程，请注意在这里先disconnect再才取数据
				}
				finish(IThis);//结束线程
			}	
		};
		
		public void run() {
			// TODO Auto-generated method stub
			try {
				Response res = null;
				if(this.method.equals(Method.GET))
					res = beelnnRequest.get(url, params, authenticate);
				else if(this.method.equals(Method.POST))
					res = beelnnRequest.post(url, params, authenticate);
				else
					res = beelnnRequest.multiRequest(fileName, url, params, file, authenticate);
				Message msg = new Message();
				msg.obj = res;
				handler.sendMessage(msg);
				
			} catch (GezitechException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public enum Method{	
		GET,POST,MULTIREQUEST
	}
	
	public interface OnAsynCallBackListener{	
		void OnAsynCallBack(Response response,boolean succeed,String errorMsg);
	}
	
}
