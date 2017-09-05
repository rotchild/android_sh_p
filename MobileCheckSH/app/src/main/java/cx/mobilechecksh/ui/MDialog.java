package cx.mobilechecksh.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by cx on 2017/8/21.
 */

public class MDialog {
    public static void negativeDialog(Context context,String mess){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("温馨提示");
        builder.setMessage(mess);
        builder.setNegativeButton("确定",new AlertDialog.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
