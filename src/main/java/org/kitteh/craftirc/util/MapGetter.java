/*
 * * Copyright (C) 2014-2015 Matt Baxter http://kitteh.org
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Gets elements from a String-Object map.
 */
public final class MapGetter {
    @Nullable
    public static Map<Object, Object> castToMap(@Nullable Object o) {
        if (o instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<Object, Object> map = (Map<Object, Object>) o;
            return map;
        }
        return null;
    }

    @Nullable
    public static <Type> Type get(@Nullable Map<Object, Object> map, @Nonnull String key, @Nonnull Class<Type> type) {
        if (map == null) {
            return null;
        }
        Object o = map.get(key);
        if (o == null || !type.isAssignableFrom(o.getClass())) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Type t = (Type) o;
        return t;
    }

    @Nullable
    public static List<Object> getList(@Nullable Map<Object, Object> map, @Nonnull String key) {
        @SuppressWarnings("unchecked")
        List<Object> list = get(map, key, List.class);
        return list;
    }

    @Nullable
    public static Map<Object, Object> getMap(@Nullable Map<Object, Object> map, @Nonnull String key) {
        @SuppressWarnings("unchecked")
        Map<Object, Object> newMap = get(map, key, Map.class);
        return newMap;
    }

    @Nullable
    public static String getString(@Nullable Map<Object, Object> map, @Nonnull String key) {
        return get(map, key, String.class);
    }

    @Nullable
    public static Integer getInt(@Nullable Map<Object, Object> map, @Nonnull String key) {
        return get(map, key, Integer.class);
    }
}