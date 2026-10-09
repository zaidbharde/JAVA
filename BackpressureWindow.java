/** Thread-safe token bucket for bounded request admission. */
public final class BackpressureWindow {
    private final long capacity;
    private final double refillPerSecond;
    private double tokens;
    private long lastNanos;

    public BackpressureWindow(long capacity, double refillPerSecond) {
        if (capacity <= 0 || refillPerSecond <= 0.0) {
            throw new IllegalArgumentException("capacity and refill rate must be positive");
        }
        this.capacity = capacity;
        this.refillPerSecond = refillPerSecond;
        this.tokens = capacity;
        this.lastNanos = System.nanoTime();
    }

    public synchronized boolean tryAcquire() {
        refill();
        if (tokens < 1.0) {
            return false;
        }
        tokens -= 1.0;
        return true;
    }

    public synchronized long nanosUntilAvailable() {
        refill();
        if (tokens >= 1.0) {
            return 0L;
        }
        return (long) (((1.0 - tokens) / refillPerSecond) * 1_000_000_000L);
    }

    private void refill() {
        long now = System.nanoTime();
        double elapsed = (now - lastNanos) / 1_000_000_000.0;
        tokens = Math.min(capacity, tokens + elapsed * refillPerSecond);
        lastNanos = now;
    }
}
