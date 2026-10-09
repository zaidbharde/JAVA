import java.util.Arrays;

/** Maintains a sorted snapshot for small sliding-window median queries. */
public final class WindowedMedian {
    private final int[] values;

    public WindowedMedian(int[] input) {
        if (input == null || input.length == 0) {
            throw new IllegalArgumentException("input must not be empty");
        }
        values = Arrays.copyOf(input, input.length);
        Arrays.sort(values);
    }

    public double median() {
        int middle = values.length / 2;
        if ((values.length & 1) == 1) {
            return values[middle];
        }
        return (values[middle - 1] + values[middle]) / 2.0;
    }

    public int percentile(double fraction) {
        if (fraction < 0.0 || fraction > 1.0) {
            throw new IllegalArgumentException("fraction must be between zero and one");
        }
        int index = (int) Math.round(fraction * (values.length - 1));
        return values[index];
    }

    public int size() {
        return values.length;
    }
}
