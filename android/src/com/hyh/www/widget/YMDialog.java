package com.hyh.www.widget;


import com.hyh.www.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
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
public class YMDialog{
	private Context context;
	private YMDialog _this = this;
	private Dialog dialog;
	private TextView tv_hint_msg;
	private TextView tv_cancel2;
	private TextView tv_confim;
	public YMDialog( Context context ) {
		dialog = new Dialog(context,R.style.dialog_load1);
        dialog.setContentView(R.layout.activity_ym_dialog);

        tv_hint_msg = (TextView) dialog.findViewById( R.id.tv_hint_msg );
        tv_cancel2 = (TextView) dialog.findViewById( R.id.tv_cancel2 );
        tv_confim = (TextView) dialog.findViewById( R.id.tv_confim ); 
        dialog.setCancelable(false);
      //  dialog.setCanceledOnTouchOutside( true );
        dialog.show();
        
	}
	public YMDialog setHintMsg(String str) {
		tv_hint_msg.setText(str);
		if( str.length() >15 ){
			tv_hint_msg.setGravity( Gravity.LEFT|Gravity.CENTER_VERTICAL );
		}
		return this;
	}
	/**
	 * 关闭
	 * @param text
	 * @param listener
	 */
	public YMDialog setCloseButton(final View.OnClickListener listener)
	{
		tv_cancel2.setOnClickListener(listener);
		return this;
	}
	public YMDialog setCloseButton(String str , final View.OnClickListener listener)
	{
		tv_cancel2.setOnClickListener(listener);
		tv_cancel2.setText( str );
		return this;
	}
	/**
	 * 确定
	 * @param text
	 * @param listener
	 */
	public YMDialog setConfigButton(final View.OnClickListener listener)
	{
		tv_confim.setOnClickListener(listener);
		return this;
	}
	public YMDialog setConfigButton(String str ,  final View.OnClickListener listener)
	{
		tv_confim.setOnClickListener( listener );
		tv_confim.setText( str );
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
