package liang.mvc.commons;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liangzhiyan on 2016/11/28.
 */
public class LogUtils {

    private static final String FILE_PATH = "/logFilter.properties";
    private static final String IS_OPEN_KEY = "log.open";
    private static final String MARK_KEY = "log.filter.word";

    private static final Map<String, Log> map = new ConcurrentHashMap<>();

    public static synchronized Log getInstance(Class clazz) {
        Log log = map.get(clazz.getName());
        if (log != null) {
            return log;
        }
        log = new Log(clazz, new PropertiesConfigReader());
        map.put(clazz.getName(), log);
        return log;
    }

    public static synchronized Log getInstance(Class clazz, ConfigReader configReader) {
        Log log = map.get(clazz.getName() + configReader.getClass().getName());
        if (log != null) {
            return log;
        }
        log = new Log(clazz, configReader);
        map.put(clazz.getName() + configReader.getClass().getName(), log);
        return log;
    }

    public static interface ConfigReader {

        String logFilterWord(String defaultValue);

        boolean logIsOpen(boolean defaultValue);
    }

    private static class PropertiesConfigReader implements ConfigReader {

        private static final long updateInterval = 10000;
        private long lastUpdateTime;
        private Properties properties = new Properties();

        private void init() {
            try {
                long nowTime = System.currentTimeMillis();
                if (nowTime - lastUpdateTime > updateInterval) {
                    lastUpdateTime = nowTime;
                    properties.load(new FileInputStream(LogUtils.class.getResource(FILE_PATH).getPath()));
//                    properties.load(LogUtils.class.getResourceAsStream(FILE_PATH));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String logFilterWord(String defaultValue) {
            init();
            return properties.getProperty(MARK_KEY, defaultValue);
        }

        @Override
        public boolean logIsOpen(boolean defaultValue) {
            init();
            return parse(IS_OPEN_KEY, defaultValue);
        }

        private <T> T parse(String key, T defaultValue) {
            String value = properties.getProperty(key);
            if (StringUtils.isNotBlank(value)) {
                try {
                    if (defaultValue instanceof Long) {
                        return (T) Long.valueOf(value);
                    } else if (defaultValue instanceof Boolean) {
                        return (T) Boolean.valueOf(value);
                    } else if (defaultValue instanceof Integer) {
                        return (T) Integer.valueOf(value);
                    }
                } catch (Exception e) {
                }
            }
            return defaultValue;
        }
    }

    public static class Log implements Logger {

        private static final String MARK = "all";
        private static final long updateInterval = 60000;

        private boolean isOpen;
        private String[] markNameArray;
        private long lastUpdateTime;

        private Logger logger;

        private ConfigReader configReader;

        private Log(Class clazz, ConfigReader configReader) {
            logger = LoggerFactory.getLogger(clazz);
            this.configReader = configReader;
        }

        private void init() {
            long nowTime = System.currentTimeMillis();
            if (nowTime - lastUpdateTime > updateInterval) {
                lastUpdateTime = nowTime;
                try {
                    isOpen = configReader.logIsOpen(false);
                    String mark = configReader.logFilterWord("");
                    if (StringUtils.isNotBlank(mark)) {
                        markNameArray = mark.split(",");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean isPrint(String msg) {
            init();
            if (isOpen && markNameArray != null) {
                for (String mark : markNameArray) {
                    if (StringUtils.contains(msg, mark) || StringUtils.equalsIgnoreCase(mark, MARK)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public void debugLog(String msg) {
            if (isPrint(msg)) {
                info(msg);
            }
        }

        public void debugLog(String format, Object... arguments) {
            if (isPrint(format)) {
                info(format, arguments);
            }
        }

        public void debugLog(String msg, Throwable t) {
            if (isPrint(msg)) {
                info(msg, t);
            }
        }


        @Override
        public String getName() {
            return logger.getName();
        }

        @Override
        public boolean isTraceEnabled() {
            return logger.isTraceEnabled();
        }

        @Override
        public void trace(String msg) {
            logger.trace(msg);
        }

        @Override
        public void trace(String format, Object arg) {
            logger.trace(format, arg);
        }

        @Override
        public void trace(String format, Object arg1, Object arg2) {
            logger.trace(format, arg1, arg2);
        }

        @Override
        public void trace(String format, Object... arguments) {
            logger.trace(format, arguments);
        }

        @Override
        public void trace(String msg, Throwable t) {
            logger.trace(msg, t);
        }

        @Override
        public boolean isTraceEnabled(Marker marker) {
            return logger.isTraceEnabled(marker);
        }

        @Override
        public void trace(Marker marker, String msg) {
            logger.trace(marker, msg);
        }

        @Override
        public void trace(Marker marker, String format, Object arg) {
            logger.trace(marker, format, arg);
        }

        @Override
        public void trace(Marker marker, String format, Object arg1, Object arg2) {
            logger.trace(marker, format, arg1, arg2);
        }

        @Override
        public void trace(Marker marker, String format, Object... argArray) {
            logger.trace(marker, format, argArray);
        }

        @Override
        public void trace(Marker marker, String msg, Throwable t) {
            logger.trace(marker, msg, t);
        }

        @Override
        public boolean isDebugEnabled() {
            return logger.isDebugEnabled();
        }

        @Override
        public void debug(String msg) {
            logger.debug(msg);
        }

        @Override
        public void debug(String format, Object arg) {
            logger.debug(format, arg);
        }

        @Override
        public void debug(String format, Object arg1, Object arg2) {
            logger.debug(format, arg1, arg2);
        }

        @Override
        public void debug(String format, Object... arguments) {
            logger.debug(format, arguments);
        }

        @Override
        public void debug(String msg, Throwable t) {
            logger.debug(msg, t);
        }

        @Override
        public boolean isDebugEnabled(Marker marker) {
            return logger.isDebugEnabled(marker);
        }

        @Override
        public void debug(Marker marker, String msg) {
            logger.debug(marker, msg);
        }

        @Override
        public void debug(Marker marker, String format, Object arg) {
            logger.debug(marker, format, arg);
        }

        @Override
        public void debug(Marker marker, String format, Object arg1, Object arg2) {
            logger.debug(marker, format, arg1, arg2);
        }

        @Override
        public void debug(Marker marker, String format, Object... arguments) {
            logger.debug(marker, format, arguments);
        }

        @Override
        public void debug(Marker marker, String msg, Throwable t) {
            logger.debug(marker, msg, t);
        }

        @Override
        public boolean isInfoEnabled() {
            return logger.isInfoEnabled();
        }

        @Override
        public void info(String msg) {
            logger.info(msg);
        }

        @Override
        public void info(String format, Object arg) {
            logger.info(format, arg);
        }

        @Override
        public void info(String format, Object arg1, Object arg2) {
            logger.info(format, arg1, arg2);
        }

        @Override
        public void info(String format, Object... arguments) {
            logger.info(format, arguments);
        }

        @Override
        public void info(String msg, Throwable t) {
            logger.info(msg, t);
        }

        @Override
        public boolean isInfoEnabled(Marker marker) {
            return logger.isInfoEnabled(marker);
        }

        @Override
        public void info(Marker marker, String msg) {
            logger.info(marker, msg);
        }

        @Override
        public void info(Marker marker, String format, Object arg) {
            logger.info(marker, format, arg);
        }

        @Override
        public void info(Marker marker, String format, Object arg1, Object arg2) {
            logger.info(marker, format, arg1, arg2);
        }

        @Override
        public void info(Marker marker, String format, Object... arguments) {
            logger.info(marker, format, arguments);
        }

        @Override
        public void info(Marker marker, String msg, Throwable t) {
            logger.info(marker, msg, t);
        }

        @Override
        public boolean isWarnEnabled() {
            return logger.isWarnEnabled();
        }

        @Override
        public void warn(String msg) {
            logger.warn(msg);
        }

        @Override
        public void warn(String format, Object arg) {
            logger.warn(format, arg);
        }

        @Override
        public void warn(String format, Object... arguments) {
            logger.warn(format, arguments);
        }

        @Override
        public void warn(String format, Object arg1, Object arg2) {
            logger.warn(format, arg1, arg2);
        }

        @Override
        public void warn(String msg, Throwable t) {
            logger.warn(msg, t);
        }

        @Override
        public boolean isWarnEnabled(Marker marker) {
            return logger.isWarnEnabled(marker);
        }

        @Override
        public void warn(Marker marker, String msg) {
            logger.warn(marker, msg);
        }

        @Override
        public void warn(Marker marker, String format, Object arg) {
            logger.warn(marker, format, arg);
        }

        @Override
        public void warn(Marker marker, String format, Object arg1, Object arg2) {
            logger.warn(marker, format, arg1, arg2);
        }

        @Override
        public void warn(Marker marker, String format, Object... arguments) {
            logger.warn(marker, format, arguments);
        }

        @Override
        public void warn(Marker marker, String msg, Throwable t) {
            logger.warn(marker, msg, t);
        }

        @Override
        public boolean isErrorEnabled() {
            return logger.isErrorEnabled();
        }

        @Override
        public void error(String msg) {
            logger.error(msg);
        }

        @Override
        public void error(String format, Object arg) {
            logger.error(format, arg);
        }

        @Override
        public void error(String format, Object arg1, Object arg2) {
            logger.error(format, arg1, arg2);
        }

        @Override
        public void error(String format, Object... arguments) {
            logger.error(format, arguments);
        }

        @Override
        public void error(String msg, Throwable t) {
            logger.error(msg, t);
        }

        @Override
        public boolean isErrorEnabled(Marker marker) {
            return logger.isErrorEnabled(marker);
        }

        @Override
        public void error(Marker marker, String msg) {
            logger.error(marker, msg);
        }

        @Override
        public void error(Marker marker, String format, Object arg) {
            logger.error(marker, format, arg);
        }

        @Override
        public void error(Marker marker, String format, Object arg1, Object arg2) {
            logger.error(marker, format, arg1, arg2);
        }

        @Override
        public void error(Marker marker, String format, Object... arguments) {
            logger.error(marker, format, arguments);
        }

        @Override
        public void error(Marker marker, String msg, Throwable t) {
            logger.error(marker, msg, t);
        }
    }
}
