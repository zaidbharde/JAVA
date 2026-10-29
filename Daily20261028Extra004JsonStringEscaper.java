public final class Daily20261028Extra004JsonStringEscaper {
    private Daily20261028Extra004JsonStringEscaper() {}

    /** Escapes JSON string contents (without surrounding quotes). */
    public static String escape(String input) {
        if (input == null) throw new IllegalArgumentException("input must not be null");
        StringBuilder out = new StringBuilder(input.length() + 16);
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '"': out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\b': out.append("\\b"); break;
                case '\f': out.append("\\f"); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        out.append("\\u00");
                        out.append("0123456789abcdef".charAt((c >>> 4) & 0xf));
                        out.append("0123456789abcdef".charAt(c & 0xf));
                    } else if (Character.isHighSurrogate(c)) {
                        if (i + 1 >= input.length() || !Character.isLowSurrogate(input.charAt(i + 1)))
                            throw new IllegalArgumentException("unpaired high surrogate at index " + i);
                        out.append(c).append(input.charAt(++i));
                    } else if (Character.isLowSurrogate(c)) {
                        throw new IllegalArgumentException("unpaired low surrogate at index " + i);
                    } else {
                        out.append(c);
                    }
            }
        }
        return out.toString();
    }

    private static void check(boolean condition) {
        if (!condition) throw new AssertionError("JSON string escaping self-check failed");
    }

    public static void main(String[] args) {
        check(escape("quote \" slash \\ and\nline")
                .equals("quote \\\" slash \\\\ and\\nline"));
        check(escape("\u0000\u001f\b\t").equals("\\u0000\\u001f\\b\\t"));
        check(escape("plain 😀").equals("plain 😀"));
        try {
            escape("bad\ud800");
            throw new AssertionError("unpaired surrogate should be rejected");
        } catch (IllegalArgumentException expected) {
            // Expected validation behavior.
        }
        System.out.println("JsonStringEscaper self-check passed");
    }
}
