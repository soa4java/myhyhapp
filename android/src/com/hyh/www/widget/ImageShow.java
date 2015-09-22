package com.hyh.www.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.image.ImageDetailActivity;
import com.hyh.www.R;

public class ImageShow {
	//跳转到查看大图的界面
	public static  void jumpDisplayPic(String[] images, int index,Activity activity  ){
		Intent intent = new Intent(activity,ImageDetailActivity.class );
		Bundle bundle = new Bundle();
		bundle.putStringArray("images", images);
		bundle.putInt("index", index);
		intent.putExtras( bundle );
		activity.startActivity( intent );
		activity.overridePendingTransition(R.anim.out_to_down,R.anim.exit_anim );
	}
}
