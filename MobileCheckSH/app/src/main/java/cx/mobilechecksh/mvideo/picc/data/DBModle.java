package cx.mobilechecksh.mvideo.picc.data;

public class DBModle {
	
	public final static String MESSAGE_SOURCES = "MessageSources";
	/**
	 * 消息记录表模板
	 * */
	public final static  class MessageSources{
		public final static String SMSID = "SMSID";
		public final static String Contents = "Contents";
		public final static String IsRead = "IsRead";
		public final static String TID = "TID";
	}
	
	public final static String USERS = "Users";
	/**
	 * 用户表模板
	 * */
	public final static class Users{
		public final static String UserName = "UserName";
		public final static String PassWord = "PassWord";
		public final static String UserClass = "UserClass";
		public final static String LastLoginTime = "LastLoginTime";
		public final static String IsRemember = "IsRemember";
		public final static String AreaID = "AreaID";
		public final static String RealName = "RealName";
		public final static String Email = "Email";
		public final static String UserState = "UserState";
	}
	
	public final static String AREAS = "Areas";
	/**
	 * 区域表模板
	 * */
	public final static class Areas{
		public final static String AreaID = "AreaID";
		public final static String AreaName = "AreaName";
		public final static String AreaCode = "AreaCode";
		public final static String ParentId = "ParentId";
		public final static String AreaClass = "AreaClass";
	}
	
	public final static String CARTYPE = "CarType";
	/**
	 * 车型表模板
	 * */
	public final static class CarType{
		public final static String CarTypeID = "CarTypeID";
		public final static String TypeName = "TypeName";
	}
	
	public final static String GARAGE = "Garage";
	/**
	 * 修理厂表模板mainnew_casemess
	 * */
	public final static class Garage{
		public final static String GarageID = "GarageID";
		public final static String AreaID = "AreaID";
		public final static String ShortName = "ShortName";
		public final static String CarType = "CarType";
		public final static String Telephone = "Telephone";
		public final static String Address = "Address";
		public final static String Latitude = "Latitude";
		public final static String Longitude = "Longitude";
		public final static String ImgPath = "ImgPath";
		public final static String MoneyLimit = "MoneyLimit";
		public final static String Memo = "Memo";
		public final static String UserName = "UserName";
		public final static String IsPartner = "IsPartner";
		public final static String Is4s = "Is4s";
		public final static String IsSynthesize = "IsSynthesize";
		public final static String MonthDone = "MonthDone";
		public final static String ReachRate = "ReachRate";
		public final static String FullName = "FullName";
	}
	
	public final static String TASK = "Task";
	/**
	 * 任务表模板
	 * */
	public final static class Task{
		/**TaskId */
		public final static String TID = "TID";
		/**案件号 */
		public final static String CaseNo = "CaseNo";
		/**车牌号 */
		public final static String CarMark = "CarMark";
		/**驾驶员 */
		public final static String CarDriver = "CarDriver";
		/**被保险人 */
		public final static String CarOwner = "CarOwner";
		/** 联系电话*/
		public final static String Telephone = "Telephone";
		/**事故地点 */
		public final static String Address = "Address";
		/** 图片路径(无效)*/
		public final static String ImgPath = "ImgPath";
		/** */
		public final static String FrontPrice = "FrontPrice";
		/** */
		public final static String BackPrice = "BackPrice";
		/**  维修金额(无效) */
		public final static String FixedPrice = "FixedPrice";
		/**纬度 */
		public final static String Latitude = "Latitude";
		/**经度 */
		public final static String Longitude = "Longitude";
		/** */
		public final static String TaskID = "TaskID";
		/** */
		public final static String TaskType = "TaskType";
		/** */
		public final static String FrontOperator = "FrontOperator";
		/** */
		public final static String FrontState = "FrontState";
		/** */
		public final static String BackOperator = "BackOperator";
		/** */
		public final static String BackState = "BackState";
		/** */
		public final static String Watcher = "Watcher";
		/** */
		public final static String StateUpdateTime = "StateUpdateTime";
		/**出险时间 */
		public final static String AccidentTime = "AccidentTime";
		/**备注 */
		public final static String Memo = "Memo";
		/** */
		public final static String GarageID = "GarageID";
		/** */
		public final static String CarTypeID = "CarTypeID";
		/** 1:新案件，0：不是新案件*/
		public final static String IsNew = "IsNew";
		/** */
		public final static String IsCooperation = "IsCooperation";
		public final static String LinkCaseNo = "LinkCaseNo";
		/**案件类型（调查、本地查勘、异地代查勘） */
		public final static String CaseType = "CaseType";
		/** 自编号*/
		public final static String AutoGenerationNO = "AutoGenerationNO";
		/**车辆品牌 */
		public final static String CarType = "CarType";
		/**事故类型（单方事故、双方事故、多方事故） */
		public final static String AccidentType = "AccidentType";
		/**险种（商业，交强，商业交强） */
		public final static String InsuranceType = "InsuranceType";
		/**保险公司编号 */
		public final static String InsuranceID = "InsuranceID";
		/**保险公司联系人 */
		public final static String InsuranceContactPeople = "InsuranceContactPeople";
		/**保险公司联系电话 */
		public final static String InsuranceContactTelephone = "InsuranceContactTelephone";
		/**公估费*/
		public final static String PaymentAmount = "PaymentAmount";
		/**支付方式*/
		public final static String PaymentMethod = "PaymentMethod";
		/**是否风险案件（0不是，1是 ）*/
		public final static String IsRisk = "IsRisk";
		/**事故经过*/
		public final static String AccidentDescribe = "AccidentDescribe";
		
