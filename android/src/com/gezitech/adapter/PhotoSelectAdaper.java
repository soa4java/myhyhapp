package com.gezitech.adapter;

import java.io.File;
import java.util.ArrayList;

import com.gezitech.adapter.PhotoAlbumAdapter.Hv;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.entity.PageList;
import com.gezitech.entity.PhotoItemModel;
import com.hyh.www.R;
import com.squareup.picasso.Picasso;


import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 
 * @author xiaobai
 * 2014-5-27
 * @todo(  广告的幻灯片  )
 */
public class PhotoSelectAdaper extends BaseAdapter{
	PageList list = new PageList();
	Context context;
	PhotoSelectAdaper _this = this;
	private LayoutInflater inflater;
	public PhotoSelectAdaper(Context context) {
		_this.context = context;
		_this.inflater	= LayoutInflater.from( context );
	}

	@Override
	public int getCount() {
		return list.size();
	}
	@Override
	public GezitechEntity_I getItem(int position) {
		return list.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		Hv hv;
		if( view == null){
			view = inflater.inflate(R.layout.list_photo, null);
			hv = new Hv( view );
			view.setTag( hv );
			
		}else{
			hv = (Hv)view.getTag();
		}		
		PhotoItemModel item  = (PhotoItemModel) getItem(position);

		//Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), item.photoID , Thumbnails.MINI_KIND, null);
		//iv_img.setImageBitmap( bitmap );
		Picasso.with(context)
        .load(new File(item.path))
        .error(R.drawable.default_error )
        .resize(150, 150)
        .centerCrop()
        .into( hv.iv_img );
		if( item.select ){//选中
			hv.tv_select.setVisibility( View.VISIBLE );
			hv.iv_select.setVisibility( View.VISIBLE );
		}else{
			hv.tv_select.setVisibility( View.GONE );
			hv.iv_select.setVisibility( View.GONE );
		}
		
		
        return view;
	}
	class Hv{
		private ImageView iv_img;
		private TextView tv_select;
		private ImageView iv_select;

		public Hv( View view ){
			 iv_img = (ImageView) view.findViewById( R.id.iv_img );
			 tv_select = (TextView) view.findViewById( R.id.tv_select );
			 iv_select = (ImageView) view.findViewById( R.id.iv_select );
		}
	}
	public void addList(ArrayList<GezitechEntity_I> list,boolean sign){
		if(sign){
			_this.list.addAll(0,list);
		}else{
			_this.list.addAll(list);
		}
		_this.notifyDataSetChanged();
	}
	public void addItem(GezitechEntity_I item,boolean sign){
		if(sign){
			_this.list.add(0,item);
		}else{
			_this.list.add(item);
		}
		_this.notifyDataSetChanged();
	}
	public void addItem(GezitechEntity_I item,int index){
		_this.list.add(index,item);
		_this.notifyDataSetChanged();
	}
	public ArrayList<GezitechEntity_I> getList(){		
		return _this.list;
	}
	public GezitechEntity_I getItemList(int index){		
		return _this.list.get( index );
	}
	public void removeAll() {
		_this.list.clear();
		_this.notifyDataSetChanged();
	}
	public void setItem(GezitechEntity_I item,int index){
		if(item != null && _this.list.size()>index && index>=0 ){
			_this.list.set(index, item);
			_this.notifyDataSetChanged();
		}
	}
}
