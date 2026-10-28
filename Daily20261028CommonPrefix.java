/** Finds the longest shared prefix across a set of strings. */
public final class Daily20261028CommonPrefix {
    private Daily20261028CommonPrefix() { }
    public static String find(String[] words) {
        if (words == null || words.length == 0 || words[0] == null) return "";
        String prefix = words[0];
        for (int i = 1; i < words.length; i++) {
            if (words[i] == null) return "";
            while (!words[i].startsWith(prefix)) {
                if (prefix.isEmpty()) return "";
                prefix = prefix.substring(0, prefix.length() - 1);
            }
        }
        return prefix;
    }
    public static void main(String[] args) {
        System.out.println(find(new String[]{"interview", "internet", "internal"}));
    }
}