		/**三者车*/
		public final static String ThirdCar = "ThirdCar";
		
		/**区域ID*/
		public final static String AreaID = "AreaID";
	}
	
	public final static String TASKLOG = "TaskLog";
	/**
	 * 任务记录表模板
	 * */
	public final static class TaskLog{
		public final static String TaskLogID = "TaskLogID";
		public final static String TID = "TID";
		public final static String FrontState = "FrontState";
		public final static String StateUpdateTime = "StateUpdateTime";
		public final static String Latitude = "Latitude";
		public final static String Longitude = "Longitude";
		
		/** 未分配 */
		public final static String FrontState_NO_ASSIGN = "0";
		/** 拒绝 */
		public final static String FrontState_REFUSE = "1";
		/** 接受 */
		public final static String FrontState_ACCEPT = "3";
		/** 开始查勘 (推修开始)*/
		public final static String FrontState_START = "4";
		/** 完成 (推修完成)*/
		public final static String FrontState_FINISH = "5";
		/** 调度错误 */
		public final static String FrontState_ASSIGN_ERROE = "7";
		/** 有照销案*/
		public final static String FrontState_CLEAR_CASE_WITH_PICTURE = "8";
		/** 无照销案 */
		public final static String FrontState_CLEAR_CASE_NO_PICTURE = "9";
		
		public final static String FrontState_SUPPLY = "11";
		
		/**未知是否是车行保单*/
		public final static int COOPERATION = -1;
		/**非车行保单*/
		public final static int COOPERATION_NO = 0;
		/**车行保单*/
		public final static int COOPERATION_YES = 1;
	}
	
	public final static String TASKSTACK = "TaskStack";
	/**
	 * 任务堆栈表模板
	 * */
	public final static class TaskStack{
		public final static String StackID = "StackID";
		public final static String TID = "TID";
		public final static String Action = "Action";
		public final static String StackType = "StackType";
		public final static String RequestURL = "RequestURL";
		public final static String Keys = "Keys";
		public final static String Vals = "Vals";
		/** 上传级别 数值越小越高 */
		public final static String Level = "[Level]";
		public final static String IsRequest = "IsRequest";
		public final static String Description = "Description";
		public final static String Data = "Data";
		
		/** 开始上传 */
		public final static String IsRequest_Y = "1";
		/** 不上传 */
		public final static String IsRequest_N = "0";
		
		/** 动作http */
		public final static String Action_Http = "1";
		/** 动作上传文件 */
		public final static String Action_Upload = "2";
		/** 下载文件 */
		public final static String Action_Download = "3";
		
		/** 上传级别 0 为网络请求优先 */
		public final static String Level_Http = "0";
		/** 上传级别5 上传文件 */
		public final static String Level_Upload = "5";
		
		/** 文件类型 */
		public final static String Keys_File = "FilePath";
	}
	
	/**保险公司列表*/
	public final static String INSURANCE = "Insurance";
	/**
	 * 保险公司
	 * */
	public final static class Insurance{
		public final static String IID = "IID";
		public final static String InsuranceID = "InsuranceID";
		public final static String InsuranceName = "InsuranceName";
		public final static String InsuranceLink = "InsuranceLink";
		public final static String UserNames = "UserNames";
		
	}
	
	/**常用字段表*/
	public final static String DICT = "Dict";
	/**
	 * 字段信息表
	 */
	public final static class Dict{
		public final static String DID = "DID";
		public final static String DictID = "DictID";
		public final static String TableName = "TableName";
		public final static String FieldName = "FieldName";
		public final static String FieldValue = "FieldValue";
		public final static String Description = "Description";
		public final static String Memo = "Memo";
	}
	
	
	/**常用字段表*/
	public final static String ROLEDICTS = "RoleDicts";
	/**
	 * 字段信息表
	 */
	public final static class RoleDicts{
		public final static String RDID = "RDID";
		public final static String RoleID = "RoleID";
		public final static String RoleType = "RoleType";
		public final static String RoleName = "RoleName";
	}
	
	
}
