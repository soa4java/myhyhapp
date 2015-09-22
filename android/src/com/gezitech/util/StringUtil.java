package com.gezitech.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.gezitech.basic.GezitechApplication;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
//字符串操作工具集 
public class StringUtil {
	private static final String PASSWORD_CRYPT_KEY = "thinksns";
	private final static String DES = "DES";

	public static boolean isEmpty(String s) {
		return null == s || "".equals(s);
	}

	public static String curString(String s, int len) {
		if (s == null)
			return "";
		if (s.length() < len)
			return s;
		return s.substring(0, len) + "...";
	}

	/**
	 * ����
	 * 
	 * @param src
	 *            ���Դ
	 * @param key
	 *            ��Կ�����ȱ�����8�ı���
	 * @return ���ؼ��ܺ�����
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
		// DES�㷨Ҫ����һ�������ε������Դ
		SecureRandom sr = new SecureRandom();
		// ��ԭʼ�ܳ���ݴ���DESKeySpec����
		DESKeySpec dks = new DESKeySpec(key);
		// ����һ���ܳ׹�����Ȼ�������DESKeySpecת����
		// һ��SecretKey����
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher����ʵ����ɼ��ܲ���
		Cipher cipher = Cipher.getInstance(DES);
		// ���ܳ׳�ʼ��Cipher����
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
		// ���ڣ���ȡ��ݲ�����
		// ��ʽִ�м��ܲ���
		return cipher.doFinal(src);
	}

	/**
	 * ����
	 * 
	 * @param src
	 *            ���Դ
	 * @param key
	 *            ��Կ�����ȱ�����8�ı���
	 * @return ���ؽ��ܺ��ԭʼ���
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
		// DES�㷨Ҫ����һ�������ε������Դ
		SecureRandom sr = new SecureRandom();
		// ��ԭʼ�ܳ���ݴ���һ��DESKeySpec����
		DESKeySpec dks = new DESKeySpec(key);
		// ����һ���ܳ׹�����Ȼ�������DESKeySpec����ת����
		// һ��SecretKey����
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher����ʵ����ɽ��ܲ���
		Cipher cipher = Cipher.getInstance(DES);
		// ���ܳ׳�ʼ��Cipher����
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
		// ���ڣ���ȡ��ݲ�����
		// ��ʽִ�н��ܲ���
		return cipher.doFinal(src);
	}

	/**
	 * �������
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public final static String decrypt(String data) {
		try {
			return new String(decrypt(hex2byte(data.getBytes()),
					PASSWORD_CRYPT_KEY.getBytes()));
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * �������
	 * 
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public final static String encrypt(String password) {
		try {
			return byte2hex(encrypt(password.getBytes(),
					PASSWORD_CRYPT_KEY.getBytes()));
		} catch (Exception e) {
			return null;
		}
	}

	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}

	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException("���Ȳ���ż��");
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}

	// ΢�������ַ�ת��
	public static SpannableString formatWeiboContent(String originString) {
		SpannableString ss = new SpannableString(originString);
		Pattern p = Pattern.compile("@[^:]*:");
		Matcher m = p.matcher(originString);
		boolean isFound = m.find();
		int i = 0;
		while (isFound) {
			String s = m.group(0);
			int start = m.start();
			int end = m.end();
			ss.setSpan(new ForegroundColorSpan(Color.BLUE), start,
					start + s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			isFound = m.find(end);
		}
		return ss;

	}

	/**
	 * ��֤�ֻ���롢�绰�����Ƿ���Ч �ֻ��ǰ���86�����Ҳ���� ����ͨ ���� *���й���ͨ+�й���ͨ���ֻ���뿪ͷ����
	 * 130��131��132��145��155��156��185��186 ���� * ���ƶ� ���� * ���й��ƶ�+�й���ͨ���ֻ���뿪ͷ����
	 * 134��135��136��137��138��139��147��150��151��152��157��158��159��182��183��187��188 ���� *
	 * �µ��� ���� * ���й���� <http://baike.baidu.com/view/3214.htm>+�й���ͨ���ֻ���뿪ͷ����
	 * 133��153��189��180 ��� 3/4λ��ţ����֣�+ ��-�� + 7/8λ�����֣�+ ��-��+����λ����
	 * ˵������-��+����λ���ޣ���ο��п���
	 */
	public static boolean isPhone(String photo) {
		if (photo
				.matches("(^[0-9]{3,4}-[0-9]{3,8}$)|^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|2|3|5|6|7|8|9])\\d{8}$")) {
			return true;
		} else if (photo
				.matches("(^[0-9]{3,4}-[0-9]{3,8}-[0-9]{0,100}$)|^((\\+86)|(86))?(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|2|3|5|6|7|8|9])\\d{8}$")) {
			return true;
		}
		return false;
	}

	/**
	 * ��ȡ��Ӣ�Ļ����ַ�
	 * 
	 * @param str
	 *            ԭʼ�ַ�
	 * @param num
	 *            Ӣ�ĳ���
	 * @return
	 */
	public static String subString(String str, int num) {
		int max = num;
		try {
			max = trimGBK(str.getBytes("GBK"), num);
		} catch (Exception e) {
		}
		int sum = 0;
		if (str != null && str.length() > max) {
			StringBuilder sb = new StringBuilder(max);
			for (int i = 0; i < str.length(); i++) {
				int c = str.charAt(i);
				// if ((c & 0xff00) != 0)
				// sum += 2;
				// else
				sum += 1;
				if (sum <= max)
					sb.append((char) c);
				else
					break;
			}
			// return sb.append("...").toString();
			return sb.toString();
		} else
			return str != null ? str : "";
	}

