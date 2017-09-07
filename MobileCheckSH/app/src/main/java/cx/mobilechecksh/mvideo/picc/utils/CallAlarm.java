package cx.mobilechecksh.mvideo.picc.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cx.mobilechecksh.mvideo.picc.data.DBOperator;

public class CallAlarm extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = AlarmColock.getInstance(context).ACTION;
	if(action.equals(intent.getAction())){
			boolean b = CatchException.sendExceptionEmail(context);
			if(b){
				CatchException.clearException(context);
			}
		}
		
		//定时删除一个月外的案件
		String date = getOneDate(1).substring(0, 10);
		if(date == null) return;
		boolean result =  DBOperator.deleteOverdueCase(date);
//		if(result){
//			Intent mIntent = new Intent(G.NEWTASKTRENDS); 
//            mIntent.putExtra("yaner", context.getString(R.string.yuandong)); 
//            context.sendBroadcast(mIntent);
//		}
	}

	
	private String getOneDate(int month){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal= Calendar.getInstance();
		cal.add(Calendar.MONTH, -month);    //得到前一个月
//		cal.add(Calendar.DATE, 0);
		long date = cal.getTimeInMillis();
		return format.format(new Date(date));
	}
}
