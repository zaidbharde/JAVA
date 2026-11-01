import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Small dependency-free JSON parser. Objects are maps, arrays are lists, and numbers are BigDecimal. */
public final class JsonParser {
    private final String input;
    private int position;

    private JsonParser(String input) {
        if (input == null) throw new IllegalArgumentException("input must not be null");
        this.input = input;
    }

    /** Parses one complete JSON value and rejects non-whitespace trailing data. */
    public static Object parse(String input) {
        JsonParser parser = new JsonParser(input);
        Object value = parser.readValue();
        parser.skipWhitespace();
        if (!parser.atEnd()) parser.fail("trailing data");
        return value;
    }

    private Object readValue() {
        skipWhitespace();
        if (atEnd()) return fail("expected a value");
        return switch (input.charAt(position)) {
            case 'n' -> readLiteral("null", null);
            case 't' -> readLiteral("true", Boolean.TRUE);
            case 'f' -> readLiteral("false", Boolean.FALSE);
            case '"' -> readString();
            case '[' -> readArray();
            case '{' -> readObject();
            default -> readNumber();
        };
    }

    private Object readLiteral(String literal, Object value) {
        if (!input.startsWith(literal, position)) return fail("expected '" + literal + "'");
        position += literal.length();
        return value;
    }

    private List<Object> readArray() {
        position++;
        List<Object> values = new ArrayList<>();
        skipWhitespace();
        if (take(']')) return values;
        while (true) {
            values.add(readValue());
            skipWhitespace();
            if (take(']')) return values;
            require(',');
        }
    }

    private Map<String, Object> readObject() {
        position++;
        Map<String, Object> values = new LinkedHashMap<>();
        skipWhitespace();
        if (take('}')) return values;
        while (true) {
            skipWhitespace();
            if (atEnd() || input.charAt(position) != '"') return fail("expected an object key");
            String key = readString();
            skipWhitespace();
            require(':');
            values.put(key, readValue());
            skipWhitespace();
            if (take('}')) return values;
            require(',');
        }
    }

    private String readString() {
        require('"');
        StringBuilder result = new StringBuilder();
        while (!atEnd()) {
            char ch = input.charAt(position++);
            if (ch == '"') return result.toString();
            if (ch < 0x20) return fail("unescaped control character in string");
            if (ch != '\\') {
                result.append(ch);
                continue;
            }
            if (atEnd()) return fail("unfinished escape");
            char escape = input.charAt(position++);
            switch (escape) {
                case '"', '\\', '/' -> result.append(escape);
                case 'b' -> result.append('\b');
                case 'f' -> result.append('\f');
                case 'n' -> result.append('\n');
                case 'r' -> result.append('\r');
                case 't' -> result.append('\t');
                case 'u' -> result.append(readHexCodeUnit());
                default -> { return fail("invalid escape sequence"); }
            }
        }
        return fail("unterminated string");
    }

    private char readHexCodeUnit() {
        if (input.length() - position < 4) return fail("incomplete Unicode escape");
        int value = 0;
        for (int i = 0; i < 4; i++) {
            char ch = input.charAt(position++);
            int digit;
            if (ch >= '0' && ch <= '9') digit = ch - '0';
            else if (ch >= 'a' && ch <= 'f') digit = ch - 'a' + 10;
            else if (ch >= 'A' && ch <= 'F') digit = ch - 'A' + 10;
            else digit = -1;
            if (digit < 0) return fail("invalid Unicode escape");
            value = value * 16 + digit;
        }
        return (char) value;
    }

    private BigDecimal readNumber() {
        int start = position;
        take('-');
        if (take('0')) {
            if (!atEnd() && isDigit(input.charAt(position))) return fail("leading zero in number");
        } else {
            requireDigitOneToNine();
            while (!atEnd() && isDigit(input.charAt(position))) position++;
        }
        if (take('.')) {
            if (atEnd() || !isDigit(input.charAt(position))) return fail("fraction requires a digit");
            while (!atEnd() && isDigit(input.charAt(position))) position++;
        }
        if (take('e') || take('E')) {
            take('+');
            if (position > start && input.charAt(position - 1) != '+') take('-');
            if (atEnd() || !isDigit(input.charAt(position))) return fail("exponent requires a digit");
            while (!atEnd() && isDigit(input.charAt(position))) position++;
        }
        try {
            return new BigDecimal(input.substring(start, position));
        } catch (NumberFormatException exception) {
            return fail("invalid number");
        }
    }

    private void requireDigitOneToNine() {
        if (atEnd() || input.charAt(position) < '1' || input.charAt(position) > '9') fail("expected a number");
        position++;
    }

    private static boolean isDigit(char ch) { return ch >= '0' && ch <= '9'; }
    private void skipWhitespace() {
        while (!atEnd()) {
            char ch = input.charAt(position);
            if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t') position++;
            else break;
        }
    }
    private boolean take(char expected) {
        if (!atEnd() && input.charAt(position) == expected) { position++; return true; }
        return false;
    }
    private void require(char expected) {
        if (!take(expected)) fail("expected '" + expected + "'");
    }
    private boolean atEnd() { return position >= input.length(); }
    private <T> T fail(String message) {
        throw new IllegalArgumentException(message + " at character " + position);
    }
}
