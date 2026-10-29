/** Removes common ANSI terminal control sequences from text. */
public final class Daily20261028Extra006AnsiControlStripper {
    private static final char ESC = '\u001B';
    private static final char BEL = '\u0007';

    private Daily20261028Extra006AnsiControlStripper() { }

    /** Strips CSI, OSC, and two-byte ESC controls; a null input is rejected. */
    public static String strip(String text) {
        if (text == null) throw new NullPointerException("text");
        StringBuilder out = new StringBuilder(text.length());
        for (int i = 0; i < text.length();) {
            char c = text.charAt(i);
            if (c != ESC || i + 1 >= text.length()) {
                out.append(c);
                i++;
            } else if (text.charAt(i + 1) == '[') {
                int end = i + 2;
                while (end < text.length() && (text.charAt(end) < '@' || text.charAt(end) > '~')) end++;
                if (end < text.length()) i = end + 1;
                else { out.append(text, i, text.length()); break; }
            } else if (text.charAt(i + 1) == ']') {
                int end = i + 2;
                while (end < text.length() && text.charAt(end) != BEL
                        && !(text.charAt(end) == ESC && end + 1 < text.length()
                        && text.charAt(end + 1) == '\\')) end++;
                if (end == text.length()) { out.append(text, i, end); break; }
                i = text.charAt(end) == BEL ? end + 1 : end + 2;
            } else {
                // Other ESC sequences consist of ESC followed by one final byte.
                i += 2;
            }
        }
        return out.toString();
    }

    public static void main(String[] args) {
        assert strip("\u001B[31mred\u001B[0m").equals("red");
        assert strip("title\u001B]0;window title\u0007!\u001B]8;;url\u001B\\x\u001B]8;;\u001B\\")
                .equals("title!x");
        assert strip("plain\u001B[").equals("plain\u001B[");
        assert strip("").isEmpty();
        try { strip(null); throw new AssertionError("null accepted"); }
        catch (NullPointerException expected) { }
        System.out.println("AnsiControlStripper self-check passed");
    }
}
