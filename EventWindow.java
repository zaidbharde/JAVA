import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.function.ToDoubleFunction;

/** Maintains a time-ordered event window and computes a rolling aggregate. */
public final class EventWindow<T> {
    private record Entry<T>(long timestamp, T value) {}

    private final long windowNanos;
    private final ToDoubleFunction<T> mapper;
    private final Deque<Entry<T>> entries = new ArrayDeque<>();
    private double total;

    public EventWindow(long windowNanos, ToDoubleFunction<T> mapper) {
        if (windowNanos <= 0) throw new IllegalArgumentException("window must be positive");
        this.windowNanos = windowNanos;
        this.mapper = Objects.requireNonNull(mapper);
    }

    public synchronized void add(T value) {
        long now = System.nanoTime();
        expire(now);
        entries.addLast(new Entry<>(now, value));
        total += mapper.applyAsDouble(value);
    }

    public synchronized double sum() {
        expire(System.nanoTime());
        return total;
    }

    public synchronized int size() {
        expire(System.nanoTime());
        return entries.size();
    }

    public synchronized double average() {
        expire(System.nanoTime());
        return entries.isEmpty() ? 0.0 : total / entries.size();
    }

    private void expire(long now) {
        long cutoff = now - windowNanos;
        while (!entries.isEmpty() && entries.peekFirst().timestamp() <= cutoff) {
            total -= mapper.applyAsDouble(entries.removeFirst().value());
        }
    }
}

final class EventWindowExample {
    public static void main(String[] args) {
        EventWindow<Integer> window = new EventWindow<>(1_000_000_000L, Integer::doubleValue);
        window.add(4);
        window.add(6);
        System.out.println(window.average());
    }
}
