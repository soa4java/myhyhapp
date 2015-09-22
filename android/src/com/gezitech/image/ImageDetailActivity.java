/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gezitech.image;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import org.apache.http.Header;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.MediaColumns;
import android.provider.SyncStateContract.Constants;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.http.HttpUtil;
import com.gezitech.util.NetUtil;
import com.gezitech.util.ToastMakeText;
import com.hyh.www.R;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.roger.quickviewpage.imagecache.DiskCache;
import com.roger.quickviewpage.imagecache.ImageWorker;
import com.roger.quickviewpage.imagecache.ImageCache.ImageCacheParams;
/**
 * 
 * @author web
 * @update xiaobai
 * 2014-11-13
 * @todo( 图片的查看插件 修改 )
 */
public class ImageDetailActivity extends FragmentActivity {
	private static final String IMAGE_CACHE_DIR = "images";
	public static final String EXTRA_IMAGE = "extra_image";
	private final static String TAG = "ImageDetailActivity";

	private ImagePagerAdapter mAdapter;
	private ImageWorker mImageWorker;
	private ViewPager mPager;

	/** 轮播图点点 **/
	private ViewGroup dotGroup;
	/** 装点点的 **/
	private ImageView[] tips;
	private int extraCurrentItem = -1;
	public static String[] imageUrls ;
	private ImageDetailActivity _this = this;
	private String ImageUrl ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_dialog);
		Intent intent = getIntent();
		extraCurrentItem  = intent.getExtras().getInt("index");
		
		imageUrls = intent.getExtras().getStringArray("images");
		
		// Fetch screen height and width, to use as our max size when loading
		// images as this
		// activity runs full screen
		final DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		final int height = displaymetrics.heightPixels;// 屏幕高度像素值
		final int width = displaymetrics.widthPixels;// 屏幕宽度像素值

		ImageCacheParams cacheParams = new ImageCacheParams();
		cacheParams.reqHeight = height;// 设置高
		cacheParams.reqWidth = width;// 设置宽
		cacheParams.memoryCacheEnabled = false;
//		cacheParams.loadingResId = R.drawable.ic_launcher;// 加载中默认图
		mImageWorker = ImageWorker.newInstance(this);
		mImageWorker.addParams(TAG, cacheParams);
		// mImageWorker.setLoadingImage(R.drawable.empty_photo);

		// Set up ViewPager and backing adapter
		mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), imageUrls.length);
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		dotGroup = (ViewGroup) findViewById(R.id.dot_group);
		setDot(imageUrls.length);
		// 更新下标
		mPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageSelected(int position) {
				for (int i = 0; i < tips.length; i++) {
					if (i == position % tips.length) {
						if( imageUrls.length > 1 )
							tips[i].setBackgroundResource(R.drawable.news_diang);
						ImageUrl = imageUrls[i];
					} else {
						if( imageUrls.length > 1 )
							tips[i].setBackgroundResource(R.drawable.news_diang_hover);
					}
				}
			}
		});

		if (extraCurrentItem != -1) {
			mPager.setCurrentItem(extraCurrentItem);
			ImageUrl = imageUrls[extraCurrentItem];
		}
