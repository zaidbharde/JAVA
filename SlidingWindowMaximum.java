import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/** Computes maxima for every fixed-width window in linear time. */
public final class SlidingWindowMaximum {
    private SlidingWindowMaximum() {}

    public static int[] compute(int[] values, int width) {
        if (width <= 0 || width > values.length) throw new IllegalArgumentException("bad width");
        int[] result = new int[values.length - width + 1];
        Deque<Integer> candidates = new ArrayDeque<>();
        for (int i = 0; i < values.length; i++) {
            while (!candidates.isEmpty() && candidates.peekFirst() <= i - width) candidates.removeFirst();
            while (!candidates.isEmpty() && values[candidates.peekLast()] <= values[i]) candidates.removeLast();
            candidates.addLast(i);
            if (i >= width - 1) result[i - width + 1] = values[candidates.peekFirst()];
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(compute(new int[]{4, 2, 12, 3, 8, 7}, 3)));
    }
}
