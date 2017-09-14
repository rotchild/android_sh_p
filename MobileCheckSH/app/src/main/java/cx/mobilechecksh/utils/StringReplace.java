package cx.mobilechecksh.utils;

/**
 * Created by Administrator on 2017/9/13 0013.
 */

public class StringReplace {
    /**
     * 字符串移除source中的target
     * @param source
     * @param target
     * @return
     */
    public static String toReplace(String source,String target){
        return source.replace(target,"");
    }
}
