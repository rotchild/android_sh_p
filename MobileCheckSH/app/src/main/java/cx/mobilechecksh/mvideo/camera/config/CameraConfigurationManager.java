package cx.mobilechecksh.mvideo.camera.config;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import cx.mobilechecksh.mvideo.camera.ListPreference;
import cx.mobilechecksh.mvideo.camera.PreferenceGroup;

public class CameraConfigurationManager
{
	private Context context;
	private final static String TAG = "hcxw camera";
	private Point screenResolution;
	private Point cameraResolution;
	private static Point cameraPictureSize;
	private String focusMode ,currentFlashMode="off",currentFocusMode = "auto";
	private static final int 	STANDARD_PICTURR_SIZES = 640 * 480; // 默认照片大小
	private static final Point STANDARD_PREVIEW_SIZES1 = new Point(352,288);
	private static final Point STANDARD_PREVIEW_SIZES2 = new Point(640,480);
	private   int 				fps ;
	private static PreferenceGroup group;
	private boolean				isCustomContinueMode = true;
	private int 				origFps;
	private boolean				bInit = false;
	CameraConfigurationManager(Context context, PreferenceGroup group) {
		this.context = context;
		this.group = group;
	}
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@SuppressLint("NewApi")
	public void updateCameraParametersPreference(Camera camera, boolean isRecordVideo, int cameraId) {
		if(camera == null) return;
		Parameters mParameters = camera.getParameters();
		// Set picture size.
		CharSequence[] pictureSizes = group.findPreference(CameraSettings.KEY_PICTURE_SIZE).getEntryValues();
		String pictureSize = pictureSizes[0].toString();
		if (pictureSize == null) {
			CameraSettings.initialCameraPictureSize(context, mParameters);
		} else {
			List<Size> supported = mParameters.getSupportedPictureSizes();
			boolean b = CameraSettings.setCameraPictureSize(
					pictureSize, supported, mParameters);
			//			boolean b = CameraSettings.setCameraPictureSize(
			//					"1024x768", supported, mParameters);
			if (!b) {
				Point p = 	findBestPictureSize(mParameters);
				mParameters.setPictureSize(p.x, p.y);
			}
		}

		Size size = mParameters.getPictureSize();

		cameraPictureSize = new Point(size.width, size.height);



		//如果是在录像
		//三星I9082 不支持352*288格式
		//SAMSUNG SM-C101 不支持352*288
		if (isRecordVideo) {
			//			if (CameraManager.previewWidth>0&&CameraManager.previewHeight>0) {
			if (isSupported(new Point(CameraManager.previewWidth, CameraManager.previewHeight), mParameters.getSupportedPreviewSizes())) {
				mParameters.setPreviewSize(CameraManager.previewWidth, CameraManager.previewHeight);
			}else if(isSupported(STANDARD_PREVIEW_SIZES1, mParameters.getSupportedPreviewSizes())){
				mParameters.setPreviewSize(STANDARD_PREVIEW_SIZES1.x, STANDARD_PREVIEW_SIZES1.y);

			}else if(isSupported(STANDARD_PREVIEW_SIZES2, mParameters.getSupportedPreviewSizes())){
				mParameters.setPreviewSize(STANDARD_PREVIEW_SIZES2.x, STANDARD_PREVIEW_SIZES2.y);

			}else {
				Point bestPreviewSize = getBestSize(mParameters.getSupportedPreviewSizes(), new Point(CameraManager.previewWidth, CameraManager.previewHeight));
				mParameters.setPreviewSize(bestPreviewSize.x, bestPreviewSize.y);

			}

			//			}else {
			//				CharSequence[] previewSizes = group.findPreference(CameraSettings.KEY_PREVIEW_SIZE).getEntryValues();
			//				String previewSize = previewSizes[0].toString();
			//				CameraSettings.setCameraPreviewSize(previewSize, mParameters.getSupportedPreviewSizes(), mParameters);
			//			}


		}else {
			// Set a preview size that is closest to the viewfinder height and has
			// the right aspect ratio.
			List<Size> sizes = mParameters.getSupportedPreviewSizes();
			Size optimalSize = getOptimalPreviewSize(
					sizes, (double) size.width / size.height);
			if (optimalSize != null) {
				Size original = mParameters.getPreviewSize();
				if (!original.equals(optimalSize)) {
					mParameters.setPreviewSize(optimalSize.width, optimalSize.height);

				}
			}

		}
		cameraResolution =new Point(mParameters.getPreviewSize().width,mParameters.getPreviewSize().height);
		CameraManager.previewWidth = mParameters.getPreviewSize().width;
		CameraManager.previewHeight = mParameters.getPreviewSize().height;

		if (cameraId==0) {
			// Set flash mode.
			CharSequence[] flashModes = group.findPreference(CameraSettings.KEY_FLASH_MODE).getEntryValues();
			String flashMode = flashModes[0].toString();
			List<String> supportedFlash = mParameters.getSupportedFlashModes();
			if (isSupported(flashMode, supportedFlash)) {
				if (currentFlashMode.equals(flashMode)) {
					if (!currentFlashMode.equals("auto")) {
						mParameters.setFlashMode(currentFlashMode);
					}else {
						mParameters.setFlashMode("off");
					}
				}else {
					if (!currentFlashMode.equals("auto")) {
						mParameters.setFlashMode(currentFlashMode);
					}else {
						mParameters.setFlashMode("off");
					}
				}

			} else {
				flashMode = mParameters.getFlashMode();
				if (flashMode == null) {
					flashMode = "no flash";
				}
			}

		}


		// Set focus mode.
		CharSequence[] mFocusModes = group.findPreference(CameraSettings.KEY_FOCUS_MODE).getEntryValues();
		String mFocusMode = mFocusModes[0].toString();
		//		if (isSupported("continuous-video", mParameters.getSupportedFocusModes())&&isRecordVideo) {
		//			mParameters.setFocusMode("continuous-video");
		//			mFocusMode = new String("continuous-video");
		//			isCustomContinueMode = false;
		//		} else 
		//			if (isSupported(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE, mParameters.getSupportedFocusModes())) {
		//			mParameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		//			mFocusMode = new String(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		//			isCustomContinueMode = false; 
		//		} else
		if (isSupported(mFocusMode, mParameters.getSupportedFocusModes())) {
			if (currentFocusMode.equals(mFocusMode)) {
				mParameters.setFocusMode(mFocusMode);
			}

			isCustomContinueMode = true;
		}else {
			isCustomContinueMode = true;
			mFocusMode = mParameters.getFocusMode();
			if (mFocusMode == null) {
				mFocusMode = Parameters.FOCUS_MODE_AUTO;

			}
		}
		currentFocusMode = new String(mParameters.getFocusMode());

		if (isRecordVideo) {
			// Set fps
			int fps = 10;
			if (CameraManager.fps>0) {
				fps = CameraManager.fps;
			}else {
				CharSequence[] fpss = group.findPreference(CameraSettings.KEY_FPS_ID).getEntryValues();
				fps = Integer.parseInt(fpss[0].toString());
			}
			//9100 [33, 30, 25, 24, 20, 15, 10]  ZOOM 15 30
			//XT910 最小设置是15，不支持10
			if (-1 != findSettableValue(mParameters.getSupportedPreviewFrameRates(), fps)) {
				mParameters.setPreviewFrameRate(fps);
			} else {

				if (android.os.Build.MODEL.contains("XT910")) {
					mParameters.setPreviewFrameRate(15);
				}else {
					//找出
					int targetFps = binarysearchKey(mParameters.getSupportedPreviewFrameRates().toArray(), 12);
					mParameters.setPreviewFrameRate(targetFps);
					Log.e(context.getPackageName(), "set fps fail,the transport fps is not Supported,we set the fps is "+targetFps);
				}


			}
		}else{
			if (!bInit) {
				bInit = true;
				origFps = mParameters.getPreviewFrameRate();
			}
			if(origFps!=0) mParameters.setPreviewFrameRate(origFps);

		}

		//曝光锁定检测

		if(mParameters.isAutoExposureLockSupported()) mParameters.setAutoExposureLock(false);
		if(mParameters.isAutoWhiteBalanceLockSupported()) mParameters.setAutoWhiteBalanceLock(false);



		camera.setParameters(mParameters);
		mParameters = camera.getParameters();

		//		camera.startPreview();
		HDebug.instance().Log_i("set camera parameters ", "flash mode:"+mParameters.getFlashMode()+
				";PictureSize:"+mParameters.getPictureSize().width+"+"+mParameters.getPictureSize().height
				+";previewSize:"+mParameters.getPreviewSize().width+"+"+mParameters.getPreviewSize().height+";fps:"+mParameters.getPreviewFrameRate()+";focuseMode:"+mParameters.getFocusMode());

	}


