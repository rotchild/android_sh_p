package cx.mobilechecksh.mvideo.picc.data;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cx.mobilechecksh.R;
import cx.mobilechecksh.http.AsyncHttpClient;
import cx.mobilechecksh.http.RequestParams;
import cx.mobilechecksh.mvideo.picc.net.HttpParams;
import cx.mobilechecksh.mvideo.picc.net.HttpResponseHandler;
import cx.mobilechecksh.mvideo.picc.utils.G;

/**
 * 用于手机查勘的数据提供
 * @author cj
 * */
public class DataHandler {
	
	private Context mContext;
	private AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();
	private HttpResponseHandler mResponseHandler = null;
	private ProgressDialog mProgressDialog;
	private boolean mIsShowProgressDialog = true;
	private boolean mIsShowError = true;
	private String TAG = DataHandler.this.getClass().getSimpleName();
	public DataHandler(Context ctx){
		mContext = ctx;
		initParams();
		mProgressDialog = new ProgressDialog(mContext);
		String loading = mContext.getResources().getString(R.string.request_loading);
		mProgressDialog.setMessage(loading);
		mProgressDialog.setCanceledOnTouchOutside(false);
	}
	
	private void initParams(){
		String charset = HttpParams.DEFAULT_CHARSET;
		int timeOut = HttpParams.DEFAULT_TIME_OUT;				
		mAsyncHttpClient.addHeader("Charset", charset);
		mAsyncHttpClient.setTimeout(timeOut);//设置连接超时 及返回时间都为15秒		
	}
	

	
	/**
	 * 设置是否显示对话框
	 * @param isShow true为显示，默认是显示 false 为不显示
	 * */
	public void setIsShowProgressDialog(boolean isShow){
		mIsShowProgressDialog = isShow;
	}
	
	/**
	 * 设置对话框是否可以按返回取消
	 * @param flag false不可取消，默认是显示 true 可取消
	 * */
	public void setIsCancelableProgressDialog(boolean flag){
		mProgressDialog.setCancelable(flag);
	}
	
	/**
	 * 设置是否弹出错误信息
	 * */
	public void setIsShowError(boolean isShow){
		mIsShowError = isShow;
	}
	
	/**
	 * 用户验证接口
	 * @param UserName 用户名
	 * @param PassWord 用户密码 MD5加密过的
	 * @param IMEI 设备编号
	 * @param AsyncHttpResponseHandler 回调函数
	 * */
	public void UserCheck(String UserName, String PassWord, String IMEI, HttpResponseHandler responseHandler){
		mResponseHandler = responseHandler;
		if(mIsShowProgressDialog) if(mIsShowProgressDialog) if(mIsShowProgressDialog) mProgressDialog.show();
		
		String url = HttpParams.USERCHECK;
		RequestParams params = new RequestParams();
		params.put("UserName", UserName);
		params.put("PassWord", PassWord);
		params.put("IMEI", IMEI);		
		mAsyncHttpClient.post(url, params,response);		
	}
	
	/**
	 * 修改用户密码
	 * @param UserName
	 * @param PassWord
	 * @param NewPwd
	 * @param responseHandler
	 */
	public void ChangePassWord(String UserName, String PassWord, String NewPwd, String AccessToken, HttpResponseHandler responseHandler){
		mResponseHandler = responseHandler;
		if(mIsShowProgressDialog) if(mIsShowProgressDialog) if(mIsShowProgressDialog) mProgressDialog.show();
		String url = HttpParams.CHANGEPASSWORD;
		RequestParams params = new RequestParams();
		params.put("UserName", UserName);
		params.put("OldPwd", PassWord);
		params.put("AccessToken", AccessToken);
		params.put("NewPwd", NewPwd);		
		mAsyncHttpClient.post(url, params,response);		
	}
	
	
	
	/**
	 * 获取案件列表(督导风险查勘员使用)
	 * @param UserClass 用户类型
	 * @param UserName 用户名
	 * @param AccessToken 访问凭据
	 * */
	public void GetTaskList(String UserClass, String UserName, String AccessToken, HttpResponseHandler responseHandler){
		mResponseHandler = responseHandler;
		if(mIsShowProgressDialog) if(mIsShowProgressDialog) mProgressDialog.show();
		
		String url = HttpParams.GETTASKLIST;
		RequestParams params = new RequestParams();
		params.put("UserName", UserName);
		params.put("AccessToken", AccessToken);
		params.put("UserClass", UserClass);	
		mAsyncHttpClient.post(url, params,new GetTaskListHttpResponseHandler());
	}
	
