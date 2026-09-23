import java.util.HashMap;
import java.util.Map;

/** A fixed-capacity least-recently-used cache with O(1) operations. */
public final class LruClockCache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> nodes = new HashMap<>();
    private final Node<K, V> head = new Node<>(null, null);
    private final Node<K, V> tail = new Node<>(null, null);

    public LruClockCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be positive");
        this.capacity = capacity;
        head.next = tail;
        tail.previous = head;
    }

    public V get(K key) {
        Node<K, V> node = nodes.get(key);
        if (node == null) return null;
        moveToFront(node);
        return node.value;
    }

    public void put(K key, V value) {
        Node<K, V> node = nodes.get(key);
        if (node != null) {
            node.value = value;
            moveToFront(node);
            return;
        }
        node = new Node<>(key, value);
        nodes.put(key, node);
        insertAfterHead(node);
        if (nodes.size() > capacity) {
            Node<K, V> evicted = tail.previous;
            unlink(evicted);
            nodes.remove(evicted.key);
        }
    }

    public int size() {
        return nodes.size();
    }

    private void moveToFront(Node<K, V> node) {
        unlink(node);
        insertAfterHead(node);
    }

    private void insertAfterHead(Node<K, V> node) {
        node.next = head.next;
        node.previous = head;
        head.next.previous = node;
        head.next = node;
    }

    private static void unlink(Node<?, ?> node) {
        node.previous.next = node.next;
        node.next.previous = node.previous;
    }

    private static final class Node<K, V> {
        private final K key;
        private V value;
        private Node<K, V> previous;
        private Node<K, V> next;
        private Node(K key, V value) { this.key = key; this.value = value; }
    }
}
