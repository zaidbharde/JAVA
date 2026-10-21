import java.util.*;

/** Selects a maximum-value compatible subset of weighted intervals. */
public final class Daily20261021WeightedIntervalSelector {
    public record Interval(int start, int end, int value) {
        public Interval {
            if (end < start) throw new IllegalArgumentException("end before start");
        }
    }

    public static List<Interval> select(List<Interval> input) {
        List<Interval> items = new ArrayList<>(input);
        items.sort(Comparator.comparingInt(Interval::end));
        int[] best = new int[items.size() + 1];
        int[] previous = new int[items.size()];
        for (int i = 0; i < items.size(); i++) {
            previous[i] = compatibleIndex(items, i);
            best[i + 1] = Math.max(best[i], best[previous[i] + 1] + items.get(i).value());
        }
        List<Interval> chosen = new ArrayList<>();
        for (int i = items.size(); i > 0;) {
            Interval current = items.get(i - 1);
            if (best[i] == best[i - 1]) i--;
            else { chosen.add(current); i = previous[i - 1] + 1; }
        }
        Collections.reverse(chosen);
        return chosen;
    }

    private static int compatibleIndex(List<Interval> items, int index) {
        int low = 0, high = index - 1, answer = -1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            if (items.get(mid).end() <= items.get(index).start()) { answer = mid; low = mid + 1; }
            else high = mid - 1;
        }
        return answer;
    }
}