	private static int trimGBK(byte[] buf, int n) {
		int num = 0;
		boolean bChineseFirstHalf = false;
		if (buf.length < n)
			return buf.length;
		for (int i = 0; i < n; i++) {
			if (buf[i] < 0 && !bChineseFirstHalf) {
				bChineseFirstHalf = true;
			} else {
				num++;
				bChineseFirstHalf = false;
			}
		}
		return num;
	}

	/**
	 * ɾ��input�ַ��е�html��ǩ
	 * 
	 * @param input
	 *            html�ļ�
	 * @param length
	 *            ��ȡ�ı����� -1Ϊ������
	 * @return
	 */
	public static String splitAndFilterString(String input, int length) {
		if (input == null || input.trim().equals("")) {
			return "";
		}
		// ȥ������htmlԪ��,
		String str = input.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll(
				"<[^>]*>", "");
		str = str.replaceAll("[(/>)<]", "");
		int len = str.length();
		if (len <= length || length == -1) {
			return str;
		} else {
			str = str.substring(0, length);
			str += "......";
		}
		return str;
	}

	/**
	 * ��ȡ�ı��е绰����
	 * ���û�оͷ��س���Ϊ0������
	 * @return String[] 
	 */
	public static String[] extractPhoneNo(String text) {
		if(text==null||"".equals(text))return new String[0];
		String[] phones = null;
		ArrayList<String> matchers = new ArrayList<String>();		
		// text="13112341234,010-12456789,����ʲô01012456789QQ,WW(010)12456789��,��ʲô 00861012456789,���+861012456789��";
		Pattern pattern = Pattern.compile("(1[3458]\\d{9})|((\\+[0-9]{2,4})?\\(?[0-9]+\\)?-?)?[0-9]{7,8}");
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			matchers.add(matcher.group());
		}
		phones = new String[matchers.size()];
		matchers.toArray(phones);
		return phones;
	}
	/**
	 * 判断某个字符串是否存在于数组中
	 * 
	 * @param stringArray
	 *            原数组
	 * @param source
	 *            查找的字符串
	 * @return 是否找到
	 */
	public static boolean contains(String[] stringArray, String source) {
		// 转换为list
		List<String> tempList = Arrays.asList(stringArray);
		// 利用list的包含方法,进行判断
		if (tempList.contains(source)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 字符串数组转分割的字符串
	 * 
	 * @param strArray
	 *            字符串数组
	 * @param separator
	 *            分割符 如“，”
	 * @return
	 */
	public static String stringArrayJoin(String[] strArray, String separator) {
		StringBuffer strbuf = new StringBuffer();
		for (int i = 0; i < strArray.length; i++) {
			strbuf.append(separator).append(strArray[i]);
		}
		return strbuf.deleteCharAt(0).toString();
	}

	/**
	 * 判断是否是email
	 * 
	 * @param mail
	 *            字符串
	 * @return true 是，false 不是
	 */
	public static boolean isEmail(String email) {
		if (null == email || "".equals(email))
			return false;
		Pattern pattern = Pattern.compile("^/w+([-.]/w+)*@/w+([-]/w+)*/.(/w+([-]/w+)*/.)*[a-z]{2,3}$");
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            return true;
        }
        return false;
	}
	/**
	 * 判断是否是数字
	 * @param str 字符串
	 * @return true 是，false 不是
	 */
	public static boolean isNumeric(String str){ 
	    Pattern pattern = Pattern.compile("[0-9]*"); 
	    return pattern.matcher(str).matches();    
	 } 
	/**
	 * 获取链接中得文件名称
	 * @param url
	 * @return
	 */
	public static String getUrlFileName(String url){
		if(url == null || "".equals(url)){
			return IOUtil.generateRandomFilename();
		}
		Pattern p = Pattern.compile("https?://[^/]+", Pattern.CASE_INSENSITIVE);//取出主机块
		Matcher m = p.matcher(url);
		if(!m.find()){
			return IOUtil.generateRandomFilename();
		}
		String host = m.group();//主机部分
		String local = url.substring(host.length());//目录部分
		String fileName = null;//文件名部分
		if(local == null || "".equals(local) || local.equals("/")){
			local = "/";
			fileName = "";//只有域名
		}else{			
			p = Pattern.compile("[\\?#=].*$");//匹配地址后面的参数
			m = p.matcher(local);
		
			p = Pattern.compile("[^/]*\\.[^/]*");//匹配文件名
			m = p.matcher(local);
			if(m.find()){
				fileName = m.group();
				
			}else fileName = IOUtil.generateRandomFilename();
		}
		return fileName;
	}
	/**
	 * md5 加密字符串
	 * @param s
	 * @return
	 */
	 public final static String MD5(String s) {
	        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};       
	        try {
	            byte[] btInput = s.getBytes();
	            // ���MD5ժҪ�㷨�� MessageDigest ����
	            MessageDigest mdInst = MessageDigest.getInstance("MD5");
	            // ʹ��ָ�����ֽڸ���ժҪ
	            mdInst.update(btInput);
	            // �������
	            byte[] md = mdInst.digest();
	            // ������ת����ʮ����Ƶ��ַ���ʽ
	            int j = md.length;
	            char str[] = new char[j * 2];
	            int k = 0;
	            for (int i = 0; i < j; i++) {
	                byte byte0 = md[i];
	                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
	                str[k++] = hexDigits[byte0 & 0xf];
	            }
	            return new String(str);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	 //把字符串反编码
	 public static String stringDecode( String str ){
		 if( str.equals("") || str.equals("null") || str == null ){
			 return "";
		 }
		 try {
			return URLDecoder.decode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return "";
		}
	 }
	 //把字符串编码
	 public static String stringEncode( String str ){
		 if( str.equals("") || str.equals("null") || str == null ){
			 return "";
		 }
		 try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return "";
		}
	 }
}
