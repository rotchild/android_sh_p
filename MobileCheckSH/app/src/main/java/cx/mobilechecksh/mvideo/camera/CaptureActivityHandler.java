package cx.mobilechecksh.mvideo.camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import cx.mobilechecksh.R;
import cx.mobilechecksh.mvideo.camera.config.CameraManager;
import cx.mobilechecksh.mvideo.camera.picture.PictureManager;


public class CaptureActivityHandler extends Handler implements OnKeyListener {

	private CameraMain 				activity;
	private CameraManager cameraManager;
	private final int				MESSAGE_RESTART_PREVIEW = 1;
	private final int				MESSAGE_SAVE_PIC_FAIL = 2;
	private final int				MESSAGE_TAKE_PIC = 3;
	private final int				MESSAGE_REFRESH = 4;
	private final int				MESSAGE_PREIVEW_PICTURE= 5;
	private final int				MESSAGE_START_RECORD= 6;
	private final int				MESSAGE_DO_FOCUS= 7;
	private final int				MESSAGE_STOP_RECORD= 8;
	private final int				MESSAGE_SWITCH_CAMERA= 9;
	private final int				CLEAR_SCREEN_DELAY= 10;
	private final int				MESSAGE_UPDATE_THUM= 11;
	private final int				MESSAGE_SHOW_THUM= 12;
	private final int				MESSAGE_UPDATE_FOCUS= 13;
	private final int				MESSAGE_CLEAR_FOCUS= 14;
	private final int				MESSAGE_DO_FOCUS_END= 15;
	private final int 				MESSAGE_ZOOM = 16;
	private String currentPath;
	private AlertDialog.Builder 	builder;
	private boolean					isPromot;
	private boolean					isPause;
	public CaptureActivityHandler(CameraMain activity,CameraManager cameraManager){
		this.activity = activity;
		this.cameraManager = cameraManager;
		isPromot = false;
	}


	@SuppressWarnings("static-access")
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case MESSAGE_RESTART_PREVIEW:
			Log.e("RECIEVE MESSAGES ", "MESSAGE_RESTART_PREVIEW");
			removeAllMessage(MESSAGE_REFRESH);
			removeAllMessage(MESSAGE_RESTART_PREVIEW);
			activity.setButtonEnable(true);
			activity.takingPicture(false);
			cameraManager.restartPreview();
			break;
		case MESSAGE_UPDATE_THUM:
			currentPath = new String(msg.obj.toString());
			activity.updatePreviewImage(PictureManager.getPictureManagerInstance().getThumPicture());
			break;
		case MESSAGE_SAVE_PIC_FAIL:
			isPromot = true;
			activity.takingPicture(false);
			builder = new AlertDialog.Builder(activity);
			builder.setTitle(R.string.dialog_title);
			builder.setMessage(activity.getResources().getString(R.string.camera_savepic_failer));
			builder.setPositiveButton(R.string.dialog_positive, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					activity.finish();
					dialog.dismiss();
					isPromot = false;

				}
			});
			AlertDialog alertDialog = builder.create();
			alertDialog.setCancelable(false);
			alertDialog.setOnKeyListener(this);
			alertDialog.show();
			break;
		case MESSAGE_TAKE_PIC:
			removeAllMessage(MESSAGE_TAKE_PIC);
			activity.takingPicture(true);
			activity.clearFocusState();
			cameraManager.requestTakePhoto();
			break;
		case MESSAGE_REFRESH:
			Log.e("RECIEVE MESSAGES ", "MESSAGE_REFRESH");
			
			if(isPromot == true) return;
			if(isPause) return;
			
			removeMessages(MESSAGE_RESTART_PREVIEW);
			removeMessages(MESSAGE_REFRESH);
			builder = new AlertDialog.Builder(activity);
			builder.setTitle(R.string.dialog_title);
			builder.setMessage(activity.getResources().getString(R.string.camera_error_taking_pic));
			builder.setPositiveButton(R.string.dialog_positive, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					isPromot=false;
					dialog.dismiss();
					activity.refresh();

				}
			});
			AlertDialog alertDialog2 = builder.create();
			alertDialog2.setCancelable(false);
			alertDialog2.setOnKeyListener(this);
			alertDialog2.show();
			
			break;
		case MESSAGE_PREIVEW_PICTURE:
			break;
		case MESSAGE_START_RECORD:
			removeAllMessage(MESSAGE_START_RECORD);
			cameraManager.Capture();
			break;
		case MESSAGE_STOP_RECORD:
			removeAllMessage(MESSAGE_STOP_RECORD);
			cameraManager.stopCapture(false);
			break;
		case MESSAGE_DO_FOCUS:
			removeAllMessage(MESSAGE_DO_FOCUS);
			cameraManager.doFocus(false);
			break;
		case MESSAGE_SWITCH_CAMERA:
			removeAllMessage(MESSAGE_SWITCH_CAMERA);
			cameraManager.switchCameraId();
			break;
		case CLEAR_SCREEN_DELAY: {
			activity.getWindow().clearFlags(
			WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			break;
		}
		case MESSAGE_SHOW_THUM:
			removeAllMessage(MESSAGE_SHOW_THUM);
			PictureManager.getPictureManagerInstance().showPictureCurrent(activity,currentPath);
			break;

		case MESSAGE_UPDATE_FOCUS:
			removeAllMessage(MESSAGE_UPDATE_FOCUS);
			activity.updateFocusIndicator();
			break;
		case MESSAGE_CLEAR_FOCUS:
			removeAllMessage(MESSAGE_CLEAR_FOCUS);
			activity.clearFocusState();
			break;
		case MESSAGE_DO_FOCUS_END:
			activity.setButtonEnable(true);
			removeAllMessage(MESSAGE_DO_FOCUS_END);
			activity.clearFocusState();
			break;
		case MESSAGE_ZOOM:
			if(cameraManager.setZoom(msg.arg1)==msg.arg1) 
				activity.displayZoom(msg.arg1);
			
			break;
		default:
			break;
		}
	}


	public void removeAllMessage(int what){
		removeMessages(what);
		removeMessages(MESSAGE_UPDATE_FOCUS);
		removeMessages(MESSAGE_CLEAR_FOCUS);
		activity.clearFocusState();
	}


	public void setButtonEnable(boolean enable){
		activity.setButtonEnable(enable);
	}


	/**
	 * 处理activity暂停状态
	 */
	public void quitSynchronously() {
		isPause=true;
		cameraManager.stopPreview();
		
	}
	
	public void resumeSynchronously(){
		isPause=false;
		cameraManager.startPreview();
	}
	
	
	public String getCurrentPicture(){
		return currentPath;
	}


	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
			 return true;
		if(keyCode == KeyEvent.KEYCODE_SEARCH)
			return true;

		return false;
	}
}
