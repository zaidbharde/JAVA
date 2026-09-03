import java.util.Random;

/** Produces retry delays while respecting attempt and backoff limits. */
public final class RetryPolicy {
    private final int maxAttempts;
    private final long baseDelayMillis;
    private final long maxDelayMillis;
    private final Random random;

    public RetryPolicy(int maxAttempts, long baseDelayMillis, long maxDelayMillis, long seed) {
        if (maxAttempts < 1 || baseDelayMillis < 0 || maxDelayMillis < baseDelayMillis)
            throw new IllegalArgumentException("invalid retry bounds");
        this.maxAttempts = maxAttempts;
        this.baseDelayMillis = baseDelayMillis;
        this.maxDelayMillis = maxDelayMillis;
        this.random = new Random(seed);
    }

    public boolean shouldRetry(int attempt) {
        return attempt >= 1 && attempt <= maxAttempts;
    }

    public long delayMillis(int attempt) {
        if (!shouldRetry(attempt)) throw new IllegalArgumentException("attempt outside policy");
        long exponential = baseDelayMillis * (1L << Math.min(attempt - 1, 30));
        long capped = Math.min(exponential, maxDelayMillis);
        long jitter = capped == 0 ? 0 : random.nextLong(capped + 1);
        return Math.min(maxDelayMillis, capped / 2 + jitter / 2);
    }

    public static void main(String[] args) {
        RetryPolicy policy = new RetryPolicy(5, 100, 2_000, 7);
        for (int attempt = 1; policy.shouldRetry(attempt); attempt++)
            System.out.printf("attempt %d -> %d ms%n", attempt, policy.delayMillis(attempt));
    }
}
