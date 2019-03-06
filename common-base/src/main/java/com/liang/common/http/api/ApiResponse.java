package com.liang.common.http.api;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liang.common.http.api.fetch.FetchMoreResponse;

import java.util.ArrayList;
import java.util.List;


/**
 * 对服务器返回结果的封装
 *
 */
public class ApiResponse {
    private boolean success;
    private int errorCode;
    private String message;
    private JSONObject jsonObject;

    public ApiResponse() {

    }

    public ApiResponse(JSONObject jsonObject) {
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        this.jsonObject = jsonObject;
        this.success = jsonObject.getBooleanValue("success");
        this.errorCode = jsonObject.getIntValue("errorCode");
        this.message = jsonObject.getString("message");
    }

    /**
     * 返回的数据格式如下
     * <pre>
     * {
     *  "success":true,
     *  "data":{
     *      "itemList":[],
     *      "pageCount":23,
     *      "hasMore":true,
     *      "cursor":"hello"
     *  }
     * }
     * </pre>
     */
    public <T> FetchMoreResponse<T> parseFetchMoreResponse(Class<T> cls) {
        FetchMoreResponse<T> response = new FetchMoreResponse<T>();
        JSONObject dataObject = jsonObject.getJSONObject("data");
        if (dataObject != null) {
            response.setList(getDataArray("data.itemList", cls));
            response.setPageCount(dataObject.getIntValue("pageCount"));
            response.setHasMore(dataObject.getBooleanValue("hasMore"));
            response.setCursor(dataObject.getString("cursor"));
        }
        return response;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }


    public boolean isSuccess() {
        return success;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public <T> T getData(Class<T> cls) {
        return getData("data", cls);
    }

    /**
     * 从JSONObject算起的路径自动解析成对象
     * {}
     */
    public <T> T getData(String path, Class<T> cls) {
        String[] ss = path.split("\\.");
        JSONObject json = jsonObject;
        for (String s : ss) {
            if (json != null) {
                json = json.getJSONObject(s);
            } else {
                break;
            }
        }
        if (json != null) {
            return JSON.toJavaObject(json, cls);
        } else {
            return null;
        }
    }

    public JSONObject getData() {
        return jsonObject.getJSONObject("data");
    }

    /**
     * 此方法对应的路径是 data.itemList， 如果不是这个，则不能用此方法
     */
    public <T> List<T> getDataArray(Class<T> cls) {
        return getDataArray("data.itemList", cls);
    }

    public <T> List<T> getDataArray(String path, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        String[] ss = path.split("\\.");
        JSONObject json = jsonObject;
        for (int i = 0; i < ss.length - 1; i++) {
            // 多级数据可能为空
            if (json != null) {
                json = json.getJSONObject(ss[i]);
            } else {
                break;
            }
        }
        if (json != null) {
            JSONArray array = json.getJSONArray(ss[ss.length - 1]);
            if (array != null) {
                for (int i = 0; i < array.size(); i++) {
                    list.add(array.getObject(i, cls));
                }
            }
        }
        return list;
    }
}

