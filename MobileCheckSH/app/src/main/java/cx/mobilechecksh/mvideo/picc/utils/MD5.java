package cx.mobilechecksh.mvideo.picc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5{
	
    public String toMd5(String s) {
        try {
                MessageDigest algorithm = MessageDigest.getInstance("MD5");
                algorithm.reset();                
                algorithm.update(s.getBytes());
                return toHexString(algorithm.digest());
        } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
        }
    }
    
    
	private String toHexString(byte[] bytes) {
	        StringBuilder hexString = new StringBuilder();
	        for (byte b : bytes) {
	                hexString.append(Integer.toHexString((b & 0x000000FF) | 0xFFFFFF00).substring(6));//.append(separator);
	        }
	        return hexString.toString();
	}
	
	
	public String getFileMD5String(File file) throws IOException {
		  FileInputStream in = new FileInputStream(file);
		  FileChannel ch =in.getChannel();
		  MappedByteBuffer byteBuffer =ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
		  MessageDigest algorithm = null;
		  try {
				algorithm = MessageDigest.getInstance("MD5");
		  } catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();				
				return "";
		  }
		  algorithm.update(byteBuffer);
		  return bufferToHex(algorithm.digest());
	}
	
	private static String bufferToHex(byte bytes[]){
		  return bufferToHex(bytes, 0,bytes.length);
	}
	
	private static String bufferToHex(byte bytes[], int m, int n){
		  StringBuffer stringbuffer =new StringBuffer(2 * n);
		  int k = m + n;
		  for (int l = m; l< k; l++) {
		  appendHexPair(bytes[l], stringbuffer);
		  }
		  return stringbuffer.toString();
	}
	
	protected static char hexDigits[] = { '0', '1','2', '3', '4', '5', '6', '7', '8', '9','a', 'b', 'c', 'd', 'e', 'f'};
	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		  char c0 = hexDigits[(bt& 0xf0) >> 4];
		  char c1 = hexDigits[bt& 0xf];
		  stringbuffer.append(c0);
		  stringbuffer.append(c1);
	}
	
}
