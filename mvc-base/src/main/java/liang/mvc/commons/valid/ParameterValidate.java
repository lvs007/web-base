package liang.mvc.commons.valid;

import liang.mvc.commons.exception.NullValueException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * Created by mc-050 on 2017/2/14 15:55.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class ParameterValidate {

    public static void assertNull(String param) {
        if (StringUtils.isBlank(param)) {
            throw NullValueException.throwException("字符串为空！");
        }
    }

    public static void assertNull(Object param) {
        if (param == null) {
            throw NullValueException.throwException("参数为空！");
        }
    }

    public static void assertCollectionEmpty(Collection collection) {
        if (CollectionUtils.isEmpty(collection)) {
            throw NullValueException.throwException("集合为空！");
        }
    }

    public static void assertCollectionNull(Collection collection) {
        if (collection == null) {
            throw NullValueException.throwException("集合为空！");
        }
    }
}
