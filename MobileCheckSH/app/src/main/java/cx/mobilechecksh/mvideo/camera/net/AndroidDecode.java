package cx.mobilechecksh.mvideo.camera.net;

public class AndroidDecode {

	static {
		System.loadLibrary("AudioDecode");
	}
	public native byte[] decode(byte[] data,int len);
	public native byte[] encode(byte[] data,int len);
	
    byte[] getFrame(byte[] data,int len)
    {
    	byte[] output = decode(data,len);
    	return output;
    }
    
    byte[] setFrame(byte[] data,int len)
    {
    	byte[] output = encode(data,len);
    	return output;
    }
}
