public class RabinKarp {
    private static final int PRIME = 101;

    public static int search(String text, String pattern) {
        int n = text.length(), m = pattern.length();
        if (m > n) return -1;

        long patternHash = 0, textHash = 0, h = 1;
        for (int i = 0; i < m - 1; i++) h = (h * 256) % PRIME;

        for (int i = 0; i < m; i++) {
            patternHash = (256 * patternHash + pattern.charAt(i)) % PRIME;
            textHash = (256 * textHash + text.charAt(i)) % PRIME;
        }

        for (int i = 0; i <= n - m; i++) {
            if (patternHash == textHash) {
                if (text.substring(i, i + m).equals(pattern)) return i;
            }
            if (i < n - m) {
                textHash = (256 * (textHash - text.charAt(i) * h) + text.charAt(i + m)) % PRIME;
                if (textHash < 0) textHash += PRIME;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        String text = "abaxabcabcabyabc";
        String pattern = "abcaby";
        System.out.println("Found at index: " + search(text, pattern));
    }
}
