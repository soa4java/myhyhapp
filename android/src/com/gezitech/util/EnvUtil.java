package com.gezitech.util;
/**
 * android������⹤��
 * @author qtby-heyao
 *
 */
public class EnvUtil {
	//�ж�sd���Ƿ����
	public static boolean existSDcard()
    {
        if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState()))
        {
            return true;
        }
        else{
            return false;
        }
    }
}
