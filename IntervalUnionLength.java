import java.util.Arrays;

/** Computes the total covered length of closed-open integer intervals. */
public final class IntervalUnionLength {
    private IntervalUnionLength() {}

    public static long total(int[][] intervals) {
        if (intervals == null || intervals.length == 0) return 0;
        int[][] ordered = Arrays.stream(intervals)
                .map(pair -> pair.clone())
                .sorted((a, b) -> Integer.compare(a[0], b[0]))
                .toArray(int[][]::new);
        long total = 0;
        int start = ordered[0][0];
        int end = ordered[0][1];
        for (int i = 1; i < ordered.length; i++) {
            int nextStart = ordered[i][0];
            int nextEnd = ordered[i][1];
            if (nextStart > end) {
                total += (long) end - start;
                start = nextStart;
                end = nextEnd;
            } else {
                end = Math.max(end, nextEnd);
            }
        }
        return total + (long) end - start;
    }

    public static void main(String[] args) {
        System.out.println(total(new int[][] {{1, 4}, {3, 8}, {10, 12}}));
    }
}
