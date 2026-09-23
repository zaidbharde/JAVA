import java.time.Duration;
import java.time.Instant;

/** A thread-safe token bucket for small in-process rate limits. */
public final class TokenBucketLimiter {
    private final double capacity;
    private final double refillPerSecond;
    private double tokens;
    private Instant lastRefill;

    public TokenBucketLimiter(double capacity, double refillPerSecond) {
        if (capacity <= 0 || refillPerSecond <= 0) {
            throw new IllegalArgumentException("bucket values must be positive");
        }
        this.capacity = capacity;
        this.refillPerSecond = refillPerSecond;
        this.tokens = capacity;
        this.lastRefill = Instant.now();
    }

    public synchronized boolean tryAcquire() {
        return tryAcquire(1);
    }

    public synchronized boolean tryAcquire(int requested) {
        if (requested <= 0 || requested > capacity) {
            throw new IllegalArgumentException("requested tokens are outside bucket capacity");
        }
        refill();
        if (tokens < requested) {
            return false;
        }
        tokens -= requested;
        return true;
    }

    public synchronized double availableTokens() {
        refill();
        return tokens;
    }

    private void refill() {
        Instant now = Instant.now();
        long nanos = Duration.between(lastRefill, now).toNanos();
        if (nanos <= 0) {
            return;
        }
        tokens = Math.min(capacity, tokens + nanos / 1_000_000_000.0 * refillPerSecond);
        lastRefill = now;
    }
}
