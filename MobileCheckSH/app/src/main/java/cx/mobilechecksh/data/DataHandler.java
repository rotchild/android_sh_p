package cx.mobilechecksh.data;

/**
 * Created by cx on 2017/8/21.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import org.apache.http.protocol.HTTP;

import cx.mobilechecksh.R;
import cx.mobilechecksh.http.AsyncHttpClient;
import cx.mobilechecksh.http.RequestParams;
import cx.mobilechecksh.net.HttpParams;
import cx.mobilechecksh.net.HttpResponseHandler;

/**
 * 用于手机查勘的数据提供
 */
public class DataHandler {
    private Context mContext;
    private AsyncHttpClient mAsyncHttpClient=new AsyncHttpClient();
    private HttpResponseHandler mResponseHandler=null;
    private ProgressDialog mProgressDialog;
    private boolean mIsShowProgressDialog=true;
    private boolean mIsShowError=true;
    private String TAG=DataHandler.this.getClass().getSimpleName();

    public DataHandler(Context ctx){
        mContext=ctx;
        initParams();

        mProgressDialog=new ProgressDialog(mContext);
        String loading="正在请求网络...";
        mProgressDialog.setMessage(loading);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    public void initParams(){
        String charset= HttpParams.DEFAULT_CHARSET;
        int timeout=HttpParams.DEFAULT_TIME_OUT;
        mAsyncHttpClient.addHeader("Charset",charset);
        mAsyncHttpClient.setTimeout(timeout);
    }

    public void setmIsShowProgressDialog(boolean isShow){ mIsShowProgressDialog=isShow;}

    public void setIsCancelableProgressDialog(boolean flag){mProgressDialog.setCancelable(flag);}

    public void setIsShowError(boolean isShow) { mIsShowError=isShow;}

    /**
     * 用户登录
     * @param UserName
     * @param Password
     * @param IMEI
     * @param responseHandler
     */
    public void userCheck(String UserName,String Password,String IMEI,HttpResponseHandler responseHandler){
        mResponseHandler=responseHandler;
        if(mIsShowProgressDialog) mProgressDialog.show();
        String url=HttpParams.USERCHECK;
        RequestParams params=new RequestParams();
        params.put("UserName",UserName);
        params.put("PassWord",Password);
        params.put("IMEI",IMEI);
        mAsyncHttpClient.post(url,params,response);

    }

    /**
     * 获取设备用户名
     * @param IMEI
     * @param responseHandler
     */
    public void getUserName(String IMEI,HttpResponseHandler responseHandler){
        mResponseHandler=responseHandler;
        if(mIsShowProgressDialog) mProgressDialog.show();
        String url=HttpParams.GETUSERNAME;
        RequestParams params=new RequestParams();
        params.put("IMEI",IMEI);
        mAsyncHttpClient.post(url,params,response);
    }

    /**
     * 获取任务列表
     * @param UserName
     * @param responseHandler
     */
    public void getTaskList(String UserName,HttpResponseHandler responseHandler){
        Log.e("datahandler","getTaskList enter");
        mResponseHandler=responseHandler;
        if(mIsShowProgressDialog) mProgressDialog.show();

        String url=HttpParams.GETTASKLIST;
        RequestParams params=new RequestParams();
        params.put("UserName", UserName);
        mAsyncHttpClient.post(url,params,response);
    }

    /**
     * 请求视频通信
     * @param UserName
     * @param responseHandler
     */
    public void callForVideo(String UserName,HttpResponseHandler responseHandler){
        Log.e("datahandler","callForVideo enter");
        mResponseHandler=responseHandler;
        if(mIsShowProgressDialog) mProgressDialog.show();

        String url=HttpParams.CALLFORVIDEO;
        RequestParams params=new RequestParams();
        params.put("UserName",UserName);
        mAsyncHttpClient.post(url,params,response);
    }

    private final HttpResponseHandler response=new HttpResponseHandler(){

        @Override
        public void response(boolean success, String response, Throwable error) {
            //super.response(success, response, error);
            if(mProgressDialog!=null && mIsShowProgressDialog && mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
            }else if(mIsShowProgressDialog){
                Log.d(TAG,"请求取消");
                return;
            }

            if(success && mResponseHandler!=null){
                mResponseHandler.response(success,response,error);
            }else{
                if(mIsShowError){
                    if(error!=null){
                        Log.e("datahandler","error!=null enter");
                      // G.showToast(mContext, mContext.getString(R.string.login_neterror), false);
                    }else{
                        Log.e("datahandler","error=null enter");
                        //G.showToast(mContext, R.string.request_erroe_connect, false);
                    }
                }
            }
        }

        @Override
        public void onSuccess(String response) {
            Log.e("datahandler","success");
            //mResponseHandler.response(true,response,null);
            response(true,response,null);
        }

        @Override
        public void onFailure(Throwable error, String content) {
            Log.e("datahandler","faluire");
           // mResponseHandler.response(false,content,error);
            response(false,content,error);
        }
    };
}
