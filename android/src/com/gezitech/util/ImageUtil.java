package com.gezitech.util;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
public class ImageUtil {
	
    public static int reckonThumbnail(int oldWidth, int oldHeight, int newWidth, int newHeight) {
        if ((oldHeight > newHeight && oldWidth > newWidth)
                || (oldHeight <= newHeight && oldWidth > newWidth)) {
            int be = (int) (oldWidth / (float) newWidth);
            if (be <= 1)
                be = 1;
            return be;
        } else if (oldHeight > newHeight && oldWidth <= newWidth) {
            int be = (int) (oldHeight / (float) newHeight);
            if (be <= 1)
                be = 1;
            return be;
        }

        return 1;
    }

   
    public static boolean bitmapToFile(String photoPath, File aFile, int newWidth, int newHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
        options.inJustDecodeBounds = false;
 
        
        options.inSampleSize = reckonThumbnail(options.outWidth, options.outHeight, newWidth, newHeight);

        bitmap = BitmapFactory.decodeFile(photoPath, options);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] photoBytes = baos.toByteArray();

            if (aFile.exists()) {
                aFile.delete();
            }
            aFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(aFile);
            fos.write(photoBytes);
            fos.flush();
            fos.close();

            return true;
        } catch (Exception e1) {
            e1.printStackTrace();
            if (aFile.exists()) {
                aFile.delete();
            }
            Log.e("Bitmap To File Fail", e1.toString());
            return false;
        }
    }

    public static Bitmap PicZoom(Bitmap bmp, int width, int height) {
        int bmpWidth = bmp.getWidth();
        int bmpHeght = bmp.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale((float) width / bmpWidth, (float) height / bmpHeght);
        return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeght, matrix, true);
    }
    /***
     * 
     * TODO( 压缩图片，减少内存的溢出 )
     */
    public static Bitmap PicZoom2( byte[] bytes , int IMAGE_MAX_HEIGHT, int IMAGE_MAX_WIDTH ) {
    	 //对于图片的二次采样,主要得到图片的宽与高  
        int width = 0;  
        int height = 0;  
        int sampleSize = 1; //默认缩放为1  
        BitmapFactory.Options options = new BitmapFactory.Options();  
        options.inJustDecodeBounds = true;  //仅仅解码边缘区域  
        //如果指定了inJustDecodeBounds，decodeByteArray将返回为空  
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);  
        //得到宽与高  
        height = options.outHeight;  
        width = options.outWidth;  
      
        //图片实际的宽与高，根据默认最大大小值，得到图片实际的缩放比例  
        while ( ( height / sampleSize > IMAGE_MAX_HEIGHT )  
                || (width / sampleSize > IMAGE_MAX_WIDTH ) ) {  
            sampleSize *= 2;  
        }  
      
        //不再只加载图片实际边缘  
        options.inJustDecodeBounds = false;  
        //并且制定缩放比例  
        options.inSampleSize = sampleSize;  
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options); 
    }
   public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {    
     
       Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);    
       Canvas canvas = new Canvas(output);    
     
       final int color = 0xff424242;    
       final Paint paint = new Paint();    
       final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());    
       final RectF rectF = new RectF(rect);    
       final float roundPx = pixels;    
     
       paint.setAntiAlias(true);    
       canvas.drawARGB(0, 0, 0, 0);    
       paint.setColor(color);    
       canvas.drawRoundRect(rectF, roundPx, roundPx, paint);    
     
       paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));    
       canvas.drawBitmap(bitmap, rect, rect, paint);    
     
       return output;    
   }  
   
  
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);
		return bitmapWithReflection;
	}
	

	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	public static Bitmap rotateBitmap(String filePath) {
		Bitmap bitmap = null;
		ExifInterface exifInterface;
		try {
			exifInterface = new ExifInterface(filePath);
			int result = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);
			int rotate = 0;
			switch (result) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			default:
				break;
			}
			bitmap = getSmallBitmap(filePath);
			if (rotate > 0) {
				Matrix matrix = new Matrix();
				matrix.setRotate(rotate);
				BitmapFactory.Options options = new BitmapFactory.Options(); 
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;  
				options.inJustDecodeBounds = true;
				options.inSampleSize = 1;
				Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				if (rotateBitmap != null) {
					bitmap.recycle();
					bitmap = rotateBitmap;
				}
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}   
		return bitmap;
	}

	/**
	 * 根据路径获得图片并压缩，返回bitmap用于显示
	 * @param filePath 文件路径
	 * @return Bitmap
	 */
	public static Bitmap getSmallBitmap(String filePath) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 480, 800);
		//options.inSampleSize = 1;
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        options.inSampleSize = 1;
//        return BitmapFactory.decodeFile(filePath, options);
	}
	
	public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth) {
	     int width = bitmap.getWidth();
	     int height = bitmap.getHeight();
	     float temp = ((float) height) / ((float) width);
	     int newHeight = (int) ((newWidth) * temp);
	     float scaleWidth = ((float) newWidth) / width;
	     float scaleHeight = ((float) newHeight) / height;
	     Matrix matrix = new Matrix();
	     matrix.postScale(scaleWidth, scaleHeight);
	     Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	     bitmap.recycle();
	     return resizedBitmap;
	}
	
	/**
	 * 计算图片的缩放值
	 * @param options
	 * @param reqWidth 请求宽度
	 * @param reqHeight 请求高度
	 * @return 缩放值
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
	//获取地址
	public String getPath(Uri uri,Activity context){ 

        String[] projection = {MediaStore.Images.Media.DATA }; 

       Cursor cursor = context.managedQuery(uri, projection, null, null, null); 

        int column_index = cursor 

             .getColumnIndexOrThrow(MediaStore.Images.Media.DATA); 

        cursor.moveToFirst(); 

         return cursor.getString(column_index); 

 } 
}
