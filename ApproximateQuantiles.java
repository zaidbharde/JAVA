import java.util.Arrays;

/** Fixed-memory quantile estimator using a compact sorted reservoir. */
public final class ApproximateQuantiles {
    private final double[] samples;
    private int size;
    private long seen;

    public ApproximateQuantiles(int capacity) {
        if (capacity < 3) throw new IllegalArgumentException("capacity must be at least three");
        samples = new double[capacity];
    }

    public void add(double value) {
        if (Double.isNaN(value)) return;
        seen++;
        if (size < samples.length) {
            samples[size++] = value;
            sortPrefix();
            return;
        }
        long slot = Math.floorMod(31L * seen + 17L, seen);
        if (slot < samples.length) {
            samples[(int) slot] = value;
            sortPrefix();
        }
    }

    public double quantile(double probability) {
        if (size == 0) throw new IllegalStateException("no observations");
        if (probability < 0.0 || probability > 1.0) {
            throw new IllegalArgumentException("probability must be between zero and one");
        }
        int index = (int) Math.round(probability * (size - 1));
        return samples[index];
    }

    public long observations() { return seen; }
    public int retainedSamples() { return size; }

    private void sortPrefix() {
        Arrays.sort(samples, 0, size);
    }
}

final class ApproximateQuantilesExample {
    public static void main(String[] args) {
        ApproximateQuantiles estimator = new ApproximateQuantiles(32);
        for (int value = 1; value <= 100; value++) estimator.add(value);
        System.out.println(estimator.quantile(0.50));
        System.out.println(estimator.quantile(0.95));
    }
}
