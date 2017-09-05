package cx.mobilechecksh.mobilecheck;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cx.mobilechecksh.R;
import cx.mobilechecksh.global.G;
import cx.mobilechecksh.net.HttpResponseHandler;
import cx.mobilechecksh.theme.BaseActivity;
import cx.mobilechecksh.ui.MDialog;
import cx.mobilechecksh.utils.UserManager;

public class LoginActivity extends BaseActivity {
    /**
     * 用户名
     */
    private String mUserName;
    private String md5Pass;

    private EditText name_et;
    private EditText pass_et;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext=this;
        init();
        findViews();
    }

    private void findViews() {
        Button loginBtn=(Button)findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(mloginListener);
        name_et=(EditText)findViewById(R.id.username_et);
        pass_et=(EditText)findViewById(R.id.password_et);
    }

    private void init() {

    }

    /**
     * 登录btn
     */
    View.OnClickListener mloginListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            mUserName=name_et.getText().toString();
            String userPass=pass_et.getText().toString();
            if(mUserName.equals("")|| userPass.equals("")) {
                MDialog.negativeDialog(mContext, mContext.getResources().getString(R.string.login_mess));
            }else{
//                MD5 md5=new MD5();
//                md5Pass=md5.toMd5(userPass);
//
//                DataHandler dataHandler=new DataHandler(mContext);
//                dataHandler.setmIsShowProgressDialog(true);
//                dataHandler.userCheck(mUserName,md5Pass,"",mUserCheckResponse);

                Intent toMain=new Intent(LoginActivity.this,Main.class);
                startActivity(toMain);
            }

            }

        };

    HttpResponseHandler mUserCheckResponse=new HttpResponseHandler(){
        @Override
        public void response(boolean success, String response, Throwable error) {
            if(success){
                try{
                    //保存登录信息
                    UserManager.getInstance().saveUserInfo(mContext,mUserName);
                }catch(Exception e){
                e.printStackTrace();
                    G.showToast(mContext, mContext.getResources().getString(R.string.login_error), true);
                }
            }else{
                G.showToast(mContext, mContext.getResources().getString(R.string.login_error), true);
            }
        }
    };
}
