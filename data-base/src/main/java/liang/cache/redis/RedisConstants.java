package liang.cache.redis;

/**
 * Created by liangzhiyan on 2017/6/2.
 */
public class RedisConstants {

    public static final String OK = "ok";

    /**
     * NX|XX, NX -- Only set the key if it does not already exist. XX -- Only set the key
     * if it already exist.
     */
    public static enum Commands {
        NX,
        XX;
    }

    /**
     * EX|PX, expire time units: EX = seconds; PX = milliseconds
     */
    public static enum TimeUnit {
        EX,
        PX;
    }
}
