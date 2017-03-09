package liang.common.http.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import liang.common.http.HttpClient;
import liang.common.http.NameValuePair;
import liang.common.http.SignUtils;
import liang.common.http.api.exception.ApiException;
import liang.common.http.api.exception.HttpException;
import liang.common.http.api.exception.InternalException;
import liang.common.http.api.fetch.FetchMoreRequest;
import liang.common.http.api.fetch.FetchMoreResponse;
import liang.common.util.MiscUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;


/**
 * 所有向我们服务器请求Api的类的父类，里面封装了一些常用的方法，并统一规范，
 * 能使用此子类的都必须是我们自己内部的服务器，数据返回格式类似：
 * <pre>
 * {
 *     "success": true,
 *     "message": null,
 *     "errorCode": 0,
 *     "data": [{
 *         "username": "hades",
 *         "gender": "Male",
 *         "location":{
 *             "postcode", "100080",
 *             "address", "北京市海淀区苏州街18号"
 *         }
 *     }。。]
 * }
 * </pre>
 * <p/>
 * 对于我们自己的服务器来说，所有的成功的返回值都基本上类似于上面的样子，data就是我们需要定义的数据类，允许嵌套
 * Created
 */
public abstract class BaseApi {
    /**
     * 获取API请求的网址，比如 http://xx.xxx.com
     * [注意]，后面不要添加/
     */
    protected abstract String getApiHost();

    /**
     * 获取签名的key
     */
    protected abstract String getSignKey();

    protected <T> FetchMoreResponse<T> httpGetFetchMoreResponse(StringBuilder url, FetchMoreRequest request,
                                                                Class<T> cls) throws InternalException, ApiException, HttpException {
        buildFetchMoreParams(url, request);
        ApiResponse apiResponse = httpGet(url.toString());
        return apiResponse.parseFetchMoreResponse(cls);
    }

    /**
     * {
     * "success": true,
     * "message": null,
     * "errorCode": 0
     * "data": {
     * "fuck":{
     * "shit":{
     * "a": 1,
     * "b": 2
     * }
     * }
     * <p/>
     * }
     * <p/>
     * <p/>
     * }
     */

    protected <T> T httpGetData(String url, Class<T> cls) throws InternalException, ApiException, HttpException {
        ApiResponse apiResponse = httpGet(url);
        return apiResponse.getData(cls);
    }

    protected <T> List<T> httpGetDataList(String url, Class<T> cls)
            throws InternalException, ApiException, HttpException {
        ApiResponse apiResponse = httpGet(url);
        return apiResponse.getDataArray(cls);
    }

    protected ApiResponse httpGet(String url) throws ApiException, HttpException, InternalException {
        url = buildFullUrl(url);
        try {
            String back = getMucangHttpClient().httpGet(url);
            JSONObject jsonObject = JSON.parseObject(back);
            ApiResponse response = new ApiResponse(jsonObject);
            handleResponse(url, response, HttpMethod.Get);
            return response;

        } catch (IOException ioe) {
            throw new HttpException("网络不给力", ioe);
        } catch (ApiException api) {
            throw api;
        } catch (Exception ex) {
            throw new InternalException(ex);
        }
    }

    protected ApiResponse httpPost(String url, byte[] data) throws ApiException, HttpException, InternalException {
        if (data == null || data.length == 0) {
            throw new InternalException("POST的时候,body不能为空");
        }
        url = buildFullUrl(url);
        try {
            String back = getMucangHttpClient().httpPostBody(url, data);
            JSONObject jsonObject = JSON.parseObject(back);
            ApiResponse response = new ApiResponse(jsonObject);
            handleResponse(url, response, HttpMethod.Post);
            return response;
        } catch (IOException ioe) {
            throw new HttpException(ioe);
        } catch (ApiException api) {
            throw api;
        } catch (Exception ex) {
            throw new InternalException(ex);
        }
    }

    protected ApiResponse httpPost(String url, List<NameValuePair> pairList)
            throws ApiException, HttpException, InternalException {
        if (CollectionUtils.isEmpty(pairList)) {
            throw new InternalException("POST的时候,body不能为空");
        }
        url = buildFullUrl(url);
        try {
            String back = getMucangHttpClient().httpPost(url, pairList);
            JSONObject jsonObject = JSON.parseObject(back);
            ApiResponse response = new ApiResponse(jsonObject);
            handleResponse(url, response, HttpMethod.Post);
            return response;
        } catch (IOException ioe) {
            throw new HttpException(ioe);
        } catch (ApiException api) {
            throw api;
        } catch (Exception ex) {
            throw new InternalException(ex);
        }
    }


