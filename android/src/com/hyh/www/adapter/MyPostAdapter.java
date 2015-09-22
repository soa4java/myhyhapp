package com.hyh.www.adapter;

import java.net.URLDecoder;
import java.util.Timer;
import com.gezitech.basic.GezitechActivity;
import com.gezitech.basic.GezitechAlertDialog;
import com.gezitech.contract.GezitechEntity_I;
import com.gezitech.contract.GezitechManager_I.OnAsynInsertListener;
import com.gezitech.service.managers.ShoutManager;
import com.gezitech.util.IOUtil;
import com.gezitech.util.IOUtil.CacheCompleteListener;
import com.gezitech.util.StringUtil;
import com.gezitech.widget.MyListView;
import com.gezitech.widget.RemoteImageView;
import com.hyh.www.R;
import com.hyh.www.ZhuyeActivity;
import com.hyh.www.entity.FieldVal;
import com.hyh.www.entity.Shout;
import com.hyh.www.widget.ImageShow;
import com.hyh.www.widget.YMDialog;
import com.loopj.android.http.RequestParams;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author xiaobai 2014-10-15
 * @todo( 我的发布适配器 )
 */
public class MyPostAdapter extends BasicAdapter {
	private MediaPlayer mediaPlayer;
	private String currentAudioPath;
	private GezitechActivity activity;
	Timer tr = new Timer();
	private MyListView listView;
	private int lastTimeposition = -1;
	private int currTimeposition = -1;

