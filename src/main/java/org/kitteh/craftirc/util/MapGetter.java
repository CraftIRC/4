package org.kitteh.craftirc.util;

import java.util.Map;

/**
 * Gets elements from a String-Object map.
 */
public final class MapGetter {
    public static Map<?, ?> castToMap(Object o) {
        if (Map.class.isAssignableFrom(o.getClass())) {
            return (Map<?, ?>) o;
        }
        return null;
    }

    public static <T> T get(Map<?, ?> map, String key, Class<T> type) {
        if (map == null) {
            return null;
        }
        Object o = map.get(key);
        if (o == null || !type.isAssignableFrom(o.getClass())) {
            return null;
        }
        return (T) o;
    }

    public static String getString(Map<?, ?> map, String key) {
        return get(map, key, String.class);
    }

    public static Integer getInt(Map<?,?> map, String key) {
        return get(map, key, Integer.class);
    }
}