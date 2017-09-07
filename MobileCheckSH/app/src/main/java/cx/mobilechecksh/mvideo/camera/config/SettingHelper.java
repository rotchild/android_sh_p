package cx.mobilechecksh.mvideo.camera.config;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;


public class SettingHelper {

private final String TAG = this.getClass().getSimpleName();
	
	private final String PREFERENCES_SETTING = "preferences_setting";
	
	
	private SharedPreferences mPreferences;
	
	private Context mContext;
	
	private final String PREFERENCES_SETTINTSYSTEM="preferences_settingsystem";
	
	private static SettingHelper instance;
	
	private SettingHelper(){
		
	}
	
	public static SettingHelper getInstance(){
		if(instance == null){
			instance = new SettingHelper();
		}
		return instance;		
	}
	
	public void init(Context ctx){
		mContext = ctx;
		mPreferences = getSettingPreferences();
	}
	
	public SharedPreferences getSettingPreferences(){
		return mContext.getSharedPreferences(PREFERENCES_SETTING, Context.MODE_PRIVATE);
	}
	
	public String getAccessToken(){
		return "";
	}
	
	public String getUserName(){
		return "";
	}
	
	public String getRealName(){
		return "";
	}
	
	
	/**
	 * 保存系统参数
	 * @param cxt
	 * @param FPS 帧率
	 * @param BIT 码率
	 * @param pw_ph 幅面宽度*幅面高度
	 * @return
	 */
	public boolean saveSettingSystem(Context cxt, String ip, String FPS , String BIT, String pw_ph, String connectTyep, int PwPhSelect, int conSelect, String sign){
		Editor editor = mContext.getSharedPreferences(PREFERENCES_SETTING, Context.MODE_PRIVATE).edit();
		editor.putString("FPS", FPS);
		editor.putString("ip", ip);
		editor.putString("BIT", BIT);
		editor.putString("pw_ph", pw_ph);
		editor.putString("connectTyep", connectTyep);
		editor.putInt("pw_phSelect", PwPhSelect);
		editor.putInt("conSelect", conSelect);
		editor.putString("sign", sign);
		boolean saveSucc = editor.commit();
		if(saveSucc == false){
			Log.e(TAG, "saveSettingSystem error  context:");
		}
		return saveSucc;
	}
	
	/**
	 * 返回用户IP
	 */
	public String getIP(String defaultValue){
		return mPreferences.getString("ip", defaultValue);
	}
	/**
	 * 返回用户名
	 */
	public String getSIGN(String defaultValue){
		return mPreferences.getString("sign", defaultValue);
	}
	
	
	/**
	 * 返回  帧率
	 */
	public String getFPS(String defaultValue){
		return mPreferences.getString("FPS",  defaultValue);
	}
	
	/**
	 * 返回  码率
	 */
	public String getBIT(String defaultValue){
		return mPreferences.getString("BIT",  defaultValue);
	}

	/**
	 * 返回  幅面宽度*幅面高度
	 */
	public String getPw_ph(String defaultValue){
		return mPreferences.getString("pw_ph",  defaultValue);
	}
	/**
	 * 返回 连接类型
	 */
	public String getConnectType(String defaultValue){
		return mPreferences.getString("connectTyep",  defaultValue);
	}
	
	/**
	 * 返回 幅面 选择的ID
	 */
	public int getPwPhSelect(int defaultValue){
		return mPreferences.getInt("pw_phSelect", defaultValue);
	}
	/**
	 * 返回 连接类型选择的ID
	 */
	public int getConnecSelect(int defaultValue){
		return mPreferences.getInt("conSelect", defaultValue);
	}
	
	public String getSSID(String defaultValue){
		return 	mPreferences.getString("ssid", defaultValue);
	}
	
	public String getIpAdd(String defaultValue){
		return 	mPreferences.getString("ipadd", defaultValue);
	}
	
	public String getNetMask(String defaultValue){
		return 	mPreferences.getString("netmask", defaultValue);
	}
	
	public String getGateWay(String defaultValue){
		return 	mPreferences.getString("gateway", defaultValue);
	}
	
	public boolean saveNetConfig(Context cxt, String ssid, String ip, String netmask, String gateway){
		Editor editor = mContext.getSharedPreferences(PREFERENCES_SETTING, Context.MODE_PRIVATE).edit();
		editor.putString("ssid", ssid);
		editor.putString("ipadd", ip);
		editor.putString("gateway", gateway);
		editor.putString("netmask", netmask);
		boolean saveSucc = editor.commit();
		if(saveSucc == false){
			Log.e(TAG, "saveSettingSystem error  context:");
		}
		return saveSucc;
	}
	
	
	
	
}
