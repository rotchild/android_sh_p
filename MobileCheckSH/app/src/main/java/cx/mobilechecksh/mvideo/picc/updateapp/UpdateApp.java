package cx.mobilechecksh.mvideo.picc.updateapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cx.mobilechecksh.http.AsyncHttpClient;
import cx.mobilechecksh.http.JsonHttpResponseHandler;
import cx.mobilechecksh.mvideo.picc.net.HttpParams;
import cx.mobilechecksh.mvideo.picc.net.HttpResponseHandler;


/**
 * 更新APP检测
 * @author cj
 * */
public class UpdateApp {

	private final String TAG = this.getClass().getSimpleName();
	/** 建议升级 */
	public final static String UPDATE_STAGE_SUGGEST = "1";
	/** 强制升级 */
	public final static String UPDATE_STAGE_FORCE = "2";
	/** 不提示升级 */
	public final static String UPDATE_STAGE_NOTHINT = "3";
	/** app版本信息保存  */
	private final static String APP_VERSION_PREFERENCES = "app_version_preferences";
	
	private Context mContext;
	// 版本信息下载路径
	private String mURL = "";
	private HttpResponseHandler mHttpResponseHandler;
	public UpdateApp(Context ctx, String url, HttpResponseHandler responseHandler){
		mContext = ctx;
		mURL = url;
		mHttpResponseHandler = responseHandler; 
	} 

	/**
	 * 启动版本控制升级
	 * */
	public void start(){
		checkUpdate();
	}
	
	public void stop(){
		mHttpResponseHandler = null;
	}
	
	/**
	 * 检查是否有新的版本
	 */
	private void checkUpdate() {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.addHeader("Charset", HttpParams.DEFAULT_CHARSET);
		asyncHttpClient.setTimeout(HttpParams.DEFAULT_TIME_OUT);
		Log.d("BBBBBB", "mURL:"+mURL);
		asyncHttpClient.get(mContext, mURL, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, JSONObject data) {
				super.onSuccess(statusCode, data);
				boolean operatorResult = false;
				try {
					if(data.getInt("code") == 0)
					{
						JSONObject response = data.getJSONObject("value");
						String localVersion = getVersionCode(mContext);
						// 获取服务上的支持的最低版本
						String mSupportVersion = response.getString("min_support_version");
						// 获取服务上的最新版本号
						String serviceLastVesion = response.getJSONObject("latest_version").getString("version_code");
						String updateHintMessage = response.getJSONObject("latest_version").getString("update_hint_msg");
						String updateURL = response.getJSONObject("latest_version").getString("update_url");
						boolean isHint = true;
						//初始值为不提示升级
						String updateState = UPDATE_STAGE_NOTHINT;
						
						SharedPreferences appVersionPreferences = getAppVersionPreferences(mContext);
						String locOperatorVersion = appVersionPreferences.getString("locOperatorVersion", "1.0");
						
						Log.d(TAG, "min_support_version:"+mSupportVersion+" serviceLastVesion:"+serviceLastVesion+"  localVersion:"+localVersion + " locOperatorVersion:" + locOperatorVersion);
						
						//判断本地版本是否等于服务器最高版本，是就不再提示
						if(serviceLastVesion.compareTo(localVersion) == 0){						 
							 updateState = UPDATE_STAGE_NOTHINT;						 
						}else{
							//判断本地版本是否低于最低支持版本号
							if(mSupportVersion.compareTo(localVersion)>0){						 
								 updateState = UPDATE_STAGE_FORCE;						 
							}							
							if(!locOperatorVersion.equals(serviceLastVesion)){					
								//如果和服务器版本号不一致就设置为建议升级
								updateState = UPDATE_STAGE_SUGGEST;
								JSONArray serviceVersionList = response.getJSONArray("version_list");
								for (int i = 0; i < serviceVersionList.length(); i++){
									JSONObject versionInfo = serviceVersionList.getJSONObject(i);
									String vesionCode = versionInfo.getString("version_code");
									if (localVersion.equals(vesionCode)) {
										//如果app版本号和该项版本号对应，记录该项版本号的升级动作
										updateState = versionInfo.getString("update_state");
	//									if(updateState.equals(UPDATE_STAGE_SUGGEST)){
	//										isHint = true;
	//									}
									}
								}							
							}else{
								//如果已经处理的信息，赋值为上次系统保存的
								isHint = appVersionPreferences.getBoolean("isHint", true);
								updateState = appVersionPreferences.getString("updateStage", UpdateApp.UPDATE_STAGE_NOTHINT);
							}	
						}
						//保存升级配置信息。
						boolean b = savePreferences(updateURL, updateHintMessage, updateState, serviceLastVesion,isHint);
						Log.d("savePreferences", b+"");
						operatorResult = true;
					}					
				} catch (JSONException e) {
					e.printStackTrace();
					Log.e(TAG, "updateapp parser json error json:" + data.toString());
				}
				//调用下载完成通知
				if(mHttpResponseHandler != null){
					mHttpResponseHandler.response(operatorResult, data.toString(), null);
				}
			}
			
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				//调用下载完成通知
				String result = null;
					if(mHttpResponseHandler != null){
						if (errorResponse != null){
							result = errorResponse.toString();
						}
						mHttpResponseHandler.response(false, result, e);
					}
					Log.e(TAG, "request fail url:" + mURL + " error:" + result);
			}
			
