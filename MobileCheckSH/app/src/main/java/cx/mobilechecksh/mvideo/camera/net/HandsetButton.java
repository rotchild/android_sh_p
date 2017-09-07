package cx.mobilechecksh.mvideo.camera.net;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;
import android.widget.ToggleButton;

public class HandsetButton extends ToggleButton
{
	WindowManager mWinManager;
	WindowManager.LayoutParams params;
	float x,y,startX,startY;
	boolean bcheck;
	AudioManager mAudioManager;
	AudioPlaybak mAudioPlayback;
	Context mContext;

	public HandsetButton(Context context, WindowManager wm, AudioPlaybak AudioPlayback) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		mWinManager = wm;
		mAudioPlayback = AudioPlayback;
		mAudioManager 	= (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
	    params = new WindowManager.LayoutParams();
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY //system overlay windows, which need to be displayed on top of everything else.
						| WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;  // system window, such as low power alert.
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL  //  Even when this window is focusable (its {@link #FLAG_NOT_FOCUSABLE is not set), allow any pointer events outside of the window to be sent to the windows behind it.
						| LayoutParams.FLAG_NOT_FOCUSABLE;  //
		params.gravity= Gravity.LEFT | Gravity.TOP;
		//以屏幕左上角为原点，设置x、y初始值
		params.x = 	0;
		params.y = 	0;
		setTextOn("蓝牙开");
		setTextOff("蓝牙关");
		setChecked(bcheck);
		mWinManager.addView(this, params);
	}

	public boolean onTouchEvent(MotionEvent event)
	{
        //触摸点相对于屏幕左上角坐标
	    x = event.getRawX();
	    y = event.getRawY();
	    Log.d("top left Distance", "------X: "+ x +"------Y:" + y);

	    switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    startY = event.getY();
                    break;
            case MotionEvent.ACTION_MOVE:
                    updatePosition();
                    break;
            case MotionEvent.ACTION_UP:
            	 	updatePosition();
            	 	startX = startY = 0;
            	 	Click();
                    break;
	    }
	    return true;
	}

	void Click()
	{
		mAudioPlayback.stop();
	 	bcheck = !bcheck;
	 	setChecked(bcheck);
		if(bcheck){
		//	mAudioManager.startBluetoothSco();
			mAudioManager.setBluetoothScoOn(true);
			if(mAudioManager.isBluetoothScoOn  ()){
				Log.d("123", "123");
			}
			mAudioPlayback.start(AudioManager.STREAM_VOICE_CALL);
			Toast.makeText(mContext, "手机音频切换到蓝牙耳机", Toast.LENGTH_LONG).show();
		}else{
		//	mAudioManager.stopBluetoothSco();
			mAudioManager.setBluetoothScoOn(false);
			mAudioPlayback.start(AudioManager.STREAM_MUSIC);
			Toast.makeText(mContext, "手机音频切换到外放", Toast.LENGTH_LONG).show();
		}

	}

	//更新浮动窗口位置参数
	private void updatePosition()
	{
	         // View的当前位置
         params.x = (int)(x - startX);
         params.y = (int)(y - startY);
         mWinManager.updateViewLayout(this, params);
	}

	public void UpdataPos(WindowManager.LayoutParams p)
	{
		params.x = p.x + 30;
		params.y = p.y + 30;
		mWinManager.updateViewLayout(this, params);
	}
	
}