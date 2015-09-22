package com.hyh.www.widget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.service.managers.UserManager;
import com.gezitech.util.ToastMakeText;
import com.hyh.www.entity.Friend;
import com.hyh.www.user.PersonDetailedInformationActivity;
/**
 * 
 * @author xiaobai
 * 2014-11-25
 * @todo( activity 中的公用跳转函数  )
 */
public class ActivityCommon {
		public static  void lookFriendData(long fid,final Activity activity,final Fragment fragment ) {
			// TODO Auto-generated method stub
						GezitechAlertDialog.loadDialog( activity );
						UserManager.getInstance().getfrienddata(fid,  new OnAsynGetOneListener() {
							
							@Override
							public void OnAsynRequestFail(String errorCode, String errorMsg) {
								// TODO Auto-generated method stub
								GezitechAlertDialog.closeDialog();
								new ToastMakeText( activity ).Toast( errorMsg );
							}
							
							@Override
							public void OnGetOneDone(GezitechEntity_I entity) {
								// TODO Auto-generated method stub
								GezitechAlertDialog.closeDialog();
								Intent intent = new Intent(activity, PersonDetailedInformationActivity.class ) ;
								Bundle bundle = new Bundle();
								bundle.putSerializable("friendinfo", (Friend)entity );
								intent.putExtras( bundle );
								if( fragment !=null ){
									fragment.startActivityForResult(intent, 2001);	
								}else{
									activity.startActivityForResult(intent, 2001);
								}
								
							}
						});
						
		}
		//查看资料
		public static  void lookFriendData(long fid,final Activity activity) {
			
			lookFriendData( fid , activity, null );
			
		}
}
