package liang.mvc.commons;

import liang.dao.common.Page;

import java.util.*;

public class ResponseData {

    private Integer errorId;
    private String message;
    private List<ErrorField> fields;
    private Map<String, Object> data;
    private boolean success;

    public ResponseData() {
        errorId = 0;
        message = "";
        fields = new ArrayList<ErrorField>();
        data = new HashMap<String, Object>();
        success = true;
    }

    public Integer getErrorId() {
        return errorId;
    }

    public ResponseData setErrorId(Integer errorId) {
        this.errorId = errorId;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResponseData setMessage(String message) {
        this.message = message;
        return this;
    }

    public ResponseData addErrorField(ErrorField errorField) {
        if (fields == null) {
            fields = new ArrayList<ErrorField>();
        }
        fields.add(errorField);
        return this;
    }

    public List<ErrorField> getFields() {
        return fields;
    }

    public ResponseData setFields(List<ErrorField> fields) {
        this.fields = fields;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public ResponseData addData(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * <pre>
     * List<?>				-> "data":{"result":[]}
     * Map<String, Object>	-> "data":{"field1":111, "field2": 222}
     * Page<?>				-> "data":{"field1":111, "field2": 222}
     * Bean					-> "data":{"pageNo":1, "pageSize":40 ,"field1":111, "field2": 222, "result":[]}
     * </pre>
     *
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    public ResponseData addData(Object obj) {
        if (obj == null)
            return this;
        else if (obj.getClass().isArray())
            return addList(Arrays.asList(obj));
        else if (obj instanceof Collection)
            return addList((Collection<?>) obj);
        else if (obj instanceof Map)
            return addMap((Map<String, Object>) obj);
        else if (obj instanceof Page)
            return addPage((Page<?>) obj);
        else if (obj instanceof String || obj instanceof Boolean
                || obj instanceof Number || obj instanceof Character)
            return addBaseType(obj);
        else {
            return addBean(obj);
        }
    }

    /**
     * List<?> -> "data":{"bean1":{"field1":111, "field2": 222}, "bean2":{"field1":111, "field3": 333}}
     *
     * @param objs
     * @return
     */
    public ResponseData addMultiClassData(List<?> objs) {
        for (Object obj : objs) {
            String className = obj.getClass().getSimpleName();
            String firstLetter = className.substring(0, 1).toLowerCase();
            String postfix = className.substring(1, className.length());
            String objectName = firstLetter + postfix;

            Map<String, Object> map = BeanUtils.copyProperties(obj);
            removeClass(map);
            data.put(objectName, map);
        }
        return this;
    }

    private ResponseData addBean(Object obj) {
        data.putAll(BeanUtils.copyProperties(obj));
        removeClass(data);
        return this;
    }

    private ResponseData addList(Collection<?> list) {
        data.put("result", list);
        return this;
    }

    private ResponseData addMap(Map<String, Object> map) {
        data.putAll(map);
        return this;
    }

    private ResponseData addPage(Page<?> page) {
        data.putAll(BeanUtils.copyProperties(page));
        removeClass(data);
        return this;
    }

    private ResponseData addBaseType(Object obj) {
        data.put("value", obj);
        return this;
    }

    private void removeClass(Map<String, ?> map) {
        map.remove("class");
    }

    public boolean hasError() {
        return errorId > 0;
    }
}
