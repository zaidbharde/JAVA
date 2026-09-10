import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Prefix index that returns the most popular matching terms. */
public final class TrieAutocompleteIndex {
    private static final class Node {
        final Map<Character, Node> children = new HashMap<>();
        final Map<String, Integer> scores = new HashMap<>();
    }

    private final Node root = new Node();

    public void add(String term, int score) {
        if (term == null || term.isBlank() || score < 0) {
            throw new IllegalArgumentException("term must be non-blank and score non-negative");
        }
        Node node = root;
        for (char character : term.toLowerCase().toCharArray()) {
            node = node.children.computeIfAbsent(character, ignored -> new Node());
            node.scores.merge(term, score, Math::max);
        }
    }

    public List<String> suggest(String prefix, int limit) {
        if (prefix == null || limit < 0) {
            throw new IllegalArgumentException("prefix cannot be null and limit cannot be negative");
        }
        Node node = root;
        for (char character : prefix.toLowerCase().toCharArray()) {
            node = node.children.get(character);
            if (node == null) {
                return List.of();
            }
        }
        return node.scores.entrySet().stream()
                .sorted(Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue)
                        .reversed().thenComparing(Map.Entry::getKey))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }
}