	/**
	 * 服务器端创建任务
	 * @param TaskJsonList 案件流列表
	 * @param AccessToken 访问凭据
	 * */
	public void CreateTask(String TaskJsonList, String AccessToken, HttpResponseHandler responseHandler){
		mResponseHandler = responseHandler;
		if(mIsShowProgressDialog) mProgressDialog.show();
		
		String url = HttpParams.CREATETASK;
		RequestParams params = new RequestParams();
		params.put("TaskJsonList", TaskJsonList);
		params.put("AccessToken", AccessToken);	
		mAsyncHttpClient.post(url, params,response);
	}
	
	/**
	 * 本地创建任务(查勘员)
	 * @param TaskJsonList 待定
	 * @param AccessToken 访问凭据
	 * */
	public void CreateLocTask(String TaskJsonList, String AccessToken, HttpResponseHandler responseHandler){
		mResponseHandler = responseHandler;

	}

	/**
	 * 更新案件状态
	 * @param CaseNo 案件号
	 * @param TaskID 任务号
	 * @param TaskType 案件类型
	 * @param Operator 操作员(登录用户)
	 * @param UpdateFileds 需更新的字段名（Fileds只能是operator、state、Memo） 用”|”分割例FontOperator|state|Memo
	 * @param UpdateValues 对应的filed值  Memo的格式[{message:"车子有问题，风险案件。",reply:"继续做"}]
	 * @param AccessToken 访问凭据
	 * */
	public void UpdateTask(String CaseNo, String TaskID, String TaskType, String Operator, String UpdateFileds, String UpdateValues, String AccessToken, HttpResponseHandler responseHandler){
		mResponseHandler = responseHandler;
		if(mIsShowProgressDialog) mProgressDialog.show();
		
		String url = HttpParams.UPDATETASK;
		RequestParams params = new RequestParams();
		params.put("CaseNo", CaseNo);
		params.put("TaskID", TaskID);
		params.put("TaskType", TaskType);
		params.put("Operator", Operator);
		params.put("UpdateFileds", UpdateFileds);
		params.put("UpdateValues", UpdateValues);
		params.put("AccessToken", AccessToken);	
		mAsyncHttpClient.post(url, params,response);
	}
	
	
	
	/**
	 * 督导 风险 回复   更新案件状态
	 * @param CaseNo 案件号
	 * @param TaskID 任务号
	 * @param TaskType 案件类型
	 * @param Operator 操作员(登录用户)
	 * @param UpdateFileds 需更新的字段名
	 * @param UpdateValues 对应的filed值  Memo的格式[{message:"车子有问题，风险案件。",reply:"继续做"}]
	 * @param AccessToken 访问凭据
	 * @param FrontOperator 前台操作员
	 * */
	public void ReplyTask(String CaseNo, String TaskID, String TaskType, String Operator, String UpdateFileds, String UpdateValues, String AccessToken, String FrontOperator, HttpResponseHandler responseHandler){
		mResponseHandler = responseHandler;
		if(mIsShowProgressDialog) mProgressDialog.show();
		
		String url = HttpParams.REPLYTASK;
		RequestParams params = new RequestParams();
		params.put("CaseNo", CaseNo);
		params.put("TaskID", TaskID);
		params.put("TaskType", TaskType);
		params.put("Operator", Operator);
		params.put("UpdateFileds", UpdateFileds);
		params.put("UpdateValues", UpdateValues);
		params.put("AccessToken", AccessToken);	
		params.put("UserName", FrontOperator);
		mAsyncHttpClient.post(url, params,response);
	}
	
	
	
