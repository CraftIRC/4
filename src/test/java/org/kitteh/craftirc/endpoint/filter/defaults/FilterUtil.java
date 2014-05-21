package org.kitteh.craftirc.endpoint.filter.defaults;

import org.kitteh.craftirc.endpoint.filter.Filter;

import java.lang.reflect.Method;
import java.util.Map;

final class FilterUtil {
    private static Method filterLoad;

    static {
        try {
            FilterUtil.filterLoad = Filter.class.getDeclaredMethod("load", Map.class);
            FilterUtil.filterLoad.setAccessible(true);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    static void loadFilter(Filter filter, Map<Object, Object> data) {
        try {
            FilterUtil.filterLoad.invoke(filter, data);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}