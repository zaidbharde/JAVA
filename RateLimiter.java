import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.*;

public class RateLimiter {

    public record AcquireResult(boolean allowed, long remainingTokens, long retryAfterMs, String strategyName) {
        @Override
        public String toString() {
            return allowed
                ? String.format("[%s] ALLOWED remaining=%-4d", strategyName, remainingTokens)
                : String.format("[%s] REJECTED retry-after=%dms", strategyName, retryAfterMs);
        }
    }

    public interface Strategy {
        AcquireResult tryAcquire();
        Metrics metrics();
        String name();
        void reset();
    }

    public static class Metrics {
        private final AtomicLong allowed = new AtomicLong();
        private final AtomicLong rejected = new AtomicLong();

        void recordAllowed() { allowed.incrementAndGet(); }
        void recordRejected() { rejected.incrementAndGet(); }

        public long allowed() { return allowed.get(); }
        public long rejected() { return rejected.get(); }
        public long total() { return allowed.get() + rejected.get(); }

        public double rejectionRate() {
            long t = total();
            return t == 0 ? 0.0 : (double) rejected.get() / t * 100.0;
        }

        @Override
        public String toString() {
            return String.format("allowed=%d rejected=%d total=%d rejection=%.1f%%",
                allowed(), rejected(), total(), rejectionRate());
        }
    }

    public static class TokenBucket implements Strategy {
        private final long capacity;
        private final double refillRatePerNs;
        private final Metrics metrics = new Metrics();
        private final Lock lock = new ReentrantLock();
        private double tokens;
        private long lastRefillNs;

        public TokenBucket(long capacity, double refillRatePerSecond) {
            if (capacity <= 0) throw new IllegalArgumentException("capacity > 0");
            if (refillRatePerSecond <= 0) throw new IllegalArgumentException("refillRate > 0");
            this.capacity = capacity;
            this.refillRatePerNs = refillRatePerSecond / 1_000_000_000.0;
            this.tokens = capacity;
            this.lastRefillNs = System.nanoTime();
        }

        @Override
        public AcquireResult tryAcquire() {
            lock.lock();
            try {
                refill();
                if (tokens >= 1.0) {
                    tokens--;
                    metrics.recordAllowed();
                    return new AcquireResult(true, (long) tokens, 0, name());
                }
                long retryAfterMs = (long) ((1.0 - tokens) / refillRatePerNs / 1_000_000.0);
                metrics.recordRejected();
                return new AcquireResult(false, 0, retryAfterMs, name());
            } finally {
                lock.unlock();
            }
        }

        private void refill() {
            long now = System.nanoTime();
            long elapsed = now - lastRefillNs;
            double toAdd = elapsed * refillRatePerNs;
            if (toAdd >= 0.001) {
                tokens = Math.min(capacity, tokens + toAdd);
                lastRefillNs = now;
            }
        }

        @Override public Metrics metrics() { return metrics; }
        @Override public String name() { return "TokenBucket"; }

        @Override
        public void reset() {
            lock.lock();
            try {
                tokens = capacity;
                lastRefillNs = System.nanoTime();
            } finally {
                lock.unlock();
            }
        }
    }

    public static class FixedWindow implements Strategy {
        private final long limit;
        private final long windowMs;
        private final Metrics metrics = new Metrics();
        private final Lock lock = new ReentrantLock();
        private long count;
        private long windowStart;

        public FixedWindow(long limit, long windowMs) {
            if (limit <= 0) throw new IllegalArgumentException("limit > 0");
            if (windowMs <= 0) throw new IllegalArgumentException("windowMs > 0");
            this.limit = limit;
            this.windowMs = windowMs;
            this.windowStart = System.currentTimeMillis();
        }

        @Override
        public AcquireResult tryAcquire() {
            lock.lock();
            try {
                long now = System.currentTimeMillis();
                if (now - windowStart >= windowMs) {
                    windowStart = now;
                    count = 0;
                }
                if (count < limit) {
                    count++;
                    metrics.recordAllowed();
                    return new AcquireResult(true, limit - count, 0, name());
                }
                long retryAfterMs = windowMs - (now - windowStart);
                metrics.recordRejected();
                return new AcquireResult(false, 0, retryAfterMs, name());
            } finally {
                lock.unlock();
            }
        }

        @Override public Metrics metrics() { return metrics; }
        @Override public String name() { return "FixedWindow"; }

        @Override
        public void reset() {
            lock.lock();
            try {
                count = 0;
                windowStart = System.currentTimeMillis();
            } finally {
                lock.unlock();
            }
        }
    }

    public static class SlidingWindowLog implements Strategy {
        private final long limit;
        private final long windowMs;
        private final Deque<Long> log = new ArrayDeque<>();
        private final Metrics metrics = new Metrics();
        private final Lock lock = new ReentrantLock();

        public SlidingWindowLog(long limit, long windowMs) {
            if (limit <= 0) throw new IllegalArgumentException("limit > 0");
            if (windowMs <= 0) throw new IllegalArgumentException("windowMs > 0");
            this.limit = limit;
            this.windowMs = windowMs;
        }

        @Override
        public AcquireResult tryAcquire() {
            lock.lock();
            try {
                long now = System.currentTimeMillis();
                long boundary = now - windowMs;
                while (!log.isEmpty() && log.peekFirst() <= boundary) {
                    log.pollFirst();
                }
                if (log.size() < limit) {
                    log.addLast(now);
                    metrics.recordAllowed();
                    return new AcquireResult(true, limit - log.size(), 0, name());
                }
                long retryAfterMs = log.peekFirst() + windowMs - now;
                metrics.recordRejected();
                return new AcquireResult(false, 0, Math.max(0, retryAfterMs), name());
            } finally {
                lock.unlock();
            }
        }

