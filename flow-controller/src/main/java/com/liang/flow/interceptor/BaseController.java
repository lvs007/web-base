package com.liang.flow.interceptor;

import com.liang.common.util.MiscUtils;
import com.liang.flow.config.ControlParameter;
import com.liang.mvc.filter.LoginUtils;
import com.liang.mvc.filter.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liangzhiyan on 2017/4/11.
 */
public abstract class BaseController {

    protected static final Logger LOG = LoggerFactory.getLogger(BaseController.class);

    protected static ControlParameter setControlParameter(HttpServletRequest request) {
        ControlParameter controlParameter = new ControlParameter();
        controlParameter.setCurrentUri(request.getRequestURI());
        controlParameter.setUrl(request.getRequestURI());
        UserInfo userInfo = LoginUtils.getCurrentUser(request);//todo：循环调用bug,所以设置拦截器的时候需要把获取用户信息的uri去掉
        if (userInfo != null) {
            controlParameter.setUser(userInfo.getUserName());
        }
        controlParameter.setDevice(request.getParameter("device"));
        controlParameter.setIp(MiscUtils.getIp(request));
        controlParameter.setAppType(request.getParameter("appType"));
        controlParameter.setOs(request.getParameter("os"));
        controlParameter.setCounty(request.getParameter("county"));
        controlParameter.setCity(request.getParameter("city"));
        controlParameter.setArea(request.getParameter("area"));
        controlParameter.setCountry(request.getParameter("country"));
        return controlParameter;
    }
}
