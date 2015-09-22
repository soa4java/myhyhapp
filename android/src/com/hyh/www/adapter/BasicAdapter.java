package com.hyh.www.adapter;

import java.util.ArrayList;

import com.gezitech.basic.GezitechApplication;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.entity.PageList;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
/**
 * 
 * @author xiaobai
 * 2014-10-14
 * @todo( 适配器基类  )
 */
abstract public class BasicAdapter extends BaseAdapter{
	public BasicAdapter instance = this;
	public PageList list = new PageList();
	public LayoutInflater inflater;
	public Context context ;
	protected OnClickDataPress onClickDataPress;
	public BasicAdapter(){		
		context = GezitechApplication.getContext();
		instance.inflater	= LayoutInflater.from(context );
	}
	public void addList(ArrayList<GezitechEntity_I> list,boolean sign){
		if(sign){
			instance.list.addAll(0,list);
		}else{
			instance.list.addAll(list);
		}
		instance.notifyDataSetChanged();
	}
	public void addItem(GezitechEntity_I item,boolean sign){
		if(sign){
			instance.list.add(0,item);
		}else{
			instance.list.add(item);
		}
		instance.notifyDataSetChanged();
	}
	public void addItem(GezitechEntity_I item,int index){
		instance.list.add(index,item);
		instance.notifyDataSetChanged();
	}
	public ArrayList<GezitechEntity_I> getList(){		
		return instance.list;
	}
	public GezitechEntity_I getItemList(int index){		
		if( instance.list.size()<=0 ) return null;
		return instance.list.get( index );
	}
	public void removeAll() {
		instance.list.clear();
		instance.notifyDataSetChanged();
	}
	public void remove( int index ){
		instance.list.remove( index );
		instance.notifyDataSetChanged();
	}
	public void setItem(GezitechEntity_I item,int index){
		if(item != null && instance.list.size()>index && index>=0 ){
			instance.list.set(index, item);
			instance.notifyDataSetChanged();
		}
	}
	public void setItem(GezitechEntity_I item,int index, boolean isRefresh){
		if(item != null && instance.list.size()>index && index>=0 ){
			instance.list.set(index, item);
			if( isRefresh ) instance.notifyDataSetChanged();
		}
	}
	//回调接口
	public interface OnClickDataPress{
		public void onDataPerss( GezitechEntity_I item, int position  );
	}
	
	public void setOnClickDataPress(OnClickDataPress onClickDataPress){
		this.onClickDataPress = onClickDataPress;
	}

}
