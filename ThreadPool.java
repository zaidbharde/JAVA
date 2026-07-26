import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom Thread Pool — fixed pool with task queue, graceful shutdown,
 * and statistics tracking.
 */
public class ThreadPool {

    // ── Worker thread ─────────────────────────────────────────────────
    private static class Worker extends Thread {
        private final BlockingQueue<Runnable> queue;
        private final AtomicInteger           completed;
        private volatile boolean              running = true;

        Worker(int id, BlockingQueue<Runnable> queue, AtomicInteger completed) {
            super("Worker-" + id);
            this.queue     = queue;
            this.completed = completed;
        }

        @Override
        public void run() {
            while (running || !queue.isEmpty()) {
                try {
                    Runnable task = queue.poll(100, TimeUnit.MILLISECONDS);
                    if (task != null) {
                        task.run();
                        completed.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        void shutdown() { running = false; }
    }

    // ── Pool ──────────────────────────────────────────────────────────
    private final List<Worker>             workers;
    private final BlockingQueue<Runnable>  taskQueue;
    private final AtomicInteger            submitted  = new AtomicInteger();
    private final AtomicInteger            completed  = new AtomicInteger();
    private       boolean                  isShutdown = false;

    public ThreadPool(int poolSize, int queueCapacity) {
        this.taskQueue = new LinkedBlockingQueue<>(queueCapacity);
        this.workers   = new ArrayList<>(poolSize);

        for (int i = 0; i < poolSize; i++) {
            Worker w = new Worker(i + 1, taskQueue, completed);
            workers.add(w);
            w.start();
        }
        System.out.printf("  ThreadPool started: %d workers, queue=%d%n",
            poolSize, queueCapacity);
    }

    public Future<?> submit(Runnable task) {
        if (isShutdown)
            throw new RejectedExecutionException("Pool is shut down.");

        FutureTask<?> ft = new FutureTask<>(task, null);
        if (!taskQueue.offer(ft)) {
            throw new RejectedExecutionException("Task queue is full.");
        }
        submitted.incrementAndGet();
        return ft;
    }

    public <T> Future<T> submit(Callable<T> task) {
        if (isShutdown)
            throw new RejectedExecutionException("Pool is shut down.");

        FutureTask<T> ft = new FutureTask<>(task);
        if (!taskQueue.offer(ft)) {
            throw new RejectedExecutionException("Task queue is full.");
        }
        submitted.incrementAndGet();
        return ft;
    }

    public void shutdown() throws InterruptedException {
        isShutdown = true;
        for (Worker w : workers) w.shutdown();
        for (Worker w : workers) w.join();
        System.out.println("  ThreadPool shut down.");
    }

    public void printStats() {
        System.out.printf("  Submitted: %d | Completed: %d | Pending: %d%n",
            submitted.get(), completed.get(),
            submitted.get() - completed.get());
    }

    // ── Demo ──────────────────────────────────────────────────────────
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(50));
        System.out.println("  Custom Thread Pool Demo");
        System.out.println("=".repeat(50));

        ThreadPool pool = new ThreadPool(4, 100);

        // Submit Runnable tasks
        System.out.println("\n  Submitting 10 tasks...");
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            final int id = i;
            futures.add(pool.submit(() -> {
                Thread.sleep(50 + (long)(Math.random() * 100));
                System.out.printf("    Task %2d done on %s%n",
                    id, Thread.currentThread().getName());
                return null;
            }));
        }

        // Submit Callable tasks (with return values)
        System.out.println("\n  Submitting 5 computation tasks...");
        List<Future<Long>> results = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            final int n = i * 10;
            results.add(pool.submit(() -> {
                long sum = 0;
                for (int j = 1; j <= n; j++) sum += j;
                return sum;
            }));
        }

        // Collect results
        System.out.println("\n  Results:");
        for (int i = 0; i < results.size(); i++) {
            System.out.printf("    sum(1..%d) = %d%n",
                (i + 1) * 10, results.get(i).get());
        }

        pool.shutdown();
        pool.printStats();
    }
}
