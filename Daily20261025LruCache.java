import java.util.LinkedHashMap;
import java.util.Map;

/** A small access-ordered cache with predictable memory bounds. */
public final class Daily20261025LruCache<K, V> {
    private final int capacity;
    private final LinkedHashMap<K, V> entries;

    public Daily20261025LruCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be positive");
        this.capacity = capacity;
        this.entries = new LinkedHashMap<>(capacity, 0.75f, true);
    }

    public V get(K key) { return entries.get(key); }

    public void put(K key, V value) {
        entries.put(key, value);
        if (entries.size() > capacity) {
            Map.Entry<K, V> eldest = entries.entrySet().iterator().next();
            entries.remove(eldest.getKey());
        }
    }

    public int size() { return entries.size(); }

    public boolean containsKey(K key) { return entries.containsKey(key); }

    public static void main(String[] args) {
        Daily20261025LruCache<String, Integer> cache = new Daily20261025LruCache<>(2);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.get("a");
        cache.put("c", 3);
        System.out.println(cache.containsKey("a") && !cache.containsKey("b"));
    }
}
