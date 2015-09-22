package com.gezitech.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.service.BackgroundService;
import com.gezitech.service.GezitechService;
import com.gezitech.util.StringUtil;
import com.hyh.www.R;
/**
 * 模拟对话框，其它地方的对话框建议在这里扩展，主要用于在服务中弹出对话框
 * @author Administrator
 *
 */
public class CommonDialog extends GezitechActivity implements View.OnClickListener {
	public static final String CONFIGKEY = "CONFIGKEY";
	private DialogInfo dc;
	private RelativeLayout llContent;
	private TextView txtTitle, txtContent;
	private Button btn1, btn2, btn3;
	public static final String DIALOG_INFO_KEY = "DIALOG_INFO_KEY";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_dialog);
		llContent = (RelativeLayout)findViewById(R.id.llContent);
		txtTitle = (TextView)findViewById(R.id.txt_title);
		txtContent = (TextView)findViewById(R.id.txtContent);
		btn1 = (Button)findViewById(R.id.button1);
		btn2 = (Button)findViewById(R.id.button2);
		btn3 = (Button)findViewById(R.id.button3);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		init();
	}
	protected void init(){
		dc = getDialogInfo();
		if(dc == null)return;
		if(dc.onCreate != null){
			
			dc.onCreate.onCreate(llContent);
			
		}else if(!StringUtil.isEmpty(dc.contentText)){
			
			txtContent.setVisibility(View.VISIBLE);
			txtContent.setText(dc.contentText);
			
		}
		if(!StringUtil.isEmpty(dc.title))txtTitle.setText(dc.title);
		if(StringUtil.isEmpty(dc.button1Text))btn1.setVisibility(View.GONE);
		else btn1.setText(dc.button1Text);
		if(StringUtil.isEmpty(dc.button2Text))btn2.setVisibility(View.GONE);
		else btn2.setText(dc.button2Text);
		if(StringUtil.isEmpty(dc.button3Text))btn3.setVisibility(View.GONE);
		else btn3.setText(dc.button3Text);
	}
	@Override
	public void onClick(View v) {
		if(dc == null)return;
		if(dc.onClick == null)return;
		int i = 0;
		if(v == btn2)i = 1;
		else if(v == btn3)i = 2;
		dc.onClick.onClick(i, v, this);
	}
	/**
	 * 获取对话框对象
	 * @return
	 */
	private DialogInfo getDialogInfo(){
		Intent intent = getIntent();
		String key = intent.getStringExtra(DIALOG_INFO_KEY);
		BackgroundService service = null;//GezitechService.getInstance().bindBackgroundService(this);
		if(service == null)return null;
		DialogInfo di = (DialogInfo)service.getStorageData(key);
		Log.i("读取信息的时候", di==null?"空":di.toString());
		return di;
	}

	public static class DialogInfo {
		public String title;
		public String button1Text;
		public String button2Text;
		public String button3Text;
		public String contentText;
		
		public onCreateContent onCreate;
		public onButtonClick onClick;
		
		public void setOnCreate(onCreateContent createListener){
			this.onCreate = createListener;
		}
		public void setOnButtonClick(onButtonClick clickListener){
			this.onClick = clickListener;
		}
	}
	public interface onCreateContent{
		public void onCreate(RelativeLayout parent);
	}
	public interface onButtonClick{
		public void onClick(int buttonIndex, View button, Activity activity);
	}
}
