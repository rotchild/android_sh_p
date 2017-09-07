package cx.mobilechecksh.mvideo.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.view.Surface;

import java.io.File;

public class Util {
	/**
	 * wifi信号很好
	 */
	public static final int SSID_GOOD = 0;
	/**
	 * wifi信号一般
	 */
	public static final int SSID_SOSO = 1;
	/**
	 * wifi信号很差
	 */
	public static final int SSID_BAD = 2;
	
    public static int getDisplayRotation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        switch (rotation) {
            case Surface.ROTATION_0: return 0;
            case Surface.ROTATION_90: return 90;
            case Surface.ROTATION_180: return 180;
            case Surface.ROTATION_270: return 270;
        }
        return 0;
    }
    
    
    public static void Assert(boolean cond) {
        if (!cond) {
            throw new AssertionError();
        }
    }
    
    public static <T> T checkNotNull(T object) {
        if (object == null) throw new NullPointerException();
        return object;
    }
    
    public static boolean equals(Object a, Object b) {
        return (a == b) || (a == null ? false : a.equals(b));
    }
    
    
    public static boolean hasStorage() {
		return hasStorage(true);
	}
    
	public static boolean hasStorage(boolean requireWriteAccess) {
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			if (requireWriteAccess) {
				boolean writable = checkFsWritable();
				return writable;
			} else {
				return true;
			}
		} else if (!requireWriteAccess
				&& Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}
	
	private static boolean checkFsWritable() {
		// Create a temporary file to see whether a volume is really writeable.
		// It's important not to put it in the root directory which may have a
		// limit on the number of files.
		String directoryName =
				Environment.getExternalStorageDirectory().toString() + "/DCIM";
		File directory = new File(directoryName);
		if (!directory.isDirectory()) {
			if (!directory.mkdirs()) {
				return false;
			}
		}
		return directory.canWrite();
	}
	
	
	   // Rotates the bitmap by the specified degree.
    // If a new bitmap is created, the original bitmap is recycled.
    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
//            m.setRotate(degrees,
//                    (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            m.setRotate(degrees);
            try {
                Bitmap b2 = Bitmap.createBitmap(
                        b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
            }
        }
        return b;
    }
    
    
	public static NetworkInfo getActiveNetwork(Context context){
	    if (context == null)
	        return null;
	    ConnectivityManager mConnMgr = (ConnectivityManager) context
	            .getSystemService(Context.CONNECTIVITY_SERVICE);
	    if (mConnMgr == null) return null;
	    NetworkInfo aActiveInfo =  mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);// 获取活动网络连接信息
//	    NetworkInfo aActiveInfo = mConnMgr.getActiveNetworkInfo(); // 获取活动网络连接信息
	    return aActiveInfo;
	}
	
	/**
	 * 
	 * @param strength
	 * @return 0 SSID_GOOD , 1 SSID_SOSO,2 SSID_BAD
	 */
	public static int calcSSIDStrength(int strength){
		///////////////////////////////////
		//
		//得到的值是一个0到-100的区间值，是一个int型数据，
		//其中0到-50表示信号最好，
		//-50到-70表示信号偏差，
		//小于-70表示最差，有可能连接不上或者掉线。
		//
		///////////////////////////////////
		
		if(strength<=0&&strength>=-50){
			return SSID_GOOD;
		}else if(strength<-50&&strength>=-70){
			return SSID_SOSO;
		}else {
			return SSID_BAD;
		}
	}
	
	
	
}



