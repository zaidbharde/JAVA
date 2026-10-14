import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Maintains a normalized union of closed integer intervals. */
public final class IntervalSet {
    private final List<long[]> ranges = new ArrayList<>();

    public void add(long start, long end) {
        if (start > end) throw new IllegalArgumentException("start > end");
        List<long[]> next = new ArrayList<>();
        long mergedStart = start;
        long mergedEnd = end;
        boolean inserted = false;
        for (long[] range : ranges) {
            if (range[1] + 1 < mergedStart) next.add(range);
            else if (mergedEnd + 1 < range[0]) {
                if (!inserted) { next.add(new long[]{mergedStart, mergedEnd}); inserted = true; }
                next.add(range);
            } else {
                mergedStart = Math.min(mergedStart, range[0]);
                mergedEnd = Math.max(mergedEnd, range[1]);
            }
        }
        if (!inserted) next.add(new long[]{mergedStart, mergedEnd});
        next.sort(Comparator.comparingLong(a -> a[0]));
        ranges.clear();
        ranges.addAll(next);
    }

    public boolean contains(long value) {
        for (long[] range : ranges) {
            if (value < range[0]) return false;
            if (value <= range[1]) return true;
        }
        return false;
    }

    public List<long[]> snapshot() {
        List<long[]> copy = new ArrayList<>();
        for (long[] range : ranges) copy.add(range.clone());
        return copy;
    }
}
