import java.util.HashMap;
import java.util.Map;

/** A fixed-capacity least-recently-used cache with O(1) access and eviction. */
public final class LruCache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> entries = new HashMap<>();
    private final Node<K, V> head = new Node<>(null, null);
    private final Node<K, V> tail = new Node<>(null, null);

    private static final class Node<K, V> {
        K key; V value; Node<K, V> previous; Node<K, V> next;
        Node(K key, V value) { this.key = key; this.value = value; }
    }

    public LruCache(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("capacity must be positive");
        this.capacity = capacity;
        head.next = tail;
        tail.previous = head;
    }

    public V get(K key) {
        Node<K, V> node = entries.get(key);
        if (node == null) return null;
        moveToFront(node);
        return node.value;
    }

    public void put(K key, V value) {
        Node<K, V> node = entries.get(key);
        if (node != null) { node.value = value; moveToFront(node); return; }
        node = new Node<>(key, value);
        entries.put(key, node); linkAfter(head, node);
        if (entries.size() > capacity) {
            Node<K, V> expired = tail.previous;
            unlink(expired); entries.remove(expired.key);
        }
    }

    public int size() { return entries.size(); }
    private void moveToFront(Node<K, V> node) { unlink(node); linkAfter(head, node); }
    private void linkAfter(Node<K, V> left, Node<K, V> node) {
        node.next = left.next; node.previous = left;
        left.next.previous = node; left.next = node;
    }
    private void unlink(Node<K, V> node) {
        node.previous.next = node.next; node.next.previous = node.previous;
    }
}
