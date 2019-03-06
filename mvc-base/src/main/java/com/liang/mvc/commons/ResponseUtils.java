package com.liang.mvc.commons;

import com.alibaba.fastjson.JSON;
import com.liang.mvc.constants.MvcConstants.ResultCode;
import com.liang.mvc.monitor.ControllerMonitor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import static com.liang.mvc.constants.MvcConstants.ResultCode.*;

/**
 * Created by liangzhiyan on 2017/4/11.
 */
public class ResponseUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseUtils.class);
    private static final String CHARSET = "UTF-8";

    public static void writeToResponse(HttpServletResponse response, String json) {
        if (StringUtils.isBlank(json))
            return;
        try {
            response.setContentType("application/json;charset=" + CHARSET);
            response.setContentLength(json.getBytes(CHARSET).length);
            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();
        } catch (Exception e) {
            LOG.error("Response write has error json={}", e.getMessage(), json, e);
        }
    }

    public static void writeToResponse(HttpServletResponse response, ResponseData responseData) {
        if (responseData == null) {
            return;
        }
        ControllerMonitor.get().setOutParam(responseData);
        writeToResponse(response, JSON.toJSONString(responseData));
    }

    public static ResponseData FlowControlErrorResponse() {
        ResponseData responseData = new ResponseData();
        responseData.setErrorId(ResultCode.USER_ACCESS_ERROR.code);
        responseData.setMessage(ResultCode.USER_ACCESS_ERROR.msg);
        responseData.setSuccess(false);
        return responseData;
    }

    public static ResponseData ErrorResponse() {
        ResponseData responseData = new ResponseData();
        responseData.setErrorId(ResultCode.ACCESS_ERROR.code);
        responseData.setMessage(ResultCode.ACCESS_ERROR.msg);
        responseData.setSuccess(false);
        return responseData;
    }

    public static ResponseData SuccessResponse() {
        ResponseData responseData = new ResponseData();
        responseData.setErrorId(ResultCode.SUCCESS.code);
        responseData.setMessage(ResultCode.SUCCESS.msg);
        responseData.setSuccess(true);
        return responseData;
    }

    public static ResponseData NotLoginResponse() {
        ResponseData responseData = new ResponseData();
        responseData.setErrorId(ResultCode.NOT_LOGIN_ERROR.code);
        responseData.setMessage(ResultCode.NOT_LOGIN_ERROR.msg);
        responseData.setSuccess(false);
        return responseData;
    }
}
