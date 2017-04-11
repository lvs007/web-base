package liang.flow.core;

import liang.flow.config.ControllerObject;
import liang.flow.config.ControllerType;
import liang.flow.config.FlowConfig;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by liangzhiyan on 2017/4/5.
 */
public class ControllerManager {

    public static final List<BaseFlowController> controllerList = new ArrayList<>();

    public static void clean(String uri) {
        Map<ControllerType,Map<String,ControllerObject>> controllerTypeMapMap = FlowConfig.getControllerObjectMapMap(uri);
        if (MapUtils.isNotEmpty(controllerTypeMapMap)) {
            for (Map<String,ControllerObject> controllerObjectMap : controllerTypeMapMap.values()){
                for (ControllerObject controllerObject : controllerObjectMap.values()){

                }
            }
        }
    }
}
