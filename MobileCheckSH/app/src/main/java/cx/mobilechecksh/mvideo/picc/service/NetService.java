package cx.mobilechecksh.mvideo.picc.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import cx.mobilechecksh.mvideo.camera.config.SettingHelper;


public class NetService extends Service {

	private final String TAG = this.getClass().getSimpleName();
	private UploadWork uploadWork;
	private SnycService mSnycService;
	private WakeLock wakeLock;
	
	private SendGpsLogService mGpsLogService;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d(TAG, "NetService oncreate");
		PowerManager pm = (PowerManager)getSystemService(this.getApplicationContext().POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NetServiceWakeLock");
		wakeLock.acquire();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (uploadWork==null){
			uploadWork = new UploadWork(this);		
			uploadWork.start();	
		}
		if (mSnycService==null){
			String accessToken = SettingHelper.getInstance().getAccessToken();
			mSnycService = new SnycService(this,accessToken);
			mSnycService.start();
		}
		if (mGpsLogService==null){
			mGpsLogService = new SendGpsLogService(this);
			mGpsLogService.start();
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(uploadWork != null){
			uploadWork.stop();
			uploadWork=null;
		}
		if(mSnycService!= null){
//			mSnycService.stop();
			mSnycService.mHandler.getLooper().quit();
			mSnycService=null;
//			mSnycService.interrupt();
		}
		
		if(mGpsLogService!=null){
			mGpsLogService.stop();
			mGpsLogService=null;
		}
		wakeLock.release();
		super.onDestroy();
	}
	
}
