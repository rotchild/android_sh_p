package cx.mobilechecksh.mvideo.androidcamera;

import java.io.RandomAccessFile;
//import android.util.Log;

public class H264Encoder {
	long encoder=0;
	RandomAccessFile raf=null;
	public byte[] h264Buff =null;
	public int buffSize=0;
	public byte[] descData =new byte[255];
	public int descSize =0;
	static {
		System.loadLibrary("netencoder");
	}
	private H264Encoder(){};

	
	public H264Encoder(int width, int height,int fps,int bitrate) {
		//videoHeadData:00:总长度 01:pps长度 02:sps:长度
		encoder = CompressBegin(width, height,fps,bitrate,descData);
		h264Buff = new byte[width * height *3/2];
		//Log.v("encoder", "width= "+width);
		//Log.v("encoder", "height= "+height);
		descSize=convertToWtspDesc(descData);
	};
	public int convertToWtspDesc(byte []data){
		int totalSize=descData[0];
		int spsSize=0;
		int ppsSize=0;
		int i=4;
		//write sps
		for (;i<totalSize;i++,spsSize++){
			descData[spsSize+2]=descData[i];
			if (descData[i+1]==0x00&&descData[i+2]==0x00&&
					descData[i+3]==0x00&&descData[i+4]==0x01){
				spsSize++;
				break;
			}
		}
		descData[1]=(byte) (spsSize&0xff);descData[0]=(byte) ((spsSize>>8)&0xff);
		//write pps
		for (i=i+5;i<totalSize;i++,ppsSize++){
			descData[spsSize+2+2+ppsSize]=descData[i];
		}
		descData[spsSize+3]=(byte) (ppsSize&0xff);descData[spsSize+2]=(byte) ((ppsSize>>8)&0xff);
		return ppsSize+spsSize+4;
		
	}
	protected void finalize()
    {
		CompressEnd(encoder);
    }
	private native long CompressBegin(int width,int height,int fps,int bitrate,byte[] headerData);
	private native int CompressBuffer(long encoder, int type,byte[] in, int insize,byte[] out);
	private native int CompressEnd(long encoder);
	int frameCnt=0;
	public void compressFrame(byte[] data) {
		//System.out.println(data.length);
//		long begin = System.currentTimeMillis();
		buffSize=0;
		int result=CompressBuffer(encoder, -1, data, data.length,h264Buff);
		buffSize=result;
		//Log.d("encoder", "frame length= "+result);
	//	if (h264Buff[0]!=0){
//			ppsData=new byte[h264Buff[0]];h264Buff[0]=0;
//			System.arraycopy(h264Buff, 0, ppsData, 0, ppsData.length);
//		}
//		try {
//			File file = new File("/sdcard/"+(frameCnt++)+".264");
//			raf = new RandomAccessFile(file, "rw");
//			raf.write(h264Buff, 0,result);
//			raf.close();
//		} catch (Exception ex) {
//			Log.v("System.out", ex.toString());
//		}

	}
	
}