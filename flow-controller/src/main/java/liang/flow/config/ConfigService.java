package liang.flow.config;

import liang.common.util.PropertiesManager;
import liang.flow.config.data.entity.Forbidden;
import liang.flow.interceptor.FlowController;
import liang.mvc.commons.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by liangzhiyan on 2017/4/1.
 */
public class ConfigService {

    //todo
    private static final String INTERFACE_QPS = "xxxxxx";
    private static final String SPLIT = ",";
    private static final String URL_QPS_SPLIT = "|";

    private static final PropertiesManager propertiesManager = SpringContextHolder.getBean("propertiesManager");

    public static List<InterfaceQps> getInterfaceQps() {
        String interfaceQpsStr = propertiesManager.getString(INTERFACE_QPS);
        if (StringUtils.isBlank(interfaceQpsStr)) {
            return Collections.emptyList();
        }
        String[] interfaceQpsArray = StringUtils.split(interfaceQpsStr, SPLIT);
        List<InterfaceQps> interfaceQpsList = new ArrayList<>();
        for (String interfaceQps : interfaceQpsArray) {
            InterfaceQps iq = genInterfaceQps(interfaceQps);
            interfaceQpsList.add(iq);
        }
        return interfaceQpsList;
    }

    private static InterfaceQps genInterfaceQps(String interfaceQps) {
        String[] urlAndQps = StringUtils.split(interfaceQps, URL_QPS_SPLIT);
        InterfaceQps iq = new InterfaceQps(urlAndQps[0], Long.parseLong(urlAndQps[1]));
        if (urlAndQps.length > 2) {
            iq.setSameTimeQ(Integer.parseInt(urlAndQps[2]));
        }
        return iq;
    }

    public static void changeListener(InterfaceQps interfaceQps) {
        FlowController.changeListener(interfaceQps);
    }

    public static boolean isOpenController() {
        return StringUtils.equalsIgnoreCase("true", propertiesManager.getString("", "false"));
    }

    public static boolean isControlAllRequest() {
        return StringUtils.equalsIgnoreCase("true", propertiesManager.getString("", "false"));
    }

    public static class InterfaceQps {
        private String url;
        private long qps;
        private int sameTimeQ;//并发访问量

        public InterfaceQps(String url, long qps) {
            this.url = url;
            this.qps = qps;
        }

        public InterfaceQps(String url, long qps, int sameTimeQ) {
            this.url = url;
            this.qps = qps;
            this.sameTimeQ = sameTimeQ;
        }

        public static InterfaceQps buildInterfaceQps(Forbidden forbidden) {
            InterfaceQps interfaceQps = new InterfaceQps(forbidden.getUri(), forbidden.getQps(), forbidden.getSameTimeQ());
            return interfaceQps;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public long getQps() {
            return qps;
        }

        public void setQps(long qps) {
            this.qps = qps;
        }

        public int getSameTimeQ() {
            return sameTimeQ;
        }

        public void setSameTimeQ(int sameTimeQ) {
            this.sameTimeQ = sameTimeQ;
        }
    }
}
