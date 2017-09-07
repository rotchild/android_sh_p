package cx.mobilechecksh.mvideo.camera.picture;


import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Message;
import android.util.Log;



import java.io.FileNotFoundException;

import cx.mobilechecksh.mvideo.camera.CaptureActivityHandler;
import cx.mobilechecksh.mvideo.camera.config.CameraConfigurationManager;

public class PictureSave {

	private PictureFactory 			factory;
	private Bitmap bitmap;
	private CaptureActivityHandler handler;
	private final int				MESSAGE_RESTART_PREVIEW = 1;
	private final int				MESSAGE_SAVE_PIC_FAIL = 2;
	private final int				MESSAGE_UPDATE_THUM= 11;
	private PictureManager.IstoragePictureListener mIstoragePictureListener;
	public PictureSave(PictureFactory pictureFactory,CaptureActivityHandler handler,PictureManager.IstoragePictureListener mIstoragePictureListener){
		this.factory = pictureFactory;
		this.handler = handler;
		this.mIstoragePictureListener = mIstoragePictureListener;
	}
	
	public void  save(){
		new DoPictrueThread(factory, handler,mIstoragePictureListener).start();
		
	}
	
	
	
	class DoPictrueThread extends Thread {
		PictureFactory pictureFactory;
		CaptureActivityHandler handler;
		PictureManager.IstoragePictureListener mIstoragePictureListener;
		boolean havaSave;
		DoPictrueThread(PictureFactory pictureFactory,CaptureActivityHandler handler,PictureManager.IstoragePictureListener mIstoragePictureListener){
			this.pictureFactory = pictureFactory;
			this.handler = handler;
			this.mIstoragePictureListener = mIstoragePictureListener;
		}
		
		
		@Override
		public void run() {
			if (pictureFactory!=null)
			{
				Point pictureSize = CameraConfigurationManager.getPictureSize();
				bitmap = pictureFactory.drawPicture(pictureSize.x,pictureSize.y);
				
				if (bitmap !=null) {
					try {
						havaSave = pictureFactory.savePicture(bitmap);
					} catch (FileNotFoundException e) {
						havaSave = false;
						e.printStackTrace();
					}
				}
				
				if (havaSave)
				{
					Message message = new Message();
					message.obj = pictureFactory.getCurrentFilePath();
					message.what = MESSAGE_UPDATE_THUM;
					handler.sendMessage(message);
					Log.d("com.whhcxw.MobileCheck", "send msg update the thum view");
					
				}else {
					handler.sendEmptyMessage(MESSAGE_SAVE_PIC_FAIL);
					Log.d("com.whhcxw.MobileCheck", "save failer send msg restart preview");
				}
				
			}else {
				handler.sendEmptyMessage(MESSAGE_SAVE_PIC_FAIL);
				Log.d("com.whhcxw.MobileCheck", "save failer send msg restart preview");
			}
			
			if (mIstoragePictureListener !=null) {
				mIstoragePictureListener.afterStorage(pictureFactory.getCurrentFilePath(), havaSave);
			}else {
				Log.e("com.whhcxw.MobileCheck", "save picture error");
			}
		}
	}
	
	

}
