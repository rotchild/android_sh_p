package cx.mobilechecksh.mvideo.picc.net;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.util.Log;



import java.net.MalformedURLException;
import java.net.URL;

import cx.mobilechecksh.R;

public class HttpParams {
	
	private final static String TAG = HttpParams.class.getSimpleName();
	
	/** http 存储*/
	private final static String PREFERENCES_HTTP = "preferences_http";
	
	/** 默认编码 */
	public static String DEFAULT_CHARSET = "UTF-8";
	/** 默认超时时间 */
	public static  int DEFAULT_TIME_OUT = 15 * 1000;	
	
	/** 基本的IP端口 */
	public static String BSASURL = "";
	
	/** 用户检测 */
	public static String USERCHECK = "";
	
	/** 修改用户密码*/
	public static String CHANGEPASSWORD = "";
	
	/** 获取任务列表 */
	public static String GETTASKLIST = "";
	
	/** 创建任务 */
	public static String CREATETASK = "";
	
	/** 更新任务状态 */
	public static String UPDATETASK = "";
	
	/**督导，风险回复，更新任务状态*/
	public static String REPLYTASK ="";
	
	/** 上传文件 */
	public static String UPLOADFILES = "";
	
	/** 获取车型列表 */
	public static String GETCARTYPELIST = "";
	
	/** 获取修理厂列表 */
	public static String GETGARAGELIST = "";
	
	/** 获取修理厂列表 */
	public static String GETUSERLIST = "";
	
	/** 获取图片列表 */
	public static String GETPICTURES = "";
	
	/** 上报任务 */
	public static String REPORTTASK = "";
	
	/** 绑定用户 */
	public static String BINDPUSHUSER = "";
	
	/** 获取版本号信息 */
	public static String GETVERSION = "";
	
	/**获取维修厂接待员所在的维修厂信息*/
	public static String GETFFACTORY = "";
	
	/**获取区域列表*/
	public static String GETAREALIST = "";
	
	public static String QUERYTASKS = "";
	
	/**实时定位*/
	public static String SENDLOCATIONLOG = "";
	
	/**解绑推送用户*/
	public static String UNBINDPUSHUSER  = "";
	
	/** 获取保险公司列表 */
	public static String GETINSURANCELIST = "";
	
	/** 获取Dict列表信息 */
	public static String GETDICTS = "";
	
	/** 获取RoleDicts列表信息 */
	public static String GETROLEDICTS = "";
	
	/** 初始化参数 */
	public static void init(Context ctx){
		BSASURL = ctx.getString(R.string.base_url);
		String preferencesIP  = ctx.getSharedPreferences(PREFERENCES_HTTP, Context.MODE_PRIVATE).getString("ip", "");
		if(preferencesIP!=null&&!preferencesIP.trim().equals("")){
			BSASURL = new String(preferencesIP);
		}
		
		try {
			URL url = new URL(BSASURL);
			Editor editor = ctx.getSharedPreferences(PREFERENCES_HTTP, Context.MODE_PRIVATE).edit();
			editor.putString("Host", url.getHost());
			editor.putString("Port", url.getPort()+"");
			boolean  b = editor.commit();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DEFAULT_CHARSET = ctx.getString(R.string.http_charset);
		DEFAULT_TIME_OUT = ctx.getResources().getInteger(R.integer.http_time_out);

		USERCHECK = BSASURL + ctx.getString(R.string.url_UserCheck);
		GETTASKLIST = BSASURL + ctx.getString(R.string.url_GetTaskList);		
		CREATETASK = BSASURL + ctx.getString(R.string.url_CreateTask);
		UPDATETASK = BSASURL + ctx.getString(R.string.url_UpdateTask);
		REPLYTASK = BSASURL + ctx.getString(R.string.url_ReplyTask);
		UPLOADFILES = BSASURL + ctx.getString(R.string.url_UploadFiles);
		GETCARTYPELIST = BSASURL + ctx.getString(R.string.url_GetCarTypeList);
		GETGARAGELIST = BSASURL + ctx.getString(R.string.url_GetGarageList);
		GETUSERLIST = BSASURL + ctx.getString(R.string.url_GetUserList);
		GETPICTURES = BSASURL + ctx.getString(R.string.url_GetPictures);
		REPORTTASK = BSASURL + ctx.getString(R.string.url_ReportTask);
		BINDPUSHUSER = BSASURL + ctx.getString(R.string.url_BindPushUser);
		GETVERSION = BSASURL + ctx.getString(R.string.url_GetVersion)  + "?ProjectName=" + ctx.getString(R.string.project_name)  + "&SystemOS=Android&AccessToken=123";
		GETFFACTORY = BSASURL + ctx.getString(R.string.url_GetGarageInfo);
		GETAREALIST = BSASURL + ctx.getString(R.string.url_GetAreaList);
		QUERYTASKS = BSASURL + ctx.getString(R.string.url_QueryTasks);
		SENDLOCATIONLOG = BSASURL + ctx.getString(R.string.url_SendLocationLog);
		UNBINDPUSHUSER = BSASURL + ctx.getString(R.string.url_UnBindPushUser);
		GETINSURANCELIST = BSASURL + ctx.getString(R.string.url_GetInsuranceList);
		GETDICTS = BSASURL + ctx.getString(R.string.url_GetDicts);
		GETROLEDICTS = BSASURL + ctx.getString(R.string.url_GetRoleDicts);
		CHANGEPASSWORD = BSASURL  + ctx.getString(R.string.url_ChangePassWord);
	}
	
	/**
	 * @param 获取主机 （请求IP）
	 * */
	public static String getHost(Context ctx){
		return ctx.getSharedPreferences(PREFERENCES_HTTP, Context.MODE_PRIVATE).getString("Host", "");
	}
	
	/**
	 * @param 获取端口
	 * */
	public static String getPort(Context ctx){
		return ctx.getSharedPreferences(PREFERENCES_HTTP, Context.MODE_PRIVATE).getString("Port", "");
	}
	
	
	/**
	 * 保存用户IP
	 * @param ip
	 * @return
	 */
	public static boolean saveIP(String ip, Context context){
		Editor editor = context.getSharedPreferences(PREFERENCES_HTTP, Context.MODE_PRIVATE).edit();
		editor.putString("ip", ip);		
		boolean saveSucc = editor.commit();
		if(saveSucc == false){
			Log.e(TAG, "saveUserInfo error  context:");
		}
		return saveSucc;
	}
	
	
}
