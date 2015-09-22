package com.gezitech.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gezitech.basic.GezitechApplication;
import com.gezitech.http.HttpClient;
import com.gezitech.http.Response;
import com.hyh.www.R;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

public class IOUtil {
	
	public static String IMAGEPATH, FILEPATH, UPDATEPATH, DBFILEPATH, EMOTIONPATH;
	public static String appInnerCacheDir;
	public static String fileCachePath="";
	public static String fileCacheDirectory="";
	public static String fileCacheFileName="";
	public static void init(){
		appInnerCacheDir = GezitechApplication.getContext().getCacheDir().getAbsolutePath();
		String root = getCacheFilePath();
		IMAGEPATH = root + "/images";
		FILEPATH = root + "/files";
		UPDATEPATH = root + "/update";
		DBFILEPATH = root + "/dbfile";
		EMOTIONPATH = root + "/emotions";
	}
	public static File makeFile(FileInputStream fis,String fileName) throws IOException{	
		byte[] bytes = null;
		try {
			bytes = new byte[fis.available()];
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fis.read(bytes);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		 File SDFile = android.os.Environment  
	             .getExternalStorageDirectory();
		 File destDir = new File(IMAGEPATH);
		 if (!destDir.exists())  
	         destDir.mkdirs(); 
		fileName = null == fileName ? generateRandomFilename() : fileName;
		File myFile = new File(destDir + File.separator + fileName);
		
		 if (!myFile.exists()) {  
	         try {  
	        	 myFile.createNewFile();
	         } catch (IOException e) {  
	             e.printStackTrace();  
	         }  
	     }  
	
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(myFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		fos.write(bytes);
		fos.flush();
		fos.close();
		return myFile;
	}
	
	public static File makeFile(FileInputStream fis,String fileName,String path) throws IOException{	
		byte[] bytes = null;
		try {
			bytes = new byte[fis.available()];
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fis.read(bytes);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		 File SDFile = android.os.Environment  
	             .getExternalStorageDirectory();
		 File destDir = new File(path);
		 if (!destDir.exists())  
	         destDir.mkdirs(); 
		fileName = null == fileName ? generateRandomFilename() : fileName;
		File myFile = new File(destDir + File.separator + fileName);
		
		 if (!myFile.exists()) {  
	         try {  
	        	 myFile.createNewFile();
	         } catch (IOException e) {  
	             e.printStackTrace();  
	         }  
	     }  
	
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(myFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		fos.write(bytes);
		fos.flush();
		fos.close();
		return myFile;
	}
	
	
	 /**
	  * 根据日期，随机数生成文件名
	  * @return
	  */
	 public static String generateRandomFilename(){  
	        String RandomFilename = "";  
	        Random rand = new Random();//生成随机数  
	        int random = rand.nextInt();  
	          
	        Calendar calCurrent = Calendar.getInstance();  
	        int intDay = calCurrent.get(Calendar.DATE);  
	        int intMonth = calCurrent.get(Calendar.MONTH) + 1;  
	        int intYear = calCurrent.get(Calendar.YEAR);  
	        String now = String.valueOf(intYear) + "_" + String.valueOf(intMonth) + "_" +  
	            String.valueOf(intDay) + "_";  
	        RandomFilename = now + String.valueOf(random > 0 ? random : ( -1) * random) + "";  
	        return RandomFilename;  
	    } 
	
	/*****************************************************************
	 * 从inputStream获取字节流
	 * @param is
	 * @param bufsiz
	 * @return
	 * @throws IOException
	 */
	public static byte[] getBytesFromInputStream(InputStream is, int bufsiz) throws IOException {
		int total = 0;
		byte[] bytes = new byte[4096];
		ByteBuffer bb = ByteBuffer.allocate(bufsiz);
		while (true) {
			int read = is.read(bytes);
			if (read == -1)
			break;
			bb.put(bytes, 0, read);
			total += read;
		}
		byte[] content = new byte[total];
		bb.flip();
		bb.get(content, 0, total);
		return content;
	}
	
	/**
	 * 读取文件到字节流
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte [] getFileBytes(File file) throws IOException{	
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream in = new BufferedInputStream(fis);   
	    ByteArrayOutputStream out = new ByteArrayOutputStream(1024);   
	    System.out.println("Available bytes:" + in.available());   
	    byte[] temp = new byte[1024];   
	    int size = 0;   
	    while ((size = in.read(temp)) != -1) {   
	          out.write(temp, 0, size);   
	    }   
	    in.close();     
	    byte[] content = out.toByteArray();
	    return content;
	}
	
	public static Bitmap getLocalImage(String filePath){
		try{
			//return BitmapFactory.decodeFile(filePath);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, opts);
            opts.inSampleSize = computeSampleSize(opts, -1, 768*768);
            opts.inJustDecodeBounds = false;

            Bitmap bmp = BitmapFactory.decodeFile(filePath, opts);
            
            return bmp;
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
//        BitmapFactory.Options bfOptions = new BitmapFactory.Options();
//        bfOptions.inDither = false;
//        bfOptions.inPurgeable = true;
//        bfOptions.inTempStorage = new byte[12 * 1024];
//        File file = new File(filePath);
//        FileInputStream fs = null;
//        try {
//            fs = new FileInputStream(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        Bitmap bmp = null;
//        if (fs != null) {
//            try {
//                bmp = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (OutOfMemoryError e){
//                e.printStackTrace();
//            }
//            finally {
//                if (fs != null) {
//                    try {
//                        fs.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//        return bmp;
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8 ) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }
    private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) &&
                (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
	 * 读取本地图片并对其进行压缩
	 * @param filePath
	 * @param maxHeight 最大高度。以最低要求满足宽和高（即很可能宽度或高度其中一个会超出预定值），并按比例缩放为准。结果是整数倍，所以不能绝对精确。
	 * @param maxWidth 最大宽度
	 * @return
	 * @throws IOException
	 */
	public static Bitmap getLocalImage(String filePath, int maxWidth, int maxHeight) throws IOException{
		try{
			BitmapFactory.Options options = new BitmapFactory.Options();  
			options.inJustDecodeBounds = true;  
			BitmapFactory.decodeFile(filePath, options);   
			if (options.mCancel || options.outWidth == -1 || options.outHeight == -1)
				return null;
			int hTimes = (int)Math.floor(options.outHeight * 1.0 /maxHeight);
			int wTimes = (int)Math.floor(options.outWidth * 1.0 / maxWidth);
			int times = Math.min(hTimes, wTimes);
			if(times <= 0)times = 1;
			options.inSampleSize = times;
			options.inJustDecodeBounds = false;  
			options.inDither = false;  
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;  
			return BitmapFactory.decodeFile(filePath, options);
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	public static File makeLocalImage(Bitmap bm, String fileName) throws IOException {  
        File dirFile = new File(IMAGEPATH);  
        if(!dirFile.exists()){  
            dirFile.mkdirs();  
        }  
        fileName = fileName == null ? (generateRandomFilename() + ".jpg") : fileName;
        File myCaptureFile = new File(IMAGEPATH + File.separator + fileName);  
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));  
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bos);  
        bos.flush();  
        bos.close();  
        
        return myCaptureFile;
    }
	public static byte[] getLocalImageBytes(String filePath) throws IOException{
		File file = new File(IMAGEPATH + File.separator + URLEncoder.encode(filePath,"UTF-8")); 
		return getFileBytes(file);
	}
	public static byte[] toByteArray(InputStream is){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			int len;
			byte[] buffer = new byte[2048];	
			while ((len = is.read(buffer, 0, buffer.length)) != -1) {
			  bos.write(buffer, 0, len);
			}
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bos.toByteArray();
	}
	/**
	 * 下载并缓存internet文件
	 * 下载的文件优先保存到sd卡，若sd卡不存在则保存在本机。本地位置与请求中的服务器路径中的位置相同，若有参数，则对参数进行一次hash计算
	 * @param url internet地址
	 * @param forceDownload 是否强行下载，若为否，如果本地有缓存则不再下载
	 * @param listener 下载完成事件
	 */
	public static void downloadAndCacheFile(final String url, boolean forceDownload, final CacheCompleteListener listener){
		downloadAndCacheFile(url, forceDownload, null, listener);
	}
	/**
	 * 下载并缓存internet文件
	 * 下载的文件优先保存到sd卡，若sd卡不存在则保存在本机。本地位置与请求中的服务器路径中的位置相同，若有参数，则对参数进行一次hash计算
	 * @param url internet地址
	 * @param forceDownload 是否强行下载，若为否，如果本地有缓存则不再下载
	 * @param listener 下载完成事件
	 * @param tag 用于存放数据的tag，以便在回调时候可以用于判断
	 */
	public static void downloadAndCacheFile(final String url, boolean forceDownload, final Object tag, final CacheCompleteListener listener){
		if(url == null || "".equals(url)){
			if(listener != null)listener.onCacheComplete(null, false, "地址格式不正确", tag);
			return;
		}
		Pattern p = Pattern.compile("https?://[^/]+", Pattern.CASE_INSENSITIVE);//取出主机块
		Matcher m = p.matcher(url);
		if(!m.find()){
			if(listener != null)listener.onCacheComplete(null, false, "地址格式不正确", tag);
			return;
		}
		String host = m.group();//主机部分
		String local = url.substring(host.length());//目录部分
		String fileName = null;//文件名部分
		if(local == null || "".equals(local) || local.equals("/")){
			local = "/";
			fileName = "index.htm";//只有域名
		}
		else{			
			p = Pattern.compile("[\\?#=].*$");//匹配地址后面的参数
			m = p.matcher(local);
			String search = null;//地址后面的参数的hash值
			if(m.find()){
				search = m.group();
				local = local.substring(0, local.length() - search.length());
				search = String.valueOf(search.hashCode());
			}
			p = Pattern.compile("[^/]*\\.[^/]*");//匹配文件名
			m = p.matcher(local);
			if(m.find()){
				fileName = m.group();
				local = local.substring(0, local.length() - fileName.length());
				int pos = fileName.indexOf(".");
				if(!TextUtils.isEmpty(search))
					fileName = fileName.substring(0, pos) + search + fileName.substring(pos);
				if(pos == 0)fileName = "index" + fileName;
			}
			else if(!TextUtils.isEmpty(search))
				fileName = search + ".htm";
			else fileName = "index.html";
		}
		String root = getCacheFilePath();
		String fullPath = root + local;
		fileCachePath = fullPath+fileName;
		fileCacheDirectory = fullPath;
		fileCacheFileName = fileName;
		if(fileExists(fullPath + fileName) && !forceDownload){
			if(listener != null)listener.onCacheComplete(fullPath + fileName, true, "local", tag);
		}
		else{
			final Handler handler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					boolean success = msg.arg1 == 1;
					Object[] obj = (Object[])msg.obj;
					String name = obj[0].toString();
					String mesg = obj[1].toString();
					Object tag = obj[2];
					if(listener != null)
						listener.onCacheComplete(name, success, mesg, tag);
				}
			};
			final String path = fullPath;
			final String fname = fileName;
			new Thread(new Runnable() {				
				@Override
				public void run() {
					HttpClient hc = new HttpClient();
					try {
						Response r = hc.get(url, false);
						if(r.getStatusCode() >= 400)
							throw new Exception("response code:" + r.getStatusCode());
						InputStream is = r.asStream();
						writeFile(is, path, fname, r.contentLength);
						r.disconnect();
						Message msg = new Message();
						msg.arg1 = 1;
						msg.obj = new Object[]{path + fname, "remote", tag};
						handler.sendMessage(msg);
					} catch (Exception e) {
						Message msg = new Message();
						msg.arg1 = 0;
						msg.obj = new Object[]{path + fname, e.toString(), tag};
						handler.sendMessage(msg);
						return;
					}
				}
			}).start();
		}
	}
	/**
	 * 获取缓存位置
	 * @return
	 */
	public static String getCacheFilePath(){
		String root;
		String dir = GezitechApplication.getContext().getString(R.string.app_name_directory);
		if(hasSDCard())root = Environment.getExternalStorageDirectory() + "/"+dir+"/fileCache";
		else root = appInnerCacheDir + "/fileCache";		
		return root;
	}
	/**
	 * 获取在有卡和没卡的时候缓存的位置
	 * @param hasSDCard
	 * @return
	 */
	public static String getCacheFilePath(boolean hasSDCard){
		String root;
		String dir = GezitechApplication.getContext().getString(R.string.app_name_directory);
		if(hasSDCard)root = Environment.getExternalStorageDirectory() + "/"+dir+"/fileCache";
		else root = appInnerCacheDir + "/fileCache";		
		return root;
	}
	/**
	 * 将来自网络的流写文件到文件，考虑了网络延迟情况
	 * @param is
	 * @param dir
	 * @param fileName
	 * @param length
	 * @throws IOException
	 */
	public synchronized static void writeFile(InputStream is, String dir, String fileName, int length) throws IOException{
		File destDir = new File(dir);
		if (!destDir.exists())  
	        destDir.mkdirs(); 
		fileName = null == fileName ? generateRandomFilename() : fileName;
		fileName = dir + fileName;
		
		File myFile = new File(fileName);
		if(myFile.exists())myFile.delete();
		myFile.createNewFile();
	
		FileOutputStream fos = new FileOutputStream(myFile);
//		int writedLength = 0;
//		int avi = 0;
//		int sleeped = 0;
		int readCount = 0;
		if(length <= 0)is = new BufferedInputStream(is);
		else is = new BufferedInputStream(is, length);
		byte[] by = new byte[1024*4];
		while((readCount = is.read(by)) != -1)
			fos.write(by, 0, readCount);
		/*
		while(length > writedLength){
			sleeped = 0;
			while((avi = is.available()) <= 0){
				try {
					Thread.sleep(300);
					readCount = is.read();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sleeped++;
				if(sleeped >= 100)
					break;
			}
			if(avi <= 0)break;
//			if(avi > 1024 * 4)avi = 1024 * 4;
//			byte[] buffer = new byte[avi];
			byte[] buffer = new byte[1024 * 4];
			readCount = is.read(buffer);
			fos.write(buffer);
			writedLength += avi;
		}*/
		is.close();
        fos.flush();
        fos.close();
	}
	public synchronized static void writeFile(InputStream is, String dir, String fileName) throws IOException{
		File destDir = new File(dir);
		if (!destDir.exists())  
	        destDir.mkdirs(); 
		fileName = null == fileName ? generateRandomFilename() : fileName;
		fileName = dir + fileName;
		
		File myFile = new File(fileName);
		if(myFile.exists())myFile.delete();
		myFile.createNewFile();
	
		FileOutputStream fos = new FileOutputStream(myFile);
		byte[] buffer = new byte[1024 * 4];
        while(true){
        	int len = is.read(buffer);
        	if(len < 0)break;
            fos.write(buffer);
        }
        fos.flush();
        fos.close();
	}
	/**
	 * 文件是否存在
	 * @param file
	 * @return
	 */
	public static boolean fileExists(String file){
        try{
            File f=new File(file);
            if(!f.exists()){
            	return false;
            }                
        }catch (Exception e) {
             return false;
        }
        return true;
	}
	/**
	 * 删除目录或文件
	 * @param path
	 * @return
	 */
	public static boolean deletePath(String path){
		if(!fileExists(path))return true;
		File f = new File(path);
		if(f.isFile())return f.delete();
		File[] fs = f.listFiles();
		for(File file : fs)deletePath(file.getAbsolutePath());
		return f.delete();
	}
	/**
	 * sd卡是否存在
	 * @return
	 */
	public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
	public interface CacheCompleteListener{
		public void onCacheComplete(String fileName, boolean success, String msg, Object tag);
	}
}
