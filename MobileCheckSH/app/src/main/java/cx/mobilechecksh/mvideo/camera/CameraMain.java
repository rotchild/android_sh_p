package cx.mobilechecksh.mvideo.camera;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera.Parameters;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import cx.mobilechecksh.R;
import cx.mobilechecksh.mvideo.androidcamera.NetCameraService;
import cx.mobilechecksh.mvideo.androidcamera.NetEncoder;
import cx.mobilechecksh.mvideo.camera.config.CameraManager;
import cx.mobilechecksh.mvideo.camera.config.RSSIBoradCastReceiver;
import cx.mobilechecksh.mvideo.camera.config.SettingHelper;
import cx.mobilechecksh.mvideo.camera.picture.PictureManager;
import cx.mobilechecksh.mvideo.camera.picture.PictureParameters;
import cx.mobilechecksh.mvideo.camera.ui.FocusRectangle;
import cx.mobilechecksh.mvideo.camera.ui.RotateImageView;
import cx.mobilechecksh.mvideo.camera.ui.Switcher;
import cx.mobilechecksh.mvideo.camera.ui.VerticalSeekBar;
import cx.mobilechecksh.mvideo.picc.base.MobileCheckApplocation;
import cx.mobilechecksh.mvideo.picc.net.HttpParams;
import cx.mobilechecksh.mvideo.picc.net.HttpResponseHandler;
import cx.mobilechecksh.mvideo.picc.updateapp.UpdateAppDialog;
import cx.mobilechecksh.mvideo.picc.utils.G;
import cx.mobilechecksh.theme.MBaseActivity;
import cx.mobilechecksh.utils.UserManager;

