package liang.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kiven on 2018/5/1.
 */
public class ThreadUtils {

    private static final Logger log = LoggerFactory.getLogger(ThreadUtils.class);

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("", e);
            Thread.currentThread().interrupt();
        }
    }
}