	public MyPostAdapter(GezitechActivity activity,MyListView listView) {
		this.activity = activity;
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

	public void setCannelTimer() {
		tr.cancel();
	}

	private boolean isdated = false;

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final Shout item = (Shout) getItem(position);
		final Hv hv;
		if( view == null ){
			view = inflater.inflate(R.layout.list_my_release, null);
			hv = new Hv(view);
			view.setTag( hv );
		}else{
			hv = (Hv) view.getTag();
		}
		final long curr = System.currentTimeMillis();
		final long startTime = item.activetime * 1000 + item.ctime * 1000;
		if (startTime > curr) {
			long dtime = startTime - curr;
			hv.tv_shengyu.setText(getDateStr(dtime / 1000));
			hv.tv_shengyu.setTextColor(Color.parseColor("#ff340c"));
			isdated = false;
		} else {
			hv.tv_shengyu.setText("已过期");
			hv.tv_shengyu.setTextColor(Color.parseColor("#949494"));
			isdated = true;
		}
		hv.tv_shangjia.setText(item.type);

		hv.tv_fabu.setText(item.rangename);

		hv.tv_huifu.setText(item.maxReplycount + "");

		hv.tv_myrelease_beizhu.setText(FieldVal.value( item.caption ) );
		// 语音时长
		hv.myrelease_NUMBER.setText( (int)(item.speechtime/1000) + "''");
		// 动画波音

		// 播放语音
		hv.rl_yuyin_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				IOUtil.downloadAndCacheFile(item.speech, false,
						new CacheCompleteListener() {
							@Override
							public void onCacheComplete(String fileName,
									boolean success, String msg, Object tag) {
								currTimeposition = position+1;
								if( lastTimeposition != -1 ){//停止之前的动画播音效果
									
									refreshAnim( lastTimeposition );
								}
								playAudio(fileName, hv.iv_voice_sign );
							}
						});
			}
		});
		hv.iv_voice_sign.setImageResource( R.drawable.dhck_voice_blue_03 );
		// 上传的图片
		if (item.litpic != null && !item.litpic.equals("")
				&& !item.litpic.equals("null")) {
			hv.iv_pic.setImageUrl(item.litpic);
			hv.iv_pic.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//画廊展示图片
					final String[] images = new String[1];
					String[] pic = item.litpic.split("src=");
					images[0] = StringUtil.stringDecode( pic[1] );
					ImageShow.jumpDisplayPic(images, 0, activity);
				}
			});
		} else {
			hv.iv_pic.setVisibility(View.GONE);
		}
		// 取消发布
		if (!isdated) {// 未过期
			hv.myrelease_wdfb_cancel.setVisibility(View.VISIBLE);
			hv.myrelease_wdfb_cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					cannel( item , position );
				}
			});
		} else {
			hv.myrelease_wdfb_cancel.setVisibility(View.GONE);
		}
		return view;
	}

	class Hv {
		private TextView tv_shangjia;
		private TextView tv_fabu;
		private TextView tv_huifu;
		private TextView tv_shengyu;
		private TextView tv_myrelease_beizhu;
		private TextView myrelease_NUMBER;
		private RemoteImageView iv_pic;
		private RelativeLayout myrelease_wdfb_cancel;
		private ImageView iv_voice_sign;
		private RelativeLayout rl_yuyin_bg;

		public Hv(View view) {
			tv_shangjia = (TextView) view.findViewById(R.id.tv_shangjia);
			tv_fabu = (TextView) view.findViewById(R.id.tv_fabu);
			tv_huifu = (TextView) view.findViewById(R.id.tv_huifu);
			tv_shengyu = (TextView) view.findViewById(R.id.tv_shengyu);
			tv_myrelease_beizhu = (TextView) view
					.findViewById(R.id.tv_myrelease_beizhu);
			myrelease_NUMBER = (TextView) view
					.findViewById(R.id.myrelease_NUMBER);
			iv_pic = (RemoteImageView) view.findViewById(R.id.iv_pic);
			myrelease_wdfb_cancel = (RelativeLayout) view
					.findViewById(R.id.myrelease_wdfb_cancel);
			iv_voice_sign = (ImageView) view.findViewById(R.id.iv_voice_sign);
			rl_yuyin_bg = (RelativeLayout) view.findViewById(R.id.rl_yuyin_bg);
		}
	}
	//取消喊一喊
	private void cannel( final Shout item, final int position ){
		final YMDialog ymdialog = new YMDialog( activity );
		ymdialog.setHintMsg("确定要取消?")
		.setConfigButton( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ymdialog.dismiss();
				RequestParams params = new RequestParams();
				params.put("id", item.id);
				GezitechAlertDialog.loadDialog( activity );
				ShoutManager.getInstance().deleteshout(params, new OnAsynInsertListener() {
					
					@Override
					public void OnAsynRequestFail(String errorCode, String errorMsg) {
						activity.Toast( errorMsg );
						GezitechAlertDialog.closeDialog();
					}
					
					@Override
					public void onInsertDone(String id) {
						GezitechAlertDialog.closeDialog();
						remove( position );
					}
				});
			}
		})
		.setCloseButton( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ymdialog.dismiss();				
			}
		});
		
	}
	
	private String getDateStr(long time) {
		long h = time / 3600;
		long m = (time % 3600) / 60;
		long s = (time % 3600) % 60;
		return (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":"
				+ (s < 10 ? "0" + s : s);
	}
	public void refreshAnim( int position ){
		int firstVisibleItemIndex = listView.getFirstVisiblePosition();
		//Log.v("position", position+"position===="+ listView.getChildCount()+"==="+firstVisibleItemIndex);
		int ii = position-firstVisibleItemIndex;
		for (int i = 0; i < listView.getChildCount(); i++) {    
			if( i != ii ) continue;
			View v = listView.getChildAt( i );
			if( v == null ) continue;
			try{
				ImageView iv_voice_sign = (ImageView) v.findViewById(R.id.iv_voice_sign);
				if( iv_voice_sign != null ){
					//Log.v("position",position+"shanshuposition====");
					iv_voice_sign.setImageResource(R.drawable.dhck_voice_blue_03);
					 AnimationDrawable animPlay = (AnimationDrawable) iv_voice_sign.getDrawable();
	                 if (animPlay!= null && !animPlay.isRunning()) {
	                     animPlay.stop();
	                 }
				}
		    }catch(Exception e){
		    	
		    }
			
		}
	}
	public void refreshcountdown(MyListView list_view) {

		int firstVisibleItemIndex = list_view.getFirstVisiblePosition();

		for (int i = 0; i < list_view.getChildCount(); i++) {    
		    try{
		    	View v = list_view.getChildAt( i );
		    	Shout item = (Shout)this .getItem( firstVisibleItemIndex + i -1 );
			    TextView tv_shengyu = (TextView) v.findViewById( R.id.tv_shengyu );
			    RelativeLayout myrelease_wdfb_cancel = (RelativeLayout) v.findViewById( R.id.myrelease_wdfb_cancel );
			    if( tv_shengyu != null ){
			    	final long curr = System.currentTimeMillis();
					final long startTime = item.activetime * 1000 + item.ctime * 1000;
				    if (startTime > curr) {
						long dtime = startTime - curr;
						tv_shengyu.setText(getDateStr(dtime / 1000));
						tv_shengyu.setTextColor(Color.parseColor("#ff340c"));
					} else {
						tv_shengyu.setText("已过期");
						tv_shengyu.setTextColor(Color.parseColor("#949494"));
						myrelease_wdfb_cancel.setVisibility( View.GONE );
					}
			    
			    
			    }
		    }catch(Exception e){
		    	
		    }
		}
		
	}
	/**
	 * 播放音频文件
	 * 
	 * @param audioPath
	 * @param iv_voice_sign
	 */
	private void playAudio(String audioPath,final ImageView iv_voice_sign) {
		if (audioPath.equals(currentAudioPath)) {
			stopAudio();
		} else {
			try {
				if (mediaPlayer != null) {
					if (mediaPlayer.isPlaying()) {
						mediaPlayer.stop();
					}
					mediaPlayer.release();
					mediaPlayer = null;
				}
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setDataSource(audioPath);
				mediaPlayer.prepare();
				mediaPlayer.start();
				currentAudioPath = audioPath;
				iv_voice_sign.setImageResource(R.drawable.progress_voice_blue);
				 AnimationDrawable animPlay = (AnimationDrawable) iv_voice_sign.getDrawable();
                 if (!animPlay.isRunning()) {
                     animPlay.start();
                 }
				mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mp) {
								stopAudio();
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
			}
			lastTimeposition = currTimeposition;
		}
	}

	/**
	 * 停止播放音频
	 */
	private void stopAudio() {
		currentAudioPath = null;
		try {
			if (mediaPlayer != null) {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
				}
				mediaPlayer.release();
				mediaPlayer = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		refreshAnim( currTimeposition );
	}

}
