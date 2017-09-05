package cx.mobilechecksh.global;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by cx on 2017/8/21.
 */

public class G {
    public static void showToast(final Context ctx, final String msg, final boolean time) {
        int showTime = Toast.LENGTH_SHORT;
        if (time) {
            showTime = Toast.LENGTH_LONG;
        }
        Toast.makeText(ctx, msg, showTime).show();
    }
}
