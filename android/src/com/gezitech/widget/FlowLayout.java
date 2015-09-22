package com.gezitech.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayout extends ViewGroup {
	private int line_height;
	
    public static   int DEFAULT_HORIZONTAL_SPACING = 8;
    public static   int DEFAULT_VERTICAL_SPACING = 8;
    
	public FlowLayout(Context context) {
		super(context);
	}
	public FlowLayout(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	public FlowLayout(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}
	public void setSpacing(int hSpacing, int vSpacing){
		DEFAULT_HORIZONTAL_SPACING = hSpacing;
		DEFAULT_VERTICAL_SPACING = vSpacing;
	}
	@Override
    protected void onLayout(boolean arg0, int l, int t, int r, int b) {
		final int count = getChildCount();
        final int width = r - l;
//        int xpos = getPaddingLeft();
//        int ypos = getPaddingTop();
        int xpos = DEFAULT_HORIZONTAL_SPACING;
        int ypos = DEFAULT_VERTICAL_SPACING;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int childw = child.getMeasuredWidth();
                final int childh = child.getMeasuredHeight();
                if (xpos + childw > width) {
//                    xpos = getPaddingLeft();
                	xpos = DEFAULT_HORIZONTAL_SPACING;
                    ypos += line_height;
                }
                child.layout(xpos, ypos, xpos + childw, ypos + childh);
                xpos += childw + DEFAULT_HORIZONTAL_SPACING;
            }
        }
    }
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		assert (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED);

        final int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        final int count = getChildCount();
        int line_height = 0;
        // int line_height = 22;

        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.measure(MeasureSpec.makeMeasureSpec(LayoutParams.WRAP_CONTENT, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(LayoutParams.WRAP_CONTENT, MeasureSpec.UNSPECIFIED));

                final int childw = child.getMeasuredWidth();
                line_height = Math.max(line_height, child.getMeasuredHeight() + DEFAULT_VERTICAL_SPACING);

                if (xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += line_height;
                }

                xpos += childw + DEFAULT_HORIZONTAL_SPACING;
            }
        }
        this.line_height = line_height;

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            height = ypos + line_height;

        } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            if (ypos + line_height < height) {
                height = ypos + line_height;
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(1, 1); // default of 1px spacing
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }
}
