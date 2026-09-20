import java.util.Comparator;
import java.util.PriorityQueue;

/** Stable priority queue that preserves insertion order among equal priorities. */
public final class PriorityTaskQueue<T> {
    private record Entry<T>(int priority, long sequence, T value) {}

    private final PriorityQueue<Entry<T>> queue;
    private long sequence;

    public PriorityTaskQueue() {
        queue = new PriorityQueue<>(Comparator
            .comparingInt((Entry<T> entry) -> entry.priority())
            .thenComparingLong(Entry::sequence));
    }

    public void offer(T value, int priority) {
        queue.add(new Entry<>(priority, sequence++, value));
    }

    public T poll() {
        Entry<T> entry = queue.poll();
        return entry == null ? null : entry.value();
    }

    public T peek() {
        Entry<T> entry = queue.peek();
        return entry == null ? null : entry.value();
    }

    public int size() { return queue.size(); }
    public boolean isEmpty() { return queue.isEmpty(); }
    public void clear() { queue.clear(); }
}

final class PriorityTaskQueueExample {
    public static void main(String[] args) {
        PriorityTaskQueue<String> tasks = new PriorityTaskQueue<>();
        tasks.offer("backup", 3);
        tasks.offer("alert", 1);
        tasks.offer("metrics", 3);
        while (!tasks.isEmpty()) System.out.println(tasks.poll());
    }
}
