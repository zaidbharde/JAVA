import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;

/** Small explicit state machine for resilient request processing. */
public final class CircuitStateMachine {
    public enum State { CLOSED, OPEN, HALF_OPEN }
    public enum Signal { SUCCESS, FAILURE, TIMEOUT }

    private final int failureThreshold;
    private final Duration openDuration;
    private final Map<State, Integer> transitions = new EnumMap<>(State.class);
    private State state = State.CLOSED;
    private int failures;
    private long openedAt;

    public CircuitStateMachine(int failureThreshold, Duration openDuration) {
        if (failureThreshold < 1 || openDuration.isNegative() || openDuration.isZero()) {
            throw new IllegalArgumentException("invalid circuit policy");
        }
        this.failureThreshold = failureThreshold;
        this.openDuration = openDuration;
    }

    public synchronized boolean allowRequest() {
        if (state == State.OPEN && elapsedSinceOpen() >= openDuration.toNanos()) {
            state = State.HALF_OPEN;
        }
        return state != State.OPEN;
    }

    public synchronized void record(Signal signal) {
        if (signal == Signal.SUCCESS) {
            failures = 0;
            state = State.CLOSED;
            return;
        }
        failures++;
        if (failures >= failureThreshold && state != State.OPEN) {
            state = State.OPEN;
            openedAt = System.nanoTime();
        }
    }

    public synchronized State state() { allowRequest(); return state; }
    public synchronized int consecutiveFailures() { return failures; }

    private long elapsedSinceOpen() { return System.nanoTime() - openedAt; }
}

final class CircuitStateMachineExample {
    public static void main(String[] args) {
        CircuitStateMachine circuit = new CircuitStateMachine(2, Duration.ofSeconds(1));
        circuit.record(CircuitStateMachine.Signal.FAILURE);
        circuit.record(CircuitStateMachine.Signal.TIMEOUT);
        System.out.println(circuit.state());
    }
}
