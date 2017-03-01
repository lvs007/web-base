package liang.utils;

/**
 * Created by mc-050 on 2017/1/16 14:59.
 * KIVEN will tell you life,send email to xxx@163.com
 */

import java.util.HashMap;
import java.util.Map;

public class CaseInsensitiveMap<K, V>
        extends HashMap<K, V> {
    private static final long serialVersionUID = 1L;
    private Map<String, String> keyMap = new HashMap();

    public CaseInsensitiveMap() {
    }

    public CaseInsensitiveMap(int length) {
        super(length);
    }

    public CaseInsensitiveMap(Map<K, V> map) {
        super(map.size());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    private Object findRealKey(Object key) {
        if ((key != null) && ((key instanceof String))) {
            String realKey = (String) this.keyMap.get(key.toString().toLowerCase());
            if (realKey != null) {
                return realKey;
            }
        }
        return key;
    }

    @Override
    public V get(Object key) {
        return (V) super.get(findRealKey(key));
    }

    public V put(K key, V value) {
        if ((key != null) && ((key instanceof String))) {
            String oldKey = (String) this.keyMap.put(key.toString().toLowerCase(), key.toString());
            if (oldKey != null) {
                super.remove(oldKey);
            }
        }
        return super.put((K) findRealKey(key), value);
    }

    public boolean containsKey(Object key) {
        return super.containsKey(findRealKey(key));
    }

    public V remove(Object key) {
        if ((key != null) && ((key instanceof String))) {
            this.keyMap.remove(key.toString().toLowerCase());
        }
        return (V) super.remove(findRealKey(key));
    }
}

