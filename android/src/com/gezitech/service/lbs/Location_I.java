package com.gezitech.service.lbs;

public interface Location_I {
	SItude getItude() throws Exception;
	
	public interface ItudeCallBack{
		void OnItudeGetDone(SItude sItude);
		void OnItudeGetFail(String msg);
	}
}
