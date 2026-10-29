import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** Subtracts closed integer ranges from one closed range. */
public final class Daily20261028Extra023IntervalSubtract {
    public record Span(long start, long end) {
        public Span { if (end < start) throw new IllegalArgumentException("end before start"); }
    }

    /** Returns the portions of [start,end] not covered by any blocked range. */
    public static List<Span> subtract(long start, long end, List<Span> blocked) {
        if (end < start) throw new IllegalArgumentException("end before start");
        Objects.requireNonNull(blocked, "blocked");
        List<Span> sorted = new ArrayList<>(blocked);
        sorted.forEach(s -> Objects.requireNonNull(s, "blocked span"));
        sorted.sort(Comparator.comparingLong(Span::start));
        List<Span> result = new ArrayList<>();
        long cursor = start;
        for (Span cut : sorted) {
            if (cut.end < cursor) continue;
            if (cut.start > end) break;
            long clippedStart = Math.max(start, cut.start);
            long clippedEnd = Math.min(end, cut.end);
            if (clippedStart > cursor) result.add(new Span(cursor, clippedStart - 1));
            if (clippedEnd >= end) return List.copyOf(result);
            cursor = Math.max(cursor, clippedEnd + 1);
        }
        if (cursor <= end) result.add(new Span(cursor, end));
        return List.copyOf(result);
    }

    public static void main(String[] args) {
        List<Span> left = subtract(1, 12, List.of(new Span(3, 5), new Span(5, 8), new Span(20, 30)));
        if (!left.equals(List.of(new Span(1, 2), new Span(9, 12)))) throw new AssertionError(left);
        if (!subtract(Long.MIN_VALUE, Long.MAX_VALUE, List.of(new Span(Long.MIN_VALUE, Long.MAX_VALUE))).isEmpty())
            throw new AssertionError("full coverage should leave nothing");
        if (!subtract(4, 4, List.of()).equals(List.of(new Span(4, 4)))) throw new AssertionError("singleton");
        System.out.println("IntervalSubtract self-check passed");
    }
}
