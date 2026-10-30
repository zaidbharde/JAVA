import java.util.Map;

public final class RegexEngineRegressionTest {
    public static void main(String[] args) {
        check(new RegexEngine("a+").matches("aaa"), "full match should succeed");
        check(!new RegexEngine("a+").matches("baaa"), "matches should consume the full input");

        var found = new RegexEngine("(ab)").find("zabz");
        check(found.isPresent(), "find should locate a substring");
        Map<Integer, String> groups = found.orElseThrow();
        check("ab".equals(groups.get(0)), "group 0 should contain the full match");
        check("ab".equals(groups.get(1)), "group 1 should contain the capture");
        check(new RegexEngine("q").find("abc").isEmpty(), "find should report no match");
        System.out.println("RegexEngine regressions passed");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
