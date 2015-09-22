package cn.sharesdk.onekeyshare;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import com.gezitech.basic.GezitechApplication;
import com.gezitech.config.Conf;
import com.hyh.www.R;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler.Callback;
import android.os.Message;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.sina.weibo.SinaWeibo;
/**
 * 
 * @author xiaobai
 * 2014-9-15
 * @todo( 分享工具 )
 */
public class ShareTools implements PlatformActionListener, Callback{
	
	public ShareTools _this = this;
	private OnAuthCallback onAuthCallback;
	private String TEST_IMAGE;
	private static final String FILE_NAME = "/share_pic.jpg";
	static ShareTools instance = null;
	
	public static ShareTools getInstance(){
		if(instance == null ){
			instance =  new ShareTools();
		}
		return instance;
	}
	
	/**
	 * TODO( 返回分享平台列表 )
	 */
	public static Platform[] getAuthList(){
		Platform[] pfl = ShareSDK.getPlatformList();
		
        return pfl;
	} 
	/**
	 * 
	 * TODO(获取某平台)
	 */
	public static  Platform getPlatForm(String pfl){
		return ShareSDK.getPlatform( pfl );
	}
	/**
	 * 
	 * TODO( 获取授权  )
	 */
	public void  getAuthorize( Platform pf  ){
		pf.setPlatformActionListener( this );
		pf.authorize();
	}
	@Override
	public void onCancel(Platform plat, int action) {
		
		Message msg = new Message();
		msg.arg1 = 3;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
		
	}
	@Override
	public void onComplete(Platform plat, int action, HashMap<String, Object> arg2) {
		Message msg = new Message();
		msg.arg1 = 1;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}
	@Override
	public void onError(Platform plat, int action, Throwable arg2) {
		arg2.printStackTrace();
		Message msg = new Message();
		msg.arg1 = 2;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
		
	}
	/** 通过Toast显示操作结果 */
	public boolean handleMessage(Message msg) {
		Platform plat = (Platform) msg.obj;
		String text = "";//MainActivity.actionToString(msg.arg2);
		switch (msg.arg1) {
			case 1: {
				// 成功
				//text = plat.getName() + " get token: " + plat.getDb().getToken();
				text = "授权成功";
			}
			break;
			case 2: {
				// 失败
				//text = plat.getName() + " caught error";
				text = "授权失败";
			}
			break;
			case 3: {
				// 取消
				//text = plat.getName() + " authorization canceled";
				text = "取消授权";
			}
			break;
		}
		this.onAuthCallback.callback(plat, text);
		return false;
	}
	public void setOnAuthCallback(OnAuthCallback onAuthCallback){
		this.onAuthCallback = onAuthCallback;
	}
	public interface OnAuthCallback{
		public void callback(Platform plat, String text);
	}
	
	public  void share( Context context, String platform, String title, String wap_url, String content, String imgUrl  ){
		
		
		
		final OnekeyShare oks = new OnekeyShare();
		oks.setNotification(R.drawable.launcher_96, context.getString(R.string.app_name));
		oks.setTitle( title );
		oks.setTitleUrl( wap_url );
		oks.setText( content + wap_url  );
		
		if( imgUrl!=null && !imgUrl.equals("") ){
			oks.setImageUrl( imgUrl );
		}else{
			initImagePath( );
			oks.setImagePath( TEST_IMAGE );
		}
		
		if( platform != null ){
			oks.setPlatform( platform );
		}
		oks.setUrl( wap_url );
		//oks.setFilePath(MainActivity.TEST_IMAGE);
		oks.setComment(context.getString(R.string.share) );
		oks.setSite(context.getString(R.string.app_name));
		oks.setSiteUrl( wap_url );
		oks.setVenueName( context.getString(R.string.app_name) );
		//oks.setVenueDescription("This is a beautiful place!");
		//oks.setLatitude(23.056081f);
		//oks.setLongitude(113.385708f);
		oks.setSilent( false ); //图文分享
		
		//oks.setPlatform(platform);

		// 令编辑页面显示为Dialog模式
		//oks.setDialogMode();

		// 在自动授权时可以禁用SSO方式
		oks.disableSSOWhenAuthorize();

		// 去除注释，则快捷分享的操作结果将通过OneKeyShareCallback回调
//		oks.setCallback(new OneKeyShareCallback());
//		oks.setShareContentCustomizeCallback(new ShareContentCustomizeDemo());

		// 去除注释，演示在九宫格设置自定义的图标
//		Bitmap logo = BitmapFactory.decodeResource(menu.getResources(), R.drawable.ic_launcher);
//		String label = menu.getResources().getString(R.string.app_name);
//		OnClickListener listener = new OnClickListener() {
//			public void onClick(View v) {
//				String text = "Customer Logo -- ShareSDK " + ShareSDK.getSDKVersionName();
//				Toast.makeText(menu.getContext(), text, Toast.LENGTH_SHORT).show();
//				oks.finish();
//			}
//		};
//		oks.setCustomerLogo(logo, label, listener);

		// 去除注释，则快捷分享九宫格中将隐藏新浪微博和腾讯微博
//		oks.addHiddenPlatform(SinaWeibo.NAME);
//		oks.addHiddenPlatform(TencentWeibo.NAME);

		// 为EditPage设置一个背景的View
		//oks.setEditPageBackground(getPage());
		Platform weibo = ShareSDK.getPlatform( context , SinaWeibo.NAME); 
		weibo.setPlatformActionListener(this); // 设置分享事件回调 
		
		oks.show( context );
	}
	private void initImagePath() {
		try {
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && Environment.getExternalStorageDirectory().exists()) {
				TEST_IMAGE = Environment.getExternalStorageDirectory().getAbsolutePath() + FILE_NAME;
			} else {
				TEST_IMAGE = GezitechApplication.getInstance().getFilesDir().getAbsolutePath()+ FILE_NAME;
			}
			// 创建图片文件夹
			File file = new File(TEST_IMAGE);
			if (!file.exists()) {
				file.createNewFile();
				Bitmap pic = BitmapFactory.decodeResource(GezitechApplication.getInstance().getResources(),R.drawable.launcher_96 );
				FileOutputStream fos = new FileOutputStream(file);
				pic.compress(CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			}
		} catch (Throwable t) {
			t.printStackTrace();
			TEST_IMAGE = null;
		}
	}
}
