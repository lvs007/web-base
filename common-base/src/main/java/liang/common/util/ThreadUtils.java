package liang.common.util;

/**
 * Created by kiven on 2018/5/1.
 */
public class ThreadUtils {

    public static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
