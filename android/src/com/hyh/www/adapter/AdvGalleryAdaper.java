package com.hyh.www.adapter;

import com.gezitech.entity.PageList;
import com.gezitech.util.ImageDownloader;
import com.hyh.www.R;
import com.hyh.www.entity.Adv;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
/**
 * 
 * @author xiaobai
 * 2014-5-27
 * @todo(  广告的幻灯片  )
 */
public class AdvGalleryAdaper extends BaseAdapter{
	PageList list = new PageList();
	Context context;
	AdvGalleryAdaper _this = this;
	private ImageDownloader mImageDownloader;
	public boolean mImageDownloaderOpen = true; 
	private int size = 0;
	public AdvGalleryAdaper(Context context, PageList advList) {
		_this.context = context;
		_this.list = advList;
		size = list.size();
		mImageDownloader = new ImageDownloader(null);
		mImageDownloader.setMode(ImageDownloader.Mode.CORRECT);
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}
	@Override
	public Adv getItem(int position) {
		return (Adv) list.get( position % size );
	}
	
	@Override
	public long getItemId(int position) {
		return position % size;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null){
			ImageView imageView = new ImageView( context );
	        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
	        
	        convertView = imageView;
		}
		
		Adv adv = (Adv)list.get( position % size );
		ImageView mImageView = (ImageView)convertView;
		mImageView.setBackgroundResource( R.drawable.sy_ad_01 );
		//mImageDownloader.download("", mImageDownloaderOpen, mImageView );
        return convertView;
	}

}
