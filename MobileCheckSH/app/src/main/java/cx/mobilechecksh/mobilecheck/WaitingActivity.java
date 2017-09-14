package cx.mobilechecksh.mobilecheck;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONObject;

import cx.mobilechecksh.R;
import cx.mobilechecksh.data.DBModle;
import cx.mobilechecksh.data.DataHandler;
import cx.mobilechecksh.global.G;
import cx.mobilechecksh.mvideo.camera.CameraMain;
import cx.mobilechecksh.net.HttpResponseHandler;
import cx.mobilechecksh.utils.UserManager;

public class WaitingActivity extends Activity {
String selecteCaseId="";
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        setContentView(R.layout.activity_waiting);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        selecteCaseId=bundle.getString(DBModle.Task.CaseId);
        String callType=bundle.getString(DBModle.CallType.CallType);
        if(callType.equals("first")){
            callForVideo(UserManager.getInstance().getDeviceNo(),selecteCaseId);
        }else{
            callForVideoDS(UserManager.getInstance().getDeviceNo(),selecteCaseId);
        }

       // Log.e("wait","deviceNo"+UserManager.getInstance().getDeviceNo()+" caseid"+selecteCaseId);
    }

    /**
     *
     * @param DeviceNo
     * @param TaskId
     */
    public void callForVideo(String DeviceNo,String TaskId){
        DataHandler dataHandler=new DataHandler(mContext);
        dataHandler.setmIsShowProgressDialog(true);

        dataHandler.callForVideo(DeviceNo,TaskId,mCallVideoResponse );
    }
    HttpResponseHandler mCallVideoResponse=new HttpResponseHandler(){
        @Override
        public void response(boolean success, String response, Throwable error) {
            // super.response(success, response, error);
            if(success){
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    boolean dataSuccess=jsonObject.getBoolean("success");
                    if(dataSuccess){
                            toOpenVideo();
                            WaitingActivity.this.finish();
                    }else{
                        JSONObject err=jsonObject.getJSONObject("err");
                        String message=err.getString("message");
                        G.showToast(mContext,message,false);
                        WaitingActivity.this.finish();

                    }
                }catch (Exception e){
                    e.printStackTrace();
                    G.showToast(mContext,mContext.getResources().getString(R.string.response_exception),false);
                    return;
                }

            }else{
                G.showToast(mContext,mContext.getResources().getString(R.string.response_false),false);
            }
        }
    };



    public void callForVideoDS(String DeviceNo,String TaskId){
        DataHandler dataHandler=new DataHandler(mContext);
        dataHandler.setmIsShowProgressDialog(true);

        dataHandler.callForVideods(DeviceNo,TaskId,mCallVideodsResponse );
    }

    HttpResponseHandler mCallVideodsResponse=new HttpResponseHandler(){
        @Override
        public void response(boolean success, String response, Throwable error) {
            // super.response(success, response, error);
            if(success){
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    boolean dataSuccess=jsonObject.getBoolean("success");
                    if(dataSuccess){
                        toOpenVideo();
                        WaitingActivity.this.finish();
                    }else{
                        JSONObject err=jsonObject.getJSONObject("err");
                        String message=err.getString("message");
                        G.showToast(mContext,message,false);
                        WaitingActivity.this.finish();

                    }
                }catch (Exception e){
                    e.printStackTrace();
                    G.showToast(mContext,mContext.getResources().getString(R.string.response_exception),false);
                    return;
                }

            }else{
                G.showToast(mContext,mContext.getResources().getString(R.string.response_false),false);
            }
        }
    };

    /**
     * 打开摄像头录像
     */
    public void toOpenVideo(){
        Intent toVideo=new Intent(mContext, CameraMain.class);
        startActivity(toVideo);
    }
}
