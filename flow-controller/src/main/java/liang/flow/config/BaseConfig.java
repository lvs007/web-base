package liang.flow.config;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liangzhiyan on 2017/4/4.
 */
public abstract class BaseConfig {

    private static final String ALL = "all";

    private static final Map<String, Map<ControllerType, Map<String, ControllerObject>>> controllerObjectMapMapMap = new ConcurrentHashMap<>();

    private static volatile boolean IS_OPEN = false;

    public static void changeListener(ControllerObject controllerObject) {
        addFlowObject(controllerObject);
    }

    public static void changeListener(List<ControllerObject> controllerObjectList) {
        for (ControllerObject controllerObject : controllerObjectList) {
            addFlowObject(controllerObject);
        }
    }

    public static void init(List<ControllerObject> controllerObjectList) {
        if (controllerObjectList != null) {
            for (ControllerObject controllerObject : controllerObjectList) {
                addFlowObject(controllerObject);
            }
        }
    }

    private static void addFlowObject(ControllerObject controllerObject) {
        Map<ControllerType, Map<String, ControllerObject>> controllerObjectMapMap = controllerObjectMapMapMap.get(controllerObject.getUri());
        if (MapUtils.isEmpty(controllerObjectMapMap)) {
            controllerObjectMapMap = new ConcurrentHashMap<>();
            controllerObjectMapMapMap.put(controllerObject.getUri(), controllerObjectMapMap);
        }
        Map<String, ControllerObject> controllerObjectMap = controllerObjectMapMap.get(controllerObject.getControllerType());
        if (MapUtils.isEmpty(controllerObjectMap)) {
            controllerObjectMap = new ConcurrentHashMap<>();
            controllerObjectMapMap.put(controllerObject.getControllerType(), controllerObjectMap);
        }
        controllerObjectMap.put(controllerObject.getValue(), controllerObject);
    }

    public static Map<String, Map<ControllerType, Map<String, ControllerObject>>> get() {
        return controllerObjectMapMapMap;
    }

    public static Map<ControllerType, Map<String, ControllerObject>> getControllerObjectMapMap(String uri) {
        return controllerObjectMapMapMap.get(uri);
    }

    public static Map<String, ControllerObject> getControllerObjectMap(ControllerType controllerType, String uri) {
        if (controllerType == null || StringUtils.isBlank(uri) || !controllerObjectMapMapMap.containsKey(uri)) {
            return null;
        } else {
            Map<String, ControllerObject> controllerObjectMap = controllerObjectMapMapMap.get(uri).get(controllerType);
            if (MapUtils.isEmpty(controllerObjectMap)) {
                controllerObjectMap = controllerObjectMapMapMap.get(ALL).get(controllerType);
            }
            return controllerObjectMap;
        }
    }

    public static ControllerObject getControllerObject(ControllerType controllerType, String uri, String value) {
        if (controllerType == null || StringUtils.isBlank(uri) || !controllerObjectMapMapMap.containsKey(uri)) {
            return null;
        } else {
            Map<String, ControllerObject> controllerObjectMap = controllerObjectMapMapMap.get(uri).get(controllerType);
            if (MapUtils.isEmpty(controllerObjectMap)) {
                controllerObjectMap = controllerObjectMapMapMap.get(ALL).get(controllerType);
            }
            if (MapUtils.isEmpty(controllerObjectMap)) {
                return controllerObjectMap.get(value);
            } else {
                return null;
            }
        }
    }

    public static void openUrlControl(ControllerType controllerType, String uri) {
        if (controllerType != ControllerType.URL) {
            return;
        }
        if (IS_OPEN) {
            return;
        }
        IS_OPEN = true;
        Map<String, ControllerObject> controllerObjectMap = getControllerObjectMap(controllerType, uri);
        for (ControllerObject controllerObject : controllerObjectMap.values()) {
            controllerObject.setOpen(true);
        }
    }

    public static void clean(String uri) {
        Map<ControllerType, Map<String, ControllerObject>> controllerTypeMapMap = getControllerObjectMapMap(uri);
        if (MapUtils.isNotEmpty(controllerTypeMapMap)) {
            for (Map<String, ControllerObject> controllerObjectMap : controllerTypeMapMap.values()) {
                for (ControllerObject controllerObject : controllerObjectMap.values()) {

                }
            }
        }
    }
}
