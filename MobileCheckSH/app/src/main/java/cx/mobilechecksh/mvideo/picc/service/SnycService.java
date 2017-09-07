package cx.mobilechecksh.mvideo.picc.service;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cx.mobilechecksh.mvideo.picc.data.DBModle;
import cx.mobilechecksh.mvideo.picc.data.DBOperator;
import cx.mobilechecksh.mvideo.picc.data.DataHandler;
import cx.mobilechecksh.mvideo.picc.net.HttpResponseHandler;

/**
 * 同步服务
 * */
public class SnycService extends Thread {

	private final String TAG = this.getClass().getSimpleName();
	private DataHandler mDataHandler;
	private Context mContext;
	private String AccessToken;
	public Handler mHandler;
	private boolean isSnyc = false;
	/** 4小时刷新一次 */
	private int mRefreshTime = 1000 * 60*60*8;

	public SnycService(Context ctx, String _accessToken) {
		mContext = ctx;
		AccessToken = _accessToken;
		mDataHandler = new DataHandler(mContext);
		// mRefreshTime = 1000 * 5;
	}

//	public void start() {
////		isSnyc = true;
//		mHandler.post(mRefreshRunnable);
//	}

//	public void stop() {
//		mHandler.getLooper().quit();
//		Log.d(TAG, "sync service stop");
//		isSnyc = false;
//		mHandler.removeCallbacks(mRefreshRunnable);
//	}

