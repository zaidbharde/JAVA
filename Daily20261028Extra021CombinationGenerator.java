import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/** Lazily enumerates k-element selections, preserving input order and duplicate values. */
public final class Daily20261028Extra021CombinationGenerator {
    private Daily20261028Extra021CombinationGenerator() {}

    public static <T> Iterable<List<T>> combinations(List<? extends T> items, int k) {
        Objects.requireNonNull(items, "items");
        if (k < 0) throw new IllegalArgumentException("k must be non-negative");
        List<T> snapshot = new ArrayList<>(items);
        return () -> new Iterator<List<T>>() {
            private final int[] indexes = initialIndexes();
            private boolean available = k <= snapshot.size();

            private int[] initialIndexes() {
                int[] result = new int[k];
                for (int i = 0; i < k; i++) result[i] = i;
                return result;
            }

            @Override public boolean hasNext() { return available; }

            @Override public List<T> next() {
                if (!available) throw new NoSuchElementException();
                List<T> combination = new ArrayList<>(k);
                for (int index : indexes) combination.add(snapshot.get(index));
                advance();
                return Collections.unmodifiableList(combination);
            }

            private void advance() {
                if (k == 0) { available = false; return; }
                int i = k - 1;
                while (i >= 0 && indexes[i] == snapshot.size() - k + i) i--;
                if (i < 0) { available = false; return; }
                indexes[i]++;
                for (int j = i + 1; j < k; j++) indexes[j] = indexes[j - 1] + 1;
            }
        };
    }

    public static void main(String[] args) {
        List<List<String>> actual = new ArrayList<>();
        combinations(List.of("A", "B", "C"), 2).forEach(actual::add);
        if (!actual.equals(List.of(List.of("A", "B"), List.of("A", "C"), List.of("B", "C"))))
            throw new AssertionError("unexpected combinations: " + actual);
        if (!combinations(List.of(1, 2), 0).iterator().next().isEmpty())
            throw new AssertionError("k=0 must produce one empty combination");
        if (combinations(List.of(1), 2).iterator().hasNext())
            throw new AssertionError("k greater than input size must be empty");
        System.out.println("CombinationGenerator self-check passed: " + actual);
    }
}
