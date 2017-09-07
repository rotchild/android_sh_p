package cx.mobilechecksh.mvideo.camera;



import java.util.Timer;
import java.util.TimerTask;

import cx.mobilechecksh.mvideo.camera.config.CameraManager;
import cx.mobilechecksh.mvideo.camera.picture.PictureManager;


public class CameraTest {
	private static CaptureActivityHandler hander;
	private final static int				MESSAGE_TAKE_PIC = 3;
	private static Timer t;
	private static TimerTakePic		task;
	private static PictureManager pictureManager;
	private static CameraManager cameraManager;
	public CameraTest(){
		
	}
	public CameraTest(CaptureActivityHandler hander,PictureManager pictureManager,CameraManager cameraManager){
		this.hander = hander;
		this.pictureManager = pictureManager;
		this.cameraManager = cameraManager;
	}
	public CameraTest(CaptureActivityHandler hander){
		this.hander = hander;
	}
	
	
	public static void startTaskTakePic(){
		  if (t == null) {
			  t = new Timer();
			  task = new TimerTakePic();
			//第三个参数指定了第一次调用之后，从第二次开始每隔多长的时间调用一次 run() 方法。
//			  t.schedule(task,0,1000L * 2L);
			  t.schedule(task,0,100L);
				
	}
	}
	
	static class TimerTakePic extends TimerTask {

		@Override
		public void run() {
			hander.sendEmptyMessage(MESSAGE_TAKE_PIC);
		}
		
	}
}
