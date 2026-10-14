import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Samples indexed items in proportion to their nonnegative weights. */
public final class WeightedSampler<T> {
    private final List<T> items = new ArrayList<>();
    private final List<Double> cumulative = new ArrayList<>();
    private final Random random;
    private double total;

    public WeightedSampler(Random random) { this.random = random; }

    public void add(T item, double weight) {
        if (weight <= 0 || !Double.isFinite(weight)) throw new IllegalArgumentException("invalid weight");
        total += weight;
        items.add(item);
        cumulative.add(total);
    }

    public T sample() {
        if (items.isEmpty()) throw new IllegalStateException("empty sampler");
        double target = random.nextDouble() * total;
        int low = 0, high = cumulative.size() - 1;
        while (low < high) {
            int middle = (low + high) >>> 1;
            if (target < cumulative.get(middle)) high = middle;
            else low = middle + 1;
        }
        return items.get(low);
    }
}
