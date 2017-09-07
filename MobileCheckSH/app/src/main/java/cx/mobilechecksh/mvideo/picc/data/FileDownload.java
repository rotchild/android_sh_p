package cx.mobilechecksh.mvideo.picc.data;


import cx.mobilechecksh.http.AsyncHttpClient;
import cx.mobilechecksh.http.BinaryHttpResponseHandler;
import cx.mobilechecksh.mvideo.picc.net.HttpParams;

/**
 * 文件下载
 * */
public class FileDownload {	
	/**
	 * 下载
	 * */
	public static void download(String url, BinaryHttpResponseHandler responseHandler){
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.addHeader("Charset", HttpParams.DEFAULT_CHARSET);
		asyncHttpClient.setTimeout(HttpParams.DEFAULT_TIME_OUT);
		asyncHttpClient.get(url, responseHandler);
	}
}
