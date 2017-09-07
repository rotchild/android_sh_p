package cx.mobilechecksh.mvideo.picc.service;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;



import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cx.mobilechecksh.http.AsyncHttpClient;
import cx.mobilechecksh.http.AsyncHttpResponseHandler;
import cx.mobilechecksh.http.RequestParams;
import cx.mobilechecksh.mvideo.crashreport.CrashHandler;
import cx.mobilechecksh.mvideo.picc.data.DBModle;
import cx.mobilechecksh.mvideo.picc.data.SQLiteManager;
import cx.mobilechecksh.mvideo.picc.net.HttpParams;
import cx.mobilechecksh.mvideo.picc.utils.CatchException;
import cx.mobilechecksh.mvideo.picc.utils.G;
import cx.mobilechecksh.mvideo.picc.utils.MD5;

public class UploadWork {

	private final String TAG = this.getClass().getSimpleName();
	
	private List<ContentValues> mData = null;
	private ContentValues mCurrentItem = null;
	private AsyncHttpClient mAsyncHttpClient = null;

	/** 是否开始 */
	private Context mContext;
	private Handler mHandler = new Handler();
	/** 定时刷新时间4分钟 */
	private int mRefreshTime = 1000 * 60 * 4;
	private long taskTimeout= 90*1000;
	
	public UploadWork(Context ctx){
		super();
		mContext = ctx;		
		mAsyncHttpClient = new AsyncHttpClient();
		mAsyncHttpClient.addHeader("Charset", "UTF-8");
		mAsyncHttpClient.setTimeout(60 * 1000);//设置连接时间都为15秒
		mAsyncHttpClient.setSoTimeout(180*1000);//设置传送时间都为90秒
	}
	

