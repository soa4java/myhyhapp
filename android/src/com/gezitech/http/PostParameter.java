package com.gezitech.http;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

/**
 * HTTP请求参数类
 * 
 * @author heyao
 * 
 */
public class PostParameter implements java.io.Serializable, Comparable {
	String name;
	String value;
	private File file = null;
	private byte[] fileByte = null;
	private Bitmap bitMap = null;

	private static final long serialVersionUID = -8708108746980739212L;

	public PostParameter(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public PostParameter(String name, double value) {
		this.name = name;
		this.value = String.valueOf(value);
	}

	public PostParameter(String name, int value) {
		this.name = name;
		this.value = String.valueOf(value);
	}

	public PostParameter(String name, long value) {
		this.name = name;
		this.value = String.valueOf(value);
	}

	public PostParameter(String name, Long value) {
		this.name = name;
		this.value = String.valueOf(value);
	}

	public PostParameter(String name, File file) {
		this.name = name;
		this.file = file;
	}

	public PostParameter(String name, byte[] fileByte) {
		this.name = name;
		this.fileByte = fileByte;
	}

	public PostParameter(String name, Bitmap bitMap) {
		this.name = name;
		this.bitMap = bitMap;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public File getFile() {
		return file;
	}

	public boolean isFile() {
		return null != file;
	}

	public boolean isFileByte() {
		return null != fileByte;
	}

	public boolean isBitmap() {
		return null != bitMap;
	}

	private static final String JPEG = "image/jpeg";
	private static final String GIF = "image/gif";
	private static final String PNG = "image/png";
	private static final String OCTET = "application/octet-stream";

	/**
	 * 
	 * @return content-type
	 */
	public String getContentType() {
		if (!isFile()) {
			throw new IllegalStateException("not a file");
		}
		String contentType;
		String extensions = file.getName();
		int index = extensions.lastIndexOf(".");
		if (-1 == index) {
			// no extension
			contentType = OCTET;
		} else {
			extensions = extensions.substring(extensions.lastIndexOf(".") + 1)
					.toLowerCase();
			if (extensions.length() == 3) {
				if ("gif".equals(extensions)) {
					contentType = GIF;
				} else if ("png".equals(extensions)) {
					contentType = PNG;
				} else if ("jpg".equals(extensions)) {
					contentType = JPEG;
				} else {
					contentType = OCTET;
				}
			} else if (extensions.length() == 4) {
				if ("jpeg".equals(extensions)) {
					contentType = JPEG;
				} else {
					contentType = OCTET;
				}
			} else {
				contentType = OCTET;
			}
		}
		return contentType;
	}

	public static boolean containsFile(PostParameter[] params) {
		boolean containsFile = false;
		if (null == params) {
			return false;
		}
		for (PostParameter param : params) {
			if (param.isFile()) {
				containsFile = true;
				break;
			}
		}
		return containsFile;
	}

	/* package */static boolean containsFile(List<PostParameter> params) {
		boolean containsFile = false;
		for (PostParameter param : params) {
			if (param.isFile()) {
				containsFile = true;
				break;
			}
		}
		return containsFile;
	}

	public static PostParameter[] getParameterArray(String name, String value) {
		return new PostParameter[] { new PostParameter(name, value) };
	}

	public static PostParameter[] getParameterArray(String name, int value) {
		return getParameterArray(name, String.valueOf(value));
	}

	public static PostParameter[] getParameterArray(String name1,
			String value1, String name2, String value2) {
		return new PostParameter[] { new PostParameter(name1, value1),
				new PostParameter(name2, value2) };
	}

	public static PostParameter[] getParameterArray(String name1, int value1,
			String name2, int value2) {
		return getParameterArray(name1, String.valueOf(value1), name2,
				String.valueOf(value2));
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + value.hashCode();
		result = 31 * result + (file != null ? file.hashCode() : 0);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj instanceof PostParameter) {
			PostParameter that = (PostParameter) obj;

			if (file != null ? !file.equals(that.file) : that.file != null)
				return false;

			return this.name.equals(that.name) && this.value.equals(that.value);
		}
		return false;
	}

	@Override
	public String toString() {
		return "PostParameter{" + "name='" + name + '\'' + ", value='" + value
				+ '\'' + ", file=" + file + '}';
	}

	public int compareTo(Object o) {
		int compared;
		PostParameter that = (PostParameter) o;
		compared = name.compareTo(that.name);
		if (0 == compared) {
			compared = value.compareTo(that.value);
		}
		return compared;
	}

	public static String encodeParameters(PostParameter[] httpParams) {
		if (null == httpParams) {
			return "";
		}
		StringBuffer buf = new StringBuffer();
		for (int j = 0; j < httpParams.length; j++) {
			if (httpParams[j].isFile()) {
				throw new IllegalArgumentException("parameter ["
						+ httpParams[j].name + "]should be text");
			}
			if (j != 0) {
				buf.append("&");
			}
			try {
				buf.append(URLEncoder.encode(httpParams[j].name, "UTF-8"))
						.append("=")
						.append(URLEncoder.encode(httpParams[j].value, "UTF-8"));
			} catch (java.io.UnsupportedEncodingException neverHappen) {
			}
		}
		return buf.toString();

	}

	/**
	 * 参数列表，可以链式操作。不能添加同名参数，后添加的将失败，除非先将原有参数删除。
	 * 
	 * @author Administrator
	 * 
	 */
	public static class PostParameterList {
		private ArrayList<PostParameter> list;

		public PostParameterList() {
			list = new ArrayList<PostParameter>();
		}

		/**
		 * 往集合对象中添加一个参数，如果已有同名参数存在，则添加将失败。应该先移除
		 * 
		 * @param p
		 * @return
		 */
		public PostParameterList add(PostParameter p) {
			if (p == null)
				return this;
			PostParameter pp = getParameter(p.name);
			if (pp != null)
				return this;
			list.add(p);
			return this;
		}

		public PostParameter getParameter(String name) {
			for (PostParameter p : list)
				if (p.name.equals(name))
					return p;
			return null;
		}

		public ArrayList<PostParameter> getList() {
			return this.list;
		}

		public PostParameterList removeParameter(String name) {
			PostParameter p = getParameter(name);
			if (p == null)
				return this;
			list.remove(p);
			return this;
		}

		public PostParameterList addOrder(String value) {
			return add("order", value);
		}

		public PostParameterList addVersion(String value) {
			return add("v", value);
		}

		public PostParameterList addRel(String value) {
			return add("rel", value);
		}

		public PostParameterList add(String name, int value) {
			PostParameter p = new PostParameter(name, value);
			return add(p);
		}

		public PostParameterList add(String name, long value) {
			PostParameter p = new PostParameter(name, value);
			return add(p);
		}

		public PostParameterList add(String name, Bitmap value) {
			PostParameter p = new PostParameter(name, value);
			return add(p);
		}

		public PostParameterList add(String name, byte[] value) {
			PostParameter p = new PostParameter(name, value);
			return add(p);
		}

		public PostParameterList add(String name, double value) {
			PostParameter p = new PostParameter(name, value);
			return add(p);
		}

		public PostParameterList add(String name, File value) {
			PostParameter p = new PostParameter(name, value);
			return add(p);
		}

		public PostParameterList add(String name, String value) {
			PostParameter p = new PostParameter(name, value);
			return add(p);
		}

		public PostParameter[] toArray() {
			PostParameter[] res = new PostParameter[list.size()];
			list.toArray(res);
			return res;
		}

		public PostParameterList addAll(PostParameterList list) {
			if (list == null)
				return this;
			ArrayList<PostParameter> ps = list.getList();
			if (ps == null)
				return this;
			for (PostParameter p : ps)
				add(p);
			return this;
		}
	}
}
