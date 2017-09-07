package cx.mobilechecksh.mvideo.camera.picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cx.mobilechecksh.R;

public class PictureFactory {
	private Context context;
	private byte[] 			data;
	private int 			iconResId;
	private String filePath;
	private double			latitude,longtitude;
	private String markName;
	private	int 			mOrientationCompensation;
	private String time;
	private String userNo;
	private boolean			isAlpha;
	private int				nAlpha = 120;
	private int				color;
	public PictureFactory(Context context, byte[] data, String markName , double latitude, double longtitude, int iconResId, String time, String userNo, String filePath, int mOrientationCompensation, boolean isAlpha){
		this.context = context;
		this.data = data;
		this.longtitude = longtitude;
		this.latitude = latitude;
		this.iconResId = iconResId;
		this.filePath = filePath;
		this.mOrientationCompensation = mOrientationCompensation;
		this.time = time;
		this.userNo = userNo;
		this.isAlpha = isAlpha;
		
	}
	public PictureFactory(Context context, byte[] data, long dateTaken, int mOrientationCompensation, PictureManager.IstoragePictureListener mStorageCallback){
		this.context = context;
		this.data = data;
		this.mOrientationCompensation = mOrientationCompensation;

		getStorageData(mStorageCallback, dateTaken);
	}


	private void getStorageData(PictureManager.IstoragePictureListener mStorageCallback, long dateTaken){
		String title,directory,markName;
		int resId;
		double latitude,longtitude;
		boolean success,bAlpha = false;
		String CAMERA_IMAGE_BUCKET_NAME =
				Environment.getExternalStorageDirectory().toString()
				+ "/hcxw/Camera";
		Date date = new Date(dateTaken);
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
		String time = null;
		String userNo = null;
		int nColor = Color.YELLOW;

		if (mStorageCallback != null) {
			PictureParameters picParameter = mStorageCallback.setStorageParameters(dateTaken);
			title = picParameter.getTitle();
			directory = picParameter.getDirectory();
			markName = picParameter.getMarkName();
			resId = picParameter.getResId();
			latitude = picParameter.getLatitude();
			longtitude = picParameter.getLongtitude();
			time = picParameter.getTime();
			userNo =picParameter.getUserNo(); 
			bAlpha = picParameter.isAlpha();
			nColor = picParameter.getColor();
			success = true;

			if(title==null||"".equals(title))  dateFormat.format(date);
			if(directory ==null||"".equals(directory)) directory = CAMERA_IMAGE_BUCKET_NAME;
			if(markName ==null||"".equals(markName)) markName = "";
			if(resId ==0) resId = -1;
			if (resId !=0&&resId !=-1) {
				Drawable dra = context.getResources().getDrawable(resId);
				if (dra ==null) {
					resId = -1;
				}
			}
			if (time == null||time.equals("")) {
				SimpleDateFormat date2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a");
				time = date2.format(Calendar.getInstance().getTime());
			}
			if (userNo ==null) {
				userNo = "";
			}

		}else {

			title =  dateFormat.format(date);

			directory = CAMERA_IMAGE_BUCKET_NAME;
			markName = "";
			resId =-1;
			latitude = 0;
			longtitude = 0;
			success = false;
			
		}
		String filename = title+".jpg";
		if (!new File(directory).exists()) {
			new File(directory).mkdirs();
		}
		String filePath = directory + filename;


		this.longtitude = longtitude;
		this.latitude = latitude;
		this.iconResId = resId;
		this.filePath = filePath;
		this.time = time;
		this.userNo = userNo;
		this.isAlpha = bAlpha;
		this.color = nColor;
	}

	private void drawText(Canvas canvasTemp , String text, int x, int y, int size)
	{
		Paint p = new Paint();
		String fontS = context.getResources().getString(R.string.font_style);
		String familyName = fontS;
		Typeface font = Typeface.create(familyName, Typeface.BOLD);
		p.setColor(Color.TRANSPARENT);
		p.setTypeface(font);
		p.setTextSize(size);
//		p.setShadowLayer(5, 0, 0, Color.WHITE);
		canvasTemp.drawText(text, x-1, y-1, p);
		canvasTemp.drawText(text, x-1, y, p);
		canvasTemp.drawText(text, x+1, y, p);
		canvasTemp.drawText(text, x+1, y+1, p);
		p.setColor(this.color);
		canvasTemp.drawText(text, x, y, p);
	}


	public Bitmap drawPicture(int w, int h){
		Bitmap cameraBitmap = BitmapFactory.decodeByteArray(data, 0,data.length);
//		return cameraBitmap;

//		switch (mOrientationCompensation) {
//		case 90:
//			cameraBitmap = Util.rotate(cameraBitmap, 90);
//			int temp;
//			temp =w;
//			w = h;
//			h = temp;
//			break;
//		case 180:
//			cameraBitmap = Util.rotate(cameraBitmap, 180);
//			break;
//		case 270:
//			cameraBitmap = Util.rotate(cameraBitmap, 270);
//			temp =w;
//			w = h;
//			h = temp;
//			break;
//
//		default:
//			break;
//		}

		String error = context.getResources().getString(R.string.GPS_fail);

		int rightMarginTop = 30;
		Bitmap outfile = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvasTemp = new Canvas(outfile);
		canvasTemp.drawColor(Color.YELLOW);
		canvasTemp.drawBitmap(Bitmap.createScaledBitmap(cameraBitmap,w, h,false), 0,0, null);
		//		SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a");
		//		String time = date.format(Calendar.getInstance().getTime());
		drawText(canvasTemp,time,w-220,h-rightMarginTop+20,18);
		if(latitude !=0){
			String m_lo = longtitude+"";
			String m_la = latitude+"";
			String GPS = "(" + m_lo + "," + m_la +")";
			drawText(canvasTemp,GPS,w-220,h-rightMarginTop-5,18);

		}else{
			drawText(canvasTemp,error,w-220,h-rightMarginTop-5,18);
		}
		Bitmap logo = null;
		if (iconResId !=-1) {
			InputStream is = context.getResources().openRawResource(iconResId);   //添加log 图标
			BitmapDrawable bmpDraw = new BitmapDrawable(is);
			logo = bmpDraw.getBitmap();
			int scale = logo.getWidth()/logo.getHeight();
			int height = 32;
			logo = Bitmap.createScaledBitmap(logo, height*scale ,height , false);
			try {
				is.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}					
			//			canvasTemp.drawBitmap(logo, w - logo.getWidth() - 250,h -50, null);
			Paint vPaint = new Paint();
			if (isAlpha) {
	            vPaint .setStyle( Paint.Style.STROKE );
	            vPaint .setAlpha( nAlpha );   // Bitmap透明度(0 ~ 100)
			}
			canvasTemp.drawBitmap(logo, 15,h-40, vPaint);
		}

		if (userNo!=null) {
			drawText(canvasTemp,userNo,15,h-50,18);
		}


		canvasTemp.save( Canvas.ALL_SAVE_FLAG );//保存属性
		canvasTemp.restore(); 

		return outfile;
	}

	public boolean savePicture(Bitmap outFileBitmap) throws FileNotFoundException {
		boolean suc = true;

		//保存文件
		File myCaptureFile = new File(filePath);

		BufferedOutputStream bos;
		bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
		outFileBitmap.compress(Bitmap.CompressFormat.JPEG,90, bos);
		try {
			bos.flush();
			bos.close();
		} catch (IOException e) {
			suc = false;
			e.printStackTrace();
		}

		return suc;
	}

	public String getCurrentFilePath(){
		return filePath;
	}


}
