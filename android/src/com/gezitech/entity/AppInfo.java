package com.gezitech.entity;
/***
 * 储存app应用的版本信息等
 */
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.gezitech.basic.GezitechApplication;
import com.gezitech.basic.GezitechEntity;
import com.gezitech.basic.GezitechEntity.TableInfo;
import com.gezitech.service.sqlitedb.GezitechDBHelper;
@TableInfo(tableName="appinfo")
public class AppInfo extends GezitechEntity {
	private static final long serialVersionUID = 1L;
	@FieldInfo
	public String version;
	public static final String defaultVersion = "can't read version";
	@Override
	public void afterCreateTable() {
		super.afterCreateTable();
		GezitechDBHelper<AppInfo> db = new GezitechDBHelper<AppInfo>(AppInfo.class);
		String version = defaultVersion;
		PackageInfo pi = getPackageInfo();
		if(pi != null)version = pi.versionName;
		db.delete(null);
		AppInfo ai = new AppInfo();
		ai.id = 1;
		ai.version = version;
		db.save(ai);
		db.close();
	}
	/**
	 * 当前程序版本与上次程序版本是否相同
	 * @return
	 */
	public boolean isVersionDifferent(){
		String version = defaultVersion;
		PackageInfo pi = getPackageInfo();
		if(pi != null)version = pi.versionName;
		GezitechDBHelper<AppInfo> db = new GezitechDBHelper<AppInfo>(AppInfo.class);
		AppInfo ai = db.find(1);
		if(ai == null)return false;
		return !version.equals(ai.version);
	}
	/**
	 * 获取版本号
	 * @return
	 */
	public String getVersionName(){
		try{
			PackageInfo pi = getPackageInfo();
			if(pi == null)return null;
			return pi.versionName;
		}catch(Exception ex ){
			return null;
		}
	}
	protected static PackageInfo getPackageInfo(){
		try{
			Context context = GezitechApplication.getContext();
			return context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
}
