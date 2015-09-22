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
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.contract.GezitechManager_I.OnAsynProgressListener;
import com.gezitech.contract.GezitechManager_I.OnAsynRequestFailListener;
import com.gezitech.contract.GezitechManager_I.OnAsynUpdateListener;
import com.gezitech.entity.PageList;
import com.gezitech.entity.User;
import com.gezitech.http.HttpUtil;
import com.gezitech.http.Response;
import com.gezitech.service.GezitechService;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
import com.gezitech.util.NetUtil;
import com.hyh.www.R;
import com.hyh.www.entity.Banktype;
import com.hyh.www.entity.Bill;
import com.hyh.www.entity.Businesslist;
import com.hyh.www.entity.Companytype;
import com.hyh.www.entity.Incomelist;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 账户相关
 */
public class AccountManager {
	private AccountManager _this = this;
	static AccountManager instance = null;

	public static AccountManager getInstance() {
		if (instance == null) {
			instance = new AccountManager();
		}
		return instance;
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
								if ( state != 1 ) {
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

	// 获取提现银行的列表
	public void getaccountlist(final OnAsynGetListListener listener) {
		RequestParams params = new RequestParams();
		params.put("pageSize", 10000);
		params.put("page", 1);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/account/getaccountlist", true, params,
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
								JSONArray ja = data.getJSONArray("datas");
								PageList pl = new PageList();
								Banktype bt = null;
								for (int i = 0; i < ja.length(); i++) {
									JSONObject jo = ja.getJSONObject(i);
									bt = new Banktype(jo);
									pl.add(bt);
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

	// 提交提现资料
	public void submitcash(String bank, String bankname, String bankaccount,
			String accountname, double money,
			final OnAsynUpdateListener listener) {
		RequestParams params = new RequestParams();
		params.put("bank", bank);
		params.put("bankname", bankname);
		params.put("bankaccount", bankaccount);
		params.put("accountname", accountname);
		params.put("money", money);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/account/submitcash", true, params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							if (arg0 != 200) {
								listener.OnAsynRequestFail(
										"-1",
										GezitechApplication.getContext()
												.getString(
														R.string.tixian_error));
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
								listener.onUpdateDone(root.has("msg") ? root
										.getString("msg") : "提交成功");

							} catch (Exception ex) {
								listener.OnAsynRequestFail(
										"-1",
										GezitechApplication.getContext()
												.getString(
														R.string.tixian_error));
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.tixian_error));
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

	// 获取我的推广统计
	public void spreadcount(final OnAsynGetOneListener listener) {
		RequestParams params = new RequestParams();
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.get("api/recommend/spreadcount", true, params,
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

	// 从商家获取收益的列表
	public void businesslist(int page, int pageSize, int type,
			final OnAsynGetListListener listener) {
		RequestParams params = new RequestParams();
		params.put("pageSize", pageSize);
		params.put("page", page);
		params.put("type", type);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/recommend/businesslist", true, params,
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
								PageList pl = new PageList();
								if (data.has("datas") && !data.isNull("datas")) {
									JSONArray ja = data.getJSONArray("datas");
									Businesslist bl = null;
									for (int i = 0; i < ja.length(); i++) {
										JSONObject jo = ja.getJSONObject(i);
										bl = new Businesslist(jo);
										pl.add(bl);
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

	// 从个人获取收益的列表
	public void incomelist(int page, int pageSize,
			final OnAsynGetListListener listener) {
		RequestParams params = new RequestParams();
		params.put("pageSize", pageSize);
		params.put("page", page);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/recommend/incomelist", true, params,
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
								if (data.has("datas") && !data.isNull("datas")) {
									JSONArray ja = data.getJSONArray("datas");
									Incomelist il = null;
									for (int i = 0; i < ja.length(); i++) {
										JSONObject jo = ja.getJSONObject(i);
										il = new Incomelist(jo);
										pl.add(il);
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

	// 购买账单
	public void buylist(int page, int pageSize,
			final OnAsynGetListListener listener) {
		salelist(page, pageSize, -1, listener);
	}

	// 销售账单 购买账单
	public void salelist(int page, int pageSize, long bid,
			final OnAsynGetListListener listener) {
		RequestParams params = new RequestParams();
		params.put("pageSize", pageSize);
		params.put("page", page);
		if (bid != -1)
			params.put("bid", bid);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post(bid == -1 ? "api/trade/buylist"
					: "api/trade/salelist", true, params,
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
								if (data.has("datas") && !data.isNull("datas")) {
									JSONArray ja = data.getJSONArray("datas");
									Bill bill = null;
									for (int i = 0; i < ja.length(); i++) {
										JSONObject jo = ja.getJSONObject(i);
										bill = new Bill(jo);
										pl.add(bill);
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
	/**
	 * 
	 * TODO( 获取账户明细  )
	 */
	public void getAccountRecordList(int page, int pageSize,final OnAsynGetListListener listener) {
		RequestParams params = new RequestParams();
		params.put("pageSize", pageSize);
		params.put("page", page);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/User/getAccountRecordList", true, params,
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
								if (data.has("datas") && !data.isNull("datas")) {
									JSONArray ja = data.getJSONArray("datas");
									Bill bill = null;
									for (int i = 0; i < ja.length(); i++) {
										JSONObject jo = ja.getJSONObject(i);
										bill = new Bill(jo);
										pl.add(bill);
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

	// 把活动劵充值为余额
	public void spendCoupon(final OnAsynUpdateListener listener) {
		RequestParams params = new RequestParams();
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/Account/spendCoupon", true, params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							if (arg0 != 200) {
								listener.OnAsynRequestFail(
										"-1",
										GezitechApplication.getContext()
												.getString(
														R.string.tixian_error));
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
								listener.onUpdateDone(root.has("msg") ? root
										.getString("msg") : "充值成功");

							} catch (Exception ex) {
								listener.OnAsynRequestFail(
										"-1",
										GezitechApplication.getContext()
												.getString(
														R.string.tixian_error));
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							listener.OnAsynRequestFail(
									"-1",
									GezitechApplication.getContext().getString(
											R.string.tixian_error));
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
	 * 订单列表 trade/tradenumberlist 参数 oauth_token 不能为空 state_o
	 * uid 获取某个用户的订单
	 * 1为所有订单，2为已付款订单，3为服务中订单
	 */
	public void tradenumberlist(int page, int pageSize, int state_o,long uid,
			final OnAsynGetListListener listener) {
		RequestParams params = new RequestParams();
		params.put("pageSize", pageSize);
		params.put("page", page);
		params.put("state_o", state_o);
		params.put("uid", uid);
		if (NetUtil.isNetworkAvailable()) {
			HttpUtil.post("api/trade/tradenumberlist", true, params,
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
								if (data.has("datas") && !data.isNull("datas")) {
									JSONArray ja = data.getJSONArray("datas");
									Bill bill = null;
									for (int i = 0; i < ja.length(); i++) {
										JSONObject jo = ja.getJSONObject(i);
										bill = new Bill(jo);
										bill.info = jo.has("info") ? jo.getJSONObject("info") : null;
										if( bill.info != null ){
											bill.username = bill.info.has("username") ? bill.info.getString("username") : "";
											bill.head = bill.info.has("head") ? bill.info.getString("head") : "";
											bill.isfriend = bill.info.has("isfriend") ? bill.info.getInt("isfriend") : 2;
											bill.nickname = bill.info.has("nickname") ? bill.info.getString("nickname") : "";
										}
										pl.add(bill);
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
}
