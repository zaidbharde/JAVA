/**
 * Palindrome Checker — works for strings, numbers, and sentences
 * (ignoring spaces, punctuation, and case).
 */
public class PalindromeChecker {

    // ── String palindrome ─────────────────────────────────────────────
    public static boolean isPalindrome(String input) {
        if (input == null) return false;

        // Strip non-alphanumeric and lowercase
        String cleaned = input.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        int left = 0, right = cleaned.length() - 1;

        while (left < right) {
            if (cleaned.charAt(left) != cleaned.charAt(right)) return false;
            left++;
            right--;
        }
        return true;
    }

    // ── Integer palindrome (no string conversion) ─────────────────────
    public static boolean isPalindrome(int number) {
        if (number < 0 || (number % 10 == 0 && number != 0)) return false;

        int reversed = 0;
        while (number > reversed) {
            reversed = reversed * 10 + number % 10;
            number  /= 10;
        }
        return number == reversed || number == reversed / 10;
    }

    // ── Demo ──────────────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("=".repeat(48));
        System.out.println("  Palindrome Checker Demo");
        System.out.println("=".repeat(48));

        String[] words = {
            "racecar",
            "hello",
            "A man a plan a canal Panama",
            "Was it a car or a cat I saw",
            "Not a palindrome"
        };

        System.out.println("\n  Strings:");
        System.out.println("  " + "─".repeat(44));
        for (String w : words) {
            System.out.printf("  %-35s → %s%n", "\"" + w + "\"",
                isPalindrome(w) ? "✅ Yes" : "❌ No");
        }

        int[] numbers = {121, 123, 1221, 12321, -121, 10};
        System.out.println("\n  Numbers:");
        System.out.println("  " + "─".repeat(44));
        for (int n : numbers) {
            System.out.printf("  %-10d → %s%n", n,
                isPalindrome(n) ? "✅ Yes" : "❌ No");
        }
    }
}
