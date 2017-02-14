package liang.mvc.commons.exception;

/**
 * Created by mc-050 on 2017/2/14 9:54.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class NullValueException extends BaseException{
    public NullValueException(String message) {
        super(message);
    }

    public static NullValueException throwException(String message){
        return new NullValueException(message);
    }
}
