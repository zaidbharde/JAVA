import java.util.*;

/** Selects configured keys by smooth weighted cycling. */
public final class WeightedCycleSelector<T> {
    private final List<T> keys;
    private final int[] weights;
    private final int[] current;

    public WeightedCycleSelector(LinkedHashMap<T, Integer> configuration) {
        if (configuration.isEmpty()) throw new IllegalArgumentException("configuration is empty");
        keys = List.copyOf(configuration.keySet());
        weights = configuration.values().stream().mapToInt(Integer::intValue).toArray();
        current = new int[weights.length];
        for (int weight : weights) if (weight <= 0) throw new IllegalArgumentException("weights must be positive");
    }

    public T next() {
        int total = 0;
        int best = 0;
        for (int i = 0; i < weights.length; i++) {
            current[i] += weights[i];
            total += weights[i];
            if (current[i] > current[best]) best = i;
        }
        current[best] -= total;
        return keys.get(best);
    }

    public List<T> preview(int count) {
        if (count < 0) throw new IllegalArgumentException("count must not be negative");
        List<T> result = new ArrayList<>();
        for (int i = 0; i < count; i++) result.add(next());
        return List.copyOf(result);
    }
}
