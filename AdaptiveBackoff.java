import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/** Computes bounded exponential retry delays with optional jitter. */
public final class AdaptiveBackoff {
    private final Duration initial;
    private final Duration maximum;
    private final double multiplier;
    private final double jitter;

    public AdaptiveBackoff(Duration initial, Duration maximum, double multiplier, double jitter) {
        if (initial.isNegative() || initial.isZero() || maximum.compareTo(initial) < 0) {
            throw new IllegalArgumentException("invalid delay bounds");
        }
        if (multiplier < 1.0 || jitter < 0.0 || jitter > 1.0) {
            throw new IllegalArgumentException("invalid multiplier or jitter");
        }
        this.initial = Objects.requireNonNull(initial);
        this.maximum = Objects.requireNonNull(maximum);
        this.multiplier = multiplier;
        this.jitter = jitter;
    }

    public Duration delayFor(int attempt) {
        if (attempt < 0) throw new IllegalArgumentException("attempt must be non-negative");
        double raw = initial.toNanos() * Math.pow(multiplier, Math.min(attempt, 60));
        long capped = Math.min(maximum.toNanos(), (long) Math.min(raw, Long.MAX_VALUE));
        double factor = 1.0 - jitter + ThreadLocalRandom.current().nextDouble() * 2.0 * jitter;
        return Duration.ofNanos(Math.max(1L, Math.min(maximum.toNanos(), (long) (capped * factor))));
    }

    public static AdaptiveBackoff standard() {
        return new AdaptiveBackoff(Duration.ofMillis(50), Duration.ofSeconds(10), 2.0, 0.20);
    }
}

final class AdaptiveBackoffExample {
    public static void main(String[] args) {
        AdaptiveBackoff policy = AdaptiveBackoff.standard();
        for (int attempt = 0; attempt < 5; attempt++) {
            System.out.println(attempt + ": " + policy.delayFor(attempt).toMillis() + "ms");
        }
    }
}
