import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/** Small thread-safe LRU cache whose entries expire after a caller-supplied duration. */
public final class LruTtlCache<K, V> {
    private record Entry<V>(V value, long expiresAtNanos) {}
    private final int capacity;
    private final Map<K, Entry<V>> entries;

    public LruTtlCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be positive");
        this.capacity = capacity;
        this.entries = new LinkedHashMap<>(capacity, 0.75f, true);
    }

    public synchronized void put(K key, V value, Duration ttl) {
        if (ttl.isNegative() || ttl.isZero()) throw new IllegalArgumentException("ttl must be positive");
        entries.put(key, new Entry<>(value, System.nanoTime() + ttl.toNanos()));
        trim();
    }

    public synchronized V get(K key) {
        Entry<V> entry = entries.get(key);
        if (entry == null) return null;
        if (entry.expiresAtNanos() <= System.nanoTime()) {
            entries.remove(key);
            return null;
        }
        return entry.value();
    }

    public synchronized int size() {
        entries.entrySet().removeIf(e -> e.getValue().expiresAtNanos() <= System.nanoTime());
        return entries.size();
    }

    private void trim() {
        while (entries.size() > capacity) entries.remove(entries.keySet().iterator().next());
    }

    public static void main(String[] args) throws InterruptedException {
        LruTtlCache<String, Integer> cache = new LruTtlCache<>(2);
        cache.put("attempts", 3, Duration.ofSeconds(1));
        System.out.println("attempts=" + cache.get("attempts") + " size=" + cache.size());
    }
}
