package liang.mvc.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liangzhiyan on 2017/6/5.
 */
public class ControllerMonitor {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerMonitor.class);

    public static final ThreadLocal<ControllerMonitorObject> threadLocal = new ThreadLocal<ControllerMonitorObject>() {
        @Override
        protected ControllerMonitorObject initialValue() {
            return ControllerMonitorObject.create();
        }
    };

    public static ControllerMonitorObject get() {
        return threadLocal.get();
    }

    public static void set(ControllerMonitorObject controllerMonitorObject) {
        threadLocal.set(controllerMonitorObject);
    }

    public static void remove() {
        threadLocal.remove();
    }

    public static String toLogString() {
        return get().build();
    }

    public static void printLog() {
        LOG.info(get().build());
    }

    public static void end(){
        ControllerMonitor.get().setEndTime(System.currentTimeMillis());
        ControllerMonitor.printLog();
        ControllerMonitor.remove();
    }

}
