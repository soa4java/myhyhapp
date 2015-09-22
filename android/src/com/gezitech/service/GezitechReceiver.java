package com.gezitech.service;

import com.gezitech.basic.GezitechApplication;
import com.gezitech.service.xmpp.IMChatService;
import com.gezitech.service.xmpp.XmppConnectionManager;
import com.gezitech.util.ToastMakeText;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
//全局静态广播
public class GezitechReceiver extends BroadcastReceiver {

	public GezitechReceiver() {
		super();
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("guangbo", "===123===");
		//Intent service = new Intent(context, IMChatService.class);
		//context.startService(service);
		//GezitechService.getInstance().unbindBackgroundService(  context );
		//GezitechService.getInstance().bindBackgroundService( context );
	}
}
