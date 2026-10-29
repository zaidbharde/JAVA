import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Removes a matched outer Markdown code fence, leaving other input untouched. */
public final class Daily20261028Extra001MarkdownFenceStripper {
    private static final Pattern OPEN = Pattern.compile("^ {0,3}(`{3,}|~{3,})(.*)$");

    private Daily20261028Extra001MarkdownFenceStripper() {}

    /**
     * Removes opening and closing fence lines only when they form a balanced
     * outer pair. The opener may carry an info string; a closer must use the
     * same marker, at least as long, and contain only trailing whitespace.
     * Null, unclosed, malformed, and non-fenced input is returned unchanged.
     */
    public static String strip(String markdown) {
        if (markdown == null) return null;
        String[] lines = markdown.split("\\n", -1);
        if (lines.length < 2) return markdown;
        Matcher opener = OPEN.matcher(removeCarriageReturn(lines[0]));
        if (!opener.matches()) return markdown;
        String marker = opener.group(1);
        boolean finalNewline = lines[lines.length - 1].isEmpty();
        int closingIndex = finalNewline ? lines.length - 2 : lines.length - 1;
        if (closingIndex < 1) return markdown;
        String last = removeCarriageReturn(lines[closingIndex]);
        Pattern close = Pattern.compile("^ {0,3}" + Pattern.quote(marker.substring(0, 1))
                + "{" + marker.length() + ",}\\s*$");
        if (!close.matcher(last).matches()) return markdown;
        StringBuilder body = new StringBuilder();
        for (int i = 1; i < closingIndex; i++) {
            if (i > 1) body.append('\n');
            body.append(lines[i]);
        }
        if (finalNewline) body.append('\n');
        return body.toString();
    }

    private static String removeCarriageReturn(String line) {
        return line.endsWith("\r") ? line.substring(0, line.length() - 1) : line;
    }

    public static void main(String[] args) {
        check("```java\nhello\n```", "hello");
        check("~~~\ninside\n~~~~\n", "inside\n");
        check("```\nnot closed", "```\nnot closed");
        check("plain", "plain");
        check("```\n\n```", "");
        if (strip(null) != null) throw new AssertionError("null should remain null");
    }

    private static void check(String input, String expected) {
        String actual = strip(input);
        if (!expected.equals(actual)) throw new AssertionError("Expected <" + expected + ">, got <" + actual + ">");
    }
}
