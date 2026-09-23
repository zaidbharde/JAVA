import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Maintains non-overlapping half-open time intervals. */
public final class IntervalCalendar {
    private final List<Slot> slots = new ArrayList<>();

    public boolean reserve(int start, int end) {
        if (start >= end) {
            throw new IllegalArgumentException("start must precede end");
        }
        for (Slot slot : slots) {
            if (start < slot.end() && slot.start() < end) {
                return false;
            }
        }
        slots.add(new Slot(start, end));
        slots.sort(Comparator.comparingInt(Slot::start));
        return true;
    }

    public boolean cancel(int start, int end) {
        return slots.remove(new Slot(start, end));
    }

    public int nextAvailable(int from, int duration) {
        if (duration <= 0) {
            throw new IllegalArgumentException("duration must be positive");
        }
        int candidate = from;
        for (Slot slot : slots) {
            if (candidate + duration <= slot.start()) {
                return candidate;
            }
            if (candidate < slot.end()) {
                candidate = slot.end();
            }
        }
        return candidate;
    }

    public List<Slot> snapshot() {
        return List.copyOf(slots);
    }

    public record Slot(int start, int end) {
        public Slot {
            if (start >= end) throw new IllegalArgumentException("invalid slot");
        }
    }
}
