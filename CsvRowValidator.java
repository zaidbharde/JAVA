import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** Validates simple CSV rows while preserving field-level diagnostics. */
public final class CsvRowValidator {
    private static final Pattern INTEGER = Pattern.compile("-?(0|[1-9]\\d*)");
    public record Issue(int column, String message) {}
    public record Result(List<String> values, List<Issue> issues) {
        public boolean valid() { return issues.isEmpty(); }
    }

    public static Result validate(String row, int expectedColumns, int integerColumn) {
        if (expectedColumns < 1) throw new IllegalArgumentException("columns must be positive");
        if (integerColumn < -1 || integerColumn >= expectedColumns) {
            throw new IllegalArgumentException("integer column must be -1 or a valid column index");
        }
        List<String> values = split(row);
        List<Issue> issues = new ArrayList<>();
        if (values.size() != expectedColumns) {
            issues.add(new Issue(-1, "expected " + expectedColumns + " columns, found " + values.size()));
        }
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).isBlank()) issues.add(new Issue(i, "value is blank"));
            if (i == integerColumn && !INTEGER.matcher(values.get(i)).matches()) {
                issues.add(new Issue(i, "value must be an integer"));
            }
        }
        return new Result(List.copyOf(values), List.copyOf(issues));
    }

    private static List<String> split(String row) {
        if (row == null) throw new IllegalArgumentException("row cannot be null");
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        boolean closedQuote = false;
        for (int i = 0; i < row.length(); i++) {
            char c = row.charAt(i);
            if (quoted) {
                if (c == '"') {
                    if (i + 1 < row.length() && row.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        quoted = false;
                        closedQuote = true;
                    }
                } else current.append(c);
            } else if (c == ',') {
                fields.add(current.toString().trim());
                current.setLength(0);
                closedQuote = false;
            } else if (c == '"') {
                if (current.toString().isBlank() && !closedQuote) {
                    current.setLength(0);
                    quoted = true;
                } else throw new IllegalArgumentException("quote inside unquoted field");
            } else {
                if (closedQuote && !Character.isWhitespace(c)) {
                    throw new IllegalArgumentException("characters after closing quote");
                }
                if (!closedQuote) current.append(c);
            }
        }
        if (quoted) throw new IllegalArgumentException("unterminated quoted field");
        fields.add(current.toString().trim());
        return fields;
    }
}
