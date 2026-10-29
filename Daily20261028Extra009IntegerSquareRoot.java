public final class Daily20261028Extra009IntegerSquareRoot {
    private Daily20261028Extra009IntegerSquareRoot() { }

    /** Returns floor(sqrt(value)); rejects negative input. */
    public static long floorSqrt(long value) {
        if (value < 0) throw new IllegalArgumentException("Value must be nonnegative");
        if (value < 2) return value;

        // Binary search without multiplication, avoiding overflow at Long.MAX_VALUE.
        long low = 1;
        long high = Math.min(value, 3_037_000_499L); // floor(sqrt(Long.MAX_VALUE))
        long answer = 1;
        while (low <= high) {
            long mid = low + ((high - low) >>> 1);
            if (mid <= value / mid) {
                answer = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return answer;
    }

    public static void main(String[] args) {
        long[][] cases = {
            {0, 0}, {1, 1}, {2, 1}, {3, 1}, {4, 2},
            {15, 3}, {16, 4}, {17, 4},
            {9_223_372_030_926_249_001L, 3_037_000_499L},
            {Long.MAX_VALUE, 3_037_000_499L}
        };
        for (long[] test : cases) {
            if (floorSqrt(test[0]) != test[1])
                throw new AssertionError("floorSqrt(" + test[0] + ")");
        }
        try {
            floorSqrt(-1);
            throw new AssertionError("Negative input accepted");
        } catch (IllegalArgumentException expected) { }
        System.out.println("IntegerSquareRoot self-check passed");
    }
}
