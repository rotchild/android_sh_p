package cx.mobilechecksh.mvideo.camera.config;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.os.Message;
import android.os.StatFs;
import android.os.SystemClock;
import android.util.Config;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;


import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cx.mobilechecksh.R;
import cx.mobilechecksh.mvideo.androidcamera.H264Encoder;
import cx.mobilechecksh.mvideo.androidcamera.NetCameraService;
import cx.mobilechecksh.mvideo.camera.CameraHardwareException;
import cx.mobilechecksh.mvideo.camera.CameraMain;
import cx.mobilechecksh.mvideo.camera.CaptureActivityHandler;
import cx.mobilechecksh.mvideo.camera.MyOrientationEventListener;
import cx.mobilechecksh.mvideo.camera.PreferenceGroup;
import cx.mobilechecksh.mvideo.camera.Util;
import cx.mobilechecksh.mvideo.camera.picture.PictureManager;
import cx.mobilechecksh.mvideo.camera.ui.FocusRectangle;


@SuppressLint("NewApi")
public class CameraManager implements CameraControlListener
{
	private final CameraConfigurationManager  		configManager;
	private Camera camera;
	private boolean 								initialized;
	private String TAG="hcxw camera";
	private boolean 								previewing;
	private SensorManager mSensorManager;
	private Sensor mSensor_ACCELEROMETER;
	private Sensor mSensor_LIGHT;
	private static final float 						BLANK = -9999f;  
	private static final int 						ELEMENT_COUNT = 30;
	private int 									sample_position;//取样次数
	private float[] 								samplingX = new float[ELEMENT_COUNT]; //取样
	private float[] 								samplingY = new float[ELEMENT_COUNT];
	private float[] 								samplingZ = new float[ELEMENT_COUNT];
	private CameraMain context;
	private float 									x, y, z;
	private JpgPictureCallback						mPictureCallback;
	private boolean 								mPausing;
	private int									mViewFinderWidth,mViewFinderHeight;
	private int 									mOriginalViewFinderWidth, mOriginalViewFinderHeight;
	private SurfaceView surfaceView;
	private boolean								isCapture = false;
	private boolean								bCaptureMode = false;

	private Point cameraPreviewConfig;
	public static int 							fps=10;
	public static int 							sign;
	public static int							bitrate;
	public static String serverIp;

	public static int							previewWidth,previewHeight;
	//	private Point								previewSize,pictureSize;
	private long 								lastFrameTime = 0;
	private H264Encoder h264Encoder;
	private FocusRectangle mFocusRectangle;
	private	int									mCameraId = 0;

	//对焦状态
	private static final int 				FOCUSING = 1;
	private static final int 				FOCUS_SUCCESS = 3;
	private static final int 				FOCUS_FAIL = 4;
	public static int 						mFocusState = FOCUSING;
	private boolean 						pressed = true;
	public static 	CameraManager			mCameraManager;
	private Parameters mInitialParams;//相机打开时的默认参数
	private boolean							isRecordVideo = true;


	private static final int 				NO_STORAGE_ERROR = -1;
	private static final int 				CANNOT_STAT_ERROR = -2;
	private final int						MESSAGE_RESTART_PREVIEW = 1;
	private final int						MESSAGE_REFRESH = 4;
	private final int						MESSAGE_UPDATE_FOCUS= 13;
	private final int						MESSAGE_CLEAR_FOCUS= 14;
	private final int						MESSAGE_DO_FOCUS_END= 15;
	private int								mPicturesRemaining;

	private Timer t;
	private TimerCheckAutoFocusTask			task;
	private boolean							bTasking = true;

	private boolean 						isTakingPicture = false;
	private final ShutterCallback mShutterCallback = new ShutterCallback();
	private final PostViewPictureCallback mPostViewPictureCallback =
			new PostViewPictureCallback();
	private final RawPictureCallback mRawPictureCallback =
			new RawPictureCallback();

	private long mShutterCallbackTime;
	private long mPostViewPictureCallbackTime;
	private long mRawPictureCallbackTime;
	private long mCaptureStartTime;

	private CaptureActivityHandler handler;
	private HDebug	mDebug;
	private boolean						isCustonContinueMode = false;
	public static  boolean btakpic = false;//拍照动作
	public static final int POST_TIME = 5*1000;
	public static OnNetWorkConnectedChangedListener NetWorkConnectedChangedListener;
	public static OnRSSIChangedListener	RSSIChangedListener;
	public static CameraControlListener controlListener;

	private CameraManager(CameraMain activity,PreferenceGroup group) {
		this.context = activity;
		this.configManager = new CameraConfigurationManager(context,group);
		initSensor();
		mPictureCallback = new JpgPictureCallback();
		installIntentFilter();
		checkStorage();
		addCameraControlListener(this);
		mDebug = HDebug.init(false);
	}

	public static CameraManager initCameraManagerInstance(CameraMain context,PreferenceGroup group){
		if (mCameraManager ==null) {
			mCameraManager = new CameraManager(context,group);
		}
		return mCameraManager;
	}

