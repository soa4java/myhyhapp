package com.gezitech.util;
/***
 * 获取设备id
 */
import java.util.UUID;

import com.gezitech.basic.GezitechApplication;

import android.telephony.TelephonyManager;

public class UUIDUtil {
	public static String getUUID() {
		final TelephonyManager tm = (TelephonyManager) GezitechApplication.getContext().getSystemService(GezitechApplication.getContext().TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""+ android.provider.Settings.Secure.getString(GezitechApplication.getContext().getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(),((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		return deviceUuid.toString();
		//return androidId.hashCode()+tmDevice+tmSerial;
	}
}
