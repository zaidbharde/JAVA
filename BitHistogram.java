import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class BitHistogram {

    public static void main(String[] args) {
        Map<Integer, Long> bitCountHistogram = IntStream.rangeClosed(1, 100)
                .boxed()
                .collect(groupingBy(Integer::bitCount, counting()));

        long maxFrequency = bitCountHistogram.values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(1);

        System.out.println("Bit Count Histogram (1..100)");
        System.out.println("─────────────────────────────");
        System.out.println("Bit Count | Frequency | Histogram");

        bitCountHistogram.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    int bits = entry.getKey();
                    long count = entry.getValue();

                    String bar = "#".repeat((int) count);

                    System.out.printf("   %2d     |   %3d     | %s%n", bits, count, bar);
                });
    }
}
