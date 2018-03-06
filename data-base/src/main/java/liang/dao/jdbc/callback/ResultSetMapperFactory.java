/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.dao.jdbc.callback;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 一个ResultSetMapper的工厂类
 *
 * @author
 */
public class ResultSetMapperFactory {

    public static <T> ResultSetMapper<T> createBeanMapper(Class<T> cls) {
        //如果是基本数据类型，则使用自己的实现，否则使用spring的实现
        if (cls == String.class) {
            return new ResultSetMapper<T>() {

                @Override
                public T mapper(ResultSet rs) throws SQLException {
                    return (T) rs.getString(1);
                }
            };
        } else if (cls == Integer.class || cls == int.class) {
            return new ResultSetMapper<T>() {

                @Override
                public T mapper(ResultSet rs) throws SQLException {
                    return (T) Integer.valueOf(rs.getInt(1));
                }
            };
        } else if (cls == Long.class || cls == long.class) {
            return new ResultSetMapper<T>() {

                @Override
                public T mapper(ResultSet rs) throws SQLException {
                    return (T) Long.valueOf(rs.getLong(1));
                }
            };
        } else if (cls == Float.class || cls == float.class) {
            return new ResultSetMapper<T>() {

                @Override
                public T mapper(ResultSet rs) throws SQLException {
                    return (T) Float.valueOf(rs.getFloat(1));
                }
            };
        } else if (cls == Double.class || cls == double.class) {
            return new ResultSetMapper<T>() {

                @Override
                public T mapper(ResultSet rs) throws SQLException {
                    return (T) Double.valueOf(rs.getDouble(1));
                }
            };
        } else if (cls == Boolean.class || cls == boolean.class) {
            return new ResultSetMapper<T>() {

                @Override
                public T mapper(ResultSet rs) throws SQLException {
                    return (T) Boolean.valueOf(rs.getBoolean(1));
                }
            };
        } else if (Date.class.isAssignableFrom(cls)) {
            return new ResultSetMapper<T>() {

                @Override
                public T mapper(ResultSet rs) throws SQLException {
                    return (T) rs.getTimestamp(1);
                }
            };
        } else if (Map.class.isAssignableFrom(cls)) {
            return new ResultSetMapper<T>() {

                @Override
                public T mapper(ResultSet rs) throws SQLException {
                    ResultSetMetaData metaData = rs.getMetaData();
                    Map<String, Object> map = new LinkedHashMap<>();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 0; i < columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i + 1);
                        Object value = rs.getObject(i + 1);
                        map.put(columnName, value);
                    }
                    return (T)map;
                }
            };
        }
        return BeanResultSetMapper.newInstance(cls);
    }

    public static ResultSetMapper<Map<String, Object>> createMapMapper() {
        return new ResultSetMapper<Map<String, Object>>() {

            @Override
            public Map<String, Object> mapper(ResultSet rs) throws SQLException {
                ResultSetMetaData metaData = rs.getMetaData();
                Map<String, Object> map = new HashMap<>();
                int columnCount = metaData.getColumnCount();
                for (int i = 0; i < columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i + 1);
                    Object value = rs.getObject(i + 1);
                    map.put(columnName, value);
                }
                return map;
            }
        };
    }

    private static class BeanResultSetMapper<T> extends BeanPropertyRowMapper<T> implements ResultSetMapper<T> {

        private int rowNumber = 0;

        @Override
        public T mapper(ResultSet rs) throws SQLException {
            return mapRow(rs, rowNumber++);
        }

        /**
         * Static factory method to create a new BeanPropertyRowMapper
         * (with the mapped class specified only once).
         *
         * @param <T>
         * @param mappedClass the class that each row should be mapped to
         * @return
         */
        public static <T> BeanResultSetMapper<T> newInstance(Class<T> mappedClass) {
            BeanResultSetMapper<T> newInstance = new BeanResultSetMapper<>();
            newInstance.setMappedClass(mappedClass);
            return newInstance;
        }

    }

}
