package com.hyh.www.nearby;

import org.json.JSONObject;

import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.contract.GezitechManager_I.OnAsynGetJsonObjectListener;
import com.gezitech.service.managers.NearManager;
import com.hyh.www.nearby.CommentBoxDialog.CommentListener;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.widget.Toast;

public class NearUtils {

	//喜欢的交互
	public static void likeSubmit(final Activity activity, RequestParams params , final CommentListener commentListener ){
		//GezitechAlertDialog.loadDialog(activity);
		NearManager.getInstance().addNearbyLike(params,  new OnAsynGetJsonObjectListener() {
			
			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				// TODO Auto-generated method stub
				//GezitechAlertDialog.closeDialog();
				android.widget.Toast.makeText( activity, errorMsg , Toast.LENGTH_SHORT).show();
			
			}
			@Override
			public void OnGetJSONObjectDone(JSONObject jo) {
				//GezitechAlertDialog.closeDialog();
				commentListener.setCommentCallBack(jo);
			}
		});
		
		
	}
	
}
