package com.gezitech.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.widget.OptionDialog.ItemType;
import com.hyh.www.R;
import com.hyh.www.entity.Banktype;
import com.hyh.www.entity.City;
import com.hyh.www.entity.Companytype;
import com.hyh.www.entity.Country;
import com.hyh.www.entity.PubRange;
import com.hyh.www.entity.Releasescope;
import com.hyh.www.entity.Validtimelist;


import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
/**
 * 
 * @author xiaobai
 * 2014-9-9
 * @todo( 多选和单选适配器  )
 */
public class OptionAdapter extends BaseAdapter{
	private OptionAdapter _this = this;
    private Context mcontext;
    private ArrayList<GezitechEntity_I> mlist= new ArrayList<GezitechEntity_I>();
    private LayoutInflater mInflater;
    public HashMap<String, String> selectedList=new HashMap<String, String>();;
	private OnClickDataPress onClickDataPress;
	private boolean isRadio = false;
	private ItemType type ;
	private int exception = 0;
	public OptionAdapter(Context context, HashMap<String, String> selectedList_, ArrayList<GezitechEntity_I> list ,boolean isRadio, ItemType type , int exception ){
		mcontext = context;
		mInflater	= LayoutInflater.from( context );
		_this.selectedList.clear();
		_this.mlist.clear();
		_this.mlist.addAll( list );
		if(selectedList_!=null&&!selectedList_.equals("")&&selectedList_.size()>0){ //如果选中的不为空 则分为数组
			_this.selectedList.putAll(selectedList_);
		}
		_this.type = type;
		_this.isRadio = isRadio;
		_this.exception = exception;
		
	}
	public void addList( ArrayList<Companytype> list ){
		mlist.addAll( list );
		notifyDataSetChanged();
	}
	public void clear(){
		mlist.clear();
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mlist.size();
	}

	@Override
	public GezitechEntity_I getItem(int position) {
		// TODO Auto-generated method stub
		return mlist.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	private  String typename;
	private long id;
	private String des="";
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		final GezitechEntity_I items;
		items = getItem(position);
		if( ItemType.Companytype == type ){
			Companytype item = ( Companytype) items;
			typename = item.typename;
			id = item.id;
		}else if ( ItemType.Banktype == type ){
			Banktype item = ( Banktype) items;
			typename = item.name;
			id = item.id;
		}else if ( ItemType.Releasescope == type ){
			Releasescope item = ( Releasescope) items;
			typename = item.rangename;
			id = item.id;
		}else if( ItemType.Validtimelist == type ){//有效时间列表
			Validtimelist item = (Validtimelist)items;
			typename = item.activetimeName;
			id = item.id;
		}else if( ItemType.City == type ){// 城市
			City item = (City)items;
			typename = item.name;
			id = item.id;
		}else if( ItemType.Country == type ){// 国家
			Country item = (Country)items;
			typename = item.rangename;
			id = item.id;
		}else if( ItemType.PubRange == type ){// 发布半径
			PubRange item = (PubRange)items;
			typename = item.rangetitle;
			id = item.id;
			if( _this.exception == 1 ){ des = item.description;  }
		}
		
		Viewholder viewholder;
		if( !isRadio ){		 //多选
			if(convertView==null){
				convertView=mInflater.inflate(R.layout.list_option, null);
				viewholder=new Viewholder();
				viewholder.mCheckBox=(CheckBox) convertView.findViewById(R.id.bt_hobby);
				convertView.setTag(viewholder);
			}else{
			  viewholder=(Viewholder) convertView.getTag();	
			}
			
			//判断是否选中
			if( _this.selectedList != null &&_this.selectedList.size()>0){
				if(_this.selectedList.containsKey(id+"")){
					viewholder.mCheckBox.setChecked( true );
				}else{
					viewholder.mCheckBox.setChecked( false );
				}
			}
			//名称
			viewholder.mCheckBox.setText( typename );
			viewholder.mCheckBox.setOnClickListener( new  OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//判断是否包含
					if(_this.selectedList.containsKey(id+"")){
						_this.selectedList.remove(id+"");
					}else{
						_this.selectedList.put(id+"", typename);
					}
					_this.onClickDataPress.onDataPerss( items );
				}
			});
		}else{//单选
			if( convertView == null ){
				convertView=mInflater.inflate(R.layout.list_option_radio, null);
				viewholder=new Viewholder();
				viewholder.tv_hobby_radio=(TextView) convertView.findViewById(R.id.tv_hobby_radio);
				convertView.setTag(viewholder);
			}else{
				 viewholder=(Viewholder) convertView.getTag();	
			} 
			//名称
			if( _this.exception == 1 ){ 
				
				viewholder.tv_hobby_radio.setText( Html.fromHtml( typename+"：<font color=\"#949494\">"+des+"</font>" ) );
				
			}else{
				viewholder.tv_hobby_radio.setText( typename );
			}
			
			viewholder.tv_hobby_radio.setOnClickListener( new  OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				
					_this.onClickDataPress.onDataPerss( items );
				}
			});
		}
		return convertView;
	}
  public static class Viewholder{
	  public CheckBox  mCheckBox; 
	  public TextView tv_hobby_radio;
  }
  //回调接口
  public interface OnClickDataPress{
		public void onDataPerss( GezitechEntity_I item );
	}
	
	public void setOnClickDataPress(OnClickDataPress onClickDataPress){
		this.onClickDataPress = onClickDataPress;
	}

}