	private Runnable mRefreshRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.d(TAG, "sync mRefreshRunnable  mRefreshTime:" + mRefreshTime);
			if (isSnyc) {
				snyc();
				mHandler.postDelayed(mRefreshRunnable, mRefreshTime);
			}
		}
	};


	public void run() {
		Looper.prepare();
		mHandler = new Handler();
		isSnyc = true;
		mHandler.post(mRefreshRunnable);
		Looper.loop();
		isSnyc = false;
		mHandler.removeCallbacks(mRefreshRunnable);
	}
	
	// 开始同步
	private void snyc() {
		mDataHandler.setIsShowProgressDialog(false);
		mDataHandler.setIsShowError(false);
		try {

			// 同步车牌信息
			mDataHandler.GetCarTypeList(AccessToken, new HttpResponseHandler() {
				@Override
				public void response(boolean success, String response,
						Throwable error) {
					try {
						if (!success) {
							Log.e(TAG, "sync CarType get data error ");
							return;
						}
						JSONObject data = new JSONObject(response);
						String code = data.getString("code");
						if (code.equals("0")) {
							JSONArray car_type_list = data.getJSONObject("value").getJSONArray("car_type_list");
							DBOperator.syncCarType(car_type_list);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			// 同步修理厂信息
			mDataHandler.GetGarageList(AccessToken, new HttpResponseHandler() {
				public void response(boolean success, String response,
						Throwable error) {
					if (!success) {
						Log.e(TAG, "sync Repair get data error ");
						return;
					}
					try {
						JSONObject data = new JSONObject(response);
						String code = data.getString("code");
						if (code.equals("0")) {
							JSONArray repair_list = data.getJSONObject("value").getJSONArray("repair_list");
							DBOperator.syncGarage(repair_list);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			// 同步用户信息
			mDataHandler.GetUserList(AccessToken, new HttpResponseHandler() {
				@Override
				public void response(boolean success, String response,
						Throwable error) {
					if (!success) {
						Log.e(TAG, "sync User get data error ");
						return;
					}
					try {
						JSONObject data = new JSONObject(response);
						String code = data.getString("code");
						if (code.equals("0")) {
							JSONArray user_list = data.getJSONObject("value").getJSONArray("user_list");
							DBOperator.syncUsers(user_list, "");
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			// 同步区域信息
			mDataHandler.GetAreaList(AccessToken, new HttpResponseHandler() {
				@Override
				public void response(boolean success, String response,
						Throwable error) {
					try {
						if (!success) {
							Log.e(TAG, "sync Area get data error ");
							return;
						}
						JSONObject data = new JSONObject(response);
						String code = data.getString("code");
						if (code.equals("0")) {
							JSONArray area_list = data.getJSONObject("value").getJSONArray("area_list");
							DBOperator.syncArea(area_list);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			// 同步保险公司信息
			mDataHandler.GetInsuranceList(AccessToken, new HttpResponseHandler() {
				@Override
				public void response(boolean success, String response,
						Throwable error) {
					try {
						if (!success) {
							Log.e(TAG, "sync Insurance get data error ");
							return;
						}
						JSONObject data = new JSONObject(response);
						String code = data.getString("code");
						if (code.equals("0")) {
							JSONArray insurance_list = data.getJSONObject("value").getJSONArray("insurance_list");
							DBOperator.syncInsurance(insurance_list);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			
			// 同步Dict表
			mDataHandler.GetDicts(AccessToken, new HttpResponseHandler() {
				@Override
				public void response(boolean success, String response,
						Throwable error) {
					try {
						if (!success) {
							Log.e(TAG, "sync Dict get data error ");
							return;
						}
						JSONObject data = new JSONObject(response);
						String code = data.getString("code");
						if (code.equals("0")) {
							JSONArray dict_list = data.getJSONObject("value").getJSONArray("dict_list");
							DBOperator.syncDict(dict_list);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			
			// 同步RoleDicts表
			mDataHandler.GetRoleDicts(AccessToken, new HttpResponseHandler() {
				@Override
				public void response(boolean success, String response,
						Throwable error) {
					try {
						if (!success) {
							Log.e(TAG, "sync RoleDicts get data error ");
							return;
						}
						JSONObject data = new JSONObject(response);
						String code = data.getString("code");
						if (code.equals("0")) {
							JSONArray role_dict_list = data.getJSONObject("value").getJSONArray("role_dict_list");
							DBOperator.syncRoleDicts(role_dict_list);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, "sync err" + e.getMessage());
		}
	}


	/**
	 * 同步查勘任务状态
	 * */
	public void snycSurveyTask(ArrayList<ContentValues> queryTaskList, String username, final Context context) {
		// 同步区域信息
		mDataHandler.QueryTasks(queryTaskList, AccessToken,username,new HttpResponseHandler() {
					@Override
					public void response(boolean success, String response,
							Throwable error) {
						try {
							if (!success) {
								Log.e(TAG,"sync snycSurveyTask get data error ");
								return;
							}
							JSONObject data = new JSONObject(response);
							String code = data.getString("code");
							if (code.equals("0")) {

								int modifyCount = 0;
								JSONArray task_list = data.getJSONObject("value").getJSONArray("task_list");
								for (int i = 0; i < task_list.length(); i++) {
									JSONObject task = task_list.getJSONObject(i);
									String backState = task.getString(DBModle.Task.BackState);
									String caseno = task.getString(DBModle.Task.CaseNo);
									String tasktype = task.getString(DBModle.Task.TaskType);
									String memo = task.getString(DBModle.Task.Memo);

									ContentValues values = new ContentValues();
									values.put(DBModle.Task.Memo, memo);
									values.put(DBModle.Task.BackState,backState);
									values.put(DBModle.Task.IsNew, "1");

									if (backState.equals(DBModle.TaskLog.FrontState_FINISH)) {
										// 修改案件
										if (DBOperator.updateTask(caseno,tasktype, values)) {
											// 修改成功++
											modifyCount++;
										}
									}
								}
								// 有案件状态更新，发送广播刷新列表
//								if (modifyCount > 0) {
//									Intent mIntent = new Intent(Main.NEWTASKTRENDS);
//									mIntent.putExtra("yaner", context.getString(R.string.yuandong));
//									context.sendBroadcast(mIntent);
//								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}

}
