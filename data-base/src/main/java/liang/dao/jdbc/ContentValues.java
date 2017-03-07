/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package liang.dao.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 可以实现动态更新的对象，模仿android里面的ContentValues类
 *
 * @author
 */
public class ContentValues {

    private static final Logger LOG = LoggerFactory.getLogger(ContentValues.class);
    /**
     * Holds the actual values
     */
    private final Map<String, Object> mValues;
    private Long id;

    /**
     * Creates an empty set of values using the default initial size
     *
     * @param id
     */
    public ContentValues(Long id) {
        // Choosing a default size of 8 based on analysis of typical
        // consumption by applications.
        mValues = new LinkedHashMap<>(8);
        setId(id);
    }

    public Long getId() {
        return id;
    }

    public final void setId(Long id) {
        mValues.put("id", id);
        this.id = id;
    }

    /**
     * Creates an empty set of values using the default initial size
     */
    public ContentValues() {
        // Choosing a default size of 8 based on analysis of typical
        // consumption by applications.
        mValues = new LinkedHashMap<>(8);
    }


//    public Object getValueByColumnName(String columnName) {
//        for (Entry<String, Object> ef : mValues.entrySet()) {
//            if (ef.getKey().equals(columnName)) {
//                return ef.getValue();
//            }
//        }
//        return null;
//    }

    public Object getValueByFieldName(String fieldName) {
        for (Entry<String, Object> ef : mValues.entrySet()) {
            if (ef.getKey().equals(fieldName)) {
                return ef.getValue();
            }
        }
        return null;
    }

    public Map<String, Object> toFieldsMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        for (Entry<String, Object> entry : valueSet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

//    public Map<String, Object> toColumnsMap() {
//        Map<String, Object> map = new LinkedHashMap<>();
//        for (Entry<String, Object> entry : valueSet()) {
//            map.put(entry.getKey(), entry.getValue());
//        }
//        return map;
//    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ContentValues)) {
            return false;
        }
        return mValues.equals(((ContentValues) object).mValues);
    }

    @Override
    public int hashCode() {
        return mValues.hashCode();
    }

    /**
     * Adds all values from the passed in ContentValues.
     *
     * @param other the ContentValues from which to copy
     */
    public void putAll(ContentValues other) {
        mValues.putAll(other.mValues);
    }

    public void put(String field, Object value) {
        mValues.put(field, value);
    }

    /**
     * Returns the number of values.
     *
     * @return the number of values
     */
    public int size() {
        return mValues.size();
    }

    /**
     * Remove a single value.
     *
     * @param key the name of the value to remove
     */
    public void remove(String key) {
        mValues.remove(key);
    }

    /**
     * Removes all values.
     */
    public void clear() {
        mValues.clear();
    }

    public boolean containsKey(String field) {
        return mValues.containsKey(field);
    }

    /**
     * Gets a value. Valid value types are {@link String}, {@link Boolean}, and
     * {@link Number} implementations.
     *
     * @param key the value to get
     * @return the data for the value
     */
    public Object get(String key) {
        return mValues.get(key);
    }

    /**
     * Returns a set of all of the keys and values
     *
     * @return a set of all of the keys and values
     */
    public Set<Entry<String, Object>> valueSet() {
        return mValues.entrySet();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String name : mValues.keySet()) {
            Object value = get(name);
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(name).append("=").append(value);
        }
        return sb.toString();
    }
}
