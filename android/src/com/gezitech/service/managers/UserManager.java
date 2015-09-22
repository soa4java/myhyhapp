package com.gezitech.service.managers;

import java.util.ArrayList;
import java.util.Date;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.gezitech.basic.GezitechApplication;
import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechException;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynProgressListener;
import com.gezitech.contract.GezitechManager_I.OnAsynRequestFailListener;
import com.gezitech.contract.GezitechManager_I.OnAsynUpdateListener;
import com.gezitech.entity.AppInfo;
import com.gezitech.entity.User;
import com.gezitech.http.HttpUtil;
import com.gezitech.http.Response;
import com.gezitech.service.GezitechService;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.gezitech.util.NetUtil;
import com.hyh.www.R;
import com.hyh.www.entity.Bill;
import com.hyh.www.entity.Chat;
import com.hyh.www.entity.Friend;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @author true或者false:取决于接口中oauth_token，如果有就是true,没有 就是false *@author
 *         parms是一个参数名称，不管参数里面有没有值他都必须存在
 * @author xiaobai 2014-4-21
 * @todo( 用户登录 和注册 )
 */
public class UserManager {
	private UserManager _this = this;
	static UserManager instance = null;
	private static User curruser;

	public static UserManager getInstance() {
		if (instance == null) {
			instance = new UserManager();
			curruser = GezitechService.getInstance().getCurrentUser();
		}
		return instance;
	}

