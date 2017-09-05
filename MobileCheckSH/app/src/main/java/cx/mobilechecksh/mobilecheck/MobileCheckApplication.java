package cx.mobilechecksh.mobilecheck;

import android.app.Application;

import cx.mobilechecksh.net.HttpParams;
import cx.mobilechecksh.utils.UserManager;

/**
 * Created by cx on 2017/9/4.
 */

public class MobileCheckApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        intApp();
    }

    private void intApp() {
        HttpParams.init(getApplicationContext());
        UserManager.getInstance().init(getApplicationContext());
    }
}
