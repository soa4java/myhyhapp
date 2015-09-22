package com.hyh.www.widget;


import com.hyh.www.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 
 * @author xiaobai
 * 2014-5-9
 * @todo(  自定义的alert 弹出框  )
 */
public class YMDialog2{
	private Context context;
	private YMDialog2 _this = this;
	private Dialog dialog;
	private TextView tv_hint_msg;
	private TextView tv_cancel2;
	private TextView tv_get_credits2;
	private TextView tv_fabuchenggong;
	private TextView tv_content;
	private Button Login_login;
	public YMDialog2( Context context ) {
		dialog = new Dialog(context,R.style.dialog_load1);
        dialog.setContentView(R.layout.releasehyh_text_dialog );

        tv_fabuchenggong = (TextView) dialog.findViewById( R.id.tv_fabuchenggong );
        tv_content = (TextView) dialog.findViewById( R.id.tv_content );
        Login_login = (Button) dialog.findViewById( R.id.Login_login ); 
        dialog.setCancelable(false);
        dialog.show();
        
	}
	public YMDialog2 setHead( String str ){
		tv_fabuchenggong.setText(str);
		return this;
	}
	public YMDialog2 setHintMsg(String str) {
		tv_content.setText(str);
		return this;
	}
	public YMDialog2 setConfigText(String str) {
		Login_login.setText(str);
		return this;
	} 
	/**
	 * 关闭
	 * @param text
	 * @param listener
	 */
	public YMDialog2 setCloseButton(final View.OnClickListener listener)
	{
		Login_login.setOnClickListener(listener);
		return this;
	}
	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		if( dialog.isShowing() ){
			dialog.dismiss();
		}
	}
}
