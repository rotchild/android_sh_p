package cx.mobilechecksh.data;

/**
 * Created by cx on 2017/8/22.
 */

public class DBModle {
public final static class Task{
    /**案件号 */
    public final static String CaseNo = "CaseNo";
    /**车牌号 */
    public final static String CarMark = "CarMark";
    /** 1:新案件，0：不是新案件*/
    public final static String IsNew = "IsNew";
    /** 案件状态: 待定损 定损中*/
    public final static String TaskState="TaskState";
    /** 提交时间*/
    public final static String AddTime="AddTime";
    /**车型*/
    public final static String CarType="CarType";
    /**任务状态*/
    public final static String CaseState="CaseState";
    /**定损员*/
    public final static String Dingsuner="Dingsuner";
}
}
