import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** Standalone regression suite; run with assertions implemented as explicit checks. */
public final class JsonParserRegressionTest {
    public static void main(String[] args) {
        check(JsonParser.parse("null") == null, "null");
        check(Boolean.TRUE.equals(JsonParser.parse("true")), "true");
        check(Boolean.FALSE.equals(JsonParser.parse("false")), "false");
        check("hello".equals(JsonParser.parse("\"hello\"")), "string");
        check(new BigDecimal("-12.50e+2").compareTo((BigDecimal) JsonParser.parse("-12.50e+2")) == 0, "decimal number");
        check(((BigDecimal) JsonParser.parse("0")).signum() == 0, "zero");
        check(((List<?>) JsonParser.parse("[]")).isEmpty(), "empty array");
        check(((Map<?, ?>) JsonParser.parse("{}")).isEmpty(), "empty object");

        Object nested = JsonParser.parse(" {\"items\":[1,true,null,{\"x\":\"v\"}],\"flag\":false} \n");
        Map<?, ?> object = (Map<?, ?>) nested;
        List<?> items = (List<?>) object.get("items");
        check(items.size() == 4 && items.get(2) == null, "nested array values");
        check(Boolean.TRUE.equals(((Map<?, ?>) items.get(3)).get("x").equals("v")), "nested object");
        check(Boolean.FALSE.equals(object.get("flag")), "nested boolean");

        String escaped = (String) JsonParser.parse("\"\\\"\\\\\\/\\b\\f\\n\\r\\t\\u0041\"");
        check("\"\\/\b\f\n\r\tA".equals(escaped), "all legal escapes");
        check("\uD83D\uDE03".equals(JsonParser.parse("\"\\uD83D\\uDE03\"")), "Unicode surrogate pair");
        check("é".equals(JsonParser.parse("\"é\"")), "literal Unicode character");

        for (String invalid : List.of("", " ", "tru", "nul", "[", "[1", "[1,]", "{", "{\"a\"}",
                "{\"a\":1", "{a:1}", "01", "-01", ".1", "1.", "1e", "1e+", "--1", "+1",
                "NaN", "Infinity", "\"unfinished", "\"\\q\"", "\"\\u12G4\"", "\"\\u123\"",
                "\"line\nfeed\"", "true false", "{}x", "[1,,2]")) {
            expectInvalid(invalid);
        }
        expectInvalid("\"\\u" + (char) 0x0661 + "2ab\"");
        expectNullRejected();
        System.out.println("JsonParser regressions passed");
    }

    private static void expectInvalid(String input) {
        try {
            JsonParser.parse(input);
            throw new AssertionError("invalid JSON was accepted: " + input);
        } catch (IllegalArgumentException expected) {
            check(expected.getMessage().contains("character"), "parse errors include a position");
        }
    }

    private static void expectNullRejected() {
        try {
            JsonParser.parse(null);
            throw new AssertionError("null Java input was accepted");
        } catch (IllegalArgumentException expected) {
            check(expected.getMessage().contains("input"), "null input diagnostic");
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
