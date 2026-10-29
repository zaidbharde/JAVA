import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Compacts a sorted integer sequence into inclusive consecutive ranges. */
public final class Daily20261028Extra025ConsecutiveRangeCompactor {
    public record Range(int start, int end) {
        public Range {
            if (end < start) throw new IllegalArgumentException("end before start");
        }
    }

    /** Accepts nondecreasing input; repeated values are ignored. */
    public static List<Range> compact(int[] sortedValues) {
        Objects.requireNonNull(sortedValues, "sortedValues");
        if (sortedValues.length == 0) return List.of();
        List<Range> ranges = new ArrayList<>();
        int start = sortedValues[0], end = start;
        for (int i = 1; i < sortedValues.length; i++) {
            int value = sortedValues[i];
            if (value < end) throw new IllegalArgumentException("input must be sorted");
            if ((long) value <= (long) end + 1) {
                end = value;
            } else {
                ranges.add(new Range(start, end));
                start = end = value;
            }
        }
        ranges.add(new Range(start, end));
        return List.copyOf(ranges);
    }

    public static void main(String[] args) {
        if (!compact(new int[]{-3, -2, -2, 0, 1, 2, 7})
                .equals(List.of(new Range(-3, -2), new Range(0, 2), new Range(7, 7))))
            throw new AssertionError("ordinary compaction");
        if (!compact(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE})
                .equals(List.of(new Range(Integer.MIN_VALUE, Integer.MIN_VALUE),
                                 new Range(Integer.MAX_VALUE, Integer.MAX_VALUE))))
            throw new AssertionError("integer boundary handling");
        if (!compact(new int[0]).isEmpty()) throw new AssertionError("empty input");
        try {
            compact(new int[]{2, 1});
            throw new AssertionError("unsorted input accepted");
        } catch (IllegalArgumentException expected) { /* expected */ }
        System.out.println("ConsecutiveRangeCompactor self-check passed");
    }
}
