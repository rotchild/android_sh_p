package cx.mobilechecksh.mvideo.picc.data;

import android.database.sqlite.SQLiteDatabase;


/**
 * 数据库升级帮助类
 * @author json you 
 *
 */
public class DataUpgradeHelper {
	
	private  final String TEMP_TABLE = "TABLE_TEMP_";
	private SQLiteDatabase db;

	public DataUpgradeHelper(){
		
	}
	public DataUpgradeHelper(SQLiteDatabase db){
		this.db  = db;
	}
	
	/**
	 * 重命名table 到 临时table
	 * @param tableName
	 * @return 临时表表名
	 */
	private String reNameTable(String tableName){
		String sql = "ALTER TABLE "+tableName+" RENAME TO "+TEMP_TABLE+tableName;
		db.execSQL(sql);
		return TEMP_TABLE+tableName;
	}
	
	/**
	 * 创建新表（最新版本的表）
	 * @param sql
	 */
	private  void createNewTableWithSql(String sql_create_newTable){
		db.execSQL(sql_create_newTable);
	}
	/**
	 * 从临时表中导入原始数据，
	 *  格式   ：INSERT INTO oldtable SELECT colum1, “”, colum2 FROM tempTable;
	 *  “” 是新建表中的原来不存在的数据（新添加的字段）
	 * @param sql
	 */
	private  void importData(String sql_import){
		db.execSQL(sql_import);
	}
	
	/**
	 * 删除临时表
	 * @param tempTable
	 */
	private  void dropTempTable(String tempTable){
		String sql = "DROP TABLE "+tempTable;
		db.execSQL(sql);
	}
	
	/**
	 * 升级数据库操作
	 * @param tableName : old table name
	 * @param sql_create_newTable : the aim table after you upgraded 
	 * @param sql_import INSERT INTO oldtable SELECT colum1, “”, colum2 FROM tempTable; “” means the new colum
	 * 
	 */
	public  void upgradeDatabase(String tableName, String sql_create_newTable, String sql_import){
		String tempTable = reNameTable(tableName);
		createNewTableWithSql(sql_create_newTable);
		importData(sql_import);
		dropTempTable(tempTable);
	}
	
	
	
}
