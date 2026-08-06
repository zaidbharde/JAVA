/**
 * Stack implementation using a fixed-size array.
 * Supports push, pop, peek, and overflow/underflow detection.
 */
public class StackUsingArray {

    private final int[] data;
    private int top;
    private final int capacity;

    public StackUsingArray(int capacity) {
        if (capacity <= 0)
            throw new IllegalArgumentException("Capacity must be positive.");
        this.capacity = capacity;
        this.data     = new int[capacity];
        this.top      = -1;
    }

    // ── Operations ────────────────────────────────────────────────────

    public void push(int value) {
        if (isFull())
            throw new RuntimeException("Stack Overflow — capacity: " + capacity);
        data[++top] = value;
    }

    public int pop() {
        if (isEmpty())
            throw new RuntimeException("Stack Underflow — stack is empty.");
        return data[top--];
    }

    public int peek() {
        if (isEmpty())
            throw new RuntimeException("Stack is empty.");
        return data[top];
    }

    public boolean isEmpty()  { return top == -1; }
    public boolean isFull()   { return top == capacity - 1; }
    public int     size()     { return top + 1; }

    @Override
    public String toString() {
        if (isEmpty()) return "Stack []";
        StringBuilder sb = new StringBuilder("Stack [");
        for (int i = 0; i <= top; i++) {
            sb.append(data[i]);
            if (i < top) sb.append(", ");
        }
        return sb.append("]  ← top").toString();
    }

    // ── Demo ──────────────────────────────────────────────────────────
    public static void main(String[] args) {
        StackUsingArray stack = new StackUsingArray(5);

        System.out.println("=".repeat(40));
        System.out.println("  Stack Using Array Demo");
        System.out.println("=".repeat(40));

        for (int v : new int[]{10, 20, 30, 40, 50}) {
            stack.push(v);
            System.out.println("Pushed " + v + "  →  " + stack);
        }

        System.out.println("\nPeek   : " + stack.peek());
        System.out.println("Pop    : " + stack.pop());
        System.out.println("Pop    : " + stack.pop());
        System.out.println("After  : " + stack);
        System.out.println("Size   : " + stack.size());

        // Test overflow
        try {
            for (int i = 0; i < 10; i++) stack.push(i);
        } catch (RuntimeException e) {
            System.out.println("\nCaught : " + e.getMessage());
        }
    }
}
