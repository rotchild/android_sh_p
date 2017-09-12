package cx.mobilechecksh.theme;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import cx.mobilechecksh.utils.AppManager;

public class MBaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        AppManager.getAppManager().addActivity(this);
    }

}
