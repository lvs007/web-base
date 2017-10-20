package liang.cache;

/**
 * Created by liangzhiyan on 2017/3/17.
 */
public interface BaseCache<K, V> {
    V get(K key);

    boolean set(K key, V value);

    boolean set(K key, V value, long expire);

    boolean setNx(K key, V value, long expire);

    boolean remove(K key);

    String buildKey(K key);
}
