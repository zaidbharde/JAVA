import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 * BitHistogram – Displays the frequency distribution of the number of set bits
 * (population count) for integers in the range [1, 100].
 * 
 * For each integer, we count how many bits are 1 (using Integer.bitCount),
 * then group by that count and show the distribution as a histogram.
 */
public class BitHistogram {

    public static void main(String[] args) {
        // Create a map: bitCount -> number of integers having that many set bits
        Map<Integer, Long> bitCountHistogram = IntStream.rangeClosed(1, 100)
                .boxed()
                .collect(groupingBy(Integer::bitCount, counting()));

        // Find the maximum frequency for scaling the histogram bars (optional)
        long maxFrequency = bitCountHistogram.values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(1);

        // Print the histogram sorted by bit count
        System.out.println("Bit Count Histogram (1..100)");
        System.out.println("─────────────────────────────");
        System.out.println("Bit Count | Frequency | Histogram");

        bitCountHistogram.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    int bits = entry.getKey();
                    long count = entry.getValue();

                    // Create a simple visual bar (one star per integer)
                    String bar = "#".repeat((int) count); // Java 11+ String.repeat

                    System.out.printf("   %2d     |   %3d     | %s%n", bits, count, bar);
                });
    }
}
