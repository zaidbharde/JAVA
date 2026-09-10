import java.util.ArrayDeque;
import java.util.Deque;

/** Tracks timestamped requests and answers rolling-window capacity queries. */
public final class RateWindowLedger {
    private final long windowMillis;
    private final int limit;
    private final Deque<Long> timestamps = new ArrayDeque<>();

    public RateWindowLedger(long windowMillis, int limit) {
        if (windowMillis <= 0 || limit <= 0) {
            throw new IllegalArgumentException("window and limit must be positive");
        }
        this.windowMillis = windowMillis;
        this.limit = limit;
    }

    private void prune(long nowMillis) {
        long cutoff = nowMillis - windowMillis;
        while (!timestamps.isEmpty() && timestamps.peekFirst() <= cutoff) {
            timestamps.removeFirst();
        }
    }

    public synchronized boolean record(long nowMillis) {
        prune(nowMillis);
        if (timestamps.size() >= limit) {
            return false;
        }
        timestamps.addLast(nowMillis);
        return true;
    }

    public synchronized long retryAfterMillis(long nowMillis) {
        prune(nowMillis);
        if (timestamps.isEmpty()) {
            return 0L;
        }
        return Math.max(0L, timestamps.peekFirst() + windowMillis - nowMillis);
    }

    public synchronized int size(long nowMillis) {
        prune(nowMillis);
        return timestamps.size();
    }
}
