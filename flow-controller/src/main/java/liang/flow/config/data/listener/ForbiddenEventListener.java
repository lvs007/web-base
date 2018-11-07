package liang.flow.config.data.listener;

import liang.flow.config.ConfigService.InterfaceQps;
import liang.flow.config.ControllerObject;
import liang.flow.config.FlowConfig;
import liang.flow.config.data.entity.Forbidden;
import liang.flow.interceptor.FlowController;
import liang.mvc.event.EventListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * Created by liangzhiyan on 2018/3/22.
 */
@Service
public class ForbiddenEventListener implements EventListener<ForbiddenEvent> {
    @Override
    public void listener(ForbiddenEvent event) {
        switch (event.getType()) {
            case UPDATE:
            case CREATE:
                change(event);
                break;
            case DELETE:
                break;
            default:
                break;
        }
    }

    private void change(ForbiddenEvent event) {
        Forbidden forbidden = new Forbidden();
        BeanUtils.copyProperties(event.getForbiddenVo(), forbidden);
        FlowController.changeListener(InterfaceQps.buildInterfaceQps(forbidden));
        FlowConfig.changeListener(ControllerObject.buildControllerObject(forbidden));
    }
}
