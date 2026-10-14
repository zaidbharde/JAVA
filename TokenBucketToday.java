import java.time.Duration;
import java.time.Instant;

/** Thread-safe token bucket for rate-limiting work by cost. */
public final class TokenBucketToday {
    private final double capacity;
    private final double refillPerSecond;
    private double tokens;
    private Instant updatedAt;

    public TokenBucketToday(double capacity, double refillPerSecond, Instant now) {
        if (capacity <= 0 || refillPerSecond <= 0) throw new IllegalArgumentException("invalid bucket");
        this.capacity = capacity;
        this.refillPerSecond = refillPerSecond;
        this.tokens = capacity;
        this.updatedAt = now;
    }

    public synchronized boolean tryConsume(double cost, Instant now) {
        if (cost <= 0 || cost > capacity || now.isBefore(updatedAt)) return false;
        refill(now);
        if (tokens < cost) return false;
        tokens -= cost;
        return true;
    }

    public synchronized Duration untilAvailable(double cost, Instant now) {
        if (cost <= 0 || cost > capacity || now.isBefore(updatedAt)) return Duration.ZERO;
        refill(now);
        if (tokens >= cost) return Duration.ZERO;
        return Duration.ofMillis((long) Math.ceil((cost - tokens) * 1000 / refillPerSecond));
    }

    private void refill(Instant now) {
        double seconds = Duration.between(updatedAt, now).toNanos() / 1_000_000_000.0;
        tokens = Math.min(capacity, tokens + seconds * refillPerSecond);
        updatedAt = now;
    }
}
