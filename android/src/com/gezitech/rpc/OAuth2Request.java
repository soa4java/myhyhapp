package com.gezitech.rpc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.gezitech.basic.GezitechException;
import com.gezitech.config.Configuration;
import com.gezitech.http.HttpClient;
import com.gezitech.http.OAuth2HttpClient;
import com.gezitech.http.PostParameter;
import com.gezitech.http.Response;
import com.gezitech.service.GezitechService;
import com.gezitech.util.PagingUtil;

/*******************************************************
 * ��������������󹫹���
 * @author qtby-heyao
 *
 */
public class OAuth2Request {
	
	protected String baseURL = Configuration.getScheme() + Configuration.getAppPath();
	protected HttpClient http = new HttpClient();
	public OAuth2Request(){}

	/************************************************************************************
	 * get ��ʽ����
	 * @param url ��������·��
	 * @param params �����ֵ��
	 * @param authenticate �Ƿ���Ҫ��Ȩ
	 * @return
	 * @throws GezitechException
	 */
	public  Response get(String url, PostParameter[] params, boolean authenticate) throws GezitechException {
    	if (null != params && params.length > 0) {
    		if(url.indexOf("?")==-1)
    			url += "?" + OAuth2HttpClient.encodeUrlParameters(params);
    		else
    			url += "&" + OAuth2HttpClient.encodeUrlParameters(params);
		}
        return http.get(buildUrl(url), authenticate);
    }
	
	//���ģʽ����
	public Response multiRequest(String fileParamName,String url,  PostParameter[] params,File file,boolean authenticated) throws GezitechException{
		return this.http.multPartURL(fileParamName, buildUrl(url), params, file, authenticated);
	}	
	
	public Response post(String url,PostParameter [] params,boolean authenticate)  throws GezitechException{	
		 return http.post(buildUrl(url), params, authenticate);
	}
	
	protected String buildUrl(String url){	
			/*if(BeelnnService.nowUser != null){
		    	url += "/oauth_token/"+ BeelnnService.nowUser.getOauthToken() +"/oauth_token_secret/"+ BeelnnService.nowUser.getOauthTokenSecrect() +"";
		    }
		    url += "/from/2";*/
		if(url.indexOf("http")==0){
			return url;
		}else{
			return getBaseURL() + url;
		}
	}
	 
	protected String getBaseURL() {
		return baseURL;
	}

	protected void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	protected HttpClient getHttp() {
		return http;
	}

	protected void setHttp(HttpClient http) {
		this.http = http;
	} 
}

