import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrequencyCounter {
    public static Map<String, Integer> countWords(String text) {
        Map<String, Integer> counts = new HashMap<>();
        for (String raw : text.toLowerCase().split("[^a-z0-9]+")) {
            if (!raw.isEmpty()) {
                counts.merge(raw, 1, Integer::sum);
            }
        }
        return counts;
    }

    public static List<Map.Entry<String, Integer>> ranked(String text) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(countWords(text).entrySet());
        entries.sort(Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue)
                .reversed().thenComparing(Map.Entry::getKey));
        return entries;
    }

    public static void main(String[] args) {
        String sample = "Streams make data pipelines clear; streams make programs composable.";
        for (Map.Entry<String, Integer> entry : ranked(sample)) {
            System.out.printf("%-12s %d%n", entry.getKey(), entry.getValue());
        }
    }
}
