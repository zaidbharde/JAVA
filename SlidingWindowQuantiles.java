import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/** Maintains a fixed-size window and answers percentile queries by sorting a snapshot. */
public final class SlidingWindowQuantiles {
    private final int capacity;
    private final Deque<Double> values = new ArrayDeque<>();

    public SlidingWindowQuantiles(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be positive");
        this.capacity = capacity;
    }

    public void add(double value) {
        if (!Double.isFinite(value)) throw new IllegalArgumentException("value must be finite");
        values.addLast(value);
        if (values.size() > capacity) values.removeFirst();
    }

    public int size() { return values.size(); }

    public double percentile(double fraction) {
        if (values.isEmpty()) throw new IllegalStateException("window is empty");
        if (fraction < 0.0 || fraction > 1.0) throw new IllegalArgumentException("fraction out of range");
        double[] sorted = values.stream().mapToDouble(Double::doubleValue).toArray();
        Arrays.sort(sorted);
        double position = fraction * (sorted.length - 1);
        int lower = (int) Math.floor(position);
        int upper = (int) Math.ceil(position);
        double weight = position - lower;
        return sorted[lower] * (1 - weight) + sorted[upper] * weight;
    }

    public static void main(String[] args) {
        SlidingWindowQuantiles window = new SlidingWindowQuantiles(5);
        for (double value : new double[] {8, 2, 7, 4, 9, 3}) window.add(value);
        System.out.printf("size=%d median=%.1f p90=%.1f%n", window.size(),
                window.percentile(0.50), window.percentile(0.90));
    }
}

// Example output: size=5 median=4.0 p90=8.2