	public static CameraManager getCameraManagerInstance(){
		return mCameraManager;
	}

	public void setHandler(CaptureActivityHandler handler){
		this.handler = handler;
		//		//测试程序 不断拍照
		//				CameraTest cameraTest = new CameraTest(handler);
		//				cameraTest.startTaskTakePic();
	}

	public void getCameraParameter(PreferenceGroup group){
	
		
		CharSequence[] fpss = group.findPreference(CameraSettings.KEY_FPS_ID).getEntryValues();
		//		CharSequence[] signs = group.findPreference(CameraSettings.KEY_SIGN_ID).getEntryValues();
		CharSequence[] bitrates = group.findPreference(CameraSettings.KEY_BITRATE_ID).getEntryValues();
		CharSequence[] previews = group.findPreference(CameraSettings.KEY_PREVIEW_SIZE).getEntryValues();
		//		CharSequence[] serverIps = group.findPreference(CameraSettings.KEY_SERVERIP_ID).getEntryValues();

		fps = Integer.parseInt(fpss[0].toString());
		//		sign = Integer.parseInt(signs[0].toString());
		//		serverIp = serverIps[0].toString();
		String previewSize = previews[0].toString();
		int index = previews[0].toString().indexOf('x');
		if (index != -1){
			previewWidth = Integer.parseInt(previewSize.substring(0, index));
			previewHeight = Integer.parseInt(previewSize.substring(index + 1));
		}
		bitrate = Integer.parseInt(bitrates[0].toString());

		mDebug.Log_i("xml config fps", fps+"");
		mDebug.Log_i("xml config sign", sign+"");
		mDebug.Log_i("xml config serverIp", serverIp+"");
		mDebug.Log_i("xml config previewWidth", previewWidth+"");
		mDebug.Log_i("xml config previewHeight", previewHeight+"");
		mDebug.Log_i("xml config bitrate", bitrate+"");
	}

	/**
	 * 开启相机
	 * @param holder
	 * @throws IOException
	 */
	public void openDriver(SurfaceHolder holder) throws IOException {
		Camera theCamera = camera;
		if (theCamera == null) {
			theCamera = Camera.open(mCameraId);
			mInitialParams = theCamera.getParameters();
			camera = theCamera;
			camera.setErrorCallback(new ErrorCallback() {

				@Override
				public void onError(int error, Camera camera) {
					Log.e("whhcxw camera error", "camera error"+error);
					if (error == Camera.CAMERA_ERROR_SERVER_DIED) {
						Log.e("whhcxw camera error", "camera server died");
						if(handler != null)
							handler.sendEmptyMessage(MESSAGE_REFRESH);
					}

				}
			});
		}

		if (!initialized) {
			initialized = true;
			configManager.updateCameraParametersPreference(theCamera, isRecordVideo,mCameraId);
		}

		mDebug.Log_i("openDriver()", "openDriver()");

		//获取当前模式是否是自定义持续对焦模式
		isCustonContinueMode = configManager.isCustomContinueMode();
		startTask();

	}
	/**
	 * 开启对焦线程
	 */
	private void startTask(){
		t = new Timer();
		task = new TimerCheckAutoFocusTask();
		//第三个参数指定了第一次调用之后，从第二次开始每隔多长的时间调用一次 run() 方法。

		if (bTasking==false) {
			bTasking = true;
		}
		t.schedule(task,0,1000L * 2L);
		mDebug.Log_i("startTask()", "startTask()");
	}
	/**
	 * 停止对焦
	 */
	private void stopTask(){
		if (isCustonContinueMode) {
			if (t !=null) {
				t.cancel();
				if (camera!=null) {
//					camera.cancelAutoFocus();//在9100上拍照前取消对焦会出现拍照不清，对焦回缩情况
					if (!isTakingPicture) {
						clearFocusState();
					}
					//					mFocusState = FOCUS_FAIL;
				}
				task.cancel();
				bTasking = false;
				mDebug.Log_i("stopTask()", "stopTask()");
			}
		}

	}

