package liang.common.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by liangzhiyan on 2017/4/7.
 */
public class LockUtils {

    private static final int TRY_TIMES = 5;

    private static final LoadingCache<String, ReentrantLock> lockCache = CacheBuilder.newBuilder()
            .initialCapacity(10000).maximumSize(1000000).expireAfterAccess(1, TimeUnit.MINUTES)
            .build(new CacheLoader<String, ReentrantLock>() {
                @Override
                public ReentrantLock load(String key) throws Exception {
                    return new ReentrantLock();
                }
            });

    public static void lock(String key) {
        int tryTimes = 0;
        while (tryTimes < TRY_TIMES) {
            ReentrantLock reentrantLock = lockCache.getIfPresent(key);
            if (reentrantLock == null) {
                ++tryTimes;
            } else {
                reentrantLock.lock();
                return;
            }
        }
    }

    public static boolean tryLock(String key) {
        ReentrantLock reentrantLock = get(key);
        if (reentrantLock == null) {
            return false;
        }
        return reentrantLock.tryLock();
    }

    public static boolean tryLock(String key, long timeOut) throws InterruptedException {
        ReentrantLock reentrantLock = lockCache.getIfPresent(key);
        if (reentrantLock == null) {
            return false;
        }
        return reentrantLock.tryLock(timeOut, TimeUnit.MILLISECONDS);
    }

    public static void unLock(String key) {
        ReentrantLock reentrantLock = get(key);
        if (reentrantLock != null) {
            reentrantLock.unlock();
        }
    }

    public static ReentrantLock get(String key) {
        try {
            return lockCache.get(key);
        } catch (Exception e) {
            return null;
        }
    }
}
