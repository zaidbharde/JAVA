import java.util.ArrayDeque;
import java.util.Queue;

public class BoundedQueue<T> {
    private final Queue<T> queue;
    private final int capacity;

    public BoundedQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be > 0");
        this.capacity = capacity;
        this.queue = new ArrayDeque<>(capacity);
    }

    public synchronized void put(T item) throws InterruptedException {
        while (queue.size() == capacity) {
            wait();
        }
        queue.add(item);
        notifyAll(); 
    }

    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        T item = queue.poll();
        notifyAll(); 
        return item;
    }

    public synchronized int size() {
        return queue.size();
    }

    public static void main(String[] args) throws InterruptedException {
        BoundedQueue<Integer> bq = new BoundedQueue<>(2);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    System.out.println("Putting: " + i);
                    bq.put(i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        Thread.sleep(1000); 

        System.out.println("Taken: " + bq.take());
        System.out.println("Taken: " + bq.take());
        System.out.println("Taken: " + bq.take());

        producer.join();
    }
}
