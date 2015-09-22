package com.hyh.www.adapter;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.basic.GezitechApplication;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynGetJsonObjectListener;
import com.gezitech.contract.GezitechManager_I.OnAsynInsertListener;
import com.gezitech.entity.User;
import com.gezitech.service.GezitechService;
import com.gezitech.service.managers.NearManager;
import com.gezitech.service.managers.ShoutManager;
import com.gezitech.util.ClickSpanUtil;
import com.gezitech.util.DateUtil;
import com.gezitech.util.IOUtil;
import com.gezitech.util.ImageDownloader;
import com.gezitech.util.IOUtil.CacheCompleteListener;
import com.gezitech.util.StringUtil;
import com.gezitech.util.ToastMakeText;
import com.gezitech.widget.FlowLayout;
import com.gezitech.widget.MyListView;
import com.gezitech.widget.RemoteImageView;
import com.hyh.www.R;
import com.hyh.www.ZhuyeActivity;
import com.hyh.www.entity.Emotion;
import com.hyh.www.entity.FieldVal;
import com.hyh.www.entity.NearMsg;
import com.hyh.www.entity.Shout;
import com.hyh.www.nearby.CommentBoxDialog;
import com.hyh.www.nearby.CommentBoxDialog.CommentListener;
import com.hyh.www.nearby.NearUtils;
import com.hyh.www.widget.ActivityCommon;
import com.hyh.www.widget.ImageShow;
import com.hyh.www.widget.YMDialog;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author xiaobai 2014-10-15
 * @todo( 附近人的适配器 )
 */
public class NearbyAdapter extends BasicAdapter {

	private Activity activity;
	private ImageDownloader mImageDownloader;
	public boolean mImageDownloaderOpen = true;
	private DisplayMetrics dm;
	private boolean isShowHead = false;
	private Fragment FragmentObj = null;// 回调的 startofresult();
	private boolean isJump = true;// 是否跳转，主要防止 高亮用户名 和 评论同时跳转的实现
	private MyListView listView = null;
	private User user;

	public NearbyAdapter(Activity activity) {
		this.activity = activity;
		mImageDownloader = new ImageDownloader(null);
		mImageDownloader.setMode(ImageDownloader.Mode.CORRECT);
		dm = this.activity.getResources().getDisplayMetrics();
		user = GezitechService.getInstance().getCurrentLoginUser(activity);
	}

	public void isShowHead(boolean isShowHead) {
		this.isShowHead = isShowHead;
	}

	public void setFragmentVal(Fragment FragmentObj) {
		this.FragmentObj = FragmentObj;
	}

	public void setisJump(boolean isJump) {
		this.isJump = isJump;
	}

	public void setListView(MyListView listView) {
		this.listView = listView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public GezitechEntity_I getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub

		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final Hv hv;
		if (view == null) {
			view = inflater.inflate(R.layout.list_nearby, null);
			hv = new Hv(view);
			view.setTag(hv);
		} else {
			hv = (Hv) view.getTag();
		}
		final NearMsg item = (NearMsg) list.get(position);

		initGetView(hv, item, position, view);

		return view;
	}

