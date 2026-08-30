import java.util.*;

public class PalindromePartition {
    static boolean palindrome(String text, int left, int right) {
        while (left < right) if (text.charAt(left++) != text.charAt(right--)) return false;
        return true;
    }

    static void split(String text, int start, List<String> path) {
        if (start == text.length()) {
            System.out.println(path);
            return;
        }
        for (int end = start; end < text.length(); end++) {
            if (palindrome(text, start, end)) {
                path.add(text.substring(start, end + 1));
                split(text, end + 1, path);
                path.remove(path.size() - 1);
            }
        }
    }

    public static void main(String[] args) { split("aab", 0, new ArrayList<>()); }
}
