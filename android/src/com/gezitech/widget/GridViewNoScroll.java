package com.gezitech.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.GridView;
/**
 * 
 * @author xiaobai
 * 2014-8-12
 * @todo( 自定义不滚动的 gridview )
 */
public class GridViewNoScroll extends GridView{
	public GridViewNoScroll(Context context) {
		super(context);
	}
	public GridViewNoScroll(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GridViewNoScroll(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