	// 登录 账户和密码
	public void Login(String username, String passWd,
			final OnAsynRequestFailListener listener) {
		RequestParams params = new RequestParams();
		params.put("username", username+"");
		params.put("password", passWd+"");
		params.put("grant_type", "password"+"");
		//params.put("long", longs+"");
		//params.put("lat", lat+"");
		//params.put("city", "city");
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/Login/login", true, params,
					new AsyncHttpResponseHandler() {
						public void onFinish() { // 完成后调用，失败，成功，都要掉
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {

							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.login_fail));

						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							_this.userInfoProcess(new String(arg2), listener);
						};
					});
		} else {
			listener.OnAsynRequestFail("-1", GezitechApplication.getContext()
					.getString(R.string.network_error));
		}
	}

	// 注册
	public void register(RequestParams params,
			final OnAsynRequestFailListener listener) {
		params.put("grant_type", "password");
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/Login/register", true, params,
					new AsyncHttpResponseHandler() {
						public void onFinish() {
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.register_fail));
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							// TODO Auto-generated method stub
							_this.userInfoProcess(new String(arg2), listener);
						};
					});
		} else {
			listener.OnAsynRequestFail("-1", GezitechApplication.getContext()
					.getString(R.string.network_error));
		}
	}

	// 用户数据的公用处理
	public void userInfoProcess(String arg0,
			final OnAsynRequestFailListener listener) {
		GezitechDBHelper<User> dbHelper;
		try {
			Response res = new Response(arg0);
			JSONObject jsonObj = res.asJSONObject();

			int state = jsonObj.getInt("state");
			String msg = jsonObj.getString("msg");
			if (state == 1) {// 成功
				// 把登录数据存储到数据库
				if (jsonObj.isNull("data")) {
					listener.OnAsynRequestFail("-1", msg);
					return;
				}
				// data数据包
				JSONObject data = jsonObj.getJSONObject("data");
				// token 和 user_info 数据包
				if (data.isNull("token") || data.isNull("user_info")) {
					listener.OnAsynRequestFail("-1", GezitechApplication
							.getContext().getString(R.string.login_fail));
					return;
				}
				JSONObject user_info = data.getJSONObject("user_info");
				JSONObject token = data.getJSONObject("token");
				
				//取出用户id
				long uid = user_info.has("id") ? user_info.getLong("id") : 0;
				
				if( uid <= 0 ){
					
					listener.OnAsynRequestFail("-1", GezitechApplication.getContext()
							.getString(R.string.login_fail ));
					return;
					
				}
				
				
				GezitechApplication.systemSp.edit().putLong("uid", uid ).commit();
				
				//为了根据用户不同生成不一样的数据把
				AppInfo ai = new AppInfo();
		        if(ai.isVersionDifferent()){//保存系统升级后数据库版本也是同步的。
		        	GezitechDBHelper<GezitechEntity> db = new GezitechDBHelper<GezitechEntity>(GezitechEntity.class);
		        	db.dropAllTables();
		        	ai = new AppInfo();//重新生成版本信息
		        } 
				
				
				
				
				User user = new User(user_info);
				user.access_token = token.has("access_token") ? token.getString("access_token") : "";

				// 获取当前时间
				Date t = new Date();
				long currentTime = t.getTime() / 1000;
				currentTime += token.has("expires_in") ? (token
						.getLong("expires_in") <= 0 ? 604800 : token
						.getLong("expires_in")) : 604800;

				user.expires_in = currentTime;

				user.refresh_token = token.has("refresh_token") ? token
						.getString("refresh_token") : "";
				user.islogin = 1;
				
				// 保存到数据库
				dbHelper = new GezitechDBHelper<User>(User.class);
				GezitechService.getInstance().clearCurrentUser();
				dbHelper.insert(user);
				GezitechService.getInstance().setCurrentUser(user);
				
				// 推送数据回传
				GezitechService.getInstance().pushInfo(true);
				
				listener.OnAsynRequestFail("1", GezitechApplication
						.getContext().getString(R.string.login_success));
			} else {// 失败
				listener.OnAsynRequestFail("-1", msg);
			}

		} catch (GezitechException e) {
			listener.OnAsynRequestFail("-1", GezitechApplication.getContext()
					.getString(R.string.register_fail));

		} catch (JSONException e) {
			listener.OnAsynRequestFail("-1", GezitechApplication.getContext()
					.getString(R.string.register_fail));

		}
	}

	// 获取用户资料
	public void gainuserinfo(final OnAsynGetOneListener listener) {
		RequestParams params = new RequestParams();
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/User/gainuserinfo", true, params,
					new AsyncHttpResponseHandler() {
						public void onFinish() { // 完成后调用，失败，成功，都要掉
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							// TODO Auto-generated method stub
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.data_error));
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {

							try {
								Response res = new Response(new String(arg2));
								JSONObject jsonObj = res.asJSONObject();

								int state = jsonObj.getInt("state");
								String msg = jsonObj.getString("msg");
								if (state != 1 ) {
									listener.OnAsynRequestFail("-1", msg);
									return;
								}
								if (!jsonObj.has("data")
										|| jsonObj.isNull("data")) {
									listener.OnAsynRequestFail("-1", msg);
									return;
								}

								JSONObject data = jsonObj.getJSONObject("data");

								User user = new User(data);

								User user_cache = GezitechService.getInstance()
										.getCurrentLoginUser(
												GezitechApplication
														.getContext());

								// 保存到数据库
								GezitechDBHelper<User> dbHelper = new GezitechDBHelper<User>(
										User.class);

								user_cache.uid = user.uid;
								user_cache.realname = user.realname;
								//user_cache.IDnumber = user.IDnumber;
								user_cache.tel = user.tel;
								//user_cache.email = user.email;
								user_cache.address = user.address;
								user_cache.money = user.money;
								user_cache.inviteCode = user.inviteCode;
								dbHelper.save(user_cache);

								listener.OnGetOneDone(user);

							} catch (GezitechException e) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));

							} catch (JSONException e) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));

							}
						};
					});
		} else {
			listener.OnAsynRequestFail("-1", GezitechApplication.getContext()
					.getString(R.string.network_error));
		}
	}

	// 更新用户信息
	public void updateUserInfo(RequestParams params,
			final OnAsynUpdateListener listener) {
		HttpUtil.post("api/User/updateUserInfo", true, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

						try {
							Response res = new Response(new String(arg2));
							JSONObject jsonObj = res.asJSONObject();

							int state = jsonObj.getInt("state");
							String msg = jsonObj.getString("msg");
							if (state != 1 ) {
								listener.OnAsynRequestFail("-1", msg);
								return;
							}
							listener.onUpdateDone("1");
						} catch (GezitechException e) {
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.save_userinfo_fail));
						} catch (JSONException e) {
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.save_userinfo_fail));
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						listener.OnAsynRequestFail(
								"-1",
								GezitechApplication.getContext().getString(
										R.string.save_userinfo_fail));
					}

					@Override
					public void onFinish() {
						super.onFinish();
					}
				});
	}

	// 获取手机验证码 )
	public void phonecode(String phone,int type, final OnAsynUpdateListener listener) {
		RequestParams params = new RequestParams();
		params.put("phone", phone);
		params.put("type", type );
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/Common/phonecode", false, params,
					new AsyncHttpResponseHandler() {
						public void onFinish() { // 完成后调用，失败，成功，都要掉
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							// TODO Auto-generated method stub
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.get_phone_code_fail));
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {

							try {
								Response res = new Response(new String(arg2));
								JSONObject jsonObj = res.asJSONObject();

								int state = jsonObj.getInt("state");
								String msg = jsonObj.getString("msg");
								if (state != 1 ) {
									listener.OnAsynRequestFail("-1", msg);
									return;
								}
								//String code = jsonObj.getString("code");

								listener.onUpdateDone("1");

							} catch (GezitechException e) {
								listener.OnAsynRequestFail(
										"-1",
										GezitechApplication
												.getContext()
												.getString(
														R.string.get_phone_code_fail));

							} catch (JSONException e) {
								listener.OnAsynRequestFail(
										"-1",
										GezitechApplication
												.getContext()
												.getString(
														R.string.get_phone_code_fail));

							}
						};
					});
		} else {
			listener.OnAsynRequestFail("-1", GezitechApplication.getContext()
					.getString(R.string.network_error));
		}
	}

	// 上传头像
	public void uploadhead(RequestParams params,
			final OnAsynProgressListener listener) {
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/user/uploadhead", true, params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							if (arg0 != 200) {
								listener.OnAsynRequestFail(
										"-1",
										GezitechApplication
												.getContext()
												.getString(
														R.string.upload_head_error));
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
								listener.onUpdateDone(root.has("head") ? root
										.getString("head") : "");
							} catch (Exception ex) {
								listener.OnAsynRequestFail(
										"-1",
										GezitechApplication
												.getContext()
												.getString(
														R.string.upload_head_error));
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.upload_head_error));
						}

						@Override
						public void onFinish() { // 完成后调用，失败，成功，都要掉

						}

						@Override
						public void onProgress(int bytesWritten, int totalSize) {
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

	// 修改密码
	public void updatepassword(RequestParams params,
			final OnAsynUpdateListener listener) {
		HttpUtil.post("api/User/updatepassword", true, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

						try {
							Response res = new Response(new String(arg2));
							JSONObject jsonObj = res.asJSONObject();

							int state = jsonObj.getInt("state");
							String msg = jsonObj.getString("msg");
							if (state != 1 ) {
								listener.OnAsynRequestFail("-1", msg);
								return;
							}
							listener.onUpdateDone("1");
						} catch (GezitechException e) {
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.save_userinfoPass_fail));
						} catch (JSONException e) {
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.save_userinfoPass_fail));
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						listener.OnAsynRequestFail(
								"-1",
								GezitechApplication.getContext().getString(
										R.string.save_userinfoPass_fail));
					}

					@Override
					public void onFinish() {
						super.onFinish();
					}
				});
	}

	// 获取用户资料
	//是否保存
	public void getcompanyinfo(long uid, final boolean issave, final OnAsynGetOneListener listener) {
		RequestParams params = new RequestParams();
		params.put("uid", uid);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/User/getcompanyinfo", true, params,
					new AsyncHttpResponseHandler() {
						public void onFinish() { // 完成后调用，失败，成功，都要掉
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							// TODO Auto-generated method stub
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.data_error));
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {

							try {
								Response res = new Response(new String(arg2));
								JSONObject jsonObj = res.asJSONObject();

								int state = jsonObj.getInt("state");
								String msg = jsonObj.getString("msg");
								if (state != 1 ) {
									listener.OnAsynRequestFail("-1", msg);
									return;
								}
								if (!jsonObj.has("data")
										|| jsonObj.isNull("data")) {
									listener.OnAsynRequestFail("-1", msg);
									return;
								}

								JSONObject data = jsonObj.getJSONObject("data");

								User user = new User(data);
									
								if(issave){ 
								User user_cache = GezitechService.getInstance()
										.getCurrentLoginUser(
												GezitechApplication
														.getContext());

								// 保存到数据库
								GezitechDBHelper<User> dbHelper = new GezitechDBHelper<User>(
										User.class);

								user_cache.companyTypeId = user.companyTypeId;
								user_cache.companyTypeName = user.companyTypeName;
								user_cache.company_name = user.company_name;
								user_cache.company_address = user.company_address;
								user_cache.company_tel = user.company_tel;
								user_cache.company_shopname = user.company_shopname;
								user_cache.company_license = user.company_license;
								user_cache.company_certificate = user.company_certificate;
								user_cache.passtime = user.passtime;
								user_cache.state = user.state;
								user_cache.company_userphoto = user.company_userphoto;
								user_cache.company_placeshowone = user.company_placeshowone;
								user_cache.company_placeshowtwo = user.company_placeshowtwo;
								user_cache.company_placeshowthree = user.company_placeshowthree;
								//user_cache.isbusiness = user.state == 1 ? 1 : 0;
								user_cache.businesstime = user.businesstime;
								user_cache.isdelivery = user.isdelivery;
								user_cache.touchname = user.touchname;
								user_cache.IDnumber = user.IDnumber;
								user_cache.auth_type = user.auth_type;
								user_cache.account_name = user.account_name;
								user_cache.account_number = user.account_number;
								user_cache.account_bankname = user.account_bankname;
								dbHelper.save(user_cache);
								}
								listener.OnGetOneDone(user);

							} catch (GezitechException e) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));

							} catch (JSONException e) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));

							}
						};
					});
		} else {
			listener.OnAsynRequestFail("-1", GezitechApplication.getContext()
					.getString(R.string.network_error));
		}
	}

	// 提交商家资料
	public void submitretailers(RequestParams params,
			final OnAsynUpdateListener listener) {
		HttpUtil.post("api/User/submitretailers", true, params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

						try {
							Response res = new Response(new String(arg2));
							JSONObject jsonObj = res.asJSONObject();

							int state = jsonObj.getInt("state");
							String msg = jsonObj.getString("msg");
							if (state != 1 ) {
								listener.OnAsynRequestFail("-1", msg);
								return;
							}
							listener.onUpdateDone("1");
						} catch (GezitechException e) {
							listener.OnAsynRequestFail("-1", "提交失败,重新再试");
						} catch (JSONException e) {
							listener.OnAsynRequestFail("-1", "提交失败,重新再试");
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						listener.OnAsynRequestFail("-1", "提交失败,重新再试");
					}

					@Override
					public void onFinish() {
						super.onFinish();
					}
				});
	}

	// 获取个人账户
	public void accountlist(final OnAsynGetOneListener listener) {
		RequestParams params = new RequestParams();
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/account/accountlist", true, params,
					new AsyncHttpResponseHandler() {
						public void onFinish() { // 完成后调用，失败，成功，都要掉
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							// TODO Auto-generated method stub
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.data_error));
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {

							try {
								Response res = new Response(new String(arg2));
								JSONObject jsonObj = res.asJSONObject();

								int state = jsonObj.getInt("state");
								String msg = jsonObj.getString("msg");
								if (state != 1 ) {
									listener.OnAsynRequestFail("-1", msg);
									return;
								}
								if (!jsonObj.has("data")
										|| jsonObj.isNull("data")) {
									listener.OnAsynRequestFail("-1", msg);
									return;
								}

								JSONObject data = jsonObj.getJSONObject("data");
								User user = new User(data);

								listener.OnGetOneDone(user);

							} catch (GezitechException e) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));

							} catch (JSONException e) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));

							}
						};
					});
		} else {
			listener.OnAsynRequestFail("-1", GezitechApplication.getContext()
					.getString(R.string.network_error));
		}
	}

	// 获取好友资料 user/getfrienddata
	// 参数
	// oauth_token 不能为空
	// fid 好友id
	public void getfrienddata(long fid, final OnAsynGetOneListener listener) {
		getfrienddata( 0, fid, listener);
	}
	public void getfrienddata(long hyhId , long fid, final OnAsynGetOneListener listener) {
		RequestParams params = new RequestParams();
		params.put("fid", fid);
		params.put("sid", hyhId);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/user/getfrienddata", true, params,
					new AsyncHttpResponseHandler() {
						public void onFinish() { // 完成后调用，失败，成功，都要掉
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							// TODO Auto-generated method stub
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.data_error));
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {

							try {
								Response res = new Response(new String(arg2));
								JSONObject jsonObj = res.asJSONObject();

								int state = jsonObj.getInt("state");
								if (state != 1 ) {
									listener.OnAsynRequestFail("-1", jsonObj.getString("msg") );
									return;
								}
								if (!jsonObj.has("data")
										|| jsonObj.isNull("data")) {
									listener.OnAsynRequestFail("-1", GezitechApplication.getContext()
											.getString(R.string.data_error) );
									return;
								}
								
								JSONObject data = jsonObj.getJSONObject("data");

								Friend user = new Friend(data);
								
								long cuid = 0;
								curruser = GezitechService.getInstance().getCurrentUser();
								if(  curruser != null ){
									cuid = curruser.id;
								}
								
								user.uid = cuid;
								user.fid = user.id;
								user.id = 0;
								
								GezitechDBHelper<Friend> friendDB = new GezitechDBHelper<Friend>(Friend.class);
								try{
									ArrayList<Friend> isExists = friendDB.query("fid="+user.fid+" and uid="+user.uid, 0, "");
									if( isExists !=null && isExists.size() >0 )
										friendDB.delete("fid="+user.fid+" and uid="+user.uid);
								}catch(Exception e){}
								
								friendDB.insert( user );
								
								listener.OnGetOneDone(user);

							} catch (GezitechException e) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));

							} catch (JSONException e) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));

							}
						};
					});
		} else {
			listener.OnAsynRequestFail("-1", GezitechApplication.getContext()
					.getString(R.string.network_error));
		}
	}
	/*获取客服	user/getkefu
	参数	
	oauth_token	不能为空*/
	
	public void getkefu( final OnAsynGetOneListener listener) {
		RequestParams params = new RequestParams();
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/user/getkefu", true, params,
					new AsyncHttpResponseHandler() {
						public void onFinish() { // 完成后调用，失败，成功，都要掉
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							// TODO Auto-generated method stub
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.data_error));
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {

							try {
								Response res = new Response(new String(arg2));
								JSONObject jsonObj = res.asJSONObject();

								int state = jsonObj.getInt("state");
								if (state != 1 ) {
									listener.OnAsynRequestFail("-1", jsonObj.getString("msg") );
									return;
								}
								if (!jsonObj.has("data")
										|| jsonObj.isNull("data")) {
									listener.OnAsynRequestFail("-1", GezitechApplication.getContext()
											.getString(R.string.data_error) );
									return;
								}
								JSONObject data = jsonObj.getJSONObject("data");
								
								Friend f = new Friend(data);
								
								listener.OnGetOneDone( f );
								

							} catch (GezitechException e) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));

							} catch (JSONException e) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));

							}
						};
					});
		} else {
			listener.OnAsynRequestFail("-1", GezitechApplication.getContext()
					.getString(R.string.network_error));
		}
	}