/*		//关闭
		((ImageView)findViewById(R.id.iv_close)).setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				_this.finish();
				//overridePendingTransition( R.anim.window_in_down_up,R.anim.window_out_up_down);
			}
		});
		//保存
		((ImageView)findViewById(R.id.iv_save)).setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				downloadImg( );
				
			}
		});*/
		
	}
	/**
	 * 
	 * TODO( 文件的下载 )
	 */
	protected void downloadImg() {
		GezitechAlertDialog.loadDialog( this );
		if(NetUtil.isNetworkAvailable()){
	    	HttpUtil.getFullUrl(ImageUrl ,new BinaryHttpResponseHandler(){
	    		@Override
	    		public void onProgress(int bytesWritten, int totalSize) {
	    			// TODO Auto-generated method stub
	    			super.onProgress(bytesWritten, totalSize);
	    			
	    		}
	    		
				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2,
						Throwable arg3) {
					// TODO Auto-generated method stub
					new ToastMakeText( _this ).Toast("下载失败,请重新下载");
					GezitechAlertDialog.closeDialog();
				}
				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					
					
					final DiskCache cache = DiskCache.openCache();
					final File cacheFile = new File(cache.createFilePath(_this.getResources().getString(R.string.app_name_directory)));
					cacheFile.mkdirs();
					final File cacheFiles = new File( cacheFile, com.gezitech.util.DateUtils.getStringToday()+".jpg");
					GezitechAlertDialog.closeDialog();
	                try {
	                	
	                    FileOutputStream oStream = new FileOutputStream( cacheFiles );
	                    oStream.write( arg2 );
	                    oStream.flush();
	                    oStream.close();
	                    
	                    URL u = cacheFiles.toURL();

	                    //2种
	                   /* String uriStr = MediaStore.Images.Media.insertImage(getContentResolver(), BitmapFactory.decodeByteArray(arg2, 0, arg2.length), "", "");
	                    String picPath  = getFilePathByContentResolver(_this, Uri.parse(uriStr) );                
	                    ContentResolver contentResolver = _this.getContentResolver();
	                    ContentValues values = new ContentValues(4);
	                    values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
	                    values.put(Images.Media.MIME_TYPE, "image/png");
	                    values.put(Images.Media.ORIENTATION, 0);
	                    values.put(Images.Media.DATA, picPath);
	                    contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values);
	                    */
	                    scanPhoto( _this, u.getPath() );
	                    new ToastMakeText( _this ).Toast("保存成功");
	                    
	                } catch (Exception e) {
	                    e.printStackTrace();
	                    new ToastMakeText( _this ).Toast("下载失败,请重新下载");
	                }
				}
	    	});
	    }else{
	    	GezitechAlertDialog.closeDialog();
	    	new ToastMakeText( this ).Toast( GezitechApplication.getContext().getString( R.string.network_error )  );
	    }   	
	}
	 private static void scanPhoto(Context ctx, String imgFileName) {
	        Intent mediaScanIntent = new Intent(
	                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	        File file = new File(imgFileName);
	        Uri contentUri = Uri.fromFile(file);
	        mediaScanIntent.setData(contentUri);
	        ctx.sendBroadcast(mediaScanIntent);
	    }
	/**
	 * 
	 * TODO( 获取图片的绝对路径  )
	 */
	private  String getFilePathByContentResolver(Context context, Uri uri) {  
        if (null == uri) {  
	            return null;  
	        }  
	        Cursor c = context.getContentResolver().query(uri, null, null, null, null);  
        String filePath  = null;  
	        if (null == c) {  
	            throw new IllegalArgumentException(  
	                    "Query on " + uri + " returns null result.");  
	        }  
	        try {  
            if ((c.getCount() != 1) || !c.moveToFirst()) {  
	            } else {  
	                filePath = c.getString(  
                       c.getColumnIndexOrThrow(MediaColumns.DATA));  
	           }  
	       } finally {  
	            c.close();  
	        }  
	        return filePath;  
	    }  
	@Override
	public void onResume() {
		super.onResume();
		mImageWorker.setOnScreen(TAG, true);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPause() {
		super.onPause();
		mImageWorker.setOnScreen(TAG, false);
	}

	/**
	 * Called by the ViewPager child fragments to load images via the one
	 * ImageWorker
	 * 
	 * @return
	 */
	public ImageWorker getImageWorker() {
		return mImageWorker;
	}

	/**
	 * The main adapter that backs the ViewPager. A subclass of
	 * FragmentStatePagerAdapter as there could be a large number of items in
	 * the ViewPager and we don't want to retain them all in memory at once but
	 * create/destroy them on the fly.
	 */
	private class ImagePagerAdapter extends FragmentStatePagerAdapter {
		private final int mSize;

		public ImagePagerAdapter(FragmentManager fm, int size) {
			super(fm);
			mSize = size;
		}

		@Override
		public int getCount() {
			return mSize;
		}

		@Override
		public Fragment getItem(int position) {
			return ImageDetailFragment.newInstance(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			final ImageDetailFragment fragment = (ImageDetailFragment) object;
			// As the item gets destroyed we try and cancel any existing work.
			fragment.cancelWork();
			super.destroyItem(container, position, object);
		}
	}

	// 设置点点的
	public void setDot(int index) {
		dotGroup.removeAllViews();
		tips = new ImageView[index];
		for (int i = 0; i < tips.length; i++) {
			ImageView imageView = new ImageView(ImageDetailActivity.this);
			imageView.setLayoutParams(new LayoutParams(5, 5));
			tips[i] = imageView;
			if (i == 0) {
				tips[i].setBackgroundResource(R.drawable.news_diang);
			} else {
				tips[i].setBackgroundResource(R.drawable.news_diang_hover);
			}

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 5;
			layoutParams.rightMargin = 5;
			if( imageUrls.length>1 )
				dotGroup.addView(imageView, layoutParams);
		}
	}
	@Override
	public void onBackPressed() {
		this.finish();
		overridePendingTransition(R.anim.in_anim, R.anim.in_from_down );
	}
	public void closeActivity(){
		finish();
		overridePendingTransition(R.anim.in_anim, R.anim.in_from_down );
	}
	

}
