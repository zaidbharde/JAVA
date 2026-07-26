import java.util.*;

public class MiniRegex {

    private final String pattern;

    public MiniRegex(String pattern) {
        this.pattern = pattern;
    }

    public boolean matches(String text) {
        if (pattern.startsWith("^"))
            return matchHere(pattern.substring(1), text, 0);

        for (int i = 0; i <= text.length(); i++)
            if (matchHere(pattern, text, i))
                return true;
        return false;
    }

    public List<String> findAll(String text) {
        List<String> results = new ArrayList<>();
        String p = pattern.startsWith("^") ? pattern.substring(1) : pattern;

        for (int i = 0; i < text.length(); i++) {
            for (int len = 1; len <= text.length() - i; len++) {
                if (matchHere(p, text.substring(i, i + len), 0)
                    && matchExact(p, text.substring(i, i + len))) {
                    results.add(text.substring(i, i + len));
                    break;
                }
            }
        }
        return results;
    }

    private boolean matchExact(String pat, String text) {
        return matchHere(pat, text, 0) && consumesAll(pat, text, 0);
    }

    private boolean consumesAll(String pat, String text, int ti) {
        if (pat.isEmpty()) return ti == text.length();
        if (pat.length() >= 2 && pat.charAt(1) == '*') {
            char pc = pat.charAt(0);
            String rest = pat.substring(2);
            for (int j = ti; j <= text.length(); j++) {
                if (consumesAll(rest, text, j)) return true;
                if (j < text.length() && matchChar(pc, text.charAt(j))) continue;
                break;
            }
            return false;
        }
        if (ti >= text.length()) return pat.equals("$");
        if (matchChar(pat.charAt(0), text.charAt(ti)))
            return consumesAll(pat.substring(1), text, ti + 1);
        return false;
    }

    private boolean matchHere(String pat, String text, int ti) {
        if (pat.isEmpty()) return true;
        if (pat.equals("$")) return ti == text.length();

        if (pat.length() >= 2 && pat.charAt(1) == '*')
            return matchStar(pat.charAt(0), pat.substring(2), text, ti);

        if (pat.length() >= 2 && pat.charAt(1) == '+') {
            if (ti >= text.length() || !matchChar(pat.charAt(0), text.charAt(ti)))
                return false;
            return matchStar(pat.charAt(0), pat.substring(2), text, ti + 1);
        }

        if (pat.length() >= 2 && pat.charAt(1) == '?') {
            if (matchHere(pat.substring(2), text, ti)) return true;
            if (ti < text.length() && matchChar(pat.charAt(0), text.charAt(ti)))
                return matchHere(pat.substring(2), text, ti + 1);
            return false;
        }

        if (ti < text.length() && matchChar(pat.charAt(0), text.charAt(ti)))
            return matchHere(pat.substring(1), text, ti + 1);

        return false;
    }

    private boolean matchStar(char pc, String restPat, String text, int ti) {
        for (int i = ti; ; i++) {
            if (matchHere(restPat, text, i)) return true;
            if (i >= text.length() || !matchChar(pc, text.charAt(i))) break;
        }
        return false;
    }

    private boolean matchChar(char pc, char tc) {
        if (pc == '.') return true;
        if (pc == '\\') return false;
        return pc == tc;
    }

    @Override
    public String toString() { return "/" + pattern + "/"; }

    public static void main(String[] args) {
        System.out.println("=".repeat(50));
        System.out.println("  Mini Regex Engine");
        System.out.println("=".repeat(50));

        record Test(String pattern, String text, boolean expected) {}

        Test[] tests = {
            new Test("hello",      "say hello world",   true),
            new Test("^hello",     "hello world",       true),
            new Test("^hello",     "say hello",         false),
            new Test("world$",     "hello world",       true),
            new Test("world$",     "world hello",       false),
            new Test("h.llo",      "hello",             true),
            new Test("h.llo",      "hxllo",             true),
            new Test("ab*c",       "ac",                true),
            new Test("ab*c",       "abbbbc",            true),
            new Test("ab+c",       "ac",                false),
            new Test("ab+c",       "abc",               true),
            new Test("ab?c",       "ac",                true),
            new Test("ab?c",       "abc",               true),
            new Test("ab?c",       "abbc",              false),
            new Test(".*",         "anything",          true),
            new Test("a.*b",       "axxxb",             true),
        };

        System.out.printf("\n  %-15s %-20s %-10s %-10s%n",
            "Pattern", "Text", "Expected", "Result");
        System.out.println("  " + "─".repeat(55));

        int passed = 0;
        for (Test t : tests) {
            boolean result = new MiniRegex(t.pattern).matches(t.text);
            boolean ok = result == t.expected;
            if (ok) passed++;
            System.out.printf("  %-15s %-20s %-10s %-10s %s%n",
                t.pattern, "\""+t.text+"\"", t.expected, result,
                ok ? "✅" : "❌");
        }

        System.out.printf("\n  Passed: %d/%d%n", passed, tests.length);

        System.out.println("\n  Find all matches:");
        MiniRegex r = new MiniRegex("a.b");
        String text = "aab axb ayb acb qqq aob";
        System.out.println("  Pattern: " + r + "  Text: \"" + text + "\"");
        System.out.println("  Found: " + r.findAll(text));
    }
}
