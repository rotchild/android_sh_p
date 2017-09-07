package cx.mobilechecksh.mvideo.camera;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import cx.mobilechecksh.R;
import cx.mobilechecksh.mvideo.camera.config.SettingHelper;
import cx.mobilechecksh.mvideo.camera.config.WifiAdmin;
import cx.mobilechecksh.mvideo.picc.ui.Titlebar;
import cx.mobilechecksh.theme.MBaseActivity;

public class Fixnet extends MBaseActivity {
	
	private String mipAddress,mgateway,mSsid;
	private EditText ssidText,ipText,gateText,maskText;
	private Context context;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fix_net);
		initTitle();
		initView();
		
		context = this;
		
		Button ipSetBtn = (Button) findViewById(R.id.fix_net_btn);
		ipSetBtn.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				WifiAdmin admin = new WifiAdmin(Fixnet.this);
				admin.openWifi();
				admin.addNetwork(admin.CreateWifiInfo(mSsid, "1234567890", 3));
				
				
				WifiConfiguration wifiConf = null;
				WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
				WifiInfo connectionInfo = wifiManager.getConnectionInfo();
				List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
				for (WifiConfiguration conf : configuredNetworks){
					if (conf.networkId == connectionInfo.getNetworkId()){
						wifiConf = conf;
						break;
					}
				}
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				String ssid = wifiInfo.getSSID();
				if(ssid.equals(mSsid)){
					try{	
						setIpAssignment("STATIC", wifiConf); //or "DHCP" for dynamic setting
						setIpAddress(InetAddress.getByName(mipAddress), 24, wifiConf);
						setGateway(InetAddress.getByName(mgateway), wifiConf);
						wifiManager.updateNetwork(wifiConf); //apply the setting
						Toast.makeText(context, "恢复成功", Toast.LENGTH_LONG).show();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				else
					Toast.makeText(context, "未连接到正确网络", Toast.LENGTH_LONG).show();
				
				
				
			
				
			}
		});
	}


	private  void initTitle(){
		Titlebar titlebar = (Titlebar)findViewById(R.id.titlebar);
		titlebar.setLeftBackImagesRes(R.drawable.ui_titile_return);
		titlebar.showLeft();
	}
	
	private void initView(){
		String mssid = null;
		String mip = null;
		String mgate = null;
		String mmask = null;
		ssidText = (EditText) findViewById(R.id.ssid_edit);
		ipText = (EditText) findViewById(R.id.ip_edit);
		gateText = (EditText) findViewById(R.id.gateway_edit);
		maskText = (EditText) findViewById(R.id.netmask_edit);
		mssid = SettingHelper.getInstance().getSSID("");
		mip = SettingHelper.getInstance().getIpAdd("");
		mgate = SettingHelper.getInstance().getGateWay("");
		mmask = SettingHelper.getInstance().getNetMask("");
		Log.i("1111111111111", mssid);
		ssidText.setText(mssid);
		ipText.setText(mip);
		gateText.setText(mgate);
		maskText.setText(mmask);
		mSsid = mssid;
		/*保存ip和网关用于恢复设置*/
		mipAddress = mip;
		mgateway = mgate;
	}
	
	private static void setIpAssignment(String assign , WifiConfiguration wifiConf)
		    throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		        setEnumField(wifiConf, assign, "ipAssignment");     
		    }
	
	private static void setIpAddress(InetAddress addr, int prefixLength, WifiConfiguration wifiConf)
		    throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException,
            NoSuchMethodException, ClassNotFoundException, InstantiationException, InvocationTargetException {
		Object linkProperties = getField(wifiConf, "linkProperties");
		if(linkProperties == null)
			return;
		Class laClass = Class.forName("android.net.LinkAddress");
		Constructor laConstructor = laClass.getConstructor(new Class[]{InetAddress.class, int.class});
		Object linkAddress = laConstructor.newInstance(addr, prefixLength);

		ArrayList mLinkAddresses = (ArrayList)getDeclaredField(linkProperties, "mLinkAddresses");
		mLinkAddresses.clear();
		mLinkAddresses.add(linkAddress);        
		}
    
	private static Object getField(Object obj, String name)
			    throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field f = obj.getClass().getField(name);
		Object out = f.get(obj);
		return out;
		}

	private static Object getDeclaredField(Object obj, String name)
			    throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
		Field f = obj.getClass().getDeclaredField(name);
		f.setAccessible(true);
		Object out = f.get(obj);
		return out;
		}  

	private static void setEnumField(Object obj, String value, String name)
			    throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field f = obj.getClass().getField(name);
		f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
	}
	
	public static void setGateway(InetAddress gateway, WifiConfiguration wifiConf)
		    throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException,
            ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
		Object linkProperties = getField(wifiConf, "linkProperties");
		if(linkProperties == null)
			return;
		Class routeInfoClass = Class.forName("android.net.RouteInfo");
		Constructor routeInfoConstructor = routeInfoClass.getConstructor(new Class[]{InetAddress.class});
		Object routeInfo = routeInfoConstructor.newInstance(gateway);
		ArrayList mRoutes = (ArrayList)getDeclaredField(linkProperties, "mRoutes");
		mRoutes.clear();
		mRoutes.add(routeInfo);
	}
		
}
