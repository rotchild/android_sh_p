package cx.mobilechecksh.mobilecheck;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import cx.mobilechecksh.R;
import cx.mobilechecksh.data.DataHandler;
import cx.mobilechecksh.global.G;
import cx.mobilechecksh.net.HttpResponseHandler;
import cx.mobilechecksh.theme.MBaseActivity;
import cx.mobilechecksh.utils.AppManager;
import cx.mobilechecksh.utils.IMEIManager;
import cx.mobilechecksh.utils.UserManager;

public class WelcomeActivity extends MBaseActivity {
    String userName="";//修理厂名称
    String deviceNo="";//修理厂名称
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);;
        intView();

    }

    /**
     * 获取IMEI
     * @return
     */
    private String  getIMEI() {
        return IMEIManager.getInstance(mContext).getIMEI();
    }

    private void getUserName(String deviceNo) {
        DataHandler dataHandler=new DataHandler(mContext);
        dataHandler.setmIsShowProgressDialog(true);
        dataHandler.getUserName(deviceNo,mUserNameRepsonse);
    }
    HttpResponseHandler mUserNameRepsonse=new HttpResponseHandler(){
        @Override
        public void response(boolean success, String response, Throwable error) {
            //super.response(success, response, error);
            if(success){
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    boolean dataSuccess=jsonObject.getBoolean("success");
                    if(dataSuccess){
                        userName = jsonObject.getJSONObject("data").getString(
                                "station_name");
                    }else {
                        JSONObject err=jsonObject.getJSONObject("err");
                        String message=err.getString("message");
                        G.showToast(mContext,message,false);
                    }

                }catch(Exception e){
                    e.printStackTrace();
                    G.showToast(mContext,mContext.getResources().getString(R.string.response_exception),false);
                    return;
                }
                UserManager.getInstance().saveUserInfo(mContext,userName,deviceNo);
                Intent toLogin=new Intent(mContext,LoginActivity.class);
                startActivity(toLogin);
                AppManager.getAppManager().finishActivity();
            }else{
                G.showToast(mContext,mContext.getResources().getString(R.string.response_false),false);
            }
        }
    };


    private void intView() {
        deviceNo=getIMEI();
        deviceNo="201708311544";
        getUserName(deviceNo);
        /*Intent toLogin=new Intent(mContext,LoginActivity.class);
        startActivity(toLogin);
        AppManager.getAppManager().finishActivity();*/
    }


}
