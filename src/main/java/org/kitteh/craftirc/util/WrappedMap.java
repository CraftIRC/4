package org.kitteh.craftirc.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps an existing map.
 * <p/>
 * The wrapped map is untouched, while an outer map stores any values which
 * have been changed, as well as any additional values.
 */
public class WrappedMap<K, V> {
    private Map<K, V> innerMap;
    private Map<K, V> outerMap = new HashMap<K, V>();

    /**
     * Wraps a map.
     *
     * @param map map to be wrapped and untouched
     */
    public WrappedMap(Map<K, V> map) {
        this.innerMap = map;
    }

    /**
     * Gets the size of the map. Duplicated
     *
     * @return number of unique keys between both maps stored
     */
    public int size() {
        int size = this.innerMap.size();
        for (K key : this.outerMap.keySet()) {
            if (!this.innerMap.containsKey(key)) {
                size++;
            }
        }
        return size;
    }

    /**
     * Gets if this map contains the specified key.
     *
     * @param key key which may exist in the map
     * @return true if the key is stored at either level
     */
    public boolean containsKey(Object key) {
        return this.innerMap.containsKey(key) || this.outerMap.containsKey(key);
    }

    /**
     * Gets if this map contains the specified value. The value may be
     * located in the wrapped map but hidden by the outer map and in this
     * scenario will return false.
     *
     * @param value value which may exist in the map
     * @return true if the value is in the map and visible
     */
    public boolean containsValue(Object value) {
        if (this.outerMap.containsValue(value)) {
            return true;
        }
        for (Map.Entry<K, V> entry : this.innerMap.entrySet()) {
            if (!this.outerMap.containsKey(entry.getKey())) {
                V v = entry.getValue();
                if (value == null ? v == null : value.equals(v)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the value mapped by the specified key.
     *
     * @param key the key
     * @return the value the key is mapped to, or null if no mapping exists
     */
    public V get(Object key) {
        if (this.outerMap.containsKey(key)) {
            return this.outerMap.get(key);
        }
        return this.innerMap.get(key);
    }

    /**
     * Maps a key to a value in the map.
     * <p/>
     * If the key exists in the modifiable, outer map, this method will
     * return that value. If the key only exists in the inner, wrapped map
     * this method will return the inner value which is now hidden by the
     * presence of the new mapping.
     *
     * @param key the key to map
     * @param value the value mapped to the key
     * @return the value 'displaced' by the new mapping (See above) or null
     * if nothing was displaced.
     */
    public V put(K key, V value) {
        V displaced;
        if (!this.outerMap.containsKey(key)) {
            displaced = this.innerMap.get(key);
        } else {
            displaced = this.outerMap.get(key);
        }
        this.outerMap.put(key, value);
        return displaced;
    }

    /**
     * Removes a mapping from the modifiable map.
     *
     * @param key the key for which the mapping should be removed
     * @return the removed mapped value, or null if no mapping existed
     */
    public V remove(Object key) {
        return this.outerMap.remove(key);
    }

    /**
     * Deposits a pile of mappings to the modifiable map.
     *
     * @param m mappings to add to the modifiable map
     */
    public void putAll(Map<? extends K, ? extends V> m) {
        this.outerMap.putAll(m);
    }
}