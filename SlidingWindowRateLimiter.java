import java.time.Clock;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/** Fixed-window-memory limiter that keeps each accepted request timestamp. */
public final class SlidingWindowRateLimiter {
    private final int limit;
    private final long windowMillis;
    private final Clock clock;
    private final Deque<Long> accepted = new ArrayDeque<>();

    public SlidingWindowRateLimiter(int limit, long windowMillis, Clock clock) {
        if (limit <= 0 || windowMillis <= 0) {
            throw new IllegalArgumentException("limit and window must be positive");
        }
        this.limit = limit;
        this.windowMillis = windowMillis;
        this.clock = Objects.requireNonNull(clock);
    }

    public synchronized boolean tryAcquire() {
        long now = clock.millis();
        discardExpired(now);
        if (accepted.size() >= limit) {
            return false;
        }
        accepted.addLast(now);
        return true;
    }

    public synchronized int remaining() {
        discardExpired(clock.millis());
        return limit - accepted.size();
    }

    private void discardExpired(long now) {
        long cutoff = now - windowMillis;
        while (!accepted.isEmpty() && accepted.peekFirst() <= cutoff) {
            accepted.removeFirst();
        }
    }

    public synchronized long retryAfterMillis() {
        if (accepted.size() < limit) {
            return 0;
        }
        long expiry = accepted.peekFirst() + windowMillis;
        return Math.max(0, expiry - clock.millis());
    }
}
