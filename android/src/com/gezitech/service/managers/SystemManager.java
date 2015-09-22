package com.gezitech.service.managers;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.provider.Settings.System;
import android.util.Log;

import com.gezitech.basic.GezitechApplication;
import com.gezitech.basic.GezitechException;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynInsertListener;
import com.gezitech.contract.GezitechManager_I.OnAsynProgressListener;
import com.gezitech.contract.GezitechManager_I.OnAsynRequestListener;
import com.gezitech.contract.GezitechManager_I.OnAsynUpdateListener;
import com.gezitech.entity.PageList;
import com.gezitech.http.HttpUtil;
import com.gezitech.http.Response;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.gezitech.util.NetUtil;
import com.hyh.www.R;
import com.hyh.www.entity.Adv;
import com.hyh.www.entity.City;
import com.hyh.www.entity.Companytype;
import com.hyh.www.entity.Configuration;
import com.hyh.www.entity.Country;
import com.hyh.www.entity.News;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

//系统资料的获取管理器
public class SystemManager {
	private SystemManager _this = this;
	static SystemManager instance = null;

	public static SystemManager getInstance() {
		if (instance == null) {
			instance = new SystemManager();
		}
		return instance;
	}

	// 获取企业的类型
	public void companytypelist(final OnAsynGetListListener listener) {
		RequestParams params = new RequestParams();
		params.put("pageSize", 10000);
		params.put("thisPage", 1);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/system/companytypelist", true, params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							if (arg0 != 200) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));
							}
							try {
								Response response = new Response(new String(
										arg2));
								JSONObject root = response.asJSONObject();
								
								if (root.getInt("state")!= 1) {// 0
									if (listener != null)
										listener.OnAsynRequestFail("-1",
												root.getString("msg"));
									return;
								}
								JSONObject data = root.getJSONObject("data");
								JSONArray ja = data.getJSONArray("datas");
								PageList pl = new PageList();
								Companytype ct = null;
								for (int i = 0; i < ja.length(); i++) {
									JSONObject jo = ja.getJSONObject(i);
									ct = new Companytype(jo);
									ct.childtype = jo.has("childtype") ? jo.getJSONArray("childtype") : null;
									pl.add(ct);
								}

								listener.OnGetListDone(pl);

							} catch (Exception ex) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.data_error));
						}

						@Override
						public void onFinish() { // 完成后调用，失败，成功，都要掉

						}
					});
		} else {
			if (listener != null) {
				listener.OnAsynRequestFail("-1", GezitechApplication
						.getContext().getString(R.string.network_error));
			}
		}
	}

	// 提交意见反馈
	public void addFeedback(RequestParams params,
			final OnAsynUpdateListener listener) {
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/feedback/addFeedback", true, params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {

							try {
								Response res = new Response(new String(arg2));
								JSONObject jsonObj = res.asJSONObject();

								int state = jsonObj.getInt("state");
								String msg = jsonObj.getString("msg");
								if (state != 1) {
									listener.OnAsynRequestFail("-1", msg);
									return;
								}
								listener.onUpdateDone(GezitechApplication
										.getContext().getString(
												R.string.feekback_success));
							} catch (GezitechException e) {
								listener.OnAsynRequestFail(
										"-1",
										GezitechApplication.getContext()
												.getString(
														R.string.feekback_fail));
							} catch (JSONException e) {
								listener.OnAsynRequestFail(
										"-1",
										GezitechApplication.getContext()
												.getString(
														R.string.feekback_fail));
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.feekback_fail));
						}

						@Override
						public void onFinish() {
							super.onFinish();
						}
					});
		} else {
			if (listener != null) {
				listener.OnAsynRequestFail("-1", GezitechApplication
						.getContext().getString(R.string.network_error));
			}
		}
	}
	
	// 联系我们
	public void configuration(final OnAsynGetListListener listener) {
		RequestParams params = new RequestParams();
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/system/configuration", true, params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							if (arg0 != 200) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));
							}
							try {
								Response response = new Response(new String(
										arg2));
								JSONObject root = response.asJSONObject();
								if (root.getInt("state")!= 1) {// 0
									if (listener != null)
										listener.OnAsynRequestFail("-1",
												root.getString("msg"));
									return;
								}
								JSONArray data = root.getJSONArray("data");
								PageList pl = new PageList();
								Configuration cg = null;
								GezitechDBHelper<Configuration> db = new GezitechDBHelper<Configuration>(Configuration.class);
								for (int i = 0; i < data.length(); i++) {
									JSONObject jo = data.getJSONObject(i);
									cg = new Configuration(jo);
									pl.add(cg);
									db.update(cg);
								}

								listener.OnGetListDone(pl);

							} catch (Exception ex) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.data_error));
						}

						@Override
						public void onFinish() { // 完成后调用，失败，成功，都要掉

						}
					});
		} else {
			if (listener != null) {
				listener.OnAsynRequestFail("-1", GezitechApplication
						.getContext().getString(R.string.network_error));
			}
		}
	}
	//获取客户端缓存的配置信息
	public Configuration getConfiguration(int system_id){
		GezitechDBHelper<Configuration> db = new GezitechDBHelper<Configuration>(Configuration.class);
		
		ArrayList<Configuration> list = db.query("system_id="+system_id, 1, "");
		if( list == null  || list.size() <= 0 ){
			return null;
		}else{
			return list.get(0);
		}
		
	}

	// 获取公告（新闻）列表
	public void getannouncementlist(int page, int pageSize,
			final OnAsynGetListListener listener) {
		RequestParams params = new RequestParams();
		params.put("page", page);
		params.put("pageSize", pageSize);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/News/getannouncementlist", true, params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							if (arg0 != 200) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));
							}
							try {
								Response response = new Response(new String(
										arg2));
								JSONObject root = response.asJSONObject();
								if (root.getInt("state") != 1) {// 0
									if (listener != null)
										listener.OnAsynRequestFail("-1",
												root.getString("msg"));
									return;
								}
								JSONObject data = root.getJSONObject("data");
								PageList pl = new PageList();
								News news = null;
								if (data.has("datas") && !data.isNull("datas")) {
									JSONArray datas = data
											.getJSONArray("datas");
									for (int i = 0; i < datas.length(); i++) {
										JSONObject jo = datas.getJSONObject(i);
										news = new News();
										news.id = jo.has("id") ? jo.getLong("id") : 0;
										news.ctime = jo.has("ctime") ? jo.getLong("ctime") : 0;
										news.title = jo.has("title") ? jo.getString("title") : "";
										news.content = jo.has("content") ? jo.getString("content") : "";
										
										pl.add(news);
									}
								}
								listener.OnGetListDone(pl);

							} catch (Exception ex) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.data_error));
						}

						@Override
						public void onFinish() { // 完成后调用，失败，成功，都要掉

						}
					});
		} else {
			if (listener != null) {
				listener.OnAsynRequestFail("-1", GezitechApplication
						.getContext().getString(R.string.network_error));
			}
		}
	}

	// 获取公告（新闻）详情
	public void getannouncementdetails(long id,
			final OnAsynGetOneListener listener) {
		RequestParams params = new RequestParams();
		params.put("id", id);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.get("api/News/getannouncementdetails", true, params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							if (arg0 != 200) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));
							}
							try {
								Response response = new Response(new String(
										arg2));
								JSONObject root = response.asJSONObject();
								if (root.getInt("state")!= 1) {// 0
									if (listener != null)
										listener.OnAsynRequestFail("-1",
												root.getString("msg"));
									return;
								}
								JSONObject data = root.getJSONObject("data");

								News news = new News();
								news.id = data.has("id") ? data.getLong("id") : 0;
								news.ctime = data.has("ctime") ? data.getLong("ctime") : 0;
								news.title = data.has("title") ? data.getString("title") : "";
								news.content = data.has("content") ? data.getString("content") : "";

								listener.OnGetOneDone(news);

							} catch (Exception ex) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.data_error));
						}

						@Override
						public void onFinish() { // 完成后调用，失败，成功，都要掉

						}
					});
		} else {
			if (listener != null) {
				listener.OnAsynRequestFail("-1", GezitechApplication
						.getContext().getString(R.string.network_error));
			}
		}
	}

	/*
	 * 单独上传图片 Common/fileclear 参数 oauth_token 不能为空 litpic 图片
	 */

	public void fileclear(RequestParams params,
			final OnAsynInsertListener listener) {

		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/common/fileclear", true, params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							if (arg0 != 200) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));
							}
							try {
								Response response = new Response(new String(
										arg2));
								JSONObject root = response.asJSONObject();
								if (root.getInt("state") != 1) {// 0
									if (listener != null)
										listener.OnAsynRequestFail("-1",
												root.getString("msg"));
									return;
								}
								JSONObject data = root.getJSONObject("data");
								String litpic = data.has("litpic") ? data
										.getString("litpic") : "";
								if (litpic.equals("")) {
									listener.OnAsynRequestFail("-1", "上传失败");
									return;
								}
								listener.onInsertDone(litpic);

							} catch (Exception ex) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.data_error));
						}

						@Override
						public void onFinish() { // 完成后调用，失败，成功，都要掉

						}
					});
		} else {
			if (listener != null) {
				listener.OnAsynRequestFail("-1", GezitechApplication
						.getContext().getString(R.string.network_error));
			}
		}

	}
	//进度条
	public void fileclearProcess(RequestParams params,
			final OnAsynProgressListener listener) {

		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/common/fileclear", true, params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							if (arg0 != 200) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));
							}
							try {
								Response response = new Response(new String(
										arg2));
								JSONObject root = response.asJSONObject();
								if (root.getInt("state") != 1) {// 0
									if (listener != null)
										listener.OnAsynRequestFail("-1",
												root.getString("msg"));
									return;
								}
								JSONObject data = root.getJSONObject("data");
								String litpic = data.has("litpic") ? data
										.getString("litpic") : "";
								if (litpic.equals("")) {
									listener.OnAsynRequestFail("-1", "上传失败");
									return;
								}
								listener.onUpdateDone( litpic );

							} catch (Exception ex) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.data_error));
						}

						@Override
						public void onFinish() { // 完成后调用，失败，成功，都要掉

						}
						@Override
						public void onProgress(int bytesWritten, int totalSize) {
							// TODO Auto-generated method stub
							//super.onProgress(bytesWritten, totalSize);
							listener.OnProgress(bytesWritten, totalSize);
						}
					});
		} else {
			if (listener != null) {
				listener.OnAsynRequestFail("-1", GezitechApplication
						.getContext().getString(R.string.network_error));
			}
		}

	}

	/*
	 * 单独上传amr格式语音文件 Common/phonetic 参数 speech 文件 oauth_token 不能为空
	 */
	public void phonetic(RequestParams params,
			final OnAsynInsertListener listener) {

		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/common/phonetic", true, params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							if (arg0 != 200) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));
							}
							try {
								Response response = new Response(new String(
										arg2));
								JSONObject root = response.asJSONObject();
								if (root.getInt("state") != 1 ) {// 0
									if (listener != null)
										listener.OnAsynRequestFail("-1",
												root.getString("msg"));
									return;
								}
								JSONObject data = root.getJSONObject("data");
								String speech = data.has("speech") ? data
										.getString("speech") : "";
								if (speech.equals("")) {
									listener.OnAsynRequestFail("-1", "上传失败");
									return;
								}
								listener.onInsertDone(speech);

							} catch (Exception ex) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.data_error));
						}

						@Override
						public void onFinish() { // 完成后调用，失败，成功，都要掉

						}
					});
		} else {
			if (listener != null) {
				listener.OnAsynRequestFail("-1", GezitechApplication
						.getContext().getString(R.string.network_error));
			}
		}

	}

	/*
	 * 获取幻灯广告列表（暂停的广告不要返回） 
	 * system/advlist 
	 * 参数 
	 * city 城市 
	 * page 当前页、 
	 * pageSize 每页显示数量
	 */
	public void advlist(int page, int pageSize, String city,
			final OnAsynGetListListener listener) {
		RequestParams params = new RequestParams();
		params.put("city", city );
		params.put("page", page);
		params.put("pageSize", pageSize);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/system/advlist", true, params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							if (arg0 != 200) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));
							}
							try {
								Response response = new Response(new String(
										arg2));
								JSONObject root = response.asJSONObject();
								if (root.getInt("state") != 1 ) {// 0
									if (listener != null)
										listener.OnAsynRequestFail("-1",
												root.getString("msg"));
									return;
								}
								JSONObject data = root.getJSONObject("data");
								PageList pl = new PageList();
								Adv adv = null;
								GezitechDBHelper<Adv> advDB = new GezitechDBHelper<Adv>( Adv.class );
								if (data.has("datas") && !data.isNull("datas")) {
									JSONArray datas = data.getJSONArray("datas");
									
									PageList cachepl = getClientAdvList();
									if(cachepl !=null && cachepl.size()>0 )advDB.delete("");
									
									for (int i = 0; i < datas.length(); i++) {
										JSONObject jo = datas.getJSONObject(i);
										adv = new Adv(jo);
										pl.add(adv);
										advDB.insert( adv );
									}
								}
								listener.OnGetListDone(pl);

							} catch (Exception ex) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.data_error));
						}

						@Override
						public void onFinish() { // 完成后调用，失败，成功，都要掉

						}
					});
		} else {
			if (listener != null) {
				listener.OnAsynRequestFail("-1", GezitechApplication
						.getContext().getString(R.string.network_error));
			}
		}
	}
	//获取客户端的id
	public PageList  getClientAdvList(){
		GezitechDBHelper<Adv> advDB = new GezitechDBHelper<Adv>( Adv.class );
		ArrayList<Adv> advList = advDB.query("",0, "sort desc,id desc");
		PageList pl = new PageList();
		if( advList!=null){
			pl.addAll( advList );
		}
		return pl;
	}
	/**
	 * 获取系统消息的更新

api/News/getNewsUpdate
ctime	历史查看时间， 不传为当前时间
oauth_token	oauth_token
返回数据	
state	成功状态 1成功 0失败
msg	状态描述信息
data	有几条信息 整型

	 * TODO()
	 */
	public void getNewsUpdate(long ctime,final OnAsynInsertListener listener) {
			RequestParams params = new RequestParams();
			//params.put("ctime", ctime );
			if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/News/getNewsUpdate/ctime/"+ctime, true, params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							if (arg0 != 200) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));
							}
							try {
								Response response = new Response(new String(
										arg2));
								JSONObject root = response.asJSONObject();
								if (root.getInt("state") != 1 ) {// 0
									if (listener != null)
										listener.OnAsynRequestFail("-1",
												root.getString("msg"));
									return;
								}
								
								int data =  root.has("data") ? root.getInt( "data" ) : 0;
								
								Log.v("测试返回", data+"========data====");
								listener.onInsertDone( data+""  );

							} catch (Exception ex) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.data_error));
						}

						@Override
						public void onFinish() { // 完成后调用，失败，成功，都要掉

						}
					});
		} else {
			if (listener != null) {
				listener.OnAsynRequestFail("-1", GezitechApplication
						.getContext().getString(R.string.network_error));
			}
		}
	}
		// 获取省市
		public void getCityAreaStreet(long parentId ,long nationalityidVal, final OnAsynGetListListener listener) {
			RequestParams params = new RequestParams();
			params.put("parentId", parentId );
			params.put("nationalityid", nationalityidVal );
			if (NetUtil.isNetworkAvailable()) {
				HttpUtil.post("api/system/getCityAreaStreet", true, params,
						new AsyncHttpResponseHandler() {
							@Override
							public void onSuccess(int arg0, Header[] arg1,
									byte[] arg2) {
								if (arg0 != 200) {
									listener.OnAsynRequestFail("-1",
											GezitechApplication.getContext()
													.getString(R.string.data_error));
								}
								try {
									Response response = new Response(new String(
											arg2));
									JSONObject root = response.asJSONObject();
									
									if (root.getInt("state")!= 1) {// 0
										if (listener != null)
											listener.OnAsynRequestFail("-1",
													root.getString("msg"));
										return;
									}
									JSONArray data = root.getJSONArray("data");
									PageList pl = new PageList();
									City ct = null;
									for (int i = 0; i < data.length(); i++) {
										JSONObject jo = data.getJSONObject(i);
										ct = new City(jo);
										pl.add(ct);
									}

									listener.OnGetListDone(pl);

								} catch (Exception ex) {
									listener.OnAsynRequestFail("-1",
											GezitechApplication.getContext()
													.getString(R.string.data_error));
								}
							}

							@Override
							public void onFailure(int arg0, Header[] arg1,
									byte[] arg2, Throwable arg3) {
								listener.OnAsynRequestFail(
										"-1",
										GezitechApplication.getContext().getString(
												R.string.data_error));
							}

							@Override
							public void onFinish() { // 完成后调用，失败，成功，都要掉

							}
						});
			} else {
				if (listener != null) {
					listener.OnAsynRequestFail("-1", GezitechApplication
							.getContext().getString(R.string.network_error));
				}
			}
		}
		//获取国家  system/getCountryList  获取国家
		
				public void getCountryList(final OnAsynGetListListener listener) {
					RequestParams params = new RequestParams();
					if (NetUtil.isNetworkAvailable()) {
						HttpUtil.post("api/system/getCountryList", true, params,
								new AsyncHttpResponseHandler() {
									@Override
									public void onSuccess(int arg0, Header[] arg1,
											byte[] arg2) {
										if (arg0 != 200) {
											listener.OnAsynRequestFail("-1",
													GezitechApplication.getContext()
															.getString(R.string.data_error));
										}
										try {
											Response response = new Response(new String(
													arg2));
											JSONObject root = response.asJSONObject();
											
											if (root.getInt("state")!= 1) {// 0
												if (listener != null)
													listener.OnAsynRequestFail("-1",
															root.getString("msg"));
												return;
											}
											JSONArray data = root.getJSONArray("data");
											PageList pl = new PageList();
											Country ct = null;
											for (int i = 0; i < data.length(); i++) {
												JSONObject jo = data.getJSONObject(i);
												ct = new Country(jo);
												pl.add(ct);
											}

											listener.OnGetListDone(pl);

										} catch (Exception ex) {
											listener.OnAsynRequestFail("-1",
													GezitechApplication.getContext()
															.getString(R.string.data_error));
										}
									}

									@Override
									public void onFailure(int arg0, Header[] arg1,
											byte[] arg2, Throwable arg3) {
										listener.OnAsynRequestFail(
												"-1",
												GezitechApplication.getContext().getString(
														R.string.data_error));
									}

									@Override
									public void onFinish() { // 完成后调用，失败，成功，都要掉

									}
								});
					} else {
						if (listener != null) {
							listener.OnAsynRequestFail("-1", GezitechApplication
									.getContext().getString(R.string.network_error));
						}
					}
				}
		

}
