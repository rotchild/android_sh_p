package cx.mobilechecksh.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cx.mobilechecksh.R;
import cx.mobilechecksh.data.DBModle;

/**
 * Created by cx on 2017/8/22.
 */

public class CurrentTaskAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<ContentValues> mData;
    private LayoutInflater mInflater;
    private int mResourceID=-1;
    /**
     * 两种类型任务
     */
    private static final int TYPE_WAIT = 0;
    private static final int TYPE_DEAL=1;
    private static final int TYPECOUNT=2;
    /**
     * 页面组件
     */

    private onItemCallListener mItemCallListener;

    ViewHolder_wait holder_wait=null;
    ViewHolder_deal holder_deal=null;


    public CurrentTaskAdapter(Context mContext, ArrayList<ContentValues> mData){
        super();
        this.mContext=mContext;
        this.mData=mData;
        this.mInflater=LayoutInflater.from(mContext);
    }
    public interface onItemCallListener{
        public void onCallClick(int i);
    }

    public void setOnItemCallListener(onItemCallListener itemCallListener){
        this.mItemCallListener=itemCallListener;
    }



    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        int type=getItemViewType(position);
        if(convertView==null){
            switch (type){
                case TYPE_WAIT:
                    convertView=mInflater.inflate(R.layout.item_currenttask_wait,null);
                    holder_wait=new ViewHolder_wait();
                    holder_wait.caseNo=(TextView)convertView.findViewById(R.id.caseno_val);
                    holder_wait.carNo= (TextView) convertView.findViewById(R.id.carno_val);
                    holder_wait.createTime=(TextView)convertView.findViewById(R.id.addtime_val);
                    //holder_wait.carType=(TextView)convertView.findViewById(R.id.cartype_val);
                    holder_wait.caseState=(TextView)convertView.findViewById(R.id.casestate_val);

                    holder_wait.deleteLayout=(RelativeLayout)convertView.findViewById(R.id.delete_layout);
                    holder_wait.deleteLayout.setTag(position);
                    convertView.setTag(holder_wait);
                    break;
                case TYPE_DEAL:
                    convertView=mInflater.inflate(R.layout.item_currenttask_deal,null);
                    holder_deal=new ViewHolder_deal();
                    holder_deal.caseNo=(TextView)convertView.findViewById(R.id.caseno_val_d);
                    holder_deal.carNo=(TextView)convertView.findViewById(R.id.carno_val_d);
                    holder_deal.createTime=(TextView)convertView.findViewById(R.id.addtime_val_d);
                   // holder_deal.carType=(TextView)convertView.findViewById(R.id.carno_val_d);
                    holder_deal.caseState=(TextView)convertView.findViewById(R.id.casestate_val_d);
                    holder_deal.dinsuner=(TextView)convertView.findViewById(R.id.dinsuner_val);

                    holder_deal.takephotoLayout=(LinearLayout)convertView.findViewById(R.id.takephoto_layout);
                    holder_deal.callLayout=(LinearLayout)convertView.findViewById(R.id.call_layout);
                    holder_deal.callLayout.setTag(position);
                    convertView.setTag(holder_deal);
                    break;
                default:
                    break;
            }
        }else{
            switch (type){
                case TYPE_WAIT:
                    holder_wait= (ViewHolder_wait) convertView.getTag();
                    break;
                case TYPE_DEAL:
                    holder_deal= (ViewHolder_deal) convertView.getTag();
                    break;
            }

        }
        //设置资源
        switch (type){
            case TYPE_WAIT:
                holder_wait.caseNo.setText(mData.get(position).getAsString(DBModle.Task.CaseNo));
                holder_wait.createTime.setText(mData.get(position).getAsString(DBModle.Task.CreateTime));
                holder_wait.carNo.setText(mData.get(position).getAsString(DBModle.Task.CarMark));
               // holder_wait.carType.setText(mData.get(position).getAsString(DBModle.Task.CarType));
                holder_wait.caseState.setText(mData.get(position).getAsString(DBModle.Task.CaseState));
                break;
            case TYPE_DEAL:
                holder_deal.caseNo.setText(mData.get(position).getAsString(DBModle.Task.CaseNo));
                holder_deal.createTime.setText(mData.get(position).getAsString(DBModle.Task.CreateTime));
                holder_deal.carNo.setText(mData.get(position).getAsString(DBModle.Task.CarMark));
                //holder_deal.carType.setText(mData.get(position).getAsString(DBModle.Task.CarType));
                holder_deal.caseState.setText(mData.get(position).getAsString(DBModle.Task.CaseState));
                holder_deal.dinsuner.setText(mData.get(position).getAsString(DBModle.Task.CaseState));
                holder_deal.callLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            mItemCallListener.onCallClick(position);
                    }
                });
                break;
            default:
                break;
        }
        return convertView;
    }

    /**
     * 存在两种不同布局：待定损，定损中
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if(mData.get(position).getAsString(DBModle.Task.CaseState).equals("0")){
            return TYPE_WAIT;
        }else{
            return TYPE_DEAL;
        }
    }

    @Override
    public int getViewTypeCount() {
        return TYPECOUNT;
    }

    public void updateData(ArrayList<ContentValues> _data){
        mData=_data;
        notifyDataSetChanged();
    }

    /**
     * 设置删除事件
     * @param listener
     */
    public void setDeleteListener(View.OnClickListener listener){
        holder_wait.deleteLayout.setOnClickListener(listener);
    }

    /**
     * 设置拍照
     * @param listener
     */
    public void setTakePhotoListener(View.OnClickListener listener){
        holder_deal.takephotoLayout.setOnClickListener(listener);
    }


    public class ViewHolder_wait{
        //TextView caseNo,createTime,carNo,carType,caseState;
        TextView caseNo,createTime,carNo,caseState;
        RelativeLayout deleteLayout;
    }

    public class ViewHolder_deal{
        //TextView caseNo,createTime,carNo,carType,caseState,dinsuner;
        TextView caseNo,createTime,carNo,caseState,dinsuner;
        LinearLayout takephotoLayout,callLayout;
    }


}
