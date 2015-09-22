package com.gezitech.http;

import org.apache.http.Header;

import android.util.Log;

import com.gezitech.config.Configuration;
import com.gezitech.entity.User;
import com.gezitech.service.GezitechService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpUtil {
	public static AsyncHttpClient client = new AsyncHttpClient(); // 实例话对象
	static {
		client.setTimeout(Configuration.getConnectionTimeout()); // 设置链接超时，如果不设置，默认为10s
	}

	/**
	 * get方式的请求
	 * 
	 * @param urlString
	 * @param auth
	 *            是否进行oauth认证
	 * @param res
	 */
	public static void get(String urlString, boolean auth,
			AsyncHttpResponseHandler res) // 用一个完整url获取一个string对象
	{
		// 需要验证
		if (auth) {
			RequestParams params = new RequestParams();
			params.put("client_id",
					Configuration.getProperty("gezitech.oauth2.clientId"));
			params.put("client_secret",
					Configuration.getProperty("gezitech.oauth2.clientSecret"));

			// 获取用户的oauth_token
			User user = GezitechService.getInstance().getCurrentUser();
			;
			if (user != null)
				params.put("oauth_token", user.access_token);

			client.get(getAbsoluteUrl(urlString), params, res);
			// 不需要验证
		} else {

			client.get(getAbsoluteUrl(urlString), res);
		}

	}

	public static void get(String urlString, boolean auth,
			RequestParams params, AsyncHttpResponseHandler res) // url里面带参数
	{
		// 需要验证
		if (auth) {
			params.put("client_id",
					Configuration.getProperty("gezitech.oauth2.clientId"));
			params.put("client_secret",
					Configuration.getProperty("gezitech.oauth2.clientSecret"));
			// 获取用户的oauth_token
			User user = GezitechService.getInstance().getCurrentUser();
			;
			if (user != null)
				params.put("oauth_token", user.access_token);
		}
		
		client.get(getAbsoluteUrl(urlString), params, res);
	}

	public static void get(String urlString, boolean auth,
			JsonHttpResponseHandler res) // 不带参数，获取json对象或者数组
	{
		// 需要验证
		if (auth) {
			RequestParams params = new RequestParams();
			params.put("client_id",
					Configuration.getProperty("gezitech.oauth2.clientId"));
			params.put("client_secret",
					Configuration.getProperty("gezitech.oauth2.clientSecret"));

			// 获取用户的oauth_token
			User user = GezitechService.getInstance().getCurrentUser();
			;
			if (user != null)
				params.put("oauth_token", user.access_token);

			client.get(getAbsoluteUrl(urlString), params, res);
			// 不需要验证
		} else {

			client.get(getAbsoluteUrl(urlString), res);
		}
	}

	public static void get(String urlString, boolean auth,
			RequestParams params, JsonHttpResponseHandler res) // 带参数，获取json对象或者数组
	{
		// 需要验证
		if (auth) {
			params.put("client_id",
					Configuration.getProperty("gezitech.oauth2.clientId"));
			params.put("client_secret",
					Configuration.getProperty("gezitech.oauth2.clientSecret"));
			// 获取用户的oauth_token
			User user = GezitechService.getInstance().getCurrentUser();
			if (user != null)
				params.put("oauth_token", user.access_token);
		}
		client.get(getAbsoluteUrl(urlString), params, res);
	}

	public static void get(String urlString, BinaryHttpResponseHandler bHandler) // 下载数据使用，会返回byte数据
	{
		client.get(getAbsoluteUrl(urlString), bHandler);
	}
	//传递的全路径 没有组装路径
	public static void getFullUrl(String urlString, BinaryHttpResponseHandler bHandler )  //下载数据使用，会返回byte数据
    { 
        client.get( urlString, bHandler);
    }
	/**
	 * post 请求
	 * 
	 * @param urlString
	 * @param res
	 * 
	 */
	public static void post(String urlString, boolean auth,
			AsyncHttpResponseHandler res) {
		// 需要验证
		if (auth) {
			RequestParams params = new RequestParams();
			params.put("client_id",
					Configuration.getProperty("gezitech.oauth2.clientId"));
			params.put("client_secret",
					Configuration.getProperty("gezitech.oauth2.clientSecret"));

			// 获取用户的oauth_token
			User user = GezitechService.getInstance().getCurrentUser();
			;
			if (user != null)
				params.put("oauth_token", user.access_token);

			client.post(getAbsoluteUrl(urlString), params, res);
			// 不需要验证
		} else {
			
			client.post(getAbsoluteUrl(urlString), res);
		}
	}

	/**
	 * post有参数传递
	 * 
	 * @param urlString
	 * @param auth
	 * @param params
	 * @param res
	 */
	public static void post(String urlString, boolean auth,
			RequestParams params, final AsyncHttpResponseHandler res) {
		// 需要验证
		if (auth) {
			params.put("client_id",
					Configuration.getProperty("gezitech.oauth2.clientId"));
			params.put("client_secret",
					Configuration.getProperty("gezitech.oauth2.clientSecret"));

			// 获取用户的oauth_token
			User user = GezitechService.getInstance().getCurrentUser();
			if (user != null)
				params.put("oauth_token", user.access_token);
		}
		client.post(getAbsoluteUrl(urlString), params, res );
	}

	/**
	 * 为url加上网络请求的觉得地址
	 * 
	 * @param urlString
	 *            url地址
	 * @return
	 */
	public static String getAbsoluteUrl(String urlString) {
		return Configuration.getScheme() + Configuration.getAppPath()
				+ urlString;
	}

	public static AsyncHttpClient getClient() {
		return client;
	}
}
