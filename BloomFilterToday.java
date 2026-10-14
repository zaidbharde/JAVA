import java.nio.charset.StandardCharsets;
import java.util.BitSet;

/** Compact probabilistic membership filter using double hashing. */
public final class BloomFilterToday {
    private final BitSet bits;
    private final int mask;
    private final int rounds;

    public BloomFilterToday(int bitCount, int rounds) {
        if (bitCount < 64 || Integer.bitCount(bitCount) != 1 || rounds < 1)
            throw new IllegalArgumentException("bitCount must be a power of two");
        this.bits = new BitSet(bitCount);
        this.mask = bitCount - 1;
        this.rounds = rounds;
    }

    public void add(String value) {
        long[] hashes = hashes(value);
        for (int i = 0; i < rounds; i++) bits.set((int) (hashes[0] + i * hashes[1]) & mask);
    }

    public boolean mightContain(String value) {
        long[] hashes = hashes(value);
        for (int i = 0; i < rounds; i++)
            if (!bits.get((int) (hashes[0] + i * hashes[1]) & mask)) return false;
        return true;
    }

    private static long[] hashes(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        long first = 0xcbf29ce484222325L;
        long second = 0x9e3779b97f4a7c15L;
        for (byte b : bytes) {
            first = (first ^ (b & 0xff)) * 0x100000001b3L;
            second ^= (second << 7) + (b & 0xff) + (second >>> 3);
        }
        return new long[]{first, second | 1};
    }
}
