package cx.mobilechecksh.data;

/**
 * Created by cx on 2017/8/22.
 */

public class DBModle {
public final static class Task{
    /**案件id*/
    public final static String CaseId="id";
    /**案件号 */
    public final static String CaseNo = "task_no";
    /**车牌号 */
    public final static String CarMark = "license_no";
    /** 1:新案件，0：不是新案件*/
    public final static String IsNew = "IsNew";
    /** 提交时间*/
    public final static String CreateTime="create_date";
    /**车型*/
   // public final static String CarType="CarType";
    /**任务状态 0待定损 1定损中 2已挂起 3已完成*/
    public final static String CaseState="status";
    /**定损员姓名*/
    public final static String DSName="ds_user_realname";
    /**用户名*/
    public final static String USName="username";
    /**定损员电话*/
    public final static String DSMobile="ds_user_mobile";

}
public static class CallType{
    public final static String CallType="calltype";

}
}
