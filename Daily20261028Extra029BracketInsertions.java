import java.util.Objects;

/** Counts the minimum parentheses needed to balance the parentheses in text. */
public final class Daily20261028Extra029BracketInsertions {
    private Daily20261028Extra029BracketInsertions() { }

    /** Ignores non-parenthesis characters; rejects null input. */
    public static int minimumInsertions(String text) {
        Objects.requireNonNull(text, "text");
        int open = 0;
        int insertions = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '(') {
                open++;
            } else if (c == ')') {
                if (open == 0) insertions++;
                else open--;
            }
        }
        return insertions + open;
    }

    public static void main(String[] args) {
        if (minimumInsertions("") != 0
                || minimumInsertions("(()())") != 0
                || minimumInsertions("())") != 1
                || minimumInsertions("((") != 2
                || minimumInsertions(")(") != 2
                || minimumInsertions("a(b)c)") != 1
                || minimumInsertions("plain text") != 0) {
            throw new AssertionError("Bracket-insertion self-check failed");
        }
        try {
            minimumInsertions(null);
            throw new AssertionError("null input should be rejected");
        } catch (NullPointerException expected) {
            // Expected validation behavior.
        }
        System.out.println("BracketInsertions checks passed");
    }
}
