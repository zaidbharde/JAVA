import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/** Counts timestamped events in the open-left interval (now - horizon, now]. */
public final class Daily20261028Extra038TimeWindowCounter {
    private final long horizonMillis;
    private final Deque<Long> events = new ArrayDeque<>();
    private long latestEvent = Long.MIN_VALUE;
    private long latestQuery = Long.MIN_VALUE;

    public Daily20261028Extra038TimeWindowCounter(long horizon, TimeUnit unit) {
        Objects.requireNonNull(unit, "unit");
        if (horizon <= 0) throw new IllegalArgumentException("horizon must be positive");
        this.horizonMillis = unit.toMillis(horizon);
        if (horizonMillis <= 0) throw new IllegalArgumentException("horizon must be at least one millisecond");
    }

    /** Adds one event; timestamps must be nondecreasing and not precede the latest query. */
    public synchronized void record(long timestampMillis) {
        if (timestampMillis < latestEvent || timestampMillis < latestQuery)
            throw new IllegalArgumentException("event timestamps must be chronological");
        events.addLast(timestampMillis);
        latestEvent = timestampMillis;
    }

    /** Returns the number of events in (nowMillis - horizon, nowMillis]. */
    public synchronized int count(long nowMillis) {
        if (nowMillis < latestQuery) throw new IllegalArgumentException("query time must be nondecreasing");
        latestQuery = nowMillis;
        long boundary;
        try { boundary = Math.subtractExact(nowMillis, horizonMillis); }
        catch (ArithmeticException overflow) { boundary = Long.MIN_VALUE; }
        while (!events.isEmpty() && events.peekFirst() <= boundary) events.removeFirst();
        return events.size();
    }

    public static void main(String[] args) {
        Daily20261028Extra038TimeWindowCounter c = new Daily20261028Extra038TimeWindowCounter(10, TimeUnit.SECONDS);
        c.record(1_000); c.record(5_000); c.record(11_000);
        if (c.count(11_000) != 2 || c.count(15_000) != 1 || c.count(21_000) != 0)
            throw new AssertionError("window boundary/count self-check failed");
        boolean rejected = false;
        try { c.record(20_000); } catch (IllegalArgumentException expected) { rejected = true; }
        if (!rejected) throw new AssertionError("out-of-order event was accepted");
        System.out.println("TimeWindowCounter self-check passed");
    }
}
