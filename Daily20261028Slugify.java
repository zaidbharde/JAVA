/** Converts a heading into a lowercase, hyphen-separated ASCII slug. */
public final class Daily20261028Slugify {
    private Daily20261028Slugify() { }
    public static String slug(String text) {
        if (text == null) return "";
        StringBuilder out = new StringBuilder();
        boolean pendingDash = false;
        for (int i = 0; i < text.length(); i++) {
            char c = Character.toLowerCase(text.charAt(i));
            if (c >= 'a' && c <= 'z' || c >= '0' && c <= '9') {
                if (pendingDash && out.length() > 0) out.append('-');
                out.append(c); pendingDash = false;
            } else pendingDash = true;
        }
        return out.toString();
    }
    public static void main(String[] args) { System.out.println(slug("Hello, Java Utilities!")); }
}
