package liang.common;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import liang.common.exception.ParameterException;
import liang.common.util.ReflectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 功能对象，提供多种便捷转换功能
 *
 * @author liangzhiyan
 */
public class MyTools {

    private static final String pattern_1 = "yyyy-MM-dd";
    private static final String pattern_2 = "yyyy-MM-dd hh:mm:ss";

    private static final Logger LOG = LoggerFactory.getLogger(MyTools.class);

    private static final Cache<String, Object> cache = CacheBuilder.newBuilder()
            .initialCapacity(10000).maximumSize(100000).expireAfterAccess(10, TimeUnit.MINUTES).build();

    /**
     * vo和bo之间的转换（两个对象list之间的转换）
     *
     * @param srcList 源对象list
     * @param desc    目标
     * @return 目标对象list
     * @throws Exception
     */
    public static <T> List<T> voAndBoTransfer(List srcList, Class desc) throws Exception {
        List<T> result = new ArrayList<T>();
        for (Object obj : srcList) {
            result.add((T) voAndBoTransfer(obj, desc));
        }
        return result;
    }


    /**
     * vo和bo之间的转换（两个对象之间的转换）
     *
     * @param srcObj 源对象
     * @param desc   目标
     * @return 目标对象
     * @throws Exception
     */
    public static <T> T voAndBoTransfer(Object srcObj, Class desc) throws Exception {
        if (srcObj == null) {
            return null;
        }
        Class src = srcObj.getClass();
        T desc_obj = (T) desc.newInstance();
        Field[] src_fields = sort(getFields(src));
        Field[] desc_fields = sort(getFields(desc));
        for (int i = 0; i < desc_fields.length; i++) {
            for (int j = 0; j < src_fields.length; j++) {
                if (desc_fields[i].getName().equals(src_fields[j].getName())) {
                    ReflectUtils.setValue(desc_obj, desc_fields[i], ReflectUtils.getValue(srcObj, src_fields[i]));
                }
            }
        }
        return desc_obj;
    }


    /**
     * 类型转换
     *
     * @param obj   原始值
     * @param clazz 目的类型（转换后值的类型）
     * @return 返回转换后的值
     * @throws java.text.ParseException
     */
    public static Object getValue(Object obj, Class clazz)
            throws ParseException {
        if (clazz.isInstance(obj)) {
            return obj;
        } else {
            if (clazz.getName().equals("java.lang.String")) {
                return getString(obj);
            } else if (clazz.getName().equals("java.sql.Date")) {
                Date d = getDate(obj);
                return d;
            } else if (clazz.getName().equals("java.util.Date")) {
                java.util.Date d = getDate(obj);
                return d;
            } else if (clazz.getName().equals("java.sql.Timestamp")) {
                return getTimestamp(obj);
            } else if (clazz.getName().equals("java.math.BigDecimal")) {
                return obj == null ? null : new BigDecimal(obj.toString());
            } else if (clazz.getName().equals("java.lang.Integer")) {
                return obj == null ? null : Integer.valueOf(obj.toString());
            } else if (clazz.getName().equals("java.lang.Long")) {
                return obj == null ? null : Long.valueOf(obj.toString());
            } else if (clazz.getName().equals("java.lang.Double")) {
                return obj == null ? null : Double.valueOf(obj.toString());
            } else if (clazz.getName().equals("java.lang.Float")) {
                return obj == null ? null : Float.valueOf(obj.toString());
            } else if (clazz.getName().equals("java.lang.Byte")) {
                return obj == null ? null : Byte.valueOf(obj.toString());
            } else if (clazz.getName().equals("java.lang.Character")) {
                return obj == null ? null : Character.valueOf(obj.toString()
                        .charAt(0));
            } else {
                return obj;
            }
        }
    }

