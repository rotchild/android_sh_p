package cx.mobilechecksh.theme;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import cx.mobilechecksh.R;
import cx.mobilechecksh.utils.AppManager;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        AppManager.getAppManager().addActivity(this);
    }

}
