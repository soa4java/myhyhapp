package com.gezitech.service.util;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.Arrays;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.gezitech.basic.GezitechEntity;
import com.gezitech.entity.PageList;
import com.gezitech.util.ChangeCaseUtil;
/**
 * 
 * 利用 SAX 解析xml文件
 * @author xiaobai
 * 
 */
public class SAXParserContentHandler extends DefaultHandler {
	private GezitechEntity javaBean = null;
	// 标签名称
	private String tag;
	private PageList list = null;
	//配置扫描结束的标签
	private static String[] endTag = {
		"message"
	};

	/**
	 * 用 SAX 解析xml文件
	 * @param XMLstr xml 字符串
	 * @param javaBean  实体类对象  返回实体类的一个list集合
	 * @throws Exception
	 */
	public PageList parseReadXml(String XMLstr, GezitechEntity javaBean)
			throws Exception {
		this.javaBean = javaBean;
	
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		XMLReader reader = parser.getXMLReader();
		reader.setContentHandler(this);
		StringReader sr = new StringReader(XMLstr);
		InputSource is = new InputSource(sr);
		reader.parse( is );
		
		return list;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (null != tag && !"".equals(tag)) {
			if (javaBean != null) {
				String data = new String(ch, start, length);
				Field[] fields = javaBean.getClass().getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					if ( tag.equals(fields[i].getName())) {
						setAttribute(javaBean, fields[i].getName(), data, new Class[] { fields[i].getType() } );
					}
				}
			}
		}
	}

	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		//这里要添加判断 碰到什么标签 插入arraylist集合
		if (null != localName && !"".equals(localName)) {
	        int result = Arrays.binarySearch(endTag, localName); 
			if( result >= 0 ) list.add( javaBean );
		}
		tag = null;
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		list = new PageList();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		tag = qName;
		Field[] fields = list.getClass().getDeclaredFields();
		for (int i = 0; i < attributes.getLength(); i++) {
			for (int j = 0; j < fields.length; j++) {
				if (attributes.getQName(i).equals(fields[j].getName())) {
					setAttributeMethod(list, attributes.getQName(i),attributes.getValue(i),new Class[] { fields[j].getType() });
				}
			}
		}
	}
	/**
	 * @param object 类
	 * @param setName  方法名
	 * @param setValue 方法设置
	 * @param obj  属性类型
	 * @throws Exception
	 */
	public void setAttribute(Object object, String setName, String setValue,
			Class[] obj) {
		Field field;
		try {
			field = object.getClass().getField( setName );
			if (obj[0].equals(Integer.class) || obj[0].equals(int.class)) {
				field.setInt(object, new Integer(setValue) );
			}
			if (obj[0].equals(Float.class) || obj[0].equals(float.class)) {
				field.setFloat(object, new Float(setValue) );
			}
			if (obj[0].equals(Short.class) || obj[0].equals(short.class)) {
				field.setShort(object, new Short(setValue) );
			}
			if (obj[0].equals(Byte.class) || obj[0].equals(byte.class)) {
				field.setByte(object, new Byte(setValue) );
			}
			if (obj[0].equals(Double.class) || obj[0].equals(double.class)) {
				field.setDouble(object, new Double(setValue) );
			}
			if (obj[0].equals(Long.class) || obj[0].equals(long.class)) {
				field.setLong(object, new Long(setValue) );
			}
			if (obj[0].equals(Boolean.class) || obj[0].equals(boolean.class)) {
				field.setBoolean(object, new Boolean(setValue));
			}
			if (obj[0].equals(String.class)) {
				field.set(object, new String(setValue));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * TODO( 设置page公用参数  )
	 */
	public void setAttributeMethod(Object object, String setName, String setValue,Class[] obj) {
		Method method;
		try {
			method = object.getClass().getMethod("set" + ChangeCaseUtil.updateStr( setName ),
					obj[0]);
			if (obj[0].equals(Integer.class) || obj[0].equals(int.class)) {
				method.invoke(object, new Integer(setValue));
			}
			if (obj[0].equals(Float.class) || obj[0].equals(float.class)) {
				method.invoke(object, new Float(setValue));
			}
			if (obj[0].equals(Short.class) || obj[0].equals(short.class)) {
				method.invoke(object, new Short(setValue));
			}
			if (obj[0].equals(Byte.class) || obj[0].equals(byte.class)) {
				method.invoke(object, new Byte(setValue));
			}
			if (obj[0].equals(Double.class) || obj[0].equals(double.class)) {
				method.invoke(object, new Double(setValue));
			}
			if (obj[0].equals(Date.class)) {
				method.invoke(object, new Date(new Long(setValue)));
			}
			if (obj[0].equals(java.util.Date.class)) {
				method.invoke(object, new java.util.Date(setValue));
			}
			if (obj[0].equals(Long.class) || obj[0].equals(long.class)) {
				method.invoke(object, new Long(setValue));
			}
			if (obj[0].equals(Boolean.class) || obj[0].equals(boolean.class)) {
				method.invoke(object, new Boolean(setValue));
			}
			if (obj[0].equals(String.class)) {
				method.invoke(object, setValue);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
