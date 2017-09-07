package cx.mobilechecksh.mvideo.picc.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class G {
	
	/**
	 * 应用程序名
	 * */
	public static final String APPNAME = "MobileCheck";
	
	public static final String NEWTASKTRENDS = "com.whhcxw.broadcaset.newtasktrends";
	public static final String PROGESSBARRECEIVE = "com.whhcxw.broadcaset.progessbarreceive";
	public static final String INTENT_NEWTASK = "FROM_PUSHSERVICE_TASK";
	
	
	/**
	 * 文件根目录
	 * */
	public static final String STORAGEPATH = Environment.getExternalStorageDirectory() + "/" + APPNAME + "/";
	
	/**
	 * 自动更新文件下载路径
	 * */
	public static final String UPDATE_APP_SAVE_PATH = STORAGEPATH + APPNAME + ".apk";
	
	/**
	 * 返回压缩比例
	 * */
	public static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound > lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
	
	/**
	 * 显示toast
	 * @param ctx 上下文对象
	 * @param msg 显示类容
	 * @param time ture 为 LONG false 为SHORT
	 * */
	public static void showToast(final Context ctx, int resId, final boolean time) {
		String msg = ctx.getString(resId);
		showToast(ctx,msg,time);
	}
	
	/**
	 * 显示toast
	 * 
	 * @param ctx
	 *            上下文对象
	 * @param msg
	 *            显示类容
	 * @param time
	 *            ture 为 LONG false 为SHORT
	 * */
	public static void showToast(final Context ctx, final String msg, final boolean time) {
		int showTime = Toast.LENGTH_SHORT;
		if (time) {
			showTime = Toast.LENGTH_LONG;
		}
		Toast.makeText(ctx, msg, showTime).show();
	}
	
	/**
	 * 获取当前时间 返回 yyyy-MM-dd mm:ss 格式
	 * 
	 * @return 返回yyyy- MM-dd mm:ss 格式当前时间字符串
	 * */
	public static String getPhoneCurrentTime() {
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return  date.format(Calendar.getInstance().getTime());
	}
	
	
	/**
	 * 获取当前时间 返回 自定义格式 例"yyyyMMddHHmmss"
	 * 
	 * @return 返回自定义格式当前时间字符串
	 * */
	public static String getPhoneCurrentTime(String format) {
		SimpleDateFormat date = new SimpleDateFormat(format);
		String time = date.format(Calendar.getInstance().getTime());
		return time;
	}
	
	
	/**
	 * 获取当前程序的版本名称 
	 * @param context
	 * @return　当前版本名称 如获取错误则返回 “1.0”
	 */
	public static String getVersionCode(Context context) {
		String localVersionName = "";
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			localVersionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (Exception e) {
			localVersionName = "1.0";
			e.printStackTrace();
		}
		return localVersionName;
	}
	
	  //测试IP地址是否合法
    public static  boolean isIp(String ipAddress){
    	 // 定义正则表达式
        String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }
}
