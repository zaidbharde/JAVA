import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Objects;

/** Computes the minimum of each fixed-width contiguous window in O(n) time. */
public final class Daily20261028Extra032SlidingRangeMinimum {
    private Daily20261028Extra032SlidingRangeMinimum() {}

    /** Returns one minimum per window; rejects null input and widths outside [1, values.length]. */
    public static int[] compute(int[] values, int width) {
        Objects.requireNonNull(values, "values");
        if (width <= 0 || width > values.length) throw new IllegalArgumentException("bad width");
        int[] result = new int[values.length - width + 1];
        Deque<Integer> candidates = new ArrayDeque<>();
        for (int i = 0; i < values.length; i++) {
            while (!candidates.isEmpty() && candidates.peekFirst() <= i - width) candidates.removeFirst();
            while (!candidates.isEmpty() && values[candidates.peekLast()] >= values[i]) candidates.removeLast();
            candidates.addLast(i);
            if (i >= width - 1) result[i - width + 1] = values[candidates.peekFirst()];
        }
        return result;
    }

    public static void main(String[] args) {
        int[] actual = compute(new int[]{4, 2, 12, 3, 8, 7}, 3);
        if (!Arrays.equals(actual, new int[]{2, 2, 3, 3})) throw new AssertionError(Arrays.toString(actual));
        if (!Arrays.equals(compute(new int[]{5, 5, -1}, 1), new int[]{5, 5, -1})) throw new AssertionError("width one");
        for (int width : new int[]{0, 4}) {
            try { compute(new int[]{1, 2, 3}, width); throw new AssertionError("accepted invalid width"); }
            catch (IllegalArgumentException expected) { /* expected */ }
        }
        System.out.println("SlidingRangeMinimum self-check passed");
    }
}
