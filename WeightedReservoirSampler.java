import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Samples a bounded set of items while respecting positive item weights. */
public final class WeightedReservoirSampler {
    private final Random random;
    private final List<Entry> reservoir = new ArrayList<>();
    private double totalWeight;
    private final int capacity;

    public WeightedReservoirSampler(int capacity, long seed) {
        if (capacity < 1) throw new IllegalArgumentException("capacity");
        this.capacity = capacity;
        random = new Random(seed);
    }

    public void offer(String item, double weight) {
        if (weight <= 0 || Double.isNaN(weight)) throw new IllegalArgumentException("weight");
        totalWeight += weight;
        if (reservoir.size() < capacity) {
            reservoir.add(new Entry(item, weight));
            return;
        }
        int replacement = random.nextInt(reservoir.size());
        if (random.nextDouble() < weight / totalWeight) reservoir.set(replacement, new Entry(item, weight));
    }

    public List<String> values() {
        List<String> values = new ArrayList<>(reservoir.size());
        for (Entry entry : reservoir) values.add(entry.item);
        return List.copyOf(values);
    }

    private record Entry(String item, double weight) {}
}
