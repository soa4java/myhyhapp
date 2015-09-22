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
import com.hyh.www.entity.Contacts;
import com.hyh.www.entity.Friend;
import com.hyh.www.entity.News;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 聊天管理器
 */
public class FriendManager {
	private FriendManager _this = this;
	static FriendManager instance = null;

	public static FriendManager getInstance() {
		if (instance == null) {
			instance = new FriendManager();
		}
		return instance;
	}

/*	获取好友请求数量接口	friend/getfriendnumber
	参数	
	oauth_token	不能为空*/

	public void getfriends(int page, int pageSize,
			final OnAsynGetListArrayListListener listener) {
		RequestParams params = new RequestParams();
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/friend/getfriends", true, params,
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
								Contacts user = null;
								GezitechDBHelper<Contacts> Db = new GezitechDBHelper<Contacts>(
										Contacts.class);
								Db.delete(""); //只删除好友数据 保留其他用户的缓存资料
								PageList pl = new PageList();
								ArrayList<String> mSections = new ArrayList<String>();
								ArrayList<Integer> mPositions = new ArrayList<Integer>();
								int index = 0;
								// 添加好友
								user = new Contacts();
								user.nickname = "添加新朋友";
								user.isLine = 0;
								pl.add(user);
								mSections.add("");
								mPositions.add(index);
								index = 1;

								// 星标好友
								if (root.has("xb") && !root.isNull("xb")) {
									JSONArray datas = root.getJSONArray("xb");
									for (int i = 0; i < datas.length(); i++) {
										JSONObject jo = datas.getJSONObject(i);
										user = new Contacts(jo);
										user.usertype = 1;
										user.isLine = 1;
										if (i + 1 == datas.length())
											user.isLine = 0;
										pl.add(user);
										Db.insert(user);
									}
									if (datas.length() > 0) {
										mSections.add("星标朋友");
										mPositions.add(index);
										index += datas.length();
									}
								}

								// 商家好友
								if (root.has("shop") && !root.isNull("shop")) {
									JSONArray datas = root.getJSONArray("shop");
									for (int i = 0; i < datas.length(); i++) {
										JSONObject jo = datas.getJSONObject(i);
										user = new Contacts(jo);
										user.isLine = 1;
										user.usertype = 2;
										if (i + 1 == datas.length())
											user.isLine = 0;
										pl.add(user);

										Db.insert(user);
									}
									if (datas.length() > 0) {
										mSections.add("商家好友");
										mPositions.add(index);
										index += datas.length();
									}
								}
								// 个人好友
								if (root.has("person")
										&& !root.isNull("person")) {
									JSONArray datas = root
											.getJSONArray("person");
									for (int i = 0; i < datas.length(); i++) {
										JSONObject jo = datas.getJSONObject(i);
										user = new Contacts(jo);
										user.isLine = 1;
										user.usertype = 3;
										if (i + 1 == datas.length())
											user.isLine = 0;
										pl.add(user);

										Db.insert(user);
									}
									if (datas.length() > 0) {
										mSections.add("个人好友");
										mPositions.add(index);
										index = datas.length();
									}
								}

								listener.OnGetListDone(pl, mSections,
										mPositions);

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

	// 获取客户端缓存的朋友列表
	public void getClientCacheFriendList(
			final OnAsynGetListArrayListListener listener) {
		GezitechDBHelper<Contacts> Db = new GezitechDBHelper<Contacts>(Contacts.class);
		PageList pl = new PageList();

		Contacts user = null;
		ArrayList<String> mSections = new ArrayList<String>();
		ArrayList<Integer> mPositions = new ArrayList<Integer>();
		int index = 0;
		// 添加好友
		user = new Contacts();
		user.nickname = "添加新朋友";
		user.isLine = 0;
		pl.add(user);
		mSections.add("");
		mPositions.add(index);
		index = 1;

		ArrayList<Contacts> xb = Db.query("usertype=1", 0, "");
		if (xb != null && xb.size() > 0) {
			pl.addAll(xb);
			mSections.add("星标朋友");
			mPositions.add(index);
			index += xb.size();
		}
		ArrayList<Contacts> shop = Db.query("usertype=2", 0, "");
		if (shop != null && shop.size() > 0) {
			pl.addAll(shop);
			mSections.add("商家好友");
			mPositions.add(index);
			index += shop.size();
		}
		ArrayList<Contacts> perosn = Db.query("usertype=3", 0, "");
		if (perosn != null && perosn.size() > 0) {
			pl.addAll(perosn);
			mSections.add("个人好友");
			mPositions.add(index);
			index += perosn.size();
		}

		listener.OnGetListDone(pl, mSections, mPositions);

	}

	// 获取朋友请求条数
	public void getfriendnumber(final OnAsynRequestListener listener) {
		RequestParams params = new RequestParams();
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.get("api/friend/getfriendnumber", true, params,
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
								int count = root.has("count") ? root
										.getInt("count") : 0;

								listener.OnAsynRequestCallBack(count);

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

	// 添加和取消星标 friend/adddelisstar
	public void adddelisstar(long fid, int isstar,
			final OnAsynRequestListener listener) {
		RequestParams params = new RequestParams();
		params.put("fid", fid);
		params.put("isstar", isstar);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.get("api/friend/adddelisstar", true, params,
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

								listener.OnAsynRequestCallBack("1");

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

	// 获取搜索朋友 api/friend/scanfriend
	public void scanfriend(int page, int pageSize,String key,
			final OnAsynGetListListener listener) {
		RequestParams params = new RequestParams();
		params.put("page", page);
		params.put("pageSize", pageSize);
		params.put("username", key);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/friend/scanfriend", true, params,
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
								PageList pl = new PageList();
								User user = null;

								JSONObject data = root.getJSONObject("data");

								if (data.has("datas") && !data.isNull("datas")) {
									JSONArray datas = data
											.getJSONArray("datas");
									for (int i = 0; i < datas.length(); i++) {
										JSONObject jo = datas.getJSONObject(i);
										user = new User(jo);
										pl.add(user);
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

	// 获取好友请求列表 api/friend/untreatedFriendlist
	public void untreatedFriendlist(int page, int pageSize,
			final OnAsynGetListListener listener) {
		RequestParams params = new RequestParams();
		params.put("page", page);
		params.put("pageSize", pageSize);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/friend/untreatedFriendlist", true, params,
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
								PageList pl = new PageList();
								Friend friend = null;

								JSONObject data = root.getJSONObject("data");

								if (data.has("datas") && !data.isNull("datas")) {
									JSONArray datas = data
											.getJSONArray("datas");
									for (int i = 0; i < datas.length(); i++) {
										JSONObject jo = datas.getJSONObject(i);
										friend = new Friend(jo);
										pl.add(friend);
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

	// 拒绝添加好友 friend/denyaddFriend
	public void denyaddFriend(long fid, final OnAsynRequestListener listener) {
		RequestParams params = new RequestParams();
		params.put("fid", fid + "");
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.get("api/friend/denyaddFriend", true, params,
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

								listener.OnAsynRequestCallBack("1");

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

	// 同意添加好友 friend/agreeaddFriend
	public void agreeaddFriend(long fid, final OnAsynRequestListener listener) {
		RequestParams params = new RequestParams();
		params.put("fid", fid);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.get("api/friend/agreeaddFriend", true, params,
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

								listener.OnAsynRequestCallBack("1");

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

	// 添加好友
	public void addFriend(long fid, final OnAsynRequestListener listener) {
		RequestParams params = new RequestParams();
		params.put("fid", fid);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.get("api/friend/addFriend", true, params,
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

								listener.OnAsynRequestCallBack("1");

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

	// 删除好友 friend/deleteFriend
	public void deleteFriend(long fid, final OnAsynRequestListener listener) {
		RequestParams params = new RequestParams();
		params.put("fid", fid);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.get("api/friend/deleteFriend", true, params,
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

								listener.OnAsynRequestCallBack("1");

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

	// 加入黑名单和解除黑名单接口 friend/isblacklist
	public void isblacklist(long fid, int isblacklist,
			final OnAsynRequestListener listener) {
		RequestParams params = new RequestParams();
		params.put("fid", fid);
		params.put("isblacklist", isblacklist);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.get("api/friend/isblacklist", true, params,
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

								listener.OnAsynRequestCallBack("1");

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

//	设置好友备注	friend/setnotes
//	参数	
//	oauth_token	不能为空
//	fid	好友id
//	notes	备注信息

	public void setnotes(long fid, String notes,
			final OnAsynRequestListener listener) {
		RequestParams params = new RequestParams();
		params.put("fid", fid);
		params.put("notes", notes);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.get("api/friend/setnotes", true, params,
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

								listener.OnAsynRequestCallBack("1");

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

//	是否置顶聊天	friend/istop
//	参数	
//	oauth_token	不能为空
//	fid	好友id
//	istop	是否置顶参数 1为置顶 ，0为不置顶

	public void istop(long fid, int istop, final OnAsynRequestListener listener) {
		RequestParams params = new RequestParams();
		params.put("fid", fid);
		params.put("istop", istop);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.get("api/friend/istop", true, params,
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

								listener.OnAsynRequestCallBack("1");

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

//	是否关闭聊天	friend/isclose
//	参数	
//	oauth_token	不能为空
//	fid	好友id
//	isclose	是否关闭聊天参数 1为关闭 ，0为不关闭


	public void isclose(long fid, int isclose,
			final OnAsynRequestListener listener) {
		RequestParams params = new RequestParams();
		params.put("fid", fid);
		params.put("isclose", isclose);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.get("api/friend/isclose", true, params,
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

								listener.OnAsynRequestCallBack("1");

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

	// 有消息是否通知 friend/isremind
	// 参数
	// oauth_token 不能为空
	// fid 好友id
	// isremind 有消息是否通知参数 1通知，0为不通知

	public void isremind(long fid, int isremind,
			final OnAsynRequestListener listener) {
		RequestParams params = new RequestParams();
		params.put("fid", fid);
		params.put("isremind", isremind);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.get("api/friend/isremind", true, params,
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

								listener.OnAsynRequestCallBack("1");

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
	/*投诉举报	system/usercomplain
	参数	
	oauth_token	不能为空
	fid	被举报人id
	ctime	举报时间
	content	举报内容*/
	public void usercomplain(long fid, String content,
			final OnAsynRequestListener listener) {
		RequestParams params = new RequestParams();
		params.put("fid", fid);
		params.put("ctime", System.currentTimeMillis()/1000);
		params.put("content", content );
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.get("api/system/usercomplain", true, params,
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

								listener.OnAsynRequestCallBack("1");

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
	 /*清空聊天记录接口	friend/emptyrecord
	 参数	
	 oauth_token	不能为空
	 fid	好友id*/
	
	public void emptyrecord(long fid,
			final OnAsynRequestListener listener) {
		RequestParams params = new RequestParams();
		params.put("fid", fid);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.get("api/friend/emptyrecord", true, params,
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

								listener.OnAsynRequestCallBack("1");

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
/*	获取聊天记录 	friend/getchatrecord
	参数	
	oauth_token	不能为空
	page	当前页数
	contenet	搜索内容
	pageSize	每页显示数量
	fid	好友id*/

	public void getchatrecord(int page, int pageSize,String contenet,long fid,
			final OnAsynGetListListener listener) {
		RequestParams params = new RequestParams();
		params.put("page", page);
		params.put("contenet", contenet);
		params.put("pageSize", pageSize);
		params.put("fid", fid);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/friend/getchatrecord", true, params,
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
								PageList pl = new PageList();
								
								

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
