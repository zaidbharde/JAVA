/** Counts letters, digits, whitespace, and other Unicode code points. */
public final class Daily20261028TextCounts {
    public final int letters, digits, spaces, other;
    private Daily20261028TextCounts(int l, int d, int s, int o) { letters = l; digits = d; spaces = s; other = o; }
    public static Daily20261028TextCounts of(String text) {
        if (text == null) throw new IllegalArgumentException("text is null");
        int l = 0, d = 0, s = 0, o = 0;
        for (int i = 0; i < text.length();) {
            int c = text.codePointAt(i); i += Character.charCount(c);
            if (Character.isLetter(c)) l++;
            else if (Character.isDigit(c)) d++;
            else if (Character.isWhitespace(c)) s++;
            else o++;
        }
        return new Daily20261028TextCounts(l, d, s, o);
    }
    public static void main(String[] args) { Daily20261028TextCounts c = of("Hi 42!"); System.out.println(c.letters + "/" + c.digits + "/" + c.spaces + "/" + c.other); }
}
