package cx.mobilechecksh.net;

import cx.mobilechecksh.http.AsyncHttpResponseHandler;

/**
 * Created by cx on 2017/8/21.
 */

public class HttpResponseHandler extends AsyncHttpResponseHandler {
    @Override
    public void onSuccess(String response) {

        //super.onSuccess(response);
        response(true,response,null);
    }

    @Override
    public void onFailure(Throwable error, String content) {

        super.onFailure(error);
        response(false,content,error);

    }

    /**
     * 回调函数
     * @param success 请求是否成功
     * @param response 相应内容
     * @param error 错误信息
     */
    public void response(boolean success,String response,Throwable error){

    }
}
