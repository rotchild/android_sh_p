package cx.mobilechecksh.mobilecheck;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    String stationId="";
    Button cancelVideoBtn;
    TextView stateInfoTv;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        setContentView(R.layout.activity_waiting);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        initView();
        selecteCaseId=bundle.getString(DBModle.Task.CaseId);
        String callType=bundle.getString(DBModle.CallType.CallType);
        if(callType.equals("first")){
            callForVideo(UserManager.getInstance().getDeviceNo(),selecteCaseId);
        }else{
            callForVideoDS(UserManager.getInstance().getDeviceNo(),selecteCaseId);
        }

       // Log.e("wait","deviceNo"+UserManager.getInstance().getDeviceNo()+" caseid"+selecteCaseId);
    }

    public void initView(){
        stateInfoTv=(TextView)findViewById(R.id.status_info);
        cancelVideoBtn=(Button)findViewById(R.id.cancelvideo);
        cancelVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stationId=UserManager.getInstance().getStationId();
                cancelVideo(stationId,selecteCaseId);
            }
        });

    }

    /**
     *待定损呼叫
     * @param DeviceNo
     * @param TaskId
     */
    public void callForVideo(String DeviceNo,String TaskId){
        DataHandler dataHandler=new DataHandler(mContext);
        dataHandler.setmIsShowProgressDialog(false);
        stateInfoTv.setText(mContext.getResources().getString(R.string.hit_call_waiting));
        dataHandler.callForVideo(DeviceNo,TaskId,mCallVideoResponse );
    }
    HttpResponseHandler mCallVideoResponse=new HttpResponseHandler(){
        @Override
        public void response(boolean success, String response, Throwable error) {
            // super.response(success, response, error);
            if(success){
               // stateInfoTv.setText("请求完成");
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    boolean dataSuccess=jsonObject.getBoolean("success");
                    if(dataSuccess){
                            toOpenVideo();
                            WaitingActivity.this.finish();
                    }else{
                        JSONObject err=jsonObject.getJSONObject("err");
                        String message=err.getString("message");
                       // G.showToast(mContext,message,false);
                        stateInfoTv.setText(message);
                       // WaitingActivity.this.finish();

                    }
                }catch (Exception e){
                    e.printStackTrace();
                    G.showToast(mContext,mContext.getResources().getString(R.string.response_exception),false);
                    return;
                }

            }else{
                //stateInfoTv.setText("请求失败");
                G.showToast(mContext,mContext.getResources().getString(R.string.response_false),false);
            }
        }
    };

    /**
     * 定损中呼叫
     * @param DeviceNo
     * @param TaskId
     */

    public void callForVideoDS(String DeviceNo,String TaskId){
        DataHandler dataHandler=new DataHandler(mContext);
        dataHandler.setmIsShowProgressDialog(false);
        stateInfoTv.setText(mContext.getResources().getString(R.string.hit_call_waiting));
        dataHandler.callForVideods(DeviceNo,TaskId,mCallVideodsResponse );
    }

    HttpResponseHandler mCallVideodsResponse=new HttpResponseHandler(){
        @Override
        public void response(boolean success, String response, Throwable error) {
            // super.response(success, response, error);
            if(success){
               // stateInfoTv.setText("请求完成");
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    boolean dataSuccess=jsonObject.getBoolean("success");
                    if(dataSuccess){
                        toOpenVideo();
                        WaitingActivity.this.finish();
                    }else{
                        JSONObject err=jsonObject.getJSONObject("err");
                        String message=err.getString("message");
                        //G.showToast(mContext,message,false);
                        stateInfoTv.setText(message);
                       // WaitingActivity.this.finish();

                    }
                }catch (Exception e){
                    e.printStackTrace();
                    G.showToast(mContext,mContext.getResources().getString(R.string.response_exception),false);
                    return;
                }

            }else{
                //stateInfoTv.setText("请求失败");
                G.showToast(mContext,mContext.getResources().getString(R.string.response_false),false);
            }
        }
    };

    /**
     * 取消呼叫
     * @param stationId
     * @param taskId
     */
    public void cancelVideo(String stationId,String taskId){
        DataHandler dataHandler=new DataHandler(mContext);
        dataHandler.setmIsShowProgressDialog(true);

        dataHandler.cancelVideo(stationId,taskId,mCancelVideoResponse );
    }

    HttpResponseHandler mCancelVideoResponse=new HttpResponseHandler(){
        @Override
        public void response(boolean success, String response, Throwable error) {
            // super.response(success, response, error);
            if(success){
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    boolean dataSuccess=jsonObject.getBoolean("success");
                    if(dataSuccess){
                        G.showToast(mContext,"取消成功",false);
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
