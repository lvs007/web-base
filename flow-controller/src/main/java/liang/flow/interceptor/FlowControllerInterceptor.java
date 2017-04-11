package liang.flow.interceptor;

import com.google.common.util.concurrent.RateLimiter;
import liang.common.util.MiscUtils;
import liang.flow.config.ControlParameter;
import liang.flow.config.ControllerObject;
import liang.flow.config.ControllerType;
import liang.flow.config.FlowConfig;
import liang.flow.core.BaseFlowController;
import liang.flow.core.ConfigService;
import liang.flow.core.ControllerManager;
import liang.mvc.filter.LoginUtils;
import liang.mvc.filter.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * Created by liangzhiyan on 2017/3/31.
 */
public class FlowControllerInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(FlowControllerInterceptor.class);

    private static final Map<String, RateLimiter> rateLimiterMap = new HashMap<>();
    private static final Map<String, Semaphore> semaphoreMap = new HashMap<>();
    private ThreadLocal<Boolean> threadLocal = new ThreadLocal<>();

    private Object lock = new Object();
    private Map<String, Boolean> controlUriMap = new ConcurrentHashMap<>();

    @PostConstruct
    private void init() {
        List<ConfigService.InterfaceQps> interfaceQpsList = ConfigService.getInterfaceQps();
        for (ConfigService.InterfaceQps interfaceQps : interfaceQpsList) {
            RateLimiter rateLimiter = RateLimiter.create(interfaceQps.getQps());
            Semaphore semaphore = new Semaphore(interfaceQps.getSameTimeQ());
            rateLimiterMap.put(interfaceQps.getUrl(), rateLimiter);
            semaphoreMap.put(interfaceQps.getUrl(), semaphore);
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!ConfigService.isOpenController()) {
            return true;
        }
        if (ConfigService.isControlAllRequest()) {
            return false;
        }
        return controlRequest(request);
    }

    private boolean controlRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        Semaphore semaphore = semaphoreMap.get(uri);
        if (semaphore == null) {
            return true;
        }
        if (semaphore.tryAcquire()) {
            threadLocal.set(true);
            RateLimiter rateLimiter = rateLimiterMap.get(uri);
            if (rateLimiter == null) {
                return true;
            } else {
                Boolean clean = controlUriMap.get(uri);
                if (!rateLimiter.tryAcquire()) {//当获取不到的时候开启流控
                    controlUriMap.put(uri, true);
                    for (BaseFlowController flowController : ControllerManager.controllerList) {
                        FlowConfig.openUrlControl(flowController.getControllerType(), uri);
                        ControlParameter controlParameter = setControlParameter(request);
                        if (flowController.control(controlParameter)) {
                            return false;
                        }
                    }
                } else if (clean != null && clean) {//清空数据
                    synchronized (lock) {
                        if (clean) {

                            controlUriMap.put(uri, false);
                            clean = false;
                        }
                    }
                }
            }
        } else {
            threadLocal.set(false);
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String uri = request.getRequestURI();
        super.afterCompletion(request, response, handler, ex);
        try {
            Semaphore semaphore = semaphoreMap.get(uri);
            if (semaphore != null && threadLocal.get()) {
                semaphore.release();
            }
        } catch (Exception e) {
            LOG.error("", e);
        }
    }

    private ControlParameter setControlParameter(HttpServletRequest request) {
        ControlParameter controlParameter = new ControlParameter();
        controlParameter.setCurrentUri(request.getRequestURI());
        controlParameter.setUrl(request.getRequestURI());
        UserInfo userInfo = LoginUtils.getCurrentUser(request);
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