	/**
	 * 获取支持的SceneModes
	 * @param camera
	 * @return
	 */
	public static HashMap<CharSequence, CharSequence> getSupportedSceneModes(Camera camera){
		 ListPreference scenes = group.findPreference(CameraSettings.KEY_SCENE_MODE);
		 scenes.filterUnsupported(camera.getParameters().getSupportedSceneModes());
		 CharSequence[] tem = scenes.getEntries();
		 CharSequence[] temValue = scenes.getEntryValues();
		 HashMap<CharSequence, CharSequence> map = new HashMap<CharSequence, CharSequence>();
			for (int i = 0; i < tem.length; i++) {
				map.put(temValue[i], tem[i]);
			}

			return map;
	}

	public static void setSceneMode(Camera camera, String mode){
		if (isSupported(mode, camera.getParameters().getSupportedSceneModes())){
			Camera.Parameters mParameters = camera.getParameters();
			  if (!mParameters.getSceneMode().equals(mode)) {
	                mParameters.setSceneMode(mode);
	                camera.setParameters(mParameters);
			  }

		}


	}


	private static boolean isSupported(String value, List<String> supported) {
		return supported == null ? false : supported.indexOf(value) >= 0;
	}

	private static boolean isSupported(Point value, List<Size> supported ){
		if (supported==null) {
			return false;
		}
		for (int i = 0; i < supported.size(); i++) {
			if (supported.get(i).width==value.x&&supported.get(i).height==value.y) {
				return true;
			}
		}
		return false;
	}

