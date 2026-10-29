/** Computes a rolling 16-bit checksum over bytes (a compact Fletcher-style checksum). */
public final class Daily20261028Extra037RollingChecksum16 {
    private static final int MODULUS = 255;

    private Daily20261028Extra037RollingChecksum16() {}

    /** Returns the checksum as an unsigned value in the range 0..65535. */
    public static int checksum(byte[] bytes) {
        if (bytes == null) throw new IllegalArgumentException("bytes must not be null");
        int sum1 = 0;
        int sum2 = 0;
        for (byte value : bytes) {
            sum1 = (sum1 + (value & 0xFF)) % MODULUS;
            sum2 = (sum2 + sum1) % MODULUS;
        }
        return (sum2 << 8) | sum1;
    }

    /** Continues the checksum from a prior unsigned 16-bit checksum value. */
    public static int update(int previous, byte[] bytes) {
        if (previous < 0 || previous > 0xFFFF)
            throw new IllegalArgumentException("previous checksum must be unsigned 16-bit");
        if (bytes == null) throw new IllegalArgumentException("bytes must not be null");
        int sum1 = previous & 0xFF;
        int sum2 = (previous >>> 8) & 0xFF;
        if (sum1 >= MODULUS || sum2 >= MODULUS)
            throw new IllegalArgumentException("previous checksum is not canonical");
        for (byte value : bytes) {
            sum1 = (sum1 + (value & 0xFF)) % MODULUS;
            sum2 = (sum2 + sum1) % MODULUS;
        }
        return (sum2 << 8) | sum1;
    }

    public static void main(String[] args) {
        byte[] sample = {1, 2, 3, (byte) 0xFF};
        int whole = checksum(sample);
        int split = update(update(0, new byte[] {1, 2}), new byte[] {3, (byte) 0xFF});
        if (whole != split || checksum(new byte[0]) != 0 || whole < 0 || whole > 0xFFFF)
            throw new AssertionError("checksum self-check failed");
        System.out.printf("RollingChecksum16 self-check passed: 0x%04X%n", whole);
    }
}
