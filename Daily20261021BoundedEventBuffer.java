import java.util.*;

/** Keeps the newest events while exposing how many older events were dropped. */
public final class Daily20261021BoundedEventBuffer<T> {
    private final int capacity;
    private final Deque<T> events = new ArrayDeque<>();
    private long dropped;

    public Daily20261021BoundedEventBuffer(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("capacity must be positive");
        this.capacity = capacity;
    }

    public void add(T event) {
        Objects.requireNonNull(event, "event");
        if (events.size() == capacity) { events.removeFirst(); dropped++; }
        events.addLast(event);
    }

    public List<T> snapshot() { return List.copyOf(events); }
    public long droppedCount() { return dropped; }
    public int size() { return events.size(); }

    public Optional<T> poll() { return Optional.ofNullable(events.pollFirst()); }

    public void clear() {
        events.clear();
        dropped = 0;
    }
}
