package liang.mvc.commons.exception;

/**
 * Created by mc-050 on 2017/2/13 10:33.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class UnExistException extends BaseException {

    private UnExistException(String message) {
        super(message);
    }

    public static UnExistException throwException(String message) {
        return new UnExistException(message);
    }
}