	/**
	 * 切换前后摄像头
	 * @param cameraId
	 */
	public void switchCameraId() {
		if (mPausing) return;

		if (Build.VERSION.SDK_INT<9) {
			return;
		}

		if (isCapture) {
			stopCapture();
		}

		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		int cameraCount = Camera.getNumberOfCameras();

		if (cameraCount==1) {
			Toast.makeText(context, "您的设备只支持后置摄像头", Toast.LENGTH_LONG).show();
			return;
		}
		if(handler != null ) handler.setButtonEnable(false);

		for ( int camIdx = 0; camIdx < cameraCount;camIdx++ ) {
			Camera.getCameraInfo( camIdx, cameraInfo );
			if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT&&mCameraId ==1) {
				// 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
				camera.release();
//				camera=null;
				camera= Camera.open( 0 );
				mCameraId = 0;
				mDebug.Log_i("switchCameraId()", "open front camera");
				break;
			}
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK&&mCameraId ==0) {
				camera.release();
//				camera=null;
				camera= Camera.open( 1 );
				mCameraId = 1;
				mDebug.Log_i("switchCameraId()", "open back camera");
				break;
			}
		}

		HDebug.instance().Log_e(TAG, "updete camera parameters before switch cameraId");
		configManager.updateCameraParametersPreference(camera, isRecordVideo,mCameraId);

		try {
			camera.setPreviewDisplay(surfaceView.getHolder());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (isRecordVideo) {
			Capture();
		}
		camera.startPreview();
		if(handler != null ) handler.setButtonEnable(true);
		HDebug.instance().Log_e(TAG, "did all job  after  switch cameraId");

	}

	public void isRecordVideo(boolean isRecordVideo){
		if (camera==null)  return;
		this.isRecordVideo = !isRecordVideo;
		bCaptureMode = this.isRecordVideo;

		if (isCapture) stopCapture();

		HDebug.instance().Log_e(TAG, "updete camera parameters before switch camera model(take pic or capture)");
		configManager.updateCameraParametersPreference(camera, !isRecordVideo,mCameraId);

		if (this.isRecordVideo) {
			camera.stopPreview();
			camera.startPreview();
		}
		HDebug.instance().Log_e(TAG, "did all job switch camera model(take pic or capture)");

	}
	/**
	 * 关闭相机
	 */
	public void closeDriver() {
		if (camera != null) {
			stopPreview();
			camera.release();
			camera = null;
			initialized = false;
			mDebug.Log_i("closeDriver()", "close the camera");
		}
	}


	/**
	 * 切换闪光灯
	 * @param flashButton
	 */
	/**
	 * 切换闪光灯
	 */
	public String swithFlashMode(){
		if (mCameraId==1) {
			return null;
		}
		if (camera !=null) {
			camera.cancelAutoFocus();
		}
		final String flashMode = configManager.setFlashMode(camera);
		mDebug.Log_i("setFlashMode", flashMode);
		return flashMode;


	}



	/**
	 * 切换对焦模式
	 * @param focusBtn
	 */
	public String switchFocusMode(){
		if (mCameraId==1) {
			return null;
		}
		if (camera !=null) {
			camera.cancelAutoFocus();
		}
		return configManager.setFocusMode(camera);
	}



