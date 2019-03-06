package com.liang.cache;

/**
 * Created by liangzhiyan on 2017/5/30.
 */
public interface BaseService {

    <K, V> V findByKey(K key);
}
