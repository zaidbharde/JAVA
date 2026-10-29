/** Converts nonnegative integers to and from reflected binary Gray code. */
public final class Daily20261028Extra012GrayCode {
    private Daily20261028Extra012GrayCode() { }

    /** Returns the reflected Gray-code representation of a nonnegative value. */
    public static long encode(long value) {
        requireNonnegative(value);
        return value ^ (value >>> 1);
    }

    /** Decodes a nonnegative Gray-code value to its original integer. */
    public static long decode(long gray) {
        requireNonnegative(gray);
        long value = gray;
        for (long shifted = gray >>> 1; shifted != 0; shifted >>>= 1) {
            value ^= shifted;
        }
        return value;
    }

    private static void requireNonnegative(long value) {
        if (value < 0) throw new IllegalArgumentException("value must be nonnegative");
    }

    public static void main(String[] args) {
        long[] cases = {0, 1, 2, 3, 7, 8, 31, 1L << 32, Long.MAX_VALUE};
        long[] expectedGray = {0, 1, 3, 2, 4, 12, 16, (1L << 32) ^ (1L << 31),
                               Long.MAX_VALUE ^ (Long.MAX_VALUE >>> 1)};
        for (int i = 0; i < cases.length; i++) {
            long encoded = encode(cases[i]);
            if (encoded != expectedGray[i] || decode(encoded) != cases[i]) {
                throw new AssertionError("round trip failed for " + cases[i]);
            }
        }
        for (long invalid : new long[] {-1, Long.MIN_VALUE}) {
            try {
                encode(invalid);
                throw new AssertionError("negative input accepted");
            } catch (IllegalArgumentException expected) { }
            try {
                decode(invalid);
                throw new AssertionError("negative Gray code accepted");
            } catch (IllegalArgumentException expected) { }
        }
        System.out.println("GrayCode self-check passed");
    }
}
