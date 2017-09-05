package cx.mobilechecksh.mobilecheck;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONObject;

import cx.mobilechecksh.R;
import cx.mobilechecksh.data.DataHandler;
import cx.mobilechecksh.global.G;
import cx.mobilechecksh.net.HttpResponseHandler;
import cx.mobilechecksh.theme.BaseActivity;
import cx.mobilechecksh.utils.AppManager;
import cx.mobilechecksh.utils.IMEIManager;
import cx.mobilechecksh.utils.UserManager;

public class WelcomeActivity extends BaseActivity {
String IMEI="";
    String userName="";
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

    private void getUserName(String IMEI) {
        DataHandler dataHandler=new DataHandler(mContext);
        dataHandler.setmIsShowProgressDialog(true);
        dataHandler.getUserName(IMEI,mUserNameRepsonse);
    }
    HttpResponseHandler mUserNameRepsonse=new HttpResponseHandler(){
        @Override
        public void response(boolean success, String response, Throwable error) {
            //super.response(success, response, error);
            if(success){
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    userName = jsonObject.getJSONObject("data").getString(
                            "station_name");
                }catch(Exception e){
                    e.printStackTrace();
                    G.showToast(mContext,mContext.getResources().getString(R.string.response_exception),false);
                    return;
                }
                UserManager.getInstance().saveUserInfo(mContext,userName);
                Intent toLogin=new Intent(mContext,LoginActivity.class);
                startActivity(toLogin);
                AppManager.getAppManager().finishActivity();
            }else{
                G.showToast(mContext,mContext.getResources().getString(R.string.response_false),false);
            }
        }
    };


    private void intView() {
        IMEI=getIMEI();
        getUserName(IMEI);
        Intent toLogin=new Intent(mContext,LoginActivity.class);
        startActivity(toLogin);
        AppManager.getAppManager().finishActivity();
    }


}
