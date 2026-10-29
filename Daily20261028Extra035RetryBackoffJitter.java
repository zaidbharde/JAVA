/** Calculates capped exponential retry delays using an explicit jitter sample. */
public final class Daily20261028Extra035RetryBackoffJitter {
    private Daily20261028Extra035RetryBackoffJitter() {}

    /**
     * Returns milliseconds for a zero-based retry number. jitterSample is in [0,1]
     * and maps linearly to the symmetric range [1-jitterFraction, 1+jitterFraction].
     */
    public static long delayMillis(int retryNumber, long baseMillis, long capMillis,
                                   double jitterFraction, double jitterSample) {
        if (retryNumber < 0 || baseMillis < 0 || capMillis < baseMillis
                || !Double.isFinite(jitterFraction) || jitterFraction < 0 || jitterFraction > 1
                || !Double.isFinite(jitterSample) || jitterSample < 0 || jitterSample > 1) {
            throw new IllegalArgumentException("invalid retry or jitter parameters");
        }
        long exponential = baseMillis;
        for (int i = 0; i < retryNumber && exponential < capMillis; i++) {
            exponential = exponential > capMillis / 2 ? capMillis : exponential * 2;
        }
        double factor = 1.0 - jitterFraction + 2.0 * jitterFraction * jitterSample;
        double adjusted = Math.min((double) capMillis, exponential * factor);
        return Math.min(capMillis, Math.round(adjusted));
    }

    public static void main(String[] args) {
        assert delayMillis(0, 100, 500, 0.0, 0.25) == 100;
        assert delayMillis(1, 100, 500, 0.5, 0.0) == 100;
        assert delayMillis(1, 100, 500, 0.5, 1.0) == 300;
        assert delayMillis(10, 100, 500, 0.0, 0.5) == 500;
        boolean rejected = false;
        try { delayMillis(0, 100, 500, 0.1, Double.NaN); }
        catch (IllegalArgumentException expected) { rejected = true; }
        assert rejected;
        System.out.println("RetryBackoffJitter self-check passed");
    }
}
