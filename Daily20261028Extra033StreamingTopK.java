import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/** Maintains the largest k distinct integers seen in a stream using O(k) state. */
public final class Daily20261028Extra033StreamingTopK {
    private final int limit;
    private final PriorityQueue<Integer> smallestFirst = new PriorityQueue<>();
    private final Set<Integer> retained = new HashSet<>();

    public Daily20261028Extra033StreamingTopK(int k) {
        if (k <= 0) throw new IllegalArgumentException("k must be positive");
        limit = k;
    }

    /** Adds a value; duplicates and values below the retained top-k are ignored. */
    public void add(int value) {
        if (retained.contains(value)) return;
        if (smallestFirst.size() < limit) {
            smallestFirst.add(value);
            retained.add(value);
        } else if (value > smallestFirst.element()) {
            retained.remove(smallestFirst.remove());
            smallestFirst.add(value);
            retained.add(value);
        }
    }

    /** Returns the retained values in descending order as an immutable snapshot. */
    public List<Integer> largest() {
        List<Integer> result = new ArrayList<>(smallestFirst);
        result.sort(Collections.reverseOrder());
        return List.copyOf(result);
    }

    public int size() { return smallestFirst.size(); }

    public static void main(String[] args) {
        Daily20261028Extra033StreamingTopK top = new Daily20261028Extra033StreamingTopK(3);
        for (int value : new int[] {4, 9, 4, -2, 7, 11, 9}) top.add(value);
        if (!top.largest().equals(List.of(11, 9, 7)) || top.size() != 3)
            throw new AssertionError("unexpected top-k: " + top.largest());
        try {
            new Daily20261028Extra033StreamingTopK(0);
            throw new AssertionError("non-positive k accepted");
        } catch (IllegalArgumentException expected) { }
        System.out.println("StreamingTopK self-check passed: " + top.largest());
    }
}
