import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/** Produces bounded exponential-backoff delays with deterministic jitter. */
public final class RetryScheduleToday {
    private RetryScheduleToday() {}

    public static List<Duration> build(int attempts, Duration initial, Duration maximum, double jitter) {
        if (attempts < 0 || initial.isNegative() || maximum.isNegative() || jitter < 0 || jitter > 1)
            throw new IllegalArgumentException("invalid retry policy");
        List<Duration> delays = new ArrayList<>();
        long cap = maximum.toMillis();
        long base = initial.toMillis();
        for (int attempt = 0; attempt < attempts; attempt++) {
            long exponential = Math.min(cap, base > cap / 2 ? cap : base << attempt);
            long spread = Math.round(exponential * jitter);
            long deterministicOffset = (attempt * 31L + 17L) % (2 * spread + 1);
            long delay = Math.max(0, exponential - spread + deterministicOffset);
            delays.add(Duration.ofMillis(Math.min(cap, delay)));
        }
        return delays;
    }
}
