package com.liang.dao.jdbc.split.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liangzhiyan on 2017/7/10.
 */
public class LogUtil {

    private static final Logger LOG = LoggerFactory.getLogger(LogUtil.class);

    public static void info(String format, Object... arguments) {
        if (CommonBaseConfig.isIsPrintLog()) {
            LOG.info(format, arguments);
        }
    }
}
