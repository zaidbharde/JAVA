/** Fixed-size moving average with integer quantization for telemetry values. */
public final class QuantizedMovingAverage {
    private final long[] samples;
    private int next;
    private int count;
    private long total;

    public QuantizedMovingAverage(int windowSize) {
        if (windowSize < 1) throw new IllegalArgumentException("window size must be positive");
        samples = new long[windowSize];
    }

    public void add(double value, double quantum) {
        if (!Double.isFinite(value) || !Double.isFinite(quantum) || quantum <= 0.0) {
            throw new IllegalArgumentException("value must be finite and quantum must be positive");
        }
        long quantized = Math.round(value / quantum);
        if (count == samples.length) total -= samples[next];
        else count++;
        samples[next] = quantized;
        total += quantized;
        next = (next + 1) % samples.length;
    }

    public double average(double quantum) {
        if (count == 0) throw new IllegalStateException("no samples recorded");
        if (!Double.isFinite(quantum) || quantum <= 0.0) throw new IllegalArgumentException("invalid quantum");
        return (total / (double) count) * quantum;
    }

    public int size() {
        return count;
    }
}
