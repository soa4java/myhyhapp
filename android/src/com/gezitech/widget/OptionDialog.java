package com.gezitech.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.gezitech.adapter.OptionAdapter;
import com.gezitech.adapter.OptionAdapter.OnClickDataPress;
import com.gezitech.contract.GezitechEntity_I;
import com.hyh.www.R;
import com.hyh.www.entity.Banktype;
import com.hyh.www.entity.City;
import com.hyh.www.entity.Companytype;
import com.hyh.www.entity.Country;
import com.hyh.www.entity.PubRange;
import com.hyh.www.entity.Releasescope;
import com.hyh.www.entity.Validtimelist;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author xiaobai
 * 2014-9-10
 * @todo( 弹出选择框 )
 */
public class OptionDialog extends Dialog implements  android.view.View.OnClickListener{
	private OptionDialog _this = this;
	private Context context = null;
	private ArrayList<GezitechEntity_I> list = null;
	private Dialog dialog;
	private TextView mTVTitle;
	private Button mButtonOK;
	private Button mButtonCancel;
	private ListViewNoScroll mListView;
	private HashMap<String, String> selectedList=new HashMap<String, String>();
	private DialogSelectDataCallBack dialogSelectDataCallBack;
	private OptionAdapter hobbyAdapter;
	private LinearLayout hobby_bottom;
	private ItemType companytype;
	private int exception = 0 ;
	private LinearLayout hobby_bottom_cannel;
	public OptionDialog(Context context, int theme, ArrayList<GezitechEntity_I> list, String title, HashMap<String, String> selectedList_, boolean isRadio, ItemType companytype ) {
		super(context, theme );
		_this.companytype = companytype;
		_init(context,list, title,selectedList_,isRadio );
	}
	public OptionDialog(Context context, int theme, ArrayList<GezitechEntity_I> list, String title, HashMap<String, String> selectedList_, boolean isRadio, ItemType companytype, int exception ) {
		super(context, theme );
		_this.companytype = companytype;
		_this.exception  = exception;
		_init(context,list, title,selectedList_,isRadio );
	}
	protected OptionDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		
	}
	/**
	 * 
	 * @param context 上下文this
	 * @param list 数据
	 * @param title 选择标题
	 * @param hStr 选中的名称 都是以逗号隔开
	 * @param hIds 选中的id 都是以逗号隔开
	 */
	public OptionDialog(Context context) {
		
		super(context);
	}
	
	private void _init(Context context, ArrayList<GezitechEntity_I> list, String title, HashMap<String, String> selectedList_,final boolean isRadio ){
		this.context = context;
		this.list  = list;
		_this.selectedList.clear();
		_this.selectedList.putAll(selectedList_);
		
		//添加布局
		setContentView(R.layout.option_dialog);
		
		mTVTitle=(TextView) findViewById(R.id.hobby_title);
		mTVTitle.setText( title );
		
		mButtonOK=(Button) findViewById(R.id.hobby_ok);
		mButtonOK.setOnClickListener(this);
		mButtonCancel=(Button) findViewById(R.id.hobby_cancel);
		mButtonCancel.setOnClickListener(this);
		
		hobby_bottom = (LinearLayout) findViewById( R.id.hobby_bottom );
		hobby_bottom_cannel = (LinearLayout) findViewById( R.id.hobby_bottom_cannel );
		if( isRadio ){ //单选
//			mTVTitle.setVisibility( View.GONE );
			hobby_bottom.setVisibility( View.GONE );
		}
		if( _this.exception == 1 ){
			
			hobby_bottom_cannel.setVisibility( View.VISIBLE );
			findViewById(R.id.hobby_cancel_2).setOnClickListener( new android.view.View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onButtonCancel();
				}
			});
		}
		
		
		mListView=(ListViewNoScroll) findViewById(R.id.list_hobby);
		
		hobbyAdapter = new OptionAdapter(context,selectedList_,list,isRadio, companytype, _this.exception );	
		mListView.setAdapter( hobbyAdapter );
		
		//选中回调
		hobbyAdapter.setOnClickDataPress( new OnClickDataPress() {
			
			@Override
			public void onDataPerss(GezitechEntity_I item) {
				if( item !=null ){
//					if( isRadio ){//单选
//						_this.selectedList.clear();
//						_this.selectedList.put(item.id+"", item.name);
//						onButtonOK();
//					}else{ //多选
//						Set<String> set=_this.selectedList.keySet();
//						if(_this.selectedList.containsKey(item.id+"")){
//							_this.selectedList.remove(item.id+"");
//						}else{
//							_this.selectedList.put(item.id+"",item.name);
//						}
//						Set<String> set1= _this.selectedList.keySet();
//					}
					
					if( isRadio ){//单选
						_this.selectedList.clear();
						if( ItemType.Companytype == companytype ){//企业类型  / 性别
							Companytype obj = (Companytype)item;
							_this.selectedList.put(obj.id+"", obj.typename);
						}else if( ItemType.Banktype == companytype  ){//银行列表
							Banktype obj = (Banktype)item;
							_this.selectedList.put(obj.id+"", obj.name);
						}else if( ItemType.Releasescope == companytype ){//发布范围列表
							Releasescope obj = (Releasescope)item;
							_this.selectedList.put(obj.id+"", obj.rangename );
						}else if( ItemType.Validtimelist == companytype ){//有效时间列表
							Validtimelist obj = (Validtimelist)item;
							_this.selectedList.put(obj.id+"", obj.activetimeName );
						}else if( ItemType.City == companytype ){//城市
							City obj = (City)item;
							_this.selectedList.put(obj.id+"", obj.name);
						}else if( ItemType.Country == companytype ){ //国家
							Country obj = (Country)item;
							_this.selectedList.put(obj.id+"", obj.rangename);
						}else if( ItemType.PubRange == companytype ){ //发布半径
							PubRange obj = (PubRange)item;
							
							_this.selectedList.put(obj.id+"", obj.rangetitle);

							if( _this.exception == 1 ){ //描述
								
								_this.selectedList.put("des", obj.description );
								
							}
						}
	
						onButtonOK();
						
					}else{
						_this.selectedList.clear();
						_this.selectedList.putAll(hobbyAdapter.selectedList);
					}
				}
			}
		});
		
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		this.getWindow().setLayout( (int)(dm.widthPixels-40*dm.density), LinearLayout.LayoutParams.WRAP_CONTENT );
		
		
		this.setCanceledOnTouchOutside( true );

		//关闭
		this.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				onButtonCancel();
			}
		});
		
		this.show();
	}
  
   
   public void onClick(View v) {
		switch (v.getId()) {
		case R.id.hobby_ok:
			onButtonOK();
			break;
		case R.id.hobby_cancel:
			onButtonCancel();
			break;
		}
	}
	private void onButtonCancel() {
		// TODO Auto-generated method stub
		this.cancel();
	}
	private void onButtonOK() {
		// TODO Auto-generated method stub
		dismiss();	
		this.dialogSelectDataCallBack.onDataCallBack(_this.selectedList);
	}
	/**
	 * 
	 * TODO(设置回调函数)
	 */
	public void setOnOKButtonListener(DialogSelectDataCallBack dialogSelectDataCallBack){
		this.dialogSelectDataCallBack = dialogSelectDataCallBack;
	}
	/**
	 * 
	 * @author xiaobai
	 * 2014-9-9
	 * @todo( 回调通信接口 )
	 */
	public interface DialogSelectDataCallBack{
		public void onDataCallBack(HashMap<String, String> selectedList);
	}
	public enum ItemType{Companytype,Banktype,Releasescope,Validtimelist,City,Country,PubRange};
}
