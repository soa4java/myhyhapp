package com.gezitech.widget;
import com.hyh.www.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ViewFlipper;
/**
 * 跑马灯ViewFlipper的重定义
 * @author xiaobai
 */
public class ExtendViewFlipper extends ViewFlipper {
	private Context context = null ;
	private ExtendViewFlipper _this = this;
	private Animation leftInAnimation,leftOutAnimation,rightInAnimation,rightOutAnimation;
	private float mLastMotionX;
	private int deltalX;
	private OnViewFlipperFackFunction onViewFlipperFackFunction = null;
	public ExtendViewFlipper(Context context) {
		super(context);
		this.context = context;
        init(context);
	}
	public ExtendViewFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
        init(context);
	}
	private void init(Context context) {
		leftInAnimation = AnimationUtils.loadAnimation(_this.context, R.anim.left_in);
		leftOutAnimation = AnimationUtils.loadAnimation(_this.context, R.anim.left_out);
		rightInAnimation = AnimationUtils.loadAnimation(_this.context, R.anim.right_in);
		rightOutAnimation = AnimationUtils.loadAnimation(_this.context, R.anim.right_out);	
	}
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float x = event.getX();
		switch( action ){
			//按下
			case MotionEvent.ACTION_DOWN:
				_this.onViewFlipperFackFunction.countDownTimerAction( false );
				mLastMotionX = x;
			//移动
			case MotionEvent.ACTION_MOVE:
				deltalX  = (int)(mLastMotionX - x);//开始位置的触点与当前位置的差值
			break;
			//松开
			case MotionEvent.ACTION_UP: 		
				_this.onViewFlipperFackFunction.countDownTimerAction( true );
				
				if( deltalX>120){
					_this.onViewFlipperFackFunction.selectStyle( true );
					_this.setInAnimation(leftInAnimation);
					_this.setOutAnimation(leftOutAnimation);
				    _this.showNext();//向右滑动
				}else if( deltalX<-120){
					_this.onViewFlipperFackFunction.selectStyle( false );
					_this.setInAnimation(rightInAnimation);
					_this.setOutAnimation(rightOutAnimation);
					_this.showPrevious();//向左滑动
				}
			break;
		} 
		return true;
    }
	public interface OnViewFlipperFackFunction{
		void selectStyle( boolean isPre  );
		void countDownTimerAction( boolean isAction );
	}
	public void setOnViewFlipperFackFunction( OnViewFlipperFackFunction onViewFlipperFackFunction){
		if( onViewFlipperFackFunction != null ){
			_this.onViewFlipperFackFunction  = onViewFlipperFackFunction;
		}
	}
}