	private static boolean isSupported(Integer value, List<Integer> supported ){
		if (supported==null) {
			return false;
		}
		for (int i = 0; i < supported.size(); i++) {
			if (supported.get(i)==value) {
				return true;
			}
		}
		return false;
	}

	private Size getOptimalPreviewSize(List<Size> sizes, double targetRatio) {
		final double ASPECT_TOLERANCE = 0.05;
		if (sizes == null) return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		// Because of bugs of overlay and layout, we sometimes will try to
		// layout the viewfinder in the portrait orientation and thus get the
		// wrong size of mSurfaceView. When we change the preview size, the
		// new overlay will be created before the old one closed, which causes
		// an exception. For now, just get the screen size

		Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
		int targetHeight = Math.min(display.getHeight(), display.getWidth());

		if (targetHeight <= 0) {
			// We don't know the size of SurefaceView, use screen height
			WindowManager windowManager = (WindowManager)
					context.getSystemService(Context.WINDOW_SERVICE);
			targetHeight = windowManager.getDefaultDisplay().getHeight();
		}

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			Log.v(TAG, "No preview size match the aspect ratio");
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}
	public String setFlashMode(Camera camera){
		Camera.Parameters mParameters = camera.getParameters();
		List<String> supportedFlash = mParameters.getSupportedFlashModes();
		if (supportedFlash != null) {
			if (supportedFlash.contains(Camera.Parameters.FLASH_MODE_AUTO)&&supportedFlash.contains(Camera.Parameters.FLASH_MODE_ON)&&supportedFlash.contains(Camera.Parameters.FLASH_MODE_OFF)&&supportedFlash.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
				if (currentFlashMode.equals(Camera.Parameters.FLASH_MODE_OFF)) {
					mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
				}
				else if (currentFlashMode.equals(Camera.Parameters.FLASH_MODE_TORCH)) {
					mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
				}
				else if (currentFlashMode.equals(Camera.Parameters.FLASH_MODE_AUTO)) {
					mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				}
			}else if (supportedFlash.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
				mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
			}else if (supportedFlash.contains(Camera.Parameters.FLASH_MODE_ON)&&supportedFlash.contains(Camera.Parameters.FLASH_MODE_OFF)) {
				if (currentFlashMode.equals(Camera.Parameters.FLASH_MODE_OFF)) {
					mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
				}
				else if (currentFlashMode.equals(Camera.Parameters.FLASH_MODE_ON)) {
					mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				}
			}
			camera.setParameters(mParameters);
			currentFlashMode = new String(mParameters.getFlashMode());
		}

		return currentFlashMode;
	}

	public String setFocusMode(Camera camera){
		Camera.Parameters mParameters = camera.getParameters();
		List<String> supportedFocus = mParameters.getSupportedFocusModes();
		if (supportedFocus != null) {
			if (supportedFocus.contains(Parameters.FOCUS_MODE_AUTO)&&supportedFocus.contains(Parameters.FOCUS_MODE_MACRO)) {
				if (currentFocusMode.equals(Parameters.FOCUS_MODE_AUTO)) {
					mParameters.setFocusMode(Parameters.FOCUS_MODE_MACRO);
				}else if (currentFocusMode.equals(Parameters.FOCUS_MODE_MACRO)) {
					mParameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
				}
				camera.setParameters(mParameters);
				currentFocusMode = new String(mParameters.getFocusMode());
			}
		}
		return currentFocusMode;
	}






	/**
	 * 查找相机是否有支持的特定格式
	 * @param supportedValues
	 * @param desiredValues
	 * @return
	 */
	private static int findSettableValue(Collection<Integer> supportedValues,
			int... desiredValues) {
		//		Log.i(TAG, "Supported values: " + supportedValues);
		int result = -1;
		if (supportedValues != null) {
			for (int desiredValue : desiredValues) {
				if (supportedValues.contains(desiredValue)) {
					result = desiredValue;
					break;
				}
			}
		}
		//		Log.i(TAG, "Settable value: " + result);
		return result;
	}




