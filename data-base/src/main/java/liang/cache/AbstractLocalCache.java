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

    private int initialCapacity = 100000;
    private long maximumSize = 1000000;
    private long expireTime = 24 * 60 * 60 * 1000;

    private Cache<String, V> cache;

    public AbstractLocalCache() {
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

    public void setInitialCapacity(int initialCapacity) {
        this.initialCapacity = initialCapacity;
    }

    public long getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(long maximumSize) {
        this.maximumSize = maximumSize;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

}
