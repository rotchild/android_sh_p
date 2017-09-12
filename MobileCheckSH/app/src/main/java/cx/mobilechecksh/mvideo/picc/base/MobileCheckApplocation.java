package cx.mobilechecksh.mvideo.picc.base;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

import cx.mobilechecksh.R;
import cx.mobilechecksh.mobilecheck.WelcomeActivity;
import cx.mobilechecksh.mvideo.androidcamera.NetCameraService;
import cx.mobilechecksh.mvideo.camera.config.SettingHelper;
import cx.mobilechecksh.mvideo.picc.net.HttpParams;
import cx.mobilechecksh.mvideo.picc.service.NetService;
import cx.mobilechecksh.mvideo.picc.updateapp.UpdateAppDialog;
import cx.mobilechecksh.utils.SystemUtil;
import cx.mobilechecksh.utils.UserManager;


public class MobileCheckApplocation extends Application {
	public static int activityInitFlag=0;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		initApp();

		NIMClient.init(this, loginInfo(), options());

		if (inMainProcess()) {
			// 注意：以下操作必须在主进程中进行
			// 1、UI相关初始化操作
			// 2、相关Service调用
			initUiKit();
		}
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}


	private void initApp(){		
		//初始化网络参数
		HttpParams.init(getApplicationContext());
		cx.mobilechecksh.net.HttpParams.init(getApplicationContext());
		UserManager.getInstance().init(getApplicationContext());
		//初始化数据库对象
//		SQLiteManager.getInstance().init(getApplicationContext());
		
		SettingHelper.getInstance().init(getApplicationContext());

		
		//启动定位服务
//		BaiduLocation baiduLocation = BaiduLocation.instance();
//		baiduLocation.init(getApplicationContext(), baiduLocation.getDefaultLocationClientOption());
		
		//启动抓异常包类
//		CrashHandler.getInstance().init(getApplicationContext(),getPackageName());
		
		
		//启动自动发送发送错误报告
//		AlarmColock.getInstance(getApplicationContext()).setAlarmColock("16:37:00");
	}

	private void initUiKit() {

		// 初始化
		NimUIKit.init(this);

		// 可选定制项
		// 注册定位信息提供者类（可选）,如果需要发送地理位置消息，必须提供。
		// demo中使用高德地图实现了该提供者，开发者可以根据自身需求，选用高德，百度，google等任意第三方地图和定位SDK。
		//NimUIKit.setLocationProvider(new NimDemoLocationProvider());

		// 会话窗口的定制: 示例代码可详见demo源码中的SessionHelper类。
		// 1.注册自定义消息附件解析器（可选）
		// 2.注册各种扩展消息类型的显示ViewHolder（可选）
		// 3.设置会话中点击事件响应处理（一般需要）
		//SessionHelper.init();

		// 通讯录列表定制：示例代码可详见demo源码中的ContactHelper类。
		// 1.定制通讯录列表中点击事响应处理（一般需要，UIKit 提供默认实现为点击进入聊天界面)
		//ContactHelper.init();
	}
	

	
	/** 注销程序 */
	public void logout(){
		//停止百度服务
//		BaiduLocation.instance().stop();
		//停止程序服务
		Intent netServiceIntent = new Intent(getApplicationContext(),NetService.class);
		getApplicationContext().stopService(netServiceIntent);
		//停止音视频服务
		Intent CameraServiceIntent = new Intent(getApplicationContext(),NetCameraService.class);
		getApplicationContext().stopService(CameraServiceIntent);
	}
	
	public void exit(){
		//清除已提示过标记
		UpdateAppDialog.cleanHint();
		logout();
	}


	// 如果返回值为 null，则全部使用默认参数。
	private SDKOptions options() {
		SDKOptions options = new SDKOptions();

		// 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
		StatusBarNotificationConfig config = new StatusBarNotificationConfig();
		config.notificationEntrance = WelcomeActivity.class; // 点击通知栏跳转到该Activity
		config.notificationSmallIconId = R.drawable.ic_launcher;
		// 呼吸灯配置
		config.ledARGB = Color.GREEN;
		config.ledOnMs = 1000;
		config.ledOffMs = 1500;
		// 通知铃声的uri字符串
		config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";
		options.statusBarNotificationConfig = config;

		// 配置保存图片，文件，log 等数据的目录
		// 如果 options 中没有设置这个值，SDK 会使用下面代码示例中的位置作为 SDK 的数据目录。
		// 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
		// 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
		String sdkPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim";
		options.sdkStorageRootPath = sdkPath;

		// 配置是否需要预下载附件缩略图，默认为 true
		options.preloadAttach = true;

		// 配置附件缩略图的尺寸大小。表示向服务器请求缩略图文件的大小
		// 该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
		options.thumbnailSize = 404 / 2;

		// 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
		options.userInfoProvider = new UserInfoProvider() {
			@Override
			public UserInfo getUserInfo(String account) {
				return null;
			}

			@Override
			public int getDefaultIconResId() {
				return R.drawable.ic_launcher;
			}

			@Override
			public Bitmap getTeamIcon(String tid) {
				return null;
			}

			@Override
			public Bitmap getAvatarForMessageNotifier(String account) {
				return null;
			}

			@Override
			public String getDisplayNameForMessageNotifier(String account, String sessionId,
														   SessionTypeEnum sessionType) {
				return null;
			}
		};
		return options;
	}

	// 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
	private LoginInfo loginInfo() {
		return null;
	}

	public boolean inMainProcess() {
		String packageName = getPackageName();
		String processName = SystemUtil.getProcessName(this);
		return packageName.equals(processName);
	}
    
}
