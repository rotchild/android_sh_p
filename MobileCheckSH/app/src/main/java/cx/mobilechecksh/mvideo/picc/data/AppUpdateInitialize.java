package cx.mobilechecksh.mvideo.picc.data;


import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import cx.mobilechecksh.mvideo.picc.utils.G;


/**
 * app 程序数据升级初始化
 * 
 * 主要用户旧版本升级到新版本初始化执行操作。
 * 如旧版本的数据迁移
 * 该功能模块必须放在UpdateApp 模块前执行
 * 
 * @author cj
 * */
public class AppUpdateInitialize {

	private final String TAG = this.getClass().getSimpleName();
	private final String PREFERENCES_VERSION_DATA_UPDATE = "preferences_app_update_initalize";
	private Context mContext;
	private String mOldVersion = "";
	private String mCurrentVserion = "";
	public AppUpdateInitialize(Context ctx){
		mContext = ctx;
	}
	
	/**
	 * 初始化程序
	 * 主要包含APP 旧版升级到新版操作
	 * */
	public void init(){
		mCurrentVserion = G.getVersionCode(mContext);
		mOldVersion = getOldVersion();
		if(mCurrentVserion.equals(mOldVersion)){
			Log.d(TAG, "app version equally not execute update");
			return;
		}else{
			Log.d(TAG, "execute update");
			boolean result = true;
			if(mOldVersion.equals("")  &&  (mCurrentVserion.equals("5.8")||mCurrentVserion.equals("5.8.1")||mCurrentVserion.equals("5.9"))){
				result = V5_5up5_8();
			}
			
			if(!result){
				clearCurrentData();
			}
		}		
		saveCurrentVersion();
	}
	
	/**
	 * 清除所有数据。回复程序初始值状态
	 * */
	private void clearCurrentData(){
		Log.d(TAG, "update app failure clear current data");
	}
	
	/**
	 * 保存系统版本为当前版本，数据升级动作只会执行一次
	 * */
	private boolean saveCurrentVersion(){
		Editor editor = mContext.getSharedPreferences(PREFERENCES_VERSION_DATA_UPDATE, Context.MODE_PRIVATE).edit();
		editor.putString("version", mCurrentVserion);		
		return editor.commit();		
	}
	
	private String getOldVersion(){
		return mContext.getSharedPreferences(PREFERENCES_VERSION_DATA_UPDATE, Context.MODE_PRIVATE).getString("version", "");
	}
	
	/**
	 * 5.5 升级到 5.8当前版本
	 * 5.8 2013-08-22 16:31 
	 * 版本  江西平安，APP 升级
	 * @return true 表示升级成功， false表示升级失败，需调用clearCurrentData
	 * */
	public boolean V5_5up5_8(){
		Log.d(TAG, "execute update v5.5 >> v5.8");
		
		if(DBOperator.updateV5_5up5_8()){
			Log.d(TAG, "V5_5up5_8 success");
			return true;
		}
		return false;
	}
	
}
