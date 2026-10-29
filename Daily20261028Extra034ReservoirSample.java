import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/** Uniformly selects up to {@code size} items from an iterable in one pass. */
public final class Daily20261028Extra034ReservoirSample {
    private Daily20261028Extra034ReservoirSample() { }

    /** Uses Algorithm R; the iterable is consumed once and the result is immutable. */
    public static <T> List<T> sample(Iterable<? extends T> items, int size, Random random) {
        Objects.requireNonNull(items, "items");
        Objects.requireNonNull(random, "random");
        if (size < 0) throw new IllegalArgumentException("size must be nonnegative");
        List<T> reservoir = new ArrayList<>();
        long seen = 0;
        for (T item : items) {
            if (seen == Long.MAX_VALUE) throw new IllegalArgumentException("iterable is too large");
            seen++;
            if (reservoir.size() < size) reservoir.add(item);
            else if (size > 0) {
                long slot = random.nextLong(seen);
                if (slot < size) reservoir.set((int) slot, item);
            }
        }
        return List.copyOf(reservoir);
    }

    public static void main(String[] args) {
        List<Integer> chosen = sample(List.of(1, 2, 3, 4, 5), 3, new Random(42));
        assert chosen.size() == 3 && List.of(1, 2, 3, 4, 5).containsAll(chosen);
        assert sample(List.of(1, 2), 5, new Random(1)).equals(List.of(1, 2));
        assert sample(List.of(1, 2), 0, new Random(1)).isEmpty();
        assert sample(List.of(), 2, new Random(1)).isEmpty();
        try {
            sample(List.of(1), -1, new Random(1));
            throw new AssertionError("negative size accepted");
        } catch (IllegalArgumentException expected) { }
        System.out.println("ReservoirSample self-check passed: " + chosen);
    }
}
