package com.gezitech.service.lbs;

import java.util.List;

import com.gezitech.basic.GezitechApplication;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

public class AppUtils {
	//程序是否在前台运行  前台运行 true  后台false
	public static boolean isAppOnForeground() {  
        ActivityManager activityManager = (ActivityManager) GezitechApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);  
        String packageName = GezitechApplication.getContext().getPackageName();  

        List<RunningAppProcessInfo> appProcesses = activityManager  
                        .getRunningAppProcesses();  
        if (appProcesses == null)  
                return false;  

        for (RunningAppProcessInfo appProcess : appProcesses) {  
                // The name of the process that this object is associated with.  
                if (appProcess.processName.equals(packageName)  
                                && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {  
                        return true;  
                }  
        }  

        return false;  
	}
}
