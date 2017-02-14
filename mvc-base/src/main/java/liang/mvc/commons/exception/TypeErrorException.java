package liang.mvc.commons.exception;

/**
 * Created by mc-050 on 2017/2/13 10:49.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class TypeErrorException extends BaseException {
    private TypeErrorException(String message) {
        super(message);
    }

    public static TypeErrorException throwException(String message) {
        return new TypeErrorException(message);
    }
}
