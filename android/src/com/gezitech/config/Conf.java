package com.gezitech.config;

import java.lang.reflect.Array;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

public class Conf {
	
	public final static String APIKEY = "ykpUwtiG8fDuCeKGrGSMhSuL";
	//public final static String APIKEY = "HDNgYUvqiNGerzhSjISQnDgG";
	//social demo
	public final static String SINA_APP_KEY = "319137445";
    
    //personal file storage demo
    public final static String PERSON_STORAGE_DIR_NAME = "/apps/FrontiaDevDemo/pic";

    public final static String PERSON_STORAGE_FILE_NAME = "/apps/FrontiaDevDemo/pic/custom.jpg";
	
    public final static String LOCAL_FILE_NAME = "/sdcard/Download/custom.jpg";
    public final static String FILE1 = "/sdcard/Download/custom.jpg";
    public final static String FILE2 = "/sdcard/Download/custom.jpg";
    public final static String FILE3 = "/sdcard/Download/custom.jpg";
    
    //app file storage demo
    public final static String APP_STORAGE_FILE_NAME = "custom.jpg";

    //statistics demo
    public static final String eventId = "1";//提醒
    public static final String reportId = "fd4278e8f6";
    
    /**
     * xmpp配置信息
     */
    public static final String host = "115.29.191.15";
    //public static final String host = "125.65.83.2";
    public static final String domian = "android";
    public static final int port = 5222;
    //public static final int port =  53306;
    public static String username; //用户名
    public static String password; //密码
    public static boolean isNovisible = true; //是否接受隐身登录信息
    public static boolean isAutoLogin = false ; //是否自动登录
    public static boolean isRemember = true; //是否记住
    public static boolean isFirstStart = true; //是否第一次运行
    
    //验证码倒计时 时间差
    public static int timedeff = 60; //秒
    //广告轮播暂停播放时间
    public static int pauseTime = 5000; //毫秒
   
    //
    // 15//好友请求，
    // 16//同意加好友，
    // 17//拒绝加好友    
    // 18,//删除好友
    
	  //11,//用户撤销付款;
	  //12,//商家拒绝收款;
	  //13,//用户申请退款;
	  //14//用户确认服务
    
    //语言时间长度
    public static int vioceTime = 60;
    //微信支付appid
    public static String wechat_pay_app_id = "wxe1386365f1903c95";
    public static String wechat_pat_app_secret = "138aee466681b57f514127b49bd7bf76";
}