	/** *
	 * 查找最接近目标值的数，并返回
	 * * @param array
	 * * @param targetNum
	 * * @return
	 *
	 * */
	public static Integer binarysearchKey(Object[] array, int targetNum)
	{
		Arrays.sort(array);
		int targetindex = 0;
		int left = 0, right = 0;
		for (right = array.length - 1; left != right;) {
			int midIndex = (right + left) / 2;
			int mid = (right - left);
			int midValue = (Integer) array[midIndex];
			if (targetNum == midValue) {
				return midIndex;
			}
			if (targetNum > midValue) {
				left = midIndex;
			} else {
				right = midIndex;
			}  if (mid <= 2) {
				break;
			}
		}
		//		System.out.println("和要查找的数：" + targetNum + "最接近的数：" + array[targetindex]);
		//		return (Integer) (((Integer) array[right] - (Integer) array[left]) / 2 > targetNum ? array[right] : array[left]);
		return (Integer) array[targetindex];
	}
	/** *
	 * 查找最接近目标值的数，并返回
	 * * @param array
	 * * @param targetNum
	 * * @return
	 *
	 * */
	public static Integer binarysearchKeyIndex(Object[] array, int targetNum)
	{
		Arrays.sort(array);
		int targetindex = 0;
		int left = 0, right = 0;
		for (right = array.length - 1; left != right;) {
			int midIndex = (right + left) / 2;
			int mid = (right - left);
			int midValue = (Integer) array[midIndex];
			if (targetNum == midValue) {
				return midIndex;
			}
			if (targetNum > midValue) {
				left = midIndex;
			} else {
				right = midIndex;
			}  if (mid <= 2) {
				break;
			}
		}
		//		System.out.println("和要查找的数：" + targetNum + "最接近的数：" + array[targetindex]);
		//		return (Integer) (((Integer) array[right] - (Integer) array[left]) / 2 > targetNum ? array[right] : array[left]);
		return (Integer) targetindex;
	}


	public static Point getBestSize(List<Size> supported, Point targetSize){
		int size = supported.size();
		Integer[] sizesWidth = new Integer[size];
		Integer[] sizesHight = new Integer[size];
		for (int i = 0; i < supported.size(); i++) {
			sizesWidth[i] = supported.get(i).width;
			sizesHight[i] = supported.get(i).height;
		}

		int index = binarysearchKeyIndex(sizesWidth, targetSize.x);
		int index2 = binarysearchKeyIndex(sizesHight, targetSize.y);

		//		return index>=0 ? new Point(sizesWidth[index], sizesHight[index]):null;
		return index2>=0 ? new Point(sizesWidth[index2], sizesHight[index2]):null;
	}


	/**
	 * 获取最佳的照片大小，默认是640*480，若不支持，则取最接近的稍大图片
	 * @param parameters
	 * @return
	 */
	private static Point findBestPictureSize(Camera.Parameters parameters){
		Point bestSize = null;
		int pictureSizes = -1;
		Size supportedPictureSize = null;
		List<Size> arrSupportedPictureSizes = parameters.getSupportedPictureSizes();
		for (int i = 0; i < arrSupportedPictureSizes.size(); i++) {
			supportedPictureSize = parameters.getSupportedPictureSizes().get(i);
			pictureSizes = supportedPictureSize.height*supportedPictureSize.width;

			if (pictureSizes==STANDARD_PICTURR_SIZES) {
				//首选（640，480）
				bestSize = new Point(supportedPictureSize.width, supportedPictureSize.height);
				break;
			}else if (arrSupportedPictureSizes.get(0).width<arrSupportedPictureSizes.get(1).width&&(supportedPictureSize.width-640)>0) {
				//如果是按照从小到大的顺序排列
				bestSize = new Point(supportedPictureSize.width, supportedPictureSize.height);
				break;
			}else if (arrSupportedPictureSizes.get(0).width>arrSupportedPictureSizes.get(1).width&&(supportedPictureSize.width-640)<0) {
				//如果是按照从大到小的顺序排列
				Size size =arrSupportedPictureSizes.get(i+1);
				bestSize = new Point(size.width, size.height);
				break;
			}
		}
		return bestSize;
	}


	Point getScreenResolution() {
		return screenResolution;
	}
	Point getCameraPreivewSize() {
		return cameraResolution;
	}

	public static Point getPictureSize(){
		return cameraPictureSize;
	}

	int getFPS(){
		return fps;
	}

	public String getCurrentFlashMode(){
		return currentFlashMode;
	}

	public boolean isCustomContinueMode(){
		return isCustomContinueMode;
	}

}

