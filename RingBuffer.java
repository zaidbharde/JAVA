public class RingBuffer<T> {
    private final Object[] buffer;
    private int head = 0, tail = 0, size = 0;
    private final int capacity;

    public RingBuffer(int capacity) {
        this.capacity = capacity;
        buffer = new Object[capacity];
    }

    public boolean write(T item) {
        if (size == capacity) return false;
        buffer[tail] = item;
        tail = (tail + 1) % capacity;
        size++;
        return true;
    }

    @SuppressWarnings("unchecked")
    public T read() {
        if (size == 0) return null;
        T item = (T) buffer[head];
        head = (head + 1) % capacity;
        size--;
        return item;
    }

    public static void main(String[] args) {
        RingBuffer<Integer> rb = new RingBuffer<>(3);
        rb.write(1); rb.write(2); rb.write(3);
        System.out.println("Write 4 (should fail): " + rb.write(4));
        System.out.println(rb.read());
        System.out.println(rb.write(4));
        System.out.println(rb.read());
        System.out.println(rb.read());
        System.out.println(rb.read());
    }
}
