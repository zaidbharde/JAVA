import java.util.concurrent.TimeUnit;

/** Thread-safe token bucket for smoothing bursts of work. */
public final class Daily20261025TokenBucket {
    private final double capacity;
    private final double refillPerSecond;
    private double tokens;
    private long lastRefillNanos;

    public Daily20261025TokenBucket(double capacity, double refillPerSecond) {
        if (capacity <= 0 || refillPerSecond <= 0) throw new IllegalArgumentException("rates must be positive");
        this.capacity = capacity;
        this.refillPerSecond = refillPerSecond;
        this.tokens = capacity;
        this.lastRefillNanos = System.nanoTime();
    }

    public synchronized boolean tryAcquire() {
        refill();
        if (tokens < 1.0) return false;
        tokens -= 1.0;
        return true;
    }

    private void refill() {
        long now = System.nanoTime();
        double elapsed = (now - lastRefillNanos) / (double) TimeUnit.SECONDS.toNanos(1);
        tokens = Math.min(capacity, tokens + elapsed * refillPerSecond);
        lastRefillNanos = now;
    }

    public synchronized double availableTokens() {
        refill();
        return tokens;
    }
}
