import java.util.*;

/** Generic approximate membership filter with saturating counters. */
public final class GenericCountingFilter<T> {
    private final int[] counters;
    private final int hashCount;

    public GenericCountingFilter(int buckets, int hashCount) {
        if (buckets < 2 || hashCount < 1) throw new IllegalArgumentException("invalid filter dimensions");
        counters = new int[buckets];
        this.hashCount = hashCount;
    }

    public void add(T value) {
        visit(value, index -> { if (counters[index] < Integer.MAX_VALUE) counters[index]++; });
    }

    public void remove(T value) {
        visit(value, index -> { if (counters[index] > 0) counters[index]--; });
    }

    public boolean mightContain(T value) {
        final boolean[] result = { true };
        visit(value, index -> { if (counters[index] == 0) result[0] = false; });
        return result[0];
    }

    private void visit(T value, java.util.function.IntConsumer action) {
        int mixed = Objects.hashCode(value) ^ (Objects.hashCode(value) >>> 16);
        for (int round = 0; round < hashCount; round++) {
            action.accept(Math.floorMod(mixed, counters.length));
            mixed = Integer.rotateLeft(mixed ^ (round + 1) * 0x9e3779b9, 11);
        }
    }
}
