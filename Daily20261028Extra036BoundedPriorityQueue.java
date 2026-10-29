import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

/** Retains at most k best values according to a comparator. */
public final class Daily20261028Extra036BoundedPriorityQueue<T> {
    private final int limit;
    private final Comparator<? super T> bestFirst;
    private final PriorityQueue<T> worstFirst;

    /** A negative comparator result means the first value is better. */
    public Daily20261028Extra036BoundedPriorityQueue(int k, Comparator<? super T> bestFirst) {
        if (k < 0) throw new IllegalArgumentException("k must be nonnegative");
        this.limit = k;
        this.bestFirst = Objects.requireNonNull(bestFirst, "comparator");
        this.worstFirst = new PriorityQueue<>(bestFirst.reversed());
    }

    /** Adds a non-null value; returns whether it remains among the retained values. */
    public boolean offer(T value) {
        Objects.requireNonNull(value, "value");
        if (limit == 0) return false;
        if (worstFirst.size() < limit) worstFirst.add(value);
        else if (bestFirst.compare(value, worstFirst.peek()) < 0) {
            worstFirst.poll();
            worstFirst.add(value);
        } else return false;
        return true;
    }

    public int size() { return worstFirst.size(); }
    public boolean isEmpty() { return worstFirst.isEmpty(); }

    /** Returns an immutable snapshot ordered best to worst. */
    public List<T> bestFirst() {
        List<T> result = new ArrayList<>(worstFirst);
        result.sort(bestFirst);
        return List.copyOf(result);
    }

    public static void main(String[] args) {
        Daily20261028Extra036BoundedPriorityQueue<Integer> top =
            new Daily20261028Extra036BoundedPriorityQueue<>(3, Comparator.naturalOrder());
        for (int n : new int[] {7, 2, 9, 1, 5}) top.offer(n);
        if (!top.bestFirst().equals(List.of(1, 2, 5)) || top.size() != 3)
            throw new AssertionError("top-k retention failed: " + top.bestFirst());
        if (new Daily20261028Extra036BoundedPriorityQueue<Integer>(0, Comparator.naturalOrder()).offer(1))
            throw new AssertionError("zero capacity retained a value");
        System.out.println("BoundedPriorityQueue self-check passed: " + top.bestFirst());
    }
}
