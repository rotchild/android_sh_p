package cx.mobilechecksh.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import cx.mobilechecksh.R;

/**
 * Created by cx on 2017/9/4.
 */

public class Dialog_NewTask extends Dialog {
    static int scrW,scrH;
    private Button call;
    private EditText caseNoET,carNoET;
    private View dialogView;
public Dialog_NewTask(Context context, int themeResId){
    super(context,themeResId);
    WindowManager wm=(WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
    scrW=wm.getDefaultDisplay().getWidth();
    scrH=wm.getDefaultDisplay().getHeight();
}
public void initDialog(){
dialogView= LayoutInflater.from(getContext()).inflate(R.layout.dialog_newtask,null);
    caseNoET= (EditText) dialogView.findViewById(R.id.caseno_input);
    carNoET= (EditText) dialogView.findViewById(R.id.carno_input);
    call=(Button)dialogView.findViewById(R.id.call_btn);
    int diaW=scrW*3/4;
    int diaH=diaW*3/4;
    super.addContentView(dialogView,new ViewGroup.LayoutParams(diaW,diaH));
}
public void setOnPositiveListener(View.OnClickListener listener){
    call.setOnClickListener(listener);
}
public String  getCaseNo(){
    return caseNoET.getText().toString().trim();
}
    public String  getCarNo(){
        return carNoET.getText().toString().trim();
    }

}
