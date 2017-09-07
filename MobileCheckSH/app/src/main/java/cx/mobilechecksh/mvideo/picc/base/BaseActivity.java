package cx.mobilechecksh.mvideo.picc.base;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;



import java.util.List;

import cx.mobilechecksh.R;
import cx.mobilechecksh.mvideo.picc.theme.PreferenceHelper;
import cx.mobilechecksh.mvideo.picc.utils.AppManager;
import cx.mobilechecksh.theme.MBaseActivity;

public class BaseActivity extends MBaseActivity {

	private final String TAG = this.getClass().getName();

	private Context mContext;
	public int mTheme = R.style.app_skin_default;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		if (savedInstanceState == null) {
			mTheme = PreferenceHelper.getTheme(this);
		} else {
			mTheme = savedInstanceState.getInt("theme");
		}
		setTheme(mTheme);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		//添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mTheme != PreferenceHelper.getTheme(this)) {
			reload();
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//结束Activity&从堆栈中移除
		AppManager.getAppManager().finishActivity(this);
	}

	/**判断程序是在在前台运行*/
	public boolean isBackground(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE) {
					Log.i(TAG, String.format("Background App:",appProcess.processName));
					return true;
				} else { 
					Log.i(TAG, String.format("Foreground App:",appProcess.processName));
					return false;
				}
			}
		}
		return false;
	}



	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("theme", mTheme);
	}

	protected void reload() {
		Intent intent = getIntent();
		overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		finish();
		overridePendingTransition(0, 0);
		startActivity(intent);
	}
}
