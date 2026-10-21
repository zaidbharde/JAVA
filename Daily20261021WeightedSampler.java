import java.util.*;

/** Samples from non-negative weights using a caller-provided random source. */
public final class Daily20261021WeightedSampler<T> {
    private final List<T> values;
    private final long[] cumulative;
    private final long total;

    public Daily20261021WeightedSampler(Map<T, Integer> weights) {
        if (weights.isEmpty()) throw new IllegalArgumentException("weights cannot be empty");
        values = new ArrayList<>(weights.keySet());
        cumulative = new long[values.size()];
        long running = 0;
        for (int i = 0; i < values.size(); i++) {
            int weight = weights.get(values.get(i));
            if (weight < 0) throw new IllegalArgumentException("negative weight");
            running = Math.addExact(running, weight);
            cumulative[i] = running;
        }
        if (running == 0) throw new IllegalArgumentException("all weights are zero");
        total = running;
    }

    public T sample(RandomGenerator random) {
        long ticket = random.nextLong(total);
        int index = Arrays.binarySearch(cumulative, ticket + 1);
        if (index < 0) index = -index - 1;
        return values.get(index);
    }

    public long totalWeight() { return total; }
    public List<T> values() { return List.copyOf(values); }
}
