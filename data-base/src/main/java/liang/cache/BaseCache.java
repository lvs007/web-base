package liang.cache;

/**
 * Created by liangzhiyan on 2017/3/17.
 */
public interface BaseCache<K, V> {
    V get(K key);

    boolean set(K key, V value);

    boolean remove(K key);
}
