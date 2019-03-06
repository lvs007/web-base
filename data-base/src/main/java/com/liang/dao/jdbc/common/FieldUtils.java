package com.liang.dao.jdbc.common;

/**
 * Created by liangzhiyan on 2017/3/6.
 */
public class FieldUtils {
    /**
     * 从实体类的属性名到数据库列名的映射，默认实现是全部小写，
     * 然后大写字母前面加一个下划线，类似于
     * cityName -> city_name
     *
     * @param fieldName
     * @return
     */
    public static String convertToColumnName(String fieldName) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldName.length(); i++) {
            char c = fieldName.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append("_");
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
