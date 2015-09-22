package com.gezitech.basic;


import com.hyh.www.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 统一弹出对话框
 * @author xiaobai
 */
public class GezitechAlertDialog {
	private static Dialog dialog;
	/**
	 * 加载等待框
	 * 
	 * @params  activity  上下文类对象
	 * @param	hint  提示文字
	 */
	public static void loadDialog(Context activity,String hint) {
		dialog = new Dialog(activity,R.style.dialog_load);
        dialog.setContentView(R.layout.alert_dialog_loading);
        TextView  tv_hint = (TextView) dialog.findViewById(R.id.tv_hint);
        if( hint.equals("") ){
        	tv_hint.setVisibility( View.GONE );
        }else{
        	tv_hint.setText( hint );
        	tv_hint.setVisibility( View.VISIBLE );
        }
        LinearLayout loading_bg = (LinearLayout) dialog.findViewById(R.id.loading_bg);
        loading_bg.getBackground().setAlpha( 200 );
        dialog.getWindow().setWindowAnimations( R.style.main_menu_animstyle );
        dialog.setCancelable( true );  
        //dialog.setCanceledOnTouchOutside(true);
        dialog.show();
	}
	/**
	 * TODO(当没有提示的信息的时候用系统默认的)
	 */
	public static void loadDialog(Context activity) {
		loadDialog(activity,"");
	}
	/**
	 * 关闭加载等待框
	 */
	public static void closeDialog(){
		if( dialog!=null && dialog.isShowing() ){
			dialog.dismiss();
		}
	}
	/**
	 * 选择处理框
	 * @param activity
	 */
	public static String title="出错",msg="哦，好像出错了，先放松一下吧",confirm="重试",cancel="取消";
	private static Dialog dialogReset;
	public static void show( final Activity activity, final OnSelectClickListener onSelectClickListener) {
		if (activity != null && !activity.isFinishing()) {
			Builder dialogs = new AlertDialog.Builder(activity)
					.setTitle(title)
					.setMessage(msg)
					.setPositiveButton(confirm,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									onSelectClickListener.onClickPositiveButton();
								}
							})
					.setNegativeButton(cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									onSelectClickListener.onClickNegativeButton();
								}
							}); 
			if (activity != null && !activity.isFinishing()) {
				dialogs.show();
			}
		}
	}
	//确定
	public interface OnSelectClickListenerBase {
		void onClickPositiveButton();//确定	
	}
	//继承
	public interface OnSelectClickListener extends OnSelectClickListenerBase{
		void onClickNegativeButton();//取消
	}
	/**
	 * 
	 * TODO( 网络错误或者加载出错 出现重新加载的按钮界面   )
	 */
	public static  void showResetDialog(Context activity, final OnSelectClickListenerBase onSelectClickListenerBase ){
		dialogReset = new Dialog(activity,R.style.dialog_load1);
		dialogReset.setContentView(R.layout.alert_dialog_reset);
        ImageView iv_reset = (ImageView) dialogReset.findViewById(R.id.iv_reset);
        iv_reset.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onSelectClickListenerBase.onClickPositiveButton();
			}
		});
        dialogReset.setCancelable(false);  
        //dialog.setCanceledOnTouchOutside(true);
        dialogReset.show();
	}
	/**
	 * 
	 * TODO( 关闭 )
	 */
	public static void closeResetDialog(){
		if( dialogReset.isShowing() ){
			dialogReset.dismiss();
		}
	}
}
