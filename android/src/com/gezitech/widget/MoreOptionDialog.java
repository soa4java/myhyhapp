package com.gezitech.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gezitech.adapter.MoreOptionAdapter;
import com.gezitech.adapter.OptionAdapter;
import com.gezitech.adapter.OptionAdapter.OnClickDataPress;
import com.gezitech.contract.GezitechEntity_I;
import com.hyh.www.R;
import com.hyh.www.entity.Banktype;
import com.hyh.www.entity.Companytype;
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
 * @todo( 多选弹出选择框 )
 */
public class MoreOptionDialog extends Dialog{
	private MoreOptionDialog _this = this;
	private Context context = null;
	private ArrayList<GezitechEntity_I> list = null;
	private Dialog dialog;
	private TextView mTVTitle;
	private ListView mListView;
	private Companytype selectedList ;
	private DialogSelectDataCallBack dialogSelectDataCallBack;
	private MoreOptionAdapter hobbyAdapter;
	private ListView mlist_child;
	public MoreOptionDialog(Context context, int theme, ArrayList<GezitechEntity_I> list, String title, Companytype selectedList_ ) {
		super(context, theme );
		_init(context,list, title,selectedList_ );
	}

	protected MoreOptionDialog(Context context, boolean cancelable,
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
	public MoreOptionDialog(Context context) {
		
		super(context);
	}
	
	private void _init(Context context, ArrayList<GezitechEntity_I> list, String title, Companytype selectedList_){
		this.context = context;
		this.list  = list;
		_this.selectedList = selectedList_;
		
		//添加布局
		setContentView(R.layout.more_option_dialog);
		
		mTVTitle=(TextView) findViewById(R.id.hobby_title);
		mTVTitle.setText( title );
		

		
		mListView = (ListView) findViewById(R.id.list_hobby);
		mlist_child = (ListView) findViewById(R.id.list_child);
		hobbyAdapter = new MoreOptionAdapter(context, selectedList_, list ,1 );	
		mListView.setAdapter( hobbyAdapter );
		//判断是否要计算pid
		if( selectedList != null && selectedList.pid <= -1  ){
			
			for( int i = 0 ; i< list.size(); i++){
				Companytype item = ( Companytype ) list.get(i);
					if( item.id == selectedList.id  ){
						selectedList.pid = 0;
						break;
					}else if( item.childtype!=null && item.childtype.length()>0 ){
						for( int j = 0 ; j< item.childtype.length() ; j++ ){
							JSONObject jo;
							try {
								jo = item.childtype.getJSONObject(j);
								Companytype ct = new Companytype(jo);
								
								if( ct.id ==  selectedList.id ){
									
									selectedList.pid = ct.pid;
									break;
								}
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
						if(  selectedList.pid>=0 ) break;
					}
				}
			
		}
		
		
		
		if( selectedList != null && selectedList.pid> 0 ){//已经选择  是二级
			showChildAdapter( selectedList);
		}
		
		
		//选中回调
		hobbyAdapter.setOnClickDataPress( new MoreOptionAdapter.OnClickDataPress() {
			
			@Override
			public void onDataPerss(Companytype item) {
				if( item !=null ){
						if( item.pid == 0 && item.childtype !=null && item.childtype.length() > 0 ){
							
							showChildAdapter( item );
							
						}else{
							
							mlist_child.setVisibility( View.GONE );
							_this.selectedList = item ;
							onButtonOK();
							
						}
				}
			}
		});
		
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		this.getWindow().setLayout( (int)(dm.widthPixels-40*dm.density), (int)(dm.heightPixels-120*dm.density) );
		
		
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
  
	public void showChildAdapter(Companytype selectedList2 ){
		ArrayList<GezitechEntity_I> childList = new ArrayList<GezitechEntity_I>();
		mlist_child.setVisibility( View.VISIBLE );
		for( int i = 0 ; i< list.size(); i++){
			Companytype item = ( Companytype ) list.get(i);
			if( selectedList2 != null   ){
				
				if( ( item.id == selectedList2.id ||  item.id == selectedList2.pid )  &&  item.childtype != null &&  item.childtype.length()>0 ){
					for( int j = 0 ; j< item.childtype.length() ; j++ )
					{
						JSONObject jo;
						try {
							jo = item.childtype.getJSONObject(j);
							Companytype ct = new Companytype(jo);
							ct.childtype = jo.has("childtype") ? jo.getJSONArray("childtype") : null;
							childList.add( ct );
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
					break;
					
				}
			}
			
		}
		
		MoreOptionAdapter childAdapter = new MoreOptionAdapter(context, selectedList2, childList , 2 );	
		
		mlist_child.setAdapter( childAdapter );
		
		//选中回调
		childAdapter.setOnClickDataPress(new MoreOptionAdapter.OnClickDataPress() {

			@Override
			public void onDataPerss(Companytype item) {
				if (item != null) {
					_this.selectedList = item;
					onButtonOK();
				}
			}
		});
		
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
		public void onDataCallBack(GezitechEntity_I selectedList);
	}
}