	public NetworkInfo getActiveNetwork(Context context){
	    if (context == null)
	        return null;
	    ConnectivityManager mConnMgr = (ConnectivityManager) context
	            .getSystemService(Context.CONNECTIVITY_SERVICE);
	    if (mConnMgr == null) return null;
	    NetworkInfo aActiveInfo = mConnMgr.getActiveNetworkInfo(); // 获取活动网络连接信息
	    return aActiveInfo;
	}

	
	public static String UPLOADWORK_DATACHANGE = "uploadwork.receiver.datachange";
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(UPLOADWORK_DATACHANGE) || action.equals(CONNECTIVITY_CHANGE_ACTION) ) {
				if (getActiveNetwork(mContext)!=null)	notifyDataChange();
			}
		}
	};
	
	/**
	 * 发送广播通知有新的任务
	 * */
	public static void sendDataChangeBroadcast(Context ctx){
		 Intent intent = new Intent(UPLOADWORK_DATACHANGE);
		 ctx.sendBroadcast(intent);
	}
	
	/** 注册广播*/
	public static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	public void registerReceiver(){
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(UPLOADWORK_DATACHANGE);
		mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);		
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(CONNECTIVITY_CHANGE_ACTION);
		filter.setPriority(1000);
		mContext.registerReceiver(mBroadcastReceiver, filter);
	}
	
	/** 解除广播 */
	public void unRegisterReceiver(){
		mContext.unregisterReceiver(mBroadcastReceiver);
	}
	
	/** 开始工作 */
	public void start(){	
		
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("UploadWork  start()  start time", G.getPhoneCurrentTime());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CatchException.saveException(mContext, jsonObject);
		
		mData = loadData();
		schedulTask();
		mHandler.postDelayed(mReloadRunnable,mRefreshTime);
		registerReceiver();
	}
	
	/** 停止工作 */
	public void stop(){
		mHandler.removeCallbacks(mReloadRunnable);
		unRegisterReceiver();
		Log.d(TAG, "uploadwork service stop");
	}
	
	private ArrayList<ContentValues> loadData(){
		String selection = DBModle.TaskStack.IsRequest + " = " + DBModle.TaskStack.IsRequest_Y;
		return SQLiteManager.getInstance().Select(DBModle.TASKSTACK, new String[] { "*" }, selection, new String[] {}, null,null, "Level asc");
	}	
	
	/** 通知数据改变 */
	public void notifyDataChange(){
		if (mCurrentItem==null) {
			mData = loadData();			
		}else{
			String selection = DBModle.TaskStack.IsRequest + " = " + DBModle.TaskStack.IsRequest_Y;
			ArrayList<ContentValues> cvList=SQLiteManager.getInstance().Select(DBModle.TASKSTACK, new String[] { "*" }, selection, new String[] {}, null,null, "Level asc,StackID asc");
			boolean foundFlag=false;
			ContentValues lastItem;
			if(mData.size()>0){
				lastItem=mData.get(mData.size()-1);
			}else {
				lastItem=mCurrentItem;
			}
			for (ContentValues cv:cvList){
				if (lastItem.getAsInteger("StackID")==cv.getAsInteger("StackID")){
					foundFlag=true;continue; 
				}
				if (foundFlag){
					Log.d(TAG, "add:"+cv.getAsString("Vals"));
					mData.add(cv);
				}
			}
		}
		schedulTask();
	}
	
	public void schedulTask(){
		long lastTime= System.currentTimeMillis();
		if (mCurrentItem!=null){lastTime=mCurrentItem.getAsLong("SchedulTime");};//
		if (mCurrentItem!=null && (System.currentTimeMillis()-lastTime)<taskTimeout) {
			Log.d(TAG, "null schedulTask");
			return;//当前任务未完成
		}
		if(mData.size() > 0){
			mCurrentItem = mData.get(0);
			mCurrentItem.put("SchedulTime", System.currentTimeMillis());
			mData.remove(0);
			Log.d(TAG, "schedulTask");
			executeTask(mCurrentItem);
		}
	}
	
	Runnable mReloadRunnable = new Runnable() {
		@Override
		public void run() {
//			// TODO Auto-generated method stub
//			if(PushManager.getBindUser(mContext).equals("")){
//				PushManager.startBindWork(mContext, PushConstants.LOGIN_TYPE_API_KEY, PushMessageReceiver.API_KEY, UserManager.getInstance().getUserName());PushManager.startBindWork(mContext, PushConstants.LOGIN_TYPE_API_KEY, PushMessageReceiver.API_KEY, UserManager.getInstance().getUserName());
//			}
			
			Log.d(TAG, "后台上传文件 定时刷新 mRefreshTime:" + G.getPhoneCurrentTime()+" queueSize= " +mData.size()+" currentItem = " +mCurrentItem);
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("后台上传文件 定时刷新 mRefreshTime:", G.getPhoneCurrentTime());
				jsonObject.put("queueSize", mData.size());
				jsonObject.put("currentItem", mCurrentItem);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			CatchException.saveException(mContext, jsonObject);
			//从新加载数据
			notifyDataChange();
			//定时刷新
			mHandler.postDelayed(mReloadRunnable,mRefreshTime);
		}
	};
	
	void executeTask(ContentValues data) {
		// TODO Auto-generated method stub
		Log.d(TAG, "execute data:" +data.toString());
		TaskStackMode taskStackMode = new TaskStackMode((ContentValues) data);
		if(taskStackMode._Action.equals(DBModle.TaskStack.Action_Http)) {
			PostRequest(taskStackMode);
		}else if(taskStackMode._Action.equals(DBModle.TaskStack.Action_Upload)) {
			UpLoadFile(taskStackMode);
		}
	}

	private void PostRequest(TaskStackMode tsm) {			
		final String url = tsm._RequestURL;
		final String StackID = tsm._StackID;
		String[] keys = tsm._Keys.split("\\|");
		String[] values = tsm._Vals.split("\\|");
		
		RequestParams params = new RequestParams();
		for(int i = 0; i < keys.length;i++){
			params.put(keys[i], values[i]);
		}			
		if(url.equals(HttpParams.UPDATETASK)){
			try {
				JSONObject json = new JSONObject(tsm._Data);
				Iterator<?> it = json.keys();
				while (it.hasNext()) {
					String key = (String) it.next();
					String value = json.getString(key);
					params.put(key, value);
				}				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG, "PostRequest json error:" + e.getMessage());
				return;
			}
		}
		post(url,StackID, params , "");
	}
	
	private void UpLoadFile(TaskStackMode tsm) {
		final String url = tsm._RequestURL;
		final String StackID = tsm._StackID;
		String[] keys = tsm._Keys. split("\\|");
		String[] values = tsm._Vals.split("\\|");
		String filePath = "";
		RequestParams params = new RequestParams();
		for(int i = 0; i < keys.length;i++){
			if(DBModle.TaskStack.Keys_File.equals(keys[i])){
				filePath = values[i];
				try {
					params.put(keys[i], new File(filePath));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d(TAG, "UpLoadFile dFileNotFoundException filePath:" + filePath);
					//文件没有找到，删除这条记录，并进行下一个
					int rt = deleteTaskStack(StackID);
					if(rt > 0){
						Log.d(TAG, "delete deleteTaskStack tid:" + StackID);
					}
					schedulTask();
					return;
				}
			}else{
				params.put(keys[i], values[i]);
			}
		}	
		
		String path = values[0];
		MD5 md5 = new MD5();
		String fileMa5 = "";
		try {
			fileMa5 = md5.getFileMD5String(new File(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "文件找不到 :"+e);
			
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("url", url);
				jsonObject.put("params", params);
				jsonObject.put("filePath", filePath);
				jsonObject.put("MD5", "MD5  getFileMD5String(new File(path))  error!!!");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			CatchException.saveException(mContext, jsonObject);
			
			return;
		}
		params.put("MD5", fileMa5);
		post(url,StackID, params , filePath);		
	}
	
	private void post(final String url, final String StackID, final RequestParams params , final String filePath){
		mAsyncHttpClient.post(url, params,new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String content) {
				// TODO Auto-generated method stub
				mCurrentItem=null;
				super.onSuccess(content);
				try {
					JSONObject result = new JSONObject(content);
					if(result.getString("code").equals("0")){	
						Log.d(TAG, "PostRequest succ url:" + url + " content:" + content);
						int rt = deleteTaskStack(StackID);
						if(rt > 0){
							Log.d(TAG, "delete deleteTaskStack tid:" + StackID);
							Intent intent = new Intent(G.PROGESSBARRECEIVE);
							mContext.sendBroadcast(intent);
						}						
					}else{
						Log.e(TAG, "PostRequest fail url:" + url + " content:" + content);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				schedulTask();
				
				
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("content", content);
					jsonObject.put("url", url);
					jsonObject.put("params", params);
					jsonObject.put("filePath", filePath);
					if(CrashHandler.getInstance().getActiveNetwork(mContext) == null){
						jsonObject.put("ActiveNetwork", "null");
					}else{
						jsonObject.put("ActiveNetwork", CrashHandler.getInstance().getActiveNetwork(mContext).toString()+"");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				CatchException.saveException(mContext, jsonObject);
				
			}

			@Override
			public void onFailure(Throwable error, String content) {
				// TODO Auto-generated method stub
				super.onFailure(error, content);
				mCurrentItem=null;
				Log.e(TAG, "PostRequest fail url:" + url);
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("error", error);
					jsonObject.put("url", url);
					jsonObject.put("params", params);
					jsonObject.put("filePath", filePath);
					if(CrashHandler.getInstance().getActiveNetwork(mContext) == null){
						jsonObject.put("ActiveNetwork", "null");
					}else{
						jsonObject.put("ActiveNetwork", CrashHandler.getInstance().getActiveNetwork(mContext).toString()+"");
					}
					
					jsonObject.put("content", content);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				CatchException.saveException(mContext, jsonObject);
				schedulTask();
			}
		});
	}

	private int deleteTaskStack(String StackID){
		String selection = DBModle.TaskStack.StackID + " = ? ";
		return SQLiteManager.getInstance().Delete(DBModle.TASKSTACK, selection, new String[]{StackID});
	}
	
	@SuppressWarnings("unused")
	private class TaskStackMode {

		String _StackID;
		String _TID;
		String _Action;
		String _StackType;
		String _RequestURL;
		String _Keys;
		String _Vals;
		String _Level;
		String _IsRequest;
		String _Description;
		String _Data;

		public TaskStackMode(ContentValues cv) {
			if (cv != null) {
				_StackID = cv.getAsString(DBModle.TaskStack.StackID);
				_TID = cv.getAsString(DBModle.TaskStack.TID);
				_Action = cv.getAsString(DBModle.TaskStack.Action);
				_StackType = cv.getAsString(DBModle.TaskStack.StackType);
				_RequestURL = cv.getAsString(DBModle.TaskStack.RequestURL);
				_Keys = cv.getAsString(DBModle.TaskStack.Keys);
				_Vals = cv.getAsString(DBModle.TaskStack.Vals);
				_Level = cv.getAsString("Level");
				_IsRequest = cv.getAsString(DBModle.TaskStack.IsRequest);
				_Description = cv.getAsString(DBModle.TaskStack.Description);
				_Data = cv.getAsString(DBModle.TaskStack.Data);
			}
		}
	}
}
