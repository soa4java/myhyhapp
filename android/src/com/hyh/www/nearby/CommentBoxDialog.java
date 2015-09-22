package com.hyh.www.nearby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetJsonObjectListener;
import com.gezitech.contract.GezitechManager_I.OnAsynGetOneListener;
import com.gezitech.service.GezitechService;
import com.gezitech.service.managers.NearManager;
import com.gezitech.service.xmpp.MessageSend;
import com.gezitech.util.StringUtil;
import com.gezitech.util.ToastMakeText;
import com.hyh.www.R;
import com.hyh.www.adapter.FaceAdapter;
import com.hyh.www.adapter.ViewPagerAdapter;
import com.hyh.www.entity.Emotion;
import com.hyh.www.entity.NearMsg;
import com.hyh.www.nearby.CommentBoxDialog.CommentListener;
import com.loopj.android.http.RequestParams;
import com.sina.weibo.sdk.component.view.AttentionComponentView.RequestParam;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

/**
 * 
 * @author xiaobai 2015-6-10
 * @todo( 评论弹出框 )
 */
public class CommentBoxDialog extends Dialog  implements
OnItemClickListener {
	private CommentBoxDialog _this = this;
	private Context context;
	private DisplayMetrics dm;
	private ArrayList<ArrayList<GezitechEntity_I>> emojis;
	private ArrayList<View> pageViews;
	private ArrayList<FaceAdapter> faceAdapters;
	private ArrayList<ImageView> pointViews;
	private LinearLayout ll_select_origin;
	private ViewPager vp_emoji;
	private int current;
	private EditText et_post_msg;
	private ImageButton ib_post_smile;
	private LinearLayout rl_emoji_action_box;
	private boolean SmileBoxIsShow = false;
	public long ruid = 0; //回复某人的uid
	public long nid = 0 ; //内容的id
	public String replayHint = ""; //回复某人的提示
	private CommentListener commentListener;
	public NearMsg itemData = null; //数据集
	protected CommentBoxDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		_this.context = context;
	}

	public CommentBoxDialog(Context context, int theme) {
		super(context, theme);
		_this.context = context;

	}

	public CommentBoxDialog(Context context) {
		super(context);
		_this.context = context;
	}

	public void initData() {
		dm = context.getResources().getDisplayMetrics();
		// 添加布局
		setContentView(R.layout.dialog_comment_box_dialog);

		et_post_msg = (EditText) this.findViewById(R.id.et_post_msg);		
		ll_select_origin = (LinearLayout) this.findViewById(R.id.ll_select_origin);
		vp_emoji = (android.support.v4.view.ViewPager) this.findViewById(R.id.vp_emoji);
		ib_post_smile = (ImageButton) this.findViewById(R.id.ib_post_smile);
		rl_emoji_action_box = (LinearLayout)this.findViewById(R.id.rl_emoji_action_box);
		//初始化值
		String initContent = GezitechApplication.systemSp.getString(nid+""+ruid, "");
		if( !this.replayHint.equals("") ){ //默认提示文字
			et_post_msg.setHint(  this.replayHint+"" );
		}
		et_post_msg.setText( initContent );
		et_post_msg.setSelection( initContent.length());
		setEditTextHeight();
		et_post_msg.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				
				setEditTextHeight();
				
			}
			
		});
		setEditTextFocus();
		
		_initEmojiView();
		_initEmojiPoint();
		_initEmojiData();

		ib_post_smile.setOnClickListener( new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (rl_emoji_action_box.getVisibility() == View.VISIBLE) {
					rl_emoji_action_box.setVisibility(View.GONE);
					_closeKeybordShow(true);
					SmileBoxIsShow = false;
				} else {
					_closeKeybordShow(false);
					rl_emoji_action_box.setVisibility(View.VISIBLE);
					SmileBoxIsShow = true;
				}
				et_post_msg.requestFocus();
				et_post_msg.setFocusable(true);
				et_post_msg.setCursorVisible(true);
				_setEditBackGround(true);
			}
		});
		
		// 发布的消息获得焦点
		et_post_msg.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if (rl_emoji_action_box.getVisibility() == View.VISIBLE) {
					rl_emoji_action_box.setVisibility(View.GONE);
					SmileBoxIsShow = false;
				}
				setEditTextFocus();
				return false;
			}
		});
		
		
		//取消回掉
		this.setOnCancelListener( new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				cannelAction();	
			}
		});
		//提交回调
		this.findViewById(R.id.ib_post_send).setOnClickListener( new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String postContent = et_post_msg.getText().toString();
				if( postContent.length() <= 0 ){
					android.widget.Toast.makeText( context, "内容不能为空！" , Toast.LENGTH_SHORT).show();
					return;
				}
				
				_closeKeybordShow( false );
				
				RequestParams params = new RequestParams();
				params.put("content",  postContent );
				String longs = GezitechApplication.systemSp.getString("long","");
				String lat = GezitechApplication.systemSp.getString("lat","");
				String city = GezitechApplication.systemSp.getString("city","");
				params.put("long", longs);
				params.put("lat", lat);
				params.put("address", city);
				params.put("nid", _this.nid );
				params.put("ruid",  _this.ruid );
				
				//GezitechAlertDialog.loadDialog( (Activity)context );
				//提交回复数据
				NearManager.getInstance().addNearbyFeedback(params, new OnAsynGetJsonObjectListener() {
					
					@Override
					public void OnAsynRequestFail(String errorCode, String errorMsg) {
						//GezitechAlertDialog.closeDialog();
						android.widget.Toast.makeText( context, errorMsg , Toast.LENGTH_SHORT).show();
					}
					
					@Override
					public void OnGetJSONObjectDone(org.json.JSONObject  jo ) {
						//发送消息,提示用户
						
						try {
							//当前用户的uid
							long currentUid = jo.has("uid") ? jo.getLong("uid") : 0 ;
							String nearbyimage = "";
							if( itemData.attachment != null && itemData.attachment.length() > 0  ){
								nearbyimage  = itemData.attachment.getString( 0 );
							}
							String json = "{"+
									"\"uid\":"+currentUid+","+
									"\"nickname\":\""+jo.getJSONObject("publisher").getString("nickname")+"\","+
									"\"head\":\""+jo.getJSONObject("publisher").getString("head")+"\","+
									"\"content\":\""+jo.getString("content")+"\","+
									"\"isLike\":0,"+
									"\"ctime\":"+jo.getLong("ctime") +","+
									"\"nearbycontent\":\""+StringUtil.subString( itemData.content , 25 )+"\","+
									"\"nearbyimage\":\""+nearbyimage+"\","+
									"\"nid\":"+_this.nid+
									"}";
							
							
							HashMap<String, Long> uids = new HashMap<String, Long>();
							if( itemData.uid  != currentUid ){
								uids.put(itemData.uid+"", itemData.uid);
							}
							if( ruid != 0 && ruid !=  itemData.uid ){
								uids.put(ruid+"", ruid);
							}
							//提示回复的人
							if (itemData.feedbackList != null && itemData.feedbackList.length() > 0) {
								
								for(int i = 0; i<itemData.feedbackList.length(); i++ ){
									
									org.json.JSONObject feed = itemData.feedbackList.getJSONObject( i );
									long uid = feed.getLong("uid");
									if( uids.containsKey( uid )  || uid == currentUid ){
										continue;
									}else{
										uids.put( uid+"" ,uid);
									}
								}
							}
							//提示喜欢的人
							if (itemData.likeList != null && itemData.likeList.length() > 0) {
								
								for(int i = 0; i<itemData.likeList.length(); i++ ){
									
									org.json.JSONObject like = itemData.likeList.getJSONObject( i );
									long uid = like.getLong("uid");
									if( uids.containsKey( uid )  || uid == currentUid ){
										continue;
									}else{
										uids.put( uid+"" ,uid);
									}
								}
							}

							GezitechService.sendMessage(20, json, uids );
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						clearDefaultVal();
						//GezitechAlertDialog.closeDialog();
						commentListener.setCommentCallBack( jo );
						_this.dismiss();
					}
				});
			}
		});
		
		this.setCanceledOnTouchOutside( true );
		this.setCancelable( true );
		//取消回调
		this.findViewById(R.id.rl_bg_click).setOnClickListener( new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cannelAction();	
			}
		});
		this.getWindow().setLayout(  LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		this.show();
	}
	private void setEditTextFocus(){
		Editable text = et_post_msg.getText();
		Spannable spanText = (Spannable) text;
		Selection.setSelection(spanText, text.length());
		et_post_msg.setEnabled(true);
		et_post_msg.requestFocus();
		et_post_msg.setFocusable(true);
		et_post_msg.setCursorVisible(true);
		et_post_msg.setFocusableInTouchMode(true);
		_setEditBackGround(true);
		_closeKeybordShow(true);
	}
	private void setEditTextHeight(){
		int LineCount = et_post_msg.getLineCount();
		if (LineCount <= 1) {
			ViewGroup.LayoutParams lp = et_post_msg.getLayoutParams();
			lp.height = (int) (48 * dm.density);
			et_post_msg.setLayoutParams(lp);
		} else {
			ViewGroup.LayoutParams lp = et_post_msg.getLayoutParams();
			lp.height = (int) ((48 + ((LineCount - 1) * 15)) * dm.density);
			et_post_msg.setLayoutParams(lp);
		}
	}
	private void cannelAction(){
		_closeKeybordShow( false );
		setDefaultVal();
		this.dismiss();
	}
	private void setDefaultVal(){
		 String content = et_post_msg.getText().toString();
		 //if( !content.equals("") ){
			GezitechApplication.systemSp.edit().putString(nid+""+ruid,content).commit();
		 //}
	}
	private void clearDefaultVal(){
		GezitechApplication.systemSp.edit().remove( nid+""+ruid ).commit();
	}

	// 初始化表情的视图
	private void _initEmojiView() {
		emojis = new Emotion().getParseEmojiList();
		pageViews = new ArrayList<View>();
		faceAdapters = new ArrayList<FaceAdapter>();
		for (int i = 0; i < emojis.size(); i++) {
			GridView view = new GridView(context);
			FaceAdapter adapter = new FaceAdapter(context, emojis.get(i));
			view.setAdapter(adapter);
			faceAdapters.add(adapter);
			view.setOnItemClickListener(this);
			view.setNumColumns(7);
			view.setBackgroundColor(Color.TRANSPARENT);
			view.setHorizontalSpacing(10);
			view.setVerticalSpacing(1);
			view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			view.setCacheColorHint(0);
			view.setPadding(5, 20, 5, 10);
			view.setSelector(new ColorDrawable(Color.TRANSPARENT));
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			view.setGravity(Gravity.CENTER);
			pageViews.add(view);
		}

	}

	// 初始化游标
	private void _initEmojiPoint() {
		// ll_select_origin
		pointViews = new ArrayList<ImageView>();
		ImageView imageView;
		for (int i = 0; i < pageViews.size(); i++) {
			imageView = new ImageView(context);
			imageView.setBackgroundResource(R.drawable.d1);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 10;
			layoutParams.rightMargin = 10;
			layoutParams.width = 8;
			layoutParams.height = 8;
			ll_select_origin.addView(imageView, layoutParams);
			if (i == 0) {
				imageView.setBackgroundResource(R.drawable.d2);
			}
			pointViews.add(imageView);
		}
	}

	/**
	 * 填充数据
	 */
	private void _initEmojiData() {
		vp_emoji.setAdapter(new ViewPagerAdapter(pageViews));

		vp_emoji.setCurrentItem(0);
		current = 0;
		vp_emoji.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				current = arg0;
				draw_Point(arg0);
				vp_emoji.setCurrentItem(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}

	/**
	 * 绘制游标背景
	 */
	public void draw_Point(int index) {
		for (int i = 0; i < pointViews.size(); i++) {
			if (index == i) {
				pointViews.get(i).setBackgroundResource(R.drawable.d2);
			} else {
				pointViews.get(i).setBackgroundResource(R.drawable.d1);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Emotion emoji = (Emotion) faceAdapters.get(current).getItem(arg2);
		if (emoji.drawable == R.drawable.face_del_icon_select) {
			int selection = et_post_msg.getSelectionStart();
			String text = et_post_msg.getText().toString();
			if (selection > 0) {
				String text2 = text.substring(selection - 1);
				if ("]".equals(text2)) {
					int start = text.lastIndexOf("[");
					int end = selection;
					et_post_msg.getText().delete(start, end);
					return;
				}
				et_post_msg.getText().delete(selection - 1, selection);
			}
		}
		if (!TextUtils.isEmpty(emoji.title)) {
			et_post_msg.append(emoji.emotion);
		}
	}
	// 判断软键盘是否打开 如果打开关闭
	private void _closeKeybordShow(boolean isOpen) {
		try {
			// 判断软键盘是否打开
			InputMethodManager im = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);

			if (!isOpen) {
				im.hideSoftInputFromWindow(et_post_msg.getWindowToken(), 0);
			} else {
				im.showSoftInput(et_post_msg, InputMethodManager.SHOW_FORCED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void _setEditBackGround(boolean isFocus) {
		if (isFocus) {
			et_post_msg.setBackgroundResource(R.drawable.common_inputbox_on);
		} else {
			et_post_msg
					.setBackgroundResource(R.drawable.common_inputbox_normal);
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		if (SmileBoxIsShow) {
			rl_emoji_action_box.setVisibility(View.GONE);
			SmileBoxIsShow = false;
			et_post_msg.requestFocus();
			et_post_msg.setFocusable(true);
			et_post_msg.setCursorVisible(true);
			_setEditBackGround(true);
			_closeKeybordShow(false);
		} else {
			cannelAction();
		}
	}
	
	public interface CommentListener{
		void setCommentCallBack( org.json.JSONObject jo );
	}
	
	public void setCommentListener(CommentListener commentListener ){
		this.commentListener = commentListener;
		
	}

}
