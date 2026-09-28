import java.util.Collections;
import java.util.PriorityQueue;

/** Maintains medians for every fixed-size sliding window. */
public final class SlidingMedianWindow {
    private SlidingMedianWindow() {}

    public static double[] medians(int[] values, int width) {
        if (width <= 0 || width > values.length) throw new IllegalArgumentException("width");
        PriorityQueue<Integer> low = new PriorityQueue<>(Collections.reverseOrder());
        PriorityQueue<Integer> high = new PriorityQueue<>();
        double[] result = new double[values.length - width + 1];
        for (int i = 0; i < values.length; i++) {
            add(values[i], low, high);
            if (i >= width) remove(values[i - width], low, high);
            rebalance(low, high);
            if (i >= width - 1) result[i - width + 1] = median(low, high);
        }
        return result;
    }

    private static void add(int value, PriorityQueue<Integer> low, PriorityQueue<Integer> high) {
        if (low.isEmpty() || value <= low.peek()) low.offer(value); else high.offer(value);
    }

    private static void remove(int value, PriorityQueue<Integer> low, PriorityQueue<Integer> high) {
        if (!low.remove(value)) high.remove(value);
    }

    private static void rebalance(PriorityQueue<Integer> low, PriorityQueue<Integer> high) {
        while (low.size() > high.size() + 1) high.offer(low.poll());
        while (high.size() > low.size()) low.offer(high.poll());
    }

    private static double median(PriorityQueue<Integer> low, PriorityQueue<Integer> high) {
        return low.size() == high.size() ? (low.peek() + high.peek()) / 2.0 : low.peek();
    }
}
