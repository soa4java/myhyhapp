package com.gezitech.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.entity.PageList;
import com.gezitech.entity.PhotoAlbumModel;
import com.gezitech.util.IOUtil;
import com.gezitech.util.ImageDownloader;
import com.gezitech.util.ImageUtil;
import com.gezitech.widget.RemoteImageView;
import com.hyh.www.R;
import com.squareup.picasso.Picasso;


import android.app.Activity;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 
 * @author xiaobai
 * 2014-9-24
 * @todo( 相册列表  )
 */
public class PhotoAlbumAdapter extends BaseAdapter{
	private PhotoAlbumAdapter _this = this;
	private LayoutInflater inflater;
	private Activity context;
	private PageList list = new PageList();
	private RemoteImageView imgFrontCover;
	private ImageDownloader mImageDownloader;
	public boolean mImageDownloaderOpen = true;
	private OnAlbumListener onAlbumListener;
	private int lineCount = 10;
	public PhotoAlbumAdapter( Activity context, int lineCount ){
		_this.context = context;
		_this.inflater	= LayoutInflater.from( context );
		_this.lineCount  = lineCount;
		mImageDownloader = new ImageDownloader(context.getResources().getDrawable(R.drawable.common_default_photo));
		mImageDownloader.setMode(ImageDownloader.Mode.CORRECT);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if( list.size() < lineCount ){  
			return lineCount;
		}else{
			return list.size();
		}
	}

	@Override
	public GezitechEntity_I getItem(int position) {
		// TODO Auto-generated method stub
		return list.get( position );
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		Hv hv;
		if( view == null){
			view = inflater.inflate(R.layout.list_album, null);
			hv = new Hv( view );
			view.setTag( hv );
			
		}else{
			hv = (Hv)view.getTag();
		}
		
		
		if( list.size()>=lineCount|| ( (list.size() <lineCount ) && ( list.size() - (position+1)  >=0 )  ) ){
			final PhotoAlbumModel item = (PhotoAlbumModel) getItem( position );
		//	Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), item.bitmap, Thumbnails.MICRO_KIND, null);
				
		//	iv_cover.setImageBitmap( bitmap  );
			Picasso.with(context)
            .load(new File(item.path))
            .error(R.drawable.default_error )
            .resize(150, 150)
            .centerCrop()
            .into( hv.iv_cover );
			hv.tv_number.setText( item.count+"张" );
			view.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onAlbumListener.albumListener( item );
					
				}
			});
			
		}else{
			hv.iv_cover.setVisibility( View.GONE );
			hv.tv_number.setVisibility( View.GONE );
			hv.ll_arrows.setVisibility( View.GONE );
			view.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onAlbumListener.albumListener( null );		
				}
			});
			
		}
		
		return view;
	}
	class Hv{
		
		private ImageView iv_cover;
		private TextView tv_number;
		private LinearLayout ll_arrows;

		public Hv(View view){

			 iv_cover = (ImageView) view.findViewById( R.id.iv_cover );
			 tv_number = (TextView) view.findViewById( R.id.tv_number );
			 ll_arrows = (LinearLayout) view.findViewById( R.id.ll_arrows );
		}
		
	}
	//点击相册获取图片的回调事件
	public void setOnAlbumListener(OnAlbumListener onAlbumListener){
		this.onAlbumListener = onAlbumListener;
	}
	public interface OnAlbumListener{
		public void albumListener( PhotoAlbumModel item );
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
