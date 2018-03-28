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
public abstract class AbstractLocalLoadingCache<K, V> implements BaseCache<K, V> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractLocalLoadingCache.class);

    private int initialCapacity = 100000;
    private long maximumSize = 1000000;
    private long expireTime = 24 * 60 * 60 * 1000;

    private LoadingCache<K, V> cache;

    public AbstractLocalLoadingCache() {
        LOG.info("init AbstractLocalLoadingCache");
        cache = CacheBuilder.newBuilder().initialCapacity(getInitialCapacity()).maximumSize(getMaximumSize()).
                expireAfterAccess(getExpireTime(), TimeUnit.MILLISECONDS).build(new CacheLoader<K, V>() {
            @Override
            public V load(K key) throws Exception {
                return loadData(key);
            }
        });
    }

    public V get(K key) {
        try {
            return cache.get(key);
        } catch (Exception e) {
            LOG.error("数据库中没有对应的用户数据,key:{}", key, e);
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

    protected abstract V loadData(K key);
}
