package cx.mobilechecksh.data;

/**
 * Created by cx on 2017/8/21.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

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
     * @param deviceNo
     * @param Password
     * @param responseHandler
     */
    public void userLogin(String deviceNo,String Password,HttpResponseHandler responseHandler){
        mResponseHandler=responseHandler;
        if(mIsShowProgressDialog) mProgressDialog.show();
        String url=HttpParams.USERLOGIN;
        RequestParams params=new RequestParams();
        params.put("device_no",deviceNo);
        params.put("password",Password);
        mAsyncHttpClient.post(url,params,response);
    }

    /**
     * 获取设备用户名
     * @param deviceNo
     * @param responseHandler
     */
    public void getUserName(String deviceNo,HttpResponseHandler responseHandler){
        mResponseHandler=responseHandler;
        if(mIsShowProgressDialog) mProgressDialog.show();
        String url=HttpParams.GETUSERNAME;
        RequestParams params=new RequestParams();
        params.put("device_no",deviceNo);
        mAsyncHttpClient.post(url,params,response);
    }


    public void getCurrentTaskList(String stationId,String taskNo,String carNo,String keyword,
                                   String startDate,String endDate,String status,HttpResponseHandler responseHandler){
        Log.e("datahandler","getTaskList enter");
        mResponseHandler=responseHandler;
        if(mIsShowProgressDialog) mProgressDialog.show();

        String url=HttpParams.GETTASKLIST;
        RequestParams params=new RequestParams();
        params.put("station_id", stationId);
        params.put("task_no", taskNo);
        params.put("license_no", carNo);
        params.put("keyword", keyword);
        params.put("startdate", startDate);
        params.put("enddate", endDate);
        params.put("status", status);
        mAsyncHttpClient.post(url,params,response);
    }

    /**
     * 创建任务
     * @param deviceNo 设备号
     * @param taskNo 案件号
     * @param carNo 车牌号
     * @param responseHandler
     */
    public void createTask(String deviceNo,String taskNo,String carNo,HttpResponseHandler responseHandler){
        Log.e("datahandler","createTask enter");
        mResponseHandler=responseHandler;
        if(mIsShowProgressDialog) mProgressDialog.show();

        String url=HttpParams.CREATETASK;
        RequestParams params=new RequestParams();
        params.put("device_no",deviceNo);
        params.put("task_no",taskNo);
        params.put("license_no",carNo);
        mAsyncHttpClient.post(url,params,response);
    }

    /**
     * 请求视频
     * @param DeviceNo
     * @param TaskId
     * @param responseHandler
     */
    public void callForVideo(String DeviceNo,String TaskId,HttpResponseHandler responseHandler){
        Log.e("datahandler","callForVideo enter");
        mResponseHandler=responseHandler;
        if(mIsShowProgressDialog) mProgressDialog.show();

        String url=HttpParams.CALLFORVIDEO;
        RequestParams params=new RequestParams();
        params.put("device_no",DeviceNo);
        params.put("task_id",TaskId);
        mAsyncHttpClient.setSoTimeout(5*60*1000);
        mAsyncHttpClient.post(url,params,response);
    }

    /**
     * 任务已分配，定损中调用
     * @param DeviceNo
     * @param TaskId
     * @param responseHandler
     */
    public void callForVideods(String DeviceNo,String TaskId,HttpResponseHandler responseHandler){
        Log.e("datahandler","callForVideo enter");
        mResponseHandler=responseHandler;
        if(mIsShowProgressDialog) mProgressDialog.show();

        String url=HttpParams.CALLFORVIDEODS;
        RequestParams params=new RequestParams();
        params.put("device_no",DeviceNo);
        params.put("task_id",TaskId);
        mAsyncHttpClient.setSoTimeout(5*60*1000);
        mAsyncHttpClient.post(url,params,response);
    }

    /**
     * 取消视频呼叫
     * @param stationId
     * @param taskId
     * @param responseHandler
     */
    public void cancelVideo(String stationId,String taskId,HttpResponseHandler responseHandler){
        Log.e("datahandler","cancelVideo enter");
        mResponseHandler=responseHandler;
        if(mIsShowProgressDialog) mProgressDialog.show();

        String url=HttpParams.CANCELVIDEO;
        RequestParams params=new RequestParams();
        params.put("station_id",stationId);
        params.put("task_id",taskId);
       // mAsyncHttpClient.setSoTimeout(5*60*1000);
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
