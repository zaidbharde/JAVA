public final class MiniRegexRegressionTest {
    public static void main(String[] args) {
        check(new MiniRegex("(a?)*").matches("a"), "optional group must terminate and match");
        check(new MiniRegex("()*").matches(""), "empty group with * must match empty input");
        check(new MiniRegex("()+").matches(""), "empty group with + must satisfy its minimum");
        check(new MiniRegex("(){2}").matches(""), "bounded empty group must satisfy its minimum");
        System.out.println("MiniRegex zero-width regressions passed");
    }

    private static void check(boolean actual, String message) {
        if (!actual) throw new AssertionError(message);
    }
}
