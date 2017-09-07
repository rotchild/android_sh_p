package cx.mobilechecksh.mvideo.picc.updateapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import cx.mobilechecksh.R;


/**
 * 是否升级对话框
 * @author cj
 * */
public class UpdateAppDialog{
	
	private final static String TAG = UpdateAppDialog.class.getSimpleName();
	/** 是否已经提示过升级信息 */
	private static boolean ISHINT = false;
	
	/**
	 * @param activity 上下文对象，弹出升级APP 的页面
	 * @param saveFilePath 文件保存路径
	 * @return boolean true 标识有更新，false标识无可用更新
	 * */
	public static boolean showUpdateAppDialog(final Activity activity, final String saveFilePath, final int iconResId){
		
		final SharedPreferences preferences = UpdateApp.getAppVersionPreferences(activity);
		final String updateURL = preferences.getString("updateURL", "");
		final String updateStage = preferences.getString("updateStage", UpdateApp.UPDATE_STAGE_NOTHINT);
		final String updateHintMessage = preferences.getString("updateHintMessage", "");
		boolean isHint = preferences.getBoolean("isHint",true);
		
		if(ISHINT && !updateStage.equals(UpdateApp.UPDATE_STAGE_FORCE)){
			Log.d(TAG, " is run once , not hint");
			return false;
		}
	
		//如果升级动作为不提示升级
		if(updateStage.equals(UpdateApp.UPDATE_STAGE_NOTHINT)){
			Log.d(TAG, "update stage is not hint update action cancel");
			return false;
		}
				
		//如果升级动作为建议升级   并且升级开关为不在提示，不弹出升级对话框 
		if(updateStage.equals(UpdateApp.UPDATE_STAGE_SUGGEST) && isHint == false){
			Log.d(TAG, "update stage is suggest and hint is false  update action cancel");
			return false;
		}
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
		dialog.setTitle(R.string.updateapp_dialog_title);
		if(updateHintMessage.equals("")){
			dialog.setMessage(R.string.updateapp_dialog_message);
		}else{
			dialog.setMessage(updateHintMessage);
		}
		
		int neutralBtnTextID = R.string.updateapp_dialog_Negative;
		//为强制升级
		if(updateStage.equals(UpdateApp.UPDATE_STAGE_FORCE)){
			neutralBtnTextID = R.string.updateapp_dialog_Negative_cancel;
		}
		//下次再说
		dialog.setNeutralButton(neutralBtnTextID, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(updateStage.equals(UpdateApp.UPDATE_STAGE_FORCE)){
					if(!activity.isFinishing()){
						activity.finish();
					}
				}
			}
		});
		
		
		//升级按钮
		dialog.setPositiveButton(R.string.updateapp_dialog_Positive, new DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Log.d(TAG, "start update app message:" + updateHintMessage);
				final DownloadAPP downloadAPP = new DownloadAPP(activity.getApplicationContext(), updateURL, saveFilePath,iconResId);
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						downloadAPP.download();
					}
				}).start();					
				
			}
		});
		
		if(updateStage.equals(UpdateApp.UPDATE_STAGE_SUGGEST)){
			//不在提示
			dialog.setNegativeButton(R.string.updateapp_dialog_Neutral, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Editor edit = preferences.edit();
					//设置为不在提示
					edit.putBoolean("isHint", false);
					if(edit.commit()){
						Log.d(TAG, "not hint uodate app");
					}else{
						Log.e(TAG, "not hint uodate app failure");
					}
				}
			});
		}
		//设置返回键不可用
		dialog.setCancelable(false);
		dialog.show(); 
		
		ISHINT = true;
		
		return true;
	}
	
	/**
	 * 清除已记住 的提示
	 * */
	public static void cleanHint(){
		ISHINT = false;
	}
	
	
	
	
}
