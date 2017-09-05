package cx.mobilechecksh.adapters;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by cx on 2017/8/15.
 */

public class MainPageAdapter extends PagerAdapter{
    private ArrayList<View> mArrayList;
    public MainPageAdapter(ArrayList<View> arrayListData){
        this.mArrayList=arrayListData;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager) container).addView((View) mArrayList.get(position), 0);
        return (View) mArrayList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        ((ViewPager) container).removeView((View) mArrayList
                .get(position));
    }

    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }
}
