package org.kitteh.craftirc.util;

import java.util.Map;

/**
 * Gets elements from a String-Object map.
 */
public final class MapGetter {
    public static Map<Object, Object> castToMap(Object o) {
        if (o instanceof Map) {
            return (Map<Object, Object>) o;
        }
        return null;
    }

    public static <T> T get(Map<Object, Object> map, String key, Class<T> type) {
        if (map == null) {
            return null;
        }
        Object o = map.get(key);
        if (o == null || !type.isAssignableFrom(o.getClass())) {
            return null;
        }
        return (T) o;
    }

    public static Map<Object, Object> getMap(Map<Object, Object> map, String key) {
        return get(map, key, Map.class);
    }

    public static String getString(Map<Object, Object> map, String key) {
        return get(map, key, String.class);
    }

    public static Integer getInt(Map<Object, Object> map, String key) {
        return get(map, key, Integer.class);
    }
}