	/**
	 * 上传文件
	 * @param FilePath 文件绝对路径
	 * @param UserName 用户名
	 * @param MD5 用户检测文件是否传输失败
	 * @param AccessToken 访问凭据
	 * */
	public void UploadFiles(String FilePath, String CaseNo, String MD5, String AccessToken, HttpResponseHandler responseHandler){
		mResponseHandler = responseHandler;
		if(mIsShowProgressDialog) mProgressDialog.show();
		
		String url = HttpParams.UPLOADFILES;
		RequestParams params = new RequestParams();
		try {
			params.put("CaseNo", new File(FilePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.response(false, "文件沒找到", e);
			return;
		}
		params.put("CaseNo", CaseNo);
		params.put("MD5", MD5);
		params.put("AccessToken", AccessToken);	
		mAsyncHttpClient.post(url, params,response);
	}
	
	/**
	 * 获取车牌类型列表
	 * @param AccessToken 访问凭据
	 * */
	public void GetCarTypeList(String AccessToken, HttpResponseHandler responseHandler){
//		mResponseHandler = responseHandler;
//		if(mIsShowProgressDialog) mProgressDialog.show();
		
		Log.d("DataHandler", "DataHandler GetCarTypeList");
		
		String url =HttpParams.GETCARTYPELIST;
		RequestParams params = new RequestParams();
		params.put("AccessToken", AccessToken);	
		mAsyncHttpClient.post(url, params,responseHandler);
	}
	
	/**
	 * 获取修理厂列表
	 * @param AccessToken 访问凭据
	 * */
	public void GetGarageList(String AccessToken, HttpResponseHandler responseHandler){
//		mResponseHandler = responseHandler;
//		if(mIsShowProgressDialog) mProgressDialog.show();
		
		String url = HttpParams.GETGARAGELIST;
		RequestParams params = new RequestParams();
		params.put("AccessToken", AccessToken);	
		mAsyncHttpClient.post(url, params,responseHandler);
	}
	
	/**
	 * 获取保险公司厂列表
	 * @param AccessToken 访问凭据
	 * */
	public void GetInsuranceList(String AccessToken, HttpResponseHandler responseHandler){
//		mResponseHandler = responseHandler;
//		if(mIsShowProgressDialog) mProgressDialog.show();
		
		String url = HttpParams.GETINSURANCELIST;
		RequestParams params = new RequestParams();
		params.put("AccessToken", AccessToken);	
		mAsyncHttpClient.post(url, params,responseHandler);
	}
	
	/**
	 * 获取Dict表列表
	 * @param AccessToken 访问凭据
	 * */
	public void GetDicts(String AccessToken, HttpResponseHandler responseHandler){
//		mResponseHandler = responseHandler;
//		if(mIsShowProgressDialog) mProgressDialog.show();
		
		String url = HttpParams.GETDICTS;
		RequestParams params = new RequestParams();
		params.put("AccessToken", AccessToken);	
		mAsyncHttpClient.post(url, params,responseHandler);
	}
	
	/**
	 * 获取Dict表列表
	 * @param AccessToken 访问凭据
	 * */
	public void GetRoleDicts(String AccessToken, HttpResponseHandler responseHandler){
//		mResponseHandler = responseHandler;
//		if(mIsShowProgressDialog) mProgressDialog.show();
		
		String url = HttpParams.GETROLEDICTS;
		RequestParams params = new RequestParams();
		params.put("AccessToken", AccessToken);	
		mAsyncHttpClient.post(url, params,responseHandler);
	}
	
	
	
	
	/**
	 * 获取修理厂列表
	 * @param AccessToken 访问凭据
	 * */
	public void GetUserList(String AccessToken, HttpResponseHandler responseHandler){
//		mResponseHandler = responseHandler;
//		if(mIsShowProgressDialog) mProgressDialog.show();
		
		String url = HttpParams.GETUSERLIST;
		RequestParams params = new RequestParams();
		params.put("AccessToken", AccessToken);	
		mAsyncHttpClient.post(url, params,responseHandler);
	}
	
	
	
	/**
	 * 获取照片列表
	 * @param AccessToken 访问凭据
	 * */
	public void GetPictures(String CaseNo, String AccessToken, HttpResponseHandler responseHandler){
		String url = HttpParams.GETPICTURES;
		if(mIsShowProgressDialog) mProgressDialog.show();
		mResponseHandler = responseHandler;
		
		RequestParams params = new RequestParams();
		params.put("CaseNo", CaseNo);
		params.put("AccessToken", AccessToken);	
		mAsyncHttpClient.post(url, params,response);
	}
	
	/**
	 * 绑定用户
	 * @param UserName 用户名
	 * @param PushID 绑定ID
	 * @param AccessToken 访问信令
	 * */
	public void BindPushUser(String UserName, String PushID, String AccessToken, HttpResponseHandler responseHandler){
		String url = HttpParams.BINDPUSHUSER;
		
		RequestParams params = new RequestParams();
		params.put("UserName", UserName);
		params.put("PushID", PushID);
		params.put("AccessToken", AccessToken);	
		mAsyncHttpClient.post(url, params,responseHandler);
	}	
	
	/**
	 * 解绑用户
	 * @param UserName 用户名
	 * @param PushID 绑定ID
	 * @param AccessToken 访问信令
	 * */
	public void UnBindPushUser(String UserName, String PushID, String AccessToken, HttpResponseHandler responseHandler){
		mResponseHandler = responseHandler;
		if(mIsShowProgressDialog) mProgressDialog.show();
		
		String url = HttpParams.UNBINDPUSHUSER;
		RequestParams params = new RequestParams();
		params.put("UserName", UserName);
		params.put("PushID", PushID);
		params.put("AccessToken", AccessToken);	
		mAsyncHttpClient.post(url, params,new GetTaskListHttpResponseHandler());
	}
	
	/**
	 * 获取维修厂接待员所在的维修厂信息
	 * 
	 * @param UserName 用户名
	 * @param AccessToken 访问信令
	 * @param responseHandler 回调涵数
	 */
	public void GetGarageInfo(String UserName, String AccessToken, HttpResponseHandler responseHandler){
		String url = HttpParams.GETFFACTORY;
		
		RequestParams params = new RequestParams();
		params.put("UserName", UserName);
		params.put("AccessToken", AccessToken);	
		mAsyncHttpClient.post(url, params,responseHandler);
	}
	
	/**
	 * 获取区域列表
	 * @param AccessToken 访问凭据
	 * */
	public void GetAreaList(String AccessToken, HttpResponseHandler responseHandler){
//		mResponseHandler = responseHandler;
//		if(mIsShowProgressDialog) mProgressDialog.show();
		
		String url =HttpParams.GETAREALIST;
		RequestParams params = new RequestParams();
		params.put("AccessToken", AccessToken);	
		mAsyncHttpClient.post(url, params,responseHandler);
	}
	
	/**
	 * 获取区域列表
	 * @param AccessToken 访问凭据
	 * */
	public void QueryTasks(ArrayList<ContentValues> queryTaskList , String AccessToken, String UserName, HttpResponseHandler responseHandler){
		
		String url = HttpParams.QUERYTASKS;
		JSONArray array = new JSONArray();
		try {
			for(ContentValues contentValues : queryTaskList){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(DBModle.Task.CaseNo, contentValues.getAsString(DBModle.Task.CaseNo));
				jsonObject.put(DBModle.Task.TaskType, contentValues.getAsString(DBModle.Task.TaskType));
				array.put(jsonObject);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		RequestParams params = new RequestParams();
		params.put("Params", array.toString());
		params.put("AccessToken", AccessToken);
		params.put("UserName", UserName);
		mAsyncHttpClient.post(url, params,responseHandler);
		
	}
	
	/**
	 * 上传定位数据
	 * @param UserName 用户名
	 * @param Latitude 经度
	 * @param Longitude 纬度
	 * @param GPSDate 时间
	 * @param AccessToken 访问凭证
	 */
	public void SendLocationLog(String UserName , String Latitude , String Longitude , String GPSDate , String AccessToken, HttpResponseHandler responseHandler){
		String url =  HttpParams.SENDLOCATIONLOG;
		RequestParams params = new RequestParams();
		params.put("UserName", UserName);
		params.put("Latitude", Latitude);
		params.put("Longitude", Longitude);
		params.put("GPSDate", GPSDate);
		params.put("AccessToken", AccessToken);
		mAsyncHttpClient.post(url,params, responseHandler);
	}
	
	
	/**
	 * 获取任务列表回调函数
	 * */
	private final class GetTaskListHttpResponseHandler extends HttpResponseHandler{
		public void response(boolean success, String response, Throwable error){
			if(mProgressDialog != null && mIsShowProgressDialog && mProgressDialog.isShowing()){
				mProgressDialog.dismiss();
			}else if(mIsShowProgressDialog){
				//如果对话框开始显示，后取消了，视为取消请求
				Log.d(TAG, "请求取消");
				return;
			}
			if(mResponseHandler != null){
				mResponseHandler.response(success, response, error);
			}						
		}
	};
	
	/**
	 * 基类回调函数
	 * */
	private final HttpResponseHandler response = new HttpResponseHandler(){
		public void response(boolean success, String response, Throwable error){
			if(mProgressDialog != null && mIsShowProgressDialog && mProgressDialog.isShowing()){
				mProgressDialog.dismiss();
			}else if(mIsShowProgressDialog){
				//如果对话框开始显示，后取消了，视为取消请求
				Log.d(TAG, "请求取消");
				return;
			}
			//请求成功
			if(success && mResponseHandler != null){
				mResponseHandler.response(success, response, error);
			}else{
				if(mIsShowError){
					if(error != null){
//						G.showToast(mContext, response + " " + error.getMessage(), false);
						G.showToast(mContext, mContext.getString(R.string.login_neterror), false);
					}else{
						G.showToast(mContext, R.string.request_erroe_connect, false);
					}
				}
			}
		}
	};
}
