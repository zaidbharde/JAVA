/** Computes the parity and population count of 64-bit values. */
public final class Daily20261028Extra013BitParity {
    private Daily20261028Extra013BitParity() { }

    /** Returns the number of set bits in the two's-complement representation. */
    public static int populationCount(long value) {
        return Long.bitCount(value);
    }

    /** Returns 0 for an even number of set bits, or 1 for an odd number. */
    public static int parity(long value) {
        return populationCount(value) & 1;
    }

    public static void main(String[] args) {
        long[] values = {0L, 1L, 3L, 7L, -1L, Long.MIN_VALUE, Long.MAX_VALUE};
        int[] counts = {0, 1, 2, 3, 64, 1, 63};
        for (int i = 0; i < values.length; i++) {
            int actualCount = populationCount(values[i]);
            int actualParity = parity(values[i]);
            if (actualCount != counts[i] || actualParity != (counts[i] & 1)) {
                throw new AssertionError("incorrect result for " + values[i]);
            }
        }
        if (populationCount(0x8000000000000001L) != 2
                || parity(0x8000000000000001L) != 0) {
            throw new AssertionError("high-bit handling failed");
        }
        System.out.println("BitParity self-check passed");
    }
}
