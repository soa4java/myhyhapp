package com.gezitech.util;

import java.util.Date;

public class DateUtil {

	/**
	 * 几年几月几天前
	 * @param timeMillis 毫秒
	 * @return
	 */
	public static String getShortTime(long timeMillis){
		return getShortTime(new Date(timeMillis));
	}
	
	public static String getShortTime(Date date) {
		String shortstring = null; 
		if (date == null) return shortstring;
		long deltime = (System.currentTimeMillis() - date.getTime()) / 1000;
		if (deltime > 365 * 24 * 60 * 60) {
			shortstring = (int) (deltime / (365 * 24 * 60 * 60)) + "年前";
			return shortstring;
		}else if(deltime > 30 * 24 * 60 * 60){
			shortstring = (int) (deltime / (30 * 24 * 60 * 60)) + "月前";
			return shortstring;
		}else if (deltime > 24 * 60 * 60) {
			shortstring = (int) (deltime / (24 * 60 * 60)) + "天前";
			return shortstring;
		} else if (deltime > 60 * 60) {
			shortstring = (int) (deltime / (60 * 60)) + "小时前";
			return shortstring;
		} else if (deltime > 60) {
			shortstring = (int) (deltime / (60)) + "分钟前";
			return shortstring;
		} else if (deltime > 1) {
			shortstring = deltime + "秒前";
			return shortstring;
		} else {
			shortstring = "1秒前";
			return shortstring;
		}
	}
	
	public static void main(String[] arge){
		System.out.println("hello asdfkasdfkjasdfkl;k");
		int c =0;
		int b = 0;
		System.out.println("hello asdfkasdfkjasdfkl;k");
		//System.out.println(DateUtil.getShortTime(1365404428000l));
	}
}
