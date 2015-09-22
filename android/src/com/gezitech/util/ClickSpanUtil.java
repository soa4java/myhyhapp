package com.gezitech.util;

import com.hyh.www.adapter.NearbyAdapter.OnTextViewClickListener;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
/**
 * 
 * @author xiaobai
 * 2015-6-1
 * @todo(设置可点击的事件  )
 */
public class ClickSpanUtil extends ClickableSpan{

	private OnTextViewClickListener mListener;
	
	public ClickSpanUtil ( OnTextViewClickListener listener ){
		this.mListener = listener;
	}
	
	@Override
	public void onClick(View widget) {
		// TODO Auto-generated method stub
		this.mListener.clickTextView();
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		mListener.setStyle(ds);
	}
	
}
