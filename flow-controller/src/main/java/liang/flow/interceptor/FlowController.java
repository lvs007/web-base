package liang.flow.interceptor;

import com.google.common.util.concurrent.RateLimiter;
import liang.flow.config.ControlParameter;
import liang.flow.config.FlowConfig;
import liang.flow.core.BaseFlowController;
import liang.flow.config.ConfigService;
import liang.flow.core.ControllerManager;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * Created by liangzhiyan on 2017/4/11.
 */
public class FlowController extends BaseController {

    private static final Map<String, RateLimiter> rateLimiterMap = new HashMap<>();
    private static final Map<String, Semaphore> semaphoreMap = new HashMap<>();

    private static final ThreadLocal<Boolean> threadLocal = new ThreadLocal<>();

    public static void init() {
        List<ConfigService.InterfaceQps> interfaceQpsList = ConfigService.getInterfaceQps();
        for (ConfigService.InterfaceQps interfaceQps : interfaceQpsList) {
            setMap(interfaceQps);
        }
    }

    private static void setMap(ConfigService.InterfaceQps interfaceQps) {
        RateLimiter rateLimiter = RateLimiter.create(interfaceQps.getQps());
        Semaphore semaphore = new Semaphore(interfaceQps.getSameTimeQ());
        rateLimiterMap.put(interfaceQps.getUrl(), rateLimiter);
        semaphoreMap.put(interfaceQps.getUrl(), semaphore);
    }

    public static void changeListener(ConfigService.InterfaceQps interfaceQps) {
        setMap(interfaceQps);
    }

    public static boolean controlRequest(HttpServletRequest request) {
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
                if (!rateLimiter.tryAcquire()) {//当获取不到的时候开启流控
                    ControlParameter controlParameter = setControlParameter(request);
                    for (BaseFlowController flowController : ControllerManager.controllerList) {
                        FlowConfig.openUrlControl(flowController.getControllerType(), uri);
                        if (flowController.flowControl(controlParameter)) {
                            return false;
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

    public static void afterCompletion(HttpServletRequest request) {
        String uri = request.getRequestURI();
        try {
            Semaphore semaphore = semaphoreMap.get(uri);
            if (semaphore != null && threadLocal.get()) {
                semaphore.release();
                threadLocal.remove();
            }
        } catch (Exception e) {
            LOG.error("", e);
        }
    }
}
