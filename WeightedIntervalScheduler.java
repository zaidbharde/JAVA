import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/** Selects a maximum-value set of non-overlapping half-open intervals. */
public final class WeightedIntervalScheduler {
    public record Job(int start, int end, int value) {
        public Job {
            if (end < start) {
                throw new IllegalArgumentException("end must not precede start");
            }
        }
    }

    private WeightedIntervalScheduler() {
    }

    public static List<Job> select(List<Job> input) {
        if (input == null || input.isEmpty()) {
            return List.of();
        }
        Job[] jobs = input.toArray(Job[]::new);
        Arrays.sort(jobs, Comparator.comparingInt(Job::end));
        int[] best = new int[jobs.length + 1];
        int[] previous = new int[jobs.length];
        for (int i = 0; i < jobs.length; i++) {
            previous[i] = compatibleIndex(jobs, i);
            best[i + 1] = Math.max(best[i], jobs[i].value() + best[previous[i] + 1]);
        }
        List<Job> selected = new ArrayList<>();
        for (int i = jobs.length; i > 0;) {
            Job current = jobs[i - 1];
            int withCurrent = current.value() + best[previous[i - 1] + 1];
            if (withCurrent >= best[i - 1]) {
                selected.add(current);
                i = previous[i - 1] + 1;
            } else {
                i--;
            }
        }
        selected.sort(Comparator.comparingInt(Job::start));
        return selected;
    }

    private static int compatibleIndex(Job[] jobs, int index) {
        int low = 0;
        int high = index - 1;
        int answer = -1;
        while (low <= high) {
            int middle = (low + high) >>> 1;
            if (jobs[middle].end() <= jobs[index].start()) {
                answer = middle;
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }
        return answer;
    }
}
