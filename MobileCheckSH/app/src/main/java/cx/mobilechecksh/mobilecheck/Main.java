package cx.mobilechecksh.mobilecheck;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cx.mobilechecksh.R;
import cx.mobilechecksh.adapters.CurrentTaskAdapter;
import cx.mobilechecksh.adapters.MainPageAdapter;
import cx.mobilechecksh.data.DBModle;
import cx.mobilechecksh.data.DataHandler;
import cx.mobilechecksh.global.G;
import cx.mobilechecksh.mvideo.camera.CameraMain;
import cx.mobilechecksh.net.HttpResponseHandler;
import cx.mobilechecksh.theme.MBaseActivity;
import cx.mobilechecksh.ui.Dialog_NewTask;
import cx.mobilechecksh.ui.PullDownListView;
import cx.mobilechecksh.utils.MRegex;
import cx.mobilechecksh.utils.MToast;
import cx.mobilechecksh.utils.UserManager;

public class Main extends MBaseActivity implements ViewPager.OnPageChangeListener,PullDownListView.OnRefreshListioner
{
    Context mContext;
    private View mCurLayout,mHisLayout,mAddress,mMessage;

    private ArrayList<View> mArrayList;
    private ViewPager mViewPager;
    public static final String PREFERENCES_TASK = "preferences_task";
    /**
     * 服务器返回数据
     */
    private ArrayList<ContentValues> mArrayList_data=new ArrayList<ContentValues>();
    private ArrayList<ContentValues> allTask_data=new ArrayList<ContentValues>();
    private JSONArray mAllData_JsonArray;

    /**
     * 当前页面标识
     */
    private int currentPage=0;
    public static final int CURRENT=0;
    public static final int HISTORY=1;
    public static final int ADDRESS=2;
    public static final int MESSAGE=3;
    /**
     * 数据处理
     */
    private DataHandler mDataHandler;
    private ArrayList<ContentValues> mCurrentData_list = new ArrayList<ContentValues>();
    private ArrayList<ContentValues> mHisData_list = new ArrayList<ContentValues>();
    private ArrayList<ContentValues> mAddressData_list = new ArrayList<ContentValues>();

    private CurrentTaskAdapter mCurrentTaskAdapter=null;
    /**
     * 登录用户信息
     */
    private String mUserName;
    /**
     * 组件
     */
    //底部
    ImageView cur_task_img,his_task_img,address_task_img,message_task_img;
    TextView cur_task_tv,his_task_tv,address_task_tv,message_task_tv;
    LinearLayout cur_task_tab,his_task_tab,address_task_tab,message_task_tab;
    //顶部
    TextView tasktitle;
    ImageView newTaskIV,filtTaskIV;
    //listView

    //任务相关
    Dialog_NewTask newTaskDg=null;

    private View.OnClickListener clickListener=null;

    private PullDownListView mCurPullLV,mHisPullLV,mAddressPullLV,mMessagePullLV;
    private ListView mCurLV,mHisLV,mAddressLV,mMessageLV;

    //摄像
    Button callVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("main","onCreate enter");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;
        init();
    }

    private void init() {
        mDataHandler=new DataHandler(mContext);
       // mUserName= UserManager.getInstance().getUserName();
        initView();
        setPage();
    }

    private void initView() {
        callVideo=(Button)findViewById(R.id.toVideo);
        callVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toVideo=new Intent(mContext, CameraMain.class);
                startActivity(toVideo);
            }
        });

        mViewPager=(ViewPager)findViewById(R.id.mViewPager);
        //底部组件
        cur_task_img=(ImageView)findViewById(R.id.cur_task_img);
        his_task_img=(ImageView)findViewById(R.id.his_task_img);
        address_task_img=(ImageView)findViewById(R.id.address_task_img);
        message_task_img=(ImageView)findViewById(R.id.message_task_img);

        cur_task_tv=(TextView)findViewById(R.id.cur_task_tv);
        his_task_tv=(TextView)findViewById(R.id.his_task_tv);
        address_task_tv=(TextView)findViewById(R.id.address_task_tv);
        message_task_tv=(TextView)findViewById(R.id.message_task_tv);
        //顶部组件
        tasktitle=(TextView)findViewById(R.id.tasktitle);
        newTaskIV=(ImageView)findViewById(R.id.newtask_img);
        newTaskIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewTaskDialog();
            }
        });

        filtTaskIV=(ImageView)findViewById(R.id.filttask_img);

