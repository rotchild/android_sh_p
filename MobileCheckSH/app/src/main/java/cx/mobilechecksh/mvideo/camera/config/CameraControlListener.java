package cx.mobilechecksh.mvideo.camera.config;

import java.util.HashMap;

public interface CameraControlListener {

	/**
	 * 前后摄像头切换
	 */
	public void switchCamera();
	/**
	 * 设置闪光灯
	 */
	public String setFlashMode();
	/**
	 * 查看缩略图
	 */
	public void showThumPhoto();
	/**
	 * 拍照
	 */
	public void takePhoto();
	/**
	 * 手动对焦
	 */
	public void dofocus();
	/**
	 * 切换对焦模式
	 */
	public String setFocusMode();
	/**
	 * 切换录像模式
	 */
	public void switchCaptureMode(boolean bCapture);
	/**
	 * 设置缩放
	 * @param value
	 */
	public int setZoom(int value);
	
	/**
	 * 获取情景模式
	 * @return
	 */
	public HashMap<CharSequence, CharSequence> getSenceModes();
	
	/**
	 * 
	 */
	public void setSenceMode(String value);
}