@SuppressLint("ResourceAsColor")
public class CameraMain extends MBaseActivity implements
SurfaceHolder.Callback ,Switcher.OnSwitchListener,OnClickListener,OnLongClickListener,
		PictureManager.IstoragePictureListener,OnSeekBarChangeListener,CameraManager.OnNetWorkConnectedChangedListener,
		CameraManager.OnRSSIChangedListener
{
	private boolean 					hasSurface;
	private CameraManager 				cameraManager;
	private CaptureActivityHandler		handler;
	//	private InactivityTimer 			inactivityTimer;
	private String TAG = "hcxw camera";
	private SurfaceView surfaceView ;
	private final int				MESSAGE_TAKE_PIC = 3;
	private final int				MESSAGE_PREIVEW_PICTURE = 5;
	private final int				MESSAGE_START_RECORD= 6;
	private final int				MESSAGE_DO_FOCUS = 7;
	private final int				MESSAGE_STOP_RECORD= 8;
	private final int				MESSAGE_SWITCH_CAMERA= 9;
	private final int				CLEAR_SCREEN_DELAY= 10;
	private final int				MESSAGE_SHOW_THUM= 12;
	private final int				MESSAGE_ZOOM= 16;

	private ImageView mShutterButton;
	private FocusRectangle mFocusRectangle;
	private RotateImageView previewImage,wifiButton,b2fButton,focusBtn,settingBtn,senceBtn;
	private Switcher				mSwitcher;


	private  boolean				isCapture,isPause,isTakingPic;
	public static String caseId;

	@SuppressWarnings("unused")
	private boolean					isVIP,isFirstLogin;
	private Bundle mBundle,cameraConfigBundle;
	private PictureManager			pictureManager;


	///////////////////编码器对象//////////////////
	private NetEncoder mNetEncoder;

	private PreferenceGroup 		group;
	private static final int 		SCREEN_DELAY = 30 * 1000;

	//对焦状态
	private static final int 		FOCUSING = 1;
	private static final int 		FOCUS_SUCCESS = 3;
	private static final int 		FOCUS_FAIL = 4;
	private boolean					bRefresh = false;

	private VerticalSeekBar zoomBar;
	private boolean					isZoomIng = false;
	private int						zoomValue = 1;
	private Toast toast = null;
	private boolean					isLeft;
	private RSSIBoradCastReceiver rssiBoradCastReceiver;
	private boolean					isWIFI;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(getPackageName(), "oncreate");


		System.gc();
		MyOrientationEventListener.initInstance(this);
		initView();
		//获取相关默认配置信息
		getCameraConfig();


		hasSurface = false;
		//		inactivityTimer = new InactivityTimer(this);


		getDefaultSetting();
		//初始化网络服务模块
		initNetEncoder();


		//启动服务
		//		Intent service = new Intent(this,NetService.class);
		//		startService(service);


/*		UpdateApp updateApp = new UpdateApp(CameraMain.this, HttpParams.GETVERSION, updateAPPHttpResponseHandler);
		updateApp.start();	*/

		pictureManager.setStorageCallback(this);
	}

	private void initNetEncoder() {
		Intent ServiceIntent = new Intent();
		Bundle bundle = new Bundle();
		//bundle.putInt("sign",CameraManager.sign);
		bundle.putInt("sign", UserManager.getInstance().getCameraSign());//从用户信息中获取
		//bundle.putString("serverIP", CameraManager.serverIp);
		bundle.putString("serverIP", UserManager.getInstance().getCameraIp());
		bundle.putInt("captureW", CameraManager.previewWidth);
		bundle.putInt("caputreH", CameraManager.previewHeight);
		bundle.putBoolean("isUdp", SettingHelper.getInstance().getConnectType("UDP").equals("UDP")? true:false);
		
//		第一次启动时，写入网络配置
		String preferencesSSID = SettingHelper.getInstance().getSSID("");
		if(preferencesSSID !=null&&preferencesSSID.trim().equals("")){
			WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
			DhcpInfo d = wifiManager.getDhcpInfo();
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			SettingHelper.getInstance().saveNetConfig(CameraMain.this,wifiInfo.getSSID(), intToIp(wifiInfo.getIpAddress()),
					String.valueOf(intToIp(d.gateway)) , String.valueOf(intToIp(d.netmask)));
		}
		
		ServiceIntent.putExtras(bundle);
		ServiceIntent.setClass(this, NetCameraService.class);
		startService(ServiceIntent);
	}
	
	public String intToIp(int i) {
		return ( i & 0xFF) + "." + ((i >> 8 ) & 0xFF) + "." +  ((i >> 16 ) & 0xFF) +"." +((i >> 24 ) & 0xFF );
	} 

	private void getDefaultSetting(){
		CameraManager.serverIp = new String(HttpParams.getHost(this));
		try{
			String mSiGN=SettingHelper.getInstance().getSIGN("10001");//test
		}catch(Exception e){
			e.printStackTrace();
		}

		CameraManager.sign = Integer.parseInt(SettingHelper.getInstance().getSIGN("10001"));
		String pw_ph = SettingHelper.getInstance().getPw_ph("640x480");
		String[] pwph = new String[2];
		pwph = pw_ph.split("x");
		CameraManager.previewWidth = Integer.parseInt(pwph[0]);//幅面宽度
		CameraManager.previewHeight = Integer.parseInt(pwph[1]);//幅面高度

		CameraManager.bitrate = Integer.parseInt(SettingHelper.getInstance().getBIT("300"));
		CameraManager.fps = Integer.parseInt(SettingHelper.getInstance().getFPS("15"));
	}

	@Override
	public void onResume()
	{
		super.onResume();
		Log.d(getPackageName(), "onResume");

		isPause = false;
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface)
		{
			//如果activity是暂停状态而不是停止，因此surface 仍存在
			//因此 surfaceCreated() 不会被调用, 所以在这里初始化camera.
			initCamera(surfaceHolder);
		}else {
			//装载callback，等待surfaceCreated() 初始化camera
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}


		//		inactivityTimer.onResume();
		MyOrientationEventListener.initInstance(this).enable();

		getDefaultSetting();
		initNetEncoder();

		Handler h = new Handler();
		h.postDelayed(new Runnable() {

			@Override
			public void run() {
				boolean bRecord = false;
				cameraManager.isRecordVideo(bRecord);
				if (handler!=null) {
					if (!bRecord) {
						handler.sendEmptyMessage(MESSAGE_START_RECORD);
					}else {
						handler.sendEmptyMessage(MESSAGE_STOP_RECORD);
					}
				}


			}
		}, 3*1000);

		if(Util.getActiveNetwork(this)!=null&&Util.getActiveNetwork(this).isConnected()){
			wifiButton.setImageResource(R.drawable.wifi);
		}else {
			wifiButton.setImageResource(R.drawable.no_wifi);
			Toast.makeText(CameraMain.this, getResources().getString(R.string.camera_no_network), Toast.LENGTH_LONG).show();
		}
		//注册监听网络信号变化
		rssiBoradCastReceiver = new RSSIBoradCastReceiver();
		registerReceiver(rssiBoradCastReceiver, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(getPackageName(), "onStart");
	}

	@Override

	public void onPause() {
		Log.d(getPackageName(), "onPause");
		isPause = true;
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		//		inactivityTimer.onPause();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
			surfaceView.setOnClickListener(this);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		MyOrientationEventListener.initInstance(this).disable();
		cameraManager.closeDriver();
		//		resetScreenOn();
		super.onPause();
		

	}

	@Override
	protected void onDestroy() {
		//		inactivityTimer.shutdown();
		Log.d(getPackageName(), "onDestroy");

		super.onDestroy();
		if (!bRefresh) {
			stop();
		}else {
			bRefresh = false;
		}
	}

	@Override
	public void onUserInteraction() {
		super.onUserInteraction();
		//		keepScreenOnAwhile();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Log.d(getPackageName(), "onRestoreInstanceState");

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(getPackageName(), "onSaveInstanceState");
	}

	private void resetScreenOn() {
		if (handler!=null) {
			handler.removeMessages(CLEAR_SCREEN_DELAY);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	private void keepScreenOnAwhile() {
		if (handler!=null) {
			handler.removeMessages(CLEAR_SCREEN_DELAY);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			handler.sendEmptyMessageDelayed(CLEAR_SCREEN_DELAY, SCREEN_DELAY);
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		if (holder == null) {
			Log.e(TAG,"*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}


	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height)
	{
		boolean preview = holder.isCreating();
		cameraManager.setViewFinder(width, height, preview,surfaceView);

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		hasSurface = false;
	}

	public void setButtonEnable(boolean enable){
		mShutterButton.setEnabled(enable);
		previewImage.setEnabled(enable);
		//		Log.e("mShutterButton", "mShutterButton enable"+enable);
	}


	/**
	 * 获取相机配置信息
	 */
	private void getCameraConfig(){


		PreferenceInflater inflater = new PreferenceInflater(this);
		group =(PreferenceGroup) inflater.inflate(R.xml.camera_preferences);

		cameraManager = CameraManager.initCameraManagerInstance(this,group);
		pictureManager = PictureManager.initInstance(this);

		cameraManager.getCameraParameter(group);
		cameraManager.addOnNetWorkConnectedChangedListener(this);
		cameraManager.addOnRSSIChangedListener(this);
	}

	private void initView(){
		// 初始化显示
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 关闭标题栏
		Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		setContentView(R.layout.camera_mian);


		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

		surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
		surfaceView.setOnClickListener(this);
		LayoutInflater inflater = getLayoutInflater();

		ViewGroup rootView = (ViewGroup) findViewById(R.id.camera);
		View controlVierw = inflater.inflate(R.layout.camera_control, rootView);
		//	            inflater.inflate(R.layout.camera_title_control, rootView);
		mSwitcher = ((Switcher)controlVierw.findViewById(R.id.camera_switch));
		mSwitcher.setOnSwitchListener(this);
		mSwitcher.addTouchView(findViewById(R.id.camera_switch_set));
		mSwitcher.setSwitch(true);


		// Initialize shutter button.
		mShutterButton = (RotateImageView) controlVierw.findViewById(R.id.shutter_button);
		mShutterButton.setOnClickListener(this);
		mShutterButton.setOnLongClickListener(this);
		mShutterButton.setVisibility(View.VISIBLE);

		mFocusRectangle = (FocusRectangle) controlVierw.findViewById(R.id.focus_rectangle);

		previewImage = (RotateImageView) controlVierw.findViewById(R.id.review_thumbnail);
		previewImage.setOnClickListener(this);

		View controlTileVierw = inflater.inflate(R.layout.camera_title_control, rootView);
		b2fButton = (RotateImageView) controlTileVierw.findViewById(R.id.camera_b2f_switch);
		wifiButton = (RotateImageView) controlTileVierw.findViewById(R.id.camera_wifi);
		focusBtn = (RotateImageView) controlTileVierw.findViewById(R.id.camera_focus_switch);
		settingBtn = (RotateImageView) controlTileVierw.findViewById(R.id.camera_setting);
		//senceBtn = (RotateImageView) controlTileVierw.findViewById(R.id.camera_sence);

		b2fButton.setOnClickListener(this);
		wifiButton.setOnClickListener(this);
		focusBtn.setOnClickListener(this);
		settingBtn.setOnClickListener(this);
		//senceBtn.setOnClickListener(this);
		

		View zoomView = inflater.inflate(R.layout.camera_control_zoom_layout, rootView);
		zoomBar = (VerticalSeekBar) zoomView.findViewById(R.id.camera_zoom_seekbar);
		zoomBar.setOnSeekBarChangeListener(this);

		if(Util.getActiveNetwork(this)!=null&&Util.getActiveNetwork(this).isConnected()){
			wifiButton.setImageResource(R.drawable.wifi);
		}else {
			wifiButton.setImageResource(R.drawable.no_wifi);
			Toast.makeText(CameraMain.this, getResources().getString(R.string.camera_no_network), Toast.LENGTH_LONG).show();
		}

	}


	public void updatePreviewImage(Bitmap bitmap){
		previewImage.setImageBitmap(bitmap);
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			cameraManager.openDriver(surfaceHolder);
			if (handler == null) {
				handler = new CaptureActivityHandler(this, cameraManager);
				cameraManager.setHandler(handler);
			}
		} catch (IOException e) {
			e.printStackTrace();
			displayFrameworkBugMessageAndExit();

		}catch (RuntimeException e) {
			e.printStackTrace();
			displayFrameworkBugMessageAndExit();
		}
	}




	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage("相机打开失败，请检查相机是否被其他程序占用！");
		builder.setPositiveButton("确定", new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}


	/**
	 * 处理退出的方法
	 */
	private void stop(){
		unregisterReceiver(rssiBoradCastReceiver);
		if (cameraManager!=null) {
			cameraManager.release();
			pictureManager.release();

		}

		((MobileCheckApplocation)getApplication()).exit();
	}

	/**
	 * 判断是否在拍照
	 * @param isTakingPic
	 */
	public void takingPicture(boolean isTakingPic){
		this.isTakingPic = isTakingPic;
	}

	private boolean bTakingPic(){
		if(isTakingPic){
			Toast.makeText(this, "正在拍照，请稍后", Toast.LENGTH_SHORT).show();
		}
		return isTakingPic;
	}
	@Override
	public boolean onSwitchChanged(Switcher source, boolean onOff) {

		if (bTakingPic()) return false;
		CameraManager.getCameraManagerInstance().controlListener.switchCaptureMode(onOff);
		mSwitcher.setSwitch(onOff);

		return false;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		//前后摄像头切换
		case R.id.camera_b2f_switch:
			if (bTakingPic()) return;
			CameraManager.getCameraManagerInstance().controlListener.switchCamera();
			break;
			//		case R.id.camera_flash_switch:
			//			if (bTakingPic()) return;
			//			String flashMode = CameraManager.getCameraManagerInstance().controlListener.setFlashMode();
			//			if (flashMode.equals("torch")) {
			//				flashButton.setImageResource(R.drawable.camera_ic_torch);
			//			}
			//			if (flashMode.equals("off")) {
			//				flashButton.setImageResource(R.drawable.camera_ic_flash_off);
			//			}
			//			if (flashMode.equals("on")) {
			//				flashButton.setImageResource(R.drawable.camera_ic_flash_on);
			//			}
			//			if (flashMode.equals("auto")) {
			//				flashButton.setImageResource(R.drawable.camera_ic_flash_auto);
			//			}
			//			break;
			//wifi信号管理
		case R.id.camera_wifi:
			if (bTakingPic()) return;
			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
			break;
		case R.id.review_thumbnail:
			if (bTakingPic()) return;
			//pictureManager.showPictureCurrent(CameraMain.this,handler.getCurrentPicture());
			break;
		case R.id.shutter_button:
			setButtonEnable(false);
			takingPicture(true);
			CameraManager.getCameraManagerInstance().controlListener.takePhoto();
			break;

		case R.id.camera_preview:
			if (isTakingPic) return;
			setButtonEnable(false);
			CameraManager.getCameraManagerInstance().controlListener.dofocus();
			break;

		case R.id.camera_focus_switch:
			if (bTakingPic()) return;
			String focusMode = CameraManager.getCameraManagerInstance().controlListener.setFocusMode();
			if (focusMode.equals(Parameters.FOCUS_MODE_AUTO)) {
				focusBtn.setImageResource(R.drawable.camera_button_focus_auto);
				Toast.makeText(CameraMain.this,getResources().getString(R.string.camera_focus_micro_closed), Toast.LENGTH_LONG).show();
			}
			if (focusMode.equals(Parameters.FOCUS_MODE_MACRO)) {
				focusBtn.setImageResource(R.drawable.camera_button_focus_micro);
				Toast.makeText(CameraMain.this, getResources().getString(R.string.camera_focus_micro_open), Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.camera_setting:
			if (bTakingPic()) return;
			Intent i = new Intent(CameraMain.this, CameraSetting.class);
			startActivity(i);
//		case R.id.camera_sence:
//			if (bTakingPic()) return;
//			disPlaySenceMode(CameraManager.getCameraManagerInstance().controlListener.getSenceModes());
			break;
		default:
			break;
		}

	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@SuppressLint({ "ResourceAsColor", "NewApi" })
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d("zooming", keyCode+"");
		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:
			new AlertDialog.Builder(CameraMain.this)
			.setTitle(R.string.dialog_title)
			.setMessage(R.string.camera_exit)
			.setPositiveButton(getResources().getString(R.string.camera_sure), new AlertDialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			}).setNegativeButton(getResources().getString(R.string.camera_not_sure), new AlertDialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).show();
			break;
		case KeyEvent.KEYCODE_CAMERA:
			//			setButtonEnable(false);
			//			handler.sendEmptyMessage(MESSAGE_TAKE_PIC);
			break;

		case KeyEvent.KEYCODE_ZOOM_IN://right +
//			if (zoomValue<10) sendZoomMesg(++zoomValue);
			if (zoomValue<10)  CameraManager.getCameraManagerInstance().controlListener.setZoom(++zoomValue);
			zoomBar.setProgress(zoomValue);
			displayZoom(zoomValue);
			break;
		case KeyEvent.KEYCODE_ZOOM_OUT://left -
			//if (zoomValue>1) sendZoomMesg(--zoomValue);
			if (zoomValue>1)  CameraManager.getCameraManagerInstance().controlListener.setZoom(--zoomValue);
			zoomBar.setProgress(zoomValue);
			displayZoom(zoomValue);
			break;

		case 256:
			break;

		default:
			break;
		}
		return true;
	}



	public  void displayZoom(int zoomValue){
		this.zoomValue = zoomValue;
		if(toast!=null) toast.cancel();
		toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		LinearLayout toastView = (LinearLayout) toast.getView();
		toastView.setBackgroundColor(android.R.color.transparent);
		TextView textView = new TextView(getApplicationContext());
		textView.setTextSize(40);
		textView.setTextColor(Color.GREEN);
		textView.setText(zoomValue+"X");
		textView.setGravity(Gravity.CENTER);
		textView.setBackgroundColor(android.R.color.transparent);
		textView.setLayoutParams(new LinearLayout.LayoutParams(150,100));
		toastView.addView(textView, 0);
		toast.show();
	}
	
	
	private void disPlaySenceMode(final HashMap<CharSequence, CharSequence> map){
		if(map == null) return;
		if (map.isEmpty()) return;
		final GridView gridView = new GridView(this);
		gridView.setLayoutParams(new GridView.LayoutParams(400,400));
		gridView.setColumnWidth(80);
		gridView.setNumColumns(4);
		gridView.setBackgroundColor(android.R.color.transparent);
		final int selectPosition = 0;
		
		Collection<CharSequence> titles = map.values();
		final List<CharSequence> titleList = new ArrayList<CharSequence>();
		 for (Iterator iterator = titles.iterator(); iterator.hasNext();) {
			 titleList.add((CharSequence) iterator.next());
			
		}
		 Collection<CharSequence> values = map.keySet();
		 final List<CharSequence> valueList = new ArrayList<CharSequence>();
		 for (Iterator iterator = values.iterator(); iterator.hasNext();) {
			 CharSequence c = (CharSequence) iterator.next();
						 valueList.add(c);
			 
		 }
		gridView.setAdapter(new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				TextView textView = new TextView(getApplicationContext());
				textView.setTextSize(20);
				textView.setTextColor(Color.GREEN);
				textView.setText(titleList.get(position));
				textView.setGravity(Gravity.CENTER);
				 if (!valueList.get(position).equals(cameraManager.getCameraManagerInstance().getSenceMode())) {
					 textView.setTextColor(Color.BLUE);
					}
				 
				textView.setLayoutParams(new GridView.LayoutParams(100,100));
				return textView;
			}
			
			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return titleList.get(position);
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return titleList.size();
			}
		});

		final Dialog dialog = new Dialog(this,R.style.dialog_custom);;
		dialog.setContentView(gridView);
		dialog.setTitle(R.string.camera_sence);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		 // 设置透明度为0.3  
		lp.alpha = 0.5f;  
		window.setAttributes(lp);  
		
//		for (int i = 0; i < valueList.size(); i++) {
//			 if (valueList.get(i).equals(cameraManager.getCameraManagerInstance().getSenceMode())) {
//					gridView.setSelection(i);
//				}
//		}
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
				cameraManager.getCameraManagerInstance().setSenceMode(valueList.get(arg2).toString());
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	



	@Override
	public void onBackPressed() {
		super.onBackPressed();



	}

	public void refresh(){
		bRefresh = true;
		finish();
		if (cameraManager!=null) {
			cameraManager.release();

		}
		Intent intent = getIntent();
		overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);

	}


	protected void updateFocusIndicator(){
		if (mFocusRectangle ==null) {
			return;
		}
		int focusState = cameraManager.mFocusState ;
		if (focusState== FOCUSING ) {
			mFocusRectangle.showStart();
		} else if (focusState == FOCUS_SUCCESS) {
			mFocusRectangle.showSuccess();
		} else if (focusState == FOCUS_FAIL) {
			mFocusRectangle.showFail();
		} else {
			mFocusRectangle.clear();
		}
		Log.d("updateFocusIndicator", "focusState = "+focusState);
	}

	protected void clearFocusState() {
		mFocusRectangle.clear();

	}


	private void sendZoomMesg(int zoomValue){
		handler.removeMessages(MESSAGE_ZOOM);
		Message message = new Message();
		message.what = MESSAGE_ZOOM;
		message.arg1 = zoomValue;
		handler.sendMessage(message);
	}

	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.shutter_button:
			//			setButtonEnable(false);
			//			handler.sendEmptyMessage(MESSAGE_DO_FOCUS);
			break;

		default:

			break;
		}

		return false;
	}


	@Override
	public PictureParameters setStorageParameters(long dateTaken) {
		String title =  G.getPhoneCurrentTime("yyyyMMddHHmmss") ;
		PictureParameters parameters = new PictureParameters();	
		String directory = Environment.getExternalStorageDirectory()+"/PICCSurvey/";
		if (!new File(directory).exists()) {
			new File(directory).mkdirs();
		}
		parameters.setDirectory(directory);
		parameters.setTitle(title);
		return parameters;
	}

	@Override
	public void afterStorage(String path, boolean success) {
		if(success)
			Log.d(TAG, "save picture success");

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, final int progress,
                                  boolean fromUser) {
		//sendZoomMesg(progress);
		CameraManager.getCameraManagerInstance().controlListener.setZoom(progress);
		displayZoom(progress);

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		Log.d("zooming", "touch");
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		Log.d("zooming", "stop");
	}

	@Override
	public void onNetWorkConnected() {
		isWIFI = true;
		wifiButton.setImageResource(R.drawable.wifi);
		Toast.makeText(CameraMain.this, getResources().getString(R.string.camera_wifi_connected), Toast.LENGTH_LONG).show();
		if(isWIFI) getSSIDstrength();
	}

	@Override
	public void onNetWorkDisconnected() {
		isWIFI = false;
		wifiButton.setImageResource(R.drawable.no_wifi);
		Toast.makeText(CameraMain.this, getResources().getString(R.string.camera_wifi_disconnected), Toast.LENGTH_LONG).show();
	}


	/** 检测APP 版本信息回调函数 */
	HttpResponseHandler updateAPPHttpResponseHandler = new HttpResponseHandler(){
		@Override
		public void response(boolean success, String response, Throwable error) {
			// TODO Auto-generated method stub
			super.response(success, response, error);
			//弹出升级APP 对话框
			if(success){
				UpdateAppDialog.cleanHint();
				boolean b = UpdateAppDialog.showUpdateAppDialog(CameraMain.this,G.UPDATE_APP_SAVE_PATH,R.drawable.logo_code_orange);
			}else{
				G.showToast(CameraMain.this, getString(R.string.about_system_weberror), true);
			}

		}			
	};
	@Override
	public void onRSSIChanged() {
		if(isWIFI) getSSIDstrength();

	}


	private void getSSIDstrength(){
		// Wifi的连接速度及信号强度：
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		// WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		WifiInfo info = wifiManager.getConnectionInfo();
		if (info.getBSSID() != null) {
			// 链接信号强度
			//			int strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);
			int strength = info.getRssi();
			// 链接速度
			//			int speed = info.getLinkSpeed();
			//			// 链接速度单位
			//			String units = WifiInfo.LINK_SPEED_UNITS;
			//			// Wifi源名称
			//			String ssid = info.getSSID();
			setSSIDstatus(Util.calcSSIDStrength(strength));

		}
	}


	private   void setSSIDstatus(int status){
		switch (status) {
		case Util.SSID_GOOD:
			//			Toast.makeText(CameraMain.this, "信号很好", Toast.LENGTH_LONG).show();
			wifiButton.setImageResource(R.drawable.wifi);
			break;
		case Util.SSID_SOSO:
			wifiButton.setImageResource(R.drawable.wifi_soso);
			//			Toast.makeText(CameraMain.this, "信号一般", Toast.LENGTH_LONG).show();
			break;
		case Util.SSID_BAD:
			wifiButton.setImageResource(R.drawable.wifi_bad);
			//			Toast.makeText(CameraMain.this, "信号很差", Toast.LENGTH_LONG).show();
			break;

		default:
			break;
		}
	}


}
