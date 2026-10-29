import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Lists at most {@code limit} dictionary segmentations, in dictionary iteration order. */
public class Daily20261028Extra030WordBreakPaths {
    public static List<List<String>> segmentations(String text, Set<String> dictionary, int limit) {
        if (text == null || dictionary == null) throw new IllegalArgumentException("arguments must not be null");
        if (limit < 0) throw new IllegalArgumentException("limit must be nonnegative");
        for (String word : dictionary) {
            if (word == null || word.isEmpty()) throw new IllegalArgumentException("dictionary words must be nonempty");
        }
        List<List<String>> result = new ArrayList<>();
        if (limit > 0) collect(text, 0, dictionary, limit, new ArrayList<>(), result);
        return result;
    }

    private static void collect(String text, int offset, Set<String> dictionary, int limit,
                                List<String> path, List<List<String>> result) {
        if (result.size() == limit) return;
        if (offset == text.length()) {
            result.add(List.copyOf(path));
            return;
        }
        for (int end = offset + 1; end <= text.length() && result.size() < limit; end++) {
            String word = text.substring(offset, end);
            if (dictionary.contains(word)) {
                path.add(word);
                collect(text, end, dictionary, limit, path, result);
                path.remove(path.size() - 1);
            }
        }
    }

    public static void main(String[] args) {
        Set<String> words = Set.of("cat", "cats", "and", "sand", "dog");
        List<List<String>> paths = segmentations("catsanddog", words, 10);
        if (!paths.equals(List.of(List.of("cat", "sand", "dog"), List.of("cats", "and", "dog"))))
            throw new AssertionError(paths);
        if (segmentations("catsanddog", words, 1).size() != 1) throw new AssertionError("limit ignored");
        if (!segmentations("", words, 1).equals(List.of(List.of()))) throw new AssertionError("empty text");
        if (!segmentations("x", words, 5).isEmpty()) throw new AssertionError("impossible text");
        System.out.println("WordBreakPaths self-check passed: " + paths);
    }
}
