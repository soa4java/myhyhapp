package com.gezitech.util;

import java.io.IOException;

import android.media.MediaRecorder;
import android.os.Environment;
/**
 * 
 * @author xiaobai
 * 2014-11-5
 * @todo( 语音 )
 */
public  class SoundMeter {
	static final private double EMA_FILTER = 0.6;

	private MediaRecorder mRecorder = null;
	private double mEMA = 0.0;
	
	public SoundMeter(){
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	}

	public void start(String path, String name) {
		if (!Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return;
		}
		try {
			if (mRecorder == null) {
				mRecorder = new MediaRecorder();
				mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
				mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			}
			
			
			
			mRecorder.setOutputFile(path+"/"+name);
		
			mRecorder.prepare();
			mRecorder.start();
			
			mEMA = 0.0;
		} catch (IllegalStateException e) {
			System.out.print(e.getMessage());
		} catch (IOException e) {
			System.out.print(e.getMessage());
		}

	}

	public void stop() {
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
	}

	public void pause() {
		if (mRecorder != null) {
			mRecorder.stop();
		}
	}

	public void start() {
		if (mRecorder != null) {
			mRecorder.start();
		}
	}

	public double getAmplitude() {
		if (mRecorder != null)
			return (mRecorder.getMaxAmplitude() / 2700.0);
		else
			return 0;

	}

	public double getAmplitudeEMA() {
		double amp = getAmplitude();
		mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
		return mEMA;
	}
}
