import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Finds the longest contiguous substring containing no repeated UTF-16 code units. */
public final class Daily20261028Extra027LongestUniqueSpan {
    private Daily20261028Extra027LongestUniqueSpan() { }

    /** Returns the earliest longest substring without repeated {@code char} values. */
    public static String longestUniqueSpan(String input) {
        Objects.requireNonNull(input, "input");
        Map<Character, Integer> lastSeen = new HashMap<>();
        int windowStart = 0, bestStart = 0, bestLength = 0;
        for (int i = 0; i < input.length(); i++) {
            char current = input.charAt(i);
            Integer previous = lastSeen.put(current, i);
            if (previous != null && previous >= windowStart) windowStart = previous + 1;
            int length = i - windowStart + 1;
            if (length > bestLength) {
                bestStart = windowStart;
                bestLength = length;
            }
        }
        return input.substring(bestStart, bestStart + bestLength);
    }

    public static void main(String[] args) {
        if (!longestUniqueSpan("abcabcbb").equals("abc")
                || !longestUniqueSpan("pwwkew").equals("wke")
                || !longestUniqueSpan("abba").equals("ab")
                || !longestUniqueSpan("").isEmpty()
                || !longestUniqueSpan("a").equals("a")) {
            throw new AssertionError("Longest-unique-span self-check failed");
        }
        try {
            longestUniqueSpan(null);
            throw new AssertionError("null input should be rejected");
        } catch (NullPointerException expected) {
            // Expected validation behavior.
        }
        System.out.println("LongestUniqueSpan checks passed");
    }
}
