package com.gezitech.util;

import java.util.Calendar;
import java.util.Date;

//获取时间
public class CalendarUtil {
	//当前时间
	public static int getCalendarString(int type) {
		return getCalendarString(type, 0);
	}
	//传时间
	public static int getCalendarString(int type, long setTime) {
		Calendar cal = Calendar.getInstance();
		if (setTime > 0) {
			cal.setTime(new Date(setTime));
		}
		int value = 0;
		switch (type) {
		case Calendar.YEAR: // 年
			value = cal.get(Calendar.YEAR);
			break;
		case Calendar.MONTH: // 月
			value = cal.get(Calendar.MONTH) + 1;
			break;
		case Calendar.DAY_OF_MONTH: // 日
			value = cal.get(Calendar.DAY_OF_MONTH);
			break;
		case Calendar.HOUR_OF_DAY:// 小时
			value = cal.get(Calendar.HOUR_OF_DAY);
			break;
		case Calendar.MINUTE:// 分
			value = cal.get(Calendar.MINUTE);
			break;
		case Calendar.SECOND:// 秒
			value = cal.get(Calendar.SECOND);
			break;
		case Calendar.AM_PM: // 0 上午 1 下午
			value = cal.get(Calendar.AM_PM);
			break;
		case Calendar.WEEK_OF_YEAR: // 当前年的第几周
			value = cal.get(Calendar.WEEK_OF_YEAR);
			break;
		case Calendar.WEEK_OF_MONTH: // 当前月的第几周
			value = cal.get(Calendar.WEEK_OF_MONTH);
			break;
		case Calendar.DAY_OF_YEAR: // 当前年的第几天
			value = cal.get(Calendar.DAY_OF_YEAR);
			break;
		}
		return value;
	}
}
