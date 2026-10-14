import java.util.ArrayList;
import java.util.List;

/** Parses one RFC-4180-style CSV row, including quoted commas and escaped quotes. */
public final class CsvRowParser {
    private CsvRowParser() {}

    public static List<String> parse(String row) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < row.length(); i++) {
            char c = row.charAt(i);
            if (c == '"') {
                if (quoted && i + 1 < row.length() && row.charAt(i + 1) == '"') {
                    field.append('"');
                    i++;
                } else quoted = !quoted;
            } else if (c == ',' && !quoted) {
                fields.add(field.toString());
                field.setLength(0);
            } else field.append(c);
        }
        if (quoted) throw new IllegalArgumentException("unterminated quoted field");
        fields.add(field.toString());
        return fields;
    }
}
