package cx.mobilechecksh.mvideo.camera.picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;



import java.util.List;

import cx.mobilechecksh.R;
import cx.mobilechecksh.mvideo.camera.ui.PhotoView;

public class SamplePagerAdapter extends PagerAdapter {
	private List<String> imageList;
	private Context context;
	public SamplePagerAdapter(Context context, List<String> imageList){
		this.imageList = imageList;
		this.context = context;
	}
	@Override
	public int getCount() {
		return imageList.size();
	}

	@Override
	public View instantiateItem(ViewGroup container, int position) {
		PhotoView photoView = new PhotoView(container.getContext());
		Bitmap bmp= null;
		if (imageList.get(position).endsWith(".mp4")) {
//			//kind：　　文件种类，可以是 MINI_KIND 或 MICRO_KIND 2.2提供的接口
//			bmp = ThumbnailUtils.createVideoThumbnail(imageList.get(position),Thumbnails.FULL_SCREEN_KIND);
			bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.camera_image_video);
		}else{
			bmp = BitmapFactory.decodeFile(imageList.get(position));
		}
		photoView.setImageBitmap(bmp);
		// Now just add PhotoView to ViewPager and return it
		container.addView(photoView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		return photoView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		PhotoView photoView = (PhotoView)object;
		photoView.setImageBitmap(null);
//		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

}
