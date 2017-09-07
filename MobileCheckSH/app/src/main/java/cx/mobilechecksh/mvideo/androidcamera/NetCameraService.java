package cx.mobilechecksh.mvideo.androidcamera;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.TimerTask;

import cx.mobilechecksh.mvideo.camera.net.AudioPlaybak;
import cx.mobilechecksh.mvideo.camera.net.AudioRecorder;
import cx.mobilechecksh.mvideo.camera.net.HandsetButton;


public class NetCameraService extends Service
{
	public NetEncoder mNetEncoder;
	
	final int AUDIOCHECK=1;
	final int DISPLAYBUTTON=2;
	final int REMOVEBUTTON=3;
	final int RESUME=4;
	final int CHECKINTERVAL=3000;

	private boolean bPause=false;
	NetCameraBinder mNetCameraBinder;
	AudioRecorder mAudioRecorder;
	int m_audioBack_size;
	final int audioBuffSize=2048*4;
	int audioLive=0;
	AudioPlaybak mAudioPlayback;
	WindowManager mWinManager;
	HandsetButton mBluetoothButton;
	boolean  bDisplayButton;
	String serverIP="0.0.0.0";
	int		sign=111;
	int		captureW=640;
	int 	captureH=480;
	byte[] videoConfig = new byte[256];
	final String resourceDir = "/sdcard/webcamera/";
	int serviceState=0; //0:init 1:start

	int videoConfigSize = 0;

	MyPhoneStateListener myPhoneStateListener;

