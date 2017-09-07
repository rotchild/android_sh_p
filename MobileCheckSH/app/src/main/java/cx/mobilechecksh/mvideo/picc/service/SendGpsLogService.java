package cx.mobilechecksh.mvideo.picc.service;

import android.content.Context;
import android.os.Handler;
import android.util.Log;



import org.json.JSONObject;

import cx.mobilechecksh.mvideo.camera.config.SettingHelper;
import cx.mobilechecksh.mvideo.picc.data.DataHandler;
import cx.mobilechecksh.mvideo.picc.net.HttpResponseHandler;

public class SendGpsLogService {

	private String TAG = this.getClass().getName();
	
	private Handler mHandler;
	
	private int mRefreshTime =  1000 * 60 * 5 ;
	
	private DataHandler mDataHandler ;
	
	private String mUserName;
	private String mAccessToken;
	@SuppressWarnings("unused")
	private boolean bWorkingTime = true;
	Context mContext;

	public  SendGpsLogService(Context ctx) {
		// TODO Auto-generated method stub
		mHandler = new Handler();
		mDataHandler = new DataHandler(ctx);
		mUserName = SettingHelper.getInstance().getUserName();
		mAccessToken = SettingHelper.getInstance().getAccessToken();
		mContext=ctx;
	}

	
	public void start() {
		mHandler.post(mRefreshRunnable);
		Log.d(TAG, "SendGpsLogService service start");
	}

	public void stop() {
		Log.d(TAG, "SendGpsLogService service stop");
		mHandler.removeCallbacks(mRefreshRunnable);
	}
	

	private Runnable mRefreshRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.d(TAG, "SendGpsLogService mRefreshRunnable  mRefreshTime:" + mRefreshTime);
//			if (checkTime(mContext)){
				//mDataHandler.SendLocationLog(mUserName, BaiduLocation.instance().LOCAITON_LATITUDE, BaiduLocation.instance().LOCAITON_LONITUDE, G.getPhoneCurrentTime(), mAccessToken, httpResponseHandler);
//			}
			mHandler.postDelayed(mRefreshRunnable, mRefreshTime);
		}
	};
	
	HttpResponseHandler httpResponseHandler = new HttpResponseHandler(){
		@Override
		public void response(boolean success, String response, Throwable error) {
			// TODO Auto-generated method stub
			if(success){
				try {
					JSONObject jsonObject = new JSONObject(response);
					String code = jsonObject.getString("code");
					if(code.equals("0")){
						Log.d(TAG, "SendGpsLogService baidu Location success");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				Log.d(TAG, "SendGpsLogService baidu Location fail");
			}
			
		}
	};
	
	/**
	 * 检测当前时间是否属于工作时间
	 * @param ctx
	 * @return 
	 */
	@SuppressWarnings("unused")
	private boolean checkTime(Context ctx){
		return true;
		//江西 一直发送定位信息，山东安时段发定位信息
/*		String recieves = UserManager.getInstance().getRoleTypes();
		if(recieves.contains(mContext.getResources().getString(R.string.RoleType_M006_name))){
			return true;
		}else {
		    ContentResolver cv = ctx.getContentResolver();
	        String strTimeFormat = android.provider.Settings.System.getString(cv,android.provider.Settings.System.TIME_12_24);
			
	        Calendar c = Calendar.getInstance();
	        int hour = c.get(Calendar.HOUR_OF_DAY);
	        int minute = c.get(Calendar.MINUTE);
	        int am_or_pm = c.get(Calendar.AM_PM);
	        String s = am_or_pm==0?"am":"pm";
	        Log.d(TAG, "the current time is "+hour+":"+minute+"\t"+s);
	        
	        if ((am_or_pm==0&&hour>=7)||(strTimeFormat!=null&&strTimeFormat.equals("24")&&am_or_pm==1&&hour<=19)||(strTimeFormat!=null&&strTimeFormat.equals("12")&&am_or_pm==1&&hour<=7)) {
				return true;
			}else if (strTimeFormat==null) {
				return true;
			}
		}
        return false;*/
        
        
        
	}
}
