package cx.mobilechecksh.mvideo.picc.base;

import android.app.Application;
import android.content.Intent;

import cx.mobilechecksh.mvideo.androidcamera.NetCameraService;
import cx.mobilechecksh.mvideo.camera.config.SettingHelper;
import cx.mobilechecksh.mvideo.picc.net.HttpParams;
import cx.mobilechecksh.mvideo.picc.service.NetService;
import cx.mobilechecksh.mvideo.picc.updateapp.UpdateAppDialog;
import cx.mobilechecksh.utils.UserManager;


public class MobileCheckApplocation extends Application {
	public static int activityInitFlag=0;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		initApp();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}


	private void initApp(){		
		//初始化网络参数
		HttpParams.init(getApplicationContext());
		cx.mobilechecksh.net.HttpParams.init(getApplicationContext());
		UserManager.getInstance().init(getApplicationContext());
		//初始化数据库对象
//		SQLiteManager.getInstance().init(getApplicationContext());
		
		SettingHelper.getInstance().init(getApplicationContext());

		
		//启动定位服务
//		BaiduLocation baiduLocation = BaiduLocation.instance();
//		baiduLocation.init(getApplicationContext(), baiduLocation.getDefaultLocationClientOption());
		
		//启动抓异常包类
//		CrashHandler.getInstance().init(getApplicationContext(),getPackageName());
		
		
		//启动自动发送发送错误报告
//		AlarmColock.getInstance(getApplicationContext()).setAlarmColock("16:37:00");
	}
	

	
	/** 注销程序 */
	public void logout(){
		//停止百度服务
//		BaiduLocation.instance().stop();
		//停止程序服务
		Intent netServiceIntent = new Intent(getApplicationContext(),NetService.class);
		getApplicationContext().stopService(netServiceIntent);
		//停止音视频服务
		Intent CameraServiceIntent = new Intent(getApplicationContext(),NetCameraService.class);
		getApplicationContext().stopService(CameraServiceIntent);
	}
	
	public void exit(){
		//清除已提示过标记
		UpdateAppDialog.cleanHint();
		logout();
	}
	
	
	
    
}
