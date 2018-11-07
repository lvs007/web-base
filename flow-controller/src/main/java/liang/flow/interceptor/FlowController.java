package liang.flow.interceptor;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import liang.common.LogUtils;
import liang.flow.config.ConfigService;
import liang.flow.config.ControlParameter;
import liang.flow.config.FlowConfig;
import liang.flow.core.BaseFlowController;
import liang.flow.core.ControllerManager;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by liangzhiyan on 2017/4/11.
 */
public class FlowController extends BaseController {

    private static final Map<String, RateLimiter> rateLimiterMap = new HashMap<>();
    private static final Map<String, Semaphore> semaphoreMap = new HashMap<>();
    private static final Map<String, AtomicLong> qpsCount = new HashMap<>();
    private static final Map<String, AtomicLong> semaphoreCount = new HashMap<>();
    private static final Cache<String, AtomicLong> qpsCountCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.SECONDS).initialCapacity(5).maximumSize(5).build();

    private static final ThreadLocal<Boolean> threadLocal = new ThreadLocal<>();

    private static Object qpsLock = new Object();

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
        int sed = (int) (System.currentTimeMillis() / 1000);
        qpsCount.put(interfaceQps.getUrl() + sed, new AtomicLong(0));
        semaphoreCount.put(interfaceQps.getUrl() + sed, new AtomicLong(0));
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
                    LogUtils.getInstance(FlowController.class).info("rateLimiter 开启流控");
                    ControlParameter controlParameter = setControlParameter(request);
                    for (BaseFlowController flowController : ControllerManager.controllerList) {
                        FlowConfig.openUrlControl(flowController.getControllerType(), uri);
                        if (flowController.flowControl(controlParameter)) {
                            semaphore.release();//这么做的目的是为了防止返回false不执行afterCompletion
                            return false;
                        }
                    }
                }
            }
        } else {
            LogUtils.getInstance(FlowController.class).info("semaphore controlRequest并行流控开启");
            threadLocal.set(false);
            return false;
        }
        return true;
    }

    private void countQps(String uri) {
        int sed = (int) (System.currentTimeMillis() / 1000);
        AtomicLong atomicLong = qpsCount.get(uri + sed);
        if (atomicLong == null) {
            synchronized (qpsLock) {
                atomicLong = qpsCount.get(uri + sed);
                if (atomicLong == null) {
                    qpsCount.put(uri + sed, new AtomicLong(1));
                } else {
                    countQps(uri);
                }
            }
        } else {
            atomicLong.addAndGet(1);
        }
    }

    public static void afterCompletion(HttpServletRequest request) {
        String uri = request.getRequestURI();
        try {
            Semaphore semaphore = semaphoreMap.get(uri);
            Boolean bool = threadLocal.get();
            threadLocal.remove();
            if (semaphore != null && bool != null && bool) {
                semaphore.release();
            }
        } catch (Exception e) {
            LOG.error("", e);
        }
    }
}
