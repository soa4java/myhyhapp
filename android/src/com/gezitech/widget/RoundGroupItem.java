package com.gezitech.widget;


import com.hyh.www.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RoundGroupItem extends RelativeLayout implements View.OnTouchListener {
	private RelativeLayout root=null,round_group_item_right= null;
	private TextView txt_name;
	private LinearLayout container=null,round_group_item_left=null;
	private ImageView arrow, titlePic;
	private ItemType itemType;
	private boolean initing;
	private float x = -1f, y = -1f;
	private boolean clickable;
	private OnClickListener onclickListener;
	private boolean selected;
	private RoundGroupItem _this;
	private TextView round_group_item_hint;
	private LinearLayout round_group_item_left_title;
	public RoundGroupItem(Context context) {
		super(context);
		init(context);
	}
	public RoundGroupItem(Context context, AttributeSet attrs){
		super(context, attrs);
		init(context);
	}
	public RoundGroupItem(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		init(context);
	}
	private synchronized void init(Context context){
		_this = this;
		initing = true;
		clickable = true;
		onclickListener = null;
		selected = false;
		LayoutInflater.from(context).inflate(R.layout.round_group_item, this);
		if(isInEditMode())return;
		root = (RelativeLayout)findViewById(R.id.round_group_item_root);
		txt_name = (TextView)findViewById(R.id.round_group_item_name);
		container = (LinearLayout)findViewById(R.id.round_group_item_container);
		arrow = (ImageView)findViewById(R.id.round_group_item_arrow);
		titlePic = (ImageView)findViewById(R.id.round_group_item_titlePic);
		round_group_item_right = (RelativeLayout) findViewById(R.id.round_group_item_right);
		round_group_item_left = (LinearLayout) findViewById(R.id.round_group_item_left);
		round_group_item_left_title = (LinearLayout) findViewById(R.id.round_group_item_left_title);
		//注释提示文字
		round_group_item_hint = (TextView) findViewById(R.id.round_group_item_hint);
		this.setClickable(true);
		root.setClickable(clickable);
		root.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_this.onClick();
			}
		});
//		this.setOnTouchListener(this);
		initing = false;
	}
	public RoundGroupItem setArrowVisibility(int visibility){
		arrow.setVisibility(visibility);
		return this;
	}
	public RoundGroupItem setArrowImageResource(int imageResource){
		arrow.setImageResource(imageResource);
		return this;
	}
	public ImageView getArrow(){
		return arrow;
	}
	@Override
	public void addView(View child) {
		if(isInEditMode() || initing)super.addView(child);
		else container.addView(child);
	}
	@Override
	public void addView(View child, int index) {
		if(isInEditMode() || initing)super.addView(child, index);
		else container.addView(child, index);
	}
	@Override
	public void addView(View child, int index,
			android.view.ViewGroup.LayoutParams params) {
		if(isInEditMode() || initing)super.addView(child, index, params);
		else container.addView(child, index, params);
	}
	@Override
	public void addView(View child, int width, int height) {
		if(isInEditMode() || initing)super.addView(child, width, height);
		else container.addView(child, width, height);
	}
	@Override
	public void addView(View child, android.view.ViewGroup.LayoutParams params) {
		if(isInEditMode() || initing)super.addView(child, params);
		else container.addView(child, params);
	}
	@Override
	public void setClickable(boolean clickable) {
		this.clickable = clickable;
		super.setClickable(clickable);
//		this.clickable = clickable;
		root.setClickable(clickable);
	}
	public View findChildAt(int index) {
		return container.getChildAt(index);
	}
	public RoundGroupItem setName(String name){
		txt_name.setText(name);
		return this;
	}
	public RoundGroupItem setNameSize( int size ){
		txt_name.setTextSize( size );
		return this;
	}
	public RoundGroupItem setNameColor( int color ){
		txt_name.setTextColor( color );
		return this;
	}
	public String getName(){
		return txt_name.getText().toString();
	}
	public RoundGroupItem setTitlePicVisibility(int visibility){
		titlePic.setVisibility(visibility);
		return this;
	}
	public RoundGroupItem setTitlePic(Bitmap bitmap){
		titlePic.setImageBitmap(bitmap);
		return this;
	}
	public RoundGroupItem setTitlePic(Drawable drawable){
		titlePic.setImageDrawable(drawable);
		return this;
	}
	public RoundGroupItem setTitlePic(int resource){
		titlePic.setImageResource(resource);
		return this;
	}
	//设置注释提示
	public RoundGroupItem setItemHint( int visibility ){		
		round_group_item_hint.setVisibility(visibility);
		return this;
	}
	//设置文字
	public RoundGroupItem setItemHint( String text ){	
		round_group_item_hint.setText( text );
		return this;
	}
	//左边的文字提示宽度
	public RoundGroupItem setItemLeftTitleWidth( int width ){	
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,
                LinearLayout.LayoutParams.WRAP_CONTENT);
		round_group_item_left_title.setLayoutParams( params );
		return this;
	}
	public enum ItemType{Start, Middle, End, StartEnd}
	public RoundGroupItem setItemType(ItemType itemType, boolean selected){
		if(selected && !this.clickable)selected = false;//不能选中
		this.itemType = itemType;
		switch (itemType) {
		case Start:
			root.setBackgroundResource(R.drawable.round_start);
			break;
		case Middle:
			root.setBackgroundResource(R.drawable.round_middle);
			break;
		case End:
			root.setBackgroundResource(R.drawable.round_end);
			break;
		default:
			root.setBackgroundResource(R.drawable.round_start_end);
			break;
		}
		return this;
	}
	public RoundGroupItem setItemType(ItemType itemType){
		return setItemType(itemType, false);
	}
	public ItemType getItemType(){
		return this.itemType;
	}
	public RoundGroupItem setNameVisibility(int visibility){
		this.txt_name.setVisibility(visibility);
		return this;
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		switch(action){
		case MotionEvent.ACTION_DOWN:
			setItemType(itemType, true);
			x = event.getX();
			y = event.getY();
			selected = true;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_OUTSIDE:
			if(!selected)break;
			onClick();
			setItemType(itemType);
			selected = false;
			break;
		case MotionEvent.ACTION_MOVE:
			if(!selected)break;
			float mx = event.getX();
			float my = event.getY();
			if(Math.abs(mx - x) > 10 || Math.abs(my - y) > 10){
				onClick();
				setItemType(itemType);
				selected = false;
			}
			break;
		}
		return false;
	}
	protected void onClick(){
		if(onclickListener != null)
			onclickListener.onClick(this);
	}
	@Override
	public void setOnClickListener(OnClickListener l) {
		onclickListener = l;
	}
	/***
	 * 只显示round_group_item_name布局
	 */
	public RoundGroupItem setShowRoundGroupItemName(){
		round_group_item_right.setVisibility(View.GONE);
		android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		round_group_item_left.setLayoutParams(params);
		round_group_item_left.setGravity(Gravity.CENTER);
		return this;
	}
}