/*        clickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.newtask_img:
                        showNewTaskDialog();
                        break;
                    default:
                        break;
                }
            }
        };*/
    }

    private void setPage(){
    mArrayList=new ArrayList<View>();
        LayoutInflater inflater=LayoutInflater.from(mContext);
        mCurLayout=inflater.inflate(R.layout.view_curtask,null);
        mArrayList.add(mCurLayout);
        mCurPullLV=(PullDownListView)mCurLayout.findViewById(R.id.current_pulldown_list);
        mCurPullLV.setRefreshListioner(this);

        mHisLayout=inflater.inflate(R.layout.view_histask,null);
        mArrayList.add(mHisLayout);
        mHisPullLV=(PullDownListView)mHisLayout.findViewById(R.id.his_pulldown_list);

        mAddress=inflater.inflate(R.layout.view_address,null);
        mArrayList.add(mAddress);
        mAddressPullLV=(PullDownListView)mAddress.findViewById(R.id.address_pulldown_list);

        mMessage=inflater.inflate(R.layout.view_message,null);
        mArrayList.add(mMessage);
        mMessagePullLV= (PullDownListView) mMessage.findViewById(R.id.message_pulldown_list);

        MainPageAdapter mPageAdapter=new MainPageAdapter(mArrayList);
        mViewPager.setAdapter(mPageAdapter);
        mViewPager.setOnPageChangeListener(this);

        //listView
        mCurLV=(ListView)mCurLayout.findViewById(R.id.current_task_list);
        mHisLV=(ListView)mHisLayout.findViewById(R.id.his_task_list);
        mAddressLV=(ListView)mAddress.findViewById(R.id.address_list);
        mMessageLV=(ListView)mMessage.findViewById(R.id.message_task_list);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPage=position;
        resetBotView();
    switch (position){
        case CURRENT:
            Log.d("main","current");
            setSelectStyle(CURRENT);
            break;
        case HISTORY:
            Log.d("main","history");
            setSelectStyle(HISTORY);
            break;
        case ADDRESS:
            Log.d("main","address");
            setSelectStyle(ADDRESS);
            break;
        case MESSAGE:
            Log.d("main","message");
            setSelectStyle(MESSAGE);
            break;
        default:
            break;
    }
    }

    /**
     * 重置底部样式
     */
    public void resetBotView(){
        cur_task_img.setImageResource(R.drawable.ic_launcher);
        his_task_img.setImageResource(R.drawable.ic_launcher);
        address_task_img.setImageResource(R.drawable.ic_launcher);
        message_task_img.setImageResource(R.drawable.ic_launcher);

        cur_task_tv.setTextColor(mContext.getResources().getColor(R.color.defaulttext_black));
        his_task_tv.setTextColor(mContext.getResources().getColor(R.color.defaulttext_black));
        address_task_tv.setTextColor(mContext.getResources().getColor(R.color.defaulttext_black));
        message_task_tv.setTextColor(mContext.getResources().getColor(R.color.defaulttext_black));
    }

    /**
     * 设置选中样式
     * @param position
     */
    public void setSelectStyle(int position){
        switch (position){
            case CURRENT:
                tasktitle.setText(R.string.current_task_title);

                cur_task_img.setImageResource(R.drawable.ic_launcher);
                cur_task_tv.setTextColor(mContext.getResources().getColor(R.color.selecttext_red));
                break;
            case HISTORY:
                tasktitle.setText(R.string.his_task_title);

                his_task_img.setImageResource(R.drawable.ic_launcher);
                his_task_tv.setTextColor(mContext.getResources().getColor(R.color.selecttext_red));
                break;
            case ADDRESS:
                tasktitle.setText(R.string.address_task_title);

                address_task_img.setImageResource(R.drawable.ic_launcher);
                address_task_tv.setTextColor(mContext.getResources().getColor(R.color.selecttext_red));
                break;
            case MESSAGE:
                tasktitle.setText(R.string.message_task_title);

                message_task_img.setImageResource(R.drawable.ic_launcher);
                message_task_tv.setTextColor(mContext.getResources().getColor(R.color.selecttext_red));
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onRefresh() {
        Log.e("main","onRefresh enter");
        getData();
    }

    @Override
    public void onLoadMore() {

    }

    /** 获取数据 */
    public void getData() {
        Log.e("main","getData enter");
        mDataHandler.setmIsShowProgressDialog(false);
/*        String stationId,String taskNo,String carNo,String keyword,
                String startDate,String endDate,String status*/
        String stationId=UserManager.getInstance().getStationId();
        String taskNo="";
        String carNo="";
        String keyword="";
        String startDate="";
        String endDate="";
        String status="";

        mDataHandler.getCurrentTaskList(stationId,taskNo,carNo,keyword,startDate,
                endDate,status,currentHttpResponseHandler);
    }

    HttpResponseHandler currentHttpResponseHandler=new HttpResponseHandler(){
        @Override
        public void response(boolean success, String response, Throwable error) {
            Log.e("main","response enter");
            if(success){
                mArrayList_data=new ArrayList<ContentValues>();
                allTask_data=new ArrayList<ContentValues>();
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    //根据返回数据调整
                    mAllData_JsonArray=jsonObject.getJSONArray("data");
                    JSONObject object;
                    ContentValues values;
                    for(int i=0;i<mAllData_JsonArray.length();i++){
                        values=new ContentValues();
                        object= (JSONObject) mAllData_JsonArray.get(i);
                        values.put(DBModle.Task.CaseNo,object.getString(DBModle.Task.CaseNo));
                        values.put(DBModle.Task.CarMark,object.getString(DBModle.Task.CarMark));
                        values.put(DBModle.Task.CaseState,object.getString(DBModle.Task.CaseState));
                        values.put(DBModle.Task.CreateTime,object.getString(DBModle.Task.CreateTime));
                        values.put(DBModle.Task.CaseState,object.getString(DBModle.Task.CaseState));
                        values.put(DBModle.Task.DSName,object.getString(DBModle.Task.DSName));
                        values.put(DBModle.Task.USName,object.getString(DBModle.Task.USName));
                        values.put(DBModle.Task.DSMobile,object.getString(DBModle.Task.DSMobile));

                        mArrayList_data.add(values);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("main","exception");
                }
            }else{
                G.showToast(mContext, getString(R.string.network_err), true);
            }
            //Log.e("main","before split");
            if(mArrayList_data.size()<=0){//虚拟数据测试用
                Log.e("main","mArrayList_data size enter");
                ContentValues values=new ContentValues();
                values.put(DBModle.Task.CaseNo,"PDZA0000000000010002");
                values.put(DBModle.Task.CarMark,"沪A33000");
                values.put(DBModle.Task.CreateTime,"2017-7-18 12:24:36");
                //values.put(DBModle.Task.CarType,"一汽大众");
                values.put(DBModle.Task.CaseState,"待定损");
                mArrayList_data.add(values);

                ContentValues values1=new ContentValues();
                values1.put(DBModle.Task.CaseNo,"PDZA0000000000010320");
                values1.put(DBModle.Task.CarMark,"沪A68520");
                values1.put(DBModle.Task.CreateTime,"2017-7-25 10:22:36");
                values1.put(DBModle.Task.CaseState,"定损中");
                values1.put(DBModle.Task.DSName,"李强");
                mArrayList_data.add(values1);

            }
            //splitData(mArrayList_data,allTask_data);
            mSplitData(mArrayList_data);
        }
    };

    /**
     * 分发数据，并且适配页面
     * @param serviceData 服务器返回数据
     * @param outAllData 对比缓存后的数据，增加isNew
     */
    public void splitData(ArrayList<ContentValues> serviceData,
                          ArrayList<ContentValues> outAllData){
        mCurPullLV.onRefreshComplete();
        mHisPullLV.onRefreshComplete();
        mAddressPullLV.onRefreshComplete();
        mMessagePullLV.onRefreshComplete();
        mCurrentData_list = new ArrayList<ContentValues>();

        try {
            SharedPreferences preferencesCase=mContext.getSharedPreferences(PREFERENCES_TASK,Context.MODE_PRIVATE);
            String cacheCase=preferencesCase.getString("task","[]");
            //适配数据之前，和上次缓存的数据进行对比，是否有更新数据
            JSONArray cacheCaseArray=new JSONArray(cacheCase);
            for(int i=0;i<serviceData.size();i++){
                ContentValues contentValues=serviceData.get(i);
                int flagIndex=-1;
                for(int j=0;j<cacheCaseArray.length();j++){
                    JSONObject caseJson=cacheCaseArray.getJSONObject(j);
                    if(contentValues.getAsString(DBModle.Task.CaseNo).equals(
                        caseJson.getString(DBModle.Task.CaseNo)
                    )&&contentValues.getAsString(DBModle.Task.CarMark).equals(
                            caseJson.getString(DBModle.Task.CarMark))){
                        flagIndex=j;
                        break;
                    }
                }
                if(flagIndex==-1){
                    JSONObject caseJson=new JSONObject();
                    caseJson.put(DBModle.Task.CaseNo,contentValues.getAsString(DBModle.Task.CaseNo));
                    caseJson.put(DBModle.Task.CarMark,contentValues.getAsString(DBModle.Task.CarMark));
                    caseJson.put(DBModle.Task.IsNew,true);
                    cacheCaseArray.put(caseJson);

                    contentValues.put(DBModle.Task.IsNew,true);
                }else{
                    contentValues.put(DBModle.Task.IsNew,
                            cacheCaseArray.getJSONObject(flagIndex).getBoolean(DBModle.Task.IsNew));

                }
                outAllData.add(contentValues);

            }
            SharedPreferences preferences_task=mContext.getSharedPreferences(PREFERENCES_TASK,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences_task.edit();
            editor.putString("task",cacheCaseArray.toString());
            editor.commit();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCurrentTaskLayout(mCurLV,mArrayList_data);
    }


    public void mSplitData(ArrayList<ContentValues> serviceData){

    }

    /**
     * 适配各种数据
     * @param listView
     * @param listdata
     */
    public void setCurrentTaskLayout(ListView listView, final ArrayList<ContentValues> listdata){
        mCurrentTaskAdapter=new CurrentTaskAdapter(mContext,listdata);
        listView.setAdapter(mCurrentTaskAdapter);
    }


    public void showNewTaskDialog(){
        newTaskDg=new Dialog_NewTask(mContext,R.style.NewTaskDialog);
        newTaskDg.initDialog();
        newTaskDg.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caseNo=newTaskDg.getCaseNo();
                String carNo=newTaskDg.getCarNo();

                createTask(caseNo,carNo);

                if(newTaskDg!=null&&newTaskDg.isShowing()){
                    newTaskDg.dismiss();
                }

            }
        });
        newTaskDg.show();
    }

    /**
     * 创建案件
     * @param caseNo 案件号
     * @param carNo 车牌号
     */
    public void createTask(String caseNo,String carNo){
        if(!MRegex.isRightCaseNO(caseNo)){
            G.showToast(mContext,mContext.getResources().getString(R.string.caseno_err),false);
            return;
        }
        if(!MRegex.isRightCarNO(caseNo)){
            G.showToast(mContext,mContext.getResources().getString(R.string.carno_err),false);
            return;
        }
        DataHandler dataHandler=new DataHandler(mContext);
        dataHandler.setmIsShowProgressDialog(true);
        String deviceNo= UserManager.getInstance().getDeviceNo();
        dataHandler.createTask(deviceNo,caseNo,carNo,createTaskHandler);
    }
    HttpResponseHandler createTaskHandler=new HttpResponseHandler(){
        @Override
        public void response(boolean success, String response, Throwable error) {
            //super.response(success, response, error);
            if(success){
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    boolean dataSuccess=jsonObject.getBoolean("success");
                    if(dataSuccess){
                        G.showToast(mContext,"success",false);
                    }else {
                    JSONObject err=jsonObject.getJSONObject("err");
                        String message=err.getString("message");
                        G.showToast(mContext,message,false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    G.showToast(mContext,mContext.getResources().getString(R.string.response_exception),false);
                }
            }else {
                G.showToast(mContext,mContext.getResources().getString(R.string.response_false),false);
            }
        }
    };

    /**
     * 请求视屏通信
     * @param userName
     */
    public void callForVideo(String userName){
DataHandler dataHandler=new DataHandler(mContext);
       dataHandler.setmIsShowProgressDialog(true);
        dataHandler.callForVideo(userName,mCallVideoResponse );
    }
    HttpResponseHandler mCallVideoResponse=new HttpResponseHandler(){
        @Override
        public void response(boolean success, String response, Throwable error) {
           // super.response(success, response, error);
            if(success){
                try{
                    JSONObject jsonObject=new JSONObject(response);
//......
                }catch (Exception e){
                    e.printStackTrace();
                    G.showToast(mContext,mContext.getResources().getString(R.string.response_exception),false);
                    return;
                }

            }else{
                G.showToast(mContext,mContext.getResources().getString(R.string.response_false),false);
            }
        }
    };


}
