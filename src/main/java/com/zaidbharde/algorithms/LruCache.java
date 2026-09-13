package com.zaidbharde.algorithms;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Small access-ordered cache with deterministic eviction. */
public final class LruCache<K, V> {
    private final int capacity;
    private final Map<K, V> entries;

    public LruCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.capacity = capacity;
        this.entries = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > LruCache.this.capacity;
            }
        };
    }

    public synchronized void put(K key, V value) {
        entries.put(key, value);
    }

    public synchronized Optional<V> get(K key) {
        return Optional.ofNullable(entries.get(key));
    }

    public synchronized boolean containsKey(K key) {
        return entries.containsKey(key);
    }

    public synchronized int size() {
        return entries.size();
    }
}
