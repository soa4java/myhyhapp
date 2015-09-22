package com.hyh.www;

import java.io.IOException;

import com.gezitech.basic.GezitechApplication;
import com.gezitech.contract.GezitechManager_I.OnAsynInsertListener;
import com.gezitech.service.managers.SystemManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;

/**
 * 
 * @author xiaobai
 * 2015-1-12
 * @todo( 系统消息更新的提示  )
 */
public class NewsHint {
	public void getNewsUpdate( final Context context, final View view){
		getNewsUpdate(context, view , false );
	}
	
	//获取更新
	public void getNewsUpdate( final Context context, final View view, boolean isLoad ){
		final SharedPreferences systemSetting = context.getSharedPreferences("systemSetting", 0);
		long ctime = systemSetting.getLong("ctime", 0 );
		final long hintCount = systemSetting.getLong("hintCount", 0 );
		if(  hintCount> 0  ){
			view.setBackgroundResource(R.drawable.common_msg_yes );
		}else{
			view.setBackgroundResource(R.drawable.common_msg);
		}
		final long currTime = System.currentTimeMillis()/1000;
		
		if( ( currTime - ctime > 24*3600*100 ) || isLoad ){
			
			SystemManager.getInstance().getNewsUpdate(ctime,  new OnAsynInsertListener() {
				
				@Override
				public void OnAsynRequestFail(String errorCode, String errorMsg) {
				}
				@Override
				public void onInsertDone(String id) {
					
					if( !id.equals("0") ){
						view.setBackgroundResource(R.drawable.common_msg_yes );
						
						final MediaPlayer mediaPlayerObj = MediaPlayer.create(context, R.raw.soundmp3);
						/*mediaPlayerObj.setLooping( false );
						mediaPlayerObj.prepare();*/
						mediaPlayerObj.start();
						
					}
					Editor edit = systemSetting.edit();
					edit.putLong("ctime",  currTime );
					edit.putLong("hintCount",  hintCount+(Integer.valueOf( id ) ) );
					edit.commit();
				}
			});
		}
	}
	//点击更新 去掉红点

	public void setNewsUpdate( Context context, final View view ){
		final SharedPreferences systemSetting = context.getSharedPreferences("systemSetting", 0);

		view.setBackgroundResource(R.drawable.common_msg );
		Editor edit = systemSetting.edit();
		edit.putLong("hintCount",  0 );
		edit.commit();
		
		
	}
	
	
}
