package com.gezitech.http;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLEncoder;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.gezitech.basic.GezitechException;
import com.gezitech.config.Configuration;

public class OAuth2HttpClient implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static boolean DEBUG = Configuration.getDebug();
	private String basic;
	private String proxyHost = Configuration.getProxyHost();
	private int proxyPort = Configuration.getProxyPort();
	private String proxyAuthUser = Configuration.getProxyUser();
	private String proxyAuthPassword = Configuration.getProxyPassword();
	private int connectionTimeout = Configuration.getConnectionTimeout();
	private int readTimeout = Configuration.getReadTimeout();
	private static boolean isJDK14orEarlier = false;
	private Map<String, String> requestHeaders = new HashMap<String, String>();
	private OAuth2 oauth2 = null;

	static {
		try {
			String versionStr = System
					.getProperty("java.specification.version");
			if (null != versionStr) {
				isJDK14orEarlier = 1.5d > Double.parseDouble(versionStr);
			}
		} catch (AccessControlException ace) {
			isJDK14orEarlier = true;
		}
	}

	public OAuth2HttpClient() {
		this.basic = null;
		setUserAgent(null);
		setRequestHeader("Accept-Encoding", "gzip");
		setRequestHeader("Cookie", "XDEBUG_SESSION=ECLIPSE_DBGP");// ����xdebuger����
		setRequestHeader("Proxy-Connection", " Keep-Alive");
		setRequestHeader("Pragma", "no-cache");
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = Configuration
				.getConnectionTimeout(connectionTimeout);

	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = Configuration.getReadTimeout(readTimeout);
	}

	public void setUserAgent(String ua) {
		setRequestHeader("User-Agent", Configuration.getUserAgent(ua));
	}

	public String getUserAgent() {
		return getRequestHeader("User-Agent");
	}

	public Response delete(String url, boolean authenticated)
			throws GezitechException {
		return httpRequest(url, null, authenticated, "DELETE");
	}

	public Response multPartURL(String url, PostParameter[] params,
			ImageItem item, boolean authenticated) throws GezitechException {
		PostMethod post = new PostMethod(url);
		try {
			org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
			long t = System.currentTimeMillis();
			Part[] parts = null;
			if (params == null) {
				parts = new Part[1];
			} else {
				parts = new Part[params.length + 1];
			}
			if (params != null) {
				int i = 0;
				for (PostParameter entry : params) {
					parts[i++] = new StringPart(entry.getName(),
							(String) entry.getValue());
				}
				parts[parts.length - 1] = new ByteArrayPart(item.getContent(),
						item.getName(), item.getImageType());
			}
			post.setRequestEntity(new MultipartRequestEntity(parts, post
					.getParams()));
			List<Header> headers = new ArrayList<Header>();

			if (authenticated) {
				if (basic == null && oauth2 == null) {
				}
				String authorization = null;
				if (null != oauth2) {
					// use OAuth
					// authorization = oauth.generateAuthorizationHeader( "POST"
					// , url, params, oauthToken);
				} else if (null != basic) {
					// use Basic Auth
					authorization = this.basic;
				} else {
					throw new IllegalStateException(
							"Neither user ID/password combination nor OAuth consumer key/secret combination supplied");
				}
				headers.add(new Header("Authorization", authorization));
				log("Authorization: " + authorization);
			}
			client.getHostConfiguration().getParams()
					.setParameter("http.default-headers", headers);
			client.executeMethod(post);

			Response response = new Response();
			response.setResponseAsString(post.getResponseBodyAsString());
			response.setStatusCode(post.getStatusCode());

			log("multPartURL URL:" + url + ", result:" + response + ", time:"
					+ (System.currentTimeMillis() - t));
			return response;
		} catch (Exception ex) {
			throw new GezitechException(ex.getMessage(), ex, -1);
		} finally {
			post.releaseConnection();
		}
	}

	public Response multPartURL(String fileParamName, String url,
			PostParameter[] params, File file, boolean authenticated)
			throws GezitechException {
		// if(BeelnnService.nowUser != null){
		// url += "/oauth_token/"+ BeelnnService.nowUser.getOauthToken()
		// +"/oauth_token_secret/"+ BeelnnService.nowUser.getOauthTokenSecrect()
		// +"";
		// }
		// url += "/from/2";
		PostMethod post = new PostMethod(url);
		org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
		try {
			long t = System.currentTimeMillis();
			Part[] parts = null;
			if (params == null) {
				parts = new Part[1];
			} else {
				parts = new Part[params.length + 1];
			}
			if (params != null) {
				int i = 0;
				for (PostParameter entry : params) {
					parts[i++] = new StringPart(entry.getName(),
							(String) entry.getValue(), "UTF-8");
				}
			}
			if (file != null) {
				FilePart filePart = new FilePart(fileParamName, file.getName(),
						file, new FileType().getMIMEType(file), "UTF-8");
				filePart.setTransferEncoding("binary");
				parts[parts.length - 1] = filePart;
			}
			post.setRequestEntity(new MultipartRequestEntity(parts, post
					.getParams()));
			List<Header> headers = new ArrayList<Header>();

			if (authenticated) {
				if (basic == null && oauth2 == null) {
				}
				String authorization = null;
				if (null != oauth2) {
					// use OAuth
					// authorization = oauth.generateAuthorizationHeader( "POST"
					// , url, params, oauthToken);
				} else if (null != basic) {
					// use Basic Auth
					authorization = this.basic;
				} else {
					throw new IllegalStateException(
							"Neither user ID/password combination nor OAuth consumer key/secret combination supplied");
				}
				headers.add(new Header("Authorization", authorization));
				log("Authorization: " + authorization);
			}
			headers.add(new Header("Accept-Encoding", "gzip, deflate"));
			headers.add(new Header("Cookie", "XDEBUG_SESSION=ECLIPSE_DBGP"));
			client.getHostConfiguration().getParams()
					.setParameter("http.default-headers", headers);
			client.getParams().setParameter(
					HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
			client.executeMethod(post);

			Response response = new Response();
			response.setResponseAsString(post.getResponseBodyAsString());
			response.setStatusCode(post.getStatusCode());

			log("multPartURL URL:" + url + ", result:" + response + ", time:"
					+ (System.currentTimeMillis() - t));
			return response;
		} catch (Exception ex) {
			throw new GezitechException(ex.getMessage(), ex, -1);
		} finally {
			post.releaseConnection();
			client = null;
		}
	}

	private static class ByteArrayPart extends PartBase {
		private byte[] mData;
		private String mName;

		public ByteArrayPart(byte[] data, String name, String type)
				throws IOException {
			super(name, type, "UTF-8", "binary");
			mName = name;
			mData = data;
		}

		protected void sendData(OutputStream out) throws IOException {
			out.write(mData);
		}

		protected long lengthOfData() throws IOException {
			return mData.length;
		}

		protected void sendDispositionHeader(OutputStream out)
				throws IOException {
			super.sendDispositionHeader(out);
			StringBuilder buf = new StringBuilder();
			buf.append("; filename=\"").append(mName).append("\"");
			out.write(buf.toString().getBytes());
		}
	}

	public Response post(String url, PostParameter[] postParameters,
			boolean authenticated) throws GezitechException {
		return httpRequest(url, postParameters, authenticated);
	}

	public Response get(String url, boolean authenticated)
			throws GezitechException {
		return httpRequest(url, null, authenticated);
	}

	protected Response httpRequest(String url, PostParameter[] postParams,
			boolean authenticated) throws GezitechException {
		int len = 1;
		PostParameter[] newPostParameters = postParams;
		String method = "GET";
		if (postParams != null) {
			method = "POST";
			len = postParams.length;
			newPostParameters = new PostParameter[len];
			for (int i = 0; i < postParams.length; i++) {
				newPostParameters[i] = postParams[i];
			}
		}
		// if(BeelnnService.nowUser != null){
		// url += "/oauth_token/"+ BeelnnService.nowUser.getOauthToken()
		// +"/oauth_token_secret/"+ BeelnnService.nowUser.getOauthTokenSecrect()
		// +"";
		// }
		// url += "/from/2";
		return httpRequest(url, newPostParameters, authenticated, method);
	}

	public Response httpRequest(String url, PostParameter[] postParams,
			boolean authenticated, String httpMethod) throws GezitechException {
		Response res = null;
		int responseCode = -1;
		HttpURLConnection hc = null;
		OutputStream os = null;
		try {
			hc = getConnection(url);
			hc.setDoInput(true);
			setHeaders(url, postParams, hc, authenticated, httpMethod);
			if (null != postParams || "POST".equals(httpMethod)) {
				hc.setRequestMethod("POST");
				hc.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				hc.setDoOutput(true);
				String postParam = "";
				if (postParams != null) {
					postParam = encodeParameters(postParams);
				}
				byte[] bytes = postParam.getBytes("UTF-8");
				hc.setRequestProperty("Content-Length",
						Integer.toString(bytes.length));
				os = hc.getOutputStream();
				if (os != null) {
					os.write(bytes);
					os.flush();
					os.close();
				}
			} else if ("DELETE".equals(httpMethod)) {
				hc.setRequestMethod("DELETE");
			} else {
				hc.setRequestMethod("GET");
			}
			res = new Response(hc);
			responseCode = hc.getResponseCode();
		} catch (Exception ex) {
			res = new Response(ex.getMessage());
		} finally {
			try {
				os.close();
			} catch (Exception ignore) {
			}
		}

		return res;
	}

	public static String encodeParameters(PostParameter[] postParams) {
		StringBuffer buf = new StringBuffer();
		for (int j = 0; j < postParams.length; j++) {
			if (j != 0) {
				buf.append("&");
			}
			try {
				buf.append(URLEncoder.encode(postParams[j].name, "UTF-8"))
						.append("=")
						.append(URLEncoder.encode(postParams[j].value, "UTF-8"));
			} catch (java.io.UnsupportedEncodingException neverHappen) {
			}
		}
		return buf.toString();
	}

	/**
	 * Ϊ������չ
	 * 
	 * @param postParams
	 * @return
	 */
	public static String encodeUrlParameters(PostParameter[] postParams) {
		StringBuffer buf = new StringBuffer();
		for (int j = 0; j < postParams.length; j++) {
			if (j != 0) {
				buf.append("&");
			}
			try {
				buf.append(URLEncoder.encode(postParams[j].name, "UTF-8"))
						.append("=")
						.append(URLEncoder.encode(
								(postParams[j].value == null || ""
										.equals(postParams[j].value)) ? ""
										: postParams[j].value, "UTF-8"));
			} catch (java.io.UnsupportedEncodingException neverHappen) {
			}
		}
		return buf.toString();
	}

	/**
	 * sets HTTP headers
	 * 
	 * @param connection
	 *            HttpURLConnection
	 * @param authenticated
	 *            boolean
	 */
	private void setHeaders(String url, PostParameter[] params,
			HttpURLConnection connection, boolean authenticated,
			String httpMethod) {
		if (authenticated) {
			if (basic == null && oauth2 == null) {
			}
			String authorization = null;
			if (null != oauth2) {
			} else if (null != basic) {
				authorization = this.basic;
			} else {
				throw new IllegalStateException(
						"Neither user ID/password combination nor OAuth consumer key/secret combination supplied");
			}
			connection.addRequestProperty("Authorization", authorization);
			log("Authorization: " + authorization);
		}

		for (String key : requestHeaders.keySet()) {
			connection.addRequestProperty(key, requestHeaders.get(key));
			log(key + ": " + requestHeaders.get(key));
		}
	}

	public void setRequestHeader(String name, String value) {
		requestHeaders.put(name, value);
	}

	public String getRequestHeader(String name) {
		return requestHeaders.get(name);
	}

	private HttpURLConnection getConnection(String url) throws IOException {
		HttpURLConnection con = null;
		if (proxyHost != null && !proxyHost.equals("")) {
			if (proxyAuthUser != null && !proxyAuthUser.equals("")) {
				Authenticator.setDefault(new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						if (getRequestorType().equals(RequestorType.PROXY)) {
							return new PasswordAuthentication(proxyAuthUser,
									proxyAuthPassword.toCharArray());
						} else {
							return null;
						}
					}
				});
			}
			final Proxy proxy = new Proxy(Type.HTTP,
					InetSocketAddress.createUnresolved(proxyHost, proxyPort));
			if (DEBUG) {
				log("Opening proxied connection(" + proxyHost + ":" + proxyPort
						+ ")");
			}
			con = (HttpURLConnection) new URL(url).openConnection(proxy);
		} else {
			con = (HttpURLConnection) new URL(url).openConnection();
		}
		if (connectionTimeout > 0 && !isJDK14orEarlier) {
			con.setConnectTimeout(connectionTimeout);
		}
		if (readTimeout > 0 && !isJDK14orEarlier) {
			con.setReadTimeout(readTimeout);
		}
		return con;
	}

	private static void log(String message) {
		if (DEBUG) {
			System.out.println("[" + new java.util.Date() + "]" + message);
		}
	}
}
