package cx.mobilechecksh.mvideo.camera.config;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RSSIBoradCastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		CameraManager.getCameraManagerInstance().RSSIChangedListener.onRSSIChanged();

	}

}
