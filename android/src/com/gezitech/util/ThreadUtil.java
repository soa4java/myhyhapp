package com.gezitech.util;

import android.os.Message;

public class ThreadUtil {
	public static Message createMessage(int arg1, int arg2, Object obj){
		Message msg = new Message();
		msg.arg1 = arg1;
		msg.arg2 = arg2;
		msg.obj = obj;
		return msg;
	}
}
