import java.util.HashMap;
import java.util.Map;

/** Finds the shortest substring containing every requested character, including multiplicities. */
public final class Daily20261028Extra026MinimumWindowSubstring {
    private Daily20261028Extra026MinimumWindowSubstring() {}

    /** Returns the earliest shortest covering window, or "" if no such window exists. */
    public static String find(String source, String target) {
        if (source == null || target == null) throw new IllegalArgumentException("inputs must not be null");
        if (target.isEmpty() || source.isEmpty() || target.length() > source.length()) return "";

        Map<Character, Integer> needed = new HashMap<>();
        for (char c : target.toCharArray()) needed.merge(c, 1, Integer::sum);
        int missing = target.length(), left = 0, bestStart = 0, bestLength = Integer.MAX_VALUE;
        for (int right = 0; right < source.length(); right++) {
            char added = source.charAt(right);
            int count = needed.getOrDefault(added, 0);
            if (count > 0) missing--;
            if (needed.containsKey(added)) needed.put(added, count - 1);
            while (missing == 0) {
                int length = right - left + 1;
                if (length < bestLength) { bestStart = left; bestLength = length; }
                char removed = source.charAt(left++);
                if (needed.containsKey(removed)) {
                    int updated = needed.get(removed) + 1;
                    if (updated > 0) missing++;
                    needed.put(removed, updated);
                }
            }
        }
        return bestLength == Integer.MAX_VALUE ? "" : source.substring(bestStart, bestStart + bestLength);
    }

    public static void main(String[] args) {
        if (!"BANC".equals(find("ADOBECODEBANC", "ABC"))
                || !"aab".equals(find("aaab", "aab"))
                || !"".equals(find("abc", "z"))
                || !"".equals(find("abc", ""))) throw new AssertionError("self-check failed");
        System.out.println("MinimumWindowSubstring self-check passed");
    }
}