	// 初始化item视图
	private void initGetView(final Hv hv, final NearMsg item,
			final int position, final View view) {
		// 内容
		final String content = FieldVal.value(item.content);
		if (content.length() > 100) {
			hv.tv_content.setText(StringUtil.curString(content, 100));
			hv.bt_expand.setVisibility(View.VISIBLE);
			hv.bt_expand.setTag("0");
			hv.bt_expand.setText("全文");
			hv.bt_expand.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Object tag = v.getTag();
					if (tag.equals("0")) {
						hv.tv_content.setText(content);
						hv.bt_expand.setText("收起");
						v.setTag("1");
					} else {
						hv.tv_content.setText(StringUtil
								.curString(content, 100));
						hv.bt_expand.setText("全文");
						v.setTag("0");
					}
				}
			});

		} else {
			hv.tv_content.setText(content);
			hv.bt_expand.setVisibility(View.GONE);
		}
		// 图片的插入
		hv.ll_pic_list.removeAllViews();

		if (item.attachment != null && item.attachment.length() > 0) {
			int threeWidth = (int)( dm.widthPixels-110*dm.density )/3;
			int attachmentLen = item.attachment.length();
			int itemPicWidth = attachmentLen == 1 ? (int)(200*dm.density)
					: attachmentLen == 2 ? (int)(100*dm.density) : threeWidth;
			// 画廊展示图片
			final String[] images = new String[attachmentLen];
			hv.ll_pic_list.setSpacing((int)(dm.density*5), (int)(dm.density*5) );
			for (int i = 0; i < attachmentLen; i++) {
				hv.ll_pic_list.setVisibility(View.VISIBLE);

				String picUrl = "";
				try {
					picUrl = item.attachment.getString(i);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				View imageView = inflater.inflate(R.layout.list_image, null);
				LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(threeWidth, threeWidth );
				imageViewParams.leftMargin = 0;
				imageViewParams.topMargin = 0;
				imageViewParams.bottomMargin = 0;
				imageViewParams.rightMargin = 0;
				imageViewParams.gravity = Gravity.LEFT;
				imageView.setLayoutParams( imageViewParams );
				ImageView mImageView = (ImageView) imageView
						.findViewById(R.id.iv_img);

				if (attachmentLen == 1) {

					// 如果只有一条，就按图片的比例展示
					int defaultW = 200;
					int defaultH = 200;
					try {
						Pattern pattern = Pattern
								.compile("^.*h=([0-9]*)&w=([0-9]*)&.*$");
						Matcher matcher = pattern.matcher(picUrl);

						while (matcher.find()) {
							defaultW = Integer.parseInt(matcher.group(2));
							defaultH = Integer.parseInt(matcher.group(1));
						}
					} catch (Exception ex) {
					}
					android.view.ViewGroup.LayoutParams para = mImageView
							.getLayoutParams();
					para.width = (int) (defaultW * dm.density);
					para.height = (int) (defaultH * dm.density);
					mImageView.setLayoutParams(para);

				} else {

					LayoutParams para = mImageView.getLayoutParams();
					para.height = itemPicWidth;
					para.width = itemPicWidth;
					mImageView.setLayoutParams(para);

				}
				mImageDownloader.download(picUrl, mImageDownloaderOpen,
						mImageView);

				final int index = i;
				// 截取原图
				String[] pic = picUrl.split("src=");
				images[index] = StringUtil.stringDecode(pic.length > 1 ? pic[1]
						: pic[0]);

				mImageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						ImageShow.jumpDisplayPic(images, index, activity);
					}
				});
				hv.ll_pic_list.addView(imageView);
			}

		} else {
			hv.ll_pic_list.setVisibility(View.GONE);
		}
		// 头像
		try {
			mImageDownloader.download(item.publisher.getString("head"),
					mImageDownloaderOpen, hv.iv_head);
			hv.iv_head.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						ActivityCommon.lookFriendData(
								item.publisher.getInt("id"), activity);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			hv.tv_username.setText(item.publisher.getString("nickname"));
			hv.tv_username.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						ActivityCommon.lookFriendData(
								item.publisher.getInt("id"), activity);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			int sex = item.publisher.getInt("sex");

			if (sex == 1) {// 男
				hv.iv_sex.setVisibility(View.VISIBLE);
				hv.iv_sex.setBackgroundResource(R.drawable.icon_male);
			} else if (sex == 2) {// 女
				hv.iv_sex.setVisibility(View.VISIBLE);
				hv.iv_sex.setBackgroundResource(R.drawable.icon_female);
			} else {// 性别未填写
				hv.iv_sex.setVisibility(View.GONE);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		hv.tv_time.setText(DateUtil.getShortTime(item.ctime * 1000));
		hv.tv_distance.setText((int) item.m + "m");
		if (item.islike > 0) {
			hv.iv_like.setBackgroundResource(R.drawable.icon_like_sel);
		} else {
			hv.iv_like.setBackgroundResource(R.drawable.icon_like_nor);
		}
		// 判断评论和喜欢的列表是否为空
		hv.ll_comment_box.setVisibility(View.GONE);
		if ((item.likeList != null && item.likeList.length() > 0)
				|| (item.feedbackList != null && item.feedbackList.length() > 0)) {
			hv.ll_comment_box.setVisibility(View.VISIBLE);
		}

		// 喜欢的交互
		hv.iv_like.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RequestParams params = new RequestParams();
				String longs = GezitechApplication.systemSp.getString("long",
						"");
				String lat = GezitechApplication.systemSp.getString("lat", "");
				String city = GezitechApplication.systemSp
						.getString("city", "");
				params.put("long", longs);
				params.put("lat", lat);
				params.put("address", city);
				params.put("nid", item.id);
				// 喜欢的回掉
				NearUtils.likeSubmit(activity, params, new CommentListener() {

					@Override
					public void setCommentCallBack(JSONObject jo) {
						item.islike = item.islike == 1 ? 0 : 1;
						if (jo != null) {// 喜欢
							item.likeList.put(jo);

							try {
								String nearbyimage = "";
								if (item.attachment != null
										&& item.attachment.length() > 0) {
									nearbyimage = item.attachment.getString(0);
								}
								String json = "{" + "\"uid\":"
										+ user.id
										+ ","
										+ "\"nickname\":\""
										+ jo.getJSONObject("publisher")
												.getString("nickname")
										+ "\","
										+ "\"head\":\""
										+ jo.getJSONObject("publisher")
												.getString("head")
										+ "\","
										+ "\"content\":\""
										+ ""
										+ "\","
										+ "\"isLike\":1,"
										+ "\"ctime\":"
										+ jo.getLong("ctime")
										+ ","
										+ "\"nearbycontent\":\""
										+ StringUtil
												.subString(item.content, 25)
										+ "\"," + "\"nearbyimage\":\""
										+ nearbyimage + "\"," + "\"nid\":"
										+ item.id + "}";

								HashMap<String, Long> uids = new HashMap<String, Long>();
								if (item.uid != user.id) {
									uids.put(item.uid + "", item.uid);
								}
								// 提示回复的人
								if (item.feedbackList != null
										&& item.feedbackList.length() > 0) {

									for (int i = 0; i < item.feedbackList
											.length(); i++) {

										org.json.JSONObject feed = item.feedbackList
												.getJSONObject(i);
										long uid = feed.getLong("uid");
										if (uids.containsKey(uid)
												|| uid == user.id) {
											continue;
										} else {
											uids.put(uid + "", uid);
										}
									}
								}
								// 提示喜欢的人
								if (item.likeList != null
										&& item.likeList.length() > 0) {

									for (int i = 0; i < item.likeList.length(); i++) {

										org.json.JSONObject like = item.likeList
												.getJSONObject(i);
										long uid = like.getLong("uid");
										if (uids.containsKey(uid)
												|| uid == user.id) {
											continue;
										} else {
											uids.put(uid + "", uid);
										}
									}
								}

								GezitechService.sendMessage(19, json, uids);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						} else {// 喜欢取消 删除 本地列表中的数据
							if (item.likeList != null
									&& item.likeList.length() > 0) {
								JSONArray likeJr = new JSONArray();
								for (int j = 0; j < item.likeList.length(); j++) {

									try {
										JSONObject likeItem = item.likeList
												.getJSONObject(j);
										if (likeItem.getLong("uid") != user.id) { // 如果喜欢列表中
																					// 遍历不等于才组装新的
																					// arrayList
											likeJr.put(likeItem);
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								item.likeList = likeJr;
							}

						}
						list.set(position, item);
						getViewByPosition(position, view);
					}
				});
			}
		});
		// 喜欢列表的追加
		hv.ll_like_list.removeAllViews();
		hv.ll_like_list.setVisibility(View.GONE);
		if (item.likeList != null && item.likeList.length() > 0) {
			hv.ll_like_list.setVisibility(View.VISIBLE);
			ImageView iv = new ImageView(activity);
			iv.setImageResource(R.drawable.icon_like_sel);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			iv.setLayoutParams(params);
			hv.ll_like_list.addView(iv);
			String step = "";
			for (int i = 0; i < item.likeList.length(); i++) {

				TextView tv = new TextView(activity);
				tv.setTextColor(activity.getResources().getColor(
						R.color.color3CA5F3));
				tv.setTextSize(12);
				LinearLayout.LayoutParams paramss = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				paramss.leftMargin = 10;
				tv.setLayoutParams(paramss);
				try {
					final JSONObject publisher = item.likeList.getJSONObject(i)
							.getJSONObject("publisher");

					tv.setText(step + publisher.getString("nickname"));
					tv.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 点击跳转到个人详情
							try {
								ActivityCommon.lookFriendData(
										publisher.getInt("id"), activity);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				step = ",";
				hv.ll_like_list.addView(tv);
			}
		}

		// 评论
		hv.iv_comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CommentBoxDialog cbd = new CommentBoxDialog(activity,
						R.style.dialog_load1);
				cbd.nid = item.id;
				cbd.ruid = 0;
				cbd.itemData = item;
				cbd.initData();
				cbd.setCommentListener(new CommentBoxDialog.CommentListener() {

					@Override
					public void setCommentCallBack(JSONObject jo) {
						// 评论回掉
							
						item.feedbackList = addJsonArray(item.feedbackList, jo );
						
						list.set(position, item);
						getViewByPosition(position, view);
						

					}
				});
			}
		});
		// 如果评论和喜欢都存在 才显示线条，否者隐藏
		hv.v_line.setVisibility(View.GONE);
		if (item.likeList != null && item.likeList.length() > 0
				&& item.feedbackList != null && item.feedbackList.length() > 0) {

			hv.v_line.setVisibility(View.VISIBLE);

		}

		// 评论列表的渲染
		hv.ll_comment_list.removeAllViews();
		if (item.feedbackList != null && item.feedbackList.length() > 0) {
			// 超过3条 显示更多
			hv.ll_comment_list.setVisibility(View.VISIBLE);
			initCommentList(item, hv, item.feedbackList.length() > 3 ? 3
					: item.feedbackList.length(), position, view);
			if (item.feedbackList.length() > 3) {
				hv.bt_more_comment.setVisibility(View.VISIBLE);
				hv.bt_more_comment.setTag("0");
				hv.bt_more_comment.setText("更多");
				hv.bt_more_comment.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Object tag = v.getTag();
						hv.ll_comment_list.removeAllViews();
						if (tag.equals("0")) {

							initCommentList(item, hv,
									item.feedbackList.length(), position, view);
							hv.bt_more_comment.setText("收起");
							v.setTag("1");
						} else {
							initCommentList(item, hv, 3, position, view);
							hv.bt_more_comment.setText("更多");
							v.setTag("0");
						}
					}
				});

			} else {
				hv.bt_more_comment.setVisibility(View.GONE);
			}

		} else {
			hv.bt_more_comment.setVisibility(View.GONE);
			hv.ll_comment_list.setVisibility(View.GONE);
		}
		
		//删除 当当前的信息 是当前用户发布的  则显示删除的操作按钮
		if( user.id == item.uid ){
			hv.tv_delete.setVisibility( View.VISIBLE );
			hv.tv_delete.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//GezitechAlertDialog.loadDialog(activity);
					RequestParams params = new RequestParams();
					params.put("nid",  item.id );
					NearManager.getInstance().delNearBy(params,  new OnAsynGetJsonObjectListener() {
						
						@Override
						public void OnAsynRequestFail(String errorCode, String errorMsg) {
							// TODO Auto-generated method stub
							//GezitechAlertDialog.closeDialog();
							new ToastMakeText(activity).Toast( errorMsg );
						}
						
						@Override
						public void OnGetJSONObjectDone(JSONObject jo) {
							//GezitechAlertDialog.closeDialog();
							remove( position );
						}
					});
				}
			});
		}else{
			hv.tv_delete.setVisibility( View.GONE );
		}
		
	}

	private void initCommentList(final NearMsg item, final Hv hv,
			int showitemLen, final int position, final View view) {
		for (int i = 0; i < showitemLen; i++) {
			final View cv = inflater.inflate(R.layout.list_comment_list, null);
			ImageView iv_person_head = (ImageView) cv
					.findViewById(R.id.iv_person_head);
			if (!isShowHead) {
				iv_person_head.setVisibility(View.GONE);
			} else {
				iv_person_head.setVisibility(View.VISIBLE);
			}
			TextView tv_comment = (TextView) cv.findViewById(R.id.tv_comment);
			try {
				final JSONObject feed = item.feedbackList.getJSONObject(i);
				// 用户名加名称
				String contents = feed.getJSONObject("publisher").getString(
						"nickname");
				boolean isreplyer = false;
				if (feed.has("replyer") && !feed.isNull("replyer")) {
					try {
						if (feed.getJSONObject("replyer").has("nickname")) {
							isreplyer = true;
							contents += "回复"
									+ feed.getJSONObject("replyer").getString(
											"nickname");
						}
					} catch (Exception e) {
						// TODO: handle exception
					}

				}
				contents += "：" + feed.getString("content");

				SpannableString ss = Emotion.getInstace().getExpressionString(
						activity, contents.replace("\\?","")); 
				tv_comment.setText(ss);
				tv_comment.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				// 回复者
				if (isreplyer) {

					Pattern p1 = Pattern.compile( feed.getJSONObject("replyer")
							.getString("nickname").replace("\\?","") );
					setKeyworkClickable(tv_comment, ss, p1, new ClickSpanUtil(
							new OnTextViewClickListener() {

								@Override
								public void setStyle(TextPaint ds) {
									ds.setColor(activity.getResources()
											.getColor(R.color.color3CA5F3));
									ds.setUnderlineText(false);
								}

								@Override
								public void clickTextView() {
									isJump = false;
									// 跳转到个人详情
									try {
										ActivityCommon.lookFriendData(feed
												.getJSONObject("replyer")
												.getInt("id"), activity,
												FragmentObj);
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
							}));
				}

				// 被回复者
				Pattern p2 = Pattern.compile(feed.getJSONObject("publisher")
						.getString("nickname").replace("\\?","") );
				setKeyworkClickable(tv_comment, ss, p2, new ClickSpanUtil(
						new OnTextViewClickListener() {

							@Override
							public void setStyle(TextPaint ds) {
								ds.setColor(activity.getResources().getColor(
										R.color.color3CA5F3));
								ds.setUnderlineText(false);
							}

							@Override
							public void clickTextView() {
								// 跳转到个人详情
								isJump = false;
								try {
									ActivityCommon.lookFriendData(
											feed.getJSONObject("publisher")
													.getInt("id"), activity,
											FragmentObj);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}));
				tv_comment.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (!isJump)
							return;
						try {
							CommentBoxDialog cbd = new CommentBoxDialog(
									activity, R.style.dialog_load1);
							cbd.nid = item.id;
							long ruid = feed.getJSONObject("publisher")
									.getLong("id");
							cbd.ruid = (ruid == user.id) ? 0 : ruid;
							cbd.replayHint = (ruid == user.id) ? "" : "回复"
									+ feed.getJSONObject("publisher")
											.getString("nickname");
							cbd.itemData = item;
							cbd.initData();
							cbd.setCommentListener(new CommentBoxDialog.CommentListener() {

								@Override
								public void setCommentCallBack(JSONObject jo) {
									// 评论回掉
									
									item.feedbackList = addJsonArray(item.feedbackList, jo );
									
									list.set(position, item);
									getViewByPosition(position, view);

								}
							});

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
				tv_comment.setOnLongClickListener( new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						long fuid = 0;
						try {
							fuid = feed.getLong("uid");
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
						}
						if( item.uid == user.id || user.id == fuid ) {
						
							final YMDialog ym = new YMDialog( activity );
							ym.setHintMsg("确定删除？")
							.setCloseButton( new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									ym.dismiss();
								}
							}).setConfigButton( new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									ym.dismiss();
									//删除评论
									GezitechAlertDialog.loadDialog(activity);
									RequestParams params =  new RequestParams();
									try {
										params.put("fid", feed.getLong("id") );
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									NearManager.getInstance().delFeedBack(params, new OnAsynGetJsonObjectListener() {
										
										@Override
										public void OnAsynRequestFail(String errorCode, String errorMsg) {
											// TODO Auto-generated method stub
											GezitechAlertDialog.closeDialog();
											new ToastMakeText(activity).Toast( errorMsg );
										}
										
										@Override
										public void OnGetJSONObjectDone(JSONObject jo) {
											// TODO Auto-generated method stub
											GezitechAlertDialog.closeDialog();
											if (item.feedbackList != null && item.feedbackList.length() > 0) {
												JSONArray feedbackJr = new JSONArray();
												for (int j = 0; j < item.feedbackList.length(); j++) {
	
													try {
														JSONObject feedItem = item.feedbackList.getJSONObject(j);
														if ( feed.getLong("id") != feedItem.getLong("id") ) {
															feedbackJr.put(feedItem);
														}
													} catch (JSONException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
												}
												item.feedbackList = feedbackJr;
												list.set(position, item );
												
												if(  item.feedbackList.length() > 0 ){
													hv.ll_comment_list.removeView( cv );
												}else{
													getViewByPosition(position, view);
												}
											}
											
										}
									});
								}
							});
						
						}
						
						return false;
					}
				});
				if (isShowHead) {
					mImageDownloader.download(feed.getJSONObject("publisher")
							.getString("head"), mImageDownloaderOpen,
							iv_person_head);
					iv_person_head.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 跳转到个人详情
							try {
								ActivityCommon.lookFriendData(feed
										.getJSONObject("publisher")
										.getInt("id"), activity);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			hv.ll_comment_list.addView(cv);
		}
	}

	class Hv {

		private TextView tv_content;
		private FlowLayout ll_pic_list;
		private Button bt_expand;
		private ImageView iv_head;
		private TextView tv_username;
		private ImageView iv_sex;
		private TextView tv_time;
		private TextView tv_distance;
		private ImageView iv_like;
		private View v_line;
		private ImageView iv_comment;
		private FlowLayout ll_like_list;
		private LinearLayout ll_comment_list;
		private LinearLayout ll_comment_box;
		private Button bt_more_comment;
		private TextView tv_delete;

		public Hv(View view) {
			tv_content = (TextView) view.findViewById(R.id.tv_content);
			ll_pic_list = (FlowLayout) view.findViewById(R.id.ll_pic_list);
			bt_expand = (Button) view.findViewById(R.id.bt_expand);
			bt_more_comment = (Button) view.findViewById(R.id.bt_more_comment);
			iv_head = (ImageView) view.findViewById(R.id.iv_head);
			tv_username = (TextView) view.findViewById(R.id.tv_username);
			iv_sex = (ImageView) view.findViewById(R.id.iv_sex);
			tv_time = (TextView) view.findViewById(R.id.tv_time);
			tv_distance = (TextView) view.findViewById(R.id.tv_distance);
			iv_like = (ImageView) view.findViewById(R.id.iv_like);
			v_line = (View) view.findViewById(R.id.v_line);
			iv_comment = (ImageView) view.findViewById(R.id.iv_comment);
			ll_like_list = (FlowLayout) view.findViewById(R.id.ll_like_list);
			ll_comment_list = (LinearLayout) view
					.findViewById(R.id.ll_comment_list);
			ll_comment_box = (LinearLayout) view
					.findViewById(R.id.ll_comment_box);
			tv_delete = (TextView) view.findViewById( R.id.tv_delete);
		}
	}

	// 设置具体某个关键字可点击
	private void setKeyworkClickable(TextView textview, SpannableString ss,
			Pattern pattern, ClickableSpan cs) {
		Matcher matcher = pattern.matcher(ss.toString());

		while (matcher.find()) {
			String key = matcher.group();
			if (!"".equals(key)) {
				int start = ss.toString().indexOf(key);
				int end = start + key.length();
				setClickTextView(textview, ss, start, end, cs);
			}

		}

	}

	private void setClickTextView(TextView textview, SpannableString ss,
			int start, int end, ClickableSpan cs) {
		ss.setSpan(cs, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		textview.setText(ss);
		textview.setMovementMethod(LinkMovementMethod.getInstance());

	}

	public interface OnTextViewClickListener {
		public void clickTextView();

		public void setStyle(TextPaint ds);
	}

	// 获取item view
	public void getViewByPosition(int pos, View view) {
		// final int firstListItemPosition = listView.getFirstVisiblePosition();
		// final int lastListItemPosition = firstListItemPosition +
		// listView.getChildCount() - 1;
		// pos += 1;
		// View view = null;
		// int position = 0;
		// if (pos < firstListItemPosition || pos > lastListItemPosition) {
		// view = this.getView(pos, null, listView);
		// position = pos;
		// } else {
		// final int childIndex = pos - firstListItemPosition;
		// view = listView.getChildAt(childIndex);
		// position = childIndex;
		// }
		NearMsg item = (NearMsg) list.get(pos);
		try {
			Hv hv = new Hv(view);
			initGetView(hv, item, pos, view);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	//追加jsonarray 的数据
	private JSONArray addJsonArray( JSONArray list, JSONObject jo ){
		
		JSONArray jr =  new JSONArray();
		jr.put( jo );
		for( int i = 0 ; i <list.length() ; i++ ){
			try {
				jr.put( list.get( i ) );
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return jr;
		
	}

}
