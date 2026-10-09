import java.util.*;

/** Immutable interval index with deterministic overlap results. */
public final class IntervalIndex<T> {
    private final List<Entry<T>> entries;

    public IntervalIndex(Collection<Entry<T>> source) {
        List<Entry<T>> copy = new ArrayList<>(source);
        copy.sort(Comparator.comparingInt(Entry::start).thenComparingInt(Entry::end));
        entries = List.copyOf(copy);
    }

    public List<T> at(int point) {
        List<T> matches = new ArrayList<>();
        for (Entry<T> entry : entries) {
            if (entry.start > point) break;
            if (point <= entry.end) matches.add(entry.value);
        }
        return List.copyOf(matches);
    }

    public List<T> overlapping(int start, int end) {
        if (start > end) throw new IllegalArgumentException("start must not exceed end");
        List<T> matches = new ArrayList<>();
        for (Entry<T> entry : entries) {
            if (entry.start > end) break;
            if (entry.end >= start) matches.add(entry.value);
        }
        return List.copyOf(matches);
    }

    public record Entry<T>(int start, int end, T value) {
        public Entry {
            if (start > end) throw new IllegalArgumentException("invalid interval");
        }
    }
}
