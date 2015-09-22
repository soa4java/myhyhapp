package com.gezitech.util;
/*package com.beelnn.util;

import java.lang.reflect.Field;

import com.beelnn.basic.BeelnnApplication;

import android.R.drawable;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

public class ResourceUtil {
	public static Drawable getDrawableByResName(String resName){

		//Class<com.beelnn.ui.lifehouse.R.drawable> c = R.drawable.class;
		Class<?> c = null;
		Field f = null;
		int resId = 0;
		try {
			f = (Field)c.getDeclaredField(resName);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			resId = f.getInt(R.drawable.class);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			Toast.makeText(BeelnnApplication.getContext(), "资源不存在"+f.getName(), 3000);
			return BeelnnApplication.getContext().getResources().getDrawable(R.drawable.loading);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			Toast.makeText(BeelnnApplication.getContext(), "资源不存在"+f.getName(), 3000);
			return BeelnnApplication.getContext().getResources().getDrawable(R.drawable.loading);
		}
		return BeelnnApplication.getContext().getResources().getDrawable(resId);
	}
	
	public static Drawable getDrawable(String resName){	
		Class<com.beelnn.ui.lifehouse.R.drawable> c = R.drawable.class;
		Field f = null;
		int resId = 0;
		try {
			f = (Field)c.getDeclaredField(resName);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			resId = f.getInt(R.drawable.class);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return BeelnnApplication.getContext().getResources().getDrawable(resId);
	}
}
*/