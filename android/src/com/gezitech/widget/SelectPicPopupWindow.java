package com.gezitech.widget;


import com.gezitech.util.DimensionUtility;
import com.hyh.www.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
/***
 * 
 * @author xiaobai
 * 2014-11-12
 * @todo( 弹出底部滑出框 )
 */
public class SelectPicPopupWindow extends Activity implements OnClickListener{

	private Button btn_take_photo, btn_pick_photo, btn_cancel;
	private LinearLayout layout;
	private LinearLayout pop_layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_dialog_bottom_pupwindow);
		btn_take_photo = (Button) this.findViewById(R.id.btn_take_photo);
		btn_pick_photo = (Button) this.findViewById(R.id.btn_pick_photo);
		btn_cancel = (Button) this.findViewById(R.id.btn_cancel);
		DisplayMetrics dm = this.getResources().getDisplayMetrics();
		this.getWindow().setLayout( dm.widthPixels , dm.heightPixels );
		btn_cancel.setOnClickListener(this);
		btn_pick_photo.setOnClickListener(this);
		btn_take_photo.setOnClickListener(this);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		overridePendingTransition(R.anim.in_anim, R.anim.in_from_down );
		return true;
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_take_photo://拍照
			returnData( "10001" );
			break;
		case R.id.btn_pick_photo:		//从相册获取		
			returnData( "10002" );
			break;
		case R.id.btn_cancel:				//取消
			break;
		default:
			break;
		}
		finish();
		overridePendingTransition(R.anim.in_anim, R.anim.in_from_down );
	}
	public void returnData( String action ){
		Intent intent = new Intent();
		intent.setAction( action );
		this.setResult( 10002, intent );
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.in_anim, R.anim.in_from_down );
	}
}
