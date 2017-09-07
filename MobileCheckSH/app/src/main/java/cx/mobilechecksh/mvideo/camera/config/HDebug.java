package cx.mobilechecksh.mvideo.camera.config;

import android.util.Log;

public class HDebug {
	public static HDebug debug;
	private static boolean 	on;
	
	public static HDebug init(boolean on){
		if(debug ==null){
			debug = new HDebug(on);
		}
		return debug;
	}
	
	public static HDebug instance(){
		return debug;
	}
	private HDebug(boolean on){
		this.on = on;
	}
	public  void Log_i(String tag, String msg){
		if (on) {
			Log.i(tag, msg);
		}
		
	}
	
	public  void Log_e(String tag, String msg){
		if (on) {
			Log.e(tag, msg);
		}
		
	}
	
	public  void Log_d(String tag, String msg){
		if (on) {
			Log.d(tag, msg);
		}
	}
}
