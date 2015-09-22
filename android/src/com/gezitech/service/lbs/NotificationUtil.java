package com.gezitech.service.lbs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.gezitech.basic.GezitechApplication;
import com.hyh.www.R;
import com.hyh.www.ZhuyeActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Audio;

public class NotificationUtil {
	/**
	 * 
	 * TODO()
	 * isVoice 是否有声音
	 * isVibration 是否有震动
	 */
	public static void sendNotification(String headTitle,String contentTitle,String contentText ,boolean isVoice, boolean isVibration ) {
		Context context = GezitechApplication.getContext();
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(ns);
		int icon = R.drawable.launcher_96;
		CharSequence tickerText = headTitle;
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		
		//notification.defaults |= Notification.FLAG_NO_CLEAR;
		notification.flags = Notification.FLAG_AUTO_CANCEL;	
		if( isVoice ){
			//notification.defaults |= Notification.DEFAULT_SOUND; 
			
			AssetManager assetManager = context.getAssets();

	         File file = new File(Environment.getExternalStorageDirectory(),
	                 "/myRingtonFolder/Audio/");
	         if (!file.exists()) {
	             file.mkdirs();
	         }
	         File out = new File(file + "/", "soundmp3.mp3");
	         if (!out.exists()) {
	             try {
	                 copyAssetsToFilesystem(assetManager.open("soundmp3.mp3") , new FileOutputStream(out) );
	             } catch (FileNotFoundException e) {
	                 // TODO Auto-generated catch block
	                 e.printStackTrace();
	             } catch (IOException e) {
	                 // TODO Auto-generated catch block
	                 e.printStackTrace();
	             }
	         }

	        Uri urii = Uri.parse( out.getPath() );
	       // notification.sound=Uri.parse("android.resource://" + context.getPackageName() + "/" +R.raw.soundmp3); 
			notification.sound = urii; 
		}else{
			
		}
		//notification.defaults |= Notification.DEFAULT_VIBRATE; 
			
		if( isVibration ){
			long[] vibrate = {0,0,0,0}; 	
			vibrate[0] = 0;
			vibrate[1] = 100;
			vibrate[2] = 100;
			vibrate[3] = 100;
			notification.vibrate = vibrate ;
		 }	
		 Intent intent = new Intent(Intent.ACTION_MAIN);
	     intent.addCategory(Intent.CATEGORY_LAUNCHER);
	     intent.setClass(context, ZhuyeActivity.class);
	     intent.putExtra("flag", "session");
	     
	     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
	     PendingIntent contextIntent = PendingIntent.getActivity(context, 0, intent, 0);
		
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contextIntent);
		mNotificationManager.notify(1, notification);
		
		
	}
	public static boolean copyAssetsToFilesystem(InputStream istream, OutputStream ostream){  
        //Log.i(tag, "Copy "+assetsSrc+" to "+des);  
       // InputStream istream = null;  
        //OutputStream ostream = null;  
        try{  
            //AssetManager am = context.getAssets();  
            //istream = am.open(assetsSrc);  
            //ostream = new FileOutputStream(des);  
            byte[] buffer = new byte[1024];  
            int length;  
            while ((length = istream.read(buffer))>0){  
                ostream.write(buffer, 0, length);  
            }  
            istream.close();  
            ostream.close();  
        }  
        catch(Exception e){  
            e.printStackTrace();  
            try{  
                if(istream!=null)  
                    istream.close();  
                if(ostream!=null)  
                    ostream.close();  
            }  
            catch(Exception ee){  
                ee.printStackTrace();  
            }  
            return false;  
        }  
        return true;  
    }  
}
