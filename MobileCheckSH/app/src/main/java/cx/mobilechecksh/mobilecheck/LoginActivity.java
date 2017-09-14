package cx.mobilechecksh.mobilecheck;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import cx.mobilechecksh.ui.MDialog;
import cx.mobilechecksh.utils.MD5;
import cx.mobilechecksh.utils.StringReplace;
import cx.mobilechecksh.utils.UserManager;

import static com.netease.nim.uikit.common.util.sys.NetworkUtil.TAG;

public class LoginActivity extends MBaseActivity {
    /**
     * 用户登录信息
     */
    private String mUserName;
    private String pwMd5;
    private String deviceNo;
    private String stationId;

    private String im_accid;//im count
    private String im_token;//im pwd

    private String camera_ip;
    private String camera_port;
    private String camera_sign;


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
            if(mUserName.equals("")|| password.equals("")) {
                MDialog.negativeDialog(mContext, mContext.getResources().getString(R.string.login_mess));
            }else{
                deviceNo=UserManager.getInstance().getDeviceNo();
                login(deviceNo,password);
            }
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
/*            pwMd5=new MD5().toMd5(password);
            loginRequest = NimUIKit.doLogin(new LoginInfo(mUserName, pwMd5), new RequestCallback<LoginInfo>() {
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
                        LogUtil.e(TAG, "login"+mUserName+"login pass"+pwMd5);
                    }
                    Toast.makeText(LoginActivity.this,"IM登录失败,errorcode:"+code,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onException(Throwable exception) {
                    //Toast.makeText(LoginActivity.this, R.string.login_exception, Toast.LENGTH_LONG).show();
                    Log.e("login","exception: "+exception.getMessage());
                    onLoginDone();
                }
            });*/
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
            pwMd5= new MD5().toMd5(password);
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
                        stationId=data.getString("id");
                        deviceNo=data.getString("device_no");
                        mUserName=data.getString("station_name");
                        im_accid=data.getString("im_accid");
                        im_token=data.getString("im_token");
                        //处理带有反斜杠的device_info
                        String deviceInfoOrign=data.getString("device_info");
                        String deviceInfoNew= StringReplace.toReplace(deviceInfoOrign,"\\");
                        JSONObject deviceInfoObj=new JSONObject(deviceInfoNew);

                        //camera_ip=deviceInfoObj.getString("ip");
                        camera_ip=deviceInfoObj.optString("ip");
                        camera_port=deviceInfoObj.optString("port");
                        camera_sign=deviceInfoObj.optString("sign");




                        //保存登录信息
                        UserManager.getInstance().saveUserInfo(mContext,mUserName,deviceNo,stationId,im_accid,im_token,true,camera_ip,camera_port,camera_sign);
/*                        Intent toMain=new Intent(LoginActivity.this,Main.class);
                        startActivity(toMain)*/;

                        LogUtil.e(TAG, "im_accid is:"+im_accid+"im_token is:"+im_token);
                        /**
                         * im_accid,im_token
                         */
                        loginRequest = NimUIKit.doLogin(new LoginInfo(im_accid, im_token), new RequestCallback<LoginInfo>() {
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
                                    LogUtil.i(TAG, "login"+mUserName+"login pass"+pwMd5);
                                }
                                Toast.makeText(LoginActivity.this,"IM登录失败,errorcode:"+code,Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onException(Throwable exception) {
                                //Toast.makeText(LoginActivity.this, R.string.login_exception, Toast.LENGTH_LONG).show();
                                Log.e("login","exception: "+exception.getMessage());
                                onLoginDone();
                            }
                        });
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
