package org.kitteh.craftirc.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Builds maps and gets in fights.
 */
public final class MapBuilder<Key, Value> {
    private final Map<Key, Value> map = new HashMap<>();

    public Map<Key, Value> build() {
        return this.map;
    }

    public MapBuilder<Key, Value> put(Key key, Value value) {
        this.map.put(key, value);
        return this;
    }
}