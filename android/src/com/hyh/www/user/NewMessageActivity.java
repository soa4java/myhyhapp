package com.hyh.www.user;

import java.util.Calendar;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechApplication;
import com.hyh.www.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
/**
 * 
 * @author xiaobai (新消息提醒)
 */
public class NewMessageActivity extends GezitechActivity implements OnClickListener {
	
	private NewMessageActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private TextView tv_title;
	private CheckBox Check_receive_notifications;
	private CheckBox Check_voice;
	private CheckBox Check_vibration;
	private CheckBox Check_disturb;
	private TextView text_start_time;
	private TextView text_end_time;
	private Editor edit;
	private LinearLayout ll_time_box;
	private final static int TIME_DIALOG_START = 0;
    private final static int TIME_DIALOG_END = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_message);
		edit = GezitechApplication.systemSp.edit();
		_init(); 
	}

	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

	    tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(_this.getResources().getString(R.string.xinxiaoxitixing));
		
		Check_receive_notifications = (CheckBox) findViewById( R.id.Check_receive_notifications );
		Check_voice = (CheckBox) findViewById( R.id.Check_voice );
		Check_vibration = ( CheckBox ) findViewById( R.id.Check_vibration );
		Check_disturb = ( CheckBox )findViewById( R.id.Check_disturb );
		
		//开始时间
		text_start_time = (TextView) findViewById( R.id.text_start_time );
		//结束时间
		text_end_time = (TextView) findViewById( R.id.text_end_time );
		
		ll_time_box = (LinearLayout) findViewById( R.id.ll_time_box );
		
		//接受通知
		int receive_notifications = GezitechApplication.systemSp.getInt("receive_notifications", 1 );
		if( receive_notifications > 0 ){
			Check_receive_notifications.setChecked( true );
		}else{
			Check_receive_notifications.setChecked( false );
		}
		Check_receive_notifications.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if( isChecked ){
					edit.putInt("receive_notifications", 1 );
				}else{
					edit.putInt("receive_notifications", 0 );
				}
				edit.commit();
			}
		});
		//声音
		int voice = GezitechApplication.systemSp.getInt("voice", 1 );
		if( voice > 0 ){
			Check_voice.setChecked( true );
		}else{
			Check_voice.setChecked( false );
		}
		Check_voice.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if( isChecked ){
					edit.putInt("voice", 1 );
				}else{
					edit.putInt("voice", 0 );
				}
				edit.commit();
			}
		});
		//震动
		int vibration = GezitechApplication.systemSp.getInt("vibration", 1 );
		if( vibration > 0 ){
			Check_vibration.setChecked( true );
		}else{
			Check_vibration.setChecked( false );
		}
		Check_vibration.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if( isChecked ){
					edit.putInt("vibration", 1 );
				}else{
					edit.putInt("vibration", 0 );
				}
				edit.commit();
			}
		});
		//面打扰时间
		int disturb = GezitechApplication.systemSp.getInt("disturb", 0 );
		if( disturb > 0 ){
			Check_disturb.setChecked( true );
			ll_time_box.setVisibility( View.VISIBLE );
			String start_time = GezitechApplication.systemSp.getString("start_time", "22:00" );
			String end_time = GezitechApplication.systemSp.getString("end_time", "08:00" );					
			text_start_time.setText( start_time );
			text_end_time.setText( end_time );
		}else{
			Check_disturb.setChecked( false );
			ll_time_box.setVisibility( View.GONE  );
			
		}
		Check_disturb.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if( isChecked ){

					ll_time_box.setVisibility( View.VISIBLE );
					
					String start_time = GezitechApplication.systemSp.getString("start_time", "22:00" );
					String end_time = GezitechApplication.systemSp.getString("end_time", "08:00" );					
					text_start_time.setText( start_time );
					text_end_time.setText( end_time );
					edit.putInt("disturb", 1 );
				}else{
					ll_time_box.setVisibility( View.GONE );
					edit.putInt("disturb", 0 );
				}
				edit.commit();
			}
		});
		
		//设置开始时间
		text_start_time.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				_this.showDialog( TIME_DIALOG_START );
			}
		});
		//设置结束时间
		text_end_time.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				_this.showDialog( TIME_DIALOG_END );
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bt_home_msg:
			finish();
			break;

		default:
			break;
		}
	}
	/**
     * 创建日期及时间选择对话框
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
        	case TIME_DIALOG_START://开始时间
	            String start_time = GezitechApplication.systemSp.getString("start_time", "22:00" );
	            String[] start_time_arr = start_time.split(":");
	            dialog=new TimePickerDialog(
	                this,new TimePickerDialog.OnTimeSetListener(){
	                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	                    	String start_time_t = ( hourOfDay/10 <= 0 ? "0"+hourOfDay : hourOfDay )  +":"+ ( minute/10 <= 0 ? "0"+minute : minute );
	                    	text_start_time.setText(  start_time_t  );
	    					edit.putString("start_time", start_time_t );
	    					edit.commit();
	                    }
	                },Integer.parseInt( start_time_arr[0] ), Integer.parseInt( start_time_arr[1] ) ,true
	            );
	            break;
        	case TIME_DIALOG_END://结束时间
        		String end_time = GezitechApplication.systemSp.getString("end_time", "08:00" );
	            String[] end_time_arr = end_time.split(":");
	            dialog=new TimePickerDialog(
	                this, 
	                new TimePickerDialog.OnTimeSetListener(){
	                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	                    	String end_time_t = ( hourOfDay/10 <= 0 ? "0"+hourOfDay : hourOfDay )  +":"+ ( minute/10 <= 0 ? "0"+minute : minute );
	                    	text_end_time.setText(  end_time_t  );
	    					edit.putString("end_time", end_time_t );
	    					edit.commit();
	                    }
	                },
	                Integer.parseInt( end_time_arr[0] ), Integer.parseInt( end_time_arr[1] ) ,
	                true
	            );
	            break;
        }
        return dialog;
    }
}
