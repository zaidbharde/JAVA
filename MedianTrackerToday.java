import java.util.Collections;
import java.util.PriorityQueue;

/** Maintains an exact running median with two heaps. */
public final class MedianTrackerToday {
    private final PriorityQueue<Integer> lower = new PriorityQueue<>(Collections.reverseOrder());
    private final PriorityQueue<Integer> upper = new PriorityQueue<>();

    public void add(int value) {
        if (lower.isEmpty() || value <= lower.peek()) lower.add(value);
        else upper.add(value);
        rebalance();
    }

    public double median() {
        if (lower.isEmpty()) throw new IllegalStateException("no samples");
        if (lower.size() == upper.size()) return (lower.peek() + (double) upper.peek()) / 2;
        return lower.peek();
    }

    public int size() {
        return lower.size() + upper.size();
    }

    private void rebalance() {
        while (lower.size() > upper.size() + 1) upper.add(lower.remove());
        while (upper.size() > lower.size()) lower.add(upper.remove());
    }
}
