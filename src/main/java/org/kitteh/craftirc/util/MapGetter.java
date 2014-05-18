package org.kitteh.craftirc.util;

import java.util.Map;

/**
 * Gets elements from a String-Object map.
 */
public final class MapGetter {
    public static Map<Object, Object> castToMap(Object o) {
        if (Map.class.isAssignableFrom(o.getClass())) {
            return (Map<Object, Object>) o;
        }
        return null;
    }

    public static Map<String, Object> castToStringObjectMap(Object o) {
        if (!(o instanceof Map)) {
            return null;
        }
        final Map<?, ?> map = (Map<?, ?>) o;
        for (final Map.Entry<?, ?> entry : map.entrySet()) {
            if (!(entry.getKey() instanceof String)) {
                return null;
            }
        }
        return (Map<String, Object>) map;
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

    public static Integer getInt(Map<?, ?> map, String key) {
        return get(map, key, Integer.class);
    }
}