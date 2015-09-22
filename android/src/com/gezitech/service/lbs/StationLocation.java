package com.gezitech.service.lbs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

public class StationLocation implements Location_I ,Runnable{
	
	/** Network type is unknown */
	public static final int NETWORK_TYPE_UNKNOWN = 0;
	/** Current network is GPRS */
	public static final int NETWORK_TYPE_GPRS = 1;
	/** Current network is EDGE */
	public static final int NETWORK_TYPE_EDGE = 2;
	/** Current network is UMTS */
	public static final int NETWORK_TYPE_UMTS = 3;
	/** Current network is CDMA： Either IS95A or IS95B*/
	public static final int NETWORK_TYPE_CDMA = 4;
	/** Current network is EVDO revision 0*/
	public static final int NETWORK_TYPE_EVDO_0 = 5;
	/** Current network is EVDO revision A*/
	public static final int NETWORK_TYPE_EVDO_A = 6;
	/** Current network is 1xRTT*/
	public static final int NETWORK_TYPE_1xRTT = 7;
	/** Current network is HSDPA */
	public static final int NETWORK_TYPE_HSDPA = 8;
	/** Current network is HSUPA */
	public static final int NETWORK_TYPE_HSUPA = 9;
	/** Current network is HSPA */
	public static final int NETWORK_TYPE_HSPA = 10;
	Context context = null;
	private ItudeCallBack itudeCallBack = null;
	public StationLocation(Context context, ItudeCallBack itudeCallBack ){
		this.context = context;
		this. itudeCallBack  =  itudeCallBack ;
	}
	@Override
	public SItude getItude() throws Exception  {
		SItude itude = null;
		try{
			
		TelephonyManager tm = (TelephonyManager)this.context.getSystemService(Context.TELEPHONY_SERVICE);
		int type = tm.getNetworkType();
		//中国电信为CTCNETWORK_TYPE_EVDO_A是中国电信3G的getNetworkType NETWORK_TYPE_CDMA电信2G是CDMA
		if (type == TelephonyManager.NETWORK_TYPE_EVDO_A || type == TelephonyManager.NETWORK_TYPE_CDMA || type ==TelephonyManager.NETWORK_TYPE_1xRTT)
		{
			itude=  this.getCdmaItude(tm);
			
		}else if(type == TelephonyManager.NETWORK_TYPE_EDGE){
			//移动2G卡 + CMCC + 2
			itude= this.getGmsItude(tm);
		}
		else if(type == TelephonyManager.NETWORK_TYPE_GPRS){
			//联通的2G经过测试 China Unicom 1 NETWORK_TYPE_GPRS
			itude= this.getGmsItude(tm);
		}
		}catch(Exception e){	
			throw e;
		}
		return  itude;
	}

	private SItude getCdmaItude(TelephonyManager mTelNet) throws Exception{	
		CdmaCellLocation	location = (CdmaCellLocation) mTelNet.getCellLocation();
        	if(location == null)
        		if(this.itudeCallBack != null) this.itudeCallBack.OnItudeGetFail("获取CDMA基站信息失败");
	        int sid = location.getSystemId();//系统标识  mobileNetworkCode
	        int bid = location.getBaseStationId();//基站小区号  cellId
	        int nid = location.getNetworkId();//网络标识  locationAreaCode
	        
	        Log.i("sid:", "" + sid);
	        Log.i("bid:", "" + bid);
	        Log.i("nid:", "" + nid);
	        ArrayList<CellIDInfo> CellID = new ArrayList<CellIDInfo>();
	        CellIDInfo info = new CellIDInfo();
	        info.cellId = bid;
	        info.locationAreaCode = nid;
	        info.mobileNetworkCode = String.valueOf(sid);
	        info.mobileCountryCode = mTelNet.getSimOperator().substring(0, 3);
	     //  info.mobileCountryCode = tm.getSimOperator().substring(3, 5);
	        info.radioType = "cdma";
	        CellID.add(info);
	        Log.d("cellId:", "" + info.cellId);
	        Log.d("locationAreaCode:", "" + info.locationAreaCode);
	        Log.d("mobileNetworkCode:", info.mobileNetworkCode);
	        Log.d("mobileCountryCode:", info.mobileCountryCode);
	        Location loc = callGear(CellID);
	        SItude itude = new SItude();
	        itude.latitude =String.valueOf( loc.getLatitude());
	        itude.longitude = String.valueOf( loc.getLongitude());
	        return itude;
	}
	

