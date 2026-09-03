import java.util.Arrays;

/** Selects a maximum-value compatible subset of closed-open intervals. */
public final class WeightedIntervalSelector {
    public record Job(int start, int finish, int value) {}

    public static int maximumValue(Job[] jobs) {
        if (jobs.length == 0) return 0;
        Job[] ordered = jobs.clone();
        Arrays.sort(ordered, (a, b) -> Integer.compare(a.finish(), b.finish()));
        int[] best = new int[ordered.length + 1];
        for (int i = 1; i <= ordered.length; i++) {
            Job current = ordered[i - 1];
            int compatible = 0;
            for (int j = i - 2; j >= 0; j--) {
                if (ordered[j].finish() <= current.start()) {
                    compatible = j + 1;
                    break;
                }
            }
            best[i] = Math.max(best[i - 1], best[compatible] + current.value());
        }
        return best[ordered.length];
    }

    public static void main(String[] args) {
        Job[] jobs = {new Job(1, 3, 5), new Job(2, 5, 6), new Job(4, 7, 5),
                new Job(6, 9, 4), new Job(8, 10, 7)};
        System.out.println(maximumValue(jobs));
    }
}
