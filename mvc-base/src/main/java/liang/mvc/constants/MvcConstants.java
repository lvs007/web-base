package liang.mvc.constants;

/**
 * Created by liangzhiyan on 2017/3/21.
 */
public class MvcConstants {

    public static final String TOKEN = "nb_token";
    public static final String COOKIE = "Cookie";
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String EQUAL = "=";

    public static enum ResultCode {
        SUCCESS(200, "成功"),
        NOT_LOGIN_ERROR(40000, "用户未登陆"),
        USER_ACCESS_ERROR(40001, "访问用户太多，请稍后重试"),
        ACCESS_ERROR(40002, "访问失败"),
        ;

        ResultCode(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int code;
        public String msg;
    }
}
