package com.gezitech.util;
/**
 * 
 * @author xiaobai
 * 2014-11-8
 * @todo( 大小写转换 )
 */
public class ChangeCaseUtil {
	/**
	   * 把字符串的首字母改为大写
	   * 
	  * @param str
	   *            :
	   * @return String
	   */
	public static String updateStr(String str) {
	   char c = (char) (str.charAt(0) - 32);
	   String s = str.substring(1, str.length());
	   return c + s;
	}
	/**
	   * 把字符串的首字母改为小写
	   * 
	  * @param str
	   *            :
	   * @return String
	   */
	public static String updateSmall(String str) {
	   char c = (char) (str.charAt(0) + 32);
	   String s = str.substring(1, str.length());
	   return c + s;
	}
}
