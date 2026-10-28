/** Extracts initials from whitespace- and punctuation-separated name parts. */
public final class Daily20261028Initials {
    private Daily20261028Initials() { }
    public static String from(String name) {
        if (name == null || name.trim().isEmpty()) return "";
        StringBuilder result = new StringBuilder();
        boolean start = true;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                if (start) result.append(Character.toUpperCase(c));
                start = false;
            } else start = true;
        }
        return result.toString();
    }
    public static void main(String[] args) { System.out.println(from("ada lovelace")); }
}
