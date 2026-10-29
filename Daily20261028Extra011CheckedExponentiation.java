/** Integer exponentiation by squaring with explicit overflow detection. */
public final class Daily20261028Extra011CheckedExponentiation {
    private Daily20261028Extra011CheckedExponentiation() { }

    /**
     * Returns {@code base^exponent}; rejects negative exponents and throws
     * {@link ArithmeticException} if the result is outside the long range.
     */
    public static long powExact(long base, int exponent) {
        if (exponent < 0) {
            throw new IllegalArgumentException("exponent must be non-negative");
        }
        long result = 1;
        long factor = base;
        int remaining = exponent;
        while (remaining > 0) {
            if ((remaining & 1) != 0) {
                result = Math.multiplyExact(result, factor);
            }
            remaining >>>= 1;
            if (remaining > 0) {
                factor = Math.multiplyExact(factor, factor);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        assert powExact(2, 10) == 1024;
        assert powExact(-3, 3) == -27;
        assert powExact(Long.MIN_VALUE, 1) == Long.MIN_VALUE;
        assert powExact(0, 0) == 1;
        try {
            powExact(2, 63);
            throw new AssertionError("overflow was not detected");
        } catch (ArithmeticException expected) {
            // Expected: 2^63 is not representable as a long.
        }
        try {
            powExact(2, -1);
            throw new AssertionError("negative exponent was accepted");
        } catch (IllegalArgumentException expected) {
            // Negative integer powers are outside this utility's domain.
        }
        System.out.println("CheckedExponentiation self-check passed");
    }
}
