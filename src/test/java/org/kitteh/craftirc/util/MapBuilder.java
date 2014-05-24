package org.kitteh.craftirc.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Builds maps and gets in fights.
 */
public final class MapBuilder<K, V> {
    private final Map<K, V> map = new HashMap<>();

    public MapBuilder<K, V> add(K key, V value) {
        this.map.put(key, value);
        return this;
    }

    public Map<K, V> map() {
        return this.map;
    }
}