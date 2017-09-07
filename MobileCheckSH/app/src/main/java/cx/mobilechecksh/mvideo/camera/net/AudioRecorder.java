package cx.mobilechecksh.mvideo.camera.net;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;


import java.io.FileOutputStream;

import cx.mobilechecksh.mvideo.androidcamera.NetEncoder;

public class AudioRecorder implements Runnable {

	private boolean bRecording;
	public boolean bPause=false;
	public boolean isStopped = false;
	
	
	private int frequence = 8000;
	private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	private int audioMinBuffer;
	private byte[] buffer;
	private int BufferSize = 2034;
	private int framePeriod =BufferSize/2;
	

	NetEncoder mNetEncoder;
	AudioRecord mAudioRecord;

	FileOutputStream totalfos;

	public AudioRecorder(NetEncoder mNetParam) {
		//初始化音频采集，bAudio指示是否发生发送音频
		bRecording = false;
		audioMinBuffer = AudioRecord.getMinBufferSize(frequence, channelConfig,audioEncoding);
		if (audioMinBuffer<BufferSize*2) audioMinBuffer=BufferSize*2; 
		buffer = new byte[BufferSize];
		//audioMinBuffer=BufferSize;
		mNetEncoder = mNetParam;
	}

	public void setRecording(boolean param) {
		bRecording = param;
		return;
	}

	public boolean isRecording() {
		return bRecording;
	}


	public void startRecording() {
		if (isRecording()) return;
		bRecording = true;
		mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
		frequence, channelConfig, audioEncoding, audioMinBuffer);
		mAudioRecord.startRecording();
		bRecording = true;
		new Thread(this).start();
		
//		mAudioRecord.setRecordPositionUpdateListener(updateListener);  
//		mAudioRecord.setPositionNotificationPeriod(100);  
// 		mAudioRecord.startRecording();
// 		mAudioRecord.read(buffer, 0, buffer.length); // Fill buffer  

	}

	public void stopRecording() {
		if (!isRecording()) return;
		bRecording = false;
		mAudioRecord.stop();
		mAudioRecord.release();
		mAudioRecord=null;
	}

	public void pauseRecording() {
		bPause=true;
		if (!isRecording()) return;
		mAudioRecord.stop();
	}

	public void resumeRecording() {
		bPause=false;
		if (!isRecording()) return;
		mAudioRecord.startRecording();
	}

	public void run() {
		if (!isRecording()) {
			Log.e("AudioSource", "stop the recording");
			return;
		}
		int rlen;
		while (bRecording) {
			if (isStopped) {
				Log.d("audiosource", "stopped");
				return;
			}
			if (bPause!=true){
				rlen = fillBuffer(buffer, 0, BufferSize, audioMinBuffer,
						mAudioRecord);
				
				while (rlen != BufferSize) {
					Log.d("audiosource", "error rlen read");
					return;
				}
				mNetEncoder.addAudioFrame(BufferSize, buffer);
			}
		}
	}

	private int fillBuffer(byte[] buf, int offset, int size, int eachMaxSize,
			AudioRecord fis) {
		int dlen;
		int buf_len = 0;
		int target_size = size;
		while (target_size > 0) {

			if (target_size >= eachMaxSize) {
				dlen = fis.read(buf, offset + buf_len, eachMaxSize);
			} else {
				dlen = fis.read(buf, offset + buf_len, target_size);

			}
			if (dlen >= 0) {
				buf_len += dlen;
				target_size -= dlen;
			} else {
				Log.e("AudioError","Unknown"+ String.valueOf(dlen));
				return -1;
			}
		}
		return size;
	}

	private AudioRecord.OnRecordPositionUpdateListener updateListener = new AudioRecord.OnRecordPositionUpdateListener()
	{
	     public void onPeriodicNotification(AudioRecord recorder)
	        {  
	    	recorder.read(buffer, 0, buffer.length); // Fill buffer  
			mNetEncoder.addAudioFrame(buffer.length, buffer);

	        }  
	  
	        public void onMarkerReached(AudioRecord recorder)
	        {  
	            // NOT USED  
	        }  
	};

}