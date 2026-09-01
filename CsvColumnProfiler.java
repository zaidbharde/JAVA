import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Profiles numeric columns in a small CSV document without external dependencies. */
public final class CsvColumnProfiler {
    public record Summary(int count, double mean, double minimum, double maximum) {}

    public static Summary profile(String csv, int column) {
        if (column < 0) throw new IllegalArgumentException("column must be non-negative");
        List<Double> numbers = new ArrayList<>();
        String[] rows = csv.strip().split("\\R");
        for (int row = 1; row < rows.length; row++) {
            String[] fields = rows[row].split(",", -1);
            if (fields.length <= column || fields[column].isBlank()) continue;
            try { numbers.add(Double.parseDouble(fields[column].trim())); }
            catch (NumberFormatException ignored) { /* non-numeric values are skipped */ }
        }
        if (numbers.isEmpty()) throw new IllegalArgumentException("no numeric values found");
        double sum = 0, min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
        for (double value : numbers) {
            sum += value;
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        return new Summary(numbers.size(), sum / numbers.size(), min, max);
    }

    public static void main(String[] args) {
        String csv = "name,latency\napi,21.5\ncache,not-set\ndb,34.0\n";
        Summary result = profile(csv, 1);
        System.out.printf(Locale.ROOT, "count=%d mean=%.2f min=%.2f max=%.2f%n",
                result.count(), result.mean(), result.minimum(), result.maximum());
    }
}
