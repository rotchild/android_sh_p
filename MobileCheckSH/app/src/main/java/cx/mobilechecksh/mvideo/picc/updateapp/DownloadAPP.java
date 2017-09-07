package cx.mobilechecksh.mvideo.picc.updateapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cx.mobilechecksh.R;

/**
 * 发起通知栏并下载app
 * @author cj
 * */
public class DownloadAPP {

	private final String TAG = this.getClass().getSimpleName();
	
	private Context mContext;
	private String mDownloadURL;
	private String mFilePath;
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	private File mAppFile;
	
	/** 下载APP时的通知栏 */
	private final int NOTIFICATION_DOWNLOAD_APP = 1;
	
	/** 开始下载APP */
	private final int HANDLER_NOTIFY_START = 0;	
	/** 下载进度有改变 */
	private final int HANDLER_NOTIFY_CHANGE = 1;
	/** 下载完成 */
	private final int HANDLER_NOTIFY_FINISH = 2;
	/** 下载出错 */
	private final int HANDLER_NOTIFY_ERROR = 3;
	

	
	/**
	 * 下载APP 
	 * @param ctx 上下问对象
	 * @param url 下载地址
	 * @param filePath 文件存放路径
	 * @param resIconId 通知栏图标ID
	 * */
	public DownloadAPP(Context ctx, String url, String filePath, int resIconId) {
		mContext = ctx;
		mDownloadURL = url;
		mFilePath = filePath;
		mNotificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotification = new Notification();
		mNotification.icon = resIconId;
		mNotification.tickerText = "正在更新应用程序";		
		mNotification.flags = Notification.FLAG_NO_CLEAR;
	}

	public void download() {
		String parentPath = mFilePath.substring(0,mFilePath.lastIndexOf("/")+1);
		try {
			File parentDirectory = new File(parentPath);
			if(!parentDirectory.exists()){
				parentDirectory.mkdirs();
			}
			
			mAppFile = new File(mFilePath);
			if(!mAppFile.exists()){			
				mAppFile.createNewFile();			
			}			    
			updateNotify("",HANDLER_NOTIFY_START);
			downloadUpdateFile(mDownloadURL,mAppFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "create file error");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "download fail  error");
		}
		
	}
	
	private long downloadUpdateFile(String downloadUrl, File saveFile){
        //这样的下载代码很多，我就不做过多的说明
        int downloadCount = 0;
        int currentSize = 0;
        long totalSize = 0;
        int updateTotalSize = 0;
        
        HttpURLConnection httpConnection = null;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            URL url = new URL(downloadUrl);
            httpConnection = (HttpURLConnection)url.openConnection();
            httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
            if(currentSize > 0) {
                httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
            }
            httpConnection.setConnectTimeout(10000);
            httpConnection.setReadTimeout(20000);
            updateTotalSize = httpConnection.getContentLength();
            String totalSizeM = getMB(updateTotalSize) + "M";
            if (httpConnection.getResponseCode() == 404) {
                throw new Exception("fail!");
            }
            is = httpConnection.getInputStream();                   
            fos = new FileOutputStream(saveFile, false);
            byte buffer[] = new byte[4096];
            int readsize = 0;
            while((readsize = is.read(buffer)) > 0){
                fos.write(buffer, 0, readsize);
                totalSize += readsize;
                //为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
                if((downloadCount == 0) ||(int) (totalSize *100 / updateTotalSize)-10 > downloadCount){ 
                    downloadCount += 10;
                    String downloadSizeM = getMB(totalSize) + "M";
//                    int precentNum = (int)totalSize * 100 / updateTotalSize;
                    //"已下载:" + ((int)totalSize * 100 / updateTotalSize) + "%"
                    String percent = String.format(getString(R.string.updateapp_notification_contentText_process), downloadSizeM + "/" + totalSizeM);
                    Log.d(TAG, "download totalsize:" + totalSize +  percent);
                    updateNotify(percent,HANDLER_NOTIFY_CHANGE);
                }                        
            }
            updateNotify("",HANDLER_NOTIFY_FINISH);
        }catch(Exception e){
        	e.printStackTrace();
            updateNotify("",HANDLER_NOTIFY_ERROR);
        }       
        finally {
            if(httpConnection != null) {
                httpConnection.disconnect();
            }
            try {
	            if(is != null) {
	            	is.close();				
	            }
	            if(fos != null) {
	                fos.close();
	            }
            } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return totalSize;
    }
	
	public void updateNotify(String message, int status){
		switch (status) {
			case HANDLER_NOTIFY_START:		
				message = String.format(getString(R.string.updateapp_notification_contentText_process), "0");
				break;
			case HANDLER_NOTIFY_CHANGE:					
				break;
			case HANDLER_NOTIFY_FINISH:
				installApp();
				message = "下载完成";
				mNotification.flags = Notification.FLAG_AUTO_CANCEL;
				break;
			case HANDLER_NOTIFY_ERROR:
				message = "下载错误";
				mNotification.flags = Notification.FLAG_AUTO_CANCEL;
				break;
			default:
				break;
		}
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,new Intent(), 0);

//		mNotification.setLatestEventInfo(mContext, getString(R.string.updateapp_notification_contentTitle), message, contentIntent);
//	    mNotificationManager.notify(NOTIFICATION_DOWNLOAD_APP, mNotification);
	    
	    if(status == HANDLER_NOTIFY_FINISH){
	    	Log.d(TAG, "download app succ ");
	    	installApp();            	
	    }
	}
	
	private void installApp(){
		if(mAppFile == null){
			Log.e(TAG, "install app fail  error appfile not find");
			return;
		}
		Uri uri = Uri.fromFile(mAppFile);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}

	private String getString(int resId){
		return mContext.getString(resId);
	}
	
	public String getMB(long size){
		double mb = size / 1024.0 / 1024.0;
		String m = String.valueOf(mb);
		m = m.substring(0,m.lastIndexOf(".")+3) + "M" ;
		return m;
	}
}
