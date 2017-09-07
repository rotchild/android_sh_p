package cx.mobilechecksh.mvideo.picc.net;


import cx.mobilechecksh.http.AsyncHttpResponseHandler;

/**
 *
 * */
public class HttpResponseHandler extends AsyncHttpResponseHandler {
	
	@Override
	public void onSuccess(String response) {
		// TODO Auto-generated method stub
		response(true,response,null);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onFailure(Throwable error, String content) {
		super.onFailure(error);
		response(false,content,error);
	}
	
	/**
	 * 回调函数
	 * @param success 请求是否成功
	 * @param response 响应内容
	 * @param error 错误信息
	 * @param errorContent 错误内容
	 * */
	public void response(boolean success, String response, Throwable error){
		
	}
}
