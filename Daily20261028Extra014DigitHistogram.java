import java.util.Arrays;

/** Counts occurrences of decimal digits in a signed 64-bit integer. */
public final class Daily20261028Extra014DigitHistogram {
    private Daily20261028Extra014DigitHistogram() { }

    /** Returns counts indexed by digit (0 through 9); the sign is ignored. */
    public static int[] count(long value) {
        int[] counts = new int[10];
        // Keep the working value non-positive so Long.MIN_VALUE is representable.
        long remaining = value > 0 ? -value : value;
        do {
            int digit = (int) -(remaining % 10);
            counts[digit]++;
            remaining /= 10;
        } while (remaining != 0);
        return counts;
    }

    private static void check(long value, int... expected) {
        int[] actual = count(value);
        if (!Arrays.equals(actual, expected)) {
            throw new AssertionError(value + ": expected " + Arrays.toString(expected)
                    + ", got " + Arrays.toString(actual));
        }
    }

    public static void main(String[] args) {
        check(0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        check(-120230, 2, 1, 2, 1, 0, 0, 0, 0, 0, 0);
        check(Long.MIN_VALUE, 2, 0, 3, 3, 1, 2, 1, 3, 3, 1);
        System.out.println("Digit histogram self-check passed: "
                + Arrays.toString(count(-120230)));
    }
}
