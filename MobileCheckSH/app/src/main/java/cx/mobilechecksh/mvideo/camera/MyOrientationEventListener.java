package cx.mobilechecksh.mvideo.camera;


import android.app.Activity;
import android.content.Context;
import android.view.OrientationEventListener;

import cx.mobilechecksh.R;
import cx.mobilechecksh.mvideo.camera.ui.RotateImageView;


public class MyOrientationEventListener extends OrientationEventListener {

	//旋转
	private int 					mOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;
	public static int 					mOrientationCompensation = 0;
	private static CameraMain					context;
	public static  MyOrientationEventListener mOrientationListener;
	private MyOrientationEventListener(Context context) {
		super(context);
	}
	public static MyOrientationEventListener initInstance(CameraMain activity){
		if (mOrientationListener ==null) {
			mOrientationListener = new MyOrientationEventListener(activity);
			mOrientationListener.enable();
		}
		context = activity;
		return mOrientationListener;
	}

	@Override
	public void onOrientationChanged(int orientation) {
		// We keep the last known orientation. So if the user first orient
		// the camera then point the camera to floor or sky, we still have
		// the correct orientation.
		if (orientation == ORIENTATION_UNKNOWN) return;
		mOrientation = roundOrientation(orientation);
		// When the screen is unlocked, display rotation may change. Always
		// calculate the up-to-date orientationCompensation.
		int orientationCompensation = mOrientation
				+ Util.getDisplayRotation((Activity)context);
		if (mOrientationCompensation != orientationCompensation) {
			mOrientationCompensation = orientationCompensation;
			setOrientationIndicator(mOrientationCompensation);
		}
	}
	
	
	public static int roundOrientation(int orientation) {
		return ((orientation + 45) / 90 * 90) % 360;
	}
	
	private void setOrientationIndicator(int degree) {
		((RotateImageView) context.findViewById(
				R.id.review_thumbnail)).setDegree(degree);
		((RotateImageView) context.findViewById(
				R.id.camera_switch_icon)).setDegree(degree);
		((RotateImageView) context.findViewById(
				R.id.video_switch_icon)).setDegree(degree);
		((RotateImageView) context.findViewById(
				R.id.shutter_button)).setDegree(degree);
		((RotateImageView) context.findViewById(
				R.id.camera_wifi)).setDegree(degree);
//		((RotateImageView) context.findViewById(
//				R.id.camera_flash_switch)).setDegree(degree);
		((RotateImageView) context.findViewById(
				R.id.camera_focus_switch)).setDegree(degree);
		((RotateImageView) context.findViewById(
				R.id.camera_b2f_switch)).setDegree(degree);
		((RotateImageView) context.findViewById(
				R.id.camera_setting)).setDegree(degree);
//		((RotateImageView) context.findViewById(
//				R.id.camera_sence)).setDegree(degree);
	}

}
