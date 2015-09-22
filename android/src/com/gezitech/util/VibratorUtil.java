package com.gezitech.util;

import android.content.Context;
import android.os.Vibrator;
/**
 * 
 * @author xiaobai
 * 2014-5-9
 * @todo(  手机震动 )
 */
public class VibratorUtil {
	private Context context;
	public VibratorUtil(Context context){
		this.context = context;
		Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);  
        long [] pattern = {100,400,100,400};   // 停止 开启 停止 开启   
        vibrator.vibrate(pattern,-1);
	}
}
