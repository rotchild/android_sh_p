package cx.mobilechecksh.mvideo.camera;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


import cx.mobilechecksh.R;
import cx.mobilechecksh.mvideo.picc.net.HttpParams;
import cx.mobilechecksh.mvideo.picc.net.HttpResponseHandler;
import cx.mobilechecksh.mvideo.picc.ui.Titlebar;
import cx.mobilechecksh.mvideo.picc.updateapp.UpdateApp;
import cx.mobilechecksh.mvideo.picc.updateapp.UpdateAppDialog;
import cx.mobilechecksh.mvideo.picc.utils.G;
import cx.mobilechecksh.theme.MBaseActivity;


public class APPabout extends MBaseActivity {

	private ProgressDialog mProgressDialog;
	private Button checkBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_about);
		initTitle();
		initView();
	}
	
	private  void initTitle(){
		Titlebar titlebar = (Titlebar)findViewById(R.id.titlebar);
		titlebar.setLeftBackImagesRes(R.drawable.ui_titile_return);
		titlebar.showLeft();
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		showProgressDialog();
		TextView versionText = (TextView) this.findViewById(R.id.about_system_version);
		versionText.setText(getVersionName(this));
		
	    checkBtn = (Button)this.findViewById(R.id.about_app_checkversion);
		checkBtn.setText(getString(R.string.about_system_current_code) + getVersionName(this));
		
		checkBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				UpdateAppDialog.cleanHint();
//				UpdateAppDialog.showUpdateAppDialog(APPabout.this, G.UPDATE_APP_SAVE_PATH,R.drawable.logo_code);
				mProgressDialog.show();
				UpdateApp.clearPreferences(APPabout.this);
				UpdateApp updateApp = new UpdateApp(APPabout.this, HttpParams.GETVERSION, updateAPPHttpResponseHandler);
				updateApp.start();	
			}
		});
//		updateApp();
	}
	
	/** 检测APP 版本信息回调函数 */
	HttpResponseHandler updateAPPHttpResponseHandler = new HttpResponseHandler(){
		@Override
		public void response(boolean success, String response, Throwable error) {
			// TODO Auto-generated method stub
			super.response(success, response, error);
				//弹出升级APP 对话框
				mProgressDialog.dismiss();
				if(success){
					UpdateAppDialog.cleanHint();
					boolean b = UpdateAppDialog.showUpdateAppDialog(APPabout.this,G.UPDATE_APP_SAVE_PATH,R.drawable.logo_code_orange);
					if(!b){
						checkBtn.setText(getString(R.string.about_system_laster_code));
					}
				}else{
					G.showToast(APPabout.this, getString(R.string.about_system_weberror), true);
				}
					
		}			
	};

//	private void updateApp(){
//		SharedPreferences preferences = UpdateApp.getAppVersionPreferences(mContext);
//		String updateStage = preferences.getString("updateStage", UpdateApp.UPDATE_STAGE_NOTHINT);
//		if(updateStage.equals("3")){
//			mButton.setEnabled(false);
//		}else{
//			if(updateStage.equals("1")){
//				Editor editor = preferences.edit();
//				editor.putBoolean("isHint", true);
//				editor.commit();
//			}
//		} 
//	}
	
	public static String getVersionName(Context context) {
		String version = "";
		try {
			// 获取packagemanager的实例
			PackageManager packageManager = context.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo;

			packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}

	
	public void showProgressDialog(){
		mProgressDialog = new ProgressDialog(this);
		String loading = getResources().getString(R.string.request_loading);
		mProgressDialog.setMessage(loading);
		mProgressDialog.setCanceledOnTouchOutside(false);
	}
	
}
