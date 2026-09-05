import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;

/** A small fixed-window limiter useful for protecting a burst-sensitive operation. */
public final class WindowedRateLimiter {
    private final int limit;
    private final long windowNanos;
    private final Deque<Long> permits = new ArrayDeque<>();

    public WindowedRateLimiter(int limit, Duration window) {
        if (limit <= 0 || window.isZero() || window.isNegative()) {
            throw new IllegalArgumentException("limit and window must be positive");
        }
        this.limit = limit;
        this.windowNanos = window.toNanos();
    }

    public synchronized boolean tryAcquire() {
        long now = System.nanoTime();
        discardExpired(now);
        if (permits.size() >= limit) {
            return false;
        }
        permits.addLast(now);
        return true;
    }

    public synchronized Duration retryAfter() {
        long now = System.nanoTime();
        discardExpired(now);
        if (permits.isEmpty()) {
            return Duration.ZERO;
        }
        long remaining = windowNanos - (now - permits.peekFirst());
        return Duration.ofNanos(Math.max(0, remaining));
    }

    public synchronized int availablePermits() {
        discardExpired(System.nanoTime());
        return limit - permits.size();
    }

    private void discardExpired(long now) {
        while (!permits.isEmpty() && now - permits.peekFirst() >= windowNanos) {
            permits.removeFirst();
        }
    }

    public static void main(String[] args) {
        WindowedRateLimiter limiter = new WindowedRateLimiter(2, Duration.ofSeconds(1));
        System.out.println(limiter.tryAcquire());
        System.out.println(limiter.tryAcquire());
        System.out.println(limiter.tryAcquire());
        System.out.println("retry after: " + limiter.retryAfter().toMillis() + "ms");
    }
}
