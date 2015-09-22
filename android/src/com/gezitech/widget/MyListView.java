package com.gezitech.widget;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hyh.www.R;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyListView extends ListView implements OnScrollListener{

	private static final String TAG = "listview";

	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;

	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;

	private LayoutInflater inflater;

	private LinearLayout headView,load_more_ing;

	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;


	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isRecored;
	private int headContentHeight;

	private int startY;
	private int firstItemIndex;

	private int state;

	private boolean isBack;

	private OnRefreshListener refreshListener;

	private boolean isRefreshable;

	private LinearLayout footerView;
	private OnMoreListener moreListener;

	private TextView load_more_text;

	private ProgressBar footer_progressBar;

	private LinearLayout ll_footer_box;
	public MyListView(Context context) {
		super(context);
		init(context);
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	private void init(Context context) {
		Log.v("csi","测试测试测试");
		inflater = LayoutInflater.from(context);
		this.setFooterDividersEnabled(false);
		this.setHeaderDividersEnabled(false );
		headView = (LinearLayout) inflater.inflate(R.layout.head, null);
		arrowImageView = (ImageView) headView.findViewById(R.id.head_arrowImageView);
		arrowImageView.setMinimumWidth(70);
		progressBar = (ProgressBar) headView.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) headView.findViewById(R.id.head_lastUpdatedTextView);
		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();
		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();//设置后刷新界面
		addHeaderView(headView, null, false);
		
		//页脚加载框 
		
		footerView = (LinearLayout) inflater.inflate(R.layout.footer_more, null);
		ll_footer_box = ( LinearLayout ) footerView.findViewById( R.id.ll_footer_box );
		load_more_text = (TextView) footerView.findViewById(R.id.tv_load_more);
		footer_progressBar = (ProgressBar) footerView.findViewById(R.id.pb_load_progress);
		
		measureView(footerView);
		addFooterView(footerView,null,false);
		//设置滚动的监听事件
		setOnScrollListener(this);
		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(250);
		reverseAnimation.setFillAfter(true);
		
		state = DONE;
		isRefreshable = false;
		//手动加载
		ll_footer_box.setOnClickListener( new OnClickListener(){
			@Override
			public void onClick(View v) {
				OnMore();
			}		
		});
	}
	/***
	 * arg0 对象
	 * firstVisiableItem  第一个显示的索引
	 * displayItemCount  屏幕能见几个item
	 * count listview 共多少个item
	 */
	public void onScroll(AbsListView arg0, int firstVisiableItem, int displayItemCount,
			int count) {
		firstItemIndex = firstVisiableItem;
		OnMore(firstVisiableItem,displayItemCount,count,getLastVisiblePosition());
	}
	public void onScrollStateChanged(AbsListView arg0, int arg1) {		
	}
	public boolean onTouchEvent(MotionEvent event) {

		if (isRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN://按下
				if (firstItemIndex == 0 && !isRecored) {
					isRecored = true;
					startY = (int) event.getY();
				}
				break;

			case MotionEvent.ACTION_UP://取消

				if (state != REFRESHING && state != LOADING) {
					if (state == DONE) {
						// 什么都不做
					}
					if (state == PULL_To_REFRESH) {
						state = DONE;
						changeHeaderViewByState();
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						onRefresh();
					}
				}
				isRecored = false;
				isBack = false;

				break;

			case MotionEvent.ACTION_MOVE://移动
				int tempY = (int) event.getY();

				if (!isRecored && firstItemIndex == 0) {
					Log.v(TAG, "在move时候记录下位置");
					isRecored = true;
					startY = tempY;
				}
				if (state != REFRESHING && isRecored && state != LOADING) {
					if (state == RELEASE_To_REFRESH) {
						setSelection(0);
						if (((tempY - startY) / RATIO < headContentHeight)
								&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();
						}
						else {
						}
					}
					if (state == PULL_To_REFRESH) {
						setSelection(0);
						if ((tempY - startY) / RATIO >= headContentHeight) {
							state = RELEASE_To_REFRESH;
							isBack = true;
							changeHeaderViewByState();
						}
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();
						}
					}
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
					}
					if (state == PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight
								+ (tempY - startY) / RATIO, 0, 0);
					}
					if (state == RELEASE_To_REFRESH) {
						headView.setPadding(0, (tempY - startY) / RATIO
								- headContentHeight, 0, 0);
					}

				}

				break;
			}
		}

		return super.onTouchEvent(event);
	}
	private void changeHeaderViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
			arrowImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);
			tipsTextview.setText( this.getResources().getString( R.string.pulldown ) );
			break;
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);
				tipsTextview.setText( this.getResources().getString( R.string.refresh ) );
			} else {
				tipsTextview.setText( this.getResources().getString( R.string.refresh ) );
			}
			break;

		case REFRESHING:
			headView.setPadding(0, 0, 0, 0);
			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText(this.getResources().getString(R.string.loading));
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);

			progressBar.setVisibility(View.GONE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageResource(R.drawable.icon_refresh_down);
			tipsTextview.setText( this.getResources().getString( R.string.refresh ) );
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			break;
		}
	}

	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public void onRefreshComplete() {
		state = DONE;
		SimpleDateFormat format=new SimpleDateFormat("MM月dd日  HH:mm");
		String date=format.format(new Date());
		lastUpdatedTextView.setText(""+date);
		changeHeaderViewByState();
	}
	public void onClickRefreshComplete(){
		state = REFRESHING;
		changeHeaderViewByState();
		invalidate();
	}
	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0,
				0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}
	/**加載更多監聽*/
	public interface OnMoreListener {
		public void OnMore(int firstVisiableItem, int displayItemCount, int count,int lastVisibleItem);
		public void OnMore();
	}
	//自动加载
	public void OnMore(int firstVisiableItem,int displayItemCount,int count,int lastVisibleItem){
		if(moreListener != null){
			moreListener.OnMore(firstVisiableItem,displayItemCount,count,lastVisibleItem);
		}
	}
	//手动加载
	public void OnMore(){
		if(moreListener != null){
			moreListener.OnMore();
		}
	}
	public void setOnMoreListener(OnMoreListener moreListener){
		this.moreListener = moreListener;
	}
	/***
	 * 页脚的加载显示的状态
	 */
	public void footerShowState(int stateVale){
		//正在加载
		if(stateVale == 0){//加载中 和 默认显示的 时候
			
			ll_footer_box.setVisibility(View.VISIBLE);
			footer_progressBar.setVisibility( View.VISIBLE );
			load_more_text.setVisibility(View.GONE);
			
		}else if(stateVale == -1){ //没有更多数据
			
			ll_footer_box.setVisibility(View.VISIBLE);
			footer_progressBar.setVisibility( View.GONE );
			load_more_text.setText(  this.getResources().getString( R.string.no_data ) );
			load_more_text.setVisibility(View.VISIBLE);
			
		}else if(stateVale == 1){ //点击加载数据
			
			ll_footer_box.setVisibility(View.VISIBLE);
			footer_progressBar.setVisibility( View.GONE );
			load_more_text.setText(  this.getResources().getString( R.string.load ) );
			load_more_text.setVisibility(View.VISIBLE);
			
		}
		else if(stateVale == 2){//页脚隐藏
			ll_footer_box.setVisibility(View.GONE);
		}
	}
	@Override
	public void setAdapter(ListAdapter adapter) {
		SimpleDateFormat format=new SimpleDateFormat("MM月dd日  HH:mm");
		String date=format.format(new Date());
		lastUpdatedTextView.setText(""+date);	
		super.setAdapter(adapter);
	}
}

