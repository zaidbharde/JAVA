import java.util.LinkedHashMap;
import java.util.Map;
/** Counts whitespace-delimited words case-insensitively. */
public final class Daily20261028WordFrequency {
    private Daily20261028WordFrequency() { }
    public static Map<String, Integer> count(String text) {
        Map<String, Integer> counts = new LinkedHashMap<String, Integer>();
        if (text == null || text.trim().isEmpty()) return counts;
        for (String word : text.toLowerCase().trim().split("\\s+")) {
            Integer old = counts.get(word);
            counts.put(word, old == null ? 1 : old + 1);
        }
        return counts;
    }
    public static void main(String[] args) {
        System.out.println(count("red blue red green blue red"));
    }
}
