package cx.mobilechecksh.mvideo.camera.picture;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cx.mobilechecksh.R;
import cx.mobilechecksh.mvideo.camera.CaptureActivityHandler;
import cx.mobilechecksh.mvideo.camera.ui.MyViewPager;

public class PictureManager {

	
	private Context context;
	private byte[]				data;
	private static String currentFile = null;
	public static 				PictureManager mPictureManager; 
	private static IstoragePictureListener mStorageCallback;
	public static final String CAMERA_IMAGE_BUCKET_NAME =
			Environment.getExternalStorageDirectory().toString()
			+ "/hcxw/Camera";
	private final int				MESSAGE_UPDATE_THUM= 11;
	private final int				MESSAGE_SAVE_PIC_FAIL = 2;
	private PictureManager(Context context){
		this.context = context;
	}

	public static PictureManager initInstance(Context context){
		if (mPictureManager == null) {
			mPictureManager = new PictureManager(context.getApplicationContext());
		}
		return mPictureManager;
	}

	public static PictureManager getPictureManagerInstance(){
		return mPictureManager;
	}
	
	public interface IstoragePictureListener {

		/**
		 * 在存储照片前设置照片的参数
		 * 关键KEY :String TITLE(照片名字)，String DIRECTORY(路径)，String MARK(水印名称)，
		 * double LATITUDE(维度)，double LONGTITUDE(经度),int RESID(水印icon)
		 * @return PictureParameters
		 */
		public PictureParameters setStorageParameters(long dateTaken);
		
		public void afterStorage(String path, boolean success);
		
	}
	
	public IstoragePictureListener getIstoragePictureCallback(){
		return mStorageCallback;
	}

	public void setStorageCallback(IstoragePictureListener mStorageCallback){
		this.mStorageCallback = mStorageCallback;
	}


	public boolean  storage(byte[] data, CaptureActivityHandler handler, long dateTaken, int mOrientationCompensation){
		this.data = data;
		if (mStorageCallback != null&&data!=null) {
			mStorageCallback.setStorageParameters(dateTaken);
			PictureFactory factory = new PictureFactory(context, data,dateTaken,mOrientationCompensation,mStorageCallback);
			PictureSave saveThread = new PictureSave(factory,handler,mStorageCallback);
			saveThread.save();
		}else {
			handler.sendEmptyMessage(MESSAGE_SAVE_PIC_FAIL);
		}
		
		return false;

	}
	public Bitmap getThumPicture()
	{
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
				data.length);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, 70, 70);
		//		bitmap = ShareUtils.getRoundedCornerBitmap(bitmap);
		return bitmap;
	}

	public Bitmap getPicture(){
		BitmapFactory bmf = new BitmapFactory();
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			if (currentFile !=null)
			{
				fis = new FileInputStream(currentFile);
				bitmap = bmf.decodeStream(fis);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return bitmap;
	}
	
	/**
	 * 
	 * @param imageList 所有照片的路径集合
	 * @param context 上下文对象
	 * @param currentPicturePath 当前要展示的照片路径
	 * @param position 当前照片在页面中的实际位置（因为有可能不同的adapter加载出的位置是不一样的），默认值为-1
	 */
	public static void showPicture(ArrayList<String > imageList, Context context, String currentPicturePath, int position){
		if (currentPicturePath != null) {
			if (!imageList.contains(currentPicturePath)) {
				Log.e(context.getPackageName(), "this currentPicturePath is not in this imageList");
			}
			
			for (int i = 0; i < imageList.size(); i++) {
				if(currentPicturePath.equals(imageList.get(i)))
					if (position !=-1) {
						showPicture(imageList, context, position);
					}else {
						showPicture(imageList, context, i);
					}
					
			}
		}else {
			showPicture(imageList, context, 0);
			Log.e(context.getPackageName(), "this currentPicturePath is null");
		}
		
	}
	
	/**
	 * 
	 * @param directory 照片的文件夹
	 * @param context
	 * @param currentPicturePath 当前要展示的照片路径 
	 * @param position 当前照片在页面中的实际位置（因为有可能不同的adapter加载出的位置是不一样的）,默认-1
	 */
	public static void showPicture(String directory, Context context, String currentPicturePath, int position){
		File directoryFile = new File(directory);
		if (directoryFile.exists()) {
			if(directoryFile.listFiles().length==0) return;
			File[] files = directoryFile.listFiles();
			ArrayList<String> imageList = new ArrayList<String>();
			for (int i = 0; i < files.length; i++) {
				imageList.add(files[i].getAbsolutePath());
				
			}
			showPicture(imageList, context, currentPicturePath,position);
		}
	}
	
	/**
	 * 
	 * @param imageList 
	 * @param context
	 * @param position
	 */
	public static void showPicture(final ArrayList<String > imageList, Context context, int position){
		View layout = LayoutInflater.from(context).inflate(R.layout.camera_photo_preview, null);

		final Dialog dialog = new Dialog(context, R.style.camera_photopreview);
		dialog.setContentView(layout);

		Window dialogWindow = dialog.getWindow();
		WindowManager m =((Activity)context).getWindowManager();
		dialogWindow.setWindowAnimations(R.style.camera_photopreview); //设置窗口弹出动画
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); // 获取对话框当前的参数值
		p.height = (int) (d.getHeight() * 1); 
		p.width = (int) (d.getWidth()*1); 

		dialogWindow.setAttributes(p);



		RelativeLayout titleBar = (RelativeLayout)layout. findViewById(R.id.camera_preview_TitleBar);
		LinearLayout linearLayout = (LinearLayout) layout.findViewById(R.id.camera_photo_preview_backbtn_linear);
		ImageView backBtn = (ImageView) layout.findViewById(R.id.camera_photo_preview_backbtn);
		ImageView deleBtn = (ImageView)layout. findViewById(R.id.camera_photo_preview_delete);
		TextView title = (TextView) layout.findViewById(R.id.camera_photo_preview_title);
		final TextView indicator = (TextView) layout.findViewById(R.id.camera_photo_preview_indicator);
		
		LinearLayout container = (LinearLayout) layout.findViewById(R.id.camera_photo_preview_LinearLayout);
		ViewPager mViewPager = new MyViewPager(context);
		container.addView(mViewPager);

		deleBtn.setVisibility(View.INVISIBLE);

		linearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		if (imageList.size()>0)
		{
			mViewPager.setAdapter(new SamplePagerAdapter(context,imageList));

			if (position>0&&position<imageList.size()) {
				mViewPager.setCurrentItem(position);
				indicator.setText((position+1)+"/"+imageList.size());
			}else {
				indicator.setText(1+"/"+imageList.size());
			}
			
		}
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				indicator.setText((arg0+1)+"/"+imageList.size());
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		dialog.show();
	}

	/**
	 * 传入一张照片浏览
	 * @param path 照片路径
	 * @param context
	 */
	public static void showPicture(String path, Context context){
		if (path !=null) {
			File directory = new File(path);
			if (!directory.isDirectory()) {
				if (!directory.mkdirs()) {
					ArrayList<String> imageList = new ArrayList<String>();
					imageList.add(path);
					showPicture(imageList,context,0);
				}else {
					Toast.makeText(context, "传入文件不可用", Toast.LENGTH_LONG).show();
				}
			}

		}

	}
	public static void showPictureCurrent(Context mContext, String currentPath){
		if (currentPath!=null) {
			
			ArrayList<String> imageList = new ArrayList<String>();
			imageList.add(currentPath);
			showPicture(imageList,mContext,-1);
		}

	}
	
	public void release(){
		if (mPictureManager != null) {
			mPictureManager = null;
		}
	}


}
