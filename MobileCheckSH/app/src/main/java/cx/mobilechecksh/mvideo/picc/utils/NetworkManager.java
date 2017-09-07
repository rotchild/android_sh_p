package cx.mobilechecksh.mvideo.picc.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import cx.mobilechecksh.mvideo.camera.config.CameraManager;


public class NetworkManager extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		Boolean lNoConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,false);
		
		connectivityChanged(cm, lNoConnectivity);
	}
	
	
	
	public void connectivityChanged(ConnectivityManager cm, boolean noConnectivity) {
		NetworkInfo eventInfo = cm.getActiveNetworkInfo();
		if(CameraManager.getCameraManagerInstance()!=null){
			if (noConnectivity || eventInfo == null || eventInfo.getState() == NetworkInfo.State.DISCONNECTED) {
				Log.i("network","No connectivity: setting network unreachable");
				CameraManager.getCameraManagerInstance().NetWorkConnectedChangedListener.onNetWorkDisconnected();
				
			} else if (eventInfo.getState() == NetworkInfo.State.CONNECTED){
				Log.i("network","network connected");
				CameraManager.getCameraManagerInstance().NetWorkConnectedChangedListener.onNetWorkConnected();
			}
			
			
		}
		
		
	}

}
