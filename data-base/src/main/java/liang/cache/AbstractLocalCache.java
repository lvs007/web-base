package liang.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Created by liangzhiyan on 2017/3/17.
 */
public abstract class AbstractLocalCache<K, V> implements BaseCache<K, V> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractLocalCache.class);

    private int initialCapacity = 100000;
    private long maximumSize = 1000000;
    private long expireTime = 24 * 60 * 60;

    private Cache<K, V> cache;

    @PostConstruct
    private void init() {
        cache = CacheBuilder.newBuilder().initialCapacity(getInitialCapacity()).maximumSize(getMaximumSize()).
                expireAfterAccess(getExpireTime(), TimeUnit.SECONDS).build();
    }

    public V get(K userName) {
        try {
            return cache.getIfPresent(userName);
        } catch (Exception e) {
            LOG.error("缓存中没有对应的数据,key:{}", userName, e);
            return null;
        }
    }

    public boolean set(K key, V value) {
        if (value == null) {
            return false;
        }
        cache.put(key, value);
        return true;
    }

    public boolean remove(K key) {
        cache.invalidate(key);
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
