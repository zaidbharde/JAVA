import java.util.Arrays;

/** Fixed-capacity byte queue with O(1) append and removal. */
public final class ByteRingBuffer {
    private final byte[] storage;
    private int head;
    private int size;

    public ByteRingBuffer(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be positive");
        storage = new byte[capacity];
    }

    public int size() { return size; }
    public int remainingCapacity() { return storage.length - size; }

    public void offer(byte value) {
        if (size == storage.length) throw new IllegalStateException("buffer full");
        storage[(head + size) % storage.length] = value;
        size++;
    }

    public byte poll() {
        if (size == 0) throw new IllegalStateException("buffer empty");
        byte value = storage[head];
        head = (head + 1) % storage.length;
        size--;
        return value;
    }

    public byte[] drain() {
        byte[] result = new byte[size];
        for (int i = 0; i < result.length; i++) result[i] = poll();
        return result;
    }

    @Override public String toString() { return Arrays.toString(drain()); }
}
