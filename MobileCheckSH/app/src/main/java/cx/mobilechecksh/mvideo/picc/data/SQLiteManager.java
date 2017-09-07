package cx.mobilechecksh.mvideo.picc.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class SQLiteManager {
	private static SQLiteManager instance = null;
	private DatabaseHelper mDBHelper;
	private SQLiteDatabase mDB;
	private final String mDBNAME = "MobileCheck.db";
	private final int mDBVERSION = 4;
	private final String TAG = this.getClass().getSimpleName();
	private boolean result = false;

	SQLiteManager(){}

	/** 获取SQLite_Manager实例 ,单例模式 */
	public static SQLiteManager getInstance() {
		if (instance == null) {
			instance = new SQLiteManager();
		}		
		return instance;
	}	

	/**初始化数据资源*/
	public SQLiteManager init(Context context){
		mDBHelper = new DatabaseHelper(context.getApplicationContext());
		Open();
		return instance;
	}

	class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, mDBNAME, null, mDBVERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			try {
				// 任务短信
				String T_MessageSources = "create table if  not exists MessageSources( "
						+ "SMSID INTEGER PRIMARY KEY AUTOINCREMENT,Contents varchar(256),IsRead int,TID int"
						+ ")";
				db.execSQL(T_MessageSources);

				// 用户表
				//第五版，加上UserState字段
				String T_Users = "create table if not exists Users( "
						+ "UserName varchar(256) primary key,PassWord varchar(256),RealName varchar(256),LastLoginTime datetime,IsRemember int,UserClass int,AreaID int,"
						+"Email varchar(50),UserState varchar(20)"
						+")";
				db.execSQL(T_Users);


				// 地区表
				String T_Area = "create table if  not exists Areas("
						+ "AreaID int, ParentId int,AreaName varchar(256),AreaCode varchar(256)," +
						"AreaClass varchar(256)"
						+ ")";
				db.execSQL(T_Area);

				// 车型表
				String T_CarType = "create table if not exists CarType(CarTypeID int primary key, TypeName varchar(50))";
				db.execSQL(T_CarType);

				// 修理厂车型表
				String T_Garage = "create table if not exists Garage(" +
						"GarageID INTEGER PRIMARY KEY AUTOINCREMENT,ShortName varchar(100),CarType varchar(100)," +
						"Telephone varchar(50),Address varchar(200),Latitude varchar(50),Longitude varchar(50)," +
						"ImgPath varchar(300),MoneyLimit float(53),Memo text,AreaID int,PushMoney float(53)," +
						"UserName varchar(200),IsPartner int,Is4s int,IsSynthesize int,MonthDone float(53)," +
						"ReachRate float(53),FullName varchar(200)"
						+")";
				db.execSQL(T_Garage);

				// 任务表    (新增字段LinkCaseNo varchar(50),"
//				+"CaseType varchar(50),AutoGenerationNO varchar(50),CarType varchar(50),AccidentType varchar(50),InsuranceType varchar(50),InsuranceID int,"
//				+"InsuranceContactPeople varchar(50),InsuranceContactTelephone varchar(50),PaymentAmount varchar(50),PaymentMethod varchar(50),IsRisk int,"
//				+"AccidentDescribe text, ThirdCar varchar(50),AreaID int")
				String T_Task = "create table if  not exists Task("
						+ "TID INTEGER PRIMARY KEY AUTOINCREMENT,SMSID int,"
						+"CaseNo varchar(256),CarMark varchar(50),CarDriver varchar(50),CarOwner varchar(50),"
						+"Telephone varchar(256),Address varchar(512),ImgPath varchar(512),FrontPrice float(53)," 
						+"BackPrice float(53),FixedPrice float(53),Latitude float(53),Longitude float(53),"
						+"TaskID int,TaskType varchar(50),FrontOperator varchar(50),FrontState int," 
						+"BackOperator varchar(50),BackState int,Watcher varchar(50),StateUpdateTime datetime,"						
						+"Memo text,AccidentTime datetime,CarTypeID int,GarageID int,IsNew int,IsCooperation int,"
						
						+"LinkCaseNo varchar(50),CaseType varchar(50),AutoGenerationNO varchar(50),CarType varchar(50),AccidentType varchar(50),InsuranceType varchar(50),InsuranceID int,"
						+"InsuranceContactPeople varchar(50),InsuranceContactTelephone varchar(50),PaymentAmount varchar(50),PaymentMethod varchar(50),IsRisk int,"
						+"AccidentDescribe text, ThirdCar varchar(50),AreaID int"
						+ ")";

				db.execSQL(T_Task);
				
				String T_TaskLog = "create table if  not exists TaskLog("
						+ "TaskLogID INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ "TID int,FrontState int,Latitude float(53),Longitude float(53),StateUpdateTime datetime"
						+ ")";
				db.execSQL(T_TaskLog);

				// 任务堆栈表
				String T_TaskStack = "create table if  not exists TaskStack("
						+ "StackID INTEGER PRIMARY KEY AUTOINCREMENT,TID int,Action int,"
						+ "StackType int, RequestURL varchar(800),Keys varchar(800),Vals text,"
						+ "[Level] int,IsRequest int,Description text,Data text" 
						+ ")";
				db.execSQL(T_TaskStack);

				//杨璐  添加保险公司列表
				String T_Insurance = "create table if  not exists Insurance("
				        +"IID INTEGER PRIMARY KEY AUTOINCREMENT,InsuranceID int,InsuranceName varchar(50)," 
						+"InsuranceLink varchar(500),UserNames varchar(50)"
						+")";
				db.execSQL(T_Insurance);
				

				//字段信息表
				String T_Dict = "create table if  not exists Dict("
				        +"DID INTEGER PRIMARY KEY AUTOINCREMENT,DictID int,TableName varchar(50)," 
						+"FieldName varchar(50),FieldValue varchar(50),Description varchar(50),Memo varchar(50)"
						+")";
				db.execSQL(T_Dict);
				
				
				//RoleDicts
				String T_RoleDicts = "create table if  not exists RoleDicts("
				        +"RDID INTEGER PRIMARY KEY AUTOINCREMENT,RoleID int,RoleType varchar(50)," 
						+"RoleName varchar(50)"
						+")";
				db.execSQL(T_RoleDicts);
				
				
				Log.d(TAG, "onCreate database success");
			} catch (SQLException e) {
				Log.d(TAG,"onCreate database fail SQLException");
			}
			
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(TAG, "onUpgrade + oldVersion:"+oldVersion + " newVersion:"+newVersion);
			//对于第二版的软件升级，删除数据库
//			if (oldVersion <4) {
			if(oldVersion == 2&&newVersion == 3){
				if (oldVersion < newVersion) {
					db.execSQL("DROP TABLE IF EXISTS MessageSources");
					db.execSQL("DROP TABLE IF EXISTS Users");
					db.execSQL("DROP TABLE IF EXISTS Garage");
					db.execSQL("DROP TABLE IF EXISTS Areas");
					db.execSQL("DROP TABLE IF EXISTS CarType");
					db.execSQL("DROP TABLE IF EXISTS GarageCarType");
					db.execSQL("DROP TABLE IF EXISTS Task");
					db.execSQL("DROP TABLE IF EXISTS TaskStack");
					db.execSQL("DROP TABLE IF EXISTS TaskLog");
					onCreate(db);
				}
			}else if (oldVersion == 3&&newVersion == 4) {
				DataUpgradeHelper helper = new DataUpgradeHelper(db);
				
				 String creatNewTable_sql_task_colums_old;
				 String creatNewTable_sql_task_colums_new;
				 String creatNewTable_sql_task_colums_old_select;
				
				
				String tableName = "Task";
				creatNewTable_sql_task_colums_old_select = "TID ,SMSID ,"
						+"CaseNo ,CarMark ,CarDriver ,CarOwner ,"
						+"Telephone ,Address ,ImgPath ,FrontPrice ," 
						+"BackPrice ,FixedPrice ,Latitude ,Longitude ,"
						+"TaskID ,TaskType ,FrontOperator ,FrontState ," 
						+"BackOperator ,BackState ,Watcher ,StateUpdateTime ,"						
						+"Memo ,AccidentTime ,CarTypeID ,GarageID ,IsNew ,IsCooperation ,";
				
				
				creatNewTable_sql_task_colums_old = "TID INTEGER PRIMARY KEY AUTOINCREMENT,SMSID int,"
				+"CaseNo varchar(256),CarMark varchar(50),CarDriver varchar(50),CarOwner varchar(50),"
				+"Telephone varchar(256),Address varchar(512),ImgPath varchar(512),FrontPrice float(53)," 
				+"BackPrice float(53),FixedPrice float(53),Latitude float(53),Longitude float(53),"
				+"TaskID int,TaskType varchar(50),FrontOperator varchar(50),FrontState int," 
				+"BackOperator varchar(50),BackState int,Watcher varchar(50),StateUpdateTime datetime,"						
				+"Memo text,AccidentTime datetime,CarTypeID int,GarageID int,IsNew int,IsCooperation int,";
				
				creatNewTable_sql_task_colums_new = 
						"LinkCaseNo varchar(50),CaseType varchar(50),AutoGenerationNO varchar(50),CarType varchar(50),AccidentType varchar(50)," 
						+"InsuranceType varchar(50),InsuranceID int,InsuranceContactPeople varchar(50),InsuranceContactTelephone varchar(50),PaymentAmount varchar(50),"
						+"PaymentMethod varchar(50),IsRisk int,AccidentDescribe text, ThirdCar varchar(50),AreaID int";
						
				
				String sql_create_newTable = "create table if  not exists Task("
						+ creatNewTable_sql_task_colums_old
						+creatNewTable_sql_task_colums_new
						+ ")";
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < 15; i++) {
					if (i!=14) {
						sb.append("\"\"");
						sb.append(",");
					}else {
						sb.append("\"\"");
					}
					
				}
				String sql_import = "INSERT INTO "+tableName+" SELECT "+creatNewTable_sql_task_colums_old_select+sb.toString()+" FROM TABLE_TEMP_"+tableName;
				helper.upgradeDatabase(tableName, sql_create_newTable, sql_import);
				
				onCreate(db);
				
				
			}
			
		}
	}

	private void Open() {
		mDB = mDBHelper.getWritableDatabase();		
	}
	
	public void beginTransaction(){
		mDB.beginTransaction();
	}
	
	public void setTransactionSuccessful(){
		mDB.setTransactionSuccessful();		
	}
	
	public void endTransaction(){
		mDB.endTransaction();
	}
	


	/**关闭数据库*/
	public void Close(){
//		mDB.close();
	}

	/**
	 * 执行sql 语句
	 * 
	 * @return boolean ture or false
	 */
	public boolean ExecSQL(String SQL) {
		result = true;
		try {
			mDB.execSQL(SQL);			
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	/**
	 * 插入数据
	 * 
	 * @param String
	 *            table 表名
	 * @param ContentValues
	 *            数据
	 * */
	public long Insert(String table, ContentValues Values) {
		long rt=-1;
		try {
			rt= mDB.insert(table, null, Values);
		} catch (Exception e) {
			Log.d(TAG, e.toString());
		}
		return rt;
	}

	/**
	 * 更新数据
	 * 
	 * @param String
	 *            table 表名
	 * @param ContentValues
	 *            Values 数据
	 * @param String
	 *            selection where条件 没有可传null
	 * @param String
	 *            [] selectionArgs where参数 selection为null 此参数必须为null
	 * */
	public long Update(String table, ContentValues Values, String selection, String[] selectionArgs) {
		try {
			return mDB.update(table, Values, selection, selectionArgs);
		} catch (Exception e) {
			Log.d(TAG,"Update failed",e);
		}
		return -1;
	}

	/**
	 * 删除数据
	 * 
	 * @param String
	 *            table 表名
	 * @param String
	 *            selection where条件 没有可传null
	 * @param String
	 *            [] selectionArgs where参数 whereClause为null 此参数必须为null
	 * */
	public int Delete(String table, String selection, String[] selectionArgs) {
		try {
			return mDB.delete(table, selection, selectionArgs);
		} catch (Exception e) {
			Log.d(TAG,"Delete failed",e);
		}
		return -1;
	}

	/**
	 * 查询数据
	 * @param Table 表名
	 * @param columns 列名
	 * @param selection 条件
	 * @param selectionArgs  条件参数 
	 * @return List<ContentValues>查询结果数据数组
	 */
	public ArrayList<ContentValues> Select(String Table, String[] columns, String selection, String[] selectionArgs) {
		try {
			return Select(Table, columns, selection, selectionArgs, null, null,	null);
		} catch (Exception e) {
			return new ArrayList<ContentValues>();
		}
	}
	
	/**
	 * 查询数据
	 * @param sql 表名
	 * @param selectionArgs  条件参数 
	 * @return List<ContentValues>查询结果数据数组
	 */
	public ArrayList<ContentValues> rawQuery(String sql, String[] selectionArgs) {
		try {
			Cursor cursor = mDB.rawQuery(sql, selectionArgs);
			return serializationCursor(cursor);
		} catch (Exception e) {
			return new ArrayList<ContentValues>();
		}
	}

	/**
	 * 查询数据
	 * 
	 * @param Table
	 *            表名
	 * @param columns
	 *            列名
	 * @param selection
	 *            条件
	 * @param selectionArgs
	 *            条件参数
	 * @param groupBy
	 *            分组
	 * @param having
	 *            ...
	 * @param orderBy
	 *            排序
	 * 
	 * @return List<ContentValues> 数据类型对象
	 */
	public ArrayList<ContentValues> Select(String Table, String[] columns,
                                           String selection, String[] selectionArgs, String groupBy,
                                           String having, String orderBy) {
		try {
			Cursor cursor = mDB.query(Table, columns, selection, selectionArgs,groupBy, having, orderBy);
			ArrayList<ContentValues> list= serializationCursor(cursor);
			cursor.close();
			return list;
		} catch (Exception e) {
			Log.e(TAG, "Select table: " + Table + "error:" + e.getMessage());
			return new ArrayList<ContentValues>();
		}
	}

	/**
	 * 根据sqlite 的游标返回序列化数据
	 * 
	 * @param cur
	 *            游标
	 * @return List<HashMap<String, Object>> 数据
	 * */
	public ArrayList<ContentValues> serializationCursor(Cursor cur) {
		ArrayList<ContentValues> list = new ArrayList<ContentValues>();
		if (cur != null) {
			cur.moveToFirst();
			while (!cur.isAfterLast()) {
				ContentValues cv = new ContentValues();
				for (int i = 0; i < cur.getColumnCount(); i++) {
					cv.put(cur.getColumnName(i), cur.getString(i));
				}
				list.add(cv);
				cur.moveToNext();
			}
		}
		return list;
	}

}
