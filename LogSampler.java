import java.util.concurrent.ThreadLocalRandom;

/** Probabilistic sampler that preserves all severe events and samples routine ones. */
public final class LogSampler {
    private final double routineProbability;
    private final int severeThreshold;
    private long inspected;
    private long accepted;

    public LogSampler(double routineProbability, int severeThreshold) {
        if (routineProbability < 0.0 || routineProbability > 1.0) {
            throw new IllegalArgumentException("probability must be between zero and one");
        }
        this.routineProbability = routineProbability;
        this.severeThreshold = severeThreshold;
    }

    public synchronized boolean accept(int severity) {
        inspected++;
        boolean keep = severity >= severeThreshold
            || ThreadLocalRandom.current().nextDouble() < routineProbability;
        if (keep) accepted++;
        return keep;
    }

    public synchronized long inspected() { return inspected; }
    public synchronized long accepted() { return accepted; }

    public synchronized double acceptanceRate() {
        return inspected == 0 ? 0.0 : (double) accepted / inspected;
    }

    public synchronized void reset() {
        inspected = 0;
        accepted = 0;
    }
}

final class LogSamplerExample {
    public static void main(String[] args) {
        LogSampler sampler = new LogSampler(0.10, 8);
        for (int severity = 0; severity < 10; severity++) {
            if (sampler.accept(severity)) System.out.println("kept " + severity);
        }
        System.out.println(sampler.acceptanceRate());
    }
}
