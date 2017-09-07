package cx.mobilechecksh.mvideo.crashreport;

import java.util.Timer;
import java.util.TimerTask;

public class ThreadWatchdog extends TimerTask {
	Thread watchThread;
	Timer timer;
	public ThreadWatchdog(Thread thread, long wtime){
		this.watchThread=thread;
		Timer timer = new Timer();
        timer.schedule(this,wtime); 
	}
	
    @Override
    public void run() {  
        watchThread.interrupt();
    }

}