/*	重设密码接口	user/Resetcipher
	参数	
	phone	手机号码
	code	手机验证码
	newpassword	新密码*/
	public void Resetcipher(RequestParams params, final OnAsynRequestFailListener listener) {
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/user/Resetcipher", true, params,
					new AsyncHttpResponseHandler() {
						public void onFinish() { // 完成后调用，失败，成功，都要掉
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {

							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.find_pass_fail));

						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							try {
								Response res = new Response(new String(arg2));
								JSONObject jsonObj = res.asJSONObject();

								int state = jsonObj.getInt("state");
								if (state != 1 ) {
									listener.OnAsynRequestFail("-1", jsonObj.getString("msg") );
									return;
								}
								listener.OnAsynRequestFail("1", GezitechApplication.getContext()
										.getString(R.string.find_pass_success));

							} catch (GezitechException e) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.find_pass_fail));

							} catch (JSONException e) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.find_pass_fail));

							}
							
						};
					});
		} else {
			listener.OnAsynRequestFail("-1", GezitechApplication.getContext()
					.getString(R.string.network_error));
		}
	}
	/*用户更新经纬度接口	user/longitude
	参数	
	oauth_token	不能为空
	long	经度
	lat	纬度
	city	城市*/
	public void longitude(RequestParams params ,final  OnAsynRequestFailListener listener ){
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/user/longitude", true, params,
					new AsyncHttpResponseHandler() {
						public void onFinish() { // 完成后调用，失败，成功，都要掉
						}
						@Override
						public void onFailure(int arg0, Header[] arg1,byte[] arg2, Throwable arg3) {}

						@Override
						public void onSuccess(int arg0, Header[] arg1,byte[] arg2) {}
					});
		}
	}
	/*提交商家认证 （改变认证资料的状态 由登记给为提交）	user/updateauthenticatestate
	参数	
	oauth_token	不能为空
	id	认证资料id*/
	
	public void updateauthenticatestate(RequestParams params, final OnAsynRequestFailListener listener) {
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/user/updateauthenticatestate", true, params,
					new AsyncHttpResponseHandler() {
						public void onFinish() { // 完成后调用，失败，成功，都要掉
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {

							listener.OnAsynRequestFail(
									"-1","申请出错,请重试");

						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							try {
								Response res = new Response(new String(arg2));
								JSONObject jsonObj = res.asJSONObject();

								int state = jsonObj.getInt("state");
								if (state != 1 ) {
									listener.OnAsynRequestFail("-1", jsonObj.getString("msg") );
									return;
								}
								listener.OnAsynRequestFail("1", "");

							} catch (GezitechException e) {
								listener.OnAsynRequestFail("-1","申请出错,请重试");

							} catch (JSONException e) {
								listener.OnAsynRequestFail("-1","申请出错,请重试");

							}
							
						};
					});
		} else {
			listener.OnAsynRequestFail("-1", GezitechApplication.getContext()
					.getString(R.string.network_error));
		}
	}
