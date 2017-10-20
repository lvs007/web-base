package liang.mvc.handler;

import liang.mvc.monitor.ControllerMonitor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;

/**
 * 加入Spring的事件监听器，用来记录请求日志
 */
@Component
public class RequiredListener implements ApplicationListener<ServletRequestHandledEvent> {

    @Override
    public void onApplicationEvent(ServletRequestHandledEvent event) {
        ControllerMonitor.end();
    }

}
