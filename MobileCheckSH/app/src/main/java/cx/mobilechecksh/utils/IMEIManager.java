package cx.mobilechecksh.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by cx on 2017/9/4.
 */

public class IMEIManager {
    static IMEIManager _instance=null;
    Context mContext;
    public IMEIManager(Context ctx){
        mContext=ctx;
    }
    public static IMEIManager  getInstance(Context ctx){
        if(_instance==null){
            _instance=new IMEIManager(ctx);
        }
        return _instance;
    }
    public String getIMEI(){
        try{
            String IMEI= ((TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE))
                    .getDeviceId();
            return IMEI;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }
}
