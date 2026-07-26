import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * File Word Counter — reads a text file and reports word frequency,
 * total words, unique words, and the top N most common words.
 */
public class FileWordCounter {

    // ── Analysis ──────────────────────────────────────────────────────
    public static Map<String, Long> countWords(String filePath) throws IOException {
        return Files.lines(Path.of(filePath))
            .flatMap(line -> Arrays.stream(line.split("[^a-zA-Z]+")))
            .filter(w -> !w.isBlank())
            .map(String::toLowerCase)
            .collect(Collectors.groupingBy(w -> w, Collectors.counting()));
    }

    public static void printReport(Map<String, Long> freq, int topN) {
        long total  = freq.values().stream().mapToLong(Long::longValue).sum();
        int  unique = freq.size();

        System.out.println("\n" + "=".repeat(42));
        System.out.println("  Word Frequency Report");
        System.out.println("=".repeat(42));
        System.out.printf("  Total words  : %,d%n", total);
        System.out.printf("  Unique words : %,d%n", unique);
        System.out.println("\n  Top " + topN + " words:");
        System.out.println("  " + "─".repeat(38));
        System.out.printf("  %-20s %8s  %s%n", "Word", "Count", "Bar");
        System.out.println("  " + "─".repeat(38));

        long max = freq.values().stream().mapToLong(Long::longValue).max().orElse(1);

        freq.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(topN)
            .forEach(e -> {
                int barLen = (int) (e.getValue() * 20 / max);
                String bar = "█".repeat(barLen);
                System.out.printf("  %-20s %8d  %s%n",
                    e.getKey(), e.getValue(), bar);
            });

        System.out.println("=".repeat(42));
    }

    // ── Demo ──────────────────────────────────────────────────────────
    public static void main(String[] args) throws IOException {
        // Create a sample file on the fly for the demo
        String demo = "sample_demo.txt";
        Files.writeString(Path.of(demo),
            """
            Java is a powerful language. Java is widely used.
            Python is also a great language. Many developers love Python.
            Java and Python are both popular. Learning Java is fun.
            Language choice depends on the project. Java runs everywhere.
            """);

        Map<String, Long> freq = countWords(demo);
        printReport(freq, 8);

        Files.deleteIfExists(Path.of(demo));   // clean up demo file
    }
}
