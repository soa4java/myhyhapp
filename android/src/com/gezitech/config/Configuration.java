package com.gezitech.config;

import java.security.AccessControlException;
import java.util.Properties;

public class Configuration {
	private static Properties defaultProperty;

	static {
		init();
	}

	static void init() {
		defaultProperty = new Properties();
		defaultProperty.setProperty("gezitech.debug", "false");
		defaultProperty.setProperty("gezitech.http.useSSL", "false");
		//主机地址
		defaultProperty.setProperty("gezitech.host", "www.hanyihan.net");
		//defaultProperty.setProperty("gezitech.host", "shout.star-lai.cn/index.php");
		
		defaultProperty.setProperty("gezitech.appPath",defaultProperty.getProperty("gezitech.host") + "/");
		
		defaultProperty.setProperty("gezitech.storePath",defaultProperty.getProperty("gezitech.host") + "/gezitech_store/");
		//客户端版本号
		defaultProperty.setProperty("gezitech.clientVersion", "");
		//服务端版本号
		defaultProperty.setProperty("gezitech.serverVersion",defaultProperty.getProperty("gezitech.appPath") + "api/gezitech_community_version.json");
		//apk下载地址
		defaultProperty.setProperty("gezitech.apkpath",
		defaultProperty.getProperty("gezitech.appPath") + "gezitech_app.apk");		
		defaultProperty.setProperty("gezitech.emotiondir",defaultProperty.getProperty("gezitech.appPath")
								+ "/public/themes/classic2/images/expression/miniblog/");
		defaultProperty.setProperty("gezitech.uploaddir", "data/uploads/");
		defaultProperty.setProperty(
						"gezitech.http.userAgent",
						"Mozilla/5.0 (Linux; U; Android 0.5; en-us) AppleWebKit/522+ (KHTML, like Gecko) Safari/419.3");
		defaultProperty.setProperty("gezitech.user", "");
		defaultProperty.setProperty("gezitech.password", "");
		defaultProperty.setProperty("gezitech.http.proxyHost", "");
		defaultProperty.setProperty("gezitech.http.proxyHost.fallback",
				"http.proxyHost");
		defaultProperty.setProperty("gezitech.http.proxyUser", "");
		defaultProperty.setProperty("gezitech.http.proxyPassword", "");
		defaultProperty.setProperty("gezitech.http.proxyPort", "");
		defaultProperty.setProperty("gezitech.http.proxyPort.fallback",
				"http.proxyPort");
		//链接时间
		defaultProperty.setProperty("gezitech.http.connectionTimeout", "120000");
		//链接超时
		defaultProperty.setProperty("gezitech.http.readTimeout", "120000");
		defaultProperty.setProperty("gezitech.http.retryCount", "3");
		//间隔多少秒重试
		defaultProperty.setProperty("gezitech.http.retryIntervalSecs", "10");
		defaultProperty.setProperty("gezitech.oauth2.clientId","1209281436113385");
		defaultProperty.setProperty("gezitech.oauth2.clientSecret","f29685805d02903eefe8261a9f483f5b");			
			
		defaultProperty.setProperty("gezitech.async.numThreads", "1");
		defaultProperty.setProperty("gezitech.store.id", "3");
		
		defaultProperty.setProperty("sina.consumer_key","2303114424"); 
		defaultProperty.setProperty("sina.redirect_url","http://www.gezitech.com");
		
		defaultProperty.setProperty("qq.app_id","100418414");  
		
		
		try {
			Class.forName("dalvik.system.VMRuntime");
			defaultProperty.setProperty("gezitech.dalvik", "true");
		} catch (ClassNotFoundException cnfe) {
			defaultProperty.setProperty("gezitech.dalvik", "false");
		}

	}

	public static boolean useSSL() {
		return getBoolean("gezitech.http.useSSL");
	}

	public static String getScheme() {
		return useSSL() ? "https://" : "http://";
	}

	public static String getAppPath() {
		return getProperty("gezitech.appPath");
	}

	public static String getStorePath() {
		return getProperty("gezitech.storePath");
	}

	public static String getCilentVersion() {
		return getProperty("gezitech.clientVersion");
	}

	public static String getCilentVersion(String clientVersion) {
		return getProperty("gezitech.clientVersion", clientVersion);
	}

	public static String getSource() {
		return getProperty("gezitech.source");
	}

	public static String getSource(String source) {
		return getProperty("gezitech.source", source);
	}

	public static String getProxyHost() {
		return getProperty("gezitech.http.proxyHost");
	}

	public static String getProxyHost(String proxyHost) {
		return getProperty("gezitech.http.proxyHost", proxyHost);
	}

	public static String getProxyUser() {
		return getProperty("gezitech.http.proxyUser");
	}

