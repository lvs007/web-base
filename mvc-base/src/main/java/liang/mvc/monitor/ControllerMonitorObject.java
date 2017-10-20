package liang.mvc.monitor;

import com.alibaba.fastjson.JSON;
import liang.mvc.commons.ResponseData;
import liang.mvc.filter.UserInfo;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liangzhiyan on 2017/6/5.
 */
public class ControllerMonitorObject {
    private long beginTime;
    private long endTime;
    private long spendTime;
    private String inParam;
    private String outParam;
    private String userName;
    private long userId;
    private String className;
    private String methodName;
    private int code;
    private boolean success = true;

    public static ControllerMonitorObject create() {
        return new ControllerMonitorObject();
    }

    public long getBeginTime() {
        return beginTime;
    }

    public ControllerMonitorObject setBeginTime(long beginTime) {
        this.beginTime = beginTime;
        return this;
    }

    public long getEndTime() {
        return endTime;
    }

    public ControllerMonitorObject setEndTime(long endTime) {
        this.endTime = endTime;
        return this;
    }

    public long getSpendTime() {
        if (spendTime <= 0 && endTime != 0 && beginTime != 0) {
            spendTime = endTime - beginTime;
        }
        return spendTime;
    }

    public ControllerMonitorObject setSpendTime(long spendTime) {
        this.spendTime = spendTime;
        return this;
    }

    public String getInParam() {
        return inParam;
    }

    public ControllerMonitorObject setInParam(String inParam) {
        this.inParam = inParam;
        return this;
    }

    public ControllerMonitorObject setInParam(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Map<String, String> result = new HashMap<>();
        if (MapUtils.isNotEmpty(map)) {
            for (String paramName : map.keySet()) {
                if (StringUtils.equalsIgnoreCase("password", paramName)) {
                    continue;
                }
                String[] values = map.get(paramName);
                result.put(paramName, StringUtils.join(values, ","));
            }
        }
        this.inParam = JSON.toJSONString(result);
        return this;
    }

    public String getOutParam() {
        return outParam;
    }

    public ControllerMonitorObject setOutParam(String outParam) {
        this.outParam = outParam;
        return this;
    }

    public ControllerMonitorObject setOutParam(Object outParam) {
        if (outParam instanceof ResponseData) {
            ResponseData responseData = (ResponseData) outParam;
            setCode(responseData.getErrorId()).setSuccess(responseData.isSuccess());
        }
        this.outParam = JSON.toJSONString(outParam);
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public ControllerMonitorObject setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public long getUserId() {
        return userId;
    }

    public ControllerMonitorObject setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public ControllerMonitorObject setClassName(String className) {
        this.className = className;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public ControllerMonitorObject setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public ControllerMonitorObject setClassAndMethod(HandlerMethod handlerMethod) {
        if (handlerMethod != null) {
            setClassName(handlerMethod.getBeanType().getName()).setMethodName(handlerMethod.getMethod().getName());
        }
        return this;
    }

    public ControllerMonitorObject setUser(UserInfo userInfo) {
        if (userInfo != null) {
            setUserId(userInfo.getId()).setUserName(userInfo.getUserName());
        }
        return this;
    }

    public int getCode() {
        return code;
    }

    public ControllerMonitorObject setCode(int code) {
        this.code = code;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public ControllerMonitorObject setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String build() {
        return JSON.toJSONString(this);
    }
}
