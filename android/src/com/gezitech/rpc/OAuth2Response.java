package com.gezitech.rpc;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.gezitech.basic.GezitechException;
import com.gezitech.config.Configuration;
import com.gezitech.http.HTMLEntity;
import com.gezitech.http.Response;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Super class of Weibo Response objects.
 * 
 * @see weibo4andriod.DirectMessage
 * @see weibo4andriod.Status
 * @see Friend.User
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class OAuth2Response implements java.io.Serializable {
    private static Map<String,SimpleDateFormat> formatMap = new HashMap<String,SimpleDateFormat>();
    private static final long serialVersionUID = 3519962197957449562L;
    private transient int rateLimitLimit = -1;
    private transient int rateLimitRemaining = -1;
    private transient long rateLimitReset = -1;
    

    public OAuth2Response() {
    }

    public OAuth2Response(Response res) {
       
    }

    protected static void ensureRootNodeNameIs(String rootName, Element elem) throws GezitechException {
        if (!rootName.equals(elem.getNodeName())) {
            throw new GezitechException("�޷�ʶ����ĵ���ڵ�:" + elem.getNodeName() + "���Ϸ���ڵ�:" + rootName + "��");
        }
    }

    protected static void ensureRootNodeNameIs(String[] rootNames, Element elem) throws GezitechException {
        String actualRootName = elem.getNodeName();
        for (String rootName : rootNames) {
            if (rootName.equals(actualRootName)) {
                return;
            }
        }
        String expected = "";
        for (int i = 0; i < rootNames.length; i++) {
            if (i != 0) {
                expected += " or ";
            }
            expected += rootNames[i];
        }
        throw new GezitechException("�޷�ʶ����ĵ���ڵ�:" + elem.getNodeName() + "���Ϸ���ڵ�:" + expected + "��");
    }

    protected static void ensureRootNodeNameIs(String rootName, Document doc) throws GezitechException {
        Element elem = doc.getDocumentElement();
        if (!rootName.equals(elem.getNodeName())) {
            throw new GezitechException("�޷�ʶ����ĵ���ڵ�:" + elem.getNodeName() + "���Ϸ���ڵ�" + rootName + "��");
        }
    }

    protected static boolean isRootNodeNilClasses(Document doc) {
        String root = doc.getDocumentElement().getNodeName();
        return "nil-classes".equals(root) || "nilclasses".equals(root);
    }

    protected static String getChildText( String str, Element elem ) {
        return HTMLEntity.unescape(getTextContent(str,elem));
    }

    protected static String getTextContent(String str, Element elem){
        NodeList nodelist = elem.getElementsByTagName(str);
        if (nodelist.getLength() > 0) {
            Node node = nodelist.item(0).getFirstChild();
            if (null != node) {
                String nodeValue = node.getNodeValue();
                return null != nodeValue ? nodeValue : "";
            }
        }
        return "";
     }

    /*modify by sycheng  add "".equals(str) */
    protected static int getChildInt(String str, Element elem) {
        String str2 = getTextContent(str, elem);
        if (null == str2 || "".equals(str2)||"null".equals(str)) {
            return -1;
        } else {
            return Integer.valueOf(str2);
        }
    }

    protected static long getChildLong(String str, Element elem) {
        String str2 = getTextContent(str, elem);
        if (null == str2 || "".equals(str2)||"null".equals(str)) {
            return -1;
        } else {
            return Long.valueOf(str2);
        }
    }

    protected static String getString(String name, JSONObject json, boolean decode) {
        String returnValue = null;
            try {
                returnValue = json.getString(name);
                if (decode) {
                    try {
                        returnValue = URLDecoder.decode(returnValue, "UTF-8");
                    } catch (UnsupportedEncodingException ignore) {
                    }
                }
            } catch (JSONException ignore) {
                // refresh_url could be missing
            }
        return returnValue;
    }

    protected static boolean getChildBoolean(String str, Element elem) {
        String value = getTextContent(str, elem);
        return Boolean.valueOf(value);
    }
    protected static Date getChildDate(String str, Element elem) throws GezitechException {
        return getChildDate(str, elem, "EEE MMM d HH:mm:ss z yyyy");
    	//return getChildDate(str, elem, "yyyy-MM-dd HH:mm");
    }

    protected static Date getChildDate(String str, Element elem, String format) throws GezitechException {
        return parseDate(getChildText(str, elem),format);
    }
    public static Date parseDate(String str, String format) throws GezitechException{
        if(str==null||"".equals(str)){
        	return null;
        }
    	SimpleDateFormat sdf = formatMap.get(format);
        if (null == sdf) {
            sdf = new SimpleDateFormat(format, Locale.ENGLISH);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            formatMap.put(format, sdf);
        }
        try {
            synchronized(sdf){
                // SimpleDateFormat is not thread safe
                return sdf.parse(str);
            }
        } catch (ParseException pe) {
            throw new GezitechException("Unexpected format(" + str + ") returned from sina.com.cn");
        }
    }

    protected static int getInt(String key, JSONObject json) throws JSONException {
        String str = json.getString(key);
        if(null == str || "".equals(str)||"null".equals(str)){
            return -1;
        }
        return Integer.parseInt(str);
    }

    protected static long getLong(String key, JSONObject json) throws JSONException {
        String str = json.getString(key);
        if(null == str || "".equals(str)||"null".equals(str)){
            return -1;
        }
        return Long.parseLong(str);
    }
    protected static boolean getBoolean(String key, JSONObject json) throws JSONException {
        String str = json.getString(key);
        if(null == str || "".equals(str)||"null".equals(str)){
            return false;
        }
        return Boolean.valueOf(str);
    }

    public int getRateLimitLimit() {
        return rateLimitLimit;
    }

    public int getRateLimitRemaining() {
        return rateLimitRemaining;
    }

    public long getRateLimitReset() {
        return rateLimitReset;
    }
}

