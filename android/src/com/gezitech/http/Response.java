package com.gezitech.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.gezitech.basic.GezitechException;
import com.gezitech.config.Configuration;
import com.gezitech.util.StringUtil;

public class Response {
	private final static boolean DEBUG = Configuration.getDebug();
	private static ThreadLocal<DocumentBuilder> builders = new ThreadLocal<DocumentBuilder>() {
		@Override
		protected DocumentBuilder initialValue() {
			try {
				return DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();
			} catch (ParserConfigurationException ex) {
				throw new ExceptionInInitializerError(ex);
			}
		}
	};
	private int statusCode;
	private Document responseAsDocument = null;
	private String responseAsString = null;
	private InputStream is;
	private HttpURLConnection con;
	private boolean streamConsumed = false;
	public int contentLength;

	public Response() {
	}

	public Response(HttpURLConnection con) throws IOException {
		this.con = con;
		this.statusCode = con.getResponseCode();
		if (null == (is = con.getErrorStream())) {
			is = con.getInputStream();
		}
		if (null != is && "gzip".equals(con.getContentEncoding())) {
			is = new GZIPInputStream(is);
		}
		this.contentLength = con.getContentLength();
	}

	public Response(String content) {
		this.responseAsString = content;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getResponseHeader(String name) {
		if (con != null)
			return con.getHeaderField(name);
		else
			return null;
	}

	public InputStream asStream() {
		if (streamConsumed) {
			throw new IllegalStateException("Stream has already been consumed.");
		}
		return is;
	}

	/****************************************************
	 * ��ȡ����ֵ�ַ��ʽ
	 * 
	 * @return
	 * @throws GezitechException
	 */
	public String asString() throws GezitechException {
		if (null == responseAsString) {
			BufferedReader br;
			try {
				InputStream stream = asStream();
				if (null == stream) {
					return null;
				}
				br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
				StringBuffer buf = new StringBuffer();
				String line;
				while (null != (line = br.readLine())) {
					buf.append(line).append("\n");
				}
				this.responseAsString = buf.toString();

				log(responseAsString);
				stream.close();
				con.disconnect();
				streamConsumed = true;
			} catch (NullPointerException npe) {
				throw new GezitechException(npe.getMessage(), npe);
			} catch (IOException ioe) {
				throw new GezitechException(ioe.getMessage(), ioe);
			}
		}
		return responseAsString;
	}

	/*****************************************************************
	 * ����xml�ĵ���ʽ���
	 * 
	 * @return
	 * @throws GezitechException
	 */
	public Document asDocument() throws GezitechException {
		if (null == responseAsDocument) {
			try {
				this.responseAsDocument = builders.get().parse(
						new ByteArrayInputStream(asString().getBytes("UTF-8")));
			} catch (SAXException saxe) {
				throw new GezitechException("������ݸ�ʽ����ȷ:\n"
						+ responseAsString, saxe);
			} catch (IOException ioe) {
				throw new GezitechException(
						"There's something with the connection.", ioe);
			}
		}
		return responseAsDocument;
	}

	/******************************************************************
	 * ������ֵ��װ��JSONObject ����
	 * 
	 * @return
	 * @throws GezitechException
	 */
	public JSONObject asJSONObject() throws GezitechException {
		try {
			String json = asString();
			if (!StringUtil.isEmpty(json))
				json = json.trim();
			if (!json.startsWith("{")) {
				json = json.substring(1);
			}
			if (!json.endsWith("}")) {
				json = json.substring(0, json.length() - 1);
			}
			return new JSONObject(json);
		} catch (JSONException jsone) {
			throw new GezitechException(jsone.getMessage() + ":"
					+ this.responseAsString, jsone);
		}
	}

	/***************************************************************
	 * ������ֵ��װ��JSONArray����
	 * 
	 * @return
	 * @throws GezitechException
	 */
	public JSONArray asJSONArray() throws GezitechException {
		try {
			String json = asString();
			if (!json.startsWith("[")) {
				json = json.substring(1);
			}
			if (!json.endsWith("]")) {
				json = json.substring(0, json.length() - 1);
			}
			return new JSONArray(json);
		} catch (Exception jsone) {
			throw new GezitechException(jsone.getMessage() + ":"
					+ this.responseAsString, jsone);
		}
	}

	public InputStreamReader asReader() {
		try {
			return new InputStreamReader(is, "UTF-8");
		} catch (java.io.UnsupportedEncodingException uee) {
			return new InputStreamReader(is);
		}
	}

	public void disconnect() {
		try {
			con.disconnect();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static Pattern escaped = Pattern.compile("&#([0-9]{3,5});");

	public static String unescape(String original) {
		Matcher mm = escaped.matcher(original);
		StringBuffer unescaped = new StringBuffer();
		while (mm.find()) {
			mm.appendReplacement(unescaped, Character.toString((char) Integer
					.parseInt(mm.group(1), 10)));
		}
		mm.appendTail(unescaped);
		return unescaped.toString();
	}

	@Override
	public String toString() {
		if (null != responseAsString) {
			return responseAsString;
		}
		return "Response{" + "statusCode=" + statusCode + ", response="
				+ responseAsDocument + ", responseString='" + responseAsString
				+ '\'' + ", is=" + is + ", con=" + con + '}';
	}

	private void log(String message) {
		if (DEBUG) {
			System.out.println("[" + new java.util.Date() + "]" + message);
		}
	}

	private void log(String message, String message2) {
		if (DEBUG) {
			log(message + message2);
		}
	}

	public String getResponseAsString() {
		return responseAsString;
	}

	public void setResponseAsString(String responseAsString) {
		this.responseAsString = responseAsString;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
}
