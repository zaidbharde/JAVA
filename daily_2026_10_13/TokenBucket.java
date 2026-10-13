/** Thread-safe token bucket for bounded request rates. */
public final class TokenBucket {
    private final double capacity;
    private final double refillPerSecond;
    private double tokens;
    private long lastNanos;

    public TokenBucket(double capacity, double refillPerSecond) {
        if (capacity <= 0 || refillPerSecond <= 0) {
            throw new IllegalArgumentException("rates must be positive");
        }
        this.capacity = capacity;
        this.refillPerSecond = refillPerSecond;
        this.tokens = capacity;
        this.lastNanos = System.nanoTime();
    }

    public synchronized boolean tryAcquire() {
        return tryAcquire(1.0);
    }

    public synchronized boolean tryAcquire(double requested) {
        if (requested <= 0 || requested > capacity) {
            throw new IllegalArgumentException("request outside bucket bounds");
        }
        refill();
        if (tokens < requested) return false;
        tokens -= requested;
        return true;
    }

    public synchronized double availableTokens() {
        refill();
        return tokens;
    }

    private void refill() {
        long now = System.nanoTime();
        double elapsed = (now - lastNanos) / 1_000_000_000.0;
        tokens = Math.min(capacity, tokens + elapsed * refillPerSecond);
        lastNanos = now;
    }

    public static void main(String[] args) {
        TokenBucket bucket = new TokenBucket(3, 2);
        System.out.println(bucket.tryAcquire() + " " + bucket.availableTokens());
    }
}
