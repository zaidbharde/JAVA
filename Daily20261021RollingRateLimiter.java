import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;

/** Sliding-window limiter that keeps admission decisions deterministic. */
public final class Daily20261021RollingRateLimiter {
    private final int limit;
    private final long windowNanos;
    private final Deque<Long> admissions = new ArrayDeque<>();

    public Daily20261021RollingRateLimiter(int limit, Duration window) {
        if (limit <= 0 || window.isZero() || window.isNegative())
            throw new IllegalArgumentException("invalid limiter configuration");
        this.limit = limit;
        this.windowNanos = window.toNanos();
    }

    public synchronized boolean tryAcquire(long nowNanos) {
        evictExpired(nowNanos);
        if (admissions.size() >= limit) return false;
        admissions.addLast(nowNanos);
        return true;
    }

    public synchronized int remaining(long nowNanos) {
        evictExpired(nowNanos);
        return limit - admissions.size();
    }

    public synchronized long retryAfterNanos(long nowNanos) {
        evictExpired(nowNanos);
        return admissions.isEmpty() ? 0 : Math.max(0, admissions.peekFirst() + windowNanos - nowNanos);
    }

    private void evictExpired(long nowNanos) {
        while (!admissions.isEmpty() && nowNanos - admissions.peekFirst() >= windowNanos)
            admissions.removeFirst();
    }
}
