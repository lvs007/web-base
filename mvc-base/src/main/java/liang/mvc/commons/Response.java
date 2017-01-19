package liang.mvc.commons;

import liang.dao.common.Page;

import java.util.*;

//import com.baidu.union.audit.commons.BeanUtils;

public class Response {

    private Integer errorId;
    private String message;
    private List<ErrorField> fields;
    private Map<String, Object> data;
    private boolean success;

    public Response() {
        errorId = 0;
        message = "";
        fields = new ArrayList<ErrorField>();
        data = new HashMap<String, Object>();
        success = true;
    }

    public Integer getErrorId() {
        return errorId;
    }

    public Response setErrorId(Integer errorId) {
        this.errorId = errorId;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Response setMessage(String message) {
        this.message = message;
        return this;
    }

    public Response addErrorField(ErrorField errorField) {
        if (fields == null) {
            fields = new ArrayList<ErrorField>();
        }
        fields.add(errorField);
        return this;
    }

    public List<ErrorField> getFields() {
        return fields;
    }

    public Response setFields(List<ErrorField> fields) {
        this.fields = fields;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Response addData(String key, Object value) {
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
    public Response addData(Object obj) {
        if (obj == null)
            return this;
        else if (obj instanceof Collection)
            return addList((Collection<?>) obj);
        else if (obj instanceof Map)
            return addMap((Map<String, Object>) obj);
        else if (obj instanceof Page)
            return addPage((Page<?>) obj);
        else if (obj instanceof String)
            return addString((String) obj);
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
    public Response addMultiClassData(List<?> objs) {
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

    private Response addBean(Object obj) {
        data.putAll(BeanUtils.copyProperties(obj));
        removeClass(data);
        return this;
    }

    private Response addList(Collection<?> list) {
        data.put("result", list);
        return this;
    }

    private Response addMap(Map<String, Object> map) {
        data.putAll(map);
        return this;
    }

    private Response addPage(Page<?> page) {
        data.putAll(BeanUtils.copyProperties(page));
        removeClass(data);
        return this;
    }

    private Response addString(String obj) {
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
