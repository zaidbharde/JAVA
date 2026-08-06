import java.util.*;

public class LRUCache<K, V> {

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev, next;

        Node(K key, V value) {
            this.key   = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<K, Node<K, V>> map;
    private final Node<K, V> head;
    private final Node<K, V> tail;
    private int hits;
    private int misses;

    public LRUCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be > 0");
        this.capacity = capacity;
        this.map      = new HashMap<>();
        this.head     = new Node<>(null, null);
        this.tail     = new Node<>(null, null);
        head.next     = tail;
        tail.prev     = head;
    }

    public V get(K key) {
        Node<K, V> node = map.get(key);
        if (node == null) {
            misses++;
            return null;
        }
        hits++;
        moveToFront(node);
        return node.value;
    }

    public void put(K key, V value) {
        Node<K, V> node = map.get(key);

        if (node != null) {
            node.value = value;
            moveToFront(node);
            return;
        }

        if (map.size() >= capacity) {
            Node<K, V> lru = tail.prev;
            remove(lru);
            map.remove(lru.key);
        }

        Node<K, V> newNode = new Node<>(key, value);
        addToFront(newNode);
        map.put(key, newNode);
    }

    public boolean containsKey(K key) { return map.containsKey(key); }
    public int     size()             { return map.size(); }
    public int     getHits()          { return hits; }
    public int     getMisses()        { return misses; }

    public double hitRate() {
        int total = hits + misses;
        return total == 0 ? 0.0 : (double) hits / total * 100;
    }

    private void addToFront(Node<K, V> node) {
        node.next      = head.next;
        node.prev      = head;
        head.next.prev = node;
        head.next      = node;
    }

    private void remove(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToFront(Node<K, V> node) {
        remove(node);
        addToFront(node);
    }

    public List<K> getOrder() {
        List<K> order = new ArrayList<>();
        Node<K, V> curr = head.next;
        while (curr != tail) {
            order.add(curr.key);
            curr = curr.next;
        }
        return order;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("LRUCache{");
        Node<K, V> curr = head.next;
        boolean first = true;
        while (curr != tail) {
            if (!first) sb.append(", ");
            sb.append(curr.key).append("=").append(curr.value);
            first = false;
            curr  = curr.next;
        }
        return sb.append("}").toString();
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(44));
        System.out.println("  LRU Cache Demo");
        System.out.println("=".repeat(44));

        LRUCache<String, Integer> cache = new LRUCache<>(3);

        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        System.out.println("After a,b,c : " + cache);

        cache.get("a");
        System.out.println("After get(a): " + cache);

        cache.put("d", 4);
        System.out.println("After put(d): " + cache);
        System.out.println("get(b)      : " + cache.get("b"));

        cache.put("e", 5);
        cache.put("f", 6);
        System.out.println("After e,f   : " + cache);

        System.out.println("\nOrder (MRU→LRU): " + cache.getOrder());
        System.out.printf("Hits: %d | Misses: %d | Rate: %.1f%%%n",
            cache.getHits(), cache.getMisses(), cache.hitRate());

        System.out.println("\n" + "─".repeat(44));
        System.out.println("Stress test: 10000 ops, capacity=100");

        LRUCache<Integer, Integer> bigCache = new LRUCache<>(100);
        Random rng = new Random(42);

        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            int key = rng.nextInt(200);
            if (rng.nextBoolean()) bigCache.put(key, i);
            else                   bigCache.get(key);
        }
        long elapsed = System.nanoTime() - start;

        System.out.printf("  Time: %.2f ms%n", elapsed / 1e6);
        System.out.printf("  Hits: %d | Misses: %d | Rate: %.1f%%%n",
            bigCache.getHits(), bigCache.getMisses(), bigCache.hitRate());
    }
}
