package cx.mobilechecksh.mobilecheck;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.json.JSONObject;

import cx.mobilechecksh.R;
import cx.mobilechecksh.data.DataHandler;
import cx.mobilechecksh.global.G;
import cx.mobilechecksh.net.HttpResponseHandler;
import cx.mobilechecksh.theme.MBaseActivity;
import cx.mobilechecksh.utils.MD5;
import cx.mobilechecksh.utils.UserManager;

import static com.netease.nim.uikit.common.util.sys.NetworkUtil.TAG;

public class LoginActivity extends MBaseActivity {
    /**
     * 用户登录信息
     */
    private String mUserName;
    private String deviceNo;
    private String stationId;

    private EditText name_et;
    private EditText pass_et;

    private Context mContext;

    private AbortableFuture<LoginInfo> loginRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext=this;
        findViews();
        init();

    }

    private void findViews() {
        Button loginBtn=(Button)findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(mloginListener);
        name_et=(EditText)findViewById(R.id.username_et);
        pass_et=(EditText)findViewById(R.id.password_et);
    }

    private void init() {
       mUserName=UserManager.getInstance().getUserName();
        name_et.setText(mUserName);

    }

    /**
     * 登录btn
     */
    View.OnClickListener mloginListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            mUserName=name_et.getText().toString();
            String password=pass_et.getText().toString();
/*            if(mUserName.equals("")|| password.equals("")) {
                MDialog.negativeDialog(mContext, mContext.getResources().getString(R.string.login_mess));
            }else{
 *//*               Intent toMain=new Intent(LoginActivity.this,Main.class);
                startActivity(toMain);*//*
                deviceNo=UserManager.getInstance().getDeviceNo();
                login(deviceNo,password);
            }*/
/*            LoginInfo info = new LoginInfo(mUserName,password); // config...
            RequestCallback<LoginInfo> callback =
                    new RequestCallback<LoginInfo>() {
                        // 可以在此保存LoginInfo到本地，下次启动APP做自动登录用

                        @Override
                        public void onSuccess(LoginInfo loginInfo) {
                            Intent toMain=new Intent(LoginActivity.this,Main.class);
                            startActivity(toMain);
                        }

                        @Override
                        public void onFailed(int i) {
                            Log.e("login","error code:"+i);
                        }

                        @Override
                        public void onException(Throwable throwable) {
                            Log.e("login","exception: "+throwable.getMessage());
                        }
                    };
            NIMClient.getService(AuthService.class).login(info)
                    .setCallback(callback);*/
            loginRequest = NimUIKit.doLogin(new LoginInfo(mUserName, password), new RequestCallback<LoginInfo>() {
                @Override
                public void onSuccess(LoginInfo param) {
                    LogUtil.i(TAG, "login success");

                    onLoginDone();

                   // DemoCache.setAccount(account);
                   // saveLoginInfo(account, token);

                    // 初始化消息提醒配置
                   // initNotificationConfig();

                    // 进入主界面
                    Intent toMain=new Intent(LoginActivity.this,Main.class);
                    startActivity(toMain);
                    finish();
                }

                @Override
                public void onFailed(int code) {
                    onLoginDone();
                    if (code == 302 || code == 404) {
                        //Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                        Log.e("login","error code:"+code);
                    } else {
                        Log.e("login","error code:"+code);
                    }
                }

                @Override
                public void onException(Throwable exception) {
                    //Toast.makeText(LoginActivity.this, R.string.login_exception, Toast.LENGTH_LONG).show();
                    Log.e("login","exception: "+exception.getMessage());
                    onLoginDone();
                }
            });
            }

        };

    private void onLoginDone() {
        loginRequest = null;
        DialogMaker.dismissProgressDialog();
    }


    /**
     * 登录请求
     * @param deviceNo 设备号：IMEI
     * @param password 未加密密码
     */
        public void login(String deviceNo,String password){
            String pwMd5= new MD5().toMd5(password);
            DataHandler dataHandler=new DataHandler(mContext);
            dataHandler.setmIsShowProgressDialog(true);
            dataHandler.userLogin(deviceNo,pwMd5,mUserLoginResponse);
        }
    HttpResponseHandler mUserLoginResponse=new HttpResponseHandler(){
        @Override
        public void response(boolean success, String response, Throwable error) {
            if(success){//连接success
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    boolean dataSuccess=jsonObject.getBoolean("success");
                    if(dataSuccess){
                        JSONObject data=jsonObject.getJSONObject("data");
                        mUserName=data.getString("station_name");
                        deviceNo=data.getString("device_no");
                        stationId=data.getString("id");
                        //保存登录信息
                        UserManager.getInstance().saveUserInfo(mContext,mUserName,deviceNo,stationId);
                        Intent toMain=new Intent(LoginActivity.this,Main.class);
                        startActivity(toMain);
                    }else{
                        JSONObject err=jsonObject.getJSONObject("err");
                        String message=err.getString("message");
                        G.showToast(mContext, message, false);
                    }

                }catch(Exception e){
                    e.printStackTrace();
                    G.showToast(mContext, mContext.getResources().getString(R.string.response_exception), false);
                }
            }else{
                G.showToast(mContext, mContext.getResources().getString(R.string.response_false), false);
            }
        }
    };
}