	public static String getProxyUser(String user) {
		return getProperty("gezitech.http.proxyUser", user);
	}

	public static String getClientURL() {
		return getProperty("gezitech.clientURL");
	}

	public static String getClientURL(String clientURL) {
		return getProperty("gezitech.clientURL", clientURL);
	}

	public static String getProxyPassword() {
		return getProperty("gezitech.http.proxyPassword");
	}

	public static String getProxyPassword(String password) {
		return getProperty("gezitech.http.proxyPassword", password);
	}

	public static int getProxyPort() {
		return getIntProperty("gezitech.http.proxyPort");
	}

	public static int getProxyPort(int port) {
		return getIntProperty("gezitech.http.proxyPort", port);
	}

	public static int getConnectionTimeout() {
		return getIntProperty("gezitech.http.connectionTimeout");
	}

	public static int getConnectionTimeout(int connectionTimeout) {
		return getIntProperty("gezitech.http.connectionTimeout",
				connectionTimeout);
	}

	public static int getReadTimeout() {
		return getIntProperty("gezitech.http.readTimeout");
	}

	public static int getReadTimeout(int readTimeout) {
		return getIntProperty("gezitech.http.readTimeout", readTimeout);
	}

	public static int getRetryCount() {
		return getIntProperty("gezitech.http.retryCount");
	}

	public static int getRetryCount(int retryCount) {
		return getIntProperty("gezitech.http.retryCount", retryCount);
	}

	public static int getRetryIntervalSecs() {
		return getIntProperty("gezitech.http.retryIntervalSecs");
	}

	public static int getRetryIntervalSecs(int retryIntervalSecs) {
		return getIntProperty("gezitech.http.retryIntervalSecs",
				retryIntervalSecs);
	}

	public static String getUser() {
		return getProperty("gezitech.user");
	}

	public static String getUser(String userId) {
		return getProperty("gezitech.user", userId);
	}

	public static String getPassword() {
		return getProperty("gezitech.password");
	}

	public static String getPassword(String password) {
		return getProperty("gezitech.password", password);
	}

	public static String getUserAgent() {
		return getProperty("gezitech.http.userAgent");
	}

	public static String getUserAgent(String userAgent) {
		return getProperty("gezitech.http.userAgent", userAgent);
	}

	public static String getOAuthConsumerKey() {
		return getProperty("gezitech.oauth.consumerKey");
	}

	public static String getOAuthConsumerKey(String consumerKey) {
		return getProperty("gezitech.oauth.consumerKey", consumerKey);
	}

	public static String getOAuthConsumerSecret() {
		return getProperty("gezitech.oauth.consumerSecret");
	}

	public static String getOAuthConsumerSecret(String consumerSecret) {
		return getProperty("gezitech.oauth.consumerSecret", consumerSecret);
	}

	public static boolean getBoolean(String name) {
		String value = getProperty(name);
		return Boolean.valueOf(value);
	}

	public static int getIntProperty(String name) {
		String value = getProperty(name);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	public static int getIntProperty(String name, int fallbackValue) {
		String value = getProperty(name, String.valueOf(fallbackValue));
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	public static long getLongProperty(String name) {
		String value = getProperty(name);
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	public static String getProperty(String name) {
		return getProperty(name, null);
	}

	public static String getProperty(String name, String fallbackValue) {
		String value;
		try {
			value = System.getProperty(name, fallbackValue);
			if (null == value) {
				value = defaultProperty.getProperty(name);
			}
			if (null == value) {
				String fallback = defaultProperty.getProperty(name
						+ ".fallback");
				if (null != fallback) {
					value = System.getProperty(fallback);
				}
			}
		} catch (AccessControlException ace) {
			// Unsigned applet cannot access System properties
			value = fallbackValue;
		}
		return replace(value);
	}

	private static String replace(String value) {
		if (null == value) {
			return value;
		}
		String newValue = value;
		int openBrace = 0;
		if (-1 != (openBrace = value.indexOf("{", openBrace))) {
			int closeBrace = value.indexOf("}", openBrace);
			if (closeBrace > (openBrace + 1)) {
				String name = value.substring(openBrace + 1, closeBrace);
				if (name.length() > 0) {
					newValue = value.substring(0, openBrace)
							+ getProperty(name)
							+ value.substring(closeBrace + 1);

				}
			}
		}
		if (newValue.equals(value)) {
			return value;
		} else {
			return replace(newValue);
		}
	}

	public static int getNumberOfAsyncThreads() {
		return getIntProperty("gezitech.async.numThreads");
	}

	public static boolean getDebug() {
		return getBoolean("gezitech.debug");

	}

	public static String getUploadPath() {
		return Configuration.getScheme() + Configuration.getAppPath()
				+ defaultProperty.getProperty("gezitech.uploaddir");
	}
}
