import java.util.ArrayList;
import java.util.List;
/** Parses a CSV record, including quoted commas and doubled quote escapes. */
public final class Daily20261028CsvLine {
    private Daily20261028CsvLine() { }
    public static List<String> parse(String line) {
        List<String> fields = new ArrayList<String>();
        if (line == null) return fields;
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"' && quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                field.append('"'); i++;
            } else if (c == '"') quoted = !quoted;
            else if (c == ',' && !quoted) { fields.add(field.toString()); field.setLength(0); }
            else field.append(c);
        }
        if (quoted) throw new IllegalArgumentException("unclosed quoted field");
        fields.add(field.toString());
        return fields;
    }
    public static void main(String[] args) { System.out.println(parse("Ada,\"Lovelace, A.\",\"said \"\"hello\"\"\"")); }
}