/*	获取订单数量	trade/usertrtradenumber
	参数	
	oauth_token	不能为空*/

	public void usertrtradenumber( final OnAsynGetOneListener listener) {
		RequestParams params = new RequestParams();
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/trade/usertrtradenumber", true, params,
					new AsyncHttpResponseHandler() {
						public void onFinish() { // 完成后调用，失败，成功，都要掉
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							// TODO Auto-generated method stub
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.data_error));
						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {

							try {
								Response res = new Response(new String(arg2));
								JSONObject jsonObj = res.asJSONObject();

								int state = jsonObj.getInt("state");
								if (state != 1 ) {
									listener.OnAsynRequestFail("-1", jsonObj.getString("msg") );
									return;
								}
								if (!jsonObj.has("data")
										|| jsonObj.isNull("data")) {
									listener.OnAsynRequestFail("-1", GezitechApplication.getContext()
											.getString(R.string.data_error) );
									return;
								}
								JSONObject data = jsonObj.getJSONObject("data");
								
								Bill bill = new Bill(data);
								
								listener.OnGetOneDone( bill );
								

							} catch (GezitechException e) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));

							} catch (JSONException e) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));

							}
						};
					});
		} else {
			listener.OnAsynRequestFail("-1", GezitechApplication.getContext()
					.getString(R.string.network_error));
		}
	}
	/**
	 * 退出登录	user/exitLogin
参数	
uid	用户id（必填）
返回值	
state	1或0,1标示可以回复，0标示不可以
msg	返回值说明
	 */
	public void exitLogin( long uid ){
		if (NetUtil.isNetworkAvailable()) {
			RequestParams params = new RequestParams();
			params.put("uid", uid);
			HttpUtil.post("api/user/exitLogin", true, params,
					new AsyncHttpResponseHandler() {
						public void onFinish() { // 完成后调用，失败，成功，都要掉
						}
						@Override
						public void onFailure(int arg0, Header[] arg1,byte[] arg2, Throwable arg3) {}

						@Override
						public void onSuccess(int arg0, Header[] arg1,byte[] arg2) {}
					});
		}
		
	}
	/*三方登录接口	user/thirdPartCheck
	参数	
	platform_uid	三方平台登录后用户的唯一标示
	platform	三方平台标示（qq,sinaweibo,wechat）
	third_oauth_token	三方平台登录的oauth_token（有则传无传空字符串）
	third_oauth_token_secret	三方平台登录的oauth_token_secret（有则传无传空字符串）
	nickname	三方平台登录后用户的昵称 （有则传无传空字符串）
	grant_type	thirdpart
	client_id	同登录注册接口
	client_secret	同登录注册接口
	返回值	
	state	返回值状态 0或1
	msg	返回数据说明
	data	登陆成功后返回值和登录注册一样*/
	public void thirdPartCheck( RequestParams params, final OnAsynRequestFailListener listener) {
		params.put("grant_type", "thirdpart");
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/Login/thirdPartCheck", true, params,
					new AsyncHttpResponseHandler() {
						public void onFinish() { // 完成后调用，失败，成功，都要掉
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {

							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.login_fail));

						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							_this.userInfoProcess(new String(arg2), listener);
						};
					});
		} else {
			listener.OnAsynRequestFail("-1", GezitechApplication.getContext()
					.getString(R.string.network_error));
		}
	}
	//三方登录，第一次填写手机号，邀请码
	public void thirdPartAddPhone( RequestParams params, final OnAsynRequestFailListener listener) {
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/Login/thirdPartAddPhone", true, params,
					new AsyncHttpResponseHandler() {
						public void onFinish() { // 完成后调用，失败，成功，都要掉
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {

							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.login_fail));

						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							
							try {
								Response res = new Response(new String(arg2));
								JSONObject jsonObj = res.asJSONObject();

								int state = jsonObj.getInt("state");
								if (state != 1 ) {
									listener.OnAsynRequestFail("-1", jsonObj.getString("msg") );
									return;
								}
								
								listener.OnAsynRequestFail("1", "上传成功");
								
							} catch (GezitechException e) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));

							} catch (JSONException e) {
								listener.OnAsynRequestFail("-1",
										GezitechApplication.getContext()
												.getString(R.string.data_error));

							}
							
							
							
						};
					});
		} else {
			listener.OnAsynRequestFail("-1", GezitechApplication.getContext()
					.getString(R.string.network_error));
		}
	}
}