	private SItude getGmsItude(TelephonyManager mTelNet) throws Exception{	
		SCell cell = new SCell();
		/** 调用API获取基站信息 */
		GsmCellLocation location = (GsmCellLocation) mTelNet.getCellLocation();
		if (location == null)
			throw new Exception("获取基站信息失败");

		String operator = mTelNet.getNetworkOperator();
		int mcc = Integer.parseInt(operator.substring(0, 3));
		int mnc = Integer.parseInt(operator.substring(3));
		int cid = location.getCid();
		int lac = location.getLac();

		/** 将获得的数据放到结构体中 */
		cell.MCC = mcc;
		cell.MNC = mnc;
		cell.LAC = lac;
		cell.CID = cid;
	    SItude itude = new SItude();

		/** 采用Android默认的HttpClient */
		HttpClient client = new DefaultHttpClient();
		/** 采用POST方法 */
		HttpPost post = new HttpPost("http://www.google.com/loc/json");
		try {
			/** 构造POST的JSON数据 */
			JSONObject holder = new JSONObject();
			holder.put("version", "1.1.0");
			holder.put("host", "maps.google.com");
			holder.put("address_language", "zh_CN");
			holder.put("request_address", true);
			holder.put("radio_type", "gsm");
			holder.put("carrier", "HTC");

			JSONObject tower = new JSONObject();
			tower.put("mobile_country_code", cell.MCC);
			tower.put("mobile_network_code", cell.MNC);
			tower.put("cell_id", cell.CID);
			tower.put("location_area_code", cell.LAC);

			JSONArray towerarray = new JSONArray();
			towerarray.put(tower);
			holder.put("cell_towers", towerarray);
			StringEntity query = new StringEntity(holder.toString());
			post.setEntity(query);
			/** 发出POST数据并获取返回数据 */
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(entity.getContent()));
			StringBuffer strBuff = new StringBuffer();
			String result = null;
			while ((result = buffReader.readLine()) != null) {
				strBuff.append(result);
			}
			/** 解析返回的JSON数据获得经纬度 */
			JSONObject json = new JSONObject(strBuff.toString());
			JSONObject subjosn = new JSONObject(json.getString("location"));
			itude.latitude = subjosn.getString("latitude");
			itude.longitude = subjosn.getString("longitude");		
		} catch (Exception e) {
			Log.e(e.getMessage(), e.toString());
			throw new Exception("获取经纬度出现错误:"+e.getMessage());
		} finally{
			post.abort();
			client = null;
		}
    	return itude;  
	}
	

	/** 基站信息结构体 */
    public class SCell{
        public int MCC;
        public int MNC;
        public int LAC;
        public int CID;
    }

    
    public class CellIDInfo {
    	public int cellId;
    	public String mobileCountryCode;
    	public String mobileNetworkCode;
    	public int locationAreaCode;
    	public String radioType;
    	
    	public CellIDInfo(){}
    }
    
  //调用google gears的方法，该方法调用gears来获取经纬度
    private Location callGear(ArrayList<CellIDInfo> cellID) {
    	if (cellID == null) 
    		return null;
    	
    	DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(
				"http://www.google.com/loc/json");
		JSONObject holder = new JSONObject();

		try {
			holder.put("version", "1.1.0");
			holder.put("host", "maps.google.com");
			holder.put("home_mobile_country_code", cellID.get(0).mobileCountryCode);
			holder.put("home_mobile_network_code", cellID.get(0).mobileNetworkCode);
			holder.put("radio_type", cellID.get(0).radioType);
			holder.put("request_address", true);
			if ("460".equals(cellID.get(0).mobileCountryCode)) 
				holder.put("address_language", "zh_CN");
			else
				holder.put("address_language", "en_US");
			
			JSONObject data,current_data;

			JSONArray array = new JSONArray();
			
			current_data = new JSONObject();
			current_data.put("cell_id", cellID.get(0).cellId);
			current_data.put("location_area_code", cellID.get(0).locationAreaCode);
			current_data.put("mobile_country_code", cellID.get(0).mobileCountryCode);
			current_data.put("mobile_network_code", cellID.get(0).mobileNetworkCode);
			current_data.put("age", 0);
			current_data.put("signal_strength", -60);
			current_data.put("timing_advance", 5555);
			array.put(current_data);
			
			holder.put("cell_towers", array);
						
			StringEntity se = new StringEntity(holder.toString());
			Log.e("Location send", holder.toString());
			post.setEntity(se);
			HttpResponse resp = client.execute(post);

			HttpEntity entity = resp.getEntity();

			BufferedReader br = new BufferedReader(
					new InputStreamReader(entity.getContent()));
			StringBuffer sb = new StringBuffer();
			String result = br.readLine();
			while (result != null) {
				Log.e("Locaiton reseive", result);
				sb.append(result);
				result = br.readLine();
			}

			data = new JSONObject(sb.toString());
			Log.d("-", sb.toString());
			data = (JSONObject) data.get("location");

			Location loc = new Location(LocationManager.NETWORK_PROVIDER);
			loc.setLatitude((Double) data.get("latitude"));
			loc.setLongitude((Double) data.get("longitude"));
			loc.setAccuracy(Float.parseFloat(data.get("accuracy").toString()));
			loc.setTime( System.currentTimeMillis());//AppUtil.getUTCTime());
			return loc;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("-", "null 1");
		return null;
	}

    
    
    
    
    
    
    
	@Override
	public void run() {
		// TODO Auto-generated method stub
		SItude itude;
		try {
			itude = this.getItude();
			Message msg = handler.obtainMessage();
			msg.what = 0;
			msg.obj = itude;
			handler.sendMessage(msg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){	
				case 0:
					SItude sItude = (SItude)msg.obj;
					if(itudeCallBack != null)
						itudeCallBack.OnItudeGetDone(sItude);
				break;
				case 1:
					if(itudeCallBack != null)
						itudeCallBack.OnItudeGetFail("基站定位失败");
				break;
			}
			
		}
	};
}
