/** Formats one string as a single POSIX-shell word; it never executes a command. */
public final class Daily20261028Extra005ShellArgumentQuote {
    private Daily20261028Extra005ShellArgumentQuote() {}

    /**
     * Returns a shell-safe representation of exactly one argument.
     * POSIX shells cannot pass NUL bytes in arguments, so strings containing NUL
     * are rejected rather than rendered misleadingly.
     *
     * @throws IllegalArgumentException if value is null or contains NUL
     */
    public static String quote(String value) {
        if (value == null) throw new IllegalArgumentException("argument must not be null");
        if (value.indexOf('\0') >= 0) throw new IllegalArgumentException("argument must not contain NUL");
        if (value.isEmpty()) return "''";
        return "'" + value.replace("'", "'\\''") + "'";
    }

    private static void expect(String input, String expected) {
        String actual = quote(input);
        if (!expected.equals(actual)) {
            throw new AssertionError("expected <" + expected + "> but got <" + actual + ">");
        }
    }

    private static void expectRejected(String input) {
        try {
            quote(input);
            throw new AssertionError("expected invalid argument to be rejected");
        } catch (IllegalArgumentException expected) {
            // Expected validation failure.
        }
    }

    public static void main(String[] args) {
        expect("", "''");
        expect("plain", "'plain'");
        expect("two words", "'two words'");
        expect("it's", "'it'\\''s'");
        expect("$HOME; `id`", "'$HOME; `id`'");
        expectRejected(null);
        expectRejected("bad\0value");
        System.out.println(quote("it's safe to display"));
    }
}
