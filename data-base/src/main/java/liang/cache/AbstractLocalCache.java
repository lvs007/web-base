package liang.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
    private long duration = 24 * 60 * 60;

    private LoadingCache<K, V> cache;

    @PostConstruct
    private void init() {
        cache = CacheBuilder.newBuilder().initialCapacity(getInitialCapacity()).maximumSize(getMaximumSize()).
                expireAfterAccess(getDuration(), TimeUnit.SECONDS).build(new CacheLoader<K, V>() {
            @Override
            public V load(K key) throws Exception {
                return loadData(key);
            }
        });
    }

    public V get(K userName) {
        try {
            return cache.get(userName);
        } catch (Exception e) {
            LOG.error("数据库中没有对应的用户数据," + e);
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    protected abstract V loadData(K key);
}
