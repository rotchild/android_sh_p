package cx.mobilechecksh.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by cx on 2017/8/14.
 */

public class UserManager {
    private final String TAG=this.getClass().getSimpleName();
    public static final String PREFERENCE_USER="preference_user";
    private SharedPreferences mSP;
    private Context mContext;
    private static UserManager instance;
    private UserManager(){};

    public static  UserManager getInstance(){
        if(instance==null){
            instance=new UserManager();
        }
        return instance;
    }

    public void init(Context ctx){
        mContext=ctx;
        mSP=getUserPreferences();
    }

    public SharedPreferences getUserPreferences(){
        return mContext.getSharedPreferences(PREFERENCE_USER,Context.MODE_PRIVATE);
    }

    /**
     * 获取登录用户
     * @return
     */
    public String getUserName(){
        return mSP.getString("UserName","");
    }

    /**
     * 获取用户登录状态
     * @return
     */
    public boolean isLogin(){
        return mSP.getBoolean("isLogin",false);
    }

    public boolean saveUserInfo(Context ctx,String UserName){
        SharedPreferences.Editor editor=mContext.getSharedPreferences(PREFERENCE_USER,Context.MODE_PRIVATE).edit();
        editor.putString("UserName",UserName);
        boolean saveSucc=editor.commit();
        if(saveSucc==false){
            Log.e(TAG,"saveUserInfo error  context:");
        }
        return saveSucc;
    }

}
