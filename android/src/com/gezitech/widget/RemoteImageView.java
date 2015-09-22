package com.gezitech.widget;

import com.gezitech.util.IOUtil;
import com.gezitech.util.StringUtil;
import com.gezitech.util.ThreadUtil;
import com.gezitech.util.IOUtil.CacheCompleteListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * ImageView extended class allowing easy downloading of remote images
 * 
 * @author Lukasz Wisniewski xiaobai 修改
 */
public class RemoteImageView extends ImageView {

	private static int MAX_FAILURES = 3;
	private Context mContext;

	public RemoteImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	public RemoteImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public RemoteImageView(Context context) {
		super(context);
		mContext = context;
	}

	/***
	 * 加载远程图标
	 * 
	 * @param url
	 *            图片远程地址
	 * forceDownload 是否强制下载
	 */
	public void setImageUrl(String url){
		setImageUrl(url,false,false, null);
	}
	public void setImageUrl(String url,boolean forceDownload ){
		setImageUrl(url,forceDownload,false,null);
	}
	public void setImageUrl(String url,boolean forceDownload, final  boolean isBack){
		setImageUrl(url,forceDownload,false,null);
	}
	public void setImageUrl(String url,boolean forceDownload, final  boolean isBack,final setBitmapListener bitmapListener ) {
		if (!StringUtil.isEmpty(url)) {
			final Handler setImageHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					try {
						if (msg.obj instanceof Bitmap){
							if( isBack ){
								
								Drawable drawable = new BitmapDrawable( (Bitmap)msg.obj  ); 
								RemoteImageView.this.setBackgroundDrawable( drawable );
							
							}else{
								RemoteImageView.this
								.setImageBitmap((Bitmap) msg.obj);
							}
							if( bitmapListener !=null ){
								bitmapListener.bitmap( (Bitmap) msg.obj );
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			};
			IOUtil.downloadAndCacheFile(url, forceDownload,
					new CacheCompleteListener() {
						@Override
						public void onCacheComplete(String fileName,
								boolean success, String msg, Object tag) {
							try {
								setImageHandler.sendMessage(ThreadUtil.createMessage(
										0,
										0,
										success ? IOUtil
												.getLocalImage(fileName) : null));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
		}

	}
	public interface setBitmapListener{
		public void bitmap( Bitmap bm );
	}
	
}