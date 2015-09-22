package com.hyh.www.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetListListener;
import com.gezitech.contract.GezitechManager_I.OnAsynUpdateListener;
import com.gezitech.service.managers.AccountManager;
import com.gezitech.service.managers.SystemManager;
import com.gezitech.util.StringUtil;
import com.gezitech.widget.OptionDialog;
import com.gezitech.widget.OptionDialog.DialogSelectDataCallBack;
import com.gezitech.widget.OptionDialog.ItemType;
import com.hyh.www.R;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.style.BackgroundColorSpan;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author xiaobai (提现)
 */
public class WithdrawalActivity extends GezitechActivity implements OnClickListener {

	private WithdrawalActivity _this = this;
	private Button bt_my_post;
	private Button bt_home_msg;
	private RelativeLayout withdrawal_bank;
	private HashMap<String, String> typeList = new HashMap<String, String>();
	public RequestParams params = new RequestParams();
	private TextView tv_withdrawal_bankchoose;
	private SharedPreferences sp;
	private String bankname; //开户行名称
	private String bank; //提现银行
	private String bankaccount; //银行帐号
	private String accountname; //账户姓名
	private EditText ed_kaihuhang;
	private EditText ed_bank_number;
	private EditText ed_zhanghu_name;
	private EditText ed_tixian_jine;
	private int bankid;
	private Button Withdrawal_querentixian;
	private double cash;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_withdrawal);
		sp = _this.getSharedPreferences("tixiandata", 0);
		bankname = sp.getString("bankname", "");
		bank = sp.getString("bank", "");
		bankid = sp.getInt("bankid", 0);
		bankaccount = sp.getString("bankaccount", "");
		accountname = sp.getString("accountname", "");
		cash = _this.getIntent().getDoubleExtra("cash", 0);
		_init();
	}

	private void _init() {
		bt_my_post = (Button) _this.findViewById(R.id.bt_my_post);
		bt_my_post.setVisibility(View.GONE);

		bt_home_msg = (Button) _this.findViewById(R.id.bt_home_msg);
		bt_home_msg.setBackgroundResource(R.drawable.button_common_back);
		bt_home_msg.setOnClickListener(this);

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText(_this.getResources().getString(R.string.tixian));
		
		withdrawal_bank = (RelativeLayout) _this.findViewById( R.id.withdrawal_bank );
		withdrawal_bank.setOnClickListener( this );
		tv_withdrawal_bankchoose = (TextView) _this.findViewById( R.id.tv_withdrawal_bankchoose );
		
		
		ed_kaihuhang = (EditText) _this.findViewById( R.id.ed_kaihuhang );
		ed_bank_number = (EditText) _this.findViewById( R.id.ed_bank_number );
		ed_zhanghu_name = (EditText) _this.findViewById( R.id.ed_zhanghu_name );
		ed_tixian_jine = (EditText) _this.findViewById( R.id.ed_tixian_jine );
		
		//初始化值
		tv_withdrawal_bankchoose.setText( bank.equals("") ? "请选择" : bank );
		ed_kaihuhang.setText( bankname  );
		ed_bank_number.setText( bankaccount );
		ed_zhanghu_name.setText( accountname );
		
		if( !bank.equals("") ){
			//初始化提现银行
			typeList.put(bankid+"", bank );
		}
		
		Withdrawal_querentixian = (Button) _this.findViewById( R.id.Withdrawal_querentixian );
		Withdrawal_querentixian.setOnClickListener( this );
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bt_home_msg:
			
			finish();
			break;
		case R.id.withdrawal_bank:
			GezitechAlertDialog.loadDialog(_this);
			AccountManager.getInstance().getaccountlist ( new OnAsynGetListListener() {	
				@Override
				public void OnAsynRequestFail(String errorCode, String errorMsg) {
					GezitechAlertDialog.closeDialog();
					Toast( errorMsg );
				}
				@Override
				public void OnGetListDone(ArrayList<GezitechEntity_I> list) {
					GezitechAlertDialog.closeDialog();
					if( list != null && list.size()>0 ){
						typeDialog( list );
					}else{
						Toast("没有数据");
					}
				}
			});
			break;
		case R.id.Withdrawal_querentixian:
			_submitData();
			break;
		default:
			break;
		}
	}
	//提交数据
	private void _submitData() {
		
		bankname = ed_kaihuhang.getText().toString().trim();
		if( bankname.equals("") ){
			Toast("开户行不能为空");
			return;
		}
		bankaccount = ed_bank_number.getText().toString().trim();
		if( bankaccount.equals("") ) {
			Toast("银行卡号不能为空");
			return;
		}
		accountname = ed_zhanghu_name.getText().toString().trim();
		if( accountname.equals("") ){
			Toast("账户姓名不能为空");
			return;
		}
		
		bank = tv_withdrawal_bankchoose.getText().toString().trim();
		if( bank.equals("") || bank.equals("请选择") ) {
			Toast("提现银行没有选择");
			return;
		}
		
		double money;
		try{
			money = Double.parseDouble( ed_tixian_jine.getText().toString().trim() );
		}catch( Exception e){
			money = 0.00;
		}
		if( money <= 0 ){
			Toast("提现金额未填写");
			return;
		}
		//判断提现金额 是否大于现在可提现金额
		if( money > cash ){
			Toast("可提现余额不足");
			return;
		}
		

		Editor ed = sp.edit();
		ed.putString("bankname", bankname);
		ed.putString("bank", bank);
		ed.putInt("bankid", bankid);
		ed.putString("bankaccount", bankaccount);
		ed.putString("accountname", accountname );
		ed.commit();
		GezitechAlertDialog.loadDialog( this );
		AccountManager.getInstance().submitcash(bank,bankname,bankaccount,accountname,money, new OnAsynUpdateListener(){

			@Override
			public void OnAsynRequestFail(String errorCode, String errorMsg) {
				// TODO Auto-generated method stub
				GezitechAlertDialog.closeDialog();
				Toast( errorMsg );
			}

			@Override
			public void onUpdateDone(String id) {
				// TODO Auto-generated method stub
				GezitechAlertDialog.closeDialog();
				Toast( id );
				_this.finish();
			}		
		});
	}
	//弹出提现银行选择框
	public void typeDialog( ArrayList<GezitechEntity_I> list ){
		OptionDialog optionDialog = new OptionDialog(_this,
				R.style.dialog_load1, list, "提现银行选择", typeList,
				true, ItemType.Banktype );
		optionDialog.setOnOKButtonListener(new DialogSelectDataCallBack() {

			@Override
			public void onDataCallBack(HashMap<String, String> selectedList) {
				_this.typeList = selectedList;
				Set<String> a = (Set<String>) selectedList.keySet();
				String[] keyArray = (String[]) a.toArray(new String[] {});
				if (keyArray.length >= 1) {
					bankid = Integer.parseInt(keyArray[0]);
				}
				
				if (selectedList.size() == 0) {
					tv_withdrawal_bankchoose.setText("请选择");
				} else {
					Collection<String> b = selectedList.values();
					String[] strArray = (String[]) b.toArray(new String[] {});
					tv_withdrawal_bankchoose.setText(StringUtil.stringArrayJoin(strArray, ",") );
				}
			}
		});
	}
}