			@Override
		    public void onFailure(Throwable e, String content) {
				//调用下载完成通知
				if(mHttpResponseHandler != null){
					mHttpResponseHandler.response(false, content.toString(), e);
				}
//				Log.e(TAG, "request fail url:" + mURL + " error:" + content);	

		    }
			
		});
	}
	
	/**
	 * 获取当前程序的版本名称 
	 * @param context
	 * @return　当前版本名称
	 */
	private String getVersionCode(Context context) {
		String localVersionName = "";
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			localVersionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (Exception e) {
			localVersionName = "1.0";
			e.printStackTrace();
		}
		return localVersionName;
	}

	/**
	 * 保存版本更新信息
	 * @param updateUrl 下载的URL
	 * @param updateHintMessage 升级提示文字
	 * @param updateStage app的升级动作   建议升级1; 强制升级2; 不提示升级3",
	 * @param locOperatorVersion 本地已处理过的版本号
	 * @param isHint 是否继续提示
	 * @return 返回是否保存成功
	 * */
	private boolean savePreferences(String updateURL, String updateHintMessage, String updateStage, String locOperatorVersion, boolean isHint){
		SharedPreferences updatePreferences = mContext.getSharedPreferences(APP_VERSION_PREFERENCES, Context.MODE_PRIVATE);
		Editor edit = updatePreferences.edit();
		edit.putString("updateURL", updateURL);
		edit.putString("updateStage", updateStage);
		edit.putBoolean("isHint", isHint);
		edit.putString("updateHintMessage", updateHintMessage);		
		edit.putString("locOperatorVersion", locOperatorVersion);
		return edit.commit();
	}
	
	public static SharedPreferences getAppVersionPreferences(Context ctx){
		return ctx.getSharedPreferences(APP_VERSION_PREFERENCES, Context.MODE_PRIVATE);
	}
	
	
	/**
	 * 清空版本更新信息
	 * */
	public static boolean clearPreferences(Context context){
		SharedPreferences updatePreferences = context.getSharedPreferences(APP_VERSION_PREFERENCES, Context.MODE_PRIVATE);
		Editor edit = updatePreferences.edit();
		edit.putString("updateURL", "");
		edit.putString("updateStage", UPDATE_STAGE_NOTHINT);
		edit.putBoolean("isHint", false);
		edit.putString("updateHintMessage", "");		
		edit.putString("locOperatorVersion", "");
		return edit.commit();
	}
	
}
