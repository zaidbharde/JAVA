import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/** Returns the most frequent words, breaking ties lexicographically. */
public final class TopKFrequentWords {
    private TopKFrequentWords() {
    }

    public static List<String> topK(String text, int limit) {
        if (limit <= 0 || text == null || text.isBlank()) {
            return List.of();
        }
        Map<String, Integer> counts = new HashMap<>();
        for (String raw : text.toLowerCase().split("[^a-z0-9']+")) {
            if (!raw.isEmpty()) {
                counts.merge(raw, 1, Integer::sum);
            }
        }
        Comparator<String> weakestFirst = (left, right) -> {
            int byCount = Integer.compare(counts.get(left), counts.get(right));
            return byCount != 0 ? byCount : right.compareTo(left);
        };
        PriorityQueue<String> heap = new PriorityQueue<>(weakestFirst);
        for (String word : counts.keySet()) {
            heap.offer(word);
            if (heap.size() > limit) {
                heap.poll();
            }
        }
        List<String> result = new ArrayList<>(heap);
        result.sort((left, right) -> {
            int byCount = Integer.compare(counts.get(right), counts.get(left));
            return byCount != 0 ? byCount : left.compareTo(right);
        });
        return result;
    }

    public static Map<String, Integer> frequencies(String text) {
        Map<String, Integer> result = new HashMap<>();
        if (text == null) {
            return result;
        }
        for (String raw : text.toLowerCase().split("[^a-z0-9']+")) {
            if (!raw.isEmpty()) {
                result.merge(raw, 1, Integer::sum);
            }
        }
        return result;
    }
}
