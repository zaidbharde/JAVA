import java.util.Objects;

/** Validates parentheses in arbitrary text and reports the maximum nesting depth. */
public final class Daily20261028Extra028ParenthesisDepth {
    private Daily20261028Extra028ParenthesisDepth() { }

    /** Outcome of scanning parentheses; depth is the maximum reached before any error. */
    public static final class Result {
        public final boolean valid;
        public final int maximumDepth;

        private Result(boolean valid, int maximumDepth) {
            this.valid = valid;
            this.maximumDepth = maximumDepth;
        }

        @Override
        public String toString() {
            return "Result{valid=" + valid + ", maximumDepth=" + maximumDepth + "}";
        }
    }

    /** Ignores non-parenthesis characters. A null input is rejected. */
    public static Result analyze(String text) {
        Objects.requireNonNull(text, "text");
        int depth = 0;
        int maximum = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '(') {
                depth++;
                maximum = Math.max(maximum, depth);
            } else if (c == ')') {
                if (depth == 0) return new Result(false, maximum);
                depth--;
            }
        }
        return new Result(depth == 0, maximum);
    }

    public static void main(String[] args) {
        if (!analyze("a(b(c)d)").valid || analyze("((x)").valid
                || analyze("())").valid || analyze("").maximumDepth != 0
                || analyze("plain text").maximumDepth != 0
                || analyze("(()())").maximumDepth != 2) {
            throw new AssertionError("Parenthesis-depth self-check failed");
        }
        try {
            analyze(null);
            throw new AssertionError("null input should be rejected");
        } catch (NullPointerException expected) {
            // Expected validation behavior.
        }
        System.out.println("ParenthesisDepth checks passed");
    }
}
