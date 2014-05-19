/*
 * * Copyright (C) 2014 Matt Baxter http://kitteh.org
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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