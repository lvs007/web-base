package liang.common.util;

import liang.common.exception.TypeErrorException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.lang.reflect.Field;
import java.text.ParseException;

/**
 * Created by mc-050 on 2017/2/4 19:15.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class ReflectUtils {

    public static <T> Object getValue(T object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = object.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        Object value = field.get(object);
        field.setAccessible(false);
        return value;
    }

    public static <T> Object getValue(T t, Field field) throws IllegalAccessException {
        field.setAccessible(true);
        Object value = field.get(t);
        field.setAccessible(false);
        return value;
    }

    public static void setValue(Object object, Field field, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(object, value);
        field.setAccessible(false);
    }

    public static Object transferParamType(String param, Field field) throws ParseException {
        Class fieldType = field.getType();
        if (fieldType.getName().contains("String")) {
            return param;
        } else if (StringUtils.containsIgnoreCase(fieldType.getName(), "int")) {
            return Integer.parseInt(param);
        } else if (StringUtils.containsIgnoreCase(fieldType.getName(), "double")) {
            return Double.parseDouble(param);
        } else if (StringUtils.containsIgnoreCase(fieldType.getName(), "float")) {
            return Float.parseFloat(param);
        } else if (StringUtils.containsIgnoreCase(fieldType.getName(), "boolean")) {
            if ("0".equals(param)) {
                return false;
            } else if ("1".equals(param)) {
                return true;
            } else {
                return Boolean.parseBoolean(param);
            }
        } else if (StringUtils.containsIgnoreCase(fieldType.getName(), "date")) {
            return DateUtils.parseDate(param, "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss");
        } else {
            throw TypeErrorException.throwException("暂时不支持这种类型的转换！");
        }
    }
}
