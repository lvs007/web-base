package liang.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by liangzhiyan on 2017/3/17.
 */
public abstract class AbstractLocalCache<K, V> implements BaseCache<K, V> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractLocalCache.class);

    private static final int INITIAL_CAPACITY_J = 100000;
    private static final long MAXIMUM_SIZE_J = 1000000;
    private static final long EXPIRE_TIME_J = 24 * 60 * 60 * 1000;

    private int initialCapacity;
    private long maximumSize;
    private long expireTime;

    private Cache<String, V> cache;

    public AbstractLocalCache() {
        this(INITIAL_CAPACITY_J, MAXIMUM_SIZE_J, EXPIRE_TIME_J);
    }

    public AbstractLocalCache(long expireTime) {
        this(INITIAL_CAPACITY_J, MAXIMUM_SIZE_J, expireTime);
    }

    public AbstractLocalCache(int initialCapacity, long maximumSize, long expireTime) {
        this.initialCapacity = initialCapacity;
        this.maximumSize = maximumSize;
        this.expireTime = expireTime;
        LOG.info("init AbstractLocalCache;initialCapacity:{},maximumSize:{},expireTime:{}",
                getInitialCapacity(), getMaximumSize(), getExpireTime());
        cache = CacheBuilder.newBuilder().initialCapacity(getInitialCapacity()).maximumSize(getMaximumSize()).
                expireAfterAccess(getExpireTime(), TimeUnit.MILLISECONDS).build();
    }

    public V get(K key) {
        String keyStr = buildKey(key);
        try {
            return cache.getIfPresent(keyStr);
        } catch (Exception e) {
            LOG.error("缓存中没有对应的数据,key:{}", keyStr, e);
            return null;
        }
    }

    public boolean set(K key, V value) {
        if (value == null) {
            return false;
        }
        String keyStr = buildKey(key);
        cache.put(keyStr, value);
        return true;
    }

    public boolean remove(K key) {
        String keyStr = buildKey(key);
        cache.invalidate(keyStr);
        return true;
    }

    public int getInitialCapacity() {
        return initialCapacity;
    }


    public long getMaximumSize() {
        return maximumSize;
    }

    public long getExpireTime() {
        return expireTime;
    }

}
