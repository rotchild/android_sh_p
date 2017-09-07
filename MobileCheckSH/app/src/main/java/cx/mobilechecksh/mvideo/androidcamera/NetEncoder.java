package cx.mobilechecksh.mvideo.androidcamera;
public class NetEncoder {
	public NetEncoder(int videoWidth, int videoHight, int frameRate,
			int bitRate, int sign, String server) {
		setVideoFormat(videoWidth, videoHight, frameRate, bitRate);
		setSign(sign);
		setServer(server);
	}
	
	public NetEncoder() {
	}	
	public native int setVideoFormat(int width, int height, int fps, int bitrate);

	public native int setSign(int sign);

	public native int setServer(String server);

	public native int setVideoDesc(int descSize, byte[] descData);

	public native int startWork(boolean isUdp);

	public native int stopWork();

	public native int addFrame(int frameType, int frameSize, byte[] framedata);

	public native int addAudioFrame(int frameSize, byte[] framedata);

	static {
		System.loadLibrary("netencoder");
	}
}
