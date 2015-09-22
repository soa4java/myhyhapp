package com.gezitech.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.gezitech.basic.GezitechApplication;


import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;

public class NetUtil {
	/***********************************************************
     * 获取网络是否链接
     * @param activity 
     * @return 
     */  
    public static boolean isNetworkAvailable(Context context) {  
    	try {  
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
            if (connectivity != null) {  
              
                NetworkInfo info = connectivity.getActiveNetworkInfo();  
                if (info != null && info.isConnected()) {  
                  
                    if (info.getState() == NetworkInfo.State.CONNECTED) {  
                        return true;  
                    }  
                }  
            }  
        } catch (Exception e) {  
        	return false;  
        }  
        return false;   
    }  
    public static boolean isNetworkAvailable( ) {  
    	try {  
            ConnectivityManager connectivity = (ConnectivityManager) GezitechApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);  
            if (connectivity != null) {  
              
                NetworkInfo info = connectivity.getActiveNetworkInfo();  
                if (info != null && info.isConnected()) {  
                  
                    if (info.getState() == NetworkInfo.State.CONNECTED) {  
                        return true;  
                    }  
                }  
            }  
        } catch (Exception e) {  
        	e.printStackTrace();
        	return false;  
        }  
        return false;   
    }  
  
    /** 
     * Gps�Ƿ�� 
     *  
     * @param context 
     * @return 
     */  
    public static boolean isGpsEnabled(Context context) {  
        LocationManager locationManager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
        if( locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER ) ){
        	return true;
        }else{
        	return false;
        }
    }  
  
    /** 
     * wifi�Ƿ�� 
     */  
    public static boolean isWifiEnabled(Context context) {  
        ConnectivityManager mgrConn = (ConnectivityManager) context  
                .getSystemService(Context.CONNECTIVITY_SERVICE);  
        TelephonyManager mgrTel = (TelephonyManager) context  
                .getSystemService(Context.TELEPHONY_SERVICE);  
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn  
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel  
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);  
    }  
  
    /** 
     * �жϵ�ǰ�����Ƿ���wifi���� 
     * if(activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) { //�ж�3G�� 
     *  
     * @param context 
     * @return boolean 
     */  
    public static boolean isWifi(Context context) {  
        ConnectivityManager connectivityManager = (ConnectivityManager) context  
                .getSystemService(Context.CONNECTIVITY_SERVICE);  
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();  
        if (activeNetInfo != null  
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {  
            return true;  
        }  
        return false;  
    }  
  
    /** 
     * �жϵ�ǰ�����Ƿ���3G���� 
     *  
     * @param context 
     * @return boolean 
     */  
    public static boolean is3G(Context context) {  
        ConnectivityManager connectivityManager = (ConnectivityManager) context  
                .getSystemService(Context.CONNECTIVITY_SERVICE);  
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();  
        if (activeNetInfo != null  
                && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {  
            return true;  
        }  
        return false;  
    }
	
	
	
	public static BitmapDrawable getImageFromUrl(URL url) {

		BitmapDrawable icon = null;

		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			icon = new BitmapDrawable(conn.getInputStream());
			conn.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return icon;
	}
	
	
	
	
	
	
	/**
	 * ��ȡ��ַ����
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String getContent(String url) throws Exception{
        StringBuilder sb = new StringBuilder();
        
        HttpClient client = new DefaultHttpClient();
        HttpParams httpParams = client.getParams();
        //�������糬ʱ����
        HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
        HttpConnectionParams.setSoTimeout(httpParams, 5000);
        HttpResponse response = client.execute(new HttpGet(url));
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"), 8192);
            
            String line = null;
            while ((line = reader.readLine())!= null){
                sb.append(line + "\n");
            }
            reader.close();
        }
        return sb.toString();
    }
	
	
	public static String formatDate(Long data,String format){	
		return formatDate(new Date(data),format);
	}
	
	
	public static String formatDate(Date date,String format){	
		 if(date == null) return "";
		 SimpleDateFormat sdf = new SimpleDateFormat(format);
		 return sdf.format(date);
	}
	
	public static Date formatDate(String dateString,String format){		
		SimpleDateFormat formater= new SimpleDateFormat(format); 
		Date date = null;
		try{	
			try {
				date = formater.parse(dateString);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}catch(ParseException e){	
			e.printStackTrace();
		}
		return date;
	}
	
	public static String getTimeDiff(Date date,String format){	
		return formatDate(date,format);
	}
	
	
	public static String getTimeDiff(Date date) {
		return formatDate(date,"yyyy-MM-dd HH:mm");
//		Calendar cal = Calendar.getInstance();
//		long diff = 0;
//		Date dnow = cal.getTime();
//		String str = "";
//		diff = dnow.getTime() - date.getTime();
//		
//		System.out.println("diff---->"+date);
//
//		if (diff > 24 * 60 * 60 * 1000) {
//			//System.out.println("1��ǰ");
//			str="1��ǰ";
//		} else if (diff > 5 * 60 * 60 * 1000) {
//			//System.out.println("2Сʱǰ");
//			str="2Сʱǰ";
//		} else if (diff > 1 * 60 * 60 * 1000) {
//			//System.out.println("1Сʱǰ");
//			str="Сʱǰ";
//		} else if (diff > 30 * 60 * 1000) {
//			//System.out.println("30����ǰ");
//			str="30����ǰ";
//		} else if (diff > 15 * 60 * 1000) {
//			//System.out.println("15����ǰ");
//			str="15����ǰ";
//		} else if (diff > 5 * 60 * 1000) {
//			//System.out.println("5����ǰ");
//			str="5����ǰ";
//		} else if (diff > 1 * 60 * 1000) {
//			//System.out.println("1����ǰ");
//			str="1����ǰ";
//		}else{
//			str="�ո�";
//		}
		

		//return date.getTime();
	}

	/**
	 * ����ֻ�ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	 
	/**
	 * ����ֻ�ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
}

