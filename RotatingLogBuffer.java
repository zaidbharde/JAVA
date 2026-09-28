import java.util.ArrayList;
import java.util.List;

/** Fixed-capacity ring buffer that retains the newest log messages. */
public final class RotatingLogBuffer {
    private final String[] entries;
    private int next;
    private int size;

    public RotatingLogBuffer(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity");
        entries = new String[capacity];
    }

    public void append(String message) {
        entries[next] = message == null ? "" : message;
        next = (next + 1) % entries.length;
        if (size < entries.length) size++;
    }

    public List<String> snapshot() {
        List<String> result = new ArrayList<>(size);
        int first = (next - size + entries.length) % entries.length;
        for (int i = 0; i < size; i++) result.add(entries[(first + i) % entries.length]);
        return result;
    }

    public int size() {
        return size;
    }

    public static void main(String[] args) {
        RotatingLogBuffer logs = new RotatingLogBuffer(2);
        logs.append("connected");
        logs.append("ready");
        logs.append("served");
        System.out.println(logs.snapshot());
    }
}
