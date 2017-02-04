package liang.mvc.constants;

/**
 * 在这里定义常量
 *
 * @author skyfalling
 */
public class SystemConfig {

    /**
     * 正则表达式配置
     */
    public static class Regex {
        /**
         * 校验域名
         */
        public final static String DOMAIN = "(?i)[a-z0-9\\u4e00-\\u9fa5][-a-z0-9\\u4e00-\\u9fa5]*\\.([a-z0-9\\u4e00-\\u9fa5][-a-z0-9\\u4e00-\\u9fa5]*\\.)*[a-z]{2,}";
        /**
         * 校验URL
         */
        public final static String URL = "^(http|www|ftp|https|)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$";
        /**
         * 校验价格,长度小数点前最多7位，小数点后最多2位
         */
//        public final static String PRICE = "\\d+(.\\d{1,2}){0,1}";
        public final static String PRICE = "([1-9]\\d{0,6}|0){1}(\\.\\d{1,2}){0,1}";

    }

    /**
     * 资源国际化配置
     */
    public static class I18N {
        /**
         * 提示信息配置路径
         */
        public static final String MESSAGES = "i18n/messages";
        /**
         * 错误信息配置路径
         */
        public static final String ERRORS = "i18n/errors";
        /**
         * 文件编码配置
         */
        public static final String ENCODING = "UTF-8";
    }

    /**
     * 字符串标识
     */
    public static final class Strings {
        /**
         * 用于标识唯一不重复的字符串
         */
        public static final String UNIQUE = "~(!@#$%^&*)";
        /**
         * session标识
         */
        public static final String SESSION = "session";
        /**
         * customer标识
         */
        public static final String CUSTOMER = "customer";
    }

    /**
     * 数字标识
     */
    public static final class Numbers {
        /**
         * TU大数据标识
         */
        public static final long TU_BIG_NUMBER = 9223372032559808512L;
        /**
         * 默认每页数据大小
         */
        public static final int PAGE_SIZE = 20;
    }
}
