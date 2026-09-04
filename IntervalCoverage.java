import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Computes merged coverage and uncovered gaps over a bounded integer domain. */
public final class IntervalCoverage {
    public record Span(int start, int end) {
        public Span { if (end < start) throw new IllegalArgumentException("reversed span"); }
    }

    public static List<Span> merge(List<Span> spans) {
        List<Span> sorted = new ArrayList<>(spans);
        sorted.sort(Comparator.comparingInt(Span::start));
        List<Span> result = new ArrayList<>();
        for (Span current : sorted) {
            if (result.isEmpty() || current.start() > result.get(result.size() - 1).end() + 1) {
                result.add(current);
            } else {
                Span previous = result.remove(result.size() - 1);
                result.add(new Span(previous.start(), Math.max(previous.end(), current.end())));
            }
        }
        return List.copyOf(result);
    }

    public static List<Span> gaps(List<Span> spans, int domainStart, int domainEnd) {
        if (domainEnd < domainStart) throw new IllegalArgumentException("invalid domain");
        List<Span> gaps = new ArrayList<>();
        int cursor = domainStart;
        for (Span span : merge(spans)) {
            if (span.end() < domainStart || span.start() > domainEnd) continue;
            int start = Math.max(span.start(), domainStart);
            int end = Math.min(span.end(), domainEnd);
            if (cursor < start) gaps.add(new Span(cursor, start - 1));
            cursor = Math.max(cursor, end + 1);
        }
        if (cursor <= domainEnd) gaps.add(new Span(cursor, domainEnd));
        return List.copyOf(gaps);
    }
}
