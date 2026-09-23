import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Selects workers proportionally while preserving deterministic rotation. */
public final class WeightedRoundRobin<T> {
    private final List<Entry<T>> entries = new ArrayList<>();
    private int cursor;
    private int remaining;

    public void add(T value, int weight) {
        Objects.requireNonNull(value, "value");
        if (weight <= 0) {
            throw new IllegalArgumentException("weight must be positive");
        }
        entries.add(new Entry<>(value, weight));
        remaining = 0;
    }

    public T next() {
        if (entries.isEmpty()) {
            throw new IllegalStateException("no workers configured");
        }
        for (int attempts = 0; attempts < entries.size() * 2; attempts++) {
            Entry<T> entry = entries.get(cursor);
            cursor = (cursor + 1) % entries.size();
            if (remaining == 0) {
                remaining = entry.weight;
            }
            remaining--;
            if (remaining >= 0) {
                return entry.value;
            }
        }
        throw new IllegalStateException("scheduler could not select a worker");
    }

    public int size() {
        return entries.size();
    }

    private record Entry<T>(T value, int weight) {}
}
