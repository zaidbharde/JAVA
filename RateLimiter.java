import java.util.concurrent.atomic.AtomicLong;

public class RateLimiter {
    private final long capacity;
    private final double refillRatePerMs;
    private AtomicLong tokens;
    private volatile long lastRefillTimestamp;

    public RateLimiter(long capacity, double refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerMs = refillRatePerSecond / 1000.0;
        this.tokens = new AtomicLong(capacity);
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {
        refill();
        if (tokens.get() > 0) {
            tokens.decrementAndGet();
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTimestamp;
        long tokensToAdd = (long) (elapsed * refillRatePerMs);
        if (tokensToAdd > 0) {
            tokens.set(Math.min(capacity, tokens.get() + tokensToAdd));
            lastRefillTimestamp = now;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        RateLimiter limiter = new RateLimiter(5, 1); // 5 tokens, refill 1/sec
        for (int i = 0; i < 8; i++) {
            System.out.println("Request " + i + " allowed? " + limiter.allowRequest());
            Thread.sleep(200);
        }
    }
}
