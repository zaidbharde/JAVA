import java.util.Comparator;
import java.util.PriorityQueue;

/** Schedules values by deadline and preserves insertion order for ties. */
public final class DeadlineQueue<T> {
    private record Item<T>(long deadline, long sequence, T value) {}

    private final PriorityQueue<Item<T>> queue = new PriorityQueue<>(
        Comparator.comparingLong(Item<T>::deadline).thenComparingLong(Item<T>::sequence));
    private long nextSequence;

    public void offer(T value, long deadline) {
        queue.offer(new Item<>(deadline, nextSequence++, value));
    }

    public T pollReady(long now) {
        Item<T> item = queue.peek();
        if (item == null || item.deadline() > now) return null;
        return queue.poll().value();
    }

    public long nextDeadline() {
        Item<T> item = queue.peek();
        return item == null ? Long.MAX_VALUE : item.deadline();
    }

    public int size() {
        return queue.size();
    }

    public void clearExpired(long now) {
        while (!queue.isEmpty() && queue.peek().deadline() <= now) {
            queue.poll();
        }
    }
}