        @Override public Metrics metrics() { return metrics; }
        @Override public String name() { return "SlidingWindowLog"; }

        @Override
        public void reset() {
            lock.lock();
            try { log.clear(); }
            finally { lock.unlock(); }
        }
    }

    public static class Registry<S extends Strategy> {
        private final ConcurrentHashMap<String, S> limiters = new ConcurrentHashMap<>();
        private final java.util.function.Supplier<S> factory;

        public Registry(java.util.function.Supplier<S> factory) {
            this.factory = factory;
        }

        public AcquireResult tryAcquire(String clientId) {
            return limiters.computeIfAbsent(clientId, k -> factory.get()).tryAcquire();
        }

        public Optional<S> limiterFor(String clientId) {
            return Optional.ofNullable(limiters.get(clientId));
        }

        public Map<String, Metrics> allMetrics() {
            Map<String, Metrics> map = new LinkedHashMap<>();
            limiters.forEach((k, v) -> map.put(k, v.metrics()));
            return Collections.unmodifiableMap(map);
        }
    }

    private static void printHeader(String title) {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.printf("║  %-48s║%n", title);
        System.out.println("╚══════════════════════════════════════════════════╝");
    }

    private static void runBurst(Strategy strategy, int requests, long sleepMs) throws InterruptedException {
        System.out.printf("  Sending %d requests (sleep %dms between each):%n", requests, sleepMs);
        for (int i = 1; i <= requests; i++) {
            AcquireResult r = strategy.tryAcquire();
            System.out.printf("    #%-3d %s%n", i, r);
            if (sleepMs > 0) Thread.sleep(sleepMs);
        }
        System.out.println("  Metrics → " + strategy.metrics());
    }

    public static void main(String[] args) throws InterruptedException {
        printHeader("1. Token Bucket  (cap=5, refill=2/sec)");
        TokenBucket bucket = new TokenBucket(5, 2.0);
        System.out.println("  Burst phase – drain all 5 tokens instantly:");
        runBurst(bucket, 7, 0);

        System.out.println("\n  Recovery phase – 1 request every 600ms (refill catches up):");
        for (int i = 1; i <= 4; i++) {
            Thread.sleep(600);
            AcquireResult r = bucket.tryAcquire();
            System.out.printf("    after 600ms  %s%n", r);
        }
        System.out.println("  Metrics → " + bucket.metrics());

        printHeader("2. Fixed Window  (limit=3, window=1000ms)");
        FixedWindow fixed = new FixedWindow(3, 1000);
        System.out.println("  5 rapid requests (expect 3 allowed, 2 rejected):");
        runBurst(fixed, 5, 0);

        System.out.println("\n  Waiting 1100ms for window to roll over…");
        Thread.sleep(1100);
        System.out.println("  3 more requests (expect all allowed):");
        runBurst(fixed, 3, 0);

        printHeader("3. Sliding Window Log  (limit=3, window=1000ms)");
        SlidingWindowLog sliding = new SlidingWindowLog(3, 1000);
        System.out.println("  5 rapid requests:");
        runBurst(sliding, 5, 0);

        System.out.println("\n  Draining window gradually (400ms gaps):");
        for (int i = 1; i <= 4; i++) {
            Thread.sleep(400);
            AcquireResult r = sliding.tryAcquire();
            System.out.printf("    after 400ms  %s%n", r);
        }
        System.out.println("  Metrics → " + sliding.metrics());

        printHeader("4. Per-client Registry  (TokenBucket cap=3, refill=1/sec)");
        Registry<TokenBucket> registry = new Registry<>(() -> new TokenBucket(3, 1.0));
        String[] clients = { "alice", "bob", "alice", "alice", "bob", "charlie", "alice" };
        System.out.println("  Request sequence: " + Arrays.toString(clients));
        for (String client : clients) {
            AcquireResult r = registry.tryAcquire(client);
            System.out.printf("    %-10s %s%n", client, r);
        }

        System.out.println("\n  Per-client metrics:");
        registry.allMetrics().forEach((client, m) ->
            System.out.printf("    %-10s %s%n", client, m));

        printHeader("5. Concurrent Stress Test  (TokenBucket cap=10, refill=5/sec)");
        TokenBucket stressed = new TokenBucket(10, 5.0);
        int threads = 8;
        int each = 20;
        CountDownLatch latch = new CountDownLatch(threads);
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        AtomicLong allowed = new AtomicLong();
        AtomicLong rejected = new AtomicLong();

        System.out.printf("  %d threads × %d requests = %d total%n", threads, each, threads * each);

        for (int t = 0; t < threads; t++) {
            pool.submit(() -> {
                for (int r = 0; r < each; r++) {
                    if (stressed.tryAcquire().allowed()) allowed.incrementAndGet();
                    else rejected.incrementAndGet();
                }
                latch.countDown();
            });
        }

        latch.await();
        pool.shutdown();

        System.out.println("  Counters → allowed=" + allowed + "  rejected=" + rejected);
        System.out.println("  Metrics  → " + stressed.metrics());
        System.out.println("  (allowed + rejected should equal " + (threads * each) + ")");
    }
}
