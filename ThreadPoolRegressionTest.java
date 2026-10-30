import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class ThreadPoolRegressionTest {
    public static void main(String[] args) throws Exception {
        for (int round = 0; round < 10; round++) runRaceRound();
        System.out.println("ThreadPool shutdown/submission regressions passed");
    }

    private static void runRaceRound() throws Exception {
        ThreadPool pool = new ThreadPool(2, 512);
        AtomicInteger executed = new AtomicInteger();
        List<Future<?>> accepted = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService callers = Executors.newFixedThreadPool(5);
        List<Future<?>> submitters = new ArrayList<>();

        for (int thread = 0; thread < 4; thread++) {
            submitters.add(callers.submit(() -> {
                start.await();
                for (int i = 0; i < 100; i++) {
                    try {
                        accepted.add(pool.submit((Runnable) executed::incrementAndGet));
                    } catch (RejectedExecutionException shutDownOrFull) {
                        break;
                    }
                }
                return null;
            }));
        }
        Future<?> shutdown = callers.submit(() -> {
            start.await();
            Thread.yield();
            pool.shutdown();
            return null;
        });

        start.countDown();
        for (Future<?> submitter : submitters) submitter.get(5, TimeUnit.SECONDS);
        shutdown.get(5, TimeUnit.SECONDS);
        for (Future<?> future : accepted) future.get(1, TimeUnit.SECONDS);
        callers.shutdownNow();

        if (executed.get() != accepted.size()) {
            throw new AssertionError("accepted=" + accepted.size() + ", executed=" + executed.get());
        }
    }
}