    protected static void buildFetchMoreParams(StringBuilder sb, FetchMoreRequest request) {
        if (request == null) {
            return;
        }
        List<NameValuePair> list = new ArrayList<>();
        if (StringUtils.isNotBlank(request.getCursor())) {
            list.add(new NameValuePair("cursor", request.getCursor()));
        }
        if (request.getPageSize() > 0) {
            list.add(new NameValuePair("pageSize", String.valueOf(request.getPageSize())));
        }
        buildUrlWithParams(sb, list);
    }

    /**
     * 把一个对象变成一个表单的形式提交出去,此方法只支持一级对象,也就是对象的成员变量都是基本数据类型或者字符串,连日期也不支持
     */
    protected static List<NameValuePair> toForm(Object object) {
        List<NameValuePair> list = new ArrayList<>();
        try {
            Field[] fs = object.getClass().getDeclaredFields();
            for (Field f : fs) {
                Class<?> cls = f.getType();
                if (cls.isPrimitive() || cls == String.class) {
                    f.setAccessible(true);
                    list.add(new NameValuePair(f.getName(), String.valueOf(f.get(object))));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    protected static void buildUrlWithParams(StringBuilder sb, List<NameValuePair> paramList) {
        if (CollectionUtils.isEmpty(paramList)) {
            return;
        }
        //先看看有没有?，如果没有，加上就是了
        if (sb.indexOf("?") < 0) {
            sb.append("?");
        }
        char lastChar = sb.charAt(sb.length() - 1);
        //如果不是以&结尾，则加上就是了
        if (lastChar != '&' && lastChar != '?') {
            sb.append("&");
        }
        for (NameValuePair pair : paramList) {
            sb.append(pair.getName()).append("=").append(safeURLEncode(pair.getValue(), "UTF-8")).append("&");
        }
        if (sb.charAt(sb.length() - 1) == '&') {
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    /**
     * 统一拦截所有的需要登录的请求，并且当服务器返回success为false的时候，自动抛出ApiException
     */
    protected void handleResponse(String url, ApiResponse response, HttpMethod method) throws ApiException {
        if (response.isSuccess() == false) {
            throw new ApiException(response);
        }
    }

    /**
     * 子类可以通过重写此方法,返回自己需求相关的MucangHttpClient,自己可以决定是每次new一个新的,还是使用一个自己创建好的
     *
     * @return
     */
    protected HttpClient getMucangHttpClient() {
        return HttpClient.getDefault();
    }

    /**
     * 如果子类需要传递额外的参数列表，则重写此方法
     */
    protected Map<String, String> getExtraParams() {
        return null;
    }

    protected String buildFullUrl(String url) {
        StringBuilder sb = new StringBuilder(url);
        Map<String, String> map = new HashMap<String, String>();
        //自动为每个请求添加一个随机的参数，以供签名所用，并且可以给服务器端进行防重放功能
        map.put("_r", UUID.randomUUID().toString().replace("-", ""));
        Map<String, String> extraMap = getExtraParams();
        //如果子类有额外的参数，则加止之
        if (MiscUtils.isNotEmpty(extraMap)) {
            for (Entry<String, String> entry : extraMap.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        buildJsonUrl(sb, map);
        String signKey = getSignKey();
        String signUrl;
        if (StringUtils.isNotBlank(signKey)) {
            signUrl = SignUtils.signUrl(sb.toString(), getSignKey());
        } else {
            signUrl = sb.toString();
        }
        return getApiHost() + signUrl;
    }

    public enum HttpMethod {
        Post,
        Get;
    }

    public static String safeURLEncode(String s, String encoding) {
        if (s == null) {
            return "";
        }
        try {
            return URLEncoder.encode(s, encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static void buildJsonUrl(StringBuilder sb, Map<String, String> apiExternalParams) {
        if (sb.indexOf("?") == -1) {
            sb.append("?");
        } else if (sb.indexOf("?") != sb.length() - 1) {
            sb.append("&");
        }
        sb.append(buildApiUrlPart(apiExternalParams));
    }

    private static String buildApiUrlPart(Map<String, String> externalParams) {
        StringBuilder sb = new StringBuilder();
        Map<String, String> map = new HashMap<>();
        if (MiscUtils.isNotEmpty(externalParams)) {
            for (Entry<String, String> entry : externalParams.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        for (Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
