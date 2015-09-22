package com.gezitech.util;

import java.io.File;
import java.io.FileOutputStream;

import com.gezitech.basic.GezitechApplication;
import com.hyh.www.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;


public class ImageLauncher {
	//获取启动图标的地址
	public static String getLauncherImagePath() {
		String TEST_IMAGE;
		try {
			TEST_IMAGE = IOUtil.getCacheFilePath()+"/share_logo.jpg";
			File file = new File( TEST_IMAGE );
			if (!file.exists()) {
				file.createNewFile();
				Bitmap pic = BitmapFactory.decodeResource(GezitechApplication.getContext().getResources(), R.drawable.launcher_96);
				FileOutputStream fos = new FileOutputStream(file);
				pic.compress(CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			}
		} catch(Throwable t) {
			t.printStackTrace();
			TEST_IMAGE = null;
		}
		return TEST_IMAGE;
	}
}
