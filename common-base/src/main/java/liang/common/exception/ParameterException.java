package liang.common.exception;

/**
 * Created by mc-050 on 2017/2/16 11:55.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class ParameterException extends BaseException {
    public ParameterException(String message) {
        super(message);
    }

    public static ParameterException throwException(String message) {
        return new ParameterException(message);
    }
}
