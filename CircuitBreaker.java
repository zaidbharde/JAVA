public class CircuitBreaker {
    enum State { CLOSED, OPEN, HALF_OPEN }

    private State state = State.CLOSED;
    private int failureCount = 0;
    private final int failureThreshold;
    private long lastFailureTime;
    private final long resetTimeoutMs;

    public CircuitBreaker(int failureThreshold, long resetTimeoutMs) {
        this.failureThreshold = failureThreshold;
        this.resetTimeoutMs = resetTimeoutMs;
    }

    public boolean allowRequest() {
        if (state == State.OPEN) {
            if (System.currentTimeMillis() - lastFailureTime > resetTimeoutMs) {
                state = State.HALF_OPEN;
                return true;
            }
            return false;
        }
        return true;
    }

    public void recordSuccess() {
        failureCount = 0;
        state = State.CLOSED;
    }

    public void recordFailure() {
        failureCount++;
        lastFailureTime = System.currentTimeMillis();
        if (failureCount >= failureThreshold) state = State.OPEN;
    }

    public static void main(String[] args) throws InterruptedException {
        CircuitBreaker cb = new CircuitBreaker(3, 2000);
        for (int i = 0; i < 5; i++) {
            if (cb.allowRequest()) {
                System.out.println("Request " + i + " allowed, simulating failure");
                cb.recordFailure();
            } else {
                System.out.println("Request " + i + " blocked (circuit OPEN)");
            }
        }
    }
}
