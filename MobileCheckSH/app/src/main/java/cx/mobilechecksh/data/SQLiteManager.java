package cx.mobilechecksh.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by cx on 2017/8/21.
 */

public class SQLiteManager {
    private static SQLiteManager instance=null;
    private DatabaseHelper mDBHelper;
    private final String mDBNAME = "MobileCheckSH.db";
    private final int mDBVERSION = 1;
    private SQLiteDatabase mDB;
    static Context sContext;
    public SQLiteManager(){};

    /**
     * 单例模式
     * @return
     */
    public static SQLiteManager getInstance(){
        if(instance==null){
            instance=new SQLiteManager();
            instance.trueInit(sContext);
        }
        return instance;
    }

    /**
     * 初始化数据资源
     * @param context
     * @return
     */
    static public SQLiteManager initContext(Context context){
        sContext=context;
        return null;
    }

    /**
     * 打开数据库
     * @param context
     * @return
     */
    public SQLiteManager trueInit(Context context){
        mDBHelper=new DatabaseHelper(context.getApplicationContext());
        open();
        return instance;
    }

    private void open() {
        //Log.e("getWritableDatabase  Open() front", G.getPhoneCurrentTime());
        mDB = mDBHelper.getWritableDatabase();
        //Log.e("getWritableDatabase  Open() back", G.getPhoneCurrentTime());
    }



    class DatabaseHelper extends SQLiteOpenHelper{
        public  DatabaseHelper(Context context){
            super(context, mDBNAME, null, mDBVERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
