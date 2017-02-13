package liang.mvc.commons.exception;

/**
 * Created by mc-050 on 2017/2/13 10:50.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class BaseException extends RuntimeException {

    private static BaseException baseException;

    private static final Object LOCK = new Object();

    public BaseException(String message) {
        super(message);
    }

    public static <T extends BaseException> T getInstance(String message) {
        if (baseException == null) {
            synchronized (BaseException.LOCK) {
                if (baseException == null) {
                    baseException = new BaseException(message);
                }
            }
        }
        return (T) baseException;
    }
}
