import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class WordLadderPaths {
    public static List<String> shortest(String start, String target, Set<String> dictionary) {
        if (start.length() != target.length()) throw new IllegalArgumentException("length mismatch");
        Set<String> words = new HashSet<>(dictionary);
        words.add(target);
        Queue<String> queue = new ArrayDeque<>();
        Map<String, String> previous = new HashMap<>();
        queue.add(start);
        previous.put(start, null);
        while (!queue.isEmpty()) {
            String current = queue.remove();
            if (current.equals(target)) return reconstruct(previous, target);
            for (String next : neighbors(current, words)) {
                if (!previous.containsKey(next)) {
                    previous.put(next, current);
                    queue.add(next);
                }
            }
        }
        return List.of();
    }

    private static List<String> neighbors(String word, Set<String> words) {
        List<String> result = new ArrayList<>();
        char[] letters = word.toCharArray();
        for (int index = 0; index < letters.length; index++) {
            char original = letters[index];
            for (char candidate = 'a'; candidate <= 'z'; candidate++) {
                letters[index] = candidate;
                String next = new String(letters);
                if (candidate != original && words.contains(next)) result.add(next);
            }
            letters[index] = original;
        }
        return result;
    }

    private static List<String> reconstruct(Map<String, String> previous, String target) {
        List<String> path = new ArrayList<>();
        for (String current = target; current != null; current = previous.get(current)) path.add(current);
        java.util.Collections.reverse(path);
        return path;
    }

    public static void main(String[] args) {
        Set<String> words = Set.of("hot", "dot", "dog", "lot", "log", "cog");
        System.out.println(shortest("hit", "cog", words));
    }
}
