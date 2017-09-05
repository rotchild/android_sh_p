package cx.mobilechecksh.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by cx on 2017/9/4.
 */

public class MToast {
    public static void toast(Context ctx,String content){
        Toast.makeText(ctx,content,Toast.LENGTH_SHORT).show();
    }
}
