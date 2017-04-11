package liang.flow.core;

import liang.flow.config.ControlParameter;
import liang.flow.config.ControllerObject;
import liang.flow.config.ControllerType;
import liang.flow.config.FlowConfig;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.Map;

/**
 * Created by liangzhiyan on 2017/4/5.
 */
public class UrlFlowController extends AbstractController implements BaseFlowController {
    @Override
    public boolean flowControl(ControlParameter controlParameter) {
        return flowControl(controlParameter.getCurrentUri(), controlParameter.getUrl(), ControllerType.URL);
    }

    @Override
    public boolean forbitddenControl(ControlParameter controlParameter) {
        return forbitddenControl(controlParameter.getCurrentUri(), controlParameter.getUrl(), ControllerType.URL);
    }

    @Override
    public ControllerType getControllerType() {
        return ControllerType.URL;
    }

}
