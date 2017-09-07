package cx.mobilechecksh.mvideo.camera.net;

import android.media.AudioFormat;
import android.media.AudioTrack;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class AudioPlaybak implements Runnable {
	///////////////////////////////////////////////////////////
	final int WORKING=0x01;
	final int STOPING=0x02;
	///////////////////////////////////////////////////////////
	
	int m_out_buf_size;
	final int audioBuffSize=2048;
	int workState=STOPING;
	AudioTrack m_out_trk;
	List<byte[]> audioList=new LinkedList<byte[]>();
	int lostFlag=0;
	int upGate=20;
	int downGate=2;
	int checkCount=0;
	public AudioPlaybak(){}
	
	public synchronized  void addAudioData(int dataSize,byte []data){
		if (m_out_trk==null) return;
//		if (m_out_trk.getPlayState()!=AudioTrack.PLAYSTATE_PLAYING) m_out_trk.play();
//		m_out_trk.write(data, 0, dataSize);		

		if (audioList.size()>=upGate){
			Log.d("AUDIO","lostBegin "+audioList.size());
			lostFlag=1;
		}
		if (audioList.size()<=downGate){
			lostFlag=0;
			checkCount=0;	
			Log.d("AUDIO","restore");
		}
		if (lostFlag!=0){
			checkCount++;
			if (checkCount%8==0) {
				return;//放弃一个包
			}
		}
		byte audioData[]=new byte[dataSize];
		for (int i=0;i<dataSize;i++){
			audioData[i]=data[i];
		}
		audioList.add(audioData);
		this.notify();
		return ;
	}
	
	public void start(int streamType){
		if (workState==WORKING) return;
		m_out_buf_size = android.media.AudioTrack.getMinBufferSize(8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		if (m_out_buf_size<audioBuffSize*2) m_out_buf_size=audioBuffSize*2;
		m_out_trk = new AudioTrack(streamType, 8000,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				m_out_buf_size,
				AudioTrack.MODE_STREAM);
		m_out_trk.setStereoVolume(AudioTrack.getMaxVolume()*90/100, AudioTrack.getMaxVolume()*90/100);
		m_out_trk.setPositionNotificationPeriod(8000/16);
		workState=WORKING;
		new Thread(this).start();
	}
	
	public void stop(){
		if (workState==STOPING) return;
		m_out_trk.stop();
		workState=STOPING;
		synchronized(this){
			this.notify();
		}
	}
	
	public void run() {
		while (workState!=STOPING){
			synchronized(this){
				try {
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				while (!audioList.isEmpty()){
					byte[] audioData; 
					synchronized(this){
					audioData=audioList.remove(0);
					}
					if (m_out_trk.getPlayState()!= AudioTrack.PLAYSTATE_PLAYING) m_out_trk.play();
					m_out_trk.write(audioData, 0, audioData.length);		
				}

//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		}
	}
