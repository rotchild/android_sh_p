package cx.mobilechecksh.net;

import android.content.Context;

import cx.mobilechecksh.R;

/**
 * Created by cx on 2017/8/21.
 */

public class HttpParams {
    private final static String TAG = HttpParams.class.getSimpleName();

    /** http 存储*/
    private final static String PREFERENCES_HTTP = "http_preferences";

    /** 默认编码 */
    public static String DEFAULT_CHARSET = "UTF-8";
    /** 默认超时时间 */
    public static  int DEFAULT_TIME_OUT = 15 * 1000;

    /** 基本的IP端口 */
    public static String BASEURL = "";

    /**获取用户名*/
    public static String GETUSERNAME="";

    /** 用户登录 */
    public static String USERLOGIN = "";

    /** 获取任务列表 */
    public static String GETTASKLIST = "";

    /** 创建案件 */
    public static String CREATETASK = "";

    /**请求视频通信*/
    public static String CALLFORVIDEO="";

    /**请求视频通信定损中*/
    public static String CALLFORVIDEODS="";

    public static void init(Context ctx){
        BASEURL=ctx.getString(R.string.base_url);
        String preferencesIP=ctx.getSharedPreferences(PREFERENCES_HTTP,Context.MODE_PRIVATE).getString("ip","");
        if(!preferencesIP.equals("")){
            BASEURL=preferencesIP;
        }
        GETUSERNAME=BASEURL+ctx.getString(R.string.GETUSERNAME);
        USERLOGIN=BASEURL+ctx.getString(R.string.USERLOGIN);
        CREATETASK=BASEURL+ctx.getString(R.string.CREATETASK);
        GETTASKLIST=BASEURL+ctx.getString(R.string.GETTASKLIST);
        CALLFORVIDEO=BASEURL+ctx.getString(R.string.CALLFORVIDEO);
        CALLFORVIDEODS=BASEURL+ctx.getString(R.string.CALLFORVIDEODS);

    }
}
