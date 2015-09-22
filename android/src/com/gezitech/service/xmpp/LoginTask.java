package com.gezitech.service.xmpp;

import java.util.Collection;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;

import com.gezitech.basic.GezitechApplication;
import com.gezitech.config.Conf;
import com.gezitech.entity.User;
import com.gezitech.service.GezitechService;
import com.hyh.www.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;
//xmpp异步登录
public class LoginTask extends AsyncTask<String, Integer, Integer> {
	private ProgressDialog pd;
	private Context context;
	private User user;
	public LoginTask(Context context) {
		this.context = context;
		user = GezitechService.getInstance().getCurrentLoginUser( context );
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(String... params) {
		return login();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
	}

	@Override
	protected void onPostExecute(Integer result) {
		GezitechApplication.isConnection = 0;
		switch (result) {
		case Constant.LOGIN_SECCESS: // 登录成功
			
			// 聊天服务
			/*Intent chatServer = new Intent(context, IMChatService.class);
			context.startService(chatServer);*/
			GezitechService.getInstance().unbindBackgroundService(  context );
			GezitechService.getInstance().bindBackgroundService( context );
			//Toast.makeText(context,"登陆1", Toast.LENGTH_SHORT ).show();
			break;
		case Constant.LOGIN_ERROR_ACCOUNT_PASS:// 账户或者密码错误
			/*Toast.makeText(
					context,
					context.getResources().getString(
							R.string.message_invalid_username_password),
					Toast.LENGTH_SHORT).show();*/
			//Toast.makeText(context,"登陆2", Toast.LENGTH_SHORT ).show();
			break;
		case Constant.SERVER_UNAVAILABLE:// 服务器连接失败
			/*Toast.makeText(
					context,
					context.getResources().getString(
							R.string.message_server_unavailable),
					Toast.LENGTH_SHORT).show();*/
			//Toast.makeText(context,"登陆3", Toast.LENGTH_SHORT ).show();
			break;
		case Constant.LOGIN_ERROR:// 未知异常
			/*Toast.makeText(
					context,
					context.getResources().getString(
							R.string.unrecoverable_error), Toast.LENGTH_SHORT)
					.show();*/
			//Toast.makeText(context,"登陆4", Toast.LENGTH_SHORT ).show();
			break;
		}
		super.onPostExecute(result);
	}

	// 登录
	private Integer login() {
		GezitechApplication.isConnection =  1;
		try {
			XMPPConnection connection = XmppConnectionManager.getInstance().getConnection();
			connection.connect();
			connection.login(user.id+"", user.access_token, Conf.domian ); // 登录
			
			//OfflineMsgManager.getInstance(activitySupport).dealOfflineMsg(connection);//处理离线消息
			connection.sendPacket(new Presence(Presence.Type.available));
			
			if ( Conf.isNovisible ) {// 隐身登录
				Presence presence = new Presence(Presence.Type.unavailable);
				Collection<RosterEntry> rosters = connection.getRoster()
						.getEntries();
				for (RosterEntry rosterEntry : rosters) {
					presence.setTo(rosterEntry.getUser());
					connection.sendPacket(presence);
				}
			}
			return Constant.LOGIN_SECCESS;
		} catch (Exception xee) {
			if (xee instanceof XMPPException) {
				XMPPException xe = (XMPPException) xee;
				final XMPPError error = xe.getXMPPError();
				int errorCode = 0;
				if (error != null) {
					errorCode = error.getCode();
				}
				if (errorCode == 401) {
					return Constant.LOGIN_ERROR_ACCOUNT_PASS;
				}else if (errorCode == 403) {
					return Constant.LOGIN_ERROR_ACCOUNT_PASS;
				} else {
					return Constant.SERVER_UNAVAILABLE;
				}
			} else {
				return Constant.LOGIN_ERROR;
			}
		}
	}
}
