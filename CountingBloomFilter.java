import java.util.BitSet;

/** Probabilistic membership filter with small counters for deletions. */
public final class CountingBloomFilter {
    private final byte[] counters;
    private final int hashCount;

    public CountingBloomFilter(int slots, int hashCount) {
        if (slots < 1 || hashCount < 1) throw new IllegalArgumentException();
        counters = new byte[slots];
        this.hashCount = hashCount;
    }

    public void add(String value) {
        for (int i = 0; i < hashCount; i++) {
            int slot = slot(value, i);
            if (counters[slot] < Byte.MAX_VALUE) counters[slot]++;
        }
    }

    public void remove(String value) {
        for (int i = 0; i < hashCount; i++) {
            int slot = slot(value, i);
            if (counters[slot] > 0) counters[slot]--;
        }
    }

    public boolean mightContain(String value) {
        for (int i = 0; i < hashCount; i++) if (counters[slot(value, i)] == 0) return false;
        return true;
    }

    private int slot(String value, int round) {
        int hash = value.hashCode() ^ (round * 0x9E3779B9);
        hash ^= hash >>> 16;
        return Math.floorMod(hash, counters.length);
    }
}