    private static String getString(Object obj) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern_1);
        if (obj instanceof Date) {
            return sdf.format((Date) obj);
        } else if (obj instanceof Timestamp) {
            return sdf.format(new Date(((Timestamp) obj).getTime()));
        } else {
            return obj == null ? null : obj.toString();
        }
    }

    @SuppressWarnings("unchecked")
    private static Date getDate(Object obj) {
        Date t;
        if (obj instanceof Timestamp) {
            t = new Date(((Timestamp) obj).getTime());
        } else if (obj instanceof Date) {
            t = new Date(((Date) obj).getTime());
        } else {
            t = obj == null ? null : java.sql.Date.valueOf(obj.toString());
        }
        return t;
    }

    private static Timestamp getTimestamp(Object obj) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern_1);
        if (obj instanceof Date) {
            return new Timestamp(((Date) obj).getTime());
        } else {
            return obj == null ? null : new Timestamp(sdf.parse(obj.toString()).getTime());
        }
    }

    /**
     * 按照属性名来排序字段
     *
     * @param fields
     * @return
     */
    public static Field[] sort(Field[] fields) {
        List<Field> list = Arrays.asList(fields);
        Collections.sort(list, new Comparator<Field>() {
            @Override
            public int compare(Field f1, Field f2) {
                return f1.getName().compareTo(f2.getName());
            }
        });
        fields = list.toArray(fields);
        return fields;
    }

    public static <T> Map<String, T> toMap(List<T> list, final String... objs) {
        Map<String, T> result = new HashMap<String, T>();
        try {
            String key = "";
            for (T ol : list) {
                key = getKey("-", ol, objs);
                result.put(key, ol);
            }
        } catch (Exception e) {
            LOG.error("toMap 发生异常！", e);
        }
        return result;
    }

    /**
     * 对一个list对象，按照指定的字段分组
     *
     * @param list 待分组的对象list
     * @param objs 指定的分组字段数组
     * @return 返回一个按照指定字段分组后的map，key为指定字段值的拼接(格式字段1-字段2...)，value为一个list（即一个分组）
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, List<T>> listToMap(String split, List<T> list, final String... objs) {

        Map<String, List<T>> result = new HashMap<String, List<T>>();
        try {
            List<T> value;
            String key = "";
            for (Object ol : list) {
                key = getKey(split, ol, objs);
                if (result.containsKey(key)) {
                    result.get(key).add((T) ol);
                } else {
                    value = new ArrayList();
                    value.add((T) ol);
                    result.put(key, value);
                }
            }
        } catch (Exception e) {
            LOG.error("listToMap 发生异常！", e);
        }
        return result;
    }

    private static String getKey(String split, Object object, String... fields) throws Exception {
        String str = "";
        Class clazz = object.getClass();
        for (String field : fields) {
            Object obj = ReflectUtils.getValue(object, getField(clazz, field));
            str += (obj == null ? "" : (obj.toString() + split));
        }
        return str.substring(0, str.length() - split.length());
    }

    /**
     * 把一个object对象转换为map（键值对），会把空字符串null过滤掉。
     *
     * @param obj 需要转换的对象
     * @return map对象
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static Map<String, Object> beanToMap(Object obj)
            throws IllegalArgumentException, IllegalAccessException {
        Class clazz = obj.getClass();
        Field[] fields = getFields(clazz);
        Map<String, Object> result = new HashMap<String, Object>();
        for (Field f : fields) {
            Object value = ReflectUtils.getValue(obj, f);
            if (value != null) {
                result.put(f.getName(), value);
            }
        }
        return result;
    }

    /**
     * bean转换成一个list，list中的字段顺序跟bean中声明字段的顺序一致
     *
     * @param bean
     * @return
     * @throws IllegalAccessException
     */
    public static <T> List<Object> beanToList(T bean) throws IllegalAccessException {
        List<Object> result = new ArrayList<>();
        Class clazz = bean.getClass();
        Field[] fields = getFields(clazz);
        for (Field f : fields) {
            result.add(ReflectUtils.getValue(bean, f));
        }
        return result;
    }

    /**
     * 参考beanToList方法
     *
     * @param beanList
     * @return
     * @throws IllegalAccessException
     */
    public static <T> List<List<Object>> beanListToList(List<T> beanList) throws IllegalAccessException {
        List<List<Object>> result = new ArrayList<>();
        for (Object bean : beanList) {
            result.add(beanToList(bean));
        }
        return result;
    }


    /**
     * 用指定的值替换指定的符号
     *
     * @param value   需要替换的字符串
     * @param regex   匹配的正则表达式
     * @param strings 替换匹配的值
     * @return 替换后的字符串，如果替换失败返回null
     */
    public static String replaceAll(String value, String regex, String... strings) {
        try {
            String result = value;
            for (String str : strings) {
                result = result.replaceFirst(regex, str);
            }
            return result;
        } catch (Exception e) {
            LOG.info("替换失败！value=" + value + "替换参数=" + Arrays.asList(strings), e);
            return null;
        }

    }

    /**
     * 把一个map转换成为一个object对象
     *
     * @param map
     * @param clazz
     * @return
     */
    public static <T> T mapToObject(Map<String, Object> map, Class<T> clazz) {
        T t = null;
        Field[] fields = getFields(clazz);
        try {
            t = clazz.newInstance();
            for (Field field : fields) {
                ReflectUtils.setValue(t, field, map.get(field.getName()));
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 获取str的最后4位
     *
     * @param str
     * @return if str is null then return null,else if str length less than 4 then return str
     */
    public static String getLast4String(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        if (str.length() <= 4) {
            return str;
        }
        return str.substring(str.length() - 4);
    }

    /**
     * 把一个字符串指定范围的字符替换成指定的字符
     *
     * @param value       原始值
     * @param replacement 替换的char
     * @param begin       开始替换的地方
     * @param end         结束替换的地方
     * @return
     */
    public static String replace(String value, char replacement, int begin, int end) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        int length = value.length();
        if (begin <= 0 || end > length || begin >= end) {
            throw ParameterException.throwException("begin必须大于0，end必须小于或等于value.length,begin必须小于end");
        }
        char[] valueCharArray = value.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i <= length; i++) {
            if (i >= begin && i <= end) {
                sb.append(replacement);
            } else {
                sb.append(valueCharArray[i - 1]);
            }
        }
        return sb.toString();
    }

    public static Field[] getFields(Class clazz) {
        String key = buildFieldsKey(clazz);
        Field[] fields = (Field[]) cache.getIfPresent(key);
        if (fields == null) {
            fields = clazz.getDeclaredFields();
            cache.put(key, fields);
        }
        return fields;
    }

    public static Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
        String key = buildFieldKey(clazz, fieldName);
        Field field = (Field) cache.getIfPresent(key);
        if (field == null) {
            field = clazz.getDeclaredField(fieldName);
            cache.put(key, field);
        }
        return field;
    }

    private static String buildFieldsKey(Class clazz) {
        return "fields." + clazz.getName();
    }

    private static String buildFieldKey(Class clazz, String fieldName) {
        return "field." + clazz.getName() + "." + fieldName;
    }

    public static void main(String[] args) {
        String value = "liang#zhi#yan#godd";
        for (int i = 0; i < 10; i++) {
            System.out.println(MyTools.replaceAll(value, "#", "--" + i * 5 + "--", "--" + i * 6 + "--", "--" + i * 7 + "--"));
        }
        String str = "key-key2-";
        System.out.println(str.substring(0, str.length() - 1));

        String json = "[{\n" +
                "platform:\"\"," +
                "status:\"\"," +
                "spendTime:\"\"," +
                "reason:\"\"" +
                "},{\n" +
                "platform:\"\"," +
                "status:\"\"," +
                "spendTime:\"\"," +
                "reason:\"\"" +
                "}]";
        for (Object object : JSON.parseArray(json)) {

        }
        System.out.println(JSON.parseArray(json));
        System.out.println(replace("15256073545", '*', 4, 7));
    }
}
