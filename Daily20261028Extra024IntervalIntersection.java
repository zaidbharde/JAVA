import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** Computes pairwise intersections of two sorted-by-result closed integer interval lists. */
public final class Daily20261028Extra024IntervalIntersection {
    public record Span(long start, long end) {
        public Span { if (end < start) throw new IllegalArgumentException("end before start"); }
    }

    /** Returns all overlaps in ascending order; each input list may be unsorted or overlapping. */
    public static List<Span> intersect(List<Span> first, List<Span> second) {
        Objects.requireNonNull(first, "first");
        Objects.requireNonNull(second, "second");
        List<Span> a = new ArrayList<>(first), b = new ArrayList<>(second);
        a.forEach(s -> Objects.requireNonNull(s, "first span"));
        b.forEach(s -> Objects.requireNonNull(s, "second span"));
        Comparator<Span> byStart = Comparator.comparingLong(Span::start).thenComparingLong(Span::end);
        a.sort(byStart);
        b.sort(byStart);
        List<Span> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < a.size() && j < b.size()) {
            Span x = a.get(i), y = b.get(j);
            long start = Math.max(x.start, y.start), end = Math.min(x.end, y.end);
            if (start <= end) result.add(new Span(start, end));
            if (x.end <= y.end) i++;
            if (y.end <= x.end) j++;
        }
        return List.copyOf(result);
    }

    public static void main(String[] args) {
        List<Span> actual = intersect(List.of(new Span(8, 12), new Span(1, 5)),
                List.of(new Span(4, 9), new Span(2, 3)));
        List<Span> expected = List.of(new Span(2, 3), new Span(4, 5), new Span(8, 9));
        if (!actual.equals(expected)) throw new AssertionError(actual);
        if (!intersect(List.of(new Span(Long.MIN_VALUE, Long.MAX_VALUE)), List.of(new Span(7, 7)))
                .equals(List.of(new Span(7, 7)))) throw new AssertionError("extreme/singleton");
        if (!intersect(List.of(), List.of(new Span(0, 1))).isEmpty()) throw new AssertionError("empty");
        System.out.println("IntervalIntersection self-check passed");
    }
}
