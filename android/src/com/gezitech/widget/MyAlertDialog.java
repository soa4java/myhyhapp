package com.gezitech.widget;


import com.hyh.www.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
/**
 * 
 * @author xiaobai
 * 2014-5-9
 * @todo(  自定义的alert 弹出框  )
 */
public class MyAlertDialog extends AlertDialog{
	private Context context;
	private android.app.AlertDialog ad;
	private TextView titleView;
	private TextView messageView;
	private MyAlertDialog _this = this;
	private Button bt_confirm;
	private Button bt_cancel;
	public MyAlertDialog(Context context, int theme) {
		super(context, theme);
	}
	public MyAlertDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}
	public MyAlertDialog(Context context) {
		super(context);
		_this.context = context;
		ad=new android.app.AlertDialog.Builder(context).create();
		ad.setCanceledOnTouchOutside( true );
		ad.show();
		//关键在下面的两行,使用window.setContentView,替换整个对话框窗口的布局
		Window window = ad.getWindow();
		window.setContentView(R.layout.my_alert_dialog);	
		titleView=(TextView)window.findViewById(R.id.tv_title);
		messageView=(TextView)window.findViewById(R.id.tv_msg);
		bt_confirm = (Button)window.findViewById(R.id.bt_confirm);
		bt_cancel = (Button)window.findViewById(R.id.bt_cancel);
	}
	public MyAlertDialog setTitle(String title) {
		titleView.setText(title);
		return this;
	}
	public MyAlertDialog setMessage(String message)
	{
		messageView.setVisibility( View.VISIBLE );
		messageView.setText(message);
		return this;
	}
	/**
	 * 设置按钮
	 * @param text
	 * @param listener
	 */
	public MyAlertDialog setPositiveButton(String text,final View.OnClickListener listener)
	{
		bt_confirm.setText( text );
		bt_confirm.setOnClickListener(listener);
		return this;
	}
 
	/**
	 * 设置按钮
	 * @param text
	 * @param listener
	 */
	public MyAlertDialog setNegativeButton(String text,final View.OnClickListener listener)
	{
		bt_cancel.setText( text );
		bt_cancel.setOnClickListener(listener);
		return this;
	}
	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		ad.dismiss();
	}
}