	Handler checker = new Handler() {
		public void handleMessage(Message msg) {
			//要做的事情
			switch (msg.what){
			case AUDIOCHECK:
				if (audioLive<=0){
					if(mAudioRecorder != null){
						mAudioRecorder.stopRecording();
					}
					if(mAudioPlayback != null){
						mAudioPlayback.stop();
					}					
				//	RemoveButton();
					Log.d("Audio","stop!!!!!");
				}else{
					audioLive--;
					Message message = obtainMessage(msg.what);
					sendMessageDelayed(message, CHECKINTERVAL);
				}
				break;
			case DISPLAYBUTTON:
				//DisplayButton();
				break;
			case REMOVEBUTTON:
				//RemoveButton();
			case RESUME:
				bPause=false;
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	void DisplayButton()
	{
		if(mWinManager == null)
			mWinManager = (WindowManager)getApplicationContext().getSystemService(WINDOW_SERVICE);
		//if(mBluetoothButton == null)
		if(!bDisplayButton){
			mBluetoothButton = new HandsetButton(this,mWinManager,mAudioPlayback);
			bDisplayButton = true;
		}
		if(mBluetoothButton.isChecked()){
			mAudioPlayback.start(AudioManager.STREAM_VOICE_CALL);
			Toast.makeText(this, "手机音频切换到蓝牙耳机", Toast.LENGTH_LONG).show();
		}else{
			mAudioPlayback.start(AudioManager.STREAM_MUSIC);
			Toast.makeText(this, "手机音频切换到外放", Toast.LENGTH_LONG).show();
		}
		
	}
	
	void RemoveButton()
	{
		if(mWinManager != null && bDisplayButton){
			mWinManager.removeView(mBluetoothButton);
			bDisplayButton = false;
		}
			
	}
	
	TimerTask audioCheck = new TimerTask(){
		public void run() {
			Log.d("Audio","check");
		}
	};  

	public interface INetCameraInterface 
	{
		//在这里添加接口
		public int setEncoder(int sign, String server);
		public int setVideoFormat(int videoWidth, int videoHight, int frameRate, int bitRate);
		public NetEncoder getNetEncoder();
	}
	public void loadVideoConfig(int dataSize,byte []data){
		try {
			File infoFile = new File(resourceDir + String.valueOf(captureW) + "_"
				+ String.valueOf(captureH) + ".dat");
			if (infoFile.exists()) {
				InputStream is;
				is = new FileInputStream(infoFile);
				if (is.available() > 256)	return ;
				videoConfigSize = is.read(videoConfig);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public void addAudioData(int dataSize,byte []data){
  		//Log.e("NetEncoder","play audio ");
		if (bPause==true) return ;
		if (audioLive==0){
			//开始录音工作
			audioLive=3;
			mAudioRecorder.startRecording();
			Message message=new Message();
			message.what=AUDIOCHECK;
			checker.sendMessageDelayed(message, CHECKINTERVAL);
//	        //振动提示
	        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	        vibrator.vibrate(1000);
	        
//	        Message msg = new Message();
//	        msg.what = DISPLAYBUTTON;
//	        checker.sendMessage(msg);
			//开始播放
			mAudioPlayback.start(AudioManager.STREAM_MUSIC);
			
		}
		audioLive=5;
		mAudioPlayback.addAudioData(dataSize, data);
		return ;
	}
	
	class NetCameraBinder extends Binder implements INetCameraInterface
	{
		public int setEncoder(int camerasign, String server){
			serverIP=server;
			sign=camerasign;
			mNetEncoder.setServer(serverIP);
			mNetEncoder.setSign(sign);
			return 0;
		}
		public int setVideoFormat(int videoWidth, int videoHight, int frameRate,
				int bitRate){
			mNetEncoder.setVideoFormat(videoWidth, videoHight, frameRate, bitRate);
			byte temp[] = null;
			mNetEncoder.setVideoDesc(0, temp);
			return 0;
		}
		public NetEncoder getNetEncoder(){
			return mNetEncoder;
		}
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mNetCameraBinder;
	}

	@Override
    public void onCreate()
	{
		mNetCameraBinder= new NetCameraBinder();
		mNetEncoder = new NetEncoder();
		mAudioRecorder = new AudioRecorder(mNetEncoder);
		mAudioPlayback = new AudioPlaybak(); 
    	registAudioPlay();

 
    	TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
    	myPhoneStateListener=new MyPhoneStateListener();
    	tm.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    	instance=this;
	}
	
    @Override
    public void onDestroy() 
    {
    	stopForeground(true);
    	mNetEncoder.stopWork();
    	mAudioRecorder.stopRecording();
    	mAudioPlayback.stop();
    	mNetEncoder=null;
    	mAudioRecorder=null;
    	mAudioPlayback=null;  
    	Message msg = new Message();
    	msg.what = REMOVEBUTTON;
    	checker.sendMessage(msg);
    	TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
    	tm.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
    }


	@Override
    public int onStartCommand(Intent intent, int flag, int startID)
    {
		if (intent==null) return START_NOT_STICKY;
		Log.d("netService","startCommand");
		Bundle bundle = new Bundle();
        bundle = intent.getExtras();
        serverIP = bundle.getString("serverIP");
        sign 	 = bundle.getInt("sign");
        captureW = bundle.getInt("captureW",captureW);
        captureH = bundle.getInt("caputreH",captureH);

        
		mNetEncoder.setVideoFormat(captureW, captureH, 10, 600);
		if (videoConfigSize != 0) {
			mNetEncoder.setVideoDesc(videoConfigSize, videoConfig);
		}
		mNetEncoder.setServer(serverIP);
		mNetEncoder.setSign(sign);
		if (serviceState==0) mNetEncoder.startWork(bundle.getBoolean("isUdp"));
		serviceState=1;
		return START_REDELIVER_INTENT;
    }
	
	Handler toastHandler = new Handler();
	public void showToast(final String msg)
	{
		  toastHandler.post(new Runnable()
		  {
			   public void run() 
			   {
				   Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			   }
		  });
	}
	
	public native int registAudioPlay();
	static {
		System.loadLibrary("netencoder");
	}
	
	
	 public class  MyPhoneStateListener extends PhoneStateListener {
        public   void  onCallStateChanged( int  state, String incomingNumber) {
            if (state== TelephonyManager.CALL_STATE_IDLE) //挂断
            {  
    			Message message=new Message();
    			message.what=RESUME;
    			checker.sendMessageDelayed(message, CHECKINTERVAL);
            }  
            else   if (state== TelephonyManager.CALL_STATE_OFFHOOK) //接听
            {  
				bPause=true;
				mAudioRecorder.stopRecording();
				mAudioPlayback.stop();
				audioLive=0;
            }  
            else   if (state== TelephonyManager.CALL_STATE_RINGING) //来电
            {  
            	//Log.e("IDLE" ,"来电");
            	//Toast.makeText(NetCameraService.this, "来电", Toast.LENGTH_SHORT).show();
				bPause=true;
				mAudioRecorder.stopRecording();
				mAudioPlayback.stop();
				audioLive = 0;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	private static NetCameraService instance = null;

	/** 获取Service实例 */
	public static NetCameraService getInstance() {
		return instance;
	}

}