//	TimerTask fpsTimerTask = new TimerTask() {
//
//		@Override
//		public void run() {
//			Log.e("currentFps", currentFps/5+"");
//			currentFps=0;
//		}
//	};
//
//
//	int currentFps =0;
//	Timer timer = null;

	/**
	 * 录像
	 */
	public void Capture(){
//		if(timer==null){
//			timer = new Timer();
//			timer.schedule(fpsTimerTask, 0, 1000L*5);
//		}


		mDebug.Log_i("Capture()", "start capture");
		//此两行代码，在9100上不支持（摄像模式下，会出现卡屏死机现象）
		//		camera.stopPreview();
		//		camera.startPreview();
		cameraPreviewConfig = configManager.getCameraPreivewSize();
		h264Encoder = new H264Encoder(previewWidth,previewHeight,12,bitrate);

		isCapture = true;
		bCaptureMode = true;
		int dataBufferSize = 0;
		if (cameraPreviewConfig !=null) {
			dataBufferSize = cameraPreviewConfig.x * cameraPreviewConfig.y * 3 / 2;
		}else {
			dataBufferSize = previewWidth*previewHeight*3/2;
		}

		camera.addCallbackBuffer(new byte[dataBufferSize]);
		camera.addCallbackBuffer(new byte[dataBufferSize]);
		NetCameraService.getInstance().mNetEncoder.setVideoFormat(previewWidth, previewHeight, 12, bitrate);
		byte[] videoDesc=new byte[h264Encoder.descSize];
		System.arraycopy(h264Encoder.descData,0, videoDesc,0,videoDesc.length);
		NetCameraService.getInstance().mNetEncoder.setVideoDesc(videoDesc.length,videoDesc);

		camera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {

			public synchronized void onPreviewFrame(byte[] data,Camera camera) {
				if (data !=null) {
//					currentFps++;
//					Log.d(TAG, "preview ......");
//										int frameInterval = 1000 / fps;
//										if (System.currentTimeMillis()-lastFrameTime>frameInterval){
					lastFrameTime= System.currentTimeMillis();
					h264Encoder.compressFrame(data);
					if (h264Encoder.h264Buff[0]==0x65){
						NetCameraService.getInstance().mNetEncoder.addFrame(0, h264Encoder.buffSize, h264Encoder.h264Buff);
					} else {
						NetCameraService.getInstance().mNetEncoder.addFrame(1, h264Encoder.buffSize, h264Encoder.h264Buff);
					}
//										}
					camera.addCallbackBuffer(data);
				}

				return;
			}
		});

	}


	/**
	 * 停止采集视频
	 */
	public void stopCapture(){
		if (isCapture) {
			isCapture = false;
			camera.setPreviewCallback(null);
			mDebug.Log_i("Capture()", "stop capture while taking photos"+"isCapture="+isCapture);
		}

	}
	/**
	 * 停止采集视频
	 */
	public void stopCapture(boolean bCaptureMode){
		this.bCaptureMode = bCaptureMode;
		if (isCapture) {
			isCapture = false;
			camera.setPreviewCallback(null);
			mDebug.Log_i("Capture()", "handle stop  capture "+"isCapture="+isCapture+"bCaptureMode="+bCaptureMode);
		}

	}



	/**
	 * 开始预览
	 */
	public void startPreview() {
		Camera theCamera = camera;
		if (theCamera != null && !previewing) {
			theCamera.startPreview();
			previewing = true;
			mPausing = false;
			mDebug.Log_i("startPreview()", "start preview "+"previewing="+previewing+"mPausing="+mPausing);
		}
	}

	/**
	 *停止预览
	 */
	public void stopPreview() {
		if (camera != null && previewing) {
			camera.stopPreview();
			camera.release();
			previewing = false;
			mDebug.Log_i("stopPreview()", "stop preview "+"previewing="+previewing+"mPausing="+mPausing);
		}
	}

	/**
	 * 调用拍照
	 */
	public void requestTakePhoto(){

		beforeTakePhoto();
		updateFocusIndicator();
		mDebug.Log_i("take picture", "mFocusState = "+mFocusState);
		if (camera != null && previewing&&mPicturesRemaining>=0) {

			final Message msg = new Message();
			msg.what = MESSAGE_REFRESH;
			msg.arg1 = CameraHardwareException.ERROR_TAKE_DEFAULT;

			String model = android.os.Build.MODEL ;
			if(model.contains("GT-I9082")){
				mFocusState = FOCUSING;
				new Thread("autoFocusTakingPicture")
				{
					public void run()
					{

						handler.sendMessageDelayed(msg, POST_TIME);
						Thread.currentThread().setDefaultUncaughtExceptionHandler(new CameraHardwareException(context,handler,camera,CameraHardwareException.ERROR_AUTO_FOCUS));
						camera.autoFocus(new AutoFocusCallBack(true, handler));

					};
				}.start();

			}else {
				handler.sendMessageDelayed(msg, POST_TIME);
				takePhoto(handler);
			}

		}

	}

	/**
	 * 拍照
	 */
	long lastTakeTime ;
	long intervalTakeTime = 500;
	private void takePhoto(final CaptureActivityHandler handler){
		mDebug.Log_e("take picture", "taking picture"+"mfocusstate="+mFocusState);
		previewing = false;
		mPictureCallback.setController(handler);
		//此方法只适用于所有子线程.捕获所有未捕获的异常.
		new Thread("takeingPicture")
		{
			public void run()
			{
				//setDefault可针对所有子线程捕获运行时异常
				Thread.currentThread().setDefaultUncaughtExceptionHandler(new CameraHardwareException(context,handler,camera,CameraHardwareException.ERROR_TAKE_PIC));
				camera.takePicture(mShutterCallback, mRawPictureCallback,
						mPostViewPictureCallback, mPictureCallback);

			};
		}.start();

	}

	/**
	 * 拍照前的预处理
	 */
	public void beforeTakePhoto(){
		if (isCapture&&bCaptureMode) {
			stopCapture();
		}
		isTakingPicture = true;
		cancelSensor();
		stopTask();
		checkStorage();
		if(mCameraId==0&&configManager.getCurrentFlashMode()!=null&&configManager.getCurrentFlashMode().equals("auto")
				&&camera.getParameters().getFlashMode()!=null&&camera.getParameters().getFlashMode().equals("off")) {
			Camera.Parameters parameters = camera.getParameters();
			parameters.setFlashMode("auto");
			camera.setParameters(parameters);
		}
		mDebug.Log_i("take picture", "before take picture");
	}

	/**
	 * 拍照后的处理
	 */
	public void restartPreview(){
		mDebug.Log_e("after take picture", "restart preview");
		surfaceView.invalidate();
		setViewFinder(mOriginalViewFinderWidth, mOriginalViewFinderHeight, true,surfaceView);
		//		if (mFocusState == FOCUS_FAIL) {
		//			requestAutoFocus();
		//		}
		//拍照后自动对焦强制开启
		//		if (!bTasking) {
		//			startTask();
		//		}
		isTakingPicture = false;
		initSensor();
		if (bCaptureMode) {
			Capture();
		}
		clearFocusState();
		mFocusState = FOCUS_FAIL;


	}
	/**
	 * 手动触摸对焦
	 * @param pressed
	 */
	public void doFocus(boolean pressed) {
		this.pressed = pressed;
		isTakingPicture = false;
		requestAutoFocus(pressed, handler);

		mDebug.Log_i("dofocus", "do focus ");
	}


	/**
	 * 重置对焦框
	 */
	public void updateFocusIndicator() {
		mDebug.Log_e("updateFocusIndicator", " do job updateFocusIndicator");
		handler.removeMessages(MESSAGE_UPDATE_FOCUS);
		handler.sendEmptyMessage(MESSAGE_UPDATE_FOCUS);

	}

	/**
	 * 清除对焦框
	 */
	private void clearFocusState() {
		handler.sendEmptyMessage(MESSAGE_CLEAR_FOCUS);

	}



	/**
	 *调用相机自动对焦
	 *
	 * @param handler The Handler to notify when the autofocus completes.
	 * @param message The message to deliver.
	 */
	private void requestAutoFocus() {
		if (camera != null && previewing) {
			try {
				mFocusState = FOCUSING;
				HDebug.instance().Log_e("test", "timer task before autofocus");
				camera.autoFocus(new AutoFocusCallBack(false));
			} catch (RuntimeException re) {
				// Have heard RuntimeException reported in Android 4.0.x+; continue?
				Log.w(TAG, "Unexpected exception while focusing", re);
			}
		}
	}

	/**
	 * 调用相机自动对焦
	 * @param pressed
	 * @param handler
	 * @param PictureManager.getPictureManagerInstance()
	 */
	private void requestAutoFocus(boolean pressed,CaptureActivityHandler handler) {
		if (camera != null && previewing) {
			try {
				mFocusState = FOCUSING;
				camera.autoFocus(new AutoFocusCallBack(pressed,handler));
			} catch (RuntimeException re) {
				// Have heard RuntimeException reported in Android 4.0.x+; continue?
				Log.w(TAG, "Unexpected exception while focusing", re);
			}
		}
	}

	/**
	 * 变焦参数设置
	 * @param value
	 */
	public int startZoom(int value){
		if (mCameraId==1) return -1;
		camera.cancelAutoFocus();
		Parameters parameters = camera.getParameters();
		parameters.setZoom(value);
		camera.setParameters(parameters);
		mFocusState = FOCUS_FAIL;
		if (!bTasking&&!btakpic) {
			startTask();
		}

		return camera.getParameters().getZoom();
	}




	class AutoFocusCallBack implements Camera.AutoFocusCallback{
		private CaptureActivityHandler handler;
		public AutoFocusCallBack(boolean pressed){
			btakpic = pressed;
		}
		public AutoFocusCallBack(boolean pressed,CaptureActivityHandler handler){
			btakpic = pressed;
			this.handler = handler;

		}
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if (mFocusState == FOCUSING) {
				if (success) {
					mFocusState = FOCUS_SUCCESS;
				} else {
					mFocusState = FOCUS_FAIL;
				}
			}
			if (btakpic) {
				HDebug.instance().Log_e("test", System.currentTimeMillis()+"");
				takePhoto(handler);

			}
			mDebug.Log_i("onAutoFocus", "mFocusState = "+mFocusState+"pressed = "+btakpic);

			CameraManager.this.camera.getParameters().setFocusMode(Parameters.FOCUS_MODE_AUTO);
			CameraManager.this.camera.cancelAutoFocus();

		}

	}

	/**
	 * 定时检查是否对焦成功，进行对焦
	 * @author Administrator
	 *
	 */
	long lastFocusTime ;
	long intervalTime = 1000*4;
	class TimerCheckAutoFocusTask extends TimerTask {
		public void run() {
			if (isCustonContinueMode) {
				if ((mFocusState == FOCUSING&&pressed ==false)||mFocusState ==FOCUS_FAIL) {
					if (System.currentTimeMillis()-lastFocusTime<intervalTime){
						HDebug.instance().Log_e(TAG, "timer to auto focus return");
						stopTask();
						return;
					}
					HDebug.instance().Log_e(TAG, "timer to auto focus start");
					requestAutoFocus();
					lastFocusTime= System.currentTimeMillis();
				}
			}

		}
	}




	/**
	 * 初始化重力感应
	 */
	private void initSensor() {
		mSensorManager = (SensorManager) context.getSystemService(Service.SENSOR_SERVICE);
		mSensor_ACCELEROMETER = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		// 注册listener，第三个参数是检测的精确度
		mSensorManager.registerListener(mSensorEventListener, mSensor_ACCELEROMETER,
				SensorManager.SENSOR_DELAY_NORMAL);

		//获得光线传感器
		mSensor_LIGHT = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

		mSensorManager.registerListener(mSensorEventListener, mSensor_LIGHT,
				SensorManager.SENSOR_DELAY_NORMAL);

		Arrays.fill(samplingX, BLANK);
		Arrays.fill(samplingY, BLANK);
		Arrays.fill(samplingZ, BLANK);
	}

	/**
	 * 取消传感器
	 */
	private void cancelSensor(){
		if (mSensorManager != null) {
			mSensorManager.unregisterListener(mSensorEventListener, mSensor_ACCELEROMETER);
			mSensorManager.unregisterListener(mSensorEventListener, mSensor_LIGHT);
		}
	}

	/**
	 * 传感器监听
	 */
	private SensorEventListener mSensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {

			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

				float[] value = lowPassFilter(event.values);
				//
				float new_x = value[0];
				float new_y = value[1];
				float new_z = value[2];
				//				float new_x = event.values[SensorManager.DATA_X];
				//				float new_y = event.values[SensorManager.DATA_Y];
				//				float new_z = event.values[SensorManager.DATA_Z];
				//如果手机动了，幅度在defaultData以上，那么就需要重新对焦处理
				float defaultData = 1.0f;
				if (Math.abs(new_x-x)>defaultData|| Math.abs(new_y-y)>defaultData|| Math.abs(new_z-z)>defaultData) {
					mFocusState = FOCUS_FAIL;
					if (!bTasking&&!btakpic) {
						startTask();
					}
					x = new_x;
					y = new_y;
					z = new_z;
				}


				//					Log.d(TAG,"data "+new_x+" "+new_y+" "+new_z);
				//					Log.d(TAG,"filter x y z "+value[0]+" "+value[1]+" "+value[2]);

			}
			if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
				float[] lights = event.values;
				if (lights[0]==0.0&&lights[1]==-1.0&&lights[2]==-1.0&&!bTasking) {
					stopTask();
				}else {
					if (!btakpic&&!bTasking) {
						startTask();
					}

				}
			}



		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};


	/**
	 *  滤波器，过滤掉低抖动的数据
	 * @param inputVal
	 * @return
	 */
	public float[] lowPassFilter(float inputVal[]){
		// High Pass filter
		if (sample_position == ELEMENT_COUNT - 1) {
			sample_position = 0;
		} else {
			sample_position++;
		}
		float x1 = inputVal[0];
		float y2 = inputVal[1];
		float z3 = inputVal[2];

		samplingX[sample_position] = x1;
		samplingY[sample_position] = y2;
		samplingZ[sample_position] = z3;

		float valueX = getMedian(samplingX);
		float valueY = getMedian(samplingY);
		float valueZ = getMedian(samplingZ);

		return new float[]{valueX, valueY, valueZ};
	}

	/**
	 * 获取平均值
	 * @param values
	 * @return
	 */
	private float getMedian(float[] values) {

		float[] tmp = values.clone();

		Arrays.sort(tmp);

		int len = tmp.length;

		int first = 0;
		for (int i = 0; i < tmp.length; i++) {
			first = i;
			if (tmp[i] != BLANK) {
				break;
			}
		}
		return tmp[(len - first) / 2 + first];
	}


	@SuppressWarnings("deprecation")
	public void setViewFinder(int w, int h, boolean startPreview,final SurfaceView surfaceView) {
		mDebug.Log_e("reinit the camera parameters", "when camera open or restart preview ,we should reInit this ");
		if (previewing &&
				w == mViewFinderWidth &&
				h == mViewFinderHeight) {
			return;
		}

		if (camera==null)
			return;

		if (surfaceView.getHolder() == null)
			return;



		// remember view finder size
		mViewFinderWidth = w;
		mViewFinderHeight = h;
		if (mOriginalViewFinderHeight == 0) {
			mOriginalViewFinderWidth = w;
			mOriginalViewFinderHeight = h;
		}

		if (startPreview == false)
			return;

		/*
		 * start the preview if we're asked to...
		 */

		// we want to start the preview and we're previewing already,
		// stop the preview first (this will blank the screen).
		if (previewing)
			stopPreview();

		// this blanks the screen if the surface changed, no-op otherwise
		try {
			this.surfaceView = surfaceView;
			camera.setPreviewDisplay(surfaceView.getHolder());
		} catch (IOException exception) {
			camera.release();
			camera = null;
			// TODO: add more exception handling logic here
			return;
		}

		// request the preview size, the hardware may not honor it,
		// if we depended on it we would have to query the size again

		//	        configManager.initFromCameraParameters(camera);
		//		configManager.resetCameraParameters(camera, pictureSize, previewSize, fps, true);
		configManager.updateCameraParametersPreference(camera, isRecordVideo,mCameraId);

		final long wallTimeStart = SystemClock.elapsedRealtime();
		final long threadTimeStart = Debug.threadCpuTimeNanos();

		final Object watchDogSync = new Object();
		Thread watchDog = new Thread(new Runnable() {
			public void run() {
				int next_warning = 1;
				while (true) {
					try {
						synchronized (watchDogSync) {
							watchDogSync.wait(1000);
						}
					} catch (InterruptedException ex) {
						//
					}
					if (previewing) break;

					int delay = (int) (SystemClock.elapsedRealtime() - wallTimeStart) / 1000;
					if (delay >= next_warning) {

						try {
							if (camera !=null&&surfaceView!=null) {
								camera.setPreviewDisplay(surfaceView.getHolder());
							}
						} catch (IOException exception) {
							camera.release();
							camera = null;
							return;
						}
						if (delay < 120) {
							Log.e(TAG, "preview hasn't started yet in " + delay + " seconds");
						} else {
							Log.e(TAG, "preview hasn't started yet in " + (delay / 60) + " minutes");
						}
						if (next_warning < 60) {
							next_warning <<= 1;
							if (next_warning == 16) {
								next_warning = 15;
							}
						} else {
							next_warning += 60;
						}
					}
				}
			}
		});

		watchDog.start();

		if (Config.LOGV)
			Log.v(TAG, "calling camera.startPreview");
		try {
			startPreview();
		} catch (Throwable e) {
			// TODO: change Throwable to IOException once android.hardware.Camera.startPreview
			// properly declares that it throws IOException.
			e.printStackTrace();
		}
		previewing = true;

		synchronized (watchDogSync) {
			watchDogSync.notify();
		}

		long threadTimeEnd = Debug.threadCpuTimeNanos();
		long wallTimeEnd = SystemClock.elapsedRealtime();
		if ((wallTimeEnd - wallTimeStart) > 3000) {
			Log.w(TAG, "startPreview() to " + (wallTimeEnd - wallTimeStart) + " ms. Thread time was"
					+ (threadTimeEnd - threadTimeStart) / 1000000 + " ms.");
		}

	}

	/**
	 * 释放相机
	 */
	public void release(){
		unregisReceiver();
		mCameraManager = null;
		mPausing = true;
		previewing = false;
		isCapture = false;
		closeDriver();
		stopTask();
		t=null;
//		if(timer!=null) {
//			timer.cancel();
//			timer =null;
//		}
		mDebug.Log_i("release", "release all");
	}


	private void checkStorage() {
		mPicturesRemaining = calculatePicturesRemaining();
		updateStorageHint(mPicturesRemaining);
	}

	private void installIntentFilter() {
		// install an intent filter to receive SD card related events.
		IntentFilter intentFilter =
				new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
		intentFilter.addDataScheme("file");
		context.registerReceiver(mReceiver, intentFilter);

		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		iFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		iFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		iFilter.addDataScheme("file");
		context.registerReceiver(mSDCardMountEventReceiver, iFilter);
	}

	private void unregisReceiver(){
		context.unregisterReceiver(mReceiver);
		context.unregisterReceiver(mSDCardMountEventReceiver);
		cancelSensor();
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_MEDIA_MOUNTED)
					|| action.equals(Intent.ACTION_MEDIA_UNMOUNTED)
					|| action.equals(Intent.ACTION_MEDIA_CHECKING)) {
				checkStorage();
			} else if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
				checkStorage();
			}
		}
	};

	private BroadcastReceiver mSDCardMountEventReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(Intent.ACTION_MEDIA_MOUNTED)
					|| action.equals(Intent.ACTION_MEDIA_UNMOUNTED)
					|| action.equals(Intent.ACTION_MEDIA_REMOVED)){
				Log.d(TAG, action);
				((Activity)CameraManager.this.context).finish();
			}
		}
	};

	private int calculatePicturesRemaining() {
		try {
			if (!Util.hasStorage()) {
				return NO_STORAGE_ERROR;
			} else {
				String storageDirectory =
						Environment.getExternalStorageDirectory().toString();
				StatFs stat = new StatFs(storageDirectory);
				final int PICTURE_BYTES = 0x500000;	/* ddl@rock-chips.com: 5 mega picture jpeg size may be 5MBytes */
				float remaining = ((float) stat.getAvailableBlocks()
						* (float) stat.getBlockSize()) / PICTURE_BYTES;
				return (int) remaining;
			}
		} catch (Exception ex) {
			// if we can't stat the filesystem then we don't know how many
			// pictures are remaining.  it might be zero but just leave it
			// blank since we really don't know.
			Log.e(TAG, "Fail to access sdcard", ex);
			return CANNOT_STAT_ERROR;
		}
	}


	private void updateStorageHint(int remaining) {
		String noStorageText = null;

		if (remaining == NO_STORAGE_ERROR) {
			String state = Environment.getExternalStorageState();
			if (state == Environment.MEDIA_CHECKING) {
				noStorageText = context.getString(R.string.preparing_sd);
			} else {
				noStorageText = context.getString(R.string.no_storage);
			}
		} else if (remaining == CANNOT_STAT_ERROR) {
			noStorageText = context.getString(R.string.access_sd_fail);
		} else if (remaining < 1) {
			noStorageText = context.getString(R.string.not_enough_space);
		}
		if (remaining <0&&noStorageText !=null) {
			Toast.makeText(context, noStorageText, Toast.LENGTH_LONG).show();
			((Activity)context).finish();
		}


	}





	private final class ShutterCallback
	implements android.hardware.Camera.ShutterCallback {
		public void onShutter() {
			mShutterCallbackTime = System.currentTimeMillis();
			long mShutterLag = mShutterCallbackTime - mCaptureStartTime;
			Log.v(TAG, "mShutterLag = " + mShutterLag + "ms");

		}
	}

	private final class PostViewPictureCallback implements PictureCallback {
		public void onPictureTaken(
				byte [] data, android.hardware.Camera camera) {
			mPostViewPictureCallbackTime = System.currentTimeMillis();
			Log.v(TAG, "mShutterToPostViewCallbackTime = "
					+ (mPostViewPictureCallbackTime - mShutterCallbackTime)
					+ "ms");
		}
	}

	private final class RawPictureCallback implements PictureCallback {
		public void onPictureTaken(
				byte [] rawData, android.hardware.Camera camera) {
			mRawPictureCallbackTime = System.currentTimeMillis();
			Log.v(TAG, "mShutterToRawCallbackTime = "
					+ (mRawPictureCallbackTime - mShutterCallbackTime) + "ms");
		}
	}



	private final class JpgPictureCallback implements android.hardware.Camera.PictureCallback {

		private CaptureActivityHandler handler;
		private long mPictureDisplayedToJpegCallbackTime;
		private final int				MESSAGE_RESTART_PREVIEW = 1;

		public void setController(CaptureActivityHandler handler){
			this.handler = handler;
			
		}

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			long mJpegPictureCallbackTime = System.currentTimeMillis();
			// If postview callback has arrived, the captured image is displayed
			// in postview callback. If not, the captured image is displayed in
			// raw picture callback.
			if (mPostViewPictureCallbackTime != 0) {
				long mShutterToPictureDisplayedTime =
						mPostViewPictureCallbackTime - mShutterCallbackTime;
				mPictureDisplayedToJpegCallbackTime =
						mJpegPictureCallbackTime - mPostViewPictureCallbackTime;
			} else {
				long mShutterToPictureDisplayedTime =
						mRawPictureCallbackTime - mShutterCallbackTime;
				mPictureDisplayedToJpegCallbackTime =
						mJpegPictureCallbackTime - mRawPictureCallbackTime;
			}
			Log.v("com.whhcxw.camera", "mPictureDisplayedToJpegCallbackTime = "
					+ mPictureDisplayedToJpegCallbackTime + "ms");

			// We want to show the taken picture for a while, so we wait
			// for at least 1.2 second before restarting the preview.
			long delay = 1200 - mPictureDisplayedToJpegCallbackTime;
			//			if (delay < 0) {
			handler.sendEmptyMessage(MESSAGE_RESTART_PREVIEW);
			btakpic = false;
			//			} else {
			//				handler.sendEmptyMessageDelayed(MESSAGE_RESTART_PREVIEW, delay);
			//			}
			//save photos
			mDebug.Log_e("Saving picture", "the progress is saving pic");	
			long dateTaken = System.currentTimeMillis();
			PictureManager.getPictureManagerInstance().storage(data,handler,dateTaken, MyOrientationEventListener.mOrientationCompensation);

		}

	}
	
	
	public interface OnNetWorkConnectedChangedListener{
		void onNetWorkConnected();
		void onNetWorkDisconnected();
	}
	
	public void addOnNetWorkConnectedChangedListener(OnNetWorkConnectedChangedListener NetWorkConnectedChangedListener){
		this.NetWorkConnectedChangedListener = NetWorkConnectedChangedListener;
	}
	
	
	public interface OnRSSIChangedListener{
		void onRSSIChanged();
	}
	
	public void addOnRSSIChangedListener(OnRSSIChangedListener RSSIChangedListener){
		this.RSSIChangedListener = RSSIChangedListener;
	}
	
	public void addCameraControlListener(CameraControlListener controlListener){
		this.controlListener = controlListener;
	}
	@Override
	public void switchCamera() {
		// TODO Auto-generated method stub
		switchCameraId();
	}

	@Override
	public String setFlashMode() {
		// TODO Auto-generated method stub
		return swithFlashMode();
	}

	@Override
	public void showThumPhoto() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void takePhoto() {
		requestTakePhoto();
		
	}

	@Override
	public void dofocus() {
		doFocus(false);
		
	}

	@Override
	public String setFocusMode() {
		// TODO Auto-generated method stub
		return switchFocusMode();
	}

	@Override
	public void switchCaptureMode(boolean bCapture) {
		isRecordVideo(bCapture);
		if (!bCapture) {
			Capture();
		}else {
			stopCapture(false);
		}
		
	}

	@Override
	public int setZoom(int value) {
		// TODO Auto-generated method stub
		return startZoom(value);
	}

	@Override
	public HashMap<CharSequence, CharSequence> getSenceModes() {
		if(mCameraId==1) return null;
		return configManager.getSupportedSceneModes(camera);
	}

	@Override
	public void setSenceMode(String value) {
		if (mCameraId==1) return;
		camera.cancelAutoFocus();
		configManager.setSceneMode(camera, value);
		
	}
	
	public String getSenceMode(){
		if(mCameraId == 1) return null;
		return camera.getParameters().getSceneMode();
	}
	
	

}
