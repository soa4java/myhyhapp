package com.gezitech.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.widget.OptionDialog.ItemType;
import com.hyh.www.R;
import com.hyh.www.entity.Banktype;
import com.hyh.www.entity.Companytype;
import com.hyh.www.entity.Releasescope;
import com.hyh.www.entity.Validtimelist;


import android.content.Context;
import android.util.Log;
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
public class MoreOptionAdapter extends BaseAdapter{
	private MoreOptionAdapter _this = this;
    private Context mcontext;
    private ArrayList<GezitechEntity_I> mlist= new ArrayList<GezitechEntity_I>();
    private LayoutInflater mInflater;
    public Companytype selectedList = null;
	private OnClickDataPress onClickDataPress;
	public boolean isSelectedBoolean = false;
	private int type;
	public MoreOptionAdapter(Context context, Companytype selectedList_, ArrayList<GezitechEntity_I> list, int type  ){
		mcontext = context;
		mInflater	= LayoutInflater.from( context );
		_this.mlist.clear();
		_this.mlist.addAll( list );
		_this.selectedList = selectedList_;
		_this.type = type;
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
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		final Companytype items = ( Companytype) getItem(position);
		final Viewholder viewholder;

		//if( convertView == null ){
			convertView = mInflater.inflate(R.layout.list_option_radio, null);
			viewholder=new Viewholder();
			viewholder.tv_hobby_radio = (TextView) convertView.findViewById(R.id.tv_hobby_radio);
		//	convertView.setTag(viewholder);
		//}else{
		//	 viewholder=(Viewholder) convertView.getTag();	
		//} 
		if( type == 2 ){
			viewholder.tv_hobby_radio.setBackgroundResource( R.drawable.input_push );
		}
		if( items.isselected == 1 ){
			
			if( type == 2 ){
				viewholder.tv_hobby_radio.setBackgroundResource( R.drawable.input_push2 );
			}else{
				viewholder.tv_hobby_radio.setBackgroundResource( R.drawable.input_push );
			}
			
		}else if(selectedList !=null && selectedList.pid > 0 && !isSelectedBoolean ){//说明是二级
			if( ( selectedList !=null &&  items.id == selectedList.pid) || ( selectedList !=null && items.id == selectedList.id )  ) {
				
				if( type == 2 ){
					viewholder.tv_hobby_radio.setBackgroundResource( R.drawable.input_push2 );
				}else{
					viewholder.tv_hobby_radio.setBackgroundResource( R.drawable.input_push );
				}
				
			}
		}else{//一级分类
			if( selectedList !=null && items.id == selectedList.id && !isSelectedBoolean ){
				
				if( type == 2 ){
					viewholder.tv_hobby_radio.setBackgroundResource( R.drawable.input_push2 );
				}else{
					viewholder.tv_hobby_radio.setBackgroundResource( R.drawable.input_push );
				}
				
			}
			
		}
		
		//名称
		viewholder.tv_hobby_radio.setText( "  "+items.typename );
		viewholder.tv_hobby_radio.setOnClickListener( new  OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				isSelectedBoolean = true;
				
				for(  int i=0; i<_this.mlist.size(); i++){
					
					Companytype itemss = ( Companytype)_this.mlist.get(i);
					if( itemss.isselected == 1 ){
						itemss.isselected = 0;
						_this.mlist.set( i, itemss );
						
					}
					
				}
				
				
				items.isselected = 1;
				
				_this.mlist.set( position, items );
				
				 _this.notifyDataSetChanged();
				
				_this.onClickDataPress.onDataPerss( items );
				
			}
		});
		
		return convertView;
	}
  public static class Viewholder{
	  public CheckBox  mCheckBox; 
	  public TextView tv_hobby_radio;
  }
  //回调接口
  public interface OnClickDataPress{
		public void onDataPerss( Companytype item );
	}
	
	public void setOnClickDataPress(OnClickDataPress onClickDataPress){
		this.onClickDataPress = onClickDataPress;
	}

}
