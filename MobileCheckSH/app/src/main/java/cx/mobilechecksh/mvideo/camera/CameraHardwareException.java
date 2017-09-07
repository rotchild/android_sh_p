/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cx.mobilechecksh.mvideo.camera;

import android.content.Context;
import android.hardware.Camera;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 抛出运行时异常时，此类会接收到异常消息
 */
public class CameraHardwareException implements UncaughtExceptionHandler {

	private CaptureActivityHandler 	handler;
	private Camera camera;
	private final int				MESSAGE_REFRESH = 4;
	private Context context;
	private int 					type;
	public static final int			ERROR_AUTO_FOCUS = 0;
	public static final int			ERROR_TAKE_PIC = 1;
	public static final int			ERROR_TAKE_DEFAULT = 2;
	public static final int			ERROR_TAKE_EXCEPTION = 3;
	
	public CameraHardwareException(Context context, CaptureActivityHandler handler, Camera camera, int type){
		this.handler = handler;
		this.context = context;
		this.type = type;
		this.camera = camera;
	}
	  @Override
	    public void uncaughtException(Thread thread, Throwable ex)
	    {
		  	Writer info = new StringWriter();
			PrintWriter printWriter = new PrintWriter(info);
			ex.printStackTrace(printWriter); 

			Throwable cause = ex.getCause();
			while (cause != null) {
				cause.printStackTrace(printWriter);
				cause = cause.getCause();
			}
			
			String result = info.toString();
			printWriter.close();
		  
		  	Log.e("CameraHardwareException", result);
	      
	      
//	      JSONObject jsonObject = new JSONObject(); 
//			try {
//				jsonObject.put("error", result);
//				if(CrashHandler.getInstance().getActiveNetwork(context) == null){
//					jsonObject.put("ActiveNetwork", "null");
//				}else{
//					jsonObject.put("ActiveNetwork", CrashHandler.getInstance().getActiveNetwork(context).toString()+"");
//				}
//				
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			CatchException.saveException(context, jsonObject);
//	      
//	      CrashHandler.getInstance().mailLog("CAMERA ERROR LOG"+UserManager.getInstance().getUserName(),result);
	      
	      
		  if(result!=null&&result.contains("takePicture")){
			  	Message msg = new Message();
				msg.what = MESSAGE_REFRESH;
				msg.arg1 = CameraHardwareException.ERROR_TAKE_EXCEPTION;
				handler.sendMessage(msg);
			  Log.e("SEND MESSAGES IN EXCEPTION", "MESSAGE_REFRESH");
			  Log.e("CAMERA EXCEPTION", "the exception screen was locked,we must restart the camera");
			  Toast.makeText(context, "相机出现异常！", Toast.LENGTH_LONG).show();
			  
			  return;
		  }else {
//				结束程序
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(1);
		}
		 
	     
	    }
}

