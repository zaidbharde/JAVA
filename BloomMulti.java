import java.util.BitSet;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public class BloomFilter {

    private static final Logger LOGGER = Logger.getLogger(BloomFilter.class.getName());

    private static final int DEFAULT_SIZE       = 1024;
    private static final int DEFAULT_HASH_COUNT = 5;
    private static final int[] HASH_SEEDS       = {7, 31, 113, 127, 997, 1009, 2003};

    private final BitSet bitSet;
    private final int    bitSetSize;
    private final int    hashCount;
    private       int    itemCount;

    public BloomFilter() {
        this(DEFAULT_SIZE, DEFAULT_HASH_COUNT);
    }

    public BloomFilter(int bitSetSize, int hashCount) {
        if (bitSetSize < 64) {
            throw new IllegalArgumentException("BitSet size must be at least 64");
        }
        if (hashCount < 1 || hashCount > HASH_SEEDS.length) {
            throw new IllegalArgumentException(
                String.format("Hash count must be between 1 and %d", HASH_SEEDS.length)
            );
        }

        this.bitSetSize = bitSetSize;
        this.hashCount  = hashCount;
        this.bitSet     = new BitSet(bitSetSize);
        this.itemCount  = 0;

        LOGGER.info(String.format(
            "BloomFilter initialized | Size: %d bits | Hash functions: %d | " +
            "Expected false positive rate: %.2f%%",
            bitSetSize, hashCount, estimateFalsePositiveRate(0) * 100
        ));
    }

    public static BloomFilter withExpectedItems(int expectedItems, double falsePositiveRate) {
        if (expectedItems < 1) {
            throw new IllegalArgumentException("Expected items must be at least 1");
        }
        if (falsePositiveRate <= 0 || falsePositiveRate >= 1) {
            throw new IllegalArgumentException("False positive rate must be between 0 and 1");
        }

        int optimalSize = (int) Math.ceil(
            -(expectedItems * Math.log(falsePositiveRate)) /
            (Math.log(2) * Math.log(2))
        );

        int optimalHashCount = (int) Math.round(
            ((double) optimalSize / expectedItems) * Math.log(2)
        );

        optimalHashCount = Math.max(1, Math.min(optimalHashCount, HASH_SEEDS.length));
        optimalSize      = Math.max(64, optimalSize);

        LOGGER.info(String.format(
            "Optimal parameters calculated | Expected items: %d | " +
            "Target FP rate: %.2f%% | Optimal size: %d | Optimal hashes: %d",
            expectedItems, falsePositiveRate * 100, optimalSize, optimalHashCount
        ));

        return new BloomFilter(optimalSize, optimalHashCount);
    }

    public void add(String item) {
        Objects.requireNonNull(item, "Item cannot be null");

        for (int i = 0; i < hashCount; i++) {
            bitSet.set(computeHash(item, i));
        }
        itemCount++;
    }

    public void addAll(Iterable<String> items) {
        Objects.requireNonNull(items, "Items collection cannot be null");
        items.forEach(this::add);
    }

    public boolean mightContain(String item) {
        Objects.requireNonNull(item, "Item cannot be null");

        for (int i = 0; i < hashCount; i++) {
            if (!bitSet.get(computeHash(item, i))) {
                return false;
            }
        }
        return true;
    }

    public void clear() {
        bitSet.clear();
        itemCount = 0;
        LOGGER.info("BloomFilter cleared");
    }

    private int computeHash(String item, int index) {
        int seed = HASH_SEEDS[index];
        int hash = 0;

        for (char c : item.toCharArray()) {
            hash = seed * hash + c;
        }

        return Math.abs(hash) % bitSetSize;
    }

    public double estimateFalsePositiveRate(int insertedItems) {
        return Math.pow(
            1 - Math.exp(-(double) hashCount * insertedItems / bitSetSize),
            hashCount
        );
    }

    public double fillRatio() {
        return (double) bitSet.cardinality() / bitSetSize;
    }

    public int    getBitSetSize() { return bitSetSize;                  }
    public int    getHashCount()  { return hashCount;                   }
    public int    getItemCount()  { return itemCount;                   }
    public BitSet getBitSet()     { return (BitSet) bitSet.clone();     }

    public void printStats() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║       Bloom Filter Statistics        ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.printf("  Bit array size  : %,d bits%n",   bitSetSize);
        System.out.printf("  Hash functions  : %d%n",         hashCount);
        System.out.printf("  Items inserted  : %d%n",         itemCount);
        System.out.printf("  Bits set        : %d%n",         bitSet.cardinality());
        System.out.printf("  Fill ratio      : %.2f%%%n",     fillRatio() * 100);
        System.out.printf("  Estimated FP%%   : %.4f%%%n",
            estimateFalsePositiveRate(itemCount) * 100);
        System.out.println("──────────────────────────────────────");
    }

    @Override
    public String toString() {
        return String.format(
            "BloomFilter{size=%d, hashCount=%d, items=%d, fillRatio=%.2f%%, estimatedFP=%.4f%%}",
            bitSetSize, hashCount, itemCount,
            fillRatio() * 100,
            estimateFalsePositiveRate(itemCount) * 100
        );
    }

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║         BloomFilter Demo             ║");
        System.out.println("╚══════════════════════════════════════╝");

        BloomFilter filter = new BloomFilter(1024, 5);

        List<String> programmingLanguages = List.of(
            "java", "python", "kotlin", "scala", "rust",
            "go", "typescript", "swift", "cpp", "haskell"
        );

        System.out.println("\n[1] Adding items to filter...");
        filter.addAll(programmingLanguages);
        programmingLanguages.forEach(lang ->
            System.out.printf("  Added: %-12s | mightContain: %b%n", lang, filter.mightContain(lang))
        );

        System.out.println("\n[2] Checking membership...");
        List<String> testItems = List.of(
            "java", "python", "ruby", "perl", "javascript", "cobol"
        );

        testItems.forEach(item -> {
            boolean result = filter.mightContain(item);
            String  label  = result ? "MIGHT CONTAIN (check DB)" : "DEFINITELY NOT in set";
            System.out.printf("  %-14s → %s%n", item, label);
        });

        System.out.println("\n[3] Optimal filter for 1000 items at 1% FP rate...");
        BloomFilter optimalFilter = BloomFilter.withExpectedItems(1000, 0.01);
        optimalFilter.printStats();

        System.out.println("\n[4] False positive rate vs fill ratio:");
        System.out.println("────────────────────────────────────────");
        System.out.printf("  %-10s | %-12s | %s%n", "Items", "Fill Ratio", "Est. FP Rate");
        System.out.println("  ──────────────────────────────────────");

        BloomFilter testFilter = new BloomFilter(512, 3);
        Set<String> added      = new HashSet<>();
        int step = 20;

        for (int n = step; n <= 200; n += step) {
            while (added.size() < n) {
                String word = "word" + added.size();
                testFilter.add(word);
                added.add(word);
            }
            System.out.printf("  %-10d | %-12.2f%% | %.4f%%%n",
                n,
                testFilter.fillRatio() * 100,
                testFilter.estimateFalsePositiveRate(n) * 100
            );
        }

        filter.printStats();
    }
